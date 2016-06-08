package database;

import common.Building;
import common.Campus;
import java.util.Collection;

/**
 * 
 * @author cmr98507
 */
public interface BuildingManager {
    
    public Building addBuilding(String buildingName, int picture, int occupancy, int squareFootage, int campusId, boolean defaultVisible); 
    public boolean updateBuilding(Building building);
    public boolean deleteBuilding(Building building);
    public Building getBuildingByID(int buildingID);
    public Collection<Building> getAllBuildingsByCampus(Campus campus);
    public Collection<Building> getAllVisibleBuildingsByCampus(Campus campus);
    public Collection<Building> getAllMeteredBuildingsByCampus(Campus campus);
    public Collection<Building> getAllMeteredBuildingsByUniversity(int universityID);
    public Collection<Building> getAllVisibleMeteredBuildingsByCampus(Campus campus);
    public Collection<Building> getAllVisibleMeteredBuildingsByUniversity(int universityID);
    
}
