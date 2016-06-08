
package database;

/**
 *
 * @author cjones
 */
public interface DatabaseManagement {
    
    public void CreateTables();
    public void initializeDatabaseManagement();
    public GreenTipManager getGreenTipManager();
    public BuildingManager getBuildingManager();
    public MeterManager getMeterManager();
    public ReadingManager getReadingManager();
    public CampusManager getCampusManager();
    public UniversityManager getUniversityManager();
    public GImageManager getGImageManager();
    public UserManager getUserManager();
    
}
