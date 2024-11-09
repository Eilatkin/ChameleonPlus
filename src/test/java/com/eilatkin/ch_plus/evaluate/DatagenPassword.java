package com.eilatkin.ch_plus.evaluate;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import ru.ibsqa.chameleon.evaluate.AbstractEvaluator;
import ru.ibsqa.chameleon.evaluate.Evaluator;
import ru.ibsqa.chameleon.storage.IVariableScope;

/**
 * Задает случайный набор символов, типичный например, для пароля пользователя.
 * <p>
 * Заглавные и строчныя буквы. Латинские и кириллицу. Цифры.
 * Допускается наличие следующих символов: ~ ! ? @ # $ % ^ & * _ - + ( ) [ ] { } > < / \ | " ' . , :
 * </p>
 * Пример использования:
 *      #datagenPassword{5}
 *      результат: 'Gi&71'
 */
@Component
@Evaluator({
        "#datagenPassword{длина}"
})
public class DatagenPassword extends AbstractEvaluator {

    @Override
    protected String getPlaceHolderName() {
        return "datagenPassword";
    }

    @Override
    protected boolean isMultiArgs() {
        return true;
    }

    @Override
    protected String evalExpression(IVariableScope variableScope, String... args) {
        int maxLength = Integer.parseInt(extract("0", args, 0));
        String charsSpecial = "~!?@#$%^&*_-+()[]{}></\\|\"'.,:";
        String charsAlphabeticEN = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String charsAlphabeticRU = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
        String charsNum = "1234567890";
        String resultChars = charsSpecial + charsAlphabeticEN + charsAlphabeticRU + charsNum;
        return RandomStringUtils.random(maxLength,resultChars);
    }
}
