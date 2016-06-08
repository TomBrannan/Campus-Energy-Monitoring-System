package database.production;

import common.Building;
import common.Campus;
import database.MYSQL_Helper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import utilities.ErrorLogger;

/**
 * Manager for the <code>Building</code>s allowing a user to add, update,
 * remove, and find <code>Building</code>s in the database.
 *
 * @author cmr98507
 */
public class BuildingManager implements database.BuildingManager {

    public static final String BUILDINGS_TABLE_NAME = "buildings";

    private static final String VAR_BUILDING_ID = "buildingId";
    private static final String VAR_BUILDING_NAME = "buildingName";
    private static final String VAR_GIMAGE_ID = "picture";
    private static final String VAR_OCCUPANCY = "occupancy";
    private static final String VAR_SQFTGE = "squareFootage";
    private static final String VAR_VISIBLE = "visible";
    private static final String VAR_CAMPUS_ID = "campusId";

    private static final String PREPARED_ADD_BUILDING
            = "insert into " + BUILDINGS_TABLE_NAME
            + " (" + VAR_BUILDING_NAME + "," + VAR_GIMAGE_ID + "," + VAR_OCCUPANCY
            + "," + VAR_SQFTGE + "," + VAR_CAMPUS_ID + "," + VAR_VISIBLE + ") "
            + "values(?,?,?,?,?,?)";

    private static final String PREPARED_DELETE_BUILDING
            = "delete from " + BUILDINGS_TABLE_NAME + " where " + VAR_BUILDING_ID + "=?";

    private static final String PREPARED_GET_BUILDING_BY_ID
            = "select * from " + BUILDINGS_TABLE_NAME + " where " + VAR_BUILDING_ID + "=?";

    private static final String PREPARED_UPDATE_BUILDING
            = "update " + BUILDINGS_TABLE_NAME + " set " + VAR_BUILDING_NAME + "=?, "
            + VAR_GIMAGE_ID + "=?, " + VAR_OCCUPANCY + "=?, " + VAR_SQFTGE + "=?, "
            + VAR_VISIBLE + "=?, " + VAR_CAMPUS_ID + "=? where " + VAR_BUILDING_ID + "=?";

    private static final String PREPARED_GET_BUILDINGS_BY_CAMPUS
            = "select * from " + BUILDINGS_TABLE_NAME + " where " + VAR_CAMPUS_ID + "=?";

    private static final String PREPARED_GET_VISIBLE_BUILDINGS_BY_CAMPUS
            = "select * from " + BUILDINGS_TABLE_NAME + " where " + VAR_CAMPUS_ID + "=? AND " + VAR_VISIBLE + "= true";

    /**
     * Attempts to add a new building to the database with the given values for
     * name, picture, occupancy, square footage, campus, and visibility.
     *
     * @param buildingName The name of the <code>Building</code> to be added.
     * @param picture The foreign key for the image of this
     * <code>Building</code>.
     * @param occupancy The maximum number of people that can fit in this
     * <code>Building</code>.
     * @param squareFootage The square footage of this <code>Building</code>.
     * @param campusId The id of the <code>Campus</code> this
     * <code>Building</code> belongs to.
     * @param defaultVisible Whether this <code>Building</code> is visible on
     * the website.
     * @return The <code>Building</code> that was added is returned unless the
     * addition fails or no name is provided, then null is returned.
     */
    @Override
    public Building addBuilding(String buildingName, int picture, int occupancy, int squareFootage, int campusId, boolean defaultVisible) {
        Connection conn = MYSQL_Helper.getConnection();
        if (buildingName == null) {
            return null;
        }

        ResultSet results = null;
        try (PreparedStatement addBldg = conn.prepareStatement(PREPARED_ADD_BUILDING, PreparedStatement.RETURN_GENERATED_KEYS)) {

            addBldg.setString(1, buildingName.trim());
            addBldg.setInt(2, picture);
            addBldg.setInt(3, occupancy);
            addBldg.setInt(4, squareFootage);
            addBldg.setInt(5, campusId);
            addBldg.setBoolean(6, defaultVisible);

            if (addBldg.executeUpdate() > 0) {
                results = addBldg.getGeneratedKeys();
                if (results.next()) {
                    return getBuildingByID(results.getInt(1));
                }
            }
        } catch (SQLException ex) {
            ErrorLogger.log(Level.SEVERE, "BuildingManager.addBuilding: Unable to prepare statement.");
        } finally {
            MYSQL_Helper.closeResultSet(results);
            MYSQL_Helper.returnConnection(conn);
        }
        return null;
    }

