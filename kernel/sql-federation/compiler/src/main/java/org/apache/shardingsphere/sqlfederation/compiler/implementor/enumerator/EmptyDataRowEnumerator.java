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

package org.apache.shardingsphere.sqlfederation.compiler.implementor.enumerator;

import org.apache.calcite.linq4j.Enumerator;

/**
 * Empty data row enumerator.
 */
public final class EmptyDataRowEnumerator implements Enumerator<Object> {
    
    @Override
    public Object current() {
        return new Object();
    }
    
    @Override
    public boolean moveNext() {
        return false;
    }
    
    @Override
    public void reset() {
    }
    
    @Override
    public void close() {
    }
}
