package com.ite.sakura.evaluate;

import org.springframework.stereotype.Component;
import pro.dagen.DataGenerator;
import ru.ibsqa.chameleon.evaluate.AbstractEvaluator;
import ru.ibsqa.chameleon.evaluate.Evaluator;
import ru.ibsqa.chameleon.storage.IVariableScope;

/**
 * Случайный мобильный телефон.
 * Возвращается строка в формате +7 YYY XXX XXXX,
 * где YYY соответствует коду мобильного оператора.
 */
@Component
@Evaluator({
        "#datagenMobile{}"
})
public class DatagenMobile extends AbstractEvaluator {

    @Override
    protected String getPlaceHolderName() {
        return "datagenMobile";
    }

    @Override
    protected boolean isMultiArgs() {
        return false;
    }

    @Override
    protected String evalExpression(IVariableScope variableScope, String... args) {
        return DataGenerator.contacts().mobile().toString();
    }
}
