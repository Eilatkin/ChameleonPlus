package com.eilatkin.ch_plus.evaluate;

import ru.ibsqa.chameleon.evaluate.AbstractEvaluator;
import ru.ibsqa.chameleon.evaluate.Evaluator;
import ru.ibsqa.chameleon.storage.IVariableScope;
import org.springframework.stereotype.Component;

/**
 * Делит строку на две части по
 * заданному символу-разделителю.
 * Возвращает первую или вторую часть.
 * Пример использования:
 *      #stringdelimeter{50/4212 записей;1;/}
 *      результат: '50'
 *      #stringdelimeter{50/4212 записей;2;/}
 *      результат: '4212 записей'
 *      #stringdelimeter{4212 записей;1;' '}
 *      результат: '4212'
 *      #stringdelimeter{mydomain.lan\MY-COMPUTER2;2;\\}
 *      результат: 'MY-COMPUTER'
 */
@Component
@Evaluator({
        "#stringdelimeter{исходная_строка;1_или_2;разделитель}",
        "#stringdelimeter{50/4212 записей;1;/}"
})
public class StringDelimeter extends AbstractEvaluator {

    @Override
    protected String getPlaceHolderName() {
        return "stringdelimeter";
    }

    @Override
    protected boolean isMultiArgs() {
        return true;
    }

    @Override
    protected String evalExpression(IVariableScope variableScope, String... args) {
        String str = extract("0", args, 0);
        int part = Integer.parseInt(extract("1", args, 1));
        part--; // т.к. массивы в Java нумеруются с 0
        String del = extract(" ", args, 2);
        return str.split(del, 2)[part];
    }
}
