package org.mili.utils.sql.transaction;

import org.mili.utils.Lambda;
import org.mili.utils.Log;
import org.mili.utils.sql.ConnectionPool;
import org.mili.utils.sql.ConnectionPool.ConnectionContext;
import org.mili.utils.sql.service.ServiceException;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionalWrapper {

    public <T> T doTransactional(Lambda<T> lambda, Object... objects) throws ServiceException {
        ConnectionContext connectionContext = enable();
        Connection connection = connectionContext.getConnection();
        try {
            T result = lambda.exec(objects);
            commit(connection);
            return result;
        } catch (Exception e) {
            rollback(connection);
            throw new ServiceException(e);
        } finally {
            disable(connection);
        }
    }

    private ConnectionContext enable() throws ServiceException {
        try {
            ConnectionContext connectionContext = ConnectionPool.getInstance().get();
            Connection connection = connectionContext.getConnection();
            Log.info(this, "enable", "enable transaction: %s", connection);
            return connectionContext;
        } catch (SQLException e) {
            throw new ServiceException(e);
        }
    }

    private void disable(Connection connection) throws ServiceException {
        try {
            Log.info(this, "disable", "disable transaction: %s", connection);
            ConnectionPool.getInstance().close();
        } catch (SQLException e) {
            throw new ServiceException(e);
        }
    }

    private void rollback(Connection connection) throws ServiceException {
        try {
            Log.info(this, "rollback", "rollback transaction: %s", connection);
            connection.rollback();
        } catch (SQLException e) {
            throw new ServiceException(e);
        }
    }

    private void commit(Connection connection) throws ServiceException {
        try {
            Log.info(this, "commit", "commit transaction: %s", connection);
            connection.commit();
        } catch (SQLException e) {
            Log.error(this, "commit", "error: %s", e.getMessage());
            rollback(connection);
            throw new ServiceException(e);
        }
    }

}
