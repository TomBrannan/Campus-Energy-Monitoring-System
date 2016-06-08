package common;

/**
 * Represents a meter that is located in a specific building. Meter's produce
 * readings with information about the energy used.
 * 
 * @author cmr98507
 */
public class Meter {
    
    private final int METER_ID; 
    private String meterName;
    private String meterDesc;
    private int buildingId;
    
    /**
     * Creates a new <code>Meter</code> object with the provided identification
     * number, name, and building.
     * 
     * @param meterId Primary key in our database that identifies this <code>Meter</code>.
     * @param meterName The name associated with this <code>Meter</code>.
     * @param meterDesc The description associated with this <code>Meter</code>
     * @param buildingId The identification number of the <code>Building</code> that this <code>Meter</code> is in.
     */
    public Meter(int meterId, String meterName, String meterDesc, int buildingId)
    {
        this.METER_ID = meterId;
        this.meterName = meterName;
        this.buildingId = buildingId;
    }

    /**
     * Returns the identification number for this <code>Meter</code>. The 
     * <code>METER_ID</code> is unique for each meter and used as the 
     * primary key in the database.
     * 
     * @return The identification number for this <code>Meter</code>.
     */
    public int getMETER_ID() {
        return METER_ID;
    }

    /**
     * Gets the name associated with this <code>Meter</code>. 
     * 
     * @return The name of this <code>Meter</code>.
     */
    public String getMeterName() {
        return meterName;
    }

    /**
     * Returns the identification number of the <code>Building</code> this 
     * meter is located in on campus.
     * 
     * @return The identification number of the <code>Building</code> this meter is in.
     */
    public int getBuildingId() {
        return buildingId;
    }

    public String getMeterDesc() {
        return meterDesc;
    }

    public void setMeterName(String meterName) {
        this.meterName = meterName;
    }

    public void setMeterDesc(String meterDesc) {
        this.meterDesc = meterDesc;
    }

    public void setBuildingId(int buildingId) {
        this.buildingId = buildingId;
    }
    
    
    
}
