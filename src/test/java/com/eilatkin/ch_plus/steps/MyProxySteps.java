package com.eilatkin.ch_plus.steps;

import com.browserup.bup.filters.RequestFilter;
import com.browserup.bup.filters.RequestFilterAdapter;

import com.browserup.bup.filters.ResponseFilter;
import com.browserup.bup.filters.ResponseFilterAdapter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.eilatkin.ch_plus.proxy.MyProxy;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ibsqa.chameleon.compare.ICompareManager;
import ru.ibsqa.chameleon.steps.BrowserSteps;
import ru.ibsqa.chameleon.steps.SeleniumFieldSteps;
import ru.ibsqa.chameleon.steps.TestStep;
import ru.ibsqa.chameleon.storage.IVariableScope;
import ru.ibsqa.chameleon.storage.IVariableStorage;
import ru.ibsqa.chameleon.utils.spring.SpringUtils;
import ru.ibsqa.chameleon.utils.waiting.Waiting;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static org.junit.jupiter.api.Assertions.fail;


@Component
@Slf4j
public class MyProxySteps extends BrowserSteps {


    @Autowired
    private IVariableStorage storage;
    @Autowired
    private ICompareManager compareManager;
    @Autowired
    private SeleniumFieldSteps seleniumFieldSteps;
    @Autowired
    private MyProxy myProxy;

    private final boolean seleniumProxy = Boolean.parseBoolean(System.getProperty("seleniumProxy", "true"));

    @Data
    @Builder
    public static class FindCondition {
        private String fieldName;
        private String operator;
        private String value;

        public String getOperator() {
            return Optional.ofNullable(operator)
                    .orElseGet(() -> SpringUtils.getBean(ICompareManager.class).defaultOperator());
        }
    }

    private void checkProxy() {
        if (!seleniumProxy || !myProxy.proxy.isStarted()) {
            log.error("Режим использования Proxy выключен!");
            fail("Включите Proxy режим браузера в настройках фреймворка!");
        }
    }

    private HttpResponse formDummyResponse(int status) {
        HttpResponse dummyResponse = new DefaultFullHttpResponse(HTTP_1_1,HttpResponseStatus.valueOf(status));
        dummyResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
        dummyResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        dummyResponse.headers().set(HttpHeaderNames.CACHE_CONTROL, "no-cache, no-store, max-age=0, must-revalidate");
//TODO: можно тестировать задержку ответа изменяя этот конструктор,
//        а также следует решить проблему лишних хедеров Via: 1.1 BrowserUpProxy
        return dummyResponse;
    }

    private HttpResponse formSmartResponse(String method, String url) {
        int status=200;
        if (method.equals("POST")) status=201;
        if (method.equals("DELETE")) status=204;
        log.info("Умная подмена запроса для {} {} - статус {}", method, url, status);
        return formDummyResponse(status);
    }

    private final HttpResponse responseOk = formDummyResponse(200);

    @TestStep("заблокировать запрос")
    public void blockRequestTo(final String url, final int responseCode) {
        checkProxy();

        myProxy.proxy.addRequestFilter((request, contents, messageInfo) -> {
            if (Pattern.compile(url).matcher(messageInfo.getOriginalUrl()).matches()) {
                return formDummyResponse(responseCode);
            }
            return null;
        });
    }

    @TestStep("подменить тело ответа")
    public void overrideContent(final String url, final int responseCode, final String responseBody) {
        checkProxy();

        myProxy.proxy.addRequestFilter((request, contents, messageInfo) -> {
            if (Pattern.compile(url).matcher(messageInfo.getOriginalUrl()).matches()) {
                ByteBuf  content = Unpooled.copiedBuffer(responseBody, StandardCharsets.UTF_8);
                HttpResponse fakeResponse = new DefaultFullHttpResponse(HTTP_1_1,HttpResponseStatus.valueOf(responseCode),content);
                fakeResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
                fakeResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
                fakeResponse.headers().set(HttpHeaderNames.CACHE_CONTROL, "no-cache, no-store, max-age=0, must-revalidate");
                return fakeResponse;
            }
            return null;
        });
    }

    /**
    * Метод работает долго, т.к. ждет реальный ответ сервера.
    * Иногда его использование в тестовых шагах будет оправдано, например из-за работы с хедером Connection: keep-alive
     */
    @TestStep("подменить тело ответа с ожиданием")
    public void overrideContentWithRealResponse(final String url, final int responseCode, final String responseBody) {
       checkProxy();

       ResponseFilter filter = (response, contents, messageInfo) -> {
            if (Pattern.compile(url).matcher(messageInfo.getOriginalUrl()).matches()) {
                if (contents!=null) contents.setTextContents(responseBody);
                response.setStatus(HttpResponseStatus.valueOf(responseCode));
            }
        };
        myProxy.proxy.addFirstHttpFilterFactory(new ResponseFilterAdapter.FilterSource(filter, 16777216));
    }

