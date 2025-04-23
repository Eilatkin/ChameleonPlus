package com.eilatkin.ch_plus.steps;

import com.browserup.bup.filters.ResponseFilter;
import com.eilatkin.ch_plus.proxy.MyProxy;
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

import java.util.regex.Pattern;


@Component
@Slf4j
public class MyProxySteps extends BrowserSteps {

    @Autowired
    private IDriverManager driverManager;

    private String originalWindow;

    @Autowired
    private MyProxy myProxy;


    @UIStep
    @TestStep("заблокировать запрос")
    public void blockRequestTo(final String url, final int responseCode) {
        myProxy.proxy.addRequestFilter((request, contents, messageInfo) -> {
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
       if (!myProxy.proxy.isStarted()) {
           log.error("Режим использования Proxy выключен!");
       }
       ResponseFilter filter = (response, contents, messageInfo) -> {
            if (Pattern.compile(url).matcher(messageInfo.getOriginalUrl()).matches()) {
                if (contents!=null) contents.setTextContents(responseBody);
                response.setStatus(HttpResponseStatus.valueOf(responseCode));
            }
        };
        myProxy.proxy.addResponseFilter(filter);
    }
}
