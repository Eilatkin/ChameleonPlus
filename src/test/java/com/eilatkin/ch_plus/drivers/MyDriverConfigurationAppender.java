package com.eilatkin.ch_plus.drivers;


import com.browserup.bup.BrowserUpProxyServer;
import com.browserup.bup.filters.RequestFilterAdapter;
import com.browserup.bup.filters.ResponseFilterAdapter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.springframework.stereotype.Component;
import ru.ibsqa.chameleon.selenium.driver.WebSupportedDriver;
import ru.ibsqa.chameleon.selenium.driver.configuration.IDriverConfiguration;
import ru.ibsqa.chameleon.selenium.driver.configuration.IDriverConfigurationAppender;

import static com.browserup.bup.client.ClientUtil.createSeleniumProxy;

import java.io.File;


/**
 * Бины, имплементирующие интерфейс IDriverConfigurationAppender, могут быть использованы для точной настройки
 * конфигурации драйвера. В данном примере добавляется запуск в режиме "инкогнито". Для типов драйвера CHROME и
 * FIREFOX это делается разными способами. Для других типов драйвера в данном примере никаких действий не выполняется.
 * Поведение управляется параметром командной строки incognito. Этот параметр зарегистрирован в файле
 * src/test/resources/properties.xml и следовательно отображается в плагине на вкладке Настройки.
 */
@Slf4j
@Component
public class MyDriverConfigurationAppender implements IDriverConfigurationAppender {

    // Получение параметра из командной строки
    private final boolean incognito = Boolean.parseBoolean(System.getProperty("incognito", "false"));
    private final boolean ingnorecerterr = Boolean.parseBoolean(System.getProperty("ignore certificate errors", "false"));
    private final boolean headless = Boolean.parseBoolean(System.getProperty("headless", "true"));
    private final boolean seleniumProxy = Boolean.parseBoolean(System.getProperty("seleniumProxy", "true"));
    private final String windowsizeW = System.getProperty("windowsizeW", "1920");
    private final String windowsizeH = System.getProperty("windowsizeH", "1080");
    private final String url = System.getProperty("applicationUrl");
    private final String addExtension = System.getProperty("addExtention", "");
    public static final BrowserUpProxyServer proxy = new BrowserUpProxyServer();

    @Override
    public void appendToConfiguration(IDriverConfiguration driverConfiguration) {
        if (WebSupportedDriver.CHROME.equals(driverConfiguration.getDriverType())) {
            ChromeOptions options = new ChromeOptions();
            if (incognito) options.addArguments("incognito");
            if (ingnorecerterr) {
                options.addArguments("ignore-certificate-errors");
                options.setCapability("acceptInsecureCerts", true);
            }
            if (headless) options.addArguments("headless=new");
             if (addExtension.length() > 1) options.addExtensions (new File(addExtension));
            options.addArguments("--no-sandbox", "--disable-dev-shm-usage");
            options.addArguments("window-size=" + windowsizeW + "," + windowsizeH);
//            для решения ошибки загрузки файлов "Скачивание файла из незащищенного источника заблокировано":
            options.addArguments("unsafely-treat-insecure-origin-as-secure=" + url);

            if (seleniumProxy) {
                proxy.setTrustAllServers(true);
                proxy.start(0);
                Proxy seleniumProxy = createSeleniumProxy(proxy);
                final String proxyStr = "localhost:" + proxy.getPort();
                seleniumProxy.setHttpProxy(proxyStr);
                seleniumProxy.setSslProxy(proxyStr);
// При использовании proxy - возникает ошибка netty Response entity too large, попытка решить:
                proxy.addFirstHttpFilterFactory(new RequestFilterAdapter.FilterSource(null, 16777216));
                proxy.addFirstHttpFilterFactory(new ResponseFilterAdapter.FilterSource(null, 16777216));
                options.setCapability(CapabilityType.PROXY, seleniumProxy);
            }

            driverConfiguration.setOptions(options);

        } else if (WebSupportedDriver.FIREFOX.equals(driverConfiguration.getDriverType())) {
            FirefoxOptions options = new FirefoxOptions();
            if (incognito) options.addArguments("-private");
            if (headless) options.addArguments("-headless");
            options.addArguments("--width=" + windowsizeW);
            options.addArguments("--height=" + windowsizeH);
            driverConfiguration.setOptions(options);
        }
    }

}