    /**
     * Защита от тестировщика!
     * Иначе единственная опечатка в регулярке url повлечет непредсказуемый каскад операций удаления и изменения данных на стендах.
     * Продублировать во все реализации прокси-фильтров:
     * else if (unsafeRequest(request)) return responseOk;
     */
    private boolean unsafeRequest(HttpRequest request) {
        if (Objects.equals(request.method(),HttpMethod.GET) || Objects.equals(request.method(),HttpMethod.CONNECT)
                || !Pattern.compile(".*/api/.*").matcher(request.uri()).matches()) {
            return false;
        }
        log.error("Небезопасный тип {}-запроса был заблокирован: проверьте корректность шага!\n" + request.uri(), request.method());
        return true;
    }

    private void triggerFound() {
        found = true;
    }
    private boolean found = false;

    @TestStep("нажатием на \"${fieldName}\" произошел \"${method}\"-запрос по маске URL")
    public void buttonTriggerRequest(final String fieldName, final String method, final String url) {
        checkProxy();
        found = false;

        RequestFilter filter = (request, contents, messageInfo) -> {
            if (request.method() == HttpMethod.valueOf(method) && Pattern.compile(url).matcher(messageInfo.getOriginalUrl()).matches()) {
                log.debug("Перехвачен подходящий {}-запрос!", method);
                triggerFound();
                return formSmartResponse(method, url);
            }
            else if (unsafeRequest(request)) return responseOk;
            return null;
        };
        myProxy.proxy.addFirstHttpFilterFactory(new RequestFilterAdapter.FilterSource(filter, 16777216));

        seleniumFieldSteps.clickField(fieldName);

        Waiting.on(Duration.ofSeconds(1))
                .ignoring(AssertionError.class)
                .check(() -> found)
                .ifNegative(() ->
                        fail("В течение секунды не произошло искомого запроса!")
                );
    }

    @TestStep("нажатием на \"${fieldName}\" произошел GET-запрос по маске URL")
    public void buttonTriggerGETRequest(final String fieldName, final String url) {
        checkProxy();
        found = false;

        RequestFilter filter = (request, contents, messageInfo) -> {
            if (request.method() == HttpMethod.valueOf("GET") && Pattern.compile(url).matcher(messageInfo.getOriginalUrl()).matches()) {
                triggerFound();
                return null;
            }
            else if (unsafeRequest(request)) return responseOk;
            return null;
        };
        myProxy.proxy.addFirstHttpFilterFactory(new RequestFilterAdapter.FilterSource(filter, 16777216));

        seleniumFieldSteps.clickField(fieldName);

        Waiting.on(Duration.ofSeconds(1))
                .ignoring(AssertionError.class)
                .check(() -> found)
                .ifNegative(() ->
                        fail("В течение секунды не произошло искомого запроса!")
                );
    }

    public JsonNode json(String json) throws JsonProcessingException {
        return new ObjectMapper().readTree(json);
    }

    @TestStep("нажатием на \"${fieldName}\" произошел \"${method}\"-запрос по маске URL с телом, таким что")
    public void buttonTriggerRequestWithJsonCheck(final String fieldName, final String method, final String url, List<FindCondition> conditions ) {
        checkProxy();
        found = false;
        collectedFailMessage = "";
        RequestFilter filter = (request, contents, messageInfo) -> {
            if (request.method() == HttpMethod.valueOf(method) && Pattern.compile(url).matcher(messageInfo.getOriginalUrl()).matches()) {
                String messageContents = contents.getTextContents();
                JsonNode jsonContent = null;
                try {
                    jsonContent = json(messageContents);
                    assert jsonContent != null;
                } catch (JsonProcessingException e) {
                    log.error("Тело перехваченного запроса не является валидным Json!");
                    triggerFail(e.getMessage());
                }

                final StringBuilder logText = new StringBuilder("Перехвачен запрос! Проверяется messageContents:\n");
                logText.append(messageContents).append("\n");
                boolean match = true;
                for (FindCondition row : conditions) {
                    final String expected = evalVariable(row.getValue()).replace("\\n", "\n");
                    assert jsonContent != null;
                    String actual = StringEscapeUtils.unescapeJava(String.valueOf(jsonContent.get(row.getFieldName())));
                    match = compareManager.checkValue(row.getOperator(), actual, expected);
                    if (!match) {
                        log.debug(logText.toString());
                        triggerFail(compareManager.buildErrorMessage(row.getOperator(), String.format("Значение %s не совпало", row.getFieldName()), actual, expected));
                        break;
                    }
                }
                if (match) {
                    log.debug(String.format("%sЭлемент тела запроса подошел по всем заданным параметрам поиска.", logText));
                    triggerFound();
                }
                return formSmartResponse(method, url);
            }
            else if (unsafeRequest(request)) return responseOk;
            return null;
        };
        myProxy.proxy.addFirstHttpFilterFactory(new RequestFilterAdapter.FilterSource(filter, 16777216));

        seleniumFieldSteps.clickField(fieldName);

        Waiting.on(Duration.ofSeconds(1))
                .ignoring(AssertionError.class)
                .check(() -> found && collectedFailMessage.isEmpty())
                .ifNegative(() ->
                        fail("В течение секунды не произошло искомого запроса!\n" + collectedFailMessage)
                );
    }