    /**
     * Deletes the building from the database that has the same building
     * identification number as the <code>Building</code> given.
     *
     * @param building The <code>Building</code> to be deleted from the
     * database.
     * @return True is returned if the <code>Building</code> was successfully
     * deleted. False is returned if the <code>Building</code> provided is null
     * or an error occurred.
     */
    @Override
    public boolean deleteBuilding(Building building) {
        Connection conn = MYSQL_Helper.getConnection();
        if (building == null) {
            return false;
        }

        try (PreparedStatement deletebuilding = conn.prepareStatement(PREPARED_DELETE_BUILDING)) {
            deletebuilding.setInt(1, building.getBuildingId());
            if (deletebuilding.executeUpdate() > 0) {
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(BuildingManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            MYSQL_Helper.returnConnection(conn);
        }
        return false;
    }

    /**
     * Retrieves information from the database about the building with the given
     * buildingID.
     *
     * @param buildingID The identification number of the <code>Building</code>.
     * @return The <code>Building</code> with the given identification number.
     * Null is returned if there is no building with the given id.
     */
    @Override
    public Building getBuildingByID(int buildingID) {
        Connection conn = MYSQL_Helper.getConnection();
        ResultSet results = null;
        Building building = null;
        try (PreparedStatement getbuilding = conn.prepareStatement(PREPARED_GET_BUILDING_BY_ID)) {
            getbuilding.setInt(1, buildingID);
            results = getbuilding.executeQuery();
            if (results.next()) {
                String buildingName = results.getString(VAR_BUILDING_NAME);
                int gimageid = results.getInt(VAR_GIMAGE_ID);
                int buildingOccupancy = results.getInt(VAR_OCCUPANCY);
                int buildingSqft = results.getInt(VAR_SQFTGE);
                int campusid = results.getInt(VAR_CAMPUS_ID);
                boolean buildingVisible = results.getBoolean(VAR_VISIBLE);
                building = new Building(buildingID, buildingName, gimageid, buildingOccupancy, buildingSqft, buildingVisible, campusid);
            }
        } catch (SQLException ex) {
            Logger.getLogger(BuildingManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            MYSQL_Helper.closeResultSet(results);
            MYSQL_Helper.returnConnection(conn);
        }

        return building;
    }

    /**
     * Updates the <code>Building</code> in the database that has the same
     * building identification number, so that it has the same values for all
     * the other fields.
     *
     * @param building The <code>Building</code> to be updated.
     * @return True is returned if the update was completed successfully, false
     * is returned if the updated was not executed.
     */
    @Override
    public boolean updateBuilding(Building building) {
        Connection conn = MYSQL_Helper.getConnection();
        if (building == null) {
            return false;
        }

        try (PreparedStatement updateBldg = conn.prepareStatement(PREPARED_UPDATE_BUILDING)) {
            updateBldg.setString(1, building.getBuildingName().trim());
            updateBldg.setInt(2, building.getgImageId());
            updateBldg.setInt(3, building.getOccupancy());
            updateBldg.setInt(4, building.getSqFootage());
            updateBldg.setBoolean(5, building.getVisible());
            updateBldg.setInt(6, building.getCampusId());
            updateBldg.setInt(7, building.getBuildingId());

            if (updateBldg.executeUpdate() > 0) {
                return true;
            }
        } catch (SQLException ex) {
            ErrorLogger.log(Level.SEVERE, "BuildingManager.updateBuilding: Unable to prepare statment.");
        } finally {
            MYSQL_Helper.returnConnection(conn);
        }
        return false;
    }

    /**
     * Gets a list of all the buildings on a given <code>Campus</code>.
     *
     * @param campus The <code>Campus</code> we want to find buildings on.
     * @return A <code>Collection</code> of the <code>Building</code>s on the
     * given campus. If no buildings are found for this campus the
     * <code>Collection</code> will be empty.
     */
    @Override
    public Collection<Building> getAllBuildingsByCampus(Campus campus) {
        Connection conn = MYSQL_Helper.getConnection();
        Collection<Building> buildings = new LinkedList<>();
        ResultSet results = null;
        try (PreparedStatement getallbuildings = conn.prepareStatement(PREPARED_GET_BUILDINGS_BY_CAMPUS)) {
            getallbuildings.setInt(1, campus.getCampusId());
            results = getallbuildings.executeQuery();
            while (results.next()) {
                int buildingId = results.getInt(VAR_BUILDING_ID);
                String buildingName = results.getString(VAR_BUILDING_NAME);
                int gimageid = results.getInt(VAR_GIMAGE_ID);
                int occupancy = results.getInt(VAR_OCCUPANCY);
                int sqft = results.getInt(VAR_SQFTGE);
                boolean visible = results.getBoolean(VAR_VISIBLE);
                int campusID = results.getInt(VAR_CAMPUS_ID);

                Building building = new Building(buildingId, buildingName, gimageid, occupancy, sqft, visible, campusID);
                buildings.add(building);
            }
        } catch (SQLException ex) {

        } finally {
            MYSQL_Helper.closeResultSet(results);
            MYSQL_Helper.returnConnection(conn);
        }

        return buildings;
    }

    /**
     * Gets a list of all <code>Building</code>s that should be visible on the
     * website. Being visible means that guests can view graphs with information
     * about the building.
     *
     * @return A <code>Collection</code> of <code>Buildings</code> That are
     * visible.
     */
    @Override
    public Collection<Building> getAllVisibleBuildingsByCampus(Campus campus) {
        Connection conn = MYSQL_Helper.getConnection();
        Collection<Building> buildings = new LinkedList<>();
        ResultSet results = null;
        try (PreparedStatement getvisiblebuildings = conn.prepareStatement(PREPARED_GET_VISIBLE_BUILDINGS_BY_CAMPUS)) {
            getvisiblebuildings.setInt(1, campus.getCampusId());
            results = getvisiblebuildings.executeQuery();
            while (results.next()) {
                int buildingId = results.getInt(VAR_BUILDING_ID);
                String buildingName = results.getString(VAR_BUILDING_NAME);
                int gimageid = results.getInt(VAR_GIMAGE_ID);
                int occupancy = results.getInt(VAR_OCCUPANCY);
                int sqft = results.getInt(VAR_SQFTGE);
                boolean visible = results.getBoolean(VAR_VISIBLE);
                int campusID = results.getInt(VAR_CAMPUS_ID);

                Building building = new Building(buildingId, buildingName, gimageid, occupancy, sqft, visible, campusID);
                buildings.add(building);
            }
        } catch (SQLException ex) {
            ErrorLogger.log(Level.SEVERE, "BuildingManager.getVisibleBuildings: Unable to prepare statment.");
        } finally {
            MYSQL_Helper.closeResultSet(results);
            MYSQL_Helper.returnConnection(conn);
        }

        return buildings;
    }

    /**
     * Get list of all visible metered buildings on the given Campus. A visible
     * building is a building which has it's visible property set to true. A
     * metered building is a building which has a meter assigned to that
     * building.
     *
     * @param campus The campus to search for visible metered buildings on
     * @return A list of all visible metered buildings.
     */
    @Override
    public Collection<Building> getAllVisibleMeteredBuildingsByCampus(Campus campus) {
        Collection<Building> visibleMeteredBuildings = new LinkedList<>();
        Collection<Building> meteredBuildings = new LinkedList<>();
        // Check for null
        if (campus == null) {
            return visibleMeteredBuildings;
        }

        meteredBuildings.addAll(getAllMeteredBuildingsByCampus(campus));
        for(Building building : meteredBuildings) {
            if (building.getVisible()) {
                visibleMeteredBuildings.add(building);
            }
        }
        return visibleMeteredBuildings;
    }

    /**
     * Get list of all visible metered buildings on the given University. A
     * visible building is a building which has it's visible property set to
     * true. A metered building is a building which has a meter assigned to that
     * building.
     *
     * @param universityID The id of the university to get visible metered
     * buildings
     * @return A list of all visible metered buildings.
     */
    @Override
    public Collection<Building> getAllVisibleMeteredBuildingsByUniversity(int universityID) {
        Collection<Building> visibleMeteredBuildings = new LinkedList<>();
        database.CampusManager cm = database.Database.getDatabaseManagement().getCampusManager();
        Collection<Campus> campuses = cm.getAllCampusInUniversity(universityID);
        for (Campus c : campuses) {
            visibleMeteredBuildings.addAll(getAllVisibleMeteredBuildingsByCampus(c));
        }
        return visibleMeteredBuildings;
    }

    @Override
    public Collection<Building> getAllMeteredBuildingsByCampus(Campus campus) {
        Collection<Building> meteredBuildings = new LinkedList<>();
        Collection<Building> buildings = new LinkedList<>();
        // Check for null
        if (campus == null) {
            return meteredBuildings;
        }

        // Get managers
        database.MeterManager mm = database.Database.getDatabaseManagement().getMeterManager();

        // Get all visible buildings belonging to the given campus
        buildings.addAll(getAllBuildingsByCampus(campus));

        // Go through the buildings 
        for (Building b : buildings) {
            // Get the meters by builiding (not null), check to see if the list contains any meters
            if (!mm.getMetersByBuilding(b).isEmpty()) {
                meteredBuildings.add(b);
            }
        }

        return meteredBuildings;
    }

    @Override
    public Collection<Building> getAllMeteredBuildingsByUniversity(int universityID) {
        Collection<Building> visibleMeteredBuildings = new LinkedList<>();
        database.CampusManager cm = database.Database.getDatabaseManagement().getCampusManager();
        Collection<Campus> campuses = cm.getAllCampusInUniversity(universityID);
        for (Campus c : campuses) {
            visibleMeteredBuildings.addAll(getAllMeteredBuildingsByCampus(c));
        }
        return visibleMeteredBuildings;
    }

}
