package com.eilatkin.ch_plus.steps;

import io.cucumber.java.ru.Когда;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ibsqa.chameleon.steps.AbstractSteps;

/**
 * Пример реализации кастомных шагов уровня BDD.
 */
public class MyDebugStorySteps extends AbstractSteps {

    @Autowired
    private MyDebugSteps myDebugSteps; // здесь подключаем бин с тестовыми шагами

    @Когда("^debug \"([^\"]*)\"")
       public void stepDebug(String param) {
        flow(() ->
                // Здесь происходит вызов кастомного тестового шага, в котором и реализуется основная логика
                myDebugSteps.stepDebug(param)
        );
    }
}
