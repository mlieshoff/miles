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

import org.mili.utils.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcTemplate {

    protected <T> List<T> query(RowTransformer<T> rowTransformer, String sql, Object... objects) throws SQLException {
        ConnectionPool.ConnectionContext connectionContext = ConnectionPool.getInstance().get();
        Connection connection = connectionContext.getConnection();
        sql = normalize(sql);
        Log.info(this, "query", connectionContext + " - query: " + sql);
        PreparedStatement preparedStatement = connectionContext.getStatement(sql);
        if (preparedStatement == null) {
            preparedStatement = connection.prepareStatement(sql);
            connectionContext.putStatement(sql, preparedStatement);
        }
        fillWithObjects(preparedStatement, objects);
        List<T> list = new ArrayList<>();
        ResultSet result = preparedStatement.executeQuery();
        while(result.next()) {
            Log.info(this, "query", (connectionContext + " - call transformer"));
            list.add(rowTransformer.transform(result));
        }
        Log.info(this, "query", (connectionContext + " - size: " + list.size()));
        return list;
    }

    private void fillWithObjects(PreparedStatement preparedStatement, Object[] objects) throws SQLException {
        if (objects != null) {
            for (int i = 0; i < objects.length; i ++) {
                Object o = objects[i];
                if (o instanceof String) {
                    o = o.toString().trim();
                }
                preparedStatement.setObject(i + 1, o);
                Log.info(this, "fillWithObjects", "    " + (i + 1) + " = " + o);
            }
        }
    }

    protected <T> T querySingle(RowTransformer<T> rowTransformer, String sql, Object... objects) throws SQLException {
        List<T> list = query(rowTransformer, sql, objects);
        int size = list.size();
        if (size == 0) {
            return null;
        } else if (size == 1) {
            return list.get(0);
        } else {
            throw new IllegalStateException("more than 1 result found[" + size + "]!");
        }
    }

    private String normalize(String sql) {
        sql = sql.replace("\r\n", "").replace("\n", "");
        sql = sql.replaceAll("\\s\\s+"," ");
        return sql;
    }

    protected int update(String sql, Object... objects) throws SQLException {
        ConnectionPool.ConnectionContext connectionContext = ConnectionPool.getInstance().get();
        Connection connection = connectionContext.getConnection();
        sql = normalize(sql);
        Log.info(this, "update", connectionContext + " - update: " + sql);
        PreparedStatement preparedStatement = connectionContext.getStatement(sql);
        if (preparedStatement == null) {
            preparedStatement = connection.prepareStatement(sql);
            connectionContext.putStatement(sql, preparedStatement);
        }
        fillWithObjects(preparedStatement, objects);
        return preparedStatement.executeUpdate();
    }

    protected boolean execute(String sql, Object... objects) throws SQLException {
        ConnectionPool.ConnectionContext connectionContext = ConnectionPool.getInstance().get();
        Connection connection = connectionContext.getConnection();
        sql = normalize(sql);
        Log.info(this, "execute", connectionContext + " - execute: " + sql);
        PreparedStatement preparedStatement = connectionContext.getStatement(sql);
        if (preparedStatement == null) {
            preparedStatement = connection.prepareStatement(sql);
            connectionContext.putStatement(sql, preparedStatement);
        }
        if (objects != null) {
            for (int i = 0; i < objects.length; i ++) {
                Object o = objects[i];
                if (o instanceof String) {
                    o = o.toString().trim();
                }
                preparedStatement.setObject(i + 1, o);
            }
        }
        return preparedStatement.execute();
    }

    protected long getTransactionCreationTime() throws SQLException {
        return ConnectionPool.getInstance().get().getCreationTime();
    }

}