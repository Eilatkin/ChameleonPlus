package com.eilatkin.ch_plus.evaluate;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import ru.ibsqa.chameleon.evaluate.AbstractEvaluator;
import ru.ibsqa.chameleon.evaluate.Evaluator;
import ru.ibsqa.chameleon.storage.IVariableScope;
import static com.eilatkin.ch_plus.evaluate.PasswordChars.*;

import java.util.*;


/**
 * <b>Задает случайный невалидный пароль заданной длины.</b>
 * <p>
 * Который нарушает требования:
 * Доступны заглавные и строчные буквы. Латинские и кириллица. Цифры. Символы
 * ~ ! ? @ # $ % ^ & * _ - + ( ) [ ] { } > < / \ | " ' . , : ; = "
 * Не может быть менее 3х символов!
 * <br/>
 * <b>Обязательно</b> должен содержать причину невалидности: "нет цифр","нет заглавных букв","нет строчных букв", "нет спецсимволов".
 * </p>
 * Пример использования:
 *      #datagenInvalidPassword{8;нет цифр}
 *      результат: 'Gi&asfЁй!'
 */
@Component
@Slf4j
@Evaluator({
        "#datagenInvalidPassword{длина,причина}"
})
public class DatagenInvalidPassword extends AbstractEvaluator {

    @Override
    protected String getPlaceHolderName() {
        return "datagenInvalidPassword";
    }

    @Override
    protected boolean isMultiArgs() {
        return true;
    }

    @Override
    protected String evalExpression(IVariableScope variableScope, String... args) {
        int maxLength = Integer.parseInt(extract("3", args, 0));
        if (maxLength<3) {
            maxLength = 3;
            log.error("Генерируемый пароль не может быть менее 3х символов!");
        }
        String reason = extract("нет цифр", args, 1);

        Map<String, PasswordChars[]> validReasons = new HashMap<>();
        validReasons.put("нет цифр", new PasswordChars[]{numeric});
        validReasons.put("нет спецсимволов",new PasswordChars[]{special});
        validReasons.put("нет заглавных букв", new PasswordChars[]{capitalAlphabeticEN, capitalAlphabeticRU});
        validReasons.put("нет строчных букв", new PasswordChars[]{alphabeticEN, alphabeticRU});

        if (!validReasons.containsKey(reason)) log.error("Неверная причина невалидности пароля в вычислителе!");
        List<PasswordChars> selectedChars = Arrays.asList(validReasons.get(reason));
        StringBuilder allCharsButReason = new StringBuilder();
        for (PasswordChars charTypes: PasswordChars.values()) {
           if (!selectedChars.contains(charTypes)) {
               allCharsButReason.append(charTypes.getCharacters());
           }
        }

        String resulted = RandomStringUtils.random(maxLength - 3, allCharsButReason.toString());
        if (!selectedChars.contains(special)) resulted = special.insertCharRandomly(resulted);
        if (!selectedChars.contains(numeric)) resulted = PasswordChars.numeric.insertCharRandomly(resulted);
        if (!selectedChars.contains(capitalAlphabeticEN)) {
            if (Math.random() < 0.5) resulted = capitalAlphabeticEN.insertCharRandomly(resulted);
            else resulted = PasswordChars.capitalAlphabeticRU.insertCharRandomly(resulted);
        }
        if (!selectedChars.contains(alphabeticEN)) {
            if (Math.random() < 0.5) resulted = PasswordChars.alphabeticEN.insertCharRandomly(resulted);
            else resulted = PasswordChars.alphabeticRU.insertCharRandomly(resulted);
        }

        return resulted;
    }
}
