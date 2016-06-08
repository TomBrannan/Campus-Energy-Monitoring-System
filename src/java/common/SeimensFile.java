/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import java.io.File;
import java.time.LocalDateTime;

/**
 * Representation of a file received from Seimens that is stored in the database.
 * Includes the files name, contents, dateAdded, and whether it has been processed.
 * 
 * @author lap60658
 */
public class SeimensFile {
    
    private int fileNumber;
    private String fileName;
    private File fileContents;
    private LocalDateTime dateAdded;
    private boolean processed;

    /**
     * Creates a new <code>SeimensFile</code> instance with default values for attributes.
     */
    public SeimensFile() {
    }

    /**
     * Constructs a new instance of <code>SeimensFile</code> given the file's 
     * number, name, contents, date added, and whether it was processed.
     * 
     * @param fileNumber The number that identifies this <code>SeimensFile</code> in the database.
     * @param fileName The name of this <code>SeimensFile</code>.
     * @param fileContents The file this <code>SeimensFile</code> refers to.
     * @param dateAdded The date this <code>SeimensFile</code> was added to the database.
     * @param processed Whether or not this <code>SeimensFile</code> has been processed.
     */
    public SeimensFile(int fileNumber, String fileName, File fileContents, LocalDateTime dateAdded, boolean processed) {
        this.fileNumber = fileNumber;
        this.fileName = fileName;
        this.fileContents = fileContents;
        this.dateAdded = dateAdded;
        this.processed = processed;
    }

    /**
     * Returns the identification number of this <code>SeimensFile</code>. This 
     * number is used as the primary key in the database.
     * 
     * @return The identification number of this <code>SeimensFile</code>.
     */
    public int getFileNumber() {
        return fileNumber;
    }

    /**
     * Sets the identification number of this <code>SeimensFile</code>. 
     * 
     * @param fileNumber The number used to identify this <code>SeimensFile</code>.
     */
    public void setFileNumber(int fileNumber) {
        this.fileNumber = fileNumber;
    }

    /**
     * Retrieves the name of this <code>SeimensFile</code>.
     * 
     * @return The name of this <code>SeimensFile</code> in a <code>String</code>.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Assigns the given <code>String</code> as the new name of this <code>SeimensFile</code>.
     * 
     * @param fileName The <code>String</code> form of this <code>SeimensFile</code>'s name.
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Gets the <code>File</code> containing the contents of the file described by this <code>SeimensFile</code>.
     * 
     * @return The <code>File</code> storing the contents of this <code>SeimensFile</code>.
     */
    public File getFileContents() {
        return fileContents;
    }

    /**
     * Assigns the provided <code>File</code> as the contents of this <code>SeimensFile</code>.
     * 
     * @param fileContents A <code>File</code> that includes the contents of this <code>SeimensFile</code>.
     */
    public void setFileContents(File fileContents) {
        this.fileContents = fileContents;
    }

    /**
     * Returns that date that this <code>SeimensFile</code> was added to the database.
     * 
     * @return A <code>LocakDateTime</code> of the date this <code>SeimensFile</code> was put in the database.
     */
    public LocalDateTime getDateAdded() {
        return dateAdded;
    }

    /**
     * Sets the date that this <code>SeimensFile</code> was added to the database.
     * 
     * @param dateAdded The <code>LocalDateTime</code> that this <code>SeimensFile</code> was added to the database.
     */
    public void setDateAdded(LocalDateTime dateAdded) {
        this.dateAdded = dateAdded;
    }

    /**
     * Returns whether or not this <code>SeimensFile</code> has been processed. 
     * 
     * @return True if this <code>SeimensFile</code> was process or false if it has not
     */
    public boolean isProcessed() {
        return processed;
    }

    /**
     * Sets whether or not this <code>SeimensFile</code> has been processed yet. 
     * 
     * @param processed A boolean value representing whether or not this <code>SeimensFile</code> was processed.
     */
    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    /**
     * Returns the hash value of this <code>SeimensFile</code> file number.
     * 
     * @return The hash value of this <code>SeimensFile</code> file number.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.fileNumber;
        return hash;
    }

    /**
     * Determines whether or not the given object is equal to this <code>SeimensFile</code>.
     * <code>SeimensFile</code>s are only equal if they have the same <code>fileNumber</code>
     * 
     * @param obj The <code>Object</code> to compare this <code>SeimensFile</code> to.
     * @return True if they are equal, false if they are not.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SeimensFile other = (SeimensFile) obj;
        if (this.fileNumber != other.fileNumber) {
            return false;
        }
        return true;
    }
    
    
}
