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

package org.apache.shardingsphere.mode.path;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobalPropertiesNodePathTest {
    
    @Test
    void assertGetRootPath() {
        assertThat(GlobalPropertiesNodePath.getRootPath(), is("/props"));
    }
    
    @Test
    void assertGetVersionRootPath() {
        assertThat(GlobalPropertiesNodePath.getVersionRootPath(), is("/props/versions"));
    }
    
    @Test
    void assertGetVersionPath() {
        assertThat(GlobalPropertiesNodePath.getVersionPath("0"), is("/props/versions/0"));
    }
    
    @Test
    void assertGetActiveVersionPath() {
        assertThat(GlobalPropertiesNodePath.getActiveVersionPath(), is("/props/active_version"));
    }
    
    @Test
    void assertIsActiveVersionPath() {
        assertTrue(GlobalPropertiesNodePath.isActiveVersionPath("/props/active_version"));
    }
}
