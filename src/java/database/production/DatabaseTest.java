package database.production;

import database.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Run main method to remove and re-initialize all of the tables in the database.
 * @author cws55854
 */
public class DatabaseTest {
    
    public static void main(String[] args) throws SQLException
    {
        Connection c = MYSQL_Helper.getConnection();
        Statement statement = c.createStatement();
        String create_query = "create table buildings ( building_id int not null primary key);";
        statement.execute(create_query);
        
        ResultSet set = statement.executeQuery("select * from buildings;");
        MYSQL_Helper.returnConnection(c);
        
    }
    
}
