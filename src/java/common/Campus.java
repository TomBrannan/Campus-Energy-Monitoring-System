package common;

import java.io.Serializable;

/**
 * Represents a campus that belongs to a university and has one or more
 * buildings associated with it. Each campus has a unique identification 
 * number, name, and image.
 * 
 * @author cmr98507
 */
public class Campus implements Serializable{
    private int campusId;
    private String campusName;
    private int universityId;

    /**
     * Constructs a <code>Campus</code> with the given identification number,
     * name, image, and university identification number. The campus 
     * identification number is used a primary key in the database and the 
     * university identification number is a foreign key.
     * 
     * @param campusId The identification number that uniquely identifies this <code>Campus</code>.
     * @param campusName The name of this <code>Campus</code>.
     * @param universityId The identification number of the <code>University</code> this <code>Campus</code> belongs to.
     */
    public Campus(int campusId, String campusName, int universityId) {
        this.campusId = campusId;
        this.campusName = campusName;
        this.universityId = universityId;
    }

    /**
     * Returns the identification number for this <code>Campus</code>. 
     * This number uniquely identifies each campus and is used as 
     * the primary key in the database. 
     * 
     * @return The unique identification number for this <code>Campus</code>.
     */
    public int getCampusId() {
        return campusId;
    }

    /**
     * Sets the campus identification number for this <code>Campus</code>.
     * 
     * @param campusId The new identification number for this <code>Campus</code>.
     */
    public void setCampusId(int campusId) {
        this.campusId = campusId;
    }

    /**
     * Returns the name of this <code>Campus</code>.
     * 
     * @return A <code>String</code> representing the name of this 
     * <code>Campus</code> (e.g. "Lower Campus").
     */
    public String getCampusName() {
        return campusName;
    }

    /**
     * Sets the name of this <code>Campus</code>.
     * 
     * @param campusName The new name for this <code>Campus</code>.
     */
    public void setCampusName(String campusName) {
        this.campusName = campusName;
    }

    /**
     * Returns the (unique) identification number of the <code>University</code> associated
     * with this <code>Campus</code>.
     * 
     * @return The identification number of the <code>University</code> that this <code>Campus</code>
     * belongs to.
     */
    public int getUniversityId() {
        return universityId;
    }

    /**
     * Sets the (unique) identification number of the <code>University</code> associated
     * with this <code>Campus</code>.
     * 
     * @param universityId The new identification number of the <code>University</code> that
     * this <code>Campus</code> belongs to.
     */
    public void setUniversityId(int universityId) {
        this.universityId = universityId;
    }

    /**
     * Overrides the equals method. Compares an object to this <code>Campus</code>. 
     * Returns true only if the object's building identification number is 
     * equal to this <code>campusId</code>.
     * 
     * @param obj The <code>Campus</code> object to compare to.
     * @return True is returned if both <code>Campus</code>s have the same <code>campusId</code>
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Campus other = (Campus) obj;
        if (this.campusId != other.campusId) {
            return false;
        }
        return true;
    }

    /**
     * Converts this <code>Campus</code> to its <code>String</code> representation which is the name 
     * of the building. (eg. "Lower Campus")
     * 
     * @return A <code>String</code> that represents this <code>Campus</code>.
     */
    @Override
    public String toString() {
        return campusName;
    }
    
    
}
