/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.test.e2e.engine.type.dml;

import org.apache.shardingsphere.test.e2e.cases.SQLCommandType;
import org.apache.shardingsphere.test.e2e.cases.SQLExecuteType;
import org.apache.shardingsphere.test.e2e.cases.value.SQLValue;
import org.apache.shardingsphere.test.e2e.engine.arg.E2ETestCaseArgumentsProvider;
import org.apache.shardingsphere.test.e2e.engine.arg.E2ETestCaseSettings;
import org.apache.shardingsphere.test.e2e.engine.composer.SingleE2EContainerComposer;
import org.apache.shardingsphere.test.e2e.env.runtime.IntegrationTestEnvironment;
import org.apache.shardingsphere.test.e2e.framework.param.array.E2ETestParameterFactory;
import org.apache.shardingsphere.test.e2e.framework.param.model.AssertionTestParameter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertFalse;

@E2ETestCaseSettings(SQLCommandType.DML)
class AdditionalDMLE2EIT extends BaseDMLE2EIT {
    
    @ParameterizedTest(name = "{0}")
    @EnabledIf("isEnabled")
    @ArgumentsSource(E2ETestCaseArgumentsProvider.class)
    void assertExecuteUpdateWithAutoGeneratedKeys(final AssertionTestParameter testParam) throws SQLException, JAXBException, IOException {
        // TODO make sure test case can not be null
        if (null == testParam.getTestCaseContext()) {
            return;
        }
        if (isPostgreSQLOrOpenGauss(testParam.getDatabaseType().getType())) {
            return;
        }
        SingleE2EContainerComposer containerComposer = new SingleE2EContainerComposer(testParam);
        init(testParam, containerComposer);
        int actualUpdateCount;
        try (Connection connection = containerComposer.getTargetDataSource().getConnection()) {
            actualUpdateCount = SQLExecuteType.Literal == containerComposer.getSqlExecuteType()
                    ? executeUpdateForStatementWithAutoGeneratedKeys(testParam, containerComposer, connection)
                    : executeUpdateForPreparedStatementWithAutoGeneratedKeys(testParam, containerComposer, connection);
        }
        assertDataSet(testParam, containerComposer, actualUpdateCount);
    }
    
    private boolean isPostgreSQLOrOpenGauss(final String databaseType) {
        return "PostgreSQL".equals(databaseType) || "openGauss".equals(databaseType);
    }
    
