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

import org.mili.utils.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Michael Lieshoff, 07.07.17
 */
public class ConnectionPool {

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    private static final ConnectionPool CONNECTION_POOL = new ConnectionPool();

    private final static ThreadLocal<ConnectionPool.ConnectionContext> cache = new ThreadLocal<>();

    public ConnectionPool.ConnectionContext get() throws SQLException {
        if (cache.get() == null) {
            cache.set(connection());
            Log.info(this, "get", "created connection, connection=%s", get().connection);
        }
        return cache.get();
    }

    private ConnectionPool.ConnectionContext connection() throws SQLException {
        String prefix = "org.mili.database.";
        String username = Optional.fromNullable(System.getProperty(prefix + "username")).or("root");
        String password = Optional.fromNullable(System.getProperty(prefix + "password")).or("");
        String url = System.getProperty(prefix + "url") + "&autoReconnect=true";
        Connection connection = DriverManager.getConnection(url, username, password);
        connection.setAutoCommit(false);
        return new ConnectionPool.ConnectionContext(connection);
    }

    public void close() throws SQLException {
        ConnectionContext connectionContext = cache.get();
        if (connectionContext != null) {
            Log.info(this, "close", "close connection context, statementCacheSize=%s, creationTime=%s, connection=%s",
                    connectionContext.statementCache.size(), new Date(connectionContext.getCreationTime()),
                    connectionContext.connection);
            connectionContext.statementCache.clear();
            connectionContext.connection.close();
            cache.remove();
        }
    }

    public static class ConnectionContext {

        private Map<String, PreparedStatement> statementCache = new ConcurrentHashMap<>();

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

        public PreparedStatement getStatement(String query) {
            return statementCache.get(query);
        }

        public void putStatement(String query, PreparedStatement preparedStatement) {
            statementCache.put(query, preparedStatement);
        }

    }

    public static ConnectionPool getInstance() {
        return CONNECTION_POOL;
    }

}