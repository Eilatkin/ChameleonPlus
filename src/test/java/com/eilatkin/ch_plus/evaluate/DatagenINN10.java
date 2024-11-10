package com.eilatkin.ch_plus.evaluate;

import org.springframework.stereotype.Component;
import pro.dagen.DataGenerator;
import ru.ibsqa.chameleon.evaluate.AbstractEvaluator;
import ru.ibsqa.chameleon.evaluate.Evaluator;
import ru.ibsqa.chameleon.storage.IVariableScope;

/**
 * ИНН Юр лица
 */
@Component
@Evaluator({
        "#datagenINN10{}"
})
public class DatagenINN10 extends AbstractEvaluator {

    @Override
    protected String getPlaceHolderName() {
        return "datagenINN10";
    }

    @Override
    protected boolean isMultiArgs() {
        return false;
    }

    @Override
    protected String evalExpression(IVariableScope variableScope, String... args) {
        return DataGenerator.accountDetails().inn10();
    }
}

