package com.eilatkin.ch_plus.drivers;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ibsqa.chameleon.selenium.driver.WebSupportedDriver;
import ru.ibsqa.chameleon.selenium.driver.configuration.IDriverConfiguration;
import ru.ibsqa.chameleon.selenium.driver.configuration.IDriverConfigurationAppender;

import java.io.File;
import java.util.Base64;


/**
 * Бины, имплементирующие интерфейс IDriverConfigurationAppender, могут быть использованы для точной настройки
 * конфигурации драйвера. В данном примере добавляется запуск в режиме "инкогнито". Для типов драйвера CHROME и
 * FIREFOX это делается разными способами. Для других типов драйвера в данном примере никаких действий не выполняется.
 * Поведение управляется параметром командной строки incognito. Этот параметр зарегистрирован в файле
 * src/test/resources/properties.xml и, следовательно, отображается в плагине на вкладке Настройки.
 */
@Slf4j
@Component
public class MyDriverConfigurationAppender implements IDriverConfigurationAppender {

    // Получение параметра из командной строки
    private final boolean incognito = Boolean.parseBoolean(System.getProperty("incognito", "false"));
    private final boolean ignoreCertificateErrors = Boolean.parseBoolean(System.getProperty("ignoreCertificateErrors", "false"));
    private final boolean headless = Boolean.parseBoolean(System.getProperty("headless", "true"));
    private final boolean seleniumProxy = Boolean.parseBoolean(System.getProperty("seleniumProxy", "true"));
    private final String windowSizeW = System.getProperty("windowSizeW", "1920");
    private final String windowSizeH = System.getProperty("windowSizeH", "1080");
    private final String url = System.getProperty("applicationUrl");
    private final String addExtension = System.getProperty("addExtension", "");
    @Autowired
    private com.eilatkin.ch_plus.proxy.MyProxy myProxy;

    @Override
    public void appendToConfiguration(IDriverConfiguration driverConfiguration) {
        if (WebSupportedDriver.CHROME.equals(driverConfiguration.getDriverType())) {
            ChromeOptions options = new ChromeOptions();
            if (incognito) options.addArguments("incognito");
            if (ignoreCertificateErrors) {
                options.addArguments("ignore-certificate-errors");
                options.setCapability("acceptInsecureCerts", true);
            }
            if (headless) options.addArguments("headless=new");
            if (addExtension.length() > 1) options.addExtensions (new File(addExtension));
            options.addArguments("--no-sandbox", "--disable-dev-shm-usage");
            options.addArguments("window-size=" + windowSizeW + "," + windowSizeH);
//            для решения ошибки загрузки файлов "Скачивание файла из незащищенного источника заблокировано":
            options.addArguments("unsafely-treat-insecure-origin-as-secure=" + url);

            if (seleniumProxy) {
                myProxy.startProxy(0);
//                Пропускать Вебсокет используя pac-файл:
                int proxyPort = myProxy.proxy.getPort();
                String pacFunction = "function FindProxyForURL(url, host) { if (url.substring(0, 3) === \"ws:\" || url.substring(0, 4) === \"wss:\") { "
                        +  "return \"DIRECT\"; } else { return \"PROXY localhost:" + proxyPort + "\"; } }";
                Proxy seleniumProxy = new Proxy();
                seleniumProxy.setProxyType(Proxy.ProxyType.PAC);
                seleniumProxy.setProxyAutoconfigUrl("data:application/x-javascript-config;base64," + Base64.getUrlEncoder().encodeToString(pacFunction.getBytes()));
                options.setCapability(CapabilityType.PROXY, seleniumProxy);
            }

            driverConfiguration.setOptions(options);

        } else if (WebSupportedDriver.FIREFOX.equals(driverConfiguration.getDriverType())) {
            FirefoxOptions options = new FirefoxOptions();
            if (incognito) options.addArguments("-private");
            if (headless) options.addArguments("-headless");
            options.addArguments("--width=" + windowSizeW);
            options.addArguments("--height=" + windowSizeH);
            driverConfiguration.setOptions(options);
        }
    }

}
