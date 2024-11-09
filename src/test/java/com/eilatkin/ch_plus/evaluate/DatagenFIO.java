package com.eilatkin.ch_plus.evaluate;

import org.springframework.stereotype.Component;
import pro.dagen.person.FakePerson;
import pro.dagen.person.Gender;
import ru.ibsqa.chameleon.evaluate.AbstractEvaluator;
import ru.ibsqa.chameleon.evaluate.Evaluator;
import ru.ibsqa.chameleon.storage.IVariableScope;
import pro.dagen.DataGenerator;
import java.util.Objects;

/**
 * Случайное ФИО пользователя: 1 - мужское, иначе - женское
 * #datagenFIO{Ф;1} - фамилия
 * #datagenFIO{И;1} - мужское имя
 * #datagenFIO{О;1} - мужское отчество
 */
@Component
@Evaluator({
        "#datagenFIO{}"
})
public class DatagenFIO extends AbstractEvaluator {

    @Override
    protected String getPlaceHolderName() {
        return "datagenFIO";
    }

    @Override
    protected boolean isMultiArgs() {
        return true;
    }

    @Override
    protected String evalExpression(IVariableScope variableScope, String... args) {
        String str = extract("0", args, 0);
        String coinFlip = extract("0", args, 1);
        Gender gender;
        if (Objects.equals(coinFlip, "1")) gender = Gender.MALE;
        else gender = Gender.FEMALE;
        FakePerson person = DataGenerator.persons().get(gender);
        if (str.equals("F")) return capitalize(person.getFio().getLastname());
        if (str.equals("I")) return capitalize(person.getFio().getFirstname());
        if (str.equals("O")) return capitalize(person.getFio().getParentName());
        if (str.equals("Ф")) return capitalize(person.getFio().getLastname());
        if (str.equals("И")) return capitalize(person.getFio().getFirstname());
        if (str.equals("О")) return capitalize(person.getFio().getParentName());
        return "Тестеров Тестировщик Тестировщикович";
    }
//    StringUtils.capitalize() - требует библиотеку org.apache.commons.lang3.StringUtils, обойдусь костылём
    public static String capitalize(String str)
    {
        if (str == null || str.length() == 0) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}

