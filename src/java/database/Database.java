package database;

import utilities.PropertyManager;

/**
 *
 * @author cjones
 */
public  class Database {
    private static DatabaseManagement database=null;
    
        
    public static DatabaseManagement getDatabaseManagement(){
       if(database == null) intialize();
       return database;
   }

    private static void intialize() {
//        if(PropertyManager.isProduction()){
//            database = new database.production.DatabaseManagement();
//        }
//        else 
        database = new database.production.DatabaseManagement();
        
        database.initializeDatabaseManagement();
    }
    
}
