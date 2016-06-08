package database.production;

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

/**
 * Done?
 * A manager for the <code>Campus</code>es. Allows a user to easily add, update, 
 * remove and find <code>Building</code>es in the database.
 * 
 * @author cmr98507
 */
public class CampusManager implements database.CampusManager { 
    
    public static final String CAMPUS_TABLE_NAME = "campus";
    
    private static final String VAR_CAMPUS_ID = "campusId",
                                VAR_CAMPUS_NAME = "campusName",
                                VAR_UNIVERSITY_ID = "universityId";
    
     private static final String PREPARED_ADD_CAMPUS
            = "insert into " + CAMPUS_TABLE_NAME + " (" + VAR_CAMPUS_NAME + "," + VAR_UNIVERSITY_ID + ") values(?,?)";
     
     private static final String PREPARED_GET_CAMPUS_BY_ID 
            = "select * from " + CAMPUS_TABLE_NAME + " where " + VAR_CAMPUS_ID + "=?";
     
     private static final String PREPARED_UPDATE_CAMPUS
            = "update " + CAMPUS_TABLE_NAME + " set " + VAR_CAMPUS_NAME + "=?," + VAR_UNIVERSITY_ID + "=? where " + VAR_CAMPUS_ID + "=?";
     
     private static final String PREPARED_DELETE_CAMPUS
            = "delete from " + CAMPUS_TABLE_NAME + " where " + VAR_CAMPUS_ID + "=?";
     
     private static final String PREPARED_GET_CAMPUSES_FOR_UNI
             = "select * from " + CAMPUS_TABLE_NAME + " where " + VAR_UNIVERSITY_ID + "=?";

     /**
      * Tries to add a new campus to the database with the given name, identification 
      * number, and university. 
      * 
      * @param campusName The name of the campus being added.
      * @param gimageId The identification number for the image of this campus.
      * @param universityId The identification number of the university this camus belongs to.
      * @return A <code>Campus</code> is returned representing the campus that was
      * added to the database. Null is returned if the campus isn't added.
      */
    @Override
    public Campus addCampus(String campusName, int gimageId, int universityId) {
        Connection conn = MYSQL_Helper.getConnection();
        ResultSet keys = null;
        try (PreparedStatement addcampus = conn.prepareStatement(PREPARED_ADD_CAMPUS, PreparedStatement.RETURN_GENERATED_KEYS))
        {
            addcampus.setString(1, campusName);
            addcampus.setInt(2, universityId);
            if (addcampus.executeUpdate() > 0) {
                keys = addcampus.getGeneratedKeys();
                if (keys.next()) {
                    return getCampusById(keys.getInt(1));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CampusManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            MYSQL_Helper.closeResultSet(keys);
            MYSQL_Helper.returnConnection(conn);
        }
        
        return null;
    }

    /**
     * Updates the database so that the campus in the database with the same 
     * identification number as the <code>Campus</code> that was given has the 
     * same information.
     * 
     * @param campus The <code>Campus</code> to update.
     * @return True is returned if the campus was updated. False is returned 
     * if the given <code>Campus</code> is null or an error occurred.
     */
    @Override
    public boolean updateCampus(Campus campus) {
        Connection conn = MYSQL_Helper.getConnection();
        if (campus == null) {
            return false;
        }
        try (PreparedStatement updatecampus = conn.prepareStatement(PREPARED_UPDATE_CAMPUS)) {
            updatecampus.setString(1, campus.getCampusName());
            updatecampus.setInt(2, campus.getUniversityId());
            updatecampus.setInt(3, campus.getCampusId());
            if (updatecampus.executeUpdate() > 0) {
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(CampusManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            MYSQL_Helper.returnConnection(conn);
        }
        
        return false;
    }

    /**
     * Tries to delete the campus from the database with the same campus 
     * identification number as the <code>Campus</code> given.
     * 
     * @param campus The <code>Campus</code> to delete from the database.
     * @return True is returned if this <code>Campus</code> was successfully deleted,
     * otherwise false is returned.
     */
    @Override
    public boolean deleteCampus(Campus campus) {
        Connection conn = MYSQL_Helper.getConnection();
        if (campus == null) {
            return false;
        }
        
        try (PreparedStatement deletecampus = conn.prepareStatement(PREPARED_DELETE_CAMPUS)) {
            deletecampus.setInt(1, campus.getCampusId());
            if (deletecampus.executeUpdate() > 0) {
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(CampusManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            MYSQL_Helper.returnConnection(conn);
        }
        
        return false;
    }

    /**
     * Gets the <code>Campus</code> from the database that has the given identification number.
     * 
     * @param campusId The identification number of the <code>Campus</code> to be found.
     * @return Returns a <code>Campus</code> object containing information from the 
     * database for the building with the given identification number.
     */
    @Override
    public Campus getCampusById(int campusId) {
        Connection conn = MYSQL_Helper.getConnection();
        ResultSet results = null;
        try (PreparedStatement getcampus = conn.prepareStatement(PREPARED_GET_CAMPUS_BY_ID)) {
            getcampus.setInt(1, campusId);
            results = getcampus.executeQuery();
            if (results.next()) {
                String campusName = results.getString(VAR_CAMPUS_NAME);
                int universityId = results.getInt(VAR_UNIVERSITY_ID);
                return new Campus(campusId, campusName, universityId);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CampusManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            MYSQL_Helper.closeResultSet(results);
            MYSQL_Helper.returnConnection(conn);
        }
        return null;
    }

    /**
     * Gets all the buildings from the database that are associated with the given
     * University identification number.
     * 
     * @param UnivId The identification number of the university you want to find campuses associated with.
     * @return A <code>Collection</code> of the <code>Campus</code>es of the given university.
     */
    @Override
    public Collection<Campus> getAllCampusInUniversity(int UnivId) {
        Connection conn = MYSQL_Helper.getConnection();
        Collection<Campus> campuses = new LinkedList<>();
        ResultSet results = null;
        try (PreparedStatement getallcampuses = conn.prepareStatement(PREPARED_GET_CAMPUSES_FOR_UNI)) {
            getallcampuses.setInt(1, UnivId);
            results = getallcampuses.executeQuery();
            while(results.next()) {
                int campusId = results.getInt(VAR_CAMPUS_ID);
                String campusName = results.getString(VAR_CAMPUS_NAME);
                int uniId = results.getInt(VAR_UNIVERSITY_ID);
                
                campuses.add(new Campus(campusId, campusName, uniId));
            }
        } catch (SQLException ex) {
            Logger.getLogger(CampusManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            MYSQL_Helper.closeResultSet(results);
            MYSQL_Helper.returnConnection(conn);
        }
        
        return campuses;
    }

     
    
    
}