package common;

import java.io.Serializable;

/**
 * Represents a building on a campus. A building has an identification 
 * number, name, picture, occupancy, size, and belongs to a campus. The 
 * visibility of a building determines if the building can be seen 
 * on the website. 
 * 
 * @author cmr98507
 */
public class Building implements Serializable{
    private int buildingId;
    private String buildingName;
    private int gImageId;
    private int occupancy;
    private int sqFootage;
    private boolean visible;
    private int campusId;

    
    /**
     * Creates a <code>Building</code> with the following building 
     * identification number, name, image, occupancy, square footage, 
     * visibility, and campus identification number.
     * 
     * @param buildingId The identification number that uniquely identifies this <code>Building</code>.
     * @param buildingName The name of this <code>Building</code>.
     * @param gImageId An image of this <code>Building</code>.
     * @param occupancy The number of people this <code>Building</code> can safely support.
     * @param sqFootage The square footage of this <code>Building</code>.
     * @param visible Represents the visibility of this <code>Building</code> on the website.
     * @param campusId The identification number of the <code>Campus</code> this <code>Building</code> is on.
     */
    public Building(int buildingId, String buildingName, int gImageId, int occupancy, int sqFootage, boolean visible, int campusId) {
        this.buildingId = buildingId;
        this.buildingName = buildingName;
        this.gImageId = gImageId;
        this.occupancy = occupancy;
        this.sqFootage = sqFootage;
        this.visible = visible;
        this.campusId = campusId;
    }

    /**
     * Returns the name of this <code>Building</code>.
     * 
     * @return A <code> String </code> representing the name of this 
     * <code>Building</code> (e.g. "Hartline Science Center").
     */
    public String getBuildingName() {
        return buildingName;
    }

    /**
     * Returns the area of this <code>Building</code>, in square feet.
     * 
     * @return The square footage of this <code>Building</code>.
     */
    public int getSqFootage() {
        return sqFootage;
    }

    /**
     * Sets the name of this <code>Building</code>.
     * 
     * @param buildingName The new name for this <code>Building</code>.
     */
    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    /**
     * Sets the area of this <code>Building</code>.
     * 
     * @param sqFootage The new square footage for this <code>Building</code>.
     */
    public void setSqFootage(int sqFootage) {
        this.sqFootage = sqFootage;
    }

    /**
     * Returns the identification number for this <code>Building</code>. 
     * This number uniquely identifies each building and is used as 
     * the primary key in the database. 
     * 
     * @return The unique identification number for this <code>Building</code>.
     */
    public int getBuildingId() {
        return buildingId;
    }

    /**
     * Sets the building identification number for this <code>Building</code>.
     * 
     * @param buildingId The new identification number for this <code>Building</code>.
     */
    public void setBuildingId(int buildingId) {
        this.buildingId = buildingId;
    }

    public int getgImageId() {
        return gImageId;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setgImageId(int gImageId) {
        this.gImageId = gImageId;
    }


    /**
     * Returns the maximum occupancy of this <code>Building</code>.
     * 
     * @return The maximum number of people this <code>Building</code> can safely support.
     */
    public int getOccupancy() {
        return occupancy;
    }

    /**
     * Sets the maximum occupancy for this <code>Building</code>.
     * 
     * @param occupancy The new maximum occupancy for this <code>Building</code>.
     */
    public void setOccupancy(int occupancy) {
        this.occupancy = occupancy;
    }

    /**
     * Returns whether or not this <code>Building</code> is visible on the website.
     * 
     * @return True if this <code>Building</code> is currently visible on the website,
     * false otherwise.
     */
    public boolean getVisible() {
        return visible;
    }

    /**
     * Sets the visibility of this <code>Building</code>.
     * 
     * @param visible The new visibility setting for this <code>Building</code>.
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Returns the (unique) identification number of the <code>Campus</code> associated
     * with this <code>Building</code>.
     * 
     * @return The identification number of the <code>Campus</code> that this <code>Building</code>
     * belongs to.
     */
    public int getCampusId() {
        return campusId;
    }

    /**
     * Sets the (unique) identification number of the <code>Campus</code> associated
     * with this <code>Building</code>.
     * 
     * @param campusId The new identification number of the <code>Campus</code> that
     * this <code>Building</code> belongs to.
     */
    public void setCampusId(int campusId) {
        this.campusId = campusId;
    }

    /**
     * Converts this <code>Building</code> to its <code>String</code> representation which is the name 
     * of the building. (eg. "Hartline Science Center")
     * 
     * @return A <code>String</code> that represents this <code>Building</code>.
     */
    @Override
    public String toString() {
        return buildingName;
    }
    
    /**
     * Overrides the equals method. Compares an object to this <code>Building</code>. 
     * Returns true only if the object's building identification number is 
     * equal to this <code>buildingId</code>.
     * 
     * @param obj The <code>Building</code> object to compare to.
     * @return True is returned if both <code>Building</code>s have the same <code>buildingId</code>
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Building other = (Building) obj;
        if (this.buildingId != other.buildingId) {
            return false;
        }
        return true;
    }   
}
