package com.eilatkin.ch_plus.evaluate;

import org.springframework.stereotype.Component;
import pro.dagen.DataGenerator;
import ru.ibsqa.chameleon.evaluate.AbstractEvaluator;
import ru.ibsqa.chameleon.evaluate.Evaluator;
import ru.ibsqa.chameleon.storage.IVariableScope;

/**
 * ОГРН Юр лица
 */
@Component
@Evaluator({
        "#datagenOGRN{}"
})
public class DatagenOGRN extends AbstractEvaluator {

    @Override
    protected String getPlaceHolderName() {
        return "datagenOGRN";
    }

    @Override
    protected boolean isMultiArgs() {
        return false;
    }

    @Override
    protected String evalExpression(IVariableScope variableScope, String... args) {
        return DataGenerator.accountDetails().ogrn();
    }
}

