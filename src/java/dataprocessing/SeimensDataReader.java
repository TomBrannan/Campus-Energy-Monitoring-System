package dataprocessing;

import common.ConditionedReading;
import common.Meter;
import common.RawReading;
import database.MeterManager;
import database.ReadingManager;
import database.DatabaseManagement;
import java.io.FileNotFoundException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import utilities.CSVFileReader;
import utilities.Debug;
import utilities.ErrorLogger;
import utilities.LocalDateTimeUtilities;
import utilities.PropertyManager;

/**
 * Used to read <code>Reading</code>'s from a Lucid Data file TODO: Make timing
 * a variable by meter instead of fixed 15 minutes.
 *
 * @author cws55854
 */
public class SeimensDataReader {

    private static final int INDEX_KEY = 0,
            INDEX_NAME_SUFFIX = 1,
            INDEX_DATE = 0,
            INDEX_TIME = 1;

    private static final String TITLE_DATE_RANGE = "Date Range:",
            TITLE_REPORT_TIMINGS = "Report Timings:";

    private final Collection<String> meter_names = new LinkedList<>();
    private final Collection<TempReading> readings = new LinkedList<>();

    // relates the 'KEY' to the 'Name:Suffix' fields
    private final Map<String, String> nameRelation = new HashMap<>();
    // relates the title strings with the index in which they appear
    private final Map<String, Integer> titleRelation = new HashMap<>();

    private final CSVFileReader reader;

    /*
     If the KWH value of a conditioned reading is greater than the threshhold,
     it is interpreted as a spike and will be filtered.
     */
    private static final double KWH_THRESHHOLD = 100_000;

    /*
     Set true to NOT add readings to the database. 
     */
    private static final boolean TESTING = false;

    /**
     * Construct a SeimensDataReader which will read from the file at the given
     * path.
     *
     * @param file_path Path of the file to read from
     * @throws java.io.FileNotFoundException If the file path does not reference
     * an existing file
     */
    public SeimensDataReader(String file_path) throws FileNotFoundException {
        reader = new CSVFileReader(file_path);
        readLucidData();
    }

    /**
     * Reads and stores meter names and meter readings from the .csv file
     * associated with the <code>CSVFileReader</code>(<code>reader</code>). It's
     * worth mentioning that the first READING row in the file will always be
     * ignored, since I'm assuming that it is the previous reading and I'm
     * taking care of that with the database.
     */
    private void readLucidData() {
        String[] values;
        if (!reader.EOF()) {
            // Read and assign the first set of titles
            setTitleRelation(getNextRow());
        }

        // Read the first table
        while (!reader.EOF()) {
            values = getNextRow();

            if (values[0].equals("")) {
                // Done reading the first table
                Debug.println("SeimensDataReader.readLucidData: \'" + values[0] + "\'");
                break;
            } else if (values[0].equalsIgnoreCase(TITLE_DATE_RANGE)) {
                // Do nothing for now. Not do anything with the date range.
                ErrorLogger.log(Level.INFO, "Row with title:(" + TITLE_DATE_RANGE + ") has been skipped.");
            } else if (values[0].equalsIgnoreCase(TITLE_REPORT_TIMINGS)) {
                // Do nothing for now. Not doing anything with the report timings.
                ErrorLogger.log(Level.INFO, "Row with title:(" + TITLE_REPORT_TIMINGS + ") has been skipped.");
            } else {
                // Get the key and remove the trainling colon
                String key = values[INDEX_KEY].replace(":", "");

                // Add a relation between the given key and NAME:SUFFIX value
                nameRelation.put(key, values[INDEX_NAME_SUFFIX]);

                // Add the meter name to the meters list
                meter_names.add(values[INDEX_NAME_SUFFIX]);

                // Get the trend definition. Not used right now.
//                int time = -1;
//                try {
//                    time = Integer.parseInt(values[INDEX_TREND_DEFINITION]);
//                } catch(NumberFormatException e) {
//                    ErrorLogger.log(Level.SEVERE, "Siemens input file:(" + reader.toString() + ") has invalid trend definition value. A value of -1 will be assumed.");
//                } 
//                timeRelation.put(values[INDEX_KEY], time);
            }
        }

        // Read and assign the headers of the second table
        setTitleRelation(getNextRow());

        // Read the remaining rows as Reading values
        boolean firstRow = true;
        while (!reader.EOF()) {
            // Get the next row of values
            values = getNextRow();

            // Skip any empty rows.
            if (values.length == 0) {
                continue;
            }

            // Make sure that there are at least 3 values per row.
            // If there are less than three, than something is missing.
            if (values.length < 3) {
                ErrorLogger.log(Level.INFO, "Seimens input file:(" + reader.toString() + ") may have missing values.");
                continue;
            }

            // Skip the first row
//            if (firstRow) {
//                firstRow = false;
//                continue;
//            }
            // get the time and date values
            String date = values[INDEX_DATE]; // not out of bounds
            String time = values[INDEX_TIME]; // not out of bounds

            // Get reading for each meter
            for (String meter_key : nameRelation.keySet()) {
                Integer key_index = titleRelation.get(meter_key);

                if (key_index == null) {
                    ErrorLogger.log(Level.WARNING, "Siemens input file:(" + reader.toString() + ") has a meter key:(" + meter_key + ") with no readings.");
                }

                String reading_str = values[key_index];

                // Reading is blank. Will be skilled so the ErrorLogger is not spammed
                if (reading_str.equals("")) {
                    continue;
                }

                try {
                    double reading = Double.parseDouble(reading_str);
                    TempReading r = new TempReading(nameRelation.get(meter_key), getLocalDateTime(date, time), reading);
                    readings.add(r);
                } catch (NumberFormatException e) {
                    ErrorLogger.log(Level.WARNING, "Siemens input file:(" + reader.toString() + ") has invalid READING value:(" + reading_str + ") for meter:(" + meter_key + "), date:(" + date + "), time:(" + time + "). Reading is ignored.", e);
                } catch (IllegalArgumentException e) {
                    ErrorLogger.log(Level.WARNING, "Siemens input file:(" + reader.toString() + ") has invalid DATE OR TIME value for meter:(" + meter_key + "), date:(" + date + "), time:(" + time + "). Reading is ignored.", e);
                }
            }
        }
    }

