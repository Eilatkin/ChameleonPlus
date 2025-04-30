package com.eilatkin.ch_plus.scenario_hooks;

import com.browserup.bup.BrowserUpProxyServer;
import com.eilatkin.ch_plus.proxy.MyProxy;
import com.eilatkin.ch_plus.steps.MyDebugSteps;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import ru.ibsqa.chameleon.utils.spring.SpringUtils;

// Примеры выполнения действий до и после каждого сценария
public class MyHooks {

    // Этот класс не является бином, поэтому вместо @Autowired используется SpringUtils.getBean(..)
    private MyDebugSteps myDebugSteps = SpringUtils.getBean(MyDebugSteps.class);
    private MyProxy myProxy = SpringUtils.getBean(MyProxy.class);
    private final boolean seleniumProxy = Boolean.parseBoolean(System.getProperty("seleniumProxy", "true"));

    @Before // Здесь также можно указать порядок выполнения хука и теги, к которым применяется данный хук.
    //@HiddenStep Используйте эту аннотацию, чтобы скрыть действия в отчете.
    public void before() {
        if (!seleniumProxy || !myProxy.proxy.isStopped()) {
            return;
        }
        int proxyPort = myProxy.proxy.getPort();
        myProxy.proxy = new BrowserUpProxyServer();
        myProxy.startProxy(proxyPort);
        myDebugSteps.stepDebug("Подготовительные действия: стартую прокси на порту " + proxyPort);
    }

    @After
    public void after() {
        if (!seleniumProxy) {
            return;
        }
        int proxyPort = myProxy.proxy.getPort();
        myProxy.proxy.stop();
        myDebugSteps.stepDebug("Завершающие действия: останавливаю прокси на порту " + proxyPort);
    }
}
