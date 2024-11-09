package com.eilatkin.ch_plus.evaluate;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import ru.ibsqa.chameleon.evaluate.AbstractEvaluator;
import ru.ibsqa.chameleon.evaluate.Evaluator;
import ru.ibsqa.chameleon.storage.IVariableScope;

/**
 * <b>Задает валидный случайный пароль заданной длины.</b>
 * <p>
 * Удовлетворяет требованиям:
 * Доступны заглавные и строчные буквы. Латинские и кириллица. Цифры. Символы
 * ~ ! ? @ # $ % ^ & * _ - + ( ) [ ] { } > < / \ | " ' . , : ; = "
 * Не может быть менее 4х символов в силу требований! Рекомендация: не менее 7.
 * <br/>
 * <b>Обязательно</b> должен содержать минимум 1 цифру, 1 заглавную, 1 строчную, 1 спецсимвол.
 * </p>
 * Пример использования:
 *      #datagenPassword{5}
 *      результат: 'Gi&71'
 */
@Component
@Slf4j
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
        int maxLength = Integer.parseInt(extract("4", args, 0));
        if (maxLength<4) {
            log.error("вычислитель datagenPassword требует указания длины не менее 4!");
            maxLength=4;
        }
        StringBuilder allChars = new StringBuilder();
        for (PasswordChars charTypes: PasswordChars.values()) {
            allChars.append(charTypes.getCharacters());
        }
        String resulted = RandomStringUtils.random(maxLength - 4, allChars.toString());

        resulted = PasswordChars.special.insertCharRandomly(resulted);
        resulted = PasswordChars.numeric.insertCharRandomly(resulted);
        if (Math.random() < 0.5) resulted = PasswordChars.capitalAlphabeticEN.insertCharRandomly(resulted);
            else resulted = PasswordChars.capitalAlphabeticRU.insertCharRandomly(resulted);
        if (Math.random() < 0.5) resulted = PasswordChars.alphabeticEN.insertCharRandomly(resulted);
            else resulted = PasswordChars.alphabeticRU.insertCharRandomly(resulted);

        return resulted;
    }
}