    /**
     * Get the delimited values in the next row. Removes quotes.
     *
     * @return The delimited values without quotes.
     */
    private String[] getNextRow() {
        String[] nextRow = reader.getNextRow();
        if (nextRow == null) {
            return null;
        }
        for (int i = 0; i < nextRow.length; i++) {
            nextRow[i] = nextRow[i].replaceAll("\"", "");
        }
        return nextRow;
    }

    /**
     * Will fill <code>titleRelation</code> (map) with the given titles and
     * their index in the array.
     *
     * @param titles Set of titles
     * @return True if the set was successful. Otherwise false.
     */
    private boolean setTitleRelation(String[] titles) {
        // Check to see if there is at least 1 title in the titles list
        if (titles == null || titles.length == 0) {
            return false;
        }

        // Clear all title relations
        titleRelation.clear();

        // Declare title relations from the titles given
        for (int i = 0; i < titles.length; i++) {
            titleRelation.put(titles[i], i);
        }
        return true;
    }

    /**
     * Creates and returns an array containing the meter names in this file.
     *
     * @return An array containing the meter names in this file.
     */
    private Collection<String> getMeterNames() {
        Collection<String> meters = new LinkedList<>();
        meters.addAll(meter_names);
        return meters;
    }

    /**
     * Creates and returns an array containing all of the <code>Reading</code>s
     * found in this file.
     *
     * @return An array containing all of the <code>Reading</code>s found in
     * this file.
     */
    private TempReading[] getTempReadings() {
        TempReading[] readings_array = new TempReading[readings.size()];
        readings.toArray(readings_array);
        return readings_array;
    }

    /**
     * Returns an instance of <code>LocalDateTime</code> which represents the
     * given date and time. .
     *
     * @param date A string which represents a date. Must have the format
     * "m/d/yyyy"
     * @param time A string which represents a time. Must have the format
     * "hh:mm:ss"
     * @return An instance of <code>LocalDateTime</code> which represents the
     * given date and time.
     * @exception IllegalArgumentException If either <code>date</code> or
     * <code>time</code> have an invalid format.
     */
    private LocalDateTime getLocalDateTime(String date, String time) throws IllegalArgumentException {
        String[] date_vals = date.split("/");
        String[] time_vals = time.split(":");
        int year, month, day, hour, minute, second;
        try {
            year = Integer.valueOf(date_vals[2]);
            month = Integer.valueOf(date_vals[0]);
            day = Integer.valueOf(date_vals[1]);
            hour = Integer.valueOf(time_vals[0]);
            minute = Integer.valueOf(time_vals[1]);
            second = Integer.valueOf(time_vals[2]);
            return LocalDateTime.of(year, month, day, hour, minute, second);
        } catch (NumberFormatException | IndexOutOfBoundsException | DateTimeException e) {
            ErrorLogger.log(Level.WARNING, "Date:(" + date + ") or Time:(" + time + ") has invalid format.");
            throw new IllegalArgumentException();
        }
    }

    /**
     * Get the RawReadings given in this Seimens Data file.
     *
     * @return A set containing all of the RawReadings from this Seimens Data
     * file.
     */
    private List<List<RawReading>> getFileRawReadings() {
        // List to store lists of RawReadings. Each List of RawReadings contains
        // RawReadings for a single meter.
        List<List<RawReading>> readingsListList = new LinkedList<>();

        // Get database manager
        DatabaseManagement dbm = new database.production.DatabaseManagement();
        MeterManager mm = dbm.getMeterManager();

        // Get temp readings
        TempReading[] tempReadings = getTempReadings();

        // get meters 
        Collection<Meter> meters = mm.getAllMetersByNames(getMeterNames());

        // Define a map relating a String (meter's name) to that meter
        Map<String, Meter> meterNameToMeterMap = new HashMap<>();
        // Define a map relating a  meter to a list of readings for that meter
        Map<Meter, List<RawReading>> meterToReadingListMap = new HashMap<>();

        // Define the maps
        for (Meter m : meters) {
            meterNameToMeterMap.put(m.getMeterName(), m);
            List<RawReading> tempList = new LinkedList<>();
            readingsListList.add(tempList);
            meterToReadingListMap.put(m, tempList);
        }

        // Go through all of the tempreadings
        for (TempReading tr : tempReadings) {
            // Get the meter for this tempreading
            Meter meter = meterNameToMeterMap.get(tr.getMeterName());
            // Create a new rawreading using the meter's id
            RawReading rawReading = new RawReading(meter.getMETER_ID(), tr.getDateTime(), tr.getValue());
            // add this rawreading to the corresponding reading list
            meterToReadingListMap.get(meter).add(rawReading);
        }

        // Sort all of the lists
        for (List<RawReading> list : readingsListList) {
            //Collections.sort(list);
            Collections.sort(list);
        }

        return readingsListList;
    }

    /**
     * Rounds the date-time value of the given reading to an interval of 15
     * minutes.
     *
     * @param r The Reading to round
     * @return the rounded reading
     */
    private RawReading roundReadingTime(RawReading r) {
        if (r == null) {
            return null;
        }

        LocalDateTime readingTime = r.getDateTime();
        readingTime = readingTime.minusSeconds(readingTime.getSecond());
        int minutes = readingTime.getMinute();
        int newMinutes;

        // Round based on the minutes
        if (minutes > 52) {
            newMinutes = 60;
        } else if (minutes <= 7) {
            newMinutes = 0;
        } else if (minutes > 7 && minutes <= 22) {
            newMinutes = 15;
        } else if (minutes > 22 && minutes <= 37) {
            newMinutes = 30;
        } else {
            newMinutes = 45;
        }
        // Add the difference
        readingTime = readingTime.plusMinutes(newMinutes - minutes);

        // create and return the rounded reading
        return new RawReading(r.getMeterId(), readingTime, r.getValue());
    }

    /**
     * Adds the given raw readings to the database using a ReadingManager. Will
     * only add raw readings which will not cause redundancy issues in the
     * database.
     *
     * @param rawReadings
     */
    private void addRawReadingsToDatabase(Collection<RawReading> rawReadings) {
        if (rawReadings == null || rawReadings.isEmpty()) {
            return;
        }
        ReadingManager rm = database.Database.getDatabaseManagement().getReadingManager();

        // BAD
        RawReading someReading = null;
        for (RawReading reading : rawReadings) {
            someReading = reading;
            break;
        }
        // someReading is not null. Guarenteed. 

        // Get the most recent reading for the given meter in the database
        RawReading latestOldReading = rm.getLastRawReadingForMeterBeforeDateTime(someReading.getMeterId(), someReading.getDateTime().plusYears(1));
        // round the lastest reading time
        latestOldReading = roundReadingTime(latestOldReading);

        // If there is no latest old reading, add all of the readings.
        if (latestOldReading == null) {
            rm.addRawReadings(rawReadings);
        } else {
            // List to store new readings (come after the latest old reading)
            LinkedList<RawReading> newReadings = new LinkedList<>();

            RawReading tempRoundedReading;
            for (RawReading reading : rawReadings) {
                // Round the reading
                tempRoundedReading = roundReadingTime(reading);
                // Check to see if the new readings come after our most previous reading
                if (tempRoundedReading.getDateTime().isAfter(latestOldReading.getDateTime())) {
                    // the current reading comes after our currenly "last" reading,
                    // so add it to the list of new readings.
                    newReadings.add(reading);
                    Debug.println("SeimensDataReader.addRawReadingsToDatabase: Adding raw reading: " + reading);
                }
            }

            // Add all of the new readings into the database
            rm.addRawReadings(newReadings);
        }
    }

    /**
     * Adds the given conditioned readings to the database using a
     * ReadingManager.
     *
     * @param conditionedReadings
     */
    private void addCondReadingsToDatabase(Collection<ConditionedReading> conditionedReadings) {
        if (conditionedReadings == null) {
            return;
        }
        ReadingManager rm = database.Database.getDatabaseManagement().getReadingManager();
        rm.addConditionedReadings(conditionedReadings);
    }

    /**
     * The rounded raw reading set is the set of RawReadings as: [previous raw
     * reading (from database), rounded(rawReadings[0]), ... ,
     * rounded(rawReadings[size-1])]
     *
     * @param rawReadings
     * @return A new set containing raw readings which will be used to create
     * conditioned readings
     */
    private List<RawReading> roundRawReadings(Collection<RawReading> rawReadings) {
        DatabaseManagement dbm = new database.production.DatabaseManagement();
        ReadingManager rm = dbm.getReadingManager();

        // set of raw readings with a rounded date-time
        List<RawReading> roundedRawReadingSet = new LinkedList<>();

        // used to flag whether or not a previous reading is added to the list
        boolean containsPrevReading = false;

        for (RawReading rawReading : rawReadings) {
            if (rawReading != null) {
                if (!containsPrevReading) {
                    RawReading previousReading = rm.getLastRawReadingForMeterBeforeDateTime(rawReading.getMeterId(), roundReadingTime(rawReading).getDateTime().minusMinutes(6));
                    if (previousReading != null) {
                        roundedRawReadingSet.add(roundReadingTime(previousReading));
                        Debug.println("SeimensDataReader.roundRawReadings: Previous reading for meterid(" + rawReading.getMeterId() + ") = " + previousReading);
                    } else {
                        Debug.println("SeimensDataReader.roundRawReadings: No previous reading for meterid(" + rawReading.getMeterId() + ") found.");
                    }
                    containsPrevReading = true;
                }
                roundedRawReadingSet.add(roundReadingTime(rawReading));
            }
        }
        return roundedRawReadingSet;
    }

    /**
     * Creates a conditioned reading from the given two RawReadings, where both
     * RawReadings are from the same meter and the first RawReading given comes
     * before (by date-time) the correspondingReading. The ConditionedReading
     * returned will copy the values from correspondingReading (reading value,
     * meter, and date-time). If the preConditionedReading would have a KWH or
     * deltaTime value less than or equal to 0, a null pointer is returned
     * instead.
     *
     * @param previousReading
     * @param correspondingReading
     * @return
     */
    private ConditionedReading createPreConditionedReading(RawReading previousReading, RawReading correspondingReading) {
        if (previousReading == null || correspondingReading == null) {
            return null;
        }

        // Makee sure that both readings belong to the same meter. Otherwise
        // the returned conditioned reading means nothing.
        if (previousReading.getMeterId() != correspondingReading.getMeterId()) {
            return null;
        }

        // difference in reading values
        double kwh = correspondingReading.getValue() - previousReading.getValue();
        // difference in date-time values using LocalDateTimeUtilities class
        int deltaTime = LocalDateTimeUtilities.minuteDifference(correspondingReading.getDateTime(), previousReading.getDateTime());

        // If there is some issue where the kwh or time value decreases or is 0,
        // then ignore this reading.
        if (kwh <= 0 || deltaTime <= 0) {
            return null;
        }

        // create new conditioned reading
        ConditionedReading condReading = new ConditionedReading(correspondingReading.getMeterId(), correspondingReading.getDateTime(), correspondingReading.getValue(), kwh, deltaTime);
        return condReading;
    }

    /**
     * Get the conditioned readings before conditioning them.
     *
     * @param rawReadings
     * @return
     */
    private List<ConditionedReading> createPreConditionedReadings(Collection<RawReading> rawReadings) {
        // copy the rawReadings to an array for efficient index access
        RawReading[] arry_raw = new RawReading[rawReadings.size()];
        rawReadings.toArray(arry_raw);

        List<ConditionedReading> conditionedReadings = new LinkedList<>();

        for (int i = 1; i < arry_raw.length; i++) {
            ConditionedReading tempReading = createPreConditionedReading(arry_raw[i - 1], arry_raw[i]);
            if (tempReading != null) {
                conditionedReadings.add(tempReading);
            }
        }
        return conditionedReadings;
    }

