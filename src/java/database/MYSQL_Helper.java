
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import utilities.Debug;
import utilities.ErrorLogger;
import utilities.PropertyManager;

/**
 *
 * @author cjones
 */
public class MYSQL_Helper {
    private static int connectionCount = 0;
    private static final boolean REUSE_CONNECTION=true;

    private static final ConnectionPool connectionPool = null;//ConnectionPool.getInstance();
   
    private static final boolean USE_DB_POOLING = false;
    
    
    
    private static Connection connection = null;
    private static final String mysqlPrefix="jdbc:mysql://";
    private static final String hostnameproduction =PropertyManager.getProperty("MySQLHostName").trim();
    private static final String hostnametest = PropertyManager.getProperty("MySQLHostNameTest").trim();
    private static final String databaseName = PropertyManager.getProperty("DatabaseName").trim();
    private static String databaseURL = null;
    private static final String userName = PropertyManager.getProperty("MySQLUserName").trim();
    private static final String password = PropertyManager.getProperty("MySQLPassword").trim();
    private static final String config = PropertyManager.getProperty("Configuration").trim();
    
    private static final String CONFIG_TEST = "test", CONFIG_PRODUCTION = "production";
    
    static {
        // Load TEST or PRODUCTION database host url
        Debug.println("MYSQL_Helper(static init): CONFIG loaded: " + config);
        Debug.println("MYSQL_Helper(static init): TEST-HOST loaded: " + hostnametest);
        Debug.println("MYSQL_Helper(static init): PRODUCTION-HOST loaded " + hostnameproduction);
        if (config.equalsIgnoreCase(CONFIG_TEST)) {
            databaseURL = mysqlPrefix+hostnametest +"/"+databaseName;
            Debug.println("MYSQL_Helper(static init): Using TEST-HOST (" + hostnametest + ") for SQL HOST-NAME");
        }
        else { 
            databaseURL = mysqlPrefix+hostnameproduction +"/"+databaseName;
            Debug.println("MYSQL_Helper(static init): Using PRODUCTION-HOST (" + hostnameproduction + ") for SQL HOST-NAME");
        }
        Debug.println("MYSQL_Helper(static init): Using SQL HOST-NAME (databaseURL): " + databaseURL);
    }
    
   
    /**
     * Returns a connection to the database. 
     * 
     * @return A <code>Connection</code> to the database.
     */
    public static synchronized Connection getConnection() {
//     ErrorLogger.log(Level.SEVERE, "DB Connection Pooling is "+
//                   USE_DB_POOLING );
     if(USE_DB_POOLING)  return connectionPool.getConnection();
     if(REUSE_CONNECTION==false)   return  MYSQL_Helper.getNewConnection(); 
     try {
        if (connection == null || connection.isClosed()) {
            connection = MYSQL_Helper.getNewConnection();
            return connection;
        }
        // If we get here the old connection did exist, but is it still valid?
        
            //allow 1 seconds to see if connection is still valid
            if (connection.isValid(1)) {
                return connection;
            } else {
                connection = getNewConnection();
                return connection;
            }
        } catch (SQLException e) {
            return getNewConnection();
        }
    }

