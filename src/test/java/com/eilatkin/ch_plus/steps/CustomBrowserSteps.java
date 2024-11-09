package com.eilatkin.ch_plus.steps;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ibsqa.chameleon.selenium.driver.IDriverManager;
import ru.ibsqa.chameleon.steps.BrowserSteps;
import ru.ibsqa.chameleon.steps.TestStep;
import ru.ibsqa.chameleon.steps.UIStep;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.openqa.selenium.logging.LogType.BROWSER;

@Component
@Slf4j
public class CustomBrowserSteps extends BrowserSteps {

    @Autowired
    private IDriverManager driverManager;

    private String originalWindow;

    @UIStep
    @TestStep("проверить консоль браузера на ошибку \"${error}\"")
    public void checkLogs(String error) {
        LogEntries entry = driverManager.getLastDriver().manage().logs().get(BROWSER);
        List<LogEntry> Logs = entry.getAll();
        for(LogEntry Log : Logs) {
            assertFalse(String.valueOf(Log).contains(error), "Найдена ошибка: " + Log);
        }
    }

    @UIStep
    @TestStep("собрать логи из консоли браузера с ошибкой \"${error}\"")
    public void collectLogs(String error) {
        LogEntries entry = driverManager.getLastDriver().manage().logs().get(BROWSER);
        List<LogEntry> Logs = entry.getAll();
        StringBuilder ErrLog = new StringBuilder();
        for(LogEntry Log : Logs) {
            if (String.valueOf(Log).contains(error)) ErrLog.append(Log);
        }
        assertEquals(0, ErrLog.length(), "Найдены ошибки: " + ErrLog);
    }

    @UIStep
    @TestStep("сбросить лог ошибок из консоли браузера")
    public void dropLogs() {
        LogEntries entry = driverManager.getLastDriver().manage().logs().get(BROWSER);
        List<LogEntry> Logs = entry.getAll();
        StringBuilder ErrLog = new StringBuilder();
        for(LogEntry Log : Logs) {
            ErrLog.append(Log);
        }
        log.warn("Проигнорированы следующие ошибки консоли браузера: " + ErrLog);
    }

    @UIStep
    @TestStep("открыть новую вкладку браузера")
    public void openNewTab() {
        originalWindow = driverManager.getLastDriver().getWindowHandle();
        driverManager.getLastDriver().switchTo().newWindow(WindowType.TAB);
    }

    @UIStep
    @TestStep("закрыть вкладку браузера")
    public void closeTab() {
        assertNotNull(originalWindow,"Ошибка: прежде чем закрывать текущую рабочую вкладку следует открыть новую!");
        driverManager.getLastDriver().close();
        driverManager.getLastDriver().switchTo().window(originalWindow);
    }
    public String pickLatestFileFromDownloads() {

        String currentUsersHomeDir = System.getProperty("user.home");
        String downloadFolder = currentUsersHomeDir + File.separator + "Downloads" + File.separator;

        File dir = new File(downloadFolder);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            log.error("В загрузках отсутствуют файлы!");
        }

        assert files != null;
        File lastModifiedFile = files[0];
        for (int i = 1; i < files.length; i++) {
            if (lastModifiedFile.lastModified() < files[i].lastModified()) {
                lastModifiedFile = files[i];
            }
        }
        String k = lastModifiedFile.toString();

        log.debug(String.valueOf(lastModifiedFile));
        Path p = Paths.get(k);
        return p.getFileName().toString();
    }

    @UIStep
    @TestStep
    public boolean fileIsLoadedWithMatchingName(String fileNameRegex) {
        String filenameLatestDownload = pickLatestFileFromDownloads();
        return filenameLatestDownload.matches(fileNameRegex);

    }
}
