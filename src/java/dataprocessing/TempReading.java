package dataprocessing;

import java.time.LocalDateTime;

/**
 * 
 * @author cmr98507
 */
public class TempReading {
    
    private final String meterName;
    private final LocalDateTime dateTime;
    private double value;
    
    public TempReading(String meterName, LocalDateTime dateTime, double value)
    {
        this.meterName = meterName;
        this.dateTime = dateTime;
        this.value = value;
    }
    
    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getMeterName() {
        return meterName;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }
    
}
