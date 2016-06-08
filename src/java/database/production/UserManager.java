/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database.production;

import common.User;
import common.UserRole;
import database.MYSQL_Helper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import utilities.Debug;
import utilities.ErrorLogger;
import utilities.PropertyManager;

/**
 *
 * @author lap60658
 */
public class UserManager implements database.UserManager {

    private static final String USER_TABLE_NAME = "userData";

    private static final String VAR_ID = "userNumber";
    private static final String VAR_LOGIN_NAME = "loginName";
    private static final String VAR_PASSWORD = "userPassword";
    private static final String VAR_FIRST_NAME = "firstName";
    private static final String VAR_LAST_NAME = "lastName";
    private static final String VAR_EMAIL_ADDRESS = "emailAddress";
    private static final String VAR_USER_ROLE = "userRole";
    private static final String VAR_LAST_LOGIN = "lastLogin";
    private static final String VAR_LOGIN_COUNT = "loginCount";

    private static final String PREPARED_GET_ALL_USERS
            = "SELECT * FROM " + USER_TABLE_NAME + " ; ";

    private static final String PREPARED_VALIDATE_USER
            = "SELECT * FROM " + USER_TABLE_NAME + " WHERE " + VAR_LOGIN_NAME
            + " = ? AND " + VAR_PASSWORD + " = ? ; ";

    private static final String PREPARED_UPDATE_USER
            = "UPDATE " + USER_TABLE_NAME + " SET "
            + VAR_LOGIN_NAME + " = ?, " + VAR_PASSWORD + " = ?, "
            + VAR_FIRST_NAME + " = ?, " + VAR_LAST_NAME + " = ?, "
            + VAR_EMAIL_ADDRESS + " = ?, " + VAR_USER_ROLE + " = ?, "
            + VAR_LAST_LOGIN + " = ?, " + VAR_LOGIN_COUNT + " = ? "
            + "WHERE " + VAR_ID + " = ? ;";

    /**
     * Returns a <code>User</code> object when the login name and password are
     * valid. The last login time and the number of logins should be updated
     * will be updated, if the login name and password are correct.
     *
     * @param loginName The login name of this <code>User</code>.
     * @param password The password of this <code>User</code>.
     * @return A <code>User</code> object that contains all the information
     * about this user.
     */
    @Override
    public User validateUser(String loginName, String password) {
        Connection conn = database.MYSQL_Helper.getConnection();
        ResultSet rs;
        User user = null;

        try {
            PreparedStatement stmt2 = conn.prepareStatement(PREPARED_VALIDATE_USER);
            stmt2.setString(1, loginName);
            stmt2.setString(2, password);
            rs = stmt2.executeQuery();
            if (rs == null || rs.next() == false) {
                MYSQL_Helper.returnConnection(conn);
                return null;
            }
            user = new User();
            user.setUserNumber(rs.getInt("userNumber"));
            user.setUserPassword(password);
            user.setLoginName(loginName);
            user.setEmailAddress(rs.getString("emailAddress"));
            user.setFirstName(rs.getString("firstName"));
            user.setLastName(rs.getString("lastName"));
            user.setUserRole(UserRole.getUserRole(rs.getString("userRole")));
            LocalDateTime now = LocalDateTime.now();
            user.setLastLogin(now);
            int loginCount = rs.getInt("loginCount") + 1;
            user.setLoginCount(loginCount);

        } catch (SQLException ex) {
            Debug.println("SQLException in validateUser(String loginName, String password)");
            MYSQL_Helper.returnConnection(conn);
            ex.printStackTrace();
            return null;
        }
        MYSQL_Helper.returnConnection(conn);
        updateUser(user);
        return user;
    }

