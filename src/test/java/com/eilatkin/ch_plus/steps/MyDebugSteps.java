package com.eilatkin.ch_plus.steps;

import lombok.extern.slf4j.Slf4j;
import ru.ibsqa.chameleon.steps.AbstractSteps;
import org.springframework.stereotype.Component;
import ru.ibsqa.chameleon.steps.TestStep;

/**
 * Пример реализации кастомных тестовых шагов. Такие шаги не могут напрямую фигурировать в сценарии BDD, но могут
 * быть вызваны из java-кода. В данном примере описан единственный шаг, он выполняет вывод в лог переданного параметра.
 */
@Component
@Slf4j
public class MyDebugSteps extends AbstractSteps {

    @TestStep("debug \"${param}\"")
    public void stepDebug(String param) {
        log.debug("stepDebug: "+param);
    }

}
