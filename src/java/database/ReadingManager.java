package database;

import common.ConditionedReading;
import common.RawReading;
import java.time.LocalDateTime;
import java.util.Collection;

/**
 * 
 * @author cmr98507
 */
public interface ReadingManager {
    
    public boolean addConditionedReading(ConditionedReading reading);
    public boolean addConditionedReadings(Collection<ConditionedReading> readings);
    public boolean updateConditionedReading(ConditionedReading reading);
    public boolean deleteConditionedReading(ConditionedReading reading);
    public ConditionedReading getLastConditionedReadingForMeterBeforeDateTime(int meterId, LocalDateTime dateTime);
    public ConditionedReading getConditionedReadingByMeterAtDateTime(int meterId, LocalDateTime dateTime);
    public Collection<ConditionedReading> getAllConditionedReadingsByMeter(int meterId);
    public Collection<ConditionedReading> getAllConditionedReadingsByMeter(int meterId, LocalDateTime start, LocalDateTime end);
    
    public boolean addRawReading(RawReading reading);
    public boolean addRawReadings(Collection<RawReading> readings);
    public boolean deleteRawReading(RawReading reading);
    public RawReading getLastRawReadingForMeterBeforeDateTime(int meterId, LocalDateTime dateTime);
    public RawReading getRawReadingByMeterAtDateTime(int meterId, LocalDateTime dateTime);
    public Collection<RawReading> getAllRawReadingsByMeter(int meterId);
    public Collection<RawReading> getAllRawReadingsByMeter(int meterId, LocalDateTime start, LocalDateTime end);
}
