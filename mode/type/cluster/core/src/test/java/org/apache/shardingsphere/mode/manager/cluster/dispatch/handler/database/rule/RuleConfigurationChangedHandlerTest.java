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

package org.apache.shardingsphere.mode.manager.cluster.dispatch.handler.database.rule;

import org.apache.shardingsphere.mode.event.DataChangedEvent;
import org.apache.shardingsphere.mode.event.DataChangedEvent.Type;
import org.apache.shardingsphere.mode.manager.ContextManager;
import org.apache.shardingsphere.mode.node.path.type.metadata.rule.DatabaseRuleNodePath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RuleConfigurationChangedHandlerTest {
    
    private RuleConfigurationChangedHandler handler;
    
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ContextManager contextManager;
    
    @BeforeEach
    void setUp() {
        handler = new RuleConfigurationChangedHandler(contextManager);
    }
    
    @Test
    void assertHandleWithInvalidPath() throws SQLException {
        handler.handle("foo_db", new DataChangedEvent("/metadata/foo_db/rules/foo_rule", "foo", Type.ADDED));
        verify(contextManager.getMetaDataContextManager().getDatabaseRuleItemManager(), times(0)).alter(any(), eq(0));
        verify(contextManager.getMetaDataContextManager().getDatabaseRuleItemManager(), times(0)).drop(any());
    }
    
    @Test
    void assertHandleWithEmptyValue() throws SQLException {
        handler.handle("foo_db", new DataChangedEvent("/metadata/foo_db/rules/fixture/versions/0", "", Type.ADDED));
        verify(contextManager.getMetaDataContextManager().getDatabaseRuleItemManager(), times(0)).alter(any(), eq(0));
        verify(contextManager.getMetaDataContextManager().getDatabaseRuleItemManager(), times(0)).drop(any());
    }
    
    @Test
    void assertHandleWithPathNotFound() throws SQLException {
        handler.handle("foo_db", new DataChangedEvent("/metadata/foo_db/rules/fixture/versions/0", "foo", Type.ADDED));
        verify(contextManager.getMetaDataContextManager().getDatabaseRuleItemManager(), times(0)).alter(any(), eq(0));
        verify(contextManager.getMetaDataContextManager().getDatabaseRuleItemManager(), times(0)).drop(any());
    }
    
    @Test
    void assertHandleWithIgnoreType() throws SQLException {
        handler.handle("foo_db", new DataChangedEvent("/metadata/foo_db/rules/fixture/named/foo_rule_item/active_version", "foo", Type.IGNORED));
        verify(contextManager.getMetaDataContextManager().getDatabaseRuleItemManager(), times(0)).alter(any(), eq(0));
        verify(contextManager.getMetaDataContextManager().getDatabaseRuleItemManager(), times(0)).drop(any());
    }
    
    @Test
    void assertHandleWithNamedRuleItemAdded() throws SQLException {
        handler.handle("foo_db", new DataChangedEvent("/metadata/foo_db/rules/fixture/named/foo_rule_item/active_version", "0", Type.ADDED));
        verify(contextManager.getMetaDataContextManager().getDatabaseRuleItemManager()).alter(any(DatabaseRuleNodePath.class), eq(0));
    }
    
    @Test
    void assertHandleWithNamedRuleItemAltered() throws SQLException {
        handler.handle("foo_db", new DataChangedEvent("/metadata/foo_db/rules/fixture/named/foo_rule_item/active_version", "0", Type.UPDATED));
        verify(contextManager.getMetaDataContextManager().getDatabaseRuleItemManager()).alter(any(DatabaseRuleNodePath.class), eq(0));
    }
    
    @Test
    void assertHandleWithNamedRuleItemDropped() throws SQLException {
        handler.handle("foo_db", new DataChangedEvent("/metadata/foo_db/rules/fixture/named/foo_rule_item", "foo", Type.DELETED));
        verify(contextManager.getMetaDataContextManager().getDatabaseRuleItemManager()).drop(any(DatabaseRuleNodePath.class));
    }
    
    @Test
    void assertHandleWithUniqueRuleItemAdded() throws SQLException {
        handler.handle("foo_db", new DataChangedEvent("/metadata/foo_db/rules/fixture/unique/active_version", "0", Type.ADDED));
        verify(contextManager.getMetaDataContextManager().getDatabaseRuleItemManager()).alter(any(DatabaseRuleNodePath.class), eq(0));
    }
    
    @Test
    void assertHandleWithUniqueRuleItemAltered() throws SQLException {
        handler.handle("foo_db", new DataChangedEvent("/metadata/foo_db/rules/fixture/unique/active_version", "0", Type.UPDATED));
        verify(contextManager.getMetaDataContextManager().getDatabaseRuleItemManager()).alter(any(DatabaseRuleNodePath.class), eq(0));
    }
    
    @Test
    void assertHandleWithUniqueRuleItemDropped() throws SQLException {
        handler.handle("foo_db", new DataChangedEvent("/metadata/foo_db/rules/fixture/unique/active_version", "foo", Type.DELETED));
        verify(contextManager.getMetaDataContextManager().getDatabaseRuleItemManager()).drop(any(DatabaseRuleNodePath.class));
    }
}
