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

package org.apache.shardingsphere.sql.parser.statement.core.statement.tcl;

import lombok.Getter;
import lombok.Setter;
import org.apache.shardingsphere.sql.parser.statement.core.enums.OperationScope;
import org.apache.shardingsphere.sql.parser.statement.core.enums.TransactionAccessType;
import org.apache.shardingsphere.sql.parser.statement.core.enums.TransactionIsolationLevel;
import org.apache.shardingsphere.sql.parser.statement.core.statement.AbstractSQLStatement;

import java.util.Optional;

/**
 * Set transaction statement.
 */
@Getter
@Setter
public class SetTransactionStatement extends AbstractSQLStatement implements TCLStatement {
    
    private TransactionIsolationLevel isolationLevel;
    
    private OperationScope scope;
    
    private TransactionAccessType accessMode;
    
    /**
     * Get isolation level.
     *
     * @return isolation level
     */
    public Optional<TransactionIsolationLevel> getIsolationLevel() {
        return Optional.ofNullable(isolationLevel);
    }
    
    /**
     * Get access mode.
     *
     * @return access mode 
     */
    public Optional<TransactionAccessType> getAccessMode() {
        return Optional.ofNullable(accessMode);
    }
}
