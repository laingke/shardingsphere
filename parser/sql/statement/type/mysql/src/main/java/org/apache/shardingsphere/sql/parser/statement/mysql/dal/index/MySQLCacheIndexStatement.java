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

package org.apache.shardingsphere.sql.parser.statement.mysql.dal.index;

import lombok.Getter;
import org.apache.shardingsphere.infra.database.core.type.DatabaseType;
import org.apache.shardingsphere.sql.parser.statement.core.segment.dal.CacheTableIndexSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.dal.PartitionDefinitionSegment;
import org.apache.shardingsphere.sql.parser.statement.core.statement.type.dal.DALStatement;
import org.apache.shardingsphere.sql.parser.statement.core.value.identifier.IdentifierValue;

import java.util.Collection;

/**
 * Cache index statement for MySQL.
 */
@Getter
public final class MySQLCacheIndexStatement extends DALStatement {
    
    private final IdentifierValue name;
    
    private final Collection<CacheTableIndexSegment> tableIndexes;
    
    private final PartitionDefinitionSegment partitionDefinition;
    
    public MySQLCacheIndexStatement(final DatabaseType databaseType,
                                    final IdentifierValue name, final Collection<CacheTableIndexSegment> tableIndexes, final PartitionDefinitionSegment partitionDefinition) {
        super(databaseType);
        this.name = name;
        this.tableIndexes = tableIndexes;
        this.partitionDefinition = partitionDefinition;
    }
}
