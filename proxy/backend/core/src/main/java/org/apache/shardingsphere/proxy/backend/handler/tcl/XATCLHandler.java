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

package org.apache.shardingsphere.proxy.backend.handler.tcl;

import lombok.RequiredArgsConstructor;
import org.apache.shardingsphere.infra.binder.context.statement.SQLStatementContext;
import org.apache.shardingsphere.infra.exception.core.ShardingSpherePreconditions;
import org.apache.shardingsphere.infra.hint.HintValueContext;
import org.apache.shardingsphere.infra.session.query.QueryContext;
import org.apache.shardingsphere.proxy.backend.connector.DatabaseConnector;
import org.apache.shardingsphere.proxy.backend.connector.DatabaseConnectorFactory;
import org.apache.shardingsphere.proxy.backend.context.ProxyContext;
import org.apache.shardingsphere.proxy.backend.handler.ProxyBackendHandler;
import org.apache.shardingsphere.proxy.backend.response.data.QueryResponseRow;
import org.apache.shardingsphere.proxy.backend.response.header.ResponseHeader;
import org.apache.shardingsphere.proxy.backend.session.ConnectionSession;
import org.apache.shardingsphere.sql.parser.statement.core.statement.type.tcl.xa.XABeginStatement;
import org.apache.shardingsphere.sql.parser.statement.core.statement.type.tcl.xa.XACommitStatement;
import org.apache.shardingsphere.sql.parser.statement.core.statement.type.tcl.xa.XARecoveryStatement;
import org.apache.shardingsphere.sql.parser.statement.core.statement.type.tcl.xa.XARollbackStatement;
import org.apache.shardingsphere.sql.parser.statement.core.statement.type.tcl.xa.XAStatement;
import org.apache.shardingsphere.transaction.ShardingSphereTransactionManagerEngine;
import org.apache.shardingsphere.transaction.rule.TransactionRule;
import org.apache.shardingsphere.transaction.xa.jta.exception.XATransactionNestedBeginException;

import java.sql.SQLException;
import java.util.Collections;

/**
 * XA TCL handler.
 */
// TODO Currently XA transaction started with `XA START` doesn't support for database with multiple datasource, a flag should be added for this both in init progress and add datasource from DistSQL.
@RequiredArgsConstructor
public final class XATCLHandler implements ProxyBackendHandler {
    
    private final XAStatement xaStatement;
    
    private final ConnectionSession connectionSession;
    
    private final DatabaseConnector backendHandler;
    
    public XATCLHandler(final SQLStatementContext sqlStatementContext, final String sql, final ConnectionSession connectionSession) {
        xaStatement = (XAStatement) sqlStatementContext.getSqlStatement();
        this.connectionSession = connectionSession;
        backendHandler = DatabaseConnectorFactory.getInstance().newInstance(new QueryContext(sqlStatementContext, sql, Collections.emptyList(), new HintValueContext(),
                connectionSession.getConnectionContext(), ProxyContext.getInstance().getContextManager().getMetaDataContexts().getMetaData()), connectionSession.getDatabaseConnectionManager(), false);
    }
    
    @Override
    public boolean next() throws SQLException {
        return xaStatement instanceof XARecoveryStatement && backendHandler.next();
    }
    
    @Override
    public QueryResponseRow getRowData() throws SQLException {
        return xaStatement instanceof XARecoveryStatement ? backendHandler.getRowData() : new QueryResponseRow(Collections.emptyList());
    }
    
    @Override
    public ResponseHeader execute() throws SQLException {
        if (xaStatement instanceof XABeginStatement) {
            return begin();
        }
        if (xaStatement instanceof XACommitStatement || xaStatement instanceof XARollbackStatement) {
            return finish();
        }
        return backendHandler.execute();
    }
    
    /*
     * We have to let session occupy the thread when doing xa transaction. According to https://dev.mysql.com/doc/refman/5.7/en/xa-states.html XA and local transactions are mutually exclusive.
     */
    private ResponseHeader begin() throws SQLException {
        ShardingSpherePreconditions.checkState(!connectionSession.getTransactionStatus().isInTransaction(), XATransactionNestedBeginException::new);
        ResponseHeader result = backendHandler.execute();
        TransactionRule transactionRule = ProxyContext.getInstance().getContextManager().getMetaDataContexts().getMetaData().getGlobalRuleMetaData().getSingleRule(TransactionRule.class);
        ShardingSphereTransactionManagerEngine engine = transactionRule.getResource();
        connectionSession.getConnectionContext().getTransactionContext().beginTransaction(transactionRule.getDefaultType().name(), engine.getTransactionManager(transactionRule.getDefaultType()));
        return result;
    }
    
    private ResponseHeader finish() throws SQLException {
        try {
            return backendHandler.execute();
        } finally {
            connectionSession.getConnectionContext().clearTransactionContext();
            connectionSession.getConnectionContext().clearCursorContext();
        }
    }
}
