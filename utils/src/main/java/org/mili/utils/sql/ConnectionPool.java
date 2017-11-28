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
package org.mili.utils.sql;

import com.google.common.base.Optional;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Michael Lieshoff, 07.07.17
 */
public class ConnectionPool {

    private final static ThreadLocal<ConnectionPool.ConnectionContext> cache = new ThreadLocal<>();

    public ConnectionPool.ConnectionContext get() throws SQLException {
        if (cache.get() == null) {
            cache.set(getConnection());
        }
        return cache.get();
    }

    private ConnectionPool.ConnectionContext getConnection() throws SQLException {
        try {
            return connection(false);
        } catch (Exception e) {
            int i = 0;
            while (i < 10) {
                try {
                    return connection(true);
                } catch (Exception e1) {
                    //
                }
                i ++;
            }
            throw new IllegalStateException(e);
        }
    }

    private ConnectionPool.ConnectionContext connection(boolean autoReconnect) throws Exception {
        String prefix = "org.mili.database.";
        String host = Optional.fromNullable(System.getProperty(prefix + "host")).or("localhost");
        String port = Optional.fromNullable(System.getProperty(prefix + "port")).or("3306");
        String username = Optional.fromNullable(System.getProperty(prefix + "username")).or("root");
        String password = Optional.fromNullable(System.getProperty(prefix + "password")).or("");
        String url = System.getProperty(prefix + "url");
        if (autoReconnect) {
            url += "&autoReconnect=true";
        }
        Class.forName("com.mysql.jdbc.Driver");
        return new ConnectionPool.ConnectionContext(DriverManager.getConnection(url, username, password));
    }

    public void close() {
        if (cache.get() != null) {
            cache.remove();
        }
    }

    public static class ConnectionContext {
        private boolean inTransaction;
        private Connection connection;
        private long creationTime = System.currentTimeMillis();

        public ConnectionContext(Connection connection) {
            this.connection = connection;
        }

        public Connection getConnection() {
            return connection;
        }

        public long getCreationTime() {
            return creationTime;
        }

        public boolean isInTransaction() {
            return inTransaction;
        }

        public void setInTransaction(boolean inTransaction) {
            this.inTransaction = inTransaction;
        }
    }

}