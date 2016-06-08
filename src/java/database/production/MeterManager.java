package database.production;

import common.Building;
import common.Meter;
import database.MYSQL_Helper;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Done? (needs javadoc and testing)
 *
 * @author cws55854
 */
public class MeterManager implements database.MeterManager {

    public static final String METER_TABLE_NAME = "meter";
    public static final String VAR_METER_ID = "meterId",
            VAR_METER_NAME = "meterName",
            VAR_METER_DESC = "meterDescription",
            VAR_BUILDING_ID = "buildingId";

    private static final String PREPARED_ADD_METER
            = "insert into " + METER_TABLE_NAME + " (" + VAR_METER_NAME + ","
            + VAR_METER_DESC + "," + VAR_BUILDING_ID + ") values(?,?,?)";

    private static final String PREPARED_UPDATE_METER
            = "update " + METER_TABLE_NAME + " set " + VAR_METER_NAME + "=?,"
            + VAR_METER_DESC + "=?," + VAR_BUILDING_ID + "=? where " + VAR_METER_ID + "=?";

    private static final String PREPARED_DELETE_METER
            = "delete from " + METER_TABLE_NAME + " where " + VAR_METER_ID + "=?";

    private static final String PREPARED_GET_METER_BY_NAME
            = "select " + VAR_METER_ID + " from " + METER_TABLE_NAME + " where " + VAR_METER_NAME + "=?";

    private static final String PREPARED_GET_METER_BY_ID
            = "select * from " + METER_TABLE_NAME + " where " + VAR_METER_ID + "=?";

    private static final String PREPARED_GET_METERS_BY_BUILDING
            = "select * from " + METER_TABLE_NAME + " where " + VAR_BUILDING_ID + "=?";

    /**
     * Adds a meter to the database with the given name, description, and
     * building identification number.
     *
     * @param name The name of this <code>Meter</code>.
     * @param meterDesc A description of this <code>Meter</code>.
     * @param buildingId The identification number of the building this
     * <code>Meter</code> belongs to.
     * @return A <code>Meter</code> object is returned representing the meter
     * that was added. If an error occurred null is returned.
     */
    @Override
    public Meter addMeter(String name, String meterDesc, int buildingId) {
        ResultSet results = null;
        Meter meter = null;
        try (PreparedStatement addmeter = MYSQL_Helper.getConnection().prepareStatement(PREPARED_ADD_METER, PreparedStatement.RETURN_GENERATED_KEYS)) {
            addmeter.setString(1, name);
            addmeter.setString(2, meterDesc);
            addmeter.setInt(3, buildingId);
            if (addmeter.executeUpdate() > 0) {
                results = addmeter.getGeneratedKeys();
                if (results.next()) {
                    meter = getMeterById(results.getInt(1));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MeterManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            MYSQL_Helper.closeResultSet(results);
        }

        return meter;
    }

    /**
     * Updates the database so that the meter with the same meter identification 
     * number as the given <code>Meter</code> has the same information stored.
     * 
     * @param meter The <code>Meter</code> to be updated in the database.
     * @return True is returned if this <code>Meter</code> was successfully update, 
     * otherwise false is returned. 
     */
    @Override
    public boolean updateMeter(Meter meter) {
        try (PreparedStatement updatemeter = MYSQL_Helper.prepareStatement(PREPARED_UPDATE_METER)) {
            updatemeter.setString(1, meter.getMeterName());
            updatemeter.setString(2, meter.getMeterDesc());
            updatemeter.setInt(3, meter.getBuildingId());
            updatemeter.setInt(4, meter.getMETER_ID());
            if (updatemeter.executeUpdate() > 0) {
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(MeterManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Deletes the meter in the database that has the same meter identification 
     * number as the <code>Meter</code> given.
     * 
     * @param meter The <code>Meter</code> to be deleted from the database.
     * @return True is returned if this <code>Meter</code> was successfully 
     * deleted from the database.
     */
    @Override
    public boolean deleteMeter(Meter meter) {
        try (PreparedStatement deletemeter = MYSQL_Helper.prepareStatement(PREPARED_DELETE_METER)) {
            deletemeter.setInt(1, meter.getMETER_ID());
            if (deletemeter.executeUpdate() > 0) {
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(MeterManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    /**
     * Retrieves a meter from the database that has the given name.
     * 
     * @param name The name of the meter to be found.
     * @return A <code>Meter</code> object containing the information about the
     * meter with the given name.
     */
    @Override
    public Meter getMeterByName(String name) {
        ResultSet results = null;
        Meter meter = null;
        try (PreparedStatement getmeterbyname = MYSQL_Helper.prepareStatement(PREPARED_GET_METER_BY_NAME)) {
            getmeterbyname.setString(1, name);
            results = getmeterbyname.executeQuery();
            if (results.next()) {
                int id = results.getInt(VAR_METER_ID);
                meter = getMeterById(id);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MeterManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            MYSQL_Helper.closeResultSet(results);
        }

        return meter;
    }

    /**
     * Gets the <code>Meter</code> from the database that has the identification number provided.
     * 
     * @param meterId The identification number of the <code>Meter</code> to be found.
     * @return A <code>Meter</code> object containing the information about the 
     * meter with the given identification number.
     */
    @Override
    public Meter getMeterById(int meterId) {
        ResultSet results = null;
        Meter meter = null;

        try (PreparedStatement getmeterbyid = MYSQL_Helper.prepareStatement(PREPARED_GET_METER_BY_ID)) {
            getmeterbyid.setInt(1, meterId);
            results = getmeterbyid.executeQuery();
            if (results.next()) {
                String meterName = results.getString(VAR_METER_NAME);
                String meterDesc = results.getString(VAR_METER_DESC);
                int buildingId = results.getInt(VAR_BUILDING_ID);
                meter = new Meter(meterId, meterName, meterDesc, buildingId);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MeterManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            MYSQL_Helper.closeResultSet(results);
        }

        return meter;
    }

    /**
     * Gets all the <code>Meter</code>s from the database that are associated with 
     * the given <code>Building</code>.
     * 
     * @param building The <code>Building</code> to find <code>Meter</code>s associated with.
     * @return A <code>Collection</code> of <code>Meter</code>s that contains 
     * all the meters associated with the given <code>Building</code> is returned.
     */
    @Override
    public Collection<Meter> getMetersByBuilding(Building building) {
        ResultSet results = null;
        Collection<Meter> meters = new LinkedList<>();

        try (PreparedStatement getMeters = MYSQL_Helper.prepareStatement(PREPARED_GET_METERS_BY_BUILDING)) {
            getMeters.setInt(1, building.getBuildingId());
            results = getMeters.executeQuery();
            while (results.next()) {
                int meterId = results.getInt(VAR_METER_ID);
                String meterName = results.getString(VAR_METER_NAME);
                String meterDesc = results.getString(VAR_METER_DESC);
                int buildingId = results.getInt(VAR_BUILDING_ID);
                meters.add(new Meter(meterId, meterName, meterDesc, buildingId));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MeterManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            MYSQL_Helper.closeResultSet(results);
        }

        return meters;
    }

    /**
     * Gets a list of <code>Meter</code>s in the database that have the given names.
     * 
     * @param meterNames A <code>Collection</code> containing the names of the 
     * meter to be found in the database.
     * @return A <code>Collection</code> of the <code>Meter</code>s with the given
     * names is returned.
     */
    public Collection<Meter> getAllMetersByNames(Collection<String> meterNames) {
        Collection<Meter> meters = new LinkedList<>();

        for (String meterName : meterNames) {
            Meter tempMeter = getMeterByName(meterName);
            if (tempMeter == null) {
                tempMeter = addMeter(meterName, "", 0); // add a default meter
            }
            meters.add(tempMeter);
        }

        return meters;
    }

}
