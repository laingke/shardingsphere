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

package org.apache.shardingsphere.single.distsql.handler.query;

import org.apache.shardingsphere.distsql.handler.engine.DistSQLConnectionContext;
import org.apache.shardingsphere.distsql.handler.engine.query.DistSQLQueryExecuteEngine;
import org.apache.shardingsphere.infra.datanode.DataNode;
import org.apache.shardingsphere.infra.merge.result.impl.local.LocalDataQueryResultRow;
import org.apache.shardingsphere.infra.metadata.database.ShardingSphereDatabase;
import org.apache.shardingsphere.infra.rule.attribute.RuleAttributes;
import org.apache.shardingsphere.infra.rule.attribute.datanode.DataNodeRuleAttribute;
import org.apache.shardingsphere.mode.manager.ContextManager;
import org.apache.shardingsphere.single.distsql.statement.rql.ShowSingleTablesStatement;
import org.apache.shardingsphere.single.rule.SingleRule;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ShowSingleTablesExecutorTest {
    
    @Test
    void assertExecuteQuery() throws SQLException {
        DistSQLQueryExecuteEngine executeEngine = new DistSQLQueryExecuteEngine(mock(ShowSingleTablesStatement.class), "foo_db", mockContextManager(), mock(DistSQLConnectionContext.class));
        executeEngine.executeQuery();
        List<LocalDataQueryResultRow> actual = new ArrayList<>(executeEngine.getRows());
        assertThat(actual.size(), is(2));
        assertThat(actual.get(0).getCell(1), is("t_order"));
        assertThat(actual.get(0).getCell(2), is("ds_1"));
        assertThat(actual.get(1).getCell(1), is("t_order_item"));
        assertThat(actual.get(1).getCell(2), is("ds_2"));
    }
    
    @Test
    void assertExecuteQueryWithLikeLiteral() throws SQLException {
        DistSQLQueryExecuteEngine executeEngine = new DistSQLQueryExecuteEngine(new ShowSingleTablesStatement(null, "%item"), "foo_db", mockContextManager(), mock(DistSQLConnectionContext.class));
        executeEngine.executeQuery();
        List<LocalDataQueryResultRow> actual = new ArrayList<>(executeEngine.getRows());
        assertThat(actual.size(), is(1));
        assertThat(actual.get(0).getCell(1), is("t_order_item"));
        assertThat(actual.get(0).getCell(2), is("ds_2"));
    }
    
    private ContextManager mockContextManager() {
        ContextManager result = mock(ContextManager.class, RETURNS_DEEP_STUBS);
        ShardingSphereDatabase database = mock(ShardingSphereDatabase.class, RETURNS_DEEP_STUBS);
        when(result.getDatabase("foo_db")).thenReturn(database);
        SingleRule rule = mockSingleRule();
        when(database.getRuleMetaData().findSingleRule(SingleRule.class)).thenReturn(Optional.of(rule));
        return result;
    }
    
    private SingleRule mockSingleRule() {
        Map<String, Collection<DataNode>> singleTableDataNodeMap = new HashMap<>(2, 1F);
        singleTableDataNodeMap.put("t_order", Collections.singleton(new DataNode("ds_1", "t_order")));
        singleTableDataNodeMap.put("t_order_item", Collections.singleton(new DataNode("ds_2", "t_order_item")));
        DataNodeRuleAttribute ruleAttribute = mock(DataNodeRuleAttribute.class);
        when(ruleAttribute.getAllDataNodes()).thenReturn(singleTableDataNodeMap);
        SingleRule result = mock(SingleRule.class);
        when(result.getAttributes()).thenReturn(new RuleAttributes(ruleAttribute));
        return result;
    }
}
