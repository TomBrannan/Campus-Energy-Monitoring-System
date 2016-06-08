package database.production;

import database.UserManager;

/**
 * Represents a database manager that implements the <code>DatabaseManagement</code> interface.
 *
 * @author cmr98507
 */
public class DatabaseManagement implements database.DatabaseManagement{
    
    private  final database.BuildingManager buildingManager = new database.production.BuildingManager();
    private  final database.CampusManager campusManager = new database.production.CampusManager();
    private  final database.GImageManager gImageManager = new database.production.GImageManager();
    private  final database.GreenTipManager greenTipManager = new database.production.GreenTipManager();
    private  final database.MeterManager meterManager = new database.production.MeterManager();
    private  final database.ReadingManager readingManager = new database.production.ReadingManager();
    private  final database.UniversityManager universityManager = new database.production.UniversityManager();
    private  final database.UserManager userManager = new database.production.UserManager();
    
    @Override
    public void initializeDatabaseManagement() {        
    }
    
    @Override
    public void CreateTables() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public database.GreenTipManager getGreenTipManager() {
        return greenTipManager;
    }

    @Override
    public database.BuildingManager getBuildingManager() {
        return buildingManager;
    }

    @Override
    public database.MeterManager getMeterManager() {
        return meterManager;
    }

    @Override
    public database.ReadingManager getReadingManager() {
        return readingManager;
    }

    @Override
    public database.CampusManager getCampusManager() {
        return campusManager;
    }

    @Override
    public database.UniversityManager getUniversityManager() {
        return universityManager;
    }

    @Override
    public database.GImageManager getGImageManager() {
        return gImageManager;
    }

    @Override
    public UserManager getUserManager() {
        return userManager;
    }
    
}
