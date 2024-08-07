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

package org.apache.shardingsphere.mode.repository.standalone.jdbc.sql;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class JDBCRepositorySQLLoaderTest {
    
    @Test
    void assertLoad() {
        assertThat(JDBCRepositorySQLLoader.load("MySQL").getType(), is("MySQL"));
        assertThat(JDBCRepositorySQLLoader.load("EmbeddedDerby").getType(), is("EmbeddedDerby"));
        assertThat(JDBCRepositorySQLLoader.load("DerbyNetworkServer").getType(), is("DerbyNetworkServer"));
        assertThat(JDBCRepositorySQLLoader.load("HSQLDB").getType(), is("HSQLDB"));
    }
    
    @Test
    void assertLoadByDefault() {
        assertThat(JDBCRepositorySQLLoader.load("nonexistent").getType(), is("H2"));
    }
}
