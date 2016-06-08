package database.production;

import common.ConditionedReading;
import common.RawReading;
import database.MYSQL_Helper;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import utilities.Debug;
import utilities.ErrorLogger;
import utilities.LocalDateTimeUtilities;
import utilities.PropertyManager;

/**
 *
 * @author cmr98507
 */
public class ReadingManager implements database.ReadingManager {

    public static final String CONDITIONED_READING_TABLE_NAME = "condReadings";
    public static final String RAW_READING_TABLE_NAME = "rawReadings";

    public static final String VAR_COND_READING_TIME = "readingTime",
            VAR_COND_METER_ID = "meterId",
            VAR_COND_READING_VALUE = "energyValue",
            VAR_COND_DELTA_ENERGY = "deltaEnergy",
            VAR_COND_DELTA_TIME = "deltaTime";
    public static final String VAR_RAW_READING_TIME = "readingTime",
            VAR_RAW_METER_ID = "meterId",
            VAR_RAW_READING_VALUE = "energyValue";

    // Time Frame Variables
    public static final String SQL_VAR_BEGIN_DATE = "@start",
            SQL_VAR_END_DATE = "@end";
    public static final String PREPARED_INIT_TIME_FRAME_VARS
            = "set " + SQL_VAR_BEGIN_DATE + " = cast(? as datetime), " + SQL_VAR_END_DATE + " = cast(? as datetime)";

    // Conditioned Readings
    private static final String PREPARED_ADD_COND_READING
            = "insert into " + CONDITIONED_READING_TABLE_NAME + " values (?,?,?,?,?)";

    private static final String PREPARED_GET_COND_READINGS_BY_METER
            = "select * from " + CONDITIONED_READING_TABLE_NAME + " where " + VAR_COND_METER_ID + " =?";

    private static final String PREPARED_GET_COND_READINGS_BY_METER_TIME_FRAME
            = "select * from " + CONDITIONED_READING_TABLE_NAME + " where " + VAR_COND_METER_ID + " =? and cast(" + VAR_COND_READING_TIME + " as datetime) >= " + SQL_VAR_BEGIN_DATE + " and cast(" + VAR_RAW_READING_TIME + " as datetime) <= " + SQL_VAR_END_DATE;

    private static final String PREPARED_UPDATE_CONDITIONED_READING
            = "update " + CONDITIONED_READING_TABLE_NAME + " set " + VAR_COND_READING_VALUE + "=?, " + VAR_COND_DELTA_TIME + "=?, " + VAR_COND_DELTA_ENERGY + "=? where " + VAR_COND_METER_ID + "=? and " + VAR_COND_READING_TIME + "=?";

    private static final String PREPARED_DELETE_CONDITIONED_READING
            = "delete from " + CONDITIONED_READING_TABLE_NAME + " where " + VAR_COND_READING_TIME + "=? and " + VAR_COND_METER_ID + "=?";

    public static final String PREPARED_GET_LAST_COND_READING_FOR_METER
            = "select max(cast(" + VAR_COND_READING_TIME + " as dateTime)) as DATE from " + CONDITIONED_READING_TABLE_NAME + " where " + VAR_COND_METER_ID + "=? and cast(" + VAR_COND_READING_TIME + " as dateTime) < cast(? as dateTime)";

    public static final String PREPARED_GET_CONDITIONED_READING_BY_METER_AT_DATETIME
            = "select * from " + CONDITIONED_READING_TABLE_NAME + " where " + VAR_COND_METER_ID + "=? and " + VAR_COND_READING_TIME + "=?";
    
    // Raw Readings
    public static final String PREPARED_ADD_RAW_READING
            = "insert into " + RAW_READING_TABLE_NAME + " values(?,?,?)";

    public static final String PREPARED_DELETE_RAW_READING
            = "delete from " + RAW_READING_TABLE_NAME + " where " + VAR_RAW_READING_TIME + "=? and " + VAR_RAW_METER_ID + "=? and " + VAR_RAW_READING_VALUE + "=?";

    public static final String PREPARED_GET_RAW_READINGS_BY_METER
            = "select * from " + RAW_READING_TABLE_NAME + " where " + VAR_RAW_METER_ID + "=?";

