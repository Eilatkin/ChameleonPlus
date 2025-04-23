package com.eilatkin.ch_plus.steps;

import io.cucumber.java.ru.Когда;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ibsqa.chameleon.steps.AbstractSteps;
import ru.ibsqa.chameleon.steps.StepDescription;
import ru.ibsqa.chameleon.steps.roles.KeyPress;
import ru.ibsqa.chameleon.steps.roles.Value;


/**
 * Допиливаю фреймворк собственными шагами уровня BDD.
 */
public class MySeleniumStorySteps extends AbstractSteps {

    @Autowired
    private MySeleniumSteps mySeleniumSteps;

    @StepDescription(action = "UI->Элементы->Действия->Перезаписать текст в поле новым"
            , parameters = {"field - наименование поля", "text - новый текст"})
    @Когда("^поле \"([^\"]*)\" перезаписано значением \"([^\"]*)\"$")
       public void rewriteField(
            @KeyPress String field,
            @Value String text) {
        flow(() ->
                mySeleniumSteps.rewriteField(field, text)
        );
    }

    @StepDescription(action = "Условия->Если чекбокс вкл/выкл"
            , parameters = {"fields - наименование поля", "value - значение атрибута"})
    @Когда("^чекбокс в поле \"([^\"]*)\" в положении \"([^\"]*)\", выполнять следующие шаги:$")
    public void checkSakuraCheckbox(
            @KeyPress String field,
            @Value String state
    ) {
        if (getStepFlow().prepareFlowStep()) {
            getStepFlow().createBlock(mySeleniumSteps.checkSakuraCheckbox(field, state));
        }
    }



}
