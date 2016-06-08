package common;

import java.time.LocalDateTime;

/**
 * Represents a raw <code>Reading</code> (which has not yet been conditioned)
 * @author tnb65846
 */
public class RawReading extends Reading {

    public RawReading(int meterId, LocalDateTime dateTime, double value) {
        this.meterId = meterId;
        this.dateTime = dateTime;
        this.value = value;
    }
}