    /**
     * Creates a new connection with a DriverManager and returns it if the connection
     * is established. Catches any SQLException exception thrown by the
     * <code>getConnection(String url, String user, String password)</code>
     * method of <code>DriverManager</code> class and logs an error message in the error log
     * file.  
     *
     * @return A <code>Connection</code> to the database or null if none could be created.
     */
    private static Connection getNewConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance(); //Not normally needed for MySQL - but ...
        } catch (ClassNotFoundException ex) {
            ErrorLogger.log(Level.SEVERE, "Could not find the class com.mysql.jdbc.Driver \n"+
                    "Program will now exit. ", ex);
            System.exit(1);
        } catch (InstantiationException ex) {
            ErrorLogger.log(Level.SEVERE, "Could not instaniate the class com.mysql.jdbc.Driver \n"+
                    "Program will now exit. ", ex);
            System.exit(1);
        } catch (IllegalAccessException ex) {
            ErrorLogger.log(Level.SEVERE, "Could not access the class com.mysql.jdbc.Driver \n"+
                    "Program will now exit. ", ex);
            System.exit(1);
        }
        try {
            conn = DriverManager.getConnection(databaseURL,userName,password);
        } catch (SQLException e) {
            ErrorLogger.log(Level.SEVERE, "SQL Exception was thrown while "
                    + "trying to connect to the database. Database string = "+
                    databaseURL+ " user =  "+userName + " password "+password);
        }
        System.out.println("Connection Count is "+ ++connectionCount);
        //Connection is thread safe, but only one thread can access the database at once now
        return conn;
    }

    
    /**
     * Returns the given <code>Connection</code> object to the connection pool.
     *
     * @param connection The Connection object being returned.
     */
    public static void returnConnection(Connection connection) {
        if(USE_DB_POOLING) {
             connectionPool.freeConnection(connection);
             return;
        }
        if(REUSE_CONNECTION) return; 
        
        // Close connection if we are not reusing them 
        if (connection != null) { 
          try { connection.close(); connection=null;
              } catch (SQLException e) {
                    ErrorLogger.log(Level.SEVERE, "SQL Exception is thrown while " +
                    "trying to close a Connection object. The connection " + 
                    "object was not null.", e); 
              }
        }
         
    }
    
    
    
    
    /**
     * Closes the given <code>Connection</code> object if it is not null.
     * Catches SQLException if it is thrown by the <code>close</code> method of
     * <code>Connection</code> interface and logs an error message in the 
     * error log file.
     *
     */
    public static void closeConnectionsOnExit() {
        if(USE_DB_POOLING) {
             return;
        }
        if (connection == null) {
            return;
        }
        try {
            connection.close();
        } catch (SQLException e) {
            ErrorLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                    + "trying to close a Connection object during "
                    + "program termination. The connection "
                    + "object was not null.", e);
        }

    }
    
   /**
     * Closes the given <code>Statement</code> object if it is not null. Catches
     * SQLException if it is thrown by the <code>close</code> method of
     * <code>Statement</code> interface and logs an error message in the error log file.
     *
     * @param stmt The Statement object to close.
     */
    public static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                ErrorLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                        + "trying to close a Statement object. The connection "
                        + "object is not null.", e);
            }
        }
    }
    
    /**
     * Closes the given <code>PreparedStatement</code> object if it is not null.
     * Catches SQLException if it is thrown by the <code>close</code> method of
     * <code>PreparedStatement</code> interface and logs an error message in the error log
     * file.
     *
     * @param ps The PreparedStatement object to close.
     */
    public static void closePreparedStatement(PreparedStatement ps) {
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                ErrorLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                        + "trying to close a PreparedStatement object. The connection "
                        + "object is not null.", e);
            }
        }
    }
    
    /**
     * Closes the given <code>ResultSet</code> object if it is not null. Catches
     * the SQLException if it is thrown by the <code>close</code> method of
     * <code>ResultSet</code> interface and logs an error message in the error log file.
     *
     * @param rs The ResultSet object to close.
     */
    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
               ErrorLogger.log(Level.SEVERE, "SQL Exception is thrown while "
                        + "trying to close a ResultSet object. The connection "
                        + "object is not null.", e);
            }
        }
    }
    
    /**
     * Create a <code>PreparedStatement</code> with the given SQL code using a <code>Connection</code> 
     * obtained from <code>this.getConnection()</code>.
     * 
     * @param sql The SQL of the prepared statement
     * @return An <code>PreparedStatement</code> with the given SQL code.
     * @throws SQLException
     */
    public static PreparedStatement prepareStatement(String sql) throws SQLException
    {
        return getConnection().prepareStatement(sql);
    }

    public static void main(String[] args){
        //PropertyManager.configure("/home/cws55854/P/CompSci480/Lucid/web/WEB-INF/config/General.properties");
        //PropertyManager.setProperty("UseDBPooling", "no");
         Connection conn = MYSQL_Helper.getConnection();
         Connection newconnection = MYSQL_Helper.getConnection();
         MYSQL_Helper.returnConnection(conn);
         MYSQL_Helper.returnConnection(newconnection);
         newconnection = MYSQL_Helper.getConnection();
         conn = MYSQL_Helper.getConnection();
         conn = MYSQL_Helper.getConnection();
         MYSQL_Helper.returnConnection(conn);
         MYSQL_Helper.returnConnection(newconnection);
         conn = MYSQL_Helper.getConnection();
         conn = MYSQL_Helper.getConnection();
         MYSQL_Helper.closeConnectionsOnExit();
    }
    
}