    public static final String PREPARED_GET_RAW_READINGS_BY_METER_TIME_FRAME
            = "select * from " + RAW_READING_TABLE_NAME + " where " + VAR_RAW_METER_ID + "=? and cast(" + VAR_RAW_READING_TIME + " as datetime) >= " + SQL_VAR_BEGIN_DATE + " and cast(" + VAR_RAW_READING_TIME + " as datetime) <= " + SQL_VAR_END_DATE;

    public static final String PREPARED_GET_LAST_RAW_READING_FOR_METER
            = "select max(cast(" + VAR_RAW_READING_TIME + " as dateTime)) as DATE from " + RAW_READING_TABLE_NAME + " where " + VAR_RAW_METER_ID + "=? and cast(" + VAR_RAW_READING_TIME + " as dateTime) < cast(? as dateTime)";

    public static final String PREPARED_GET_RAW_READING_BY_METER_AT_DATETIME
            = "select * from " + RAW_READING_TABLE_NAME + " where " + VAR_RAW_METER_ID + "=? and " + VAR_RAW_READING_TIME + "=?";

    private boolean initTimeFrameVars(LocalDateTime start, LocalDateTime end) {
        boolean success = false;
        PreparedStatement ps = null;
        
        if (start == null || end == null)
        {
            return success;
        }
        
        String startDateString = LocalDateTimeUtilities.getISODateTimeString(start);
        String endDateString = LocalDateTimeUtilities.getISODateTimeString(end);

        try {
            ps = MYSQL_Helper.prepareStatement(PREPARED_INIT_TIME_FRAME_VARS);
            ps.setString(1, startDateString);
            ps.setString(2, endDateString);
            ps.execute();
            success = true;
        } catch (SQLException ex) {
            ErrorLogger.log(Level.WARNING, "ReadingManager.initTimeFrameVars: Unable to initialize time bound variables.", ex);
        } finally {
            MYSQL_Helper.closePreparedStatement(ps);
        }

        return success;
    }

    /**
     * Get the raw reading for a given meter at a given datetime.
     *
     * @param meterId The ID of the meter
     * @param dateTime The time at which the reading was taken
     * @return The RawReading for the given meter at the given datetime. If no
     * such RawReading exists, a null pointer is returned.
     */
    @Override
    public RawReading getRawReadingByMeterAtDateTime(int meterId, LocalDateTime dateTime) {
        ResultSet results = null;
        RawReading rawReading = null;

        if (dateTime == null) {
            return rawReading;
        }

        try (PreparedStatement getRawReading = MYSQL_Helper.prepareStatement(PREPARED_GET_RAW_READING_BY_METER_AT_DATETIME)) {
            getRawReading.setInt(1, meterId);
            getRawReading.setString(2, LocalDateTimeUtilities.getISODateTimeString(dateTime));
            results = getRawReading.executeQuery();
            if (results.next()) {
                double reading = results.getDouble(VAR_RAW_READING_VALUE);
                rawReading = new RawReading(meterId, dateTime, reading);
            }
        } catch (SQLException ex) {
            ErrorLogger.log(Level.WARNING, "ReadingManager.getRawReadingByMeterAtDateTime: Unable to prepare statement.", ex);
        } finally {
            MYSQL_Helper.closeResultSet(results);
        }

        return rawReading;
    }

    /*
     select cast('2010-03-01T01:01:01' as datetime) <-- ISO time format
     */
    @Override
    public boolean addConditionedReading(ConditionedReading reading) {
        boolean successful;
        try (PreparedStatement addcondreading = MYSQL_Helper.prepareStatement(PREPARED_ADD_COND_READING)) {
            String readingDateTime = LocalDateTimeUtilities.getISODateTimeString(reading.getDateTime());

            addcondreading.setString(1, readingDateTime);
            addcondreading.setInt(2, reading.getMeterId());
            addcondreading.setDouble(3, reading.getValue());
            addcondreading.setDouble(4, reading.getKwh());
            addcondreading.setDouble(5, reading.getDeltaTime());

            successful = (addcondreading.executeUpdate() > 0);
        } catch (SQLException ex) {
            ErrorLogger.log(Level.WARNING, "ReadingManager.addConditionedReading: Unable to add conditioned reading:(" + reading + ")");
            ErrorLogger.log(Level.WARNING, "ReadingManager.addConditionedReading: Attempting to update conditioned reading(" + reading + ")");
            successful = updateConditionedReading(reading);
        }

        return successful;
    }

    @Override
    public boolean addConditionedReadings(Collection<ConditionedReading> readings) {
        for (ConditionedReading reading : readings) {
            addConditionedReading(reading);
        }
        return true;
    }

    /**
     * Update the given Conditioned Reading in the database, uniquely identified
     * by it's meter ID and timestamp value.
     *
     * @param reading
     * @return
     */
    @Override
    public boolean updateConditionedReading(ConditionedReading reading) {
        boolean successful = false;
        try (PreparedStatement updateReading = MYSQL_Helper.prepareStatement(PREPARED_UPDATE_CONDITIONED_READING)) {
            updateReading.setDouble(1, reading.getValue());
            updateReading.setDouble(2, reading.getDeltaTime());
            updateReading.setDouble(3, reading.getKwh());
            updateReading.setInt(4, reading.getMeterId());
            updateReading.setString(5, LocalDateTimeUtilities.getISODateTimeString(reading.getDateTime()));
            successful = (updateReading.executeUpdate() > 0);
        } catch (SQLException ex) {
            ErrorLogger.log(Level.WARNING, "ReadingManager.updateConditionedReading: Unable to update conditioned reading:(" + reading + ").", ex);
        }

        return successful;
    }

    @Override
    public boolean deleteConditionedReading(ConditionedReading reading) {
        boolean successful = false;
        try (PreparedStatement deleteReading = MYSQL_Helper.prepareStatement(PREPARED_DELETE_CONDITIONED_READING)) {
            deleteReading.setString(1, LocalDateTimeUtilities.getISODateTimeString(reading.getDateTime()));
            deleteReading.setInt(2, reading.getMeterId());
            successful = (deleteReading.executeUpdate() > 0);
        } catch (SQLException ex) {
            ErrorLogger.log(Level.WARNING, "ReadingManager.deleteConditionedReading: Unable to delete conditioned reading:(" + reading + ").", ex);
        }
        return successful;
    }

    @Override
    public Collection<ConditionedReading> getAllConditionedReadingsByMeter(int meterId) {
        ResultSet results = null;
        Collection<ConditionedReading> conditionedReadings = new LinkedList<>();

        String readingTime;
        double readingValue;
        LocalDateTime ldt;
        double deltaKwh;
        double deltaTime;

        try (PreparedStatement getcondreadings = MYSQL_Helper.prepareStatement(PREPARED_GET_COND_READINGS_BY_METER)) {
            getcondreadings.setInt(1, meterId);
            results = getcondreadings.executeQuery();

            while (results.next()) {
                readingTime = results.getString(VAR_COND_READING_TIME);
                ldt = LocalDateTimeUtilities.parseISODateTimeString(readingTime);
                readingValue = results.getDouble(VAR_COND_READING_VALUE);
                deltaKwh = results.getDouble(VAR_COND_DELTA_ENERGY);
                deltaTime = results.getDouble(VAR_COND_DELTA_TIME);

                conditionedReadings.add(new ConditionedReading(meterId, ldt, readingValue, deltaKwh, deltaTime));
            }
        } catch (SQLException ex) {
            ErrorLogger.log(Level.WARNING, "ReadingManager.getAllConditionedReadingsByMeter: Unable to prepare statement.", ex);
        } finally {
            MYSQL_Helper.closeResultSet(results);
        }

        return conditionedReadings;
    }

    @Override
    public Collection<ConditionedReading> getAllConditionedReadingsByMeter(int meterId, LocalDateTime start, LocalDateTime end) {
        // Initialize the time frame
        initTimeFrameVars(start, end);

        ResultSet results = null;
        Collection<ConditionedReading> conditionedReadings = new LinkedList<>();

        String readingTime;
        double readingValue;
        LocalDateTime ldt;
        double deltaKwh;
        double deltaTime;

        try (PreparedStatement getcondreadings = MYSQL_Helper.prepareStatement(PREPARED_GET_COND_READINGS_BY_METER_TIME_FRAME)) {
            getcondreadings.setInt(1, meterId);
            results = getcondreadings.executeQuery();

            while (results.next()) {
                readingTime = results.getString(VAR_COND_READING_TIME);
                ldt = LocalDateTimeUtilities.parseISODateTimeString(readingTime);
                readingValue = results.getDouble(VAR_COND_READING_VALUE);
                deltaKwh = results.getDouble(VAR_COND_DELTA_ENERGY);
                deltaTime = results.getDouble(VAR_COND_DELTA_TIME);

                conditionedReadings.add(new ConditionedReading(meterId, ldt, readingValue, deltaKwh, deltaTime));
            }
        } catch (SQLException ex) {
            ErrorLogger.log(Level.WARNING, "ReadingManager.getAllConditionedReadingsByMeter(Time Frame): Unable to prepare statement.", ex);
        } finally {
            MYSQL_Helper.closeResultSet(results);
        }

        return conditionedReadings;
    }

    @Override
    public boolean addRawReading(RawReading reading) {
        try (PreparedStatement addrawreading = MYSQL_Helper.prepareStatement(PREPARED_ADD_RAW_READING)) {
            String readingDateTime = LocalDateTimeUtilities.getISODateTimeString(reading.getDateTime());

            addrawreading.setString(1, readingDateTime);
            addrawreading.setInt(2, reading.getMeterId());
            addrawreading.setDouble(3, reading.getValue());

            if (addrawreading.executeUpdate() > 0) {
                return true;
            }
        } catch (SQLException ex) {
            ErrorLogger.log(Level.WARNING, "ReadingManager.addRawReading: Unable to add conditioned reading:(" + reading + ")");
        }

        return false;
    }

    @Override
    public boolean addRawReadings(Collection<RawReading> readings) {
        for (RawReading reading : readings) {
            boolean added = addRawReading(reading);
            Debug.println("ReadingManager.addRawReadings: " + added + " " + reading);
        }
        return true;
    }

    @Override
    public boolean deleteRawReading(RawReading reading) {
        try (PreparedStatement addrawreading = MYSQL_Helper.prepareStatement(PREPARED_DELETE_RAW_READING)) {
            String readingDateTime = LocalDateTimeUtilities.getISODateTimeString(reading.getDateTime());

            addrawreading.setString(1, readingDateTime);
            addrawreading.setInt(2, reading.getMeterId());
            addrawreading.setDouble(3, reading.getValue());

            if (addrawreading.executeUpdate() > 0) {
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReadingManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    @Override
    public Collection<RawReading> getAllRawReadingsByMeter(int meterId) {
        ResultSet results = null;
        Collection<RawReading> readings = new LinkedList<>();

        String readingTime;
        double readingValue;
        LocalDateTime ldt;

        try (PreparedStatement getrawreadings = MYSQL_Helper.prepareStatement(PREPARED_GET_RAW_READINGS_BY_METER)) {
            getrawreadings.setInt(1, meterId);
            results = getrawreadings.executeQuery();

            // Load results into RawReadings
            while (results.next()) {
                readingTime = results.getString(VAR_RAW_READING_TIME);
                readingValue = results.getDouble(VAR_RAW_READING_VALUE);
                ldt = LocalDateTimeUtilities.parseISODateTimeString(readingTime);

                readings.add(new RawReading(meterId, ldt, readingValue));
            }

        } catch (SQLException ex) {
            ErrorLogger.log(Level.WARNING, "ReadingManager.getAllRawReadingsByMeter unable to prepare statement.", ex);
        } finally {
            MYSQL_Helper.closeResultSet(results);
        }

        return readings;
    }

    @Override
    public Collection<RawReading> getAllRawReadingsByMeter(int meterId, LocalDateTime start, LocalDateTime end) {
        ResultSet results = null;
        Collection<RawReading> rawReadings = new LinkedList<>();

        initTimeFrameVars(start, end);

        try (PreparedStatement getrawreadings = MYSQL_Helper.prepareStatement(PREPARED_GET_RAW_READINGS_BY_METER_TIME_FRAME)){
            getrawreadings.setInt(1, meterId);
            results = getrawreadings.executeQuery();

            while (results.next()) {
                int meterid = results.getInt(VAR_RAW_METER_ID);
                LocalDateTime readingTime = LocalDateTimeUtilities.parseISODateTimeString(results.getString(VAR_RAW_READING_TIME));
                double meterReading = results.getDouble(VAR_RAW_READING_VALUE);
                rawReadings.add(new RawReading(meterid, readingTime, meterReading));
            }

        } catch (SQLException ex) {
            ErrorLogger.log(Level.WARNING, "ReadingManager.getAllRawReadingsByMeter unable to prepare statement.", ex);
        } finally {
            MYSQL_Helper.closeResultSet(results);
        }
        return rawReadings;
    }

    @Override
    public RawReading getLastRawReadingForMeterBeforeDateTime(int meterId, LocalDateTime dateTime) {
        ResultSet results = null;
        RawReading lastReading = null;
        try (PreparedStatement getlastreading = MYSQL_Helper.prepareStatement(PREPARED_GET_LAST_RAW_READING_FOR_METER)) {
            getlastreading.setInt(1, meterId);
            getlastreading.setString(2, LocalDateTimeUtilities.getISODateTimeString(dateTime));
            results = getlastreading.executeQuery();
            if (results.next()) {
                Timestamp stamp_lastDateTime = results.getTimestamp("DATE");
                if (stamp_lastDateTime != null) {
                    LocalDateTime lastDateTime = stamp_lastDateTime.toLocalDateTime();
                    if (lastDateTime != null) {
                        lastReading = getRawReadingByMeterAtDateTime(meterId, lastDateTime);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReadingManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            MYSQL_Helper.closeResultSet(results);
        }

        return lastReading;
    }
    
    /**
     * Get the last condition reading associated with the given 
     * meter identification number from the database before the given date/time
     * .
     * @param meterId The identification number of the meter to find a reading for.
     * @param dateTime The <code>LocalDateTime</code> you want to find the last reading before.
     * @return The last conditioned reading in the database that occurred before the given time.
     */
    @Override
    public ConditionedReading getLastConditionedReadingForMeterBeforeDateTime(int meterId, LocalDateTime dateTime) {
        ResultSet results = null;
        ConditionedReading lastReading = null;
        try (PreparedStatement getlastreading = MYSQL_Helper.prepareStatement(PREPARED_GET_LAST_COND_READING_FOR_METER)) {
            getlastreading.setInt(1, meterId);
            getlastreading.setString(2, LocalDateTimeUtilities.getISODateTimeString(dateTime));
            results = getlastreading.executeQuery();
            if (results.next()) {
                Timestamp stamp_lastDateTime = results.getTimestamp("DATE");
                if (stamp_lastDateTime != null) {
                    LocalDateTime lastDateTime = stamp_lastDateTime.toLocalDateTime();
                    if (lastDateTime != null) {
                        lastReading = getConditionedReadingByMeterAtDateTime(meterId, lastDateTime);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReadingManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            MYSQL_Helper.closeResultSet(results);
        }

        return lastReading;
    }

    /**
     * Gets the conditioned reading from the database that has the given meter
     * identification number at the given date/time.
     * 
     * @param meterId The meter identification number to get the reading for.
     * @param dateTime The <code>LocalDateTime</code> to get a reading at.
     * @return The <code>ConditionedReading</code> from that database with the given meter and time.
     */
    @Override
    public ConditionedReading getConditionedReadingByMeterAtDateTime(int meterId, LocalDateTime dateTime) { ResultSet results = null;
        ConditionedReading conditionedReading = null;

        if (dateTime == null) {
            return conditionedReading;
        }

        try (PreparedStatement getConditionedReading = MYSQL_Helper.prepareStatement(PREPARED_GET_CONDITIONED_READING_BY_METER_AT_DATETIME)) {
            getConditionedReading.setInt(1, meterId);
            getConditionedReading.setString(2, LocalDateTimeUtilities.getISODateTimeString(dateTime));
            results = getConditionedReading.executeQuery();
            if (results.next()) {
                double reading = results.getDouble(VAR_RAW_READING_VALUE);
                double deltaEnergy = results.getDouble(VAR_COND_DELTA_ENERGY);
                double deltaTime = results.getDouble(VAR_COND_DELTA_TIME);
                conditionedReading = new ConditionedReading(meterId, dateTime, reading, deltaEnergy, deltaTime);
            }
        } catch (SQLException ex) {
            ErrorLogger.log(Level.WARNING, "ReadingManager.getRawReadingByMeterAtDateTime: Unable to prepare statement.", ex);
        } finally {
            MYSQL_Helper.closeResultSet(results);
        }

        return conditionedReading;
    }

    public static void main(String[] args) {

        PropertyManager.configure("P:/CompSci480/Lucid/web/WEB-INF/config/General.properties");

        LocalDateTime dateTime1 = LocalDateTime.of(2015,3,18,8,30,0);
        //LocalDateTime dateTime2 = LocalDateTime.of(1995, Month.MARCH, 9, 6, 6, 6);

        ReadingManager manager = new ReadingManager();
        
        System.out.println(dateTime1.isAfter(dateTime1));
        

    }

}
