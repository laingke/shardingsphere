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

package org.apache.shardingsphere.infra.lock;

/**
 * ShardingSphere lock.
 */
public interface ShardingSphereLock {
    
    /**
     * Try to lock.
     *
     * @param schemaName schema name
     * @param tableName table name
     * @param timeoutMilliseconds time out milliseconds to acquire lock
     * @return true if get the lock, false if not
     */
    boolean tryLock(String schemaName, String tableName, long timeoutMilliseconds);
    
    /**
     * Release lock.
     * @param schemaName schema name
     * @param tableName table name
     */
    void releaseLock(String schemaName, String tableName);
    
    /**
     * Await lock.
     * 
     * @param timeoutMilliseconds time out milliseconds to await lock
     * @return true if no exception
     */
    boolean await(Long timeoutMilliseconds);
    
    /**
     * Signal all.
     */
    void signalAll();
}
