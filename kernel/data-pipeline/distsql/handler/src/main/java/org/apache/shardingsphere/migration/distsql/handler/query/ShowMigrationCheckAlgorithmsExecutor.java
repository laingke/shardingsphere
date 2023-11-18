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

package org.apache.shardingsphere.migration.distsql.handler.query;

import org.apache.shardingsphere.data.pipeline.core.job.service.InventoryIncrementalJobManager;
import org.apache.shardingsphere.data.pipeline.core.job.service.PipelineJobAPI;
import org.apache.shardingsphere.distsql.handler.ral.query.QueryableRALExecutor;
import org.apache.shardingsphere.infra.database.core.type.DatabaseType;
import org.apache.shardingsphere.infra.merge.result.impl.local.LocalDataQueryResultRow;
import org.apache.shardingsphere.infra.spi.type.typed.TypedSPILoader;
import org.apache.shardingsphere.migration.distsql.statement.ShowMigrationCheckAlgorithmsStatement;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Show migration check algorithms' executor.
 */
public final class ShowMigrationCheckAlgorithmsExecutor implements QueryableRALExecutor<ShowMigrationCheckAlgorithmsStatement> {
    
    @Override
    public Collection<LocalDataQueryResultRow> getRows(final ShowMigrationCheckAlgorithmsStatement sqlStatement) {
        InventoryIncrementalJobManager inventoryIncrementalJobManager = new InventoryIncrementalJobManager(TypedSPILoader.getService(PipelineJobAPI.class, "MIGRATION"));
        return inventoryIncrementalJobManager.listDataConsistencyCheckAlgorithms().stream().map(
                each -> new LocalDataQueryResultRow(each.getType(), each.getTypeAliases(),
                        each.getSupportedDatabaseTypes().stream().map(DatabaseType::getType).collect(Collectors.joining(",")), each.getDescription()))
                .collect(Collectors.toList());
    }
    
    @Override
    public Collection<String> getColumnNames() {
        return Arrays.asList("type", "type_aliases", "supported_database_types", "description");
    }
    
    @Override
    public Class<ShowMigrationCheckAlgorithmsStatement> getType() {
        return ShowMigrationCheckAlgorithmsStatement.class;
    }
}
