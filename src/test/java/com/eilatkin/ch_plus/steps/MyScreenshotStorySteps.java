package com.eilatkin.ch_plus.steps;

import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Тогда;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ibsqa.chameleon.steps.AbstractSteps;
import ru.ibsqa.chameleon.steps.StepDescription;
import ru.ibsqa.chameleon.steps.roles.KeyPress;


/**
 * Шаги уровня BDD для скриншотного тестирования.
 */
public class MyScreenshotStorySteps extends AbstractSteps {

    @Autowired
    private MyScreenshotSteps myScreenshotSteps;

    private final int allowedDiff = Integer.parseInt(System.getProperty("allowedDiff"));

    @StepDescription(action = "UI->Прочее->Сделать и сверить с эталонным скриншот всей страницы")
    @Тогда("^сделать и сверить с эталонным скриншот всей страницы$")
       public void pageScreenshot() {
        flow(() ->
                myScreenshotSteps.pageScreenshot(allowedDiff)
        );
    }

    @StepDescription(action = "UI->Прочее->Сделать и сверить с эталонным скриншот видимой области экрана")
    @Тогда("^сделать и сверить с эталонным скриншот видимой области экрана$")
    public void viewScreenshot() {
        flow(() ->
                myScreenshotSteps.viewScreenshot(allowedDiff)
        );
    }

    @StepDescription(action = "UI->Прочее->Сделать и сверить с эталонным скриншот элемента")
    @Тогда("^сделать и сверить с эталонным скриншот элемента \"([^\"]*)\"$")
    public void elementScreenshot(@KeyPress String fieldName) {
        flow(() ->
                myScreenshotSteps.elementScreenshot(fieldName,allowedDiff)
        );
    }

    @StepDescription(action = "UI->Прочее->Скрыть видимость элементов по Xpath")
    @Когда("^скрыть видимость элементов по Xpath \"([^\"]*)\"$")
    public void disableElements(String xpath) {
        flow(() ->
                myScreenshotSteps.disableElements(xpath)
        );
    }

    @StepDescription(action = "UI->Прочее->Залить хромакей элементы по Xpath")
    @Когда("^залить хромакей элементы по Xpath \"([^\"]*)\"$")
    public void chromaKeyElements(String xpath) {
        flow(() ->
                myScreenshotSteps.chromaKeyElements(xpath)
        );
    }


}
