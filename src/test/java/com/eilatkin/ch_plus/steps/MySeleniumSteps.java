package com.eilatkin.ch_plus.steps;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ibsqa.chameleon.elements.selenium.IFacadeSelenium;
import ru.ibsqa.chameleon.selenium.driver.IDriverManager;
import ru.ibsqa.chameleon.steps.*;
import ru.ibsqa.chameleon.steps.TestStep;
import ru.ibsqa.chameleon.storage.IVariableScope;
import ru.ibsqa.chameleon.storage.IVariableStorage;
import ru.ibsqa.chameleon.steps.CoreFieldSteps;
import ru.ibsqa.chameleon.utils.waiting.Waiting;
import org.openqa.selenium.interactions.Actions;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.jupiter.api.Assertions.fail;


/**
 * Допиливаю фреймворк собственными шагами.
 * rewriteField - реализует перезапись текстового поля,
 * пришлось делать т.к. стандартный шаг "поле очищено" не работал.
 */
@Component
@Slf4j
public class MySeleniumSteps extends CoreFieldSteps {

    @Autowired
    private IDriverManager driverManager;
    @Autowired
    private IVariableStorage storage;

    @UIStep
    @TestStep("поле \"${fieldName}\" перезаписано значением  \"${text}\"")
    public void rewriteField(String fieldName, String text) {
        IFacadeSelenium field = getSeleniumField(fieldName);
        field.getWrappedElement().sendKeys(Keys.chord(Keys.CONTROL, "a"));
        if (text.equals("")) field.getWrappedElement().sendKeys(Keys.BACK_SPACE);
            else field.sendKeys(text);
    }

    @SuppressWarnings("unchecked")
    public <T extends IFacadeSelenium> T getSeleniumField(String fieldName) {
        return (T) super.getField(fieldName, IFacadeSelenium.class);
    }

    @UIStep
    @TestStep("чекбокс в поле \"${fieldName}\" в положении \"${state}\", выполнять следующие шаги:")
    public boolean checkSakuraCheckbox(String fieldName, String state) {
        IFacadeSelenium field = getSeleniumField(fieldName);
        if (!state.equals("вкл") & !state.equals("выкл"))
            fail(message("Неизвестное значение! положение чекбокса задается как \"вкл\", либо \"выкл\""));
        AtomicReference<String> actualClass = new AtomicReference<>();
        AtomicReference<String> actualAriaChecked = new AtomicReference<>();
        AtomicBoolean result = new AtomicBoolean(false);
        Waiting.on(field).check(() -> {
                    actualClass.set(Optional.ofNullable(getSeleniumField(fieldName).getAttribute("class")).orElse(""));
                    actualAriaChecked.set(Optional.ofNullable(getSeleniumField(fieldName).getAttribute("aria-checked")).orElse(""));
                    result.set(actualAriaChecked.get().equals("true") || actualClass.get().equals("toggle-checked"));
                    return (actualAriaChecked.get().equals("true") || actualAriaChecked.get().equals("false") || actualClass.get().equals("toggle-checked") || actualClass.get().equals("toggle"));
                }
        );
        return result.get()&state.equals("вкл") || !result.get()&state.equals("выкл");
    }

    @UIStep
    @SneakyThrows
    @TestStep("в поле \"${fieldName}\" загружен файл \"${fileName}\"")
    public void uploadFile(String fieldName, String fileName) {
        IFacadeSelenium field = getSeleniumField(fieldName);
        URL res = getClass().getClassLoader().getResource("uploads/"+fileName);
        assert res != null;
        File file = Paths.get(res.toURI()).toFile();
        String absolutePath = file.getAbsolutePath();
        field.sendKeys(absolutePath);
    }

    @UIStep
    @SneakyThrows
    @TestStep("выполнен drag-and-drop элемента \"${fieldNameFrom}\" в \"${fieldNameTo}\"")
    public void dragAndDrop(String fieldNameFrom, String fieldNameTo) {
        IFacadeSelenium fieldFrom = getSeleniumField(fieldNameFrom);
        IFacadeSelenium fieldTo = getSeleniumField(fieldNameTo);
        WebDriver driver = driverManager.getLastDriver();
        Actions builder = new Actions(driver);
        builder.dragAndDrop(fieldFrom.getWrappedElement(), fieldTo.getWrappedElement()).build().perform();

    }

    @UIStep
    @SneakyThrows
    @TestStep("в переменной \"${variable}\" сохранено значение атрибута \"${attribute}\" поля \"${fieldName}\"")
    public void saveFieldAttribute(String variable, String attribute, String fieldName) {
        IFacadeSelenium field = getSeleniumField(fieldName);
        String attributeValue = Waiting.on(field).get(() ->
                Optional.ofNullable(field.getAttribute(attribute)).orElse(""));
        IVariableScope scope = storage.getDefaultScope();
        storage.setVariable(scope, variable, attributeValue);
    }


}