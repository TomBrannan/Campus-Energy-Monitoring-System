package datadownload;

import common.ConditionedReading;
import common.Meter;
import common.RawReading;
import common.Reading;
import database.DatabaseManagement;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import utilities.CSVFileWriter;
import utilities.Debug;
import utilities.LocalDateTimeUtilities;

/**
 *
 * @author cws55854
 */
public class ReadingDataReport {

    private Collection<Integer> buildingIDs = new LinkedList<>();
    private LocalDateTime startTime, endTime;
    private String filename;

    private List<List<Reading>> readings = new LinkedList<>();

    private String filePath = null;

    private static final String DEFAULT_PATH_START = "\\reportfiles\\";
    private static final String DEFAULT_FILE_EXTENSION = ".csv";
    private static final String DEFAULT_MISSING_READING_VALUE = "";

    public ReadingDataReport(String filename, Collection<Integer> buildingIDs, LocalDateTime startTime, LocalDateTime endTime) {
        this.filename = filename;
        if (buildingIDs != null) {
            for (Integer buildingID : buildingIDs) {
                this.buildingIDs.add(buildingID);
            }
        }

        this.startTime = startTime;
        this.endTime = endTime;
    }

    private void clearReadings() {
        for (List<Reading> readingList : readings) {
            readingList.clear();
        }
        readings.clear();
    }

    public void populateRawReadings() {
        Debug.println("Calling populateRawReadings");
        clearReadings();

        if (startTime == null || endTime == null || buildingIDs.isEmpty()) {
            return;
        }

        database.ReadingManager readingManager = database.Database.getDatabaseManagement().getReadingManager();
        Collection<RawReading> rawReadings;
        for (int buildingID : buildingIDs) {
            rawReadings = readingManager.getAllRawReadingsByMeter(buildingID, startTime, endTime);
            List<Reading> rawReadingList = new LinkedList<>();
            rawReadingList.addAll(rawReadings);
            rawReadings.clear();
            readings.add(rawReadingList);
        }
    }

    private String getNonRelativePath() {
        return DEFAULT_PATH_START + filename + DEFAULT_FILE_EXTENSION;
    }

