package common;

import java.time.LocalDateTime;
import utilities.LocalDateTimeUtilities;

/**
 * Abstract representation of an individual energy reading.  
 * @author tnb65846
 */
public abstract class Reading implements Comparable<Reading> {

    public static final double UNDEFINED_DELTA_TIME = -1;
    public static final double UNDEFINED_KWH = -1;

    protected int meterId;
    protected LocalDateTime dateTime;
    protected double value;

    /**
     * @return The <code>value</code> for this <code>Reading</code>
     */
    public double getValue() {
        return value;
    }

    /**
     * Set the <code>value</code> for this <code>Reading</code>
     * @param value The new <code>value</code>
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * @return The <code>meterId</code> for this <code>Reading</code>
     */
    public int getMeterId() {
        return meterId;
    }

    /**
     * @return The <code>dateTime</code> for this <code>Reading</code>
     */
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    @Override
    public String toString() {
        return "READING[ meterId(" + meterId + "), value(" + value + "), timestamp(" + LocalDateTimeUtilities.getISODateTimeString(dateTime) + ") ]";
    }

    @Override
    public int compareTo(Reading o) {
        return getDateTime().compareTo(o.getDateTime());
    }
    
}
