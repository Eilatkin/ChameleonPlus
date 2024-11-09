package com.eilatkin.ch_plus.steps;

import org.springframework.beans.factory.annotation.Autowired;
import ru.ibsqa.chameleon.steps.AbstractSteps;
import io.cucumber.java.ru.Когда;
import ru.ibsqa.chameleon.steps.StepDescription;
import ru.ibsqa.chameleon.steps.roles.Value;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CustomBrowserStorySteps extends AbstractSteps {

    @Autowired
    private CustomHttpSteps customHttpSteps;

    @Autowired
    private CustomBrowserSteps customBrowserSteps;

    @StepDescription(action = "UI->Браузер->Авторизация с помощью токена")
    @Когда("^авторизация с помощью токена пользователя \"([^\"]*)\" с паролем \"([^\"]*)\"$")
    public void authenticate(
            @Value String login,
            @Value String password
    ) {
        flow(() ->
                customHttpSteps.authenticate(login, password)
        );
    }

    // шаги для проверки консоли браузера на ошибки, чаще всего следует искать по их критичности - "SEVERE"
    // запрос с пустым текстом ошибки - будет искать любые
    @StepDescription(action = "UI->Браузер->Проверить консоль браузера на ошибку"
            , parameters = {"text - текст ошибки"})
    @Когда("^проверить консоль браузера на ошибку \"([^\"]*)\"$")
    public void checkLogs(
            @Value String text) {
        flow(() ->
                customBrowserSteps.checkLogs(text)
        );
    }

    @StepDescription(action = "UI->Браузер->Собрать логи из консоли браузера"
            , parameters = {"text - текст ошибки"})
    @Когда("^собрать логи из консоли браузера с ошибкой \"([^\"]*)\"$")
    public void collectLogs(
            @Value String text) {
        flow(() ->
                customBrowserSteps.collectLogs(text)
        );
    }

    @StepDescription(action = "UI->Браузер->Сбросить лог ошибок консоли браузера"
            , parameters = {"text - текст ошибки"})
    @Когда("^сбросить лог ошибок из консоли браузера$")
    public void dropLogs() {
        flow(() ->
                customBrowserSteps.dropLogs()
        );
    }

    @StepDescription(action = "UI->Браузер->Открыть новую вкладку")
    @Когда("^открыть новую вкладку браузера$")
    public void openNewTab() {
        flow(() ->
                customBrowserSteps.openNewTab()
        );
    }

    @StepDescription(action = "UI->Браузер->Закрыть вкладку")
    @Когда("^закрыть вкладку браузера$")
    public void closeTab() {
        flow(() ->
                customBrowserSteps.closeTab()
        );
    }

    @StepDescription(action = "UI->Браузер->Загружен файл")
    @Когда("^файл c именем соответствующим выражению \"([^\"]*)\" загружен")
    public void fileIsLoadedWithMatchingName(
            @Value String fileName
    ) {
        flow(() ->
                assertTrue(customBrowserSteps.fileIsLoadedWithMatchingName(fileName), "Файл с заданным именем не был загружен")
        );
    }
}