    /**
     * Condition the given raw Readings
     *
     * @param rawReadings
     * @return
     */
    private List<ConditionedReading> conditionReadings(Collection<RawReading> rawReadings) {
        List<ConditionedReading> preConditionedReadings = createPreConditionedReadings(roundRawReadings(rawReadings));

        for (ConditionedReading reading : preConditionedReadings) {
            //Debug.println("PRECOND READING: " + reading);
        }

        // Apply spike filter
        preConditionedReadings = applySpikeFilterToPreConditionedReadings(preConditionedReadings);

        List<ConditionedReading> conditionedReadings = new LinkedList<>();
        ConditionedReading[] arry_pre = new ConditionedReading[preConditionedReadings.size()];
        preConditionedReadings.toArray(arry_pre);
        for (int i = 0; i < arry_pre.length; i++) {
            ConditionedReading temp = arry_pre[i];

            if (temp.getDeltaTime() > 15) {
                LocalDateTime tempTime = temp.getDateTime(); // get the starting time
                // replace with averaged readings
                int intervals = (int) (temp.getDeltaTime() / 15);
                double avg_kwh = temp.getKwh() / intervals;

                // Pre-add previous interpolated readings
                for (int k = intervals - 1; k >= 0; k--) {
                    conditionedReadings.add(new ConditionedReading(temp.getMeterId(), tempTime.minusMinutes(15 * k), temp.getValue() - (avg_kwh * k), avg_kwh, 15.0));
                }
            } else if (temp.getDeltaTime() == 15) {
                conditionedReadings.add(temp);
            } else {
                ErrorLogger.log(Level.WARNING, "SeimensDataReader.conditionReadings: Weird time difference: " + temp.getDeltaTime());
            }
        }
        return conditionedReadings;
    }

    /**
     * Apply linear-interpolation spike filter to the pre-conditioned readings
     * given.
     *
     * @param readings The list of ConditionedReadings to apply a spike filter
     * to.
     * @return The filtered list of ConditionedReadings
     */
    private List<ConditionedReading> applySpikeFilterToPreConditionedReadings(List<ConditionedReading> readings) {
        ConditionedReading[] ary_readings = new ConditionedReading[readings.size()];
        readings.toArray(ary_readings);

        // Go from the second to the second-to-last reading and check if it needs to be filtered
        // Node: the first element of readings should be a PREVIOUS reading, which we assume
        // to be accurate in the base case (first reading ever, in which case there is no previous reading)
        for (int i = 1; i < ary_readings.length - 1; i++) {
            if (ary_readings[i].getKwh() > KWH_THRESHHOLD) {
                // interpolate based on previous and next average kw
                double prev_avg_kw = ary_readings[i - 1].getKwh() / ary_readings[i - 1].getDeltaTime();
                double next_avg_kw = ary_readings[i + 1].getKwh() / ary_readings[i + 1].getDeltaTime();
                ary_readings[i].setKwh((prev_avg_kw + next_avg_kw) / 2.0 * ary_readings[i].getDeltaTime());
            }
        }

        // If the Very last reading is a spike, it will be interpolated and 
        // updated in the next file (hopefully). 
        return readings;
    }

    /**
     * Process the current file. Processing consists of reading the file
     * (parsing for temp readings), parsing for meters, adding meters to the
     * database, turning temp readings into raw readings, creating
     * (pre)conditioned readings, conditioning the conditioned readings, and
     * adding both the raw and conditioned readings to the database.
     */
    public void processFile() {
        readLucidData();
        List<List<RawReading>> fileRawReadingsListList = getFileRawReadings();

        for (List<RawReading> rawReadingsList : fileRawReadingsListList) {
            Debug.println();

            for (RawReading rr : rawReadingsList) {
                //Debug.println("RAW READING: " + rr);
            }

            // Condition the readings
            Collection<ConditionedReading> condReadings = conditionReadings(rawReadingsList);
            for (ConditionedReading conditionedReading : condReadings) {
                //Debug.println("CONDITIONED READING: " + conditionedReading);
            }

            if (!TESTING) {
                // Add raw and conditioned readings 
                addRawReadingsToDatabase(rawReadingsList);
                addCondReadingsToDatabase(condReadings);
            }
        }
    }

    public static void main(String[] args) throws FileNotFoundException {

        Debug.setEnabled(true);
        PropertyManager.configure("P:/CompSci480/Lucid/web/WEB-INF/config/General.properties");
        SeimensDataReader reader = new SeimensDataReader("meterdata_03-18-15_09-31.csv");

        reader.processFile();

        System.out.println("ALL DONE");
    }

}
