package com.eilatkin.ch_plus.step_listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.EventHandler;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.TestCaseStarted;

/**
 * Листенер для передачи TestPath в хранимку эталонных скриншотов.
 * Необходимо зарегистрировать в cucumber.plugin свойстве конфигурации cucumber.properties.
 */
@Slf4j
@Component
public class ScreenshotStepListener implements ConcurrentEventListener {

    public String testPath;
    public String testName;

    public void setEventPublisher(EventPublisher eventPublisher) {
        eventPublisher.registerHandlerFor(TestCaseStarted.class, testCaseStartedEventHandler);
    }

    private final EventHandler<TestCaseStarted> testCaseStartedEventHandler = event -> {
        testPath = event.getTestCase().getUri().toString();
        System.setProperty("testPath", testPath);
        testName = event.getTestCase().getName();
        System.setProperty("testName", testName);
    };


}
