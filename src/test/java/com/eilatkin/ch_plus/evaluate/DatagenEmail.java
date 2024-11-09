package com.eilatkin.ch_plus.evaluate;

import org.springframework.stereotype.Component;
import pro.dagen.DataGenerator;
import ru.ibsqa.chameleon.evaluate.AbstractEvaluator;
import ru.ibsqa.chameleon.evaluate.Evaluator;
import ru.ibsqa.chameleon.storage.IVariableScope;

/**
 * Случайно сгенерированный email
 * без аргумента - случайный домен,
 * с аргументом предзаданный,
 * например:
 * #datagenEmail{test.ru} => abrakadabra@test.ru
 */
@Component
@Evaluator({
        "#datagenEmail{}"
})
public class DatagenEmail extends AbstractEvaluator {

    @Override
    protected String getPlaceHolderName() {
        return "datagenEmail";
    }

    @Override
    protected boolean isMultiArgs() {
        return false;
    }

    @Override
    protected String evalExpression(IVariableScope variableScope, String... args) {
        String str = extract("0", args, 0);
        if (str.equals("0")) return DataGenerator.contacts().email();
        return DataGenerator.contacts().email(str);
    }
}

