package me.grayingout.database.query;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * The base interface for a query to the database
 */
@FunctionalInterface
public interface DatabaseQuery<T> {
    
    /**
     * Called by the {@code DatabaseAccessor} when
     * executing the query
     * 
     * @param connection The connection to the database
     * @return The response object of type {@code T}
     */
    T execute(Connection connection) throws SQLException;
}
