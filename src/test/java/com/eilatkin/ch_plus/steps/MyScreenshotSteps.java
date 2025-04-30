package com.eilatkin.ch_plus.steps;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;
import ru.ibsqa.chameleon.elements.selenium.IFacadeSelenium;
import ru.ibsqa.chameleon.selenium.driver.IDriverManager;
import ru.ibsqa.chameleon.steps.*;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ibsqa.chameleon.utils.waiting.Waiting;

@Component
@Slf4j
public class MyScreenshotSteps extends AbstractSteps {

    @Autowired
    private Screenshotter screenshotter;
    @Autowired
    private SeleniumFieldSteps seleniumFieldSteps;
    @Autowired
    private IDriverManager driverManager;

    @UIStep
    @TestStep("Сделать и сверить с эталонным скриншот всей страницы")
    public void pageScreenshot(int allowedDiff) {
        WebDriver driver = driverManager.getLastDriver();
        screenshotter.makePageScreenshotWithScroll(driver);
        screenshotter.compareScreenshots(allowedDiff);
    }

    @UIStep
    @TestStep("Сделать и сверить с эталонным скриншот видимой области экрана")
    public void viewScreenshot(int allowedDiff) {
        WebDriver driver = driverManager.getLastDriver();
        screenshotter.makePageScreenshotWithoutScroll(driver);
        screenshotter.compareScreenshots(allowedDiff);
    }

    @UIStep
    @TestStep("Сделать и сверить с эталонным скриншот элемента")
    public void elementScreenshot(String fieldName, int allowedDiff) {
        IFacadeSelenium field = seleniumFieldSteps.getSeleniumField(fieldName);
        Waiting.on(field).apply(field::scrollIntoView);
        WebDriver driver = driverManager.getLastDriver();
        screenshotter.makeElementScreenshotWithoutScroll(driver,field.getWrappedElement());
        screenshotter.compareScreenshots(allowedDiff);
    }

    @UIStep
    @TestStep("Сделать и сверить с эталонным скриншот сайдпанели")
    public void sidepanelScreenshot(String fieldName, int allowedDiff) {
        IFacadeSelenium field = seleniumFieldSteps.getSeleniumField(fieldName);
        Waiting.on(field).apply(field::scrollIntoView);
        WebDriver driver = driverManager.getLastDriver();
        screenshotter.makeElementScreenshotWithoutScroll(driver,field.getWrappedElement());
        screenshotter.compareScreenshots(allowedDiff);
    }

    @UIStep
    @TestStep("Скрыть видимость элементов по Xpath {xpath}")
    public void disableElements(String xpath) {
            JavascriptExecutor js = driverManager.getLastDriver();
            Object output = js.executeScript(
                    "function hideElementsByXPath(xpathExpression) {\n"
                            + "const result = document.evaluate(xpathExpression, document, "
                            + "null, XPathResult.ORDERED_NODE_ITERATOR_TYPE, null);\n"
                            + "let element = result.iterateNext();\n"
                            + "while (element) {\n"
                            + "element.style.opacity = 0;\n"
                            + "element = result.iterateNext();\n"
                            + "}}"
                            + String.format("hideElementsByXPath(\"%s\");", xpath));
    }

    @UIStep
    @TestStep("Залить хромакей элементы по Xpath {xpath}")
    public void chromaKeyElements(String xpath) {
        JavascriptExecutor js = driverManager.getLastDriver();
        Object output = js.executeScript(
                "function hideElementsByXPath(xpathExpression) {\n"
                        + "const result = document.evaluate(xpathExpression, document, "
                        + "null, XPathResult.ORDERED_NODE_ITERATOR_TYPE, null);\n"
                        + "let element = result.iterateNext();\n"
                        + "while (element) {\n"
                        + "for (const child of element.children) {\n"
                        + "  child.style.opacity = 0;\n"
                        + "}"
                        + "element.style.opacity = 1;\n"
                        + "element.style.backgroundColor = 'magenta';\n"
                        + "element.style.color = 'magenta';\n"
                        + "element = result.iterateNext();\n"
                        + "}}"
                        + String.format("hideElementsByXPath(\"%s\");", xpath));
    }


}
