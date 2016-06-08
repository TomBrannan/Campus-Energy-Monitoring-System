package common;

import java.awt.Image;

/**
 * Represents an image in the database. A <code>GImage</code> has an identification 
 * number, image, and a boolean which designates whether it is associated with a 
 * <code>GreenTip</code> or not. 
 * 
 * @author cws55854
 */
public class GImage{
    
    private int id;
    private Image image;
    private boolean forGreenTip;
    
    /**
     * Constructs a new <code>GImage</code> instance given the 
     * GImage's identification number, image, and boolean designating whether it is 
     * for a <code>GreenTip</code> or not.
     * 
     * @param id The identification number that uniquely identifies this <code>GImage</code>.
     * @param image The image that represents this <code>GImage</GImage>
     * @param forGreenTip Designates whether the <code>GImage</code> is associated with <code>GreenTip</code>
     */
    public GImage(int id, Image image, boolean forGreenTip)
    {
        this.id = id;
        this.image = image;
        this.forGreenTip = forGreenTip;
    }

    /**
     * Returns the identification number for this <code>GImage</code>. 
     * This number uniquely identifies each building and is used as 
     * the primary key in the database. 
     * 
     * @return The unique identification number for this <code>GImage</code>.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the image associated with this <code>GImage</code>. 
     * 
     * @return The image for this <code>GImage</code>.
     */
    public Image getImage() {
        return image;
    }

    /**
     * Returns a boolean to distinguish between images that are for a <code>GreenTip</code>.
     * A true boolean means that this <code>GImage</code> is for a <code>GreenTip</code>.
     * 
     * @return The boolean identifier for this <code>GImage</code>.
     */
    public boolean isForGreenTip() {
        return forGreenTip;
    }

    /**
     * Sets the building identification number for this <code>GImage</code>.
     * 
     * @param id The new identification number for this <code>GImage</code>.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets or changes the <code>Image</Image> assocciated with this <code>GImage</code>
     * 
     * @param image The image to be set to this <code>GImage</code>
     */
    public void setImage(Image image) {
        this.image = image;
    }

    /**
     * Sets whether if this <code>GImage</code> is associated with a <code>GreenTip</code>
     * or not.
     * 
     * @param forGreenTip True if this <code>GImage</code> is associated with a <code>GreenTip</code>,
     * and false otherwise.
     */
    public void setForGreenTip(boolean forGreenTip) {
        this.forGreenTip = forGreenTip;
    }
    
    /**
     * Converts this <code>GImage</code> to its <code>String</code> representation which is the id,
     * the image's <code>String</code> representation and the boolean, that designates if it is associated
     * with a <code>GreenTip</code> or not.
     * 
     * @return A <code>String</code> that represents this <code>GImage</code>.
     */
    @Override
    public String toString() {
        return "id(" + id + "), image(" + image + "), forGreenTip(" + forGreenTip + ")";
    }
    
}