    private int executeUpdateForStatementWithAutoGeneratedKeys(final AssertionTestParameter testParam,
                                                               final SingleE2EContainerComposer containerComposer, final Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            int result = statement.executeUpdate(String.format(containerComposer.getSQL(), containerComposer.getAssertion().getSQLValues().toArray()), Statement.RETURN_GENERATED_KEYS);
            assertGeneratedKeys(testParam, containerComposer, statement.getGeneratedKeys());
            return result;
        }
    }
    
    private int executeUpdateForPreparedStatementWithAutoGeneratedKeys(final AssertionTestParameter testParam,
                                                                       final SingleE2EContainerComposer containerComposer, final Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(containerComposer.getSQL(), Statement.RETURN_GENERATED_KEYS)) {
            for (SQLValue each : containerComposer.getAssertion().getSQLValues()) {
                preparedStatement.setObject(each.getIndex(), each.getValue());
            }
            int result = preparedStatement.executeUpdate();
            assertGeneratedKeys(testParam, containerComposer, preparedStatement.getGeneratedKeys());
            return result;
        }
    }
    
    // TODO
    @Disabled("support execute update with column indexes in #23626")
    @ParameterizedTest(name = "{0}")
    @EnabledIf("isEnabled")
    @ArgumentsSource(E2ETestCaseArgumentsProvider.class)
    void assertExecuteUpdateWithColumnIndexes(final AssertionTestParameter testParam) throws SQLException, JAXBException, IOException {
        if (isPostgreSQLOrOpenGauss(testParam.getDatabaseType().getType())) {
            return;
        }
        SingleE2EContainerComposer containerComposer = new SingleE2EContainerComposer(testParam);
        init(testParam, containerComposer);
        int actualUpdateCount;
        try (Connection connection = containerComposer.getTargetDataSource().getConnection()) {
            actualUpdateCount = SQLExecuteType.Literal == containerComposer.getSqlExecuteType()
                    ? executeUpdateForStatementWithColumnIndexes(containerComposer, connection)
                    : executeUpdateForPreparedStatementWithColumnIndexes(containerComposer, connection);
        }
        assertDataSet(testParam, containerComposer, actualUpdateCount);
    }
    
    private int executeUpdateForStatementWithColumnIndexes(final SingleE2EContainerComposer containerComposer, final Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            return statement.executeUpdate(String.format(containerComposer.getSQL(), containerComposer.getAssertion().getSQLValues().toArray()), new int[]{1});
        }
    }
    
    private int executeUpdateForPreparedStatementWithColumnIndexes(final SingleE2EContainerComposer containerComposer, final Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(containerComposer.getSQL(), new int[]{1})) {
            for (SQLValue each : containerComposer.getAssertion().getSQLValues()) {
                preparedStatement.setObject(each.getIndex(), each.getValue());
            }
            return preparedStatement.executeUpdate();
        }
    }
    
    // TODO
    @Disabled("support execute update with column names in #23626")
    @ParameterizedTest(name = "{0}")
    @EnabledIf("isEnabled")
    @ArgumentsSource(E2ETestCaseArgumentsProvider.class)
    void assertExecuteUpdateWithColumnNames(final AssertionTestParameter testParam) throws SQLException, JAXBException, IOException {
        if (isPostgreSQLOrOpenGauss(testParam.getDatabaseType().getType())) {
            return;
        }
        SingleE2EContainerComposer containerComposer = new SingleE2EContainerComposer(testParam);
        init(testParam, containerComposer);
        int actualUpdateCount;
        try (Connection connection = containerComposer.getTargetDataSource().getConnection()) {
            actualUpdateCount = SQLExecuteType.Literal == containerComposer.getSqlExecuteType()
                    ? executeUpdateForStatementWithColumnNames(containerComposer, connection)
                    : executeUpdateForPreparedStatementWithColumnNames(containerComposer, connection);
        }
        assertDataSet(testParam, containerComposer, actualUpdateCount);
    }
    
    private int executeUpdateForStatementWithColumnNames(final SingleE2EContainerComposer containerComposer, final Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            return statement.executeUpdate(String.format(containerComposer.getSQL(), containerComposer.getAssertion().getSQLValues().toArray()));
        }
    }
    
    private int executeUpdateForPreparedStatementWithColumnNames(final SingleE2EContainerComposer containerComposer, final Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(containerComposer.getSQL(), new String[]{"TODO"})) {
            for (SQLValue each : containerComposer.getAssertion().getSQLValues()) {
                preparedStatement.setObject(each.getIndex(), each.getValue());
            }
            return preparedStatement.executeUpdate();
        }
    }
    
    @ParameterizedTest(name = "{0}")
    @EnabledIf("isEnabled")
    @ArgumentsSource(E2ETestCaseArgumentsProvider.class)
    void assertExecuteWithoutAutoGeneratedKeys(final AssertionTestParameter testParam) throws SQLException, JAXBException, IOException {
        // TODO make sure test case can not be null
        if (null == testParam.getTestCaseContext()) {
            return;
        }
        if (isPostgreSQLOrOpenGauss(testParam.getDatabaseType().getType())) {
            return;
        }
        SingleE2EContainerComposer containerComposer = new SingleE2EContainerComposer(testParam);
        init(testParam, containerComposer);
        int actualUpdateCount;
        try (Connection connection = containerComposer.getTargetDataSource().getConnection()) {
            actualUpdateCount = SQLExecuteType.Literal == containerComposer.getSqlExecuteType()
                    ? executeForStatementWithoutAutoGeneratedKeys(containerComposer, connection)
                    : executeForPreparedStatementWithoutAutoGeneratedKeys(containerComposer, connection);
        }
        assertDataSet(testParam, containerComposer, actualUpdateCount);
    }
    
    private int executeForStatementWithoutAutoGeneratedKeys(final SingleE2EContainerComposer containerComposer, final Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            assertFalse(statement.execute(String.format(containerComposer.getSQL(), containerComposer.getAssertion().getSQLValues().toArray()), Statement.NO_GENERATED_KEYS), "Not a DML statement.");
            return statement.getUpdateCount();
        }
    }
    
    private int executeForPreparedStatementWithoutAutoGeneratedKeys(final SingleE2EContainerComposer containerComposer, final Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(containerComposer.getSQL(), Statement.NO_GENERATED_KEYS)) {
            for (SQLValue each : containerComposer.getAssertion().getSQLValues()) {
                preparedStatement.setObject(each.getIndex(), each.getValue());
            }
            assertFalse(preparedStatement.execute(), "Not a DML statement.");
            return preparedStatement.getUpdateCount();
        }
    }
    
    @ParameterizedTest(name = "{0}")
    @EnabledIf("isEnabled")
    @ArgumentsSource(E2ETestCaseArgumentsProvider.class)
    void assertExecuteWithAutoGeneratedKeys(final AssertionTestParameter testParam) throws SQLException, JAXBException, IOException {
        // TODO make sure test case can not be null
        if (null == testParam.getTestCaseContext()) {
            return;
        }
        if (isPostgreSQLOrOpenGauss(testParam.getDatabaseType().getType())) {
            return;
        }
        SingleE2EContainerComposer containerComposer = new SingleE2EContainerComposer(testParam);
        init(testParam, containerComposer);
        int actualUpdateCount;
        try (Connection connection = containerComposer.getTargetDataSource().getConnection()) {
            actualUpdateCount = SQLExecuteType.Literal == containerComposer.getSqlExecuteType()
                    ? executeForStatementWithAutoGeneratedKeys(testParam, containerComposer, connection)
                    : executeForPreparedStatementWithAutoGeneratedKeys(testParam, containerComposer, connection);
        }
        assertDataSet(testParam, containerComposer, actualUpdateCount);
    }
    
    private int executeForStatementWithAutoGeneratedKeys(final AssertionTestParameter testParam,
                                                         final SingleE2EContainerComposer containerComposer, final Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            assertFalse(statement.execute(
                    String.format(containerComposer.getSQL(), containerComposer.getAssertion().getSQLValues().toArray()), Statement.RETURN_GENERATED_KEYS), "Not a DML statement.");
            assertGeneratedKeys(testParam, containerComposer, statement.getGeneratedKeys());
            return statement.getUpdateCount();
        }
    }
    
    private int executeForPreparedStatementWithAutoGeneratedKeys(final AssertionTestParameter testParam,
                                                                 final SingleE2EContainerComposer containerComposer, final Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(containerComposer.getSQL(), Statement.RETURN_GENERATED_KEYS)) {
            for (SQLValue each : containerComposer.getAssertion().getSQLValues()) {
                preparedStatement.setObject(each.getIndex(), each.getValue());
            }
            assertFalse(preparedStatement.execute(), "Not a DML statement.");
            assertGeneratedKeys(testParam, containerComposer, preparedStatement.getGeneratedKeys());
            return preparedStatement.getUpdateCount();
        }
    }
    
    // TODO
    @Disabled("support execute with column indexes in #23626")
    @ParameterizedTest(name = "{0}")
    @EnabledIf("isEnabled")
    @ArgumentsSource(E2ETestCaseArgumentsProvider.class)
    void assertExecuteWithColumnIndexes(final AssertionTestParameter testParam) throws SQLException, JAXBException, IOException {
        if (isPostgreSQLOrOpenGauss(testParam.getDatabaseType().getType())) {
            return;
        }
        SingleE2EContainerComposer containerComposer = new SingleE2EContainerComposer(testParam);
        init(testParam, containerComposer);
        int actualUpdateCount;
        try (Connection connection = containerComposer.getTargetDataSource().getConnection()) {
            actualUpdateCount = SQLExecuteType.Literal == containerComposer.getSqlExecuteType()
                    ? executeForStatementWithColumnIndexes(containerComposer, connection)
                    : executeForPreparedStatementWithColumnIndexes(containerComposer, connection);
        }
        assertDataSet(testParam, containerComposer, actualUpdateCount);
    }
    
    private int executeForStatementWithColumnIndexes(final SingleE2EContainerComposer containerComposer, final Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            assertFalse(statement.execute(String.format(containerComposer.getSQL(), containerComposer.getAssertion().getSQLValues().toArray()), new int[]{1}), "Not a DML statement.");
            return statement.getUpdateCount();
        }
    }
    
    private int executeForPreparedStatementWithColumnIndexes(final SingleE2EContainerComposer containerComposer, final Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(containerComposer.getSQL(), new int[]{1})) {
            for (SQLValue each : containerComposer.getAssertion().getSQLValues()) {
                preparedStatement.setObject(each.getIndex(), each.getValue());
            }
            assertFalse(preparedStatement.execute(), "Not a DML statement.");
            return preparedStatement.getUpdateCount();
        }
    }
    
    // TODO
    @Disabled("support execute with column names in #23626")
    @ParameterizedTest(name = "{0}")
    @EnabledIf("isEnabled")
    @ArgumentsSource(E2ETestCaseArgumentsProvider.class)
    void assertExecuteWithColumnNames(final AssertionTestParameter testParam) throws SQLException, JAXBException, IOException {
        if (isPostgreSQLOrOpenGauss(testParam.getDatabaseType().getType())) {
            return;
        }
        SingleE2EContainerComposer containerComposer = new SingleE2EContainerComposer(testParam);
        init(testParam, containerComposer);
        int actualUpdateCount;
        try (Connection connection = containerComposer.getTargetDataSource().getConnection()) {
            actualUpdateCount = SQLExecuteType.Literal == containerComposer.getSqlExecuteType()
                    ? executeForStatementWithColumnNames(containerComposer, connection)
                    : executeForPreparedStatementWithColumnNames(containerComposer, connection);
        }
        assertDataSet(testParam, containerComposer, actualUpdateCount);
    }
    
    private int executeForStatementWithColumnNames(final SingleE2EContainerComposer containerComposer, final Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            assertFalse(statement.execute(String.format(containerComposer.getSQL(), containerComposer.getAssertion().getSQLValues().toArray()), new String[]{"TODO"}), "Not a DML statement.");
            return statement.getUpdateCount();
        }
    }
    
    private int executeForPreparedStatementWithColumnNames(final SingleE2EContainerComposer containerComposer, final Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(containerComposer.getSQL(), new String[]{"TODO"})) {
            for (SQLValue each : containerComposer.getAssertion().getSQLValues()) {
                preparedStatement.setObject(each.getIndex(), each.getValue());
            }
            assertFalse(preparedStatement.execute(), "Not a DML statement.");
            return preparedStatement.getUpdateCount();
        }
    }
    
    private static boolean isEnabled() {
        return E2ETestParameterFactory.containsTestParameter() && IntegrationTestEnvironment.getInstance().isRunAdditionalTestCases();
    }
}
