/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import common.User;
import java.util.Collection;

/**
 *
 * @author lap60658
 */
public interface UserManager {
    public User validateUser(String loginName, String password);
    public User addUser(User user); 
    public User updateUser(User user);
    public boolean deleteUser(User user);
    public User getUserByID(int userID); 
    public User getUserByLoginName(String loginName );
    public Collection<User> getAllUsersWithEmailAddress(String emailAddress);
    public Collection<User> getAllUsers();
    public boolean deleteUserbyID(int  userID);
}
