
package common;

import java.io.Serializable;
import java.time.LocalDateTime;
import utilities.Debug;

/**
 * Representation of a generic user of the of the Green Power website.
 * 
 * @author cjones
 */
public class User implements Comparable<User>, Serializable  {
    private int userNumber; //Primary key in our database --auto created
    private String loginName;
    private String userPassword;
    private String lastName;  
    private String firstName;
    private String emailAddress; 
    private UserRole userRole; 
    private LocalDateTime lastLogin;//time and date of last login
    private int loginCount; // how many logins 

    /**
     * Constructs an empty <code>User</code>.
     */
    public User() {
    }

    /**
     * Constructs a new <code>User</code> with given properties such as a user number, login name, 
     * password, last name, first name, email address, 
     * user role, last login, and login count.
     * 
     * @param userNumber A number that represents this <code>User</code>. This number is the primary key
     * in our database and is auto incremented.
     * 
     * @param loginName The name that this <code>User</code> uses to login to the system.
     * 
     * @param password The password that this <code>User</code> uses to login to the system.
     * 
     * @param lastName The last name of this <code>User</code>.
     * 
     * @param firstName The first name of this <code>User</code>.
     * 
     * @param emailAddress The university email of this <code>User</code>.
     * 
     * @param userRole The <code>UserRole</code> of this <code>User</code>. This sets which privileges 
     * user has. Determines which screens and controls the user will have access to.
     * 
     * @param lastLogin The last time this <code>User</code> logged in.
     * 
     * @param loginCount The number of attempted logins by this <code>User</code>. If the user 
     * fails three times to login, the account will be locked until a system
     * administrator unlocks it. 
     */
    public User(int userNumber, String loginName, String password, String lastName, String firstName, String emailAddress, 
            UserRole userRole, LocalDateTime lastLogin, int loginCount) {
        this.userNumber = userNumber;
        this.loginName = loginName;
        this.userPassword = password;
        this.lastName = lastName;
        this.firstName = firstName;
        this.emailAddress = emailAddress;
        this.userRole = userRole;
        this.lastLogin = lastLogin;
        this.loginCount = loginCount;
    }

    /**
     * Gets the email address of this <code>User</code>.
     * 
     * @return The email address of this <code>User</code>. If there is no email address
     * null is returned.
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Sets the email address for this <code>User</code>.
     * No error checking is performed. 
     * 
     * @param emailAddress The email address that is set for this <code>User</code>.
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * Gets the user number of this <code>User</code>. This is the unique identifier for this
     * user.
     * 
     * @return The user number of this <code>User</code>. If there is no user number null is returned. 
     */
    public int getUserNumber() {
        return userNumber;
    }

    /**
     * Sets the user number for this <code>User</code>.
     * No error checking is performed.
     * 
     * @param userNumber The user number that is set for this <code>User</code>. 
     */
    public void setUserNumber(int userNumber) {
        this.userNumber = userNumber;
    }

    /**
     * Gets the last name of this <code>User</code>.
     * 
     * @return The last name of this <code>User</code>. If no last name exists null is returned.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of this <code>User</code>.
     * No error checking is performed.
     * 
     * @param lastName The last name that is set to this <code>User</code>. 
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the first name of this <code>User</code>.
     * 
     * @return The first name of this <code>User</code>. If no first name exists null is returned. 
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of this <code>User</code>.
     * No error checking is performed.
     * 
     * @param firstName The first name that is set to this <code>User</code>. 
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the <code>UserRole</code> for this <code>User</code>. This consists of
     * an enumeration of whether the student is a SystemAdmin, Administrator, or Guest.
     * 
     * @return The <code>UserRole</code> for this <code>User</code>. If no <code>UserRole</code> is
     * set null is returned.
     */
    public UserRole getUserRole() {
        return userRole;
    }

    /**
     * Sets the <code> UserRole </code> for this <code>User</code>.
     * No error checking is performed.
     * 
     * @param userRole The <code>UserRole</code> that is set to this <code>User</code>.
     */
    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    /**
     * Gets the login name of this <code>User</code>.
     * 
     * @return The login name of this <code>User</code>. If there is no login name null is returned.
     */
    public String getLoginName() {
        return loginName;
    }

    /**
     * Sets the login name of this <code>User</code>. 
     * No error checking is performed. 
     * 
     * @param loginName The login name that is set for this <code>User</code>.
     */
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    /**
     * Gets the password for the login of this <code>User</code>.
     * 
     * @return The password for this <code>User</code>. If no password exists null is returned. 
     */
    public String getUserPassword() {
        return userPassword;
    }

    /**
     * Sets the password for the login of this <code>User</code>.
     * No error checking is performed.
     * 
     * @param userPassword The password that is set for this <code>User</code>. 
     */
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    /**
     * Gets the last time this <code>User</code> logged in.
     * 
     * @return The last time this <code>User</code> logged in. If no last time logged in exists null is returned. 
     */
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    /**
     * Sets the last time this <code>User</code> logged in. This will be set right when the user
     * logs into the system.
     * 
     * @param lastLogin The last time this <code>User</code> logged in.
     */
    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    /**
     * Gets the number of log in attempts from this <code>User</code>. Only failed attempts count
     * and this gets reset when user logins successfully. 
     * 
     * @return The number of failed login attempts by this <code>User</code>. 
     */
    public int getLoginCount() {
        return loginCount;
    }

