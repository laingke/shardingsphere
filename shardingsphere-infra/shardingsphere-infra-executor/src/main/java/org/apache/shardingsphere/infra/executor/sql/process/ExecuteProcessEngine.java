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

package org.apache.shardingsphere.infra.executor.sql.process;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.shardingsphere.infra.binder.statement.SQLStatementContext;
import org.apache.shardingsphere.infra.executor.kernel.model.ExecutionGroupContext;
import org.apache.shardingsphere.infra.executor.sql.execute.engine.SQLExecutionUnit;
import org.apache.shardingsphere.infra.executor.sql.process.model.ExecuteProcessStatus;
import org.apache.shardingsphere.infra.executor.sql.process.spi.ExecuteProcessReporter;
import org.apache.shardingsphere.infra.spi.ShardingSphereServiceLoader;

import java.util.Collection;

/**
 * Execute process engine.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExecuteProcessEngine {
    
    private static final Collection<ExecuteProcessReporter> HANDLERS;
    
    static {
        ShardingSphereServiceLoader.register(ExecuteProcessReporter.class);
        HANDLERS = ShardingSphereServiceLoader.newServiceInstances(ExecuteProcessReporter.class);
    }
    
    /**
     * Initialize.
     *
     * @param context context
     * @param executionGroupContext execution group context
     */
    public static void initialize(final SQLStatementContext<?> context, final ExecutionGroupContext<? extends SQLExecutionUnit> executionGroupContext) {
        if (!HANDLERS.isEmpty() && ExecuteProcessStrategyEvaluator.evaluate(context, executionGroupContext)) {
            HANDLERS.iterator().next().report(context, executionGroupContext, ExecuteProcessStatus.DOING);
        }
    }
    
    /**
     * Complete.
     *
     * @param executionID execution ID
     * @param executionUnit execution unit
     */
    public static void complete(final String executionID, final SQLExecutionUnit executionUnit) {
        if (!HANDLERS.isEmpty()) {
            HANDLERS.iterator().next().report(executionID, executionUnit, ExecuteProcessStatus.DONE);
        }
    }
}
