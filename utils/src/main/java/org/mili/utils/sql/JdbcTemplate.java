package org.mili.utils.sql;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Michael Lieshoff, 07.07.17
 */
public class JdbcTemplate {

    private ConnectionPool connectionPool = new ConnectionPool();

    private LoadingCache<String, PreparedStatement> cache = CacheBuilder.<String, PreparedStatement>newBuilder()
            .expireAfterWrite(Long.getLong("org.mili.utils.sql.JdbcTemplate", 60 * 1000 * 5), TimeUnit.MILLISECONDS)
            .build(new CacheLoader<String, PreparedStatement>() {
                @Override
                public PreparedStatement load(String query) throws Exception {
                    ConnectionPool.ConnectionContext connectionContext = connectionPool.get();
                    Connection connection = connectionContext.getConnection();
                    return connection.prepareStatement(query);
                }
            });

    public <T> Collection<T> query(RowTransformer<T> rowTransformer, String query, Object... params) {
        try {
            query = normalize(query);
            PreparedStatement preparedStatement = cache.get(query);
            fillWithObjects(preparedStatement, params);
            List<T> list = new ArrayList<>();
            ResultSet result = preparedStatement.executeQuery();
            while(result.next()) {
                list.add(rowTransformer.transform(result));
            }
            System.out.println(connectionPool.get() + " - size: " + list.size());
            preparedStatement.close();
            return list;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public <T> T one(RowTransformer<T> rowTransformer, String query, Object... params) {
        return null;
    }

    public int update(String query, Object... params) {
        return 0;
    }

    private String normalize(String sql) {
        sql = sql.replace("\r\n", "").replace("\n", "");
        sql = sql.replaceAll("\\s\\s+"," ");
        return sql;
    }

    private void fillWithObjects(PreparedStatement preparedStatement, Object[] objects) throws SQLException {
        if (objects != null) {
            for (int i = 0; i < objects.length; i ++) {
                Object o = objects[i];
                if (o instanceof String) {
                    o = o.toString().trim();
                }
                preparedStatement.setObject(i + 1, o);
                System.out.println("    " + (i + 1) + " = " + o);
            }
        }
    }

}
