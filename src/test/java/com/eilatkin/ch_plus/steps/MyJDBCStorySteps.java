package com.eilatkin.ch_plus.steps;

import io.cucumber.java.ru.Дано;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Тогда;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ibsqa.chameleon.converters.FieldOperatorValueTable;
import ru.ibsqa.chameleon.steps.AbstractSteps;
import ru.ibsqa.chameleon.steps.StepDescription;
import ru.ibsqa.chameleon.steps.roles.Operator;
import ru.ibsqa.chameleon.steps.roles.Read;
import ru.ibsqa.chameleon.steps.roles.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Шаги с подключением к СУБД уровня BDD.
 */
public class MyJDBCStorySteps extends AbstractSteps {

    @Autowired
    private MyJDBCSteps myJDBCSteps; // здесь подключаем бин с тестовыми шагами

    /**
     * Преобразовать List<FieldValueTable> со столбцами field и value в Map
     *
     * @param conditions
     * @return
     */
    private List<MyJDBCSteps.FindCondition> parseConditions(List<FieldOperatorValueTable> conditions) {
        List<MyJDBCSteps.FindCondition> list = new ArrayList<>();
        for (FieldOperatorValueTable c : conditions) {
            list.add(MyJDBCSteps.FindCondition.builder()
                    .fieldName(c.getField())
                    .operator(c.getOperator())
                    .value(Optional.ofNullable(c.getValue()).orElse(StringUtils.EMPTY))
                    .build());
        }
        return list;
    }

    @StepDescription(action = "Прочее->Выполнить SELECT в СУБД")
    @Когда("^выполнить возвращающий запрос в СУБД$")
    public void execSelect(
            @Value String sqlCommand
    ) {
        flow(() ->
                myJDBCSteps.execSelect(sqlCommand)
        );
    }

    @StepDescription(action = "Прочее->Выполнить транзакцию в СУБД")
    @Дано("^выполнить изменяющий запрос в СУБД$")
    public void execUpdate(
            @Value String sqlCommand
    ) {
        flow(() ->
                myJDBCSteps.execUpdate(sqlCommand)
        );
    }

    @StepDescription(action = "Прочее->Результат запроса в СУБД удовлетворяет условиям"
            , parameters = {"sqlCommand - SQL-скрипт", "conditions - условия"})
    @Тогда("^результат запроса в СУБД удовлетворяет условиям:$")
    public void execSQLandValidateResult(
            @Read("field") @Operator("operator") @Value("value") List<FieldOperatorValueTable> conditions
    ) {
        flow(() ->
                myJDBCSteps.execSQLandValidateResult(parseConditions(conditions))
        );
    }
}
