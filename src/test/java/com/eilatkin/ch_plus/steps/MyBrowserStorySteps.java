package com.eilatkin.ch_plus.steps;

import org.springframework.beans.factory.annotation.Autowired;
import ru.ibsqa.chameleon.steps.AbstractSteps;
import io.cucumber.java.ru.Когда;
import ru.ibsqa.chameleon.steps.StepDescription;
import ru.ibsqa.chameleon.steps.roles.Value;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MyBrowserStorySteps extends AbstractSteps {

    @Autowired
    private MyHttpSteps myHttpSteps;

    @Autowired
    private MyBrowserSteps myBrowserSteps;

    @StepDescription(action = "UI->Браузер->Авторизация с помощью токена")
    @Когда("^авторизация с помощью токена пользователя \"([^\"]*)\" с паролем \"([^\"]*)\"$")
    public void authenticate(
            @Value String login,
            @Value String password
    ) {
        flow(() ->
                myHttpSteps.authenticate(login, password)
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
                myBrowserSteps.checkLogs(text)
        );
    }

    @StepDescription(action = "UI->Браузер->Собрать логи из консоли браузера"
            , parameters = {"text - текст ошибки"})
    @Когда("^собрать логи из консоли браузера с ошибкой \"([^\"]*)\"$")
    public void collectLogs(
            @Value String text) {
        flow(() ->
                myBrowserSteps.collectLogs(text)
        );
    }

    @StepDescription(action = "UI->Браузер->Сбросить лог ошибок консоли браузера"
            , parameters = {"text - текст ошибки"})
    @Когда("^сбросить лог ошибок из консоли браузера$")
    public void dropLogs() {
        flow(() ->
                myBrowserSteps.dropLogs()
        );
    }

    @StepDescription(action = "UI->Браузер->Открыть новую вкладку")
    @Когда("^открыть новую вкладку браузера$")
    public void openNewTab() {
        flow(() ->
                myBrowserSteps.openNewTab()
        );
    }

    @StepDescription(action = "UI->Браузер->Закрыть вкладку")
    @Когда("^закрыть вкладку браузера$")
    public void closeTab() {
        flow(() ->
                myBrowserSteps.closeTab()
        );
    }

    @StepDescription(action = "UI->Браузер->Загружен файл")
    @Когда("^файл c именем соответствующим выражению \"([^\"]*)\" загружен")
    public void fileIsLoadedWithMatchingName(
            @Value String fileName
    ) {
        flow(() ->
                assertTrue(myBrowserSteps.fileIsLoadedWithMatchingName(fileName), "Файл с заданным именем не был загружен")
        );
    }
}
