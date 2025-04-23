package com.eilatkin.ch_plus.steps;

import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.DocStringType;
import io.cucumber.java.ru.Дано;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ibsqa.chameleon.steps.AbstractSteps;
import ru.ibsqa.chameleon.steps.StepDescription;
import ru.ibsqa.chameleon.steps.roles.Value;


public class MyProxyStorySteps extends AbstractSteps {

    @Autowired
    private MyProxySteps myProxySteps;

    @DocStringType(contentType = "json")
    public JsonNode json(String json) throws JsonProcessingException {
        return new ObjectMapper().readTree(json);
    }

    @StepDescription(action = "UI->Драйвер->Блокировать запрос API")
    @Дано("^блокировать запрос по url \"([^\"]*)\" с кодом \"([^\"]*)\"$")
    public void blockRequestTo(
            @Value String url,
            @Value String responseCode
    ) {
        flow(() ->
                myProxySteps.blockRequestTo(url, Integer.parseInt(responseCode))
        );
    }

    @StepDescription(action = "UI->Драйвер->Подмена ответа API")
    @Дано("^подменить ответ по url \"([^\"]*)\" с кодом \"([^\"]*)\" и телом$")
    public void overrideContent(
            @Value String url,
            @Value String responseCode,
            @Value JsonNode responseBody
    ) {
        flow(() ->
                myProxySteps.overrideContent(url, Integer.parseInt(responseCode), String.valueOf(responseBody))
        );
    }

}