    public void writeFile(String relativePathStart) throws IOException {
        // Assign the new filePath using the relative path start
        filePath = relativePathStart + getNonRelativePath();

        // initialize a new CSVFileWriter with the absolute-relative file path
        CSVFileWriter fileWriter = new CSVFileWriter(filePath);

        // 
        DatabaseManagement manager = database.Database.getDatabaseManagement();

        // Relates a meter's name with the column number (after date and time columns) which represents that meter
        Map<String, Integer> meterNameToColumnNumberMap = new HashMap<>();

        // Relates a meter's ID (from a reading) with the name of that meter for quick index into
        // meterNameToColumnNumberMap to get the column.
        Map<Integer, String> meterIdToMeterNameMap = new HashMap<>();

        // Fill the meterNameToColumnNumber map and MeterIdToMeterName map. 
        int column_index = 0;
        for (List<Reading> readingList : readings) {
            if (readingList == null || readingList.isEmpty()) {
                continue;
            }
            // get first non null reading
            Reading reading = null;
            for (Reading temp_reading : readingList) {
                if (temp_reading != null) {
                    reading = temp_reading;
                    break;
                }
            }
            if (reading == null) {
                continue;
            }
            Meter readingMeter = manager.getMeterManager().getMeterById(reading.getMeterId());
            if (readingMeter != null) {
                String meterName = readingMeter.getMeterName();
                meterNameToColumnNumberMap.put(meterName, column_index);
                meterIdToMeterNameMap.put(readingMeter.getMETER_ID(), readingMeter.getMeterName());
                column_index++;
            }
        }

        // Map to associate a local date time to a map which associates a meter name
        // to a reading (1 reading per column per LocalDateTime)
        Map<LocalDateTime, Map<String, Double>> timestampToMeterValueMapMap = new HashMap<>();

        for (List<Reading> readingList : readings) {
            for (Reading reading : readingList) {
                // Get the time 
                LocalDateTime readingDateTime = reading.getDateTime();
                if (readingDateTime == null) {
                    continue;
                }
                // Check to see if that time is added to the map
                if (!timestampToMeterValueMapMap.containsKey(readingDateTime)) {
                    // if not then add it 
                    timestampToMeterValueMapMap.put(readingDateTime, new HashMap<>());
                }
                // get the meter name
                String meterName = meterIdToMeterNameMap.get(reading.getMeterId());
                if (meterName == null) {
                    continue;
                }
                // Try to add this reading's value 
                try {
                    timestampToMeterValueMapMap.get(readingDateTime).put(meterName, reading.getValue());
                } catch (Exception e) {
                    // Exception shouldn't happen. If this happens, there's an issue with the reading being added.
                    // This can be caused by more than 1 reading for the same meter having the same date time,
                    // which shouldn't happen as long as the data isn't messed with after retrieving 
                    // from the database. 
                }
            }
        }
        
        /* ----- Start to actually write the file ----- */
        
        // Write the date range bounds
        fileWriter.writeValue("report start:");
        if (startTime != null) {
            fileWriter.writeValue(startTime.format(LocalDateTimeUtilities.DATA_REPORT_DATETIME_FORMATTER));
        } else {
            fileWriter.writeValue("null");
        }
        fileWriter.writeLine();
        
        fileWriter.writeValue("report end:");
        if (endTime != null) {
            fileWriter.writeValue(endTime.format(LocalDateTimeUtilities.DATA_REPORT_DATETIME_FORMATTER));
        } else {
            fileWriter.writeValue("null");
        }
        fileWriter.writeLine();
        

        // Write date and time headers
        fileWriter.writeValue("datetime");

        // Write meter headers
        String[] meterNameAry = new String[column_index];
        for (String meterName : meterNameToColumnNumberMap.keySet()) {
            Integer index = meterNameToColumnNumberMap.get(meterName);
            if (index != null) {
                meterNameAry[index] = meterName;
            }
        }
        for(String meterName : meterNameAry) {
            fileWriter.writeValue(meterName);
        }
        fileWriter.writeLine();

        // Write the readings
        List<LocalDateTime> localDateTimeList = new LinkedList<>(timestampToMeterValueMapMap.keySet());
        Collections.sort(localDateTimeList);
        for (LocalDateTime timestamp : localDateTimeList) {
            // Should NEVER be null but I'm checking anyway.
            if (timestamp == null) {
                continue;
            }

            // column_index will act as the number of readings per row
            Double[] readingsForRow = new Double[column_index];
            // Get the readings map
            Map<String, Double> meterToValueMap = timestampToMeterValueMapMap.get(timestamp);
            // Go through each meter
            for (String meter_name : meterToValueMap.keySet()) {
                // get the column index for this meter name
                Integer column = meterNameToColumnNumberMap.get(meter_name);
                // get the reading (if there is one)
                if (column != null) {
                    readingsForRow[column] = meterToValueMap.get(meter_name);
                }
            }

            // Write the date and time for the time stamp
            // timestamp != null
            fileWriter.writeValue(timestamp.format(LocalDateTimeUtilities.DATA_REPORT_DATETIME_FORMATTER));

            // Write the values for each column
            for (Double reading : readingsForRow) {
                if (reading == null) {
                    fileWriter.writeValue(DEFAULT_MISSING_READING_VALUE);
                } else {
                    // Possible formatting can be done here
                    fileWriter.writeValue(reading);
                }
            }
            fileWriter.writeLine();
        }

        // Close the writer
        fileWriter.close();
    }

    public String getFilePath() {
        return filePath;
    }

    public boolean deleteFile() throws IOException {
        System.out.println("Deleting file: " + filePath);
        File file = new File(filePath);
        return file.delete();
    }

}
