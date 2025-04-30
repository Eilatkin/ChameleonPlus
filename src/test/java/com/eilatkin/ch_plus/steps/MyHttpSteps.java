package com.eilatkin.ch_plus.steps;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import io.restassured.RestAssured;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ibsqa.chameleon.selenium.driver.IDriverManager;
import ru.ibsqa.chameleon.steps.AbstractSteps;
import ru.ibsqa.chameleon.steps.TestStep;
import java.lang.String;
import static org.junit.jupiter.api.Assertions.assertEquals;


@Component
@Slf4j
public class MyHttpSteps extends AbstractSteps {

    private final String applicationUrl = System.getProperty("applicationUrl");

    @Autowired
    private IDriverManager driverManager;

    @TestStep("произведена авторизация с помощью токена пользователя \"${login}\" с паролем \"${password}\"")
    public void authenticate(String login, String password) {

        RequestSpecification httpRequestAuth = RestAssured
                .given()
                .relaxedHTTPSValidation()
                .contentType("application/json")
                .body(String.format("{\"login\":\"%s\",\"password\":\"%s\"}",login,password));
        Response auth = httpRequestAuth.post(applicationUrl + "api/user/login");
        assertEquals(auth.getStatusCode(),200,
                "Неудачная попытка аутентификации, проверьте адрес, логин и пароль!");
        String token = auth.jsonPath().get("token");

        WebDriver driver = driverManager.getLastDriver();

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("localStorage.setItem(arguments[0],arguments[1])","token",token);
    }

    public void reauthenticateAndClearLocalStorage(String login, String password) {
        WebDriver driver = driverManager.getLastDriver();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("localStorage.clear()");

        authenticate(login, password);
    }

    public void deauthenticate() {
        WebDriver driver = driverManager.getLastDriver();

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("localStorage.clear()");

        driver.navigate().refresh();
    }

}
