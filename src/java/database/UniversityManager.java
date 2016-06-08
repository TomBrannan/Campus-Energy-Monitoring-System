package database;

import common.Campus;
import common.University;
import java.util.Collection;

/**
 *
 * @author cmr98507
 */
public interface UniversityManager {
    
    public University addUniversity(String universityName);
    public boolean updateUniversity(University university);
    public boolean deleteUniversity(University university);
    public University getUniversityById(int universityId);
    public University getUniversityByName(String universtiyName);
    public Collection<University> getAllUniversities();
    
}
