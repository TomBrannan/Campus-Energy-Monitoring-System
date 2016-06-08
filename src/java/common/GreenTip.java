package common;

/**
 * Represents a green tip. Green tips are displayed on the website to give
 * students different ideas on how they can save energy and make decisions
 * that are not harmful to the environment.
 * 
 * @author cmr98507
 */
public class GreenTip{
    private int greenTipId;
    private String tipText;
    private int gimageId;

    /**
     * This class represents a green tip, which has text and a picture. Green tips
     * are helpful hints on how to live more 'green'.
     */
    public GreenTip() {
    }
    
    /**
     * Creates a new <code>GreenTip</code> given an identification number, string
     * representing the text of the tip, and the identification number of the image
     * associated with this tip.
     * 
     * @param greenTipId The identification number of the tip in the database.
     * @param tipText The text of the tip.
     * @param gimageId The identification number of the image associated with this tip.
     */
    public GreenTip(int greenTipId, String tipText, int gimageId)
    {
        this.greenTipId = greenTipId;
        this.tipText = tipText;
        this.gimageId = gimageId;
    }

    /**
     * Gets the identification number of this <code>GreenTip</code>.
     * 
     * @return The identification number associated with this green tip.
     */
    public int getId() {
        return greenTipId;
    }

    /**
     * Assigns the given value as the identification number of this <code>GreenTip</code>.
     * 
     * @param greenTipId The new identification number for this <code>GreenTip</code>.
     */
    public void setId(int greenTipId) {
        this.greenTipId = greenTipId;
    }

    /**
     * Gets the text of this <code>GreenTip</code>. 
     * 
     * @return A <code>String</code> representing the text associated with this <code>GreenTip</code>.
     */
    public String getTipText() {
        return tipText;
    }

    /**
     * Assigns a new value to the <code>tipText</code>. Changes the text associated
     * with this <code>GreenTip</code>. Can be used to fix spelling errors, grammar, etc.
     * 
     * @param tipText The new next to assign to this <code>GreenTip</code>.
     */
    public void setTipText(String tipText) {
        this.tipText = tipText;
    }

    /**
     * Gets the identification number of this <code>GreenTip</code>. Each green 
     * tip will have a unique identification number in the database.
     * 
     * @return The identification number associated with this <code>GreenTip</code>.
     */
    public int getGreenTipId() {
        return greenTipId;
    }

    /**
     * Gets the identification number of the picture that is associated with this 
     * <code>GreenTip</code>. This number is a unique identifier for the pictures 
     * in the images table in the database. Image identification number 1 refers to 
     * the null image.
     * 
     * @return The identification number of the image associated with this <code>GreenTip</code>.
     */
    public int getGimageId() {
        return gimageId;
    }

    /**
     * Assigns the given value as the new identification number for this <code>GreenTip</code>.
     * The identification number uniquely identifies this <code>GreenTip</code> in the database.
     * 
     * @param greenTipId The new identification number for this <code>GreenTip</code>.
     */
    public void setGreenTipId(int greenTipId) {
        this.greenTipId = greenTipId;
    }

    /**
     * Assigns the given value as the new identification number for the <code>GImage</code>
     * associated with this <code>GreenTip</code>. This identification number uniquely 
     * identifies this <code>GImage</code> in the database.
     * 
     * @param gimageId 
     */
    public void setGImageId(int gimageId) {
        this.gimageId = gimageId;
    }

    /**
     * Converts this <code>GreenTip</code> to a <code>String</code> so that it 
     * can be printed to the screen, etc.
     * 
     * @return A <code>String</code> representation of this <code>GreenTip</code>.
     */
    @Override
    public String toString() {
        return "GreenTip[ID(" + greenTipId + ") TEXT(" + tipText + ") GIMGID(" + gimageId + ")]";
    }
    
}
