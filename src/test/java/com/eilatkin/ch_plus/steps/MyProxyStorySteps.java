package com.eilatkin.ch_plus.steps;

import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.DocStringType;
import io.cucumber.java.ru.Дано;
import io.cucumber.java.ru.Тогда;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ibsqa.chameleon.converters.FieldOperatorValueTable;
import ru.ibsqa.chameleon.steps.AbstractSteps;
import ru.ibsqa.chameleon.steps.CoreVariableSteps;
import ru.ibsqa.chameleon.steps.StepDescription;
import ru.ibsqa.chameleon.steps.roles.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Шаги с использованием Selenium Proxy.
 * Предназначены для тестирования функционала фронта в изоляции от слоя данных.
 * А также для тестирования негативных кейсов взаимодействия с API сервера.
 */
public class MyProxyStorySteps extends AbstractSteps {

    @Autowired
    private MyProxySteps myProxySteps;
    @Autowired
    private CoreVariableSteps variableSteps;

    @DocStringType(contentType = "json")
    public JsonNode json(String json) throws JsonProcessingException {
        return new ObjectMapper().readTree(json);
    }

    /**
     * Преобразовать List<FieldValueTable> со столбцами field и value в Map
     *
     * @param conditions
     * @return
     */
    private static List<MyProxySteps.FindCondition> parseConditions(List<FieldOperatorValueTable> conditions) {
        List<MyProxySteps.FindCondition> list = new ArrayList<>();
        for (FieldOperatorValueTable c : conditions) {
            list.add(MyProxySteps.FindCondition.builder()
                    .fieldName(c.getField())
                    .operator(c.getOperator())
                    .value(Optional.ofNullable(c.getValue()).orElse(StringUtils.EMPTY))
                    .build());
        }
        return list;
    }


    @StepDescription(action = "UI->Драйвер->Блокировать запрос API")
    @Дано("^блокировать запрос по url \"([^\"]*)\" с кодом \"([^\"]*)\"$")
    public void blockRequestTo(
            @Value String url,
            @Value String responseCode
    ) {
        flow(() ->
                myProxySteps.blockRequestTo(url, Integer.parseInt(responseCode))
        );
    }

    @StepDescription(action = "UI->Драйвер->Подмена ответа API")
    @Дано("^подменить ответ по url \"([^\"]*)\" с кодом \"([^\"]*)\" и телом$")
    public void overrideContent(
            @Value String url,
            @Value String responseCode,
            @Value String responseBody
    ) {
        flow(() ->
                myProxySteps.overrideContent(url, Integer.parseInt(responseCode), String.valueOf(responseBody))
        );
    }

    @StepDescription(action = "UI->Элементы->Действия->Нажать на элемент"
            , subAction = "Нажать на кнопку и проверить наличие запроса в API"
            , parameters = {"fieldName - наименование поля", "method - http-метод запроса", "url - регулярное выражение для URL"})
    @Тогда("^нажатием на \"([^\"]*)\" произошел \"([^\"]*)\"-запрос по маске URL \"([^\"]*)\"$")
    public void buttonTriggerRequest(
            @Mouse String fieldName,
            @Value String method,
            @Value String url
    ) {
        flow(() ->
                myProxySteps.buttonTriggerRequest(fieldName, method, url)
        );
    }

    @StepDescription(action = "UI->Элементы->Действия->Нажать на элемент"
            , subAction = "Нажать на кнопку и проверить наличие GET-запроса в API"
            , parameters = {"fieldName - наименование поля", "url - регулярное выражение для URL"})
    @Тогда("^нажатием на \"([^\"]*)\" произошел GET-запрос по маске URL \"([^\"]*)\"$")
    public void buttonTriggerRequest(
            @Mouse String fieldName,
            @Value String url
    ) {
        flow(() ->
                myProxySteps.buttonTriggerGETRequest(fieldName, url)
        );
    }

    @StepDescription(action = "UI->Элементы->Действия->Нажать на элемент"
            , subAction = "Нажать на кнопку и проверить формирование запроса в API"
            , parameters = {"fieldName - наименование поля", "method - http-метод запроса", "url - регулярное выражение для URL"})
    @Тогда("^нажатием на \"([^\"]*)\" произошел \"([^\"]*)\"-запрос по маске URL \"([^\"]*)\" с телом, таким что:$")
    public void buttonTriggerRequestWithJsonCheck(
            @Mouse String fieldName,
            @Value String method,
            @Value String url,
            @Read("field") @Operator("operator") @Value("value") List<FieldOperatorValueTable> conditions
    ) {
        flow(() ->
                myProxySteps.buttonTriggerRequestWithJsonCheck(fieldName, method, url, parseConditions(conditions))
        );
    }

    @StepDescription(action = "UI->Драйвер->Проверка количества запросов в API")
    @Дано("^в переменной \"([^\"]*)\" накопить количество запросов по маске URL \"([^\"]*)\"$")
    public void checkRequests(
            @Variable String variable,
            @Value String url
    ) {
        flow(() ->
                myProxySteps.checkRequests(variable, url)
        );
    }

    @StepDescription(action = "UI->Драйвер->Проверка количества запросов в API")
    @Дано("^в переменной \"([^\"]*)\" накопить количество \"([^\"]*)\"-запросов по маске URL \"([^\"]*)\" с телом, таким что:$")
    public void checkRequestsByJsonField(
            @Variable String variable,
            @Value String method,
            @Value String url,
            @Read("field") @Operator("operator") @Value("value") List<FieldOperatorValueTable> conditions
    ) {
        flow(() ->
                myProxySteps.checkRequestsByJsonField(variable, method, url, parseConditions(conditions))
        );
    }

    @StepDescription(action = "Переменные->Сравнить значение выражения"
            , subAction = "Сравнить значение выражения с док-строкой"
            , parameters = {"expression - вычисляемое выражение", "docString - значение"})
    @Тогда("^значение выражения \"([^\"]*)\" равно$")
    public void valueEqualDocstring(
            @Variable String expression,
            @Value String docString) {
        flow(()->
                variableSteps.checkExpressionValue(expression, "равно", docString)
        );
    }
}
