package com.eilatkin.ch_plus.evaluate;

import org.springframework.stereotype.Component;
import ru.ibsqa.chameleon.evaluate.AbstractEvaluator;
import ru.ibsqa.chameleon.evaluate.Evaluator;
import ru.ibsqa.chameleon.storage.IVariableScope;

/**
 * Укорачивает строку отсекая первые N символов
 * Пример использования:
 *      #stringCutter{Дата регистрации события;12}
 *      результат: 'Дата регистр'
 *      #stringCutter{Дата;10}
 *      результат: 'Дата'
 */
@Component
@Evaluator({
        "#stringCutter{исходная_строка;N}"
})
public class StringCutter extends AbstractEvaluator {

    @Override
    protected String getPlaceHolderName() {
        return "stringCutter";
    }

    @Override
    protected boolean isMultiArgs() {
        return true;
    }

    @Override
    protected String evalExpression(IVariableScope variableScope, String... args) {
        String str = extract("0", args, 0);
        int maxLength = Integer.parseInt(extract("1", args, 1));
        if (str.length() <= maxLength) {
            return str;
        } else {
            return str.substring(0, maxLength);
        }
    }
}
