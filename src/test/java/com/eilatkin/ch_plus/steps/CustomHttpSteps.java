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
import ru.ibsqa.chameleon.steps.UIStep;
import java.lang.String;
import static org.junit.jupiter.api.Assertions.assertEquals;


@Component
@Slf4j
public class CustomHttpSteps extends AbstractSteps {

    private final String applicationUrl = System.getProperty("applicationUrl", "http://127.0.0.1/");

    @Autowired
    private IDriverManager driverManager;

    @UIStep
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
}
