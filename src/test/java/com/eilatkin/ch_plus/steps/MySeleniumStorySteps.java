package com.eilatkin.ch_plus.steps;

import io.cucumber.java.ru.Когда;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ibsqa.chameleon.context.Context;
import ru.ibsqa.chameleon.context.ContextChange;
import ru.ibsqa.chameleon.element.ElementTypeCollection;
import ru.ibsqa.chameleon.steps.AbstractSteps;
import ru.ibsqa.chameleon.steps.CollectionSteps;
import ru.ibsqa.chameleon.steps.DebugPluginAction;
import ru.ibsqa.chameleon.steps.StepDescription;
import ru.ibsqa.chameleon.steps.roles.*;


/**
 * Допиливаю фреймворк собственными шагами уровня BDD.
 */
public class MySeleniumStorySteps extends AbstractSteps {

    @Autowired
    private MySeleniumSteps mySeleniumSteps;

    @Autowired
    private CollectionSteps collectionSteps;

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

    @StepDescription(action = "UI->Коллекции->Сохранить количество элементов коллекции"
            , subAction = "сохранено количество всех элементов коллекции"
            , parameters = {"variable - переменная, в которую будет сохранено количество",
            "collectionName - наименование коллекции"
    }, expertView = true)
    @Когда("^в переменной \"([^\"]*)\" сохранено количество элементов коллекции \"([^\"]*)\"$")
    @Context(type = ElementTypeCollection.class, change = ContextChange.USE, parameter = "collectionName")
    public void stepCountCollectionItems(
            @Variable String variable,
            @Collection String collectionName
    ) {
        flow(() -> {
            int count = 0;
            try {
                count = collectionSteps.getItems(collectionName).size();
            } catch (AssertionError ignored) {
            }
            setVariable(variable, String.valueOf(count));
        });
    }

    @StepDescription(action = "UI->Элементы->Действия->Загрузить файл"
            , parameters = {"field - наименование поля", "text - наименование файла"})
    @Когда("^в поле \"([^\"]*)\" загружен файл \"([^\"]*)\"$")
    public void uploadFile(
            @KeyPress String field,
            @Value String text) {
        flow(() ->
                mySeleniumSteps.uploadFile(field, text)
        );
    }

    @StepDescription(action = "UI->Элементы->Действия->Drag-and-drop"
            , subAction = "Drag-and-drop левой кнопкой мыши"
            , parameters = {"fieldNameFrom - наименование поля которое тащим", "fieldNameTo - куда тащим"})
    @Когда("^выполнен drag-and-drop элемента \"([^\"]*)\" в \"([^\"]*)\"$")
    public void dragAndDrop(
            @Mouse String fieldNameFrom,
            @Mouse String fieldNameTo
    ) {
        flow(() -> {
            if (fieldNameFrom.isEmpty() || fieldNameTo.isEmpty()) {
                return;
            }
            mySeleniumSteps.dragAndDrop(fieldNameFrom,fieldNameTo);
        });
    }

    @DebugPluginAction
    @StepDescription(action = "UI->Элементы->Действия->Проверить атрибут поля"
            , subAction = "Сохранить артибут в переменную"
            , parameters = {"attribute - наименоване атрибута", "fieldName - наименование поля", "variable - название переменной"})
    @Когда("^в переменной \"([^\"]*)\" сохранено значение атрибута \"([^\"]*)\" поля \"([^\"]*)\"$")
    public void saveFieldAttribute(
            @Variable String variable,
            @Value String attribute,
            @UIAttr String fieldName
    ) {
        flow(() -> mySeleniumSteps.saveFieldAttribute(variable, attribute, fieldName));
    }


}