    /**
     * Tries to add the given <code>User</code> to the database. If the insert
     * is successful the <code>User</code> object is return, if errors occur
     * null is returned.
     *
     * @param user A <code>User</code> object whose data will be inserted in the
     * database.
     * @return A <code>User</code> object which has been added to the user's
     * database. null is returned if errors occurs and the user wasn't added.
     */
    @Override
    public User addUser(User user) {
        String sql = "INSERT INTO " + USER_TABLE_NAME + " VALUES (DEFAULT,?,?,?,?,?,?,?,?); ";
        Connection conn = database.MYSQL_Helper.getConnection();
        int userId = 0;

        try {
            PreparedStatement stmt2 = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt2.setString(1, user.getLoginName().trim());
            stmt2.setString(2, user.getUserPassword().trim());
            stmt2.setString(3, user.getFirstName().trim());
            stmt2.setString(4, user.getLastName().trim());
            stmt2.setString(5, user.getEmailAddress().trim());
            stmt2.setString(6, user.getUserRole().getRoleName());
            stmt2.setString(7, user.getLastLogin().toString());
            stmt2.setInt(8, user.getLoginCount());

            stmt2.executeUpdate();
            ResultSet rs = stmt2.getGeneratedKeys();
            if (rs.next()) {
                userId = rs.getInt(1);
            }

        } catch (SQLException ex) {
            Debug.println("SQLException in addUser(User user)");

            ex.printStackTrace();
            MYSQL_Helper.returnConnection(conn);
            ex.printStackTrace();
            return user;
        }

        return getUserByID(userId);//need to sue genterated keys
    }

    /**
     * Tries to update the database so that the user in the database with the
     * same user number as the <code>User</code> provided has the same
     * information for all other attributes. If it is successful the
     * <code>User</code> is returned.
     *
     * @param user A <code>User</code> object which contains the latest
     * information.
     * @return A <code>User</code> object with latest update if success was
     * reached, otherwise null is returned.
     */
    @Override
    public User updateUser(User user) {
        Connection conn = database.MYSQL_Helper.getConnection();
        try {
            PreparedStatement stmt2 = conn.prepareStatement(PREPARED_UPDATE_USER);
            stmt2.setString(1, user.getLoginName().trim());
            stmt2.setString(2, user.getUserPassword().trim());
            stmt2.setString(3, user.getFirstName().trim());
            stmt2.setString(4, user.getLastName().trim());
            stmt2.setString(5, user.getEmailAddress().trim());
            stmt2.setString(6, user.getUserRole().getRoleName());
            stmt2.setString(7, user.getLastLogin().toString());
            stmt2.setInt(8, user.getLoginCount());
            stmt2.setInt(9, user.getUserNumber());

            if (stmt2.executeUpdate() == 0) {
                return null;
            }

        } catch (SQLException ex) {
            ErrorLogger.log(Level.SEVERE, "SQLException in updateUser(User user) " + user);
            MYSQL_Helper.returnConnection(conn);
            return null;
        }

        return getUserByID(user.getUserNumber());
    }

    /**
     * Tries to delete the given <code>User</code> from the database, by
     * deleting the user with the same userNumber.
     *
     * @param user The <code>User</code> that is to be deleted.
     * @return True is returned if the given <code>User</code> has been deleted
     * from the database successfully. Otherwise, false is returned.
     */
    @Override
    public boolean deleteUser(User user) {
        String sql = "DELETE FROM " + USER_TABLE_NAME + " WHERE userNumber =?; ";
        Connection conn = database.MYSQL_Helper.getConnection();

        try {
            PreparedStatement stmt2 = conn.prepareStatement(sql);
            stmt2.setInt(1, user.getUserNumber());
            stmt2.executeUpdate();
            return getUserByID(user.getUserNumber()) == null;

        } catch (SQLException ex) {
            Debug.println("SQLException in deleteUser(User user) " + user);
            ex.printStackTrace();
            MYSQL_Helper.returnConnection(conn);
            return false;
        }

    }

