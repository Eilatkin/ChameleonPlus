package com.eilatkin.ch_plus.proxy;

import com.browserup.bup.BrowserUpProxyServer;
import com.browserup.bup.filters.RequestFilter;
import com.browserup.bup.filters.RequestFilterAdapter;
import com.browserup.bup.filters.ResponseFilter;
import com.browserup.bup.filters.ResponseFilterAdapter;
import com.browserup.bup.proxy.CaptureType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

/**
 * Бины, с реализацией прокси и её настроек запуска
 */
@Slf4j
@Component
public class MyProxy {

    private final Integer maxWait = Integer.valueOf(System.getProperty("maxWait", "30"));
    public BrowserUpProxyServer proxy;

    public void startProxy(int proxyPort) {
        BrowserUpProxyServer proxy = new BrowserUpProxyServer();
        proxy.setTrustAllServers(true);
        proxy.disableHarCaptureTypes(EnumSet.allOf(CaptureType.class));
        proxy.setRequestTimeout(maxWait, TimeUnit.SECONDS);
        proxy.setConnectTimeout(maxWait,TimeUnit.SECONDS);
        proxy.setIdleConnectionTimeout(maxWait,TimeUnit.SECONDS);
        RequestFilter requestFilter = (request, contents, messageInfo) -> null;
        ResponseFilter responseFilter = (request, contents, messageInfo) -> {};
        proxy.addFirstHttpFilterFactory(new RequestFilterAdapter.FilterSource(requestFilter, 16777216));
        proxy.addFirstHttpFilterFactory(new ResponseFilterAdapter.FilterSource(responseFilter, 16777216));
        proxy.start(proxyPort);
        this.proxy = proxy;
    }

}
