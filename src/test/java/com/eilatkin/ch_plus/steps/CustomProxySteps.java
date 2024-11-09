package com.eilatkin.ch_plus.steps;

import com.browserup.bup.filters.ResponseFilter;
import org.apache.http.HttpHeaders;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ibsqa.chameleon.selenium.driver.IDriverManager;
import ru.ibsqa.chameleon.steps.BrowserSteps;
import ru.ibsqa.chameleon.steps.TestStep;
import ru.ibsqa.chameleon.steps.UIStep;

import static com.eilatkin.ch_plus.drivers.MyDriverConfigurationAppender.proxy;

import java.util.regex.Pattern;


@Component
@Slf4j
public class CustomProxySteps extends BrowserSteps {

    @Autowired
    private IDriverManager driverManager;

    private String originalWindow;


    @UIStep
    @TestStep("заблокировать запрос")
    public void blockRequestTo(final String url, final int responseCode) {
        proxy.addRequestFilter((request, contents, messageInfo) -> {
            if (Pattern.compile(url).matcher(messageInfo.getOriginalUrl()).matches()) {
                final HttpResponse response = new DefaultHttpResponse(
                        request.getProtocolVersion(),
                        HttpResponseStatus.valueOf(responseCode));

                response.headers().add(HttpHeaders.CONNECTION, "Close");

                return response;
            }
            return null;
        });
    }


    @UIStep
    @TestStep("подменить тело ответа")
    public void overrideContent(final String url, final int responseCode, final String responseBody) {
       if (!proxy.isStarted()) {
           log.error("Режим использования Proxy выключен!");
       }
       ResponseFilter filter = (response, contents, messageInfo) -> {
            if (Pattern.compile(url).matcher(messageInfo.getOriginalUrl()).matches()) {
                if (contents!=null) contents.setTextContents(responseBody);
                response.setStatus(HttpResponseStatus.valueOf(responseCode));
            }
        };
        proxy.addResponseFilter(filter);
//        proxy.addFirstHttpFilterFactory(new ResponseFilterAdapter.FilterSource(filter, 16777216));
    }
}
