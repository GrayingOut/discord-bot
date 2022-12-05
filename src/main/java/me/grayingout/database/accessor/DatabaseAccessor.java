package me.grayingout.database.accessor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.grayingout.database.query.DatabaseQuery;

/**
 * A base class for database accessors, which are classes
 * that provided access to a database, for executing queries
 */
public abstract class DatabaseAccessor {
    
    /**
     * The connection to the database
     */
    private Connection dbConnection;

    /**
     * Holds a queue of database queries to
     * be executed
     */
    private final ExecutorService es;

    /**
     * Creates a new {@code DatabaseAccessor}
     * 
     * @param databasePath The path to the database file
     */
    public DatabaseAccessor(String databasePath) {
        es = Executors.newFixedThreadPool(1);

        /* Connect to database */
        try {
            dbConnection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
            dbConnection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        /* Initialise database */
        init();
    }

    /**
     * Queue the execution of a query
     * 
     * @param query The database query
     * @return A completable future that will be called with the result
     */
    public CompletableFuture<Object> queueQuery(DatabaseQuery<?> query) {
        CompletableFuture<Object> future = new CompletableFuture<>();

        /* Queue the query */
        es.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    future.complete(query.execute(dbConnection));
                    dbConnection.commit();
                } catch (SQLException e) {
                    System.err.println("Failed to execute db query");
                    e.printStackTrace();
                    future.complete(null);
                }
            }
        });

        return future;
    }

    /**
     * Returns if there is an active connection to the warnings
     * database
     * 
     * @return If there is an active connection
     */
    public final boolean isConnected() {
        return dbConnection != null;
    }

    /**
     * The initialisation method called immediately
     * after connecting to the database
     */
    /* default */ abstract void init();
}
