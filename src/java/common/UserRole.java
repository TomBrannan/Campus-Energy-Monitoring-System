
package common;

import java.io.Serializable;
import utilities.Debug;

/**
 * LIST WHAT EACH USER CAN DO IN SYSTEM __ SEE ADVISOR
 * Represents the role with which a <code> User </code> in this system. Different
 * users have different access levels and different levels of functionality and
 * responsibility within the system.  
 * 
 * @author cjones
 */
public enum UserRole implements Serializable{
    
    /**
     * The administrator for the entire program, has complete control of the
     * system. 
     */
    SystemAdmin("SystemAdmin"),
    
    /**
     * A user that works at the university. <code> Faculty </code> can downdload 
     * past data from the site.
     */
    Faculty("Faculty"),
    
    /**
     * The default account, has very limited access.
     */
    Guest("Guest");
    
    private final String roleName;
    
    /**
     * Constructs a <code> UserRole </code> given a role name. A user role
     * represents their position in the college and does not require them to be
     * a member of the <code> Faculty </code>.
     * 
     * @param roleName The name of this <code> UserRole </code> position.
     */
    UserRole(String roleName){
        this.roleName=roleName.trim();
    }
    
    /**
     * Returns the <code> UserRole </code> of the given role name. If the role name
     * given is not one of the roles in the enumerated type, the default is set to "Guest."
     * 
     * @param roleName The name of this role, preferably one already in the enumerated type
     * (SystemAdmin or Administrator).
     * 
     * @return The <code> UserRole </code> corresponding to the role name given.
     */
     public static UserRole getUserRole (String roleName) {
       UserRole name = Guest; // default is guest 

        for (UserRole e : UserRole.values ()) {
            if (e.roleName.equalsIgnoreCase(roleName.trim())){
                name = e;
                break;
            }
        }
        return (name);
    }

     /**
     * Returns the role name of this <code> UserRole </code>.
     * 
     * @return This <code> UserRole </code>'s name.
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * Returns this <code> UserRole </code>'s name as a string.
     * 
     * @return The name of this <code> UserRole </code> as a string.
     */
    @Override
    public String toString() {
        return "UserRole{" + "roleName=" + roleName + '}';
    }
    
    /**
     * Tests the <code>UserRole</code> class. Reviews the toString(), 
     * getRoleName(), getEnum(), and UserRole().
     * 
     * @param args Command line arguments.
     */
    public static void main (String args[])
    {
        Debug.println(getUserRole("administrator"));
        Debug.println(getUserRole("A").toString());
        
        Debug.println(SystemAdmin.getRoleName());
        Debug.println(Faculty.getRoleName());
        Debug.println(Guest.getRoleName());
        
        Debug.println(getUserRole(SystemAdmin.getRoleName()));
        Debug.println(getUserRole(Faculty.getRoleName()));
        Debug.println(getUserRole(Guest.getRoleName()));
        
        
        Debug.println();
    }
}