    private void triggerFail(String s) {
        collectedFailMessage = s;
    }
    private String collectedFailMessage = "";

    public JsonNode increment(JsonNode json, HttpMethod method) {
        int i = 0;
        if (json.get(method.name())!=null) {
            i = json.get(method.name()).intValue();
        }
        ((ObjectNode)json).put(method.name(), i+1);
        return json;
    }

    @SneakyThrows
    @TestStep("в переменной \"${variable}\" накопить количество запросов по маске URL")
    public void checkRequests(final String variable, final String url) {
        checkProxy();

        IVariableScope scope = storage.getDefaultScope();
        storage.setVariable(scope, variable, String.valueOf(0));
        RequestFilter filter = (request, contents, messageInfo) -> {
            if (Pattern.compile(url).matcher(messageInfo.getOriginalUrl()).matches()) {
                String logText = "Перехвачен запрос!\n" + request + "\n";
                String var = String.valueOf(storage.getVariable(scope, variable));
//              Пустая переменная - пустой json
                if (Objects.equals(var, "") || Objects.equals(var, "0")) {
                    var = "{}";
                }
                try {
                    JsonNode jsonContent = json(var);
                    JsonNode increment = increment(jsonContent, request.method());
                    storage.setVariable(scope, variable, String.valueOf(increment));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                log.debug(logText);
            }
            if (unsafeRequest(request)) return responseOk;
            return null;
        };
        myProxy.proxy.addFirstHttpFilterFactory(new RequestFilterAdapter.FilterSource(filter, 16777216));
    }

    @TestStep("в переменной \"${variable}\" накопить количество \"${method}\"-запросов по маске URL с телом, таким что")
    public void checkRequestsByJsonField(final String variable, final String method, final String url, List<FindCondition> conditions ) {
        checkProxy();

        IVariableScope scope = storage.getDefaultScope();
        storage.setVariable(scope, variable, String.valueOf(0));
        RequestFilter filter = (request, contents, messageInfo) -> {
            if (request.method() == HttpMethod.valueOf(method) && Pattern.compile(url).matcher(messageInfo.getOriginalUrl()).matches()) {

                String messageContents = contents.getTextContents();
                JsonNode jsonContent = null;
                try {
                    jsonContent = json(messageContents);
                } catch (JsonProcessingException e) {
                    log.error("Тело перехваченного запроса не является валидным Json!");
                    fail(e.getMessage());
                }

                final StringBuilder logText = new StringBuilder("Перехвачен запрос! Проверяется messageContents:\n");
                logText.append(messageContents).append("\n");
                boolean match = true;
                for (FindCondition row : conditions) {
                    final String expected = evalVariable(row.getValue()).replace("\\n", "\n");
                    String actual = jsonContent.findValue(row.getFieldName()).asText();
                    match = compareManager.checkValue(row.getOperator(), actual, expected);
                    if (!match) {
                        logText.append(String.format("Элемент не подошел по параметру: \"%s\". Ожидалось: \"%s\" %s \"%s\"",
                                row.getFieldName(), row.getFieldName(), row.getOperator(), expected));
                        log.debug(logText.toString());
                        break;
                    }
                }
                if (match) {
                    int increment = Integer.parseInt(String.valueOf(storage.getVariable(scope, variable))) + 1;
                    storage.setVariable(scope, variable, String.valueOf(increment));
                    log.debug(String.format("%sЭлемент тела запроса подошел по всем заданным параметрам поиска.", logText));
                }
            return responseOk;
            }
            else if (unsafeRequest(request)) return responseOk;
            return null;
        };
        myProxy.proxy.addFirstHttpFilterFactory(new RequestFilterAdapter.FilterSource(filter, 16777216));
    }


}
