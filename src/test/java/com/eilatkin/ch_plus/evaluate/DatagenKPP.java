package com.eilatkin.ch_plus.evaluate;

import org.springframework.stereotype.Component;
import ru.ibsqa.chameleon.evaluate.AbstractEvaluator;
import ru.ibsqa.chameleon.evaluate.Evaluator;
import ru.ibsqa.chameleon.storage.IVariableScope;
import java.util.Random;

/**
 * КПП Юр лица
 */
@Component
@Evaluator({
        "#datagenKPP{}"
})
public class DatagenKPP extends AbstractEvaluator {

    @Override
    protected String getPlaceHolderName() {
        return "datagenKPP";
    }

    @Override
    protected boolean isMultiArgs() {
        return false;
    }

    @Override
    protected String evalExpression(IVariableScope variableScope, String... args) {
//      9 цифр, типично ХХХХ01001, ХХХХ43001, ХХХХ35001 либо ХХХХ77001
//      допускаются латинские буквы - редко, не включаю пока в генерацию
        int[] postfix = {1001, 43001, 35001, 77001};
        Random rn = new Random();
        int range = 9999 - 1000 + 1;
        int randomNum =  rn.nextInt(range) + 1000;
        int index = rn.nextInt(4);
        return String.valueOf((randomNum*100000) + postfix[index]);
    }
}

