package common;

import java.time.LocalDateTime;

/**
 *
 * @author cmr98507
 */
public class ConditionedReading extends Reading {

    private double kwh;
    private double deltaTime;

    /**
     *
     * @param meterId
     * @param dateTime
     * @param value
     */
    public ConditionedReading(int meterId, LocalDateTime dateTime, double value, double kwh, double deltaTime) {
        this.meterId = meterId;
        this.dateTime = dateTime;
        this.value = value;
        this.kwh = kwh;
        this.deltaTime = deltaTime;
    }

    /**
     *
     * @return
     */
    public double getKwh() {
        return kwh;
    }

    /**
     *
     * @return
     */
    public double getDeltaTime() {
        return deltaTime;
    }

    /**
     *
     * @param kwh
     */
    public void setKwh(double kwh) {
        this.kwh = kwh;
    }

    /**
     *
     * @param deltaTime
     */
    public void setDeltaTime(double deltaTime) {
        this.deltaTime = deltaTime;
    }

    @Override
    public String toString() {
        return super.toString() + " kwh(" + getKwh() + "), deltaTime(" + getDeltaTime() + ")";
    }
    
    

}
