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

package org.apache.shardingsphere.infra.executor.sql.execute.result.query;

import org.apache.shardingsphere.infra.executor.sql.execute.result.ExecuteResult;

import java.io.InputStream;
import java.io.Reader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Optional;

/**
 * Query result.
 */
public interface QueryResult extends ExecuteResult, AutoCloseable {
    
    /**
     * Iterate next data.
     *
     * @return has next data
     * @throws SQLException SQL exception
     */
    boolean next() throws SQLException;
    
    /**
     * Get data value.
     *
     * @param columnIndex column index
     * @param type class type of data value
     * @return data value
     * @throws SQLException SQL exception
     */
    Object getValue(int columnIndex, Class<?> type) throws SQLException;
    
    /**
     * Get calendar value.
     *
     * @param columnIndex column index
     * @param type class type of data value
     * @param calendar calendar
     * @return calendar value
     * @throws SQLException SQL exception
     */
    Object getCalendarValue(int columnIndex, Class<?> type, @SuppressWarnings("UseOfObsoleteDateTimeApi") Calendar calendar) throws SQLException;
    
    /**
     * Get input stream.
     *
     * @param columnIndex column index
     * @param type class type of data value
     * @return input stream
     * @throws SQLException SQL exception
     */
    InputStream getInputStream(int columnIndex, String type) throws SQLException;
    
    /**
     * Get CharacterStream.
     *
     * @param columnIndex column index
     * @return reader
     * @throws SQLException SQL exception
     */
    Reader getCharacterStream(int columnIndex) throws SQLException;
    
    /**
     * Judge result set is null or not.
     *
     * @return result set is null or not
     * @throws SQLException SQL exception
     */
    boolean wasNull() throws SQLException;
    
    /**
     * Get query result meta data.
     *
     * @return query result meta data
     */
    QueryResultMetaData getMetaData();
    
    /**
     * Get JDBC result set.
     *
     * @return JDBC result set
     */
    default Optional<ResultSet> getJDBCResultSet() {
        return Optional.empty();
    }
}
