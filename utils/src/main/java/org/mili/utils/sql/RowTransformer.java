package org.mili.utils.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RowTransformer<T> {

    T transform(ResultSet resultSet) throws SQLException;

}