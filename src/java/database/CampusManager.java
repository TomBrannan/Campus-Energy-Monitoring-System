package database;

import common.Building;
import common.Campus;
import java.util.Collection;

/**
 * 
 * @author cmr98507
 */
public interface CampusManager {
    
    public Campus addCampus(String campusName, int gimageId, int universityId);
    public boolean updateCampus(Campus campus);
    public boolean deleteCampus(Campus campus);
    public Campus getCampusById(int campusId);
    public Collection<Campus> getAllCampusInUniversity(int UnivId);
    
}
