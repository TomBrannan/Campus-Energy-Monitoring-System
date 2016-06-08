package database;

import java.sql.*;
import java.util.logging.Level;
import javax.sql.DataSource;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import utilities.ErrorLogger;
import utilities.PropertyManager;

/**
 * Represents a database connection pool. This class provides and manages 
 * the database connection pool.
 * @author jl26123
 */
public class ConnectionPool {

    private static ConnectionPool pool = null;
    private static DataSource dataSource = null;
/**
 * Constructs a <code>ConnectionPool</code> object with the default <code> DataSource</code>.
 */
    private ConnectionPool() {
        try {
            InitialContext ic = new InitialContext();
            dataSource = (DataSource) ic.lookup("java:/comp/env/jdbc/GreenPower");
        } catch (NamingException e) {
            PropertyManager.setProperty("UseDBPooling", "no");
            ErrorLogger.log(Level.SEVERE,"Could not connect to data source", e);
        }
    }

    /**
     * 
     * Gets a object of this connection pool.
     * @return This <code>ConnectionPool</code> object.
     */
    public static synchronized ConnectionPool getInstance() {
        if (pool == null) {
            pool = new ConnectionPool();
        }
        return pool;
    }

    /**
     * Gets a connection to the database.
     * @return A connection to the database.
     */
    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            ErrorLogger.log(Level.SEVERE,"Could not get a connection for this data source", e);
            return null;
        }
    }

    /**
     * Releases this connection from the connection pool with given the name of this connection.
     * @param c The name of this connection.
     */
    public void freeConnection(Connection c) {
        if(c==null) return; 
        try {
            c.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    
    public static void main(String[] args) {
        ConnectionPool cp = new ConnectionPool();
    }
}