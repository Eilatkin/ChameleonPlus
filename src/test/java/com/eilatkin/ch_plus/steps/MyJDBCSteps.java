package com.eilatkin.ch_plus.steps;

import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ibsqa.chameleon.compare.ICompareManager;
import ru.ibsqa.chameleon.steps.AbstractSteps;
import ru.ibsqa.chameleon.steps.TestStep;
import ru.ibsqa.chameleon.utils.spring.SpringUtils;

import java.sql.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Шаги с подключением к СУБД
 */
@Component
@Slf4j
public class MyJDBCSteps extends AbstractSteps {

    private static Connection connection;
    private static ResultSet results;
    private static Statement statement;
    private final String databaseHost = System.getProperty("databaseHost", "127.0.0.1");
    private final String databaseName = System.getProperty("databaseName", "mydb");
    private final String databaseUser = System.getProperty("databaseUser", "postgres");
    private final String databasePassword = System.getProperty("databasePassword", "postgres");

    @Autowired
    private ICompareManager compareManager;

    @Data
    @Builder
    public static class FindCondition {
        private String fieldName;
        private String operator;
        private String value;

        public String getOperator() {
            return Optional.ofNullable(operator)
                    .orElseGet(() -> SpringUtils.getBean(ICompareManager.class).defaultOperator());
        }
    }

    public void initiateConnection() throws SQLException {
        connection = DriverManager.getConnection(
                "jdbc:postgresql://"+databaseHost+":5432/"+databaseName,
                databaseUser, databasePassword);
    }

    public void executeQuery(String query) throws SQLException {
        initiateConnection();
        statement = connection.createStatement();
        results = statement.executeQuery(query);
    }

    public void executeTransactional(String query) throws SQLException {
        initiateConnection();
        statement = connection.createStatement();
        statement.executeUpdate(query);
    }

    public void closeConnection() throws SQLException {
        if (connection!=null) {
            results.close();
            statement.close();
            connection.close();
        }
    }

    @SneakyThrows
    @TestStep("выполнить SQL запрос")
    public void execSelect(String sql) {
        initiateConnection();
        executeQuery(sql);
    }

    @SneakyThrows
    @TestStep("выполнить SQL транзакцию")
    public void execUpdate(String sql) {
        initiateConnection();
        executeTransactional(sql);
    }

    @SneakyThrows
    @TestStep("результат запроса в СУБД удовлетворяет условиям:")
    public void execSQLandValidateResult(List<FindCondition> conditions) {
        assertNotNull(results,"Выполните SELECT-запрос, прежде чем сверять результат!");
        int i = 0;
        boolean found = false;
        while (results.next()){
            boolean match = true;
            i++;
            final StringBuilder logText = new StringBuilder("Получен элемент результата SELECT-запроса:\n");

            for (FindCondition row : conditions) {
                final String expected = evalVariable(row.getValue()).replace("\\n", "\n");
                String actual = results.getString(row.getFieldName());
                logText.append(String.format("Ряд %s: %s\n", i, actual));
                        match = compareManager.checkValue(row.getOperator(), actual, expected);
                if (!match) {
                    logText.append(String.format("Элемент не подошел по параметру: \"%s\". Ожидалось: \"%s\" %s \"%s\"",
                            row.getFieldName(), row.getFieldName(), row.getOperator(), expected));
                    log.debug(logText.toString());
                    break;
                }
            }
            if (match) {
                found = true;
                log.debug(String.format("%sЭлемент результата SELECT-запроса подошел по всем заданным параметрам.", logText));
                break;
            }
        }
    assertTrue(found, "Элементы результата SELECT-запроса не подошли заданным параметрам!");
    }

}
