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

package org.apache.shardingsphere.proxy.backend.connector;

import org.apache.shardingsphere.infra.binder.context.statement.SQLStatementContext;
import org.apache.shardingsphere.infra.config.props.ConfigurationProperties;
import org.apache.shardingsphere.infra.database.core.type.DatabaseType;
import org.apache.shardingsphere.infra.hint.HintValueContext;
import org.apache.shardingsphere.infra.metadata.ShardingSphereMetaData;
import org.apache.shardingsphere.infra.metadata.database.ShardingSphereDatabase;
import org.apache.shardingsphere.infra.metadata.database.resource.ResourceMetaData;
import org.apache.shardingsphere.infra.metadata.database.rule.RuleMetaData;
import org.apache.shardingsphere.infra.metadata.statistics.ShardingSphereStatistics;
import org.apache.shardingsphere.infra.metadata.statistics.builder.ShardingSphereStatisticsFactory;
import org.apache.shardingsphere.infra.session.connection.ConnectionContext;
import org.apache.shardingsphere.infra.session.query.QueryContext;
import org.apache.shardingsphere.infra.spi.type.typed.TypedSPILoader;
import org.apache.shardingsphere.mode.manager.ContextManager;
import org.apache.shardingsphere.mode.metadata.MetaDataContexts;
import org.apache.shardingsphere.proxy.backend.context.ProxyContext;
import org.apache.shardingsphere.test.mock.AutoMockExtension;
import org.apache.shardingsphere.test.mock.StaticMockSettings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collections;
import java.util.Optional;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(AutoMockExtension.class)
@StaticMockSettings(ProxyContext.class)
class DatabaseConnectorFactoryTest {
    
    private final DatabaseType databaseType = TypedSPILoader.getService(DatabaseType.class, "FIXTURE");
    
    @Test
    void assertNewDatabaseConnectorWithoutParameter() {
        ProxyDatabaseConnectionManager databaseConnectionManager = mock(ProxyDatabaseConnectionManager.class, RETURNS_DEEP_STUBS);
        when(databaseConnectionManager.getConnectionSession().getUsedDatabaseName()).thenReturn("foo_db");
        SQLStatementContext sqlStatementContext = mock(SQLStatementContext.class, RETURNS_DEEP_STUBS);
        when(sqlStatementContext.getTablesContext().getDatabaseNames()).thenReturn(Collections.emptyList());
        when(sqlStatementContext.getSqlStatement().getDatabaseType()).thenReturn(databaseType);
        ShardingSphereDatabase database = mockDatabase();
        ShardingSphereMetaData metaData = mock(ShardingSphereMetaData.class);
        when(metaData.containsDatabase("foo_db")).thenReturn(true);
        when(metaData.getDatabase("foo_db")).thenReturn(database);
        QueryContext queryContext = new QueryContext(sqlStatementContext, "schemaName", Collections.emptyList(), new HintValueContext(), mockConnectionContext(), metaData);
        ContextManager contextManager = mockContextManager(database);
        when(ProxyContext.getInstance().getContextManager()).thenReturn(contextManager);
        DatabaseConnector engine = DatabaseConnectorFactory.getInstance().newInstance(queryContext, databaseConnectionManager, false);
        assertThat(engine, instanceOf(DatabaseConnector.class));
    }
    
    private ConnectionContext mockConnectionContext() {
        ConnectionContext result = mock(ConnectionContext.class);
        when(result.getCurrentDatabaseName()).thenReturn(Optional.of("foo_db"));
        return result;
    }
    
    @Test
    void assertNewDatabaseConnectorWithParameters() {
        ProxyDatabaseConnectionManager databaseConnectionManager = mock(ProxyDatabaseConnectionManager.class, RETURNS_DEEP_STUBS);
        when(databaseConnectionManager.getConnectionSession().getUsedDatabaseName()).thenReturn("foo_db");
        SQLStatementContext sqlStatementContext = mock(SQLStatementContext.class, RETURNS_DEEP_STUBS);
        when(sqlStatementContext.getTablesContext().getDatabaseNames()).thenReturn(Collections.emptyList());
        when(sqlStatementContext.getSqlStatement().getDatabaseType()).thenReturn(databaseType);
        ShardingSphereDatabase database = mockDatabase();
        ShardingSphereMetaData metaData = mock(ShardingSphereMetaData.class);
        when(metaData.containsDatabase("foo_db")).thenReturn(true);
        when(metaData.getDatabase("foo_db")).thenReturn(database);
        ContextManager contextManager = mockContextManager(database);
        when(ProxyContext.getInstance().getContextManager()).thenReturn(contextManager);
        assertThat(DatabaseConnectorFactory.getInstance().newInstance(
                new QueryContext(sqlStatementContext, "schemaName", Collections.emptyList(), new HintValueContext(), mockConnectionContext(), metaData), databaseConnectionManager, false),
                instanceOf(DatabaseConnector.class));
    }
    
    private ContextManager mockContextManager(final ShardingSphereDatabase database) {
        ShardingSphereMetaData metaData = new ShardingSphereMetaData(
                Collections.singleton(database), mock(ResourceMetaData.class), mock(RuleMetaData.class), new ConfigurationProperties(new Properties()));
        MetaDataContexts metaDataContexts = new MetaDataContexts(metaData, ShardingSphereStatisticsFactory.create(metaData, new ShardingSphereStatistics()));
        ContextManager result = mock(ContextManager.class, RETURNS_DEEP_STUBS);
        when(result.getMetaDataContexts()).thenReturn(metaDataContexts);
        when(result.getDatabase("foo_db")).thenReturn(database);
        return result;
    }
    
    private ShardingSphereDatabase mockDatabase() {
        ShardingSphereDatabase result = mock(ShardingSphereDatabase.class, RETURNS_DEEP_STUBS);
        when(result.getName()).thenReturn("foo_db");
        when(result.containsDataSource()).thenReturn(true);
        when(result.isComplete()).thenReturn(true);
        when(result.getProtocolType()).thenReturn(databaseType);
        return result;
    }
}