    /**
     * Sets the number of failed login attempts for this <code>User</code> since 
     * their last successful login.
     * No error checking is performed.
     * 
     * @param loginCount The number of failed attempts for this <code>User</code>.
     */
    public void setLoginCount(int loginCount) {
        this.loginCount = loginCount;
    }
    
    /**
     * Returns the hash value of user number of this <code>User</code>.
     * 
     * @return The hash value of this <code>User</code>'s user number. 
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.userNumber;
        return hash;
    }

    /**
     * Overrides the equals method to compare two <code>User</code>s.
     * If the object parameter is not a <code>User</code> or is null return false.
     * Otherwise, if the <code>User</code> is equal to this <code>User</code> based
     * on the value of their <code>userNumber</code>, return true.
     * 
     * @param obj The <code>User</code> object to be compared to.
     * @return True is returned if both <code>userNumbers</code>s are equal. False is 
     * returned if <code>userNumber</code>s are different, the object is not an 
     * <code>User</code>, or the object is null.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
       
        if (getClass() != obj.getClass()) return false;
    
        final User other = (User) obj;
        return this.userNumber == other.userNumber;
    }

    /**
     * Overrides the compareTo method to sort <code>User</code>s based on their
     * <code>lastName</code> and then <code>firstName</code>, returning the order 
     * of a <code>User</code> compared to this <code>User</code>.
     * 
     * @param o The <code>User</code> to compare to this <code>User</code>.
     * @return The order of the given <code>User</code> relative to this <code>User</code>.
     * Positive for before, zero for equal, and negative for after.
     */
    @Override
    public int compareTo(User o) {
        int order = lastName.compareToIgnoreCase(o.lastName);
        if(order !=0) return order;
        order = firstName.compareToIgnoreCase(o.firstName);
        return order;
    }

    @Override
    public String toString() {
        return "User{" + "userNumber=" + userNumber + ", loginName=" + loginName + 
                ", userPassword=" + userPassword + ", lastName=" + lastName +
                ", firstName=" + firstName + ", emailAddress=" + emailAddress + ", userRole=" + 
                userRole + ", lastLogin=" + lastLogin + ", loginCount=" + loginCount + '}';
    }
    
    /**
     * Tests the <code>User</code> class. Reviews the setFirstName(), setLastName(),
     * setEmailAddress(), setAcademicOrganization(), setLoginName(), setLastLogin(),
     * setLoginCount(), setUserPassword(), setUserRole(), toString(), compareTo(),
     * equals(), hashCode(), getFirstName(), getLastName(), getEmailAddress(), 
     * getAcademicOrganization(), getLoginName(), getLastLogin(), getLoginCount(),
     * getUserPassword(), getUserRole(), and User().
     * 
     * @param args Command line arguments.
     */
    public static void main(String args[]){
        User u1 = new User();
        u1.setFirstName("Dan");
        u1.setLastName("James");
        u1.setEmailAddress("drj@drj.com");
        u1.setLoginName("drj");
        u1.setLastLogin(LocalDateTime.now());
        u1.setLoginCount(30);
        u1.setUserNumber(007);
        u1.setUserPassword("123abc");
        u1.setUserRole(UserRole.SystemAdmin);
        
        User u2 = new User(8, "dtp", "mypasswordisbetter", "Pany", "Dan", "dtp@dtp.com", 
            UserRole.SystemAdmin, LocalDateTime.now(), 9001);
        
        Debug.println("FNAME: " +u1.getFirstName());
        Debug.println("LNAME: " +u1.getLastName());
        Debug.println("EMAIL: " +u1.getEmailAddress());
        Debug.println("USERNUMBER: " +u1.getUserNumber());
        Debug.println("LOGINNAME: " +u1.getLoginName());
        Debug.println("PASSWORD: " +u1.getUserPassword());
        Debug.println("USERROLE: " +u1.getUserRole());
        Debug.println("LAST LOGIN: " +u1.getLastLogin());
        Debug.println("LOGIN COUNT: " +u1.getLoginCount());
        
        Debug.println();
        Debug.println("FNAME: " +u2.getFirstName());
        Debug.println("LNAME: " +u2.getLastName());
        Debug.println("EMAIL: " +u2.getEmailAddress());
        Debug.println("USERNUMBER: " +u2.getUserNumber());
        Debug.println("LOGINNAME: " +u2.getLoginName());
        Debug.println("PASSWORD: " +u2.getUserPassword());
        Debug.println("USERROLE: " +u2.getUserRole());
        Debug.println("LAST LOGIN: " +u2.getLastLogin());
        Debug.println("LOGIN COUNT: " +u2.getLoginCount());
        
        Debug.println();
        Debug.println(u1.equals(u1));
        Debug.println(u1.equals(u2));
        Debug.println(u1.compareTo(u1));
        Debug.println(u1.compareTo(u2));
        Debug.println(u2.compareTo(u1));
        
        Debug.println();
        Debug.println(u1.hashCode());
        Debug.println(u2.hashCode());
    }
}