    /**
     * Retrieves the <code>User</code> from the database that has the given
     * identification number.
     *
     * @param userID The identification number for the <code>User</code> to be
     * found.
     * @return A <code>User</code> object that has the given identification
     * number.
     */
    @Override
    public User getUserByID(int userID) {
        String sql = "SELECT * FROM " + USER_TABLE_NAME + " WHERE   userNumber = ? ; ";
        Connection conn = database.MYSQL_Helper.getConnection();
        ResultSet rs;
        User user = null;

        try {
            PreparedStatement stmt2 = conn.prepareStatement(sql);
            stmt2.setInt(1, userID);

            rs = stmt2.executeQuery();
            if (rs == null || rs.next() == false) {
                MYSQL_Helper.returnConnection(conn);
                return null;
            }
            user = new User();
            user.setUserNumber(rs.getInt("userNumber"));
            user.setUserPassword(rs.getString("userPassword"));
            user.setLoginName(rs.getString("loginName"));
            user.setEmailAddress(rs.getString("emailAddress"));
            user.setFirstName(rs.getString("firstName"));
            user.setLastName(rs.getString("lastName"));
            user.setUserRole(UserRole.getUserRole(rs.getString("userRole")));
            LocalDateTime now = LocalDateTime.parse(rs.getString("lastLogin"));
            user.setLastLogin(now);
            int loginCount = rs.getInt("loginCount");
            user.setLoginCount(loginCount);
            rs.close();

        } catch (SQLException ex) {
            Debug.println("SQLException in getUserByID(int userID)");
            MYSQL_Helper.returnConnection(conn);
            ex.printStackTrace();
            return null;
        }
        MYSQL_Helper.returnConnection(conn);

        return user;
    }

    /**
     * Retrieves the <code>User</code> from the database that has the given
     * login name.
     *
     * @param loginName The login name for the <code>User</code> to be found.
     * @return A <code>User</code> object that has the given login name.
     */
    @Override
    public User getUserByLoginName(String loginName) {
        String sql = "SELECT * FROM "+USER_TABLE_NAME+" WHERE   loginName = ? ; ";
        Connection conn = database.MYSQL_Helper.getConnection();
        ResultSet rs;
        User user = null;
        
        try{
           PreparedStatement stmt2 = conn.prepareStatement(sql); 
           stmt2.setString(1, loginName);

           rs = stmt2.executeQuery();
           if(rs==null || rs.next() == false){
              MYSQL_Helper.returnConnection(conn);
              return null;      
           }
           user=new User();
           user.setUserNumber(rs.getInt("userNumber"));
           user.setUserPassword(rs.getString("userPassword"));
           user.setLoginName(rs.getString("loginName"));
           user.setEmailAddress(rs.getString("emailAddress"));
           user.setFirstName(rs.getString("firstName"));
           user.setLastName(rs.getString("lastName"));
           user.setUserRole(UserRole.getUserRole(rs.getString("userRole")));
           LocalDateTime now = LocalDateTime.parse(rs.getString("lastLogin"));
           user.setLastLogin(now);
           int loginCount = rs.getInt("loginCount");
           user.setLoginCount(loginCount);
           rs.close();
           
        }catch (SQLException ex){
           Debug.println("SQLException in getUserByLoginName(String loginName)");
           MYSQL_Helper.returnConnection(conn);
           ex.printStackTrace();
           return null;     
       }
        MYSQL_Helper.returnConnection(conn);
        return user;  
    }

    /**
     * Retrieves the <code>User</code>s in the database that have the same email
     * address as the one provided.
     *
     * @param emailAddress The email address used to find <code>User</code>s.
     * @return A collection of <code>User</code> objects that have the given
     * email address.
     */
    @Override
    public Collection<User> getAllUsersWithEmailAddress(String emailAddress) {
        Collection<User> users = new ArrayList<>();
        String sql = "SELECT * FROM "+USER_TABLE_NAME+" WHERE emailAddress = ? ; ";
        Connection conn = database.MYSQL_Helper.getConnection();
        ResultSet rs;
        User user = null;
        
        try{
           PreparedStatement stmt2 = conn.prepareStatement(sql); 
           stmt2.setString(1, emailAddress);

           rs = stmt2.executeQuery();
           if(rs==null ){
              MYSQL_Helper.returnConnection(conn);
              return users;      
           }
           while(rs.next()){
                user=new User();
                user.setUserNumber(rs.getInt("userNumber"));
                user.setUserPassword(rs.getString("userPassword"));
                user.setLoginName(rs.getString("loginName"));
                user.setEmailAddress(rs.getString("emailAddress"));
                user.setFirstName(rs.getString("firstName"));
                user.setLastName(rs.getString("lastName"));
                user.setUserRole(UserRole.getUserRole(rs.getString("userRole")));
                LocalDateTime now = LocalDateTime.parse(rs.getString("lastLogin"));
                user.setLastLogin(now);
                int loginCount = rs.getInt("loginCount");
                user.setLoginCount(loginCount);
                
                users.add(user);
           }
        }catch (SQLException ex){
           Debug.println("SQLException in getAllUsersWithEmailAddress(String emailAddress)");
           MYSQL_Helper.returnConnection(conn);
           ex.printStackTrace();
           return users;     
       }

        MYSQL_Helper.returnConnection(conn);

        return users;  
        
    }

