package com.eilatkin.ch_plus.steps;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Keys;
import org.springframework.stereotype.Component;
import ru.ibsqa.chameleon.elements.selenium.IFacadeSelenium;
import ru.ibsqa.chameleon.steps.*;
import ru.ibsqa.chameleon.steps.TestStep;
import ru.ibsqa.chameleon.steps.CoreFieldSteps;
import ru.ibsqa.chameleon.utils.waiting.Waiting;

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
public class CustomSeleniumSteps extends CoreFieldSteps {

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
}