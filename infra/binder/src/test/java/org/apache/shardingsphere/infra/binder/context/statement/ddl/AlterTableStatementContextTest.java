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

package org.apache.shardingsphere.infra.binder.context.statement.ddl;

import org.apache.shardingsphere.infra.binder.context.statement.CommonSQLStatementContext;
import org.apache.shardingsphere.sql.parser.statement.core.segment.ddl.column.ColumnDefinitionSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.ddl.column.alter.AddColumnDefinitionSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.ddl.column.alter.ModifyColumnDefinitionSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.ddl.constraint.ConstraintDefinitionSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.ddl.constraint.ConstraintSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.ddl.constraint.alter.AddConstraintDefinitionSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.ddl.constraint.alter.DropConstraintDefinitionSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.ddl.constraint.alter.ValidateConstraintDefinitionSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.ddl.index.DropIndexDefinitionSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.ddl.index.IndexNameSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.ddl.index.IndexSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.generic.bound.TableSegmentBoundInfo;
import org.apache.shardingsphere.sql.parser.statement.core.segment.generic.table.SimpleTableSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.generic.table.TableNameSegment;
import org.apache.shardingsphere.sql.parser.statement.core.statement.ddl.AlterTableStatement;
import org.apache.shardingsphere.sql.parser.statement.core.value.identifier.IdentifierValue;
import org.apache.shardingsphere.sql.parser.statement.mysql.ddl.MySQLAlterTableStatement;
import org.apache.shardingsphere.sql.parser.statement.oracle.ddl.OracleAlterTableStatement;
import org.apache.shardingsphere.sql.parser.statement.postgresql.ddl.PostgreSQLAlterTableStatement;
import org.apache.shardingsphere.sql.parser.statement.sql92.ddl.SQL92AlterTableStatement;
import org.apache.shardingsphere.sql.parser.statement.sqlserver.ddl.SQLServerAlterTableStatement;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AlterTableStatementContextTest {
    
    @Test
    void assertMySQLNewInstance() {
        assertNewInstance(new MySQLAlterTableStatement());
    }
    
    @Test
    void assertPostgreSQLNewInstance() {
        assertNewInstance(new PostgreSQLAlterTableStatement());
    }
    
    @Test
    void assertOracleNewInstance() {
        assertNewInstance(new OracleAlterTableStatement());
    }
    
    @Test
    void assertSQLServerNewInstance() {
        assertNewInstance(new SQLServerAlterTableStatement());
    }
    
    @Test
    void assertSQL92NewInstance() {
        assertNewInstance(new SQL92AlterTableStatement());
    }
    
    private void assertNewInstance(final AlterTableStatement alterTableStatement) {
        TableNameSegment tableNameSegment1 = new TableNameSegment(0, 0, new IdentifierValue("tbl_1"));
        tableNameSegment1.setTableBoundInfo(new TableSegmentBoundInfo(new IdentifierValue("foo_db"), new IdentifierValue("foo_schema")));
        TableNameSegment tableNameSegment2 = new TableNameSegment(0, 0, new IdentifierValue("rename_tbl_1"));
        tableNameSegment2.setTableBoundInfo(new TableSegmentBoundInfo(new IdentifierValue("foo_db"), new IdentifierValue("foo_schema")));
        SimpleTableSegment table = new SimpleTableSegment(tableNameSegment1);
        SimpleTableSegment renameTable = new SimpleTableSegment(tableNameSegment2);
        alterTableStatement.setTable(table);
        alterTableStatement.setRenameTable(renameTable);
        Collection<SimpleTableSegment> referencedTables = Collections.singletonList(table);
        ColumnDefinitionSegment columnDefinition = mock(ColumnDefinitionSegment.class);
        when(columnDefinition.getReferencedTables()).thenReturn(referencedTables);
        AddColumnDefinitionSegment addColumnDefinition = mock(AddColumnDefinitionSegment.class);
        when(addColumnDefinition.getColumnDefinitions()).thenReturn(Collections.singletonList(columnDefinition));
        alterTableStatement.getAddColumnDefinitions().add(addColumnDefinition);
        ModifyColumnDefinitionSegment modifyColumnDefinition = mock(ModifyColumnDefinitionSegment.class);
        when(modifyColumnDefinition.getColumnDefinition()).thenReturn(columnDefinition);
        alterTableStatement.getModifyColumnDefinitions().add(modifyColumnDefinition);
        ConstraintDefinitionSegment constraintDefinition = mock(ConstraintDefinitionSegment.class);
        when(constraintDefinition.getReferencedTable()).thenReturn(Optional.of(table));
        when(constraintDefinition.getIndexName()).thenReturn(Optional.of(new IndexSegment(0, 0, new IndexNameSegment(0, 0, new IdentifierValue("index")))));
        AddConstraintDefinitionSegment addConstraintDefinition = mock(AddConstraintDefinitionSegment.class);
        when(addConstraintDefinition.getConstraintDefinition()).thenReturn(constraintDefinition);
        ConstraintSegment constraint = new ConstraintSegment(0, 0, new IdentifierValue("constraint"));
        when(addConstraintDefinition.getConstraintDefinition().getConstraintName()).thenReturn(Optional.of(constraint));
        alterTableStatement.getAddConstraintDefinitions().add(addConstraintDefinition);
        ValidateConstraintDefinitionSegment validateConstraintDefinition = mock(ValidateConstraintDefinitionSegment.class);
        when(validateConstraintDefinition.getConstraintName()).thenReturn(constraint);
        alterTableStatement.getValidateConstraintDefinitions().add(validateConstraintDefinition);
        DropConstraintDefinitionSegment dropConstraintDefinition = mock(DropConstraintDefinitionSegment.class);
        when(dropConstraintDefinition.getConstraintName()).thenReturn(constraint);
        alterTableStatement.getDropConstraintDefinitions().add(dropConstraintDefinition);
        DropIndexDefinitionSegment dropIndexDefinitionSegment = mock(DropIndexDefinitionSegment.class);
        when(dropIndexDefinitionSegment.getIndexSegment()).thenReturn(new IndexSegment(0, 0, new IndexNameSegment(0, 0, new IdentifierValue("drop_index"))));
        alterTableStatement.getDropIndexDefinitions().add(dropIndexDefinitionSegment);
        AlterTableStatementContext actual = new AlterTableStatementContext(alterTableStatement);
        assertThat(actual, instanceOf(CommonSQLStatementContext.class));
        assertThat(actual.getSqlStatement(), is(alterTableStatement));
        assertThat(actual.getTablesContext().getSimpleTables().stream().map(each -> each.getTableName().getIdentifier().getValue()).collect(Collectors.toList()),
                is(Arrays.asList("tbl_1", "rename_tbl_1", "tbl_1", "tbl_1", "tbl_1")));
        assertThat(actual.getIndexes().stream().map(each -> each.getIndexName().getIdentifier().getValue()).collect(Collectors.toList()), is(Arrays.asList("index", "drop_index")));
        assertThat(actual.getConstraints().stream().map(each -> each.getIdentifier().getValue()).collect(Collectors.toList()),
                is(Arrays.asList("constraint", "constraint", "constraint")));
    }
}
