package database.production;

import common.University;
import database.MYSQL_Helper;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import utilities.ErrorLogger;

/**
 * Done? (needs javadoc + testing)
 *
 * @author cmr98507
 */
public class UniversityManager implements database.UniversityManager {

    public static final String UNIVERSITY_TABLE_NAME = "university";
    private static final String VAR_UNIVERSITY_ID = "universityId";
    private static final String VAR_UNIVERSITY_NAME = "universityName";

    private static final String PREPARED_ADD_UNIVERSITY
            = "insert into " + UNIVERSITY_TABLE_NAME + " (" + VAR_UNIVERSITY_NAME + ") values(?)";

    private static final String PREPARED_DELETE_UNIVERSITY
            = "delete from " + UNIVERSITY_TABLE_NAME + " where " + VAR_UNIVERSITY_ID + "=?";

    private static final String PREPARED_GET_ALL_UNIVERSITIES
            = "select * from " + UNIVERSITY_TABLE_NAME;

    private static final String PREPARED_GET_UNIVERSITY_BY_ID
            = "select * from " + UNIVERSITY_TABLE_NAME + " where " + VAR_UNIVERSITY_ID + "=?";

    private static final String PREPARED_UPDATE_UNIVERSITY
            = "update " + UNIVERSITY_TABLE_NAME + " set " + VAR_UNIVERSITY_NAME + "=? where " + VAR_UNIVERSITY_ID + "=?";
    
    public static final String PREPARED_GET_UNI_BY_NAME 
            = "select * from " + UNIVERSITY_TABLE_NAME + " where " + VAR_UNIVERSITY_NAME + "=?";

    /**
     *
     * @param universityName
     * @return
     */
    @Override
    public University addUniversity(String universityName) {
        if (universityName == null) {
            return null;
        }

        ResultSet results = null;
        try (PreparedStatement adduniversity = MYSQL_Helper.getConnection().prepareStatement(PREPARED_ADD_UNIVERSITY, PreparedStatement.RETURN_GENERATED_KEYS)) {
            adduniversity.setString(1, universityName);
            if (adduniversity.executeUpdate() > 0) {
                results = adduniversity.getGeneratedKeys();
                if (results.next()) {
                    return getUniversityById(results.getInt(1));
                }
            }
        } catch (SQLException ex) {
            ErrorLogger.log(Level.WARNING, "UniversityManager.addUniversity: Unable to prepare statment.");
        } finally {
            MYSQL_Helper.closeResultSet(results);
        }

        return null;
    }

    /**
     *
     * @param university
     * @return
     */
    @Override
    public boolean updateUniversity(University university) {
        if (university == null) {
            return false;
        }
        try (PreparedStatement updateuni = MYSQL_Helper.prepareStatement(PREPARED_UPDATE_UNIVERSITY)) {
            updateuni.setString(1, university.getUniversityName());
            if (updateuni.executeUpdate() > 0) {
                return true;
            }
        } catch (SQLException ex) {
            ErrorLogger.log(Level.WARNING, "UniversityManager.deleteUniversity: Unable to prepare statment.");
        }
        return false;
    }

    /**
     *
     * @param university
     * @return
     */
    @Override
    public boolean deleteUniversity(University university) {
        if (university == null) {
            return false;
        }

        try (PreparedStatement deleteuni = MYSQL_Helper.prepareStatement(PREPARED_DELETE_UNIVERSITY)) {
            deleteuni.setInt(1, university.getUniversityId());
            return deleteuni.executeUpdate() > 0;
        } catch (SQLException ex) {
            ErrorLogger.log(Level.WARNING, "UniversityManager.deleteUniversity: Unable to prepare statment.");
        }
        return false;
    }

    /**
     *
     * @return
     */
    @Override
    public Collection<University> getAllUniversities() {
        Collection<University> unis = new LinkedList<>();
        try (PreparedStatement getalluni = MYSQL_Helper.prepareStatement(PREPARED_GET_ALL_UNIVERSITIES)) {
            ResultSet results = getalluni.executeQuery();
            while (results.next()) {
                int id = results.getInt(VAR_UNIVERSITY_ID);
                String name = results.getString(VAR_UNIVERSITY_NAME);
                unis.add(new University(id, name));
            }
        } catch (SQLException ex) {
            ErrorLogger.log(Level.WARNING, "UniversityManager.getAllUniversities: Unable to prepare statment.");
        }
        return unis;
    }

    /**
     *
     * @param universityId
     * @return
     */
    @Override
    public University getUniversityById(int universityId) {
        ResultSet results = null;
        try (PreparedStatement getuni = MYSQL_Helper.prepareStatement(PREPARED_GET_UNIVERSITY_BY_ID)) {
            getuni.setInt(1, universityId);
            results = getuni.executeQuery();
            if (results.next()) {
                String uniname = results.getString(VAR_UNIVERSITY_NAME);
                return new University(universityId, uniname);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UniversityManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            MYSQL_Helper.closeResultSet(results);
        }
        return null;
    }

    /**
     * 
     * @param universtiyName
     * @return 
     */
    @Override
    public University getUniversityByName(String universtiyName) {
        ResultSet results = null;
        University university = null;
        try (PreparedStatement getuni = MYSQL_Helper.prepareStatement(PREPARED_GET_UNI_BY_NAME)) {
            getuni.setString(1, universtiyName);
            results = getuni.executeQuery();
            if (results.next()) {
                int uniId = results.getInt(VAR_UNIVERSITY_ID);
                return getUniversityById(uniId);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UniversityManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            MYSQL_Helper.closeResultSet(results);
        }
        
        return university;
    }

}