    /**
     * Retrieves all the <code>User</code>s from the database and stores them in
     * a collection that will be returned.
     *
     * @return A collection of <code>User</code> objects including all the users
     * in the database. If there are no users in the database the collection
     * will be empty
     */
    @Override
    public Collection<User> getAllUsers() {
        Collection<User> users = new ArrayList<>();
        Connection conn = database.MYSQL_Helper.getConnection();
        ResultSet rs;
        User user;

        try {
            PreparedStatement stmt2 = conn.prepareStatement(PREPARED_GET_ALL_USERS);
            rs = stmt2.executeQuery();
            if (rs == null) {
                MYSQL_Helper.returnConnection(conn);
                return users;
            }
            while (rs.next()) {
                user = getUserByID(rs.getInt(VAR_ID));
                LocalDateTime now = LocalDateTime.parse(rs.getString(VAR_LAST_LOGIN));
                user.setLastLogin(now);
                int loginCount = rs.getInt(VAR_LOGIN_COUNT);
                user.setLoginCount(loginCount);

                users.add(user);
            }
            rs.close();
        } catch (SQLException ex) {
            Debug.println("SQLException in getAllUsers()");
            MYSQL_Helper.returnConnection(conn);
            return users;
        }

        MYSQL_Helper.returnConnection(conn);

        return users;
    }

    /**
     * Tries to delete the <code>User</code> from the database with the given
     * identification number.
     *
     * @param userID The identification number of the <code>User</code> to be
     * deleted.
     * @return True is returned if the <code>User</code> was successfully
     * deleted, false is returned if there are any errors and the row was not
     * deleted.
     */
    @Override
    public boolean deleteUserbyID(int userID) {
        return deleteUser(getUserByID(userID));
    }

    public static void main(String[] args) {
        PropertyManager.configure("P:/webProjects/Lucid/web/WEB-INF/config/General.properties");
        database.UserManager um = database.Database.getDatabaseManagement().getUserManager();
        User testUser = new User(100, "test", "123", "Bob", "Billy", "lapeiffer@yahoo.com", UserRole.SystemAdmin, LocalDateTime.of(2015, Month.JANUARY, 5, 13, 30), 1);

        testUser = um.addUser(testUser);
        System.out.println("added: " + testUser);
        
        testUser = um.getUserByID(testUser.getUserNumber());
        System.out.println("gotten by id: " + testUser);
        
        testUser = um.getUserByLoginName("test");
        System.out.println("gotten by login name: " + testUser);

        if (um.deleteUser(testUser)) {
            System.out.println("Test user was successfully deleted.");
        }
        
        testUser.setFirstName("Ember");
        testUser.setLastName("Baker");        
        um.updateUser(testUser);
        testUser = um.addUser(testUser);
        System.out.println("test user updated and added: " + testUser);
        
        Collection<User> users = um.getAllUsers();
        Iterator<User> ui = users.iterator();
        System.out.println("All users: ");
        while(ui.hasNext()) {
            System.out.println(ui.next());
        }
        
        users = um.getAllUsersWithEmailAddress("lapeiffer@yahoo.com");
        ui = users.iterator();
        System.out.println("All users with email: ");
        while(ui.hasNext()) {
            System.out.println(ui.next());
        }
        
        if (um.deleteUserbyID(testUser.getUserNumber())) {
            System.out.println("Test user was successfully deleted by id.");
        }
    }

}
