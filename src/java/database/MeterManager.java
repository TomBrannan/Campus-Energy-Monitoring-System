package database;

import common.Building;
import common.Meter;
import java.util.Collection;

/**
 *
 * @author cmr98507
 */
public interface MeterManager {
    
    public Meter addMeter(String name, String meterDesc, int buildingId); 
    public boolean updateMeter(Meter meter);
    public boolean deleteMeter(Meter meter);
    public Meter getMeterByName(String name);
    public Meter getMeterById(int meterId);
    public Collection<Meter> getMetersByBuilding(Building building);
    public Collection<Meter> getAllMetersByNames(Collection<String> meterNames);
    
}
