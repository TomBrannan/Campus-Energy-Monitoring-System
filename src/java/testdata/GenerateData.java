package testdata;

import utilities.CSVFileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Random;
import utilities.Debug;

/**
 * Generates pseudo-real data which mimics the data we expect to receive from Seimens.
 * Date is used because this class is temporary. This class may be deleted once
 * temporary test data is no longer needed.
 * @author cws55854
 */

@Deprecated
public class GenerateData {
    
    private static final Random random = new Random();
    
    // Headers for the first table 
    private static final String[] HEADERS1 = {
        "Key",
        "Name:Suffix",
        "Trend Definitions Used"
    };
    
    // First two headers for the second table
    private static final String[] HEADERS2 = {
        "<>Date",
        "Time"
    };
    
    /**
     * Given the names of the meters being recorder, the time interval between
     * readings in minutes, the start date, and the end date, will generate appropriate
     * meter readings for each meter starting at <code>start_date</code> and ending 
     * at <code>end_date</code>. The time between recordings is determined by 
     * <code>minute_interval</code> 
     * @param fileName
     * @param meter_names
     * @param minute_interval
     * @param start_date
     * @param end_date 
     */
    public static void generatePseudoDataFile(String fileName, String[] meter_names, int minute_interval, Date start_date, Date end_date)
    {
        if (meter_names == null || minute_interval <= 0 || fileName == null 
                || start_date == null || end_date == null
                || start_date.after(end_date)) {
            Debug.println("generatePseudoDataFile: Invalid parameters. " + fileName);
            return;
        }
        
        fileName = fileName.trim();
        
        int num_meters = meter_names.length;
        CSVFileWriter csv = null;
        Meter[] meters = new Meter[num_meters];
        try {
            csv = new CSVFileWriter(fileName);
            
            // Write the first set of headers
            csv.writeValues(HEADERS1);
            csv.writeLine();
            
            // Write the name associations table
            for(int i = 0; i < num_meters; i++) {
                // Write 'Key' value
                csv.writeValue("Point_" + (i+1));
                
                // Write 'Name:Suffix' value
                csv.writeValue(meter_names[i].trim());
                
                // Write 'Trend Definitions Used' value
                csv.writeValue(minute_interval);
                csv.writeLine();
            }
            
            // Write the 'Date Range' row
            csv.writeValue("Date Range:");
            csv.writeLine();
            
            // Write the 'Report Timings' row
            csv.writeValue("Report Timings All Hours");
            csv.writeLine();
            
            // Buffer row
            csv.writeLine();
            
            /*
                Write the meter readings table
            */
            
            // Write the second set of headers
            csv.writeValues(HEADERS2);
            for(int i = 0; i < num_meters; i++)
            {
                csv.writeValue("Point_" + (i+1));
            }
            csv.writeLine();
            
            // Current date will increase, starting at start_date and ending at end_date,
            // in intervals of 'minute_interval' minutes.
            Date current_date = new Date(start_date.getTime());
            
            // Initialize meters
            for(int i = 0; i < meters.length; i++) {
                meters[i] = new Meter(new TimeOfDay(current_date.getHours(), current_date.getMinutes()));
            }
            
            while(current_date.before(end_date)) {
                // Print '<>Date' and 'Time' columns
                String date = current_date.getMonth() + "/" + current_date.getDate() + "/" + current_date.getYear();
                String time = current_date.getHours() + ":" + current_date.getMinutes() + ":" + current_date.getSeconds();
                csv.writeValue(date);
                csv.writeValue(time);
                
                // Write the meter reading for each meter
                for(Meter meter : meters)
                {
                    csv.writeValue(meter.getReading());
                    meter.incrementMeter(minute_interval);
                }
                csv.writeLine();
                
                // Increment current_time by adding 'minute_interval' worth of minutes to the current date
                current_date = new Date(current_date.getTime() + minute_interval*60000);
            } 
                    
        } catch (IOException ex) {
            Debug.println(ex.getMessage());
        } finally {
            if (csv != null) {
                csv.close();
            }
        }
    }
    
    /**
     * Abstraction of a meter. Used for generating pseudo data files.
     */
    private static class Meter
    {
        // Value that the meter reading will start with
        public static final int MAX_BASE_READING = 1000000;
        public static final int MIN_BASE_READING = 60000;
        
        // Rate at which the meter reading will increase by
        public static final int MAX_READING_INCREMENT_PER_MIN = 30;
        public static final int MIN_READING_INCREMENT_PER_MIN = 20;
        
        // Amount of decrease in meter_readings at night
        //public static final double NIGHT_FACTOR = .5;
        
        // Start and end times of when night is defined
        //public static final TimeOfDay NIGHT_START = new TimeOfDay(19, 0);
        //public static final TimeOfDay NIGHT_END = new TimeOfDay(6, 0);
        
        private double reading; //current meter reading
        // May be used later for decreasing energy usage at night.
        private TimeOfDay time; // time of day for the meter
        
        public Meter(TimeOfDay startTime)
        {
            reading = getRandomBaseReading();
            time = new TimeOfDay(startTime);
            if (time == null) {
                time = new TimeOfDay(0,0);
            }
        }
        
        /**
         * Increases the current meter reading by some amount based on the minutes given.
         * @param minutesLater The number of minutes to pass before creating a new meter reading.
         * @return The amount by which the meter reading is increased.
         */
        public double incrementMeter(int minutesLater)
        {
            double meter_increase = minutesLater * getRandomReadingIncrementPerMin();
            reading += meter_increase;
            time = time.addMinutes(minutesLater);
            return meter_increase;
        }
        
        /**
         * Calculates a random base reading between <code>MIN_BASE_READING</code>
         * and <code>MAX_BASE_READING</code>
         * @return A random base reading between 
         */
        public static double getRandomBaseReading()
        {
            return MIN_BASE_READING + random.nextInt(MAX_BASE_READING - MIN_BASE_READING);
        }
        
        /**
         * Calculates a random meter increment (per minute) between 
         * <code>MIN_READING_INCREMENT_PER_MIN</code> and <code>MAX_READING_INCREMENT_PER_MIN</code>
         * @return A random meter increment (per minute).
         */
        public static double getRandomReadingIncrementPerMin()
        {
            return MIN_READING_INCREMENT_PER_MIN + random.nextInt(MAX_READING_INCREMENT_PER_MIN - MIN_READING_INCREMENT_PER_MIN);
        }

        /**
         * Returns the meter's time of day.
         * @return The meter's time of day.
         */
        public TimeOfDay getTime() {
            return new TimeOfDay(time);
        }

        /**
         * Returns the meter's current reading value.
         * @return The meter's current reading value.
         */
        public double getReading() {
            return reading;
        }
        
    }
    
    /**
     * Abstraction of the time of day. Keeps track of the hour and minute part
     * of a time of day. Hours are in military time (range from 0 to 23 hours).
     */
    private static final class TimeOfDay implements Comparable<TimeOfDay>
    {
        private final int hour;
        private final int minutes;
        
        public TimeOfDay(TimeOfDay tod)
        {
            this.hour = tod.hour;
            this.minutes = tod.minutes;
        }
        
        public TimeOfDay(int hour, int minutes)
        {
            this.hour = hour;
            this.minutes = minutes;
        }
        
        /**
         * Creates a new TimeOfDay <code>minutes</code> minutes after <code>this</code> timeOfDay.
         * @param minutes Number of minutes to increment by
         * @return a new TimeOfDay <code>minutes</code> minutes after <code>this</code> timeOfDay.
         */
        public TimeOfDay addMinutes(int minutes)
        {
            int new_mins = this.minutes + minutes;
            int new_hour = this.hour;
            if (new_mins > 60) {
                new_mins %= 60;
                new_hour++;
                new_hour %= 24;
            }
            
            return new TimeOfDay(new_hour, new_mins);
        }
        
        /**
         * Compute whether or not this time is between the two given times.
         * @param time1 Beginning time
         * @param time2 Ending time
         * @return Whether or not this time is between the two given times.
         */
        public boolean between(TimeOfDay time1, TimeOfDay time2)
        {
            if (time1.compareTo(time2) == 1) {
                // Crosses midnight
                // See if this time is after or equal to the beginning time
                return this.compareTo(time1) != -1;
            }
            else {
                // Doesn't cross midnight
                // See if this time is between the given times
                return time1.compareTo(this) != 1 && this.compareTo(time2) != 1;
            }
        }

        /**
         * Builds a HH:MM:SS representation of the time that this <code>TimeOfDay</code>
         * represents.
         * @return a HH:MM:SS representation of the time that this <code>TimeOfDay</code>
         * represents.
         */
        @Override
        public String toString() {
            DecimalFormat format = new DecimalFormat("##");
            return "" + format.format(hour) + ":" + format.format(minutes) + ":" + "00";
        }

        /**
         * Compares the two <code>TimeOfDay</code>'s by hours and than by minutes.
         * @param timeOfDay The <code>TimeOfDay</code> to compare.
         * @return -1 if this time comes before <code>timeOfDay</code>.
         * 0 if the two times are the same. 1 if this time comes after <code>timeOfDay</code>.
         */
        @Override
        public int compareTo(TimeOfDay timeOfDay) {
            if (hour < timeOfDay.hour) {
                return -1;
            }
            else if (hour == timeOfDay.hour)
            {
                return Integer.compare(minutes, timeOfDay.minutes);
            }
            else {
                return 1;
            }
        }
        
    }
    
    public static void main(String[] args)
    {
        TimeOfDay time1 = new TimeOfDay(20,0);
        TimeOfDay time2 = new TimeOfDay(21,0);
        TimeOfDay time3 = new TimeOfDay(5,0);
        
        System.out.println("EXPECTING FALSE");
        System.out.println(time1.between(time2, time3));
        System.out.println("EXPECTING TRUE");
        System.out.println(time2.between(time1, time3));
        System.out.println("EXPECTING FALSE");
        System.out.println(time3.between(time1, time2));
        
        String[] meter_names = {
            "CL.ELEC.MTR",
            "EL.ELEC.MRT",
            "HC.METER.MTR",
            "HC.WEST.ELEC.MTR",
            "NF.ELEC.MTR",
            "RMELEC",
            "HC.EAST.ELEC.MTR1",
            "HC.EAST.ELEC.MTR2",
            "SRC.ELECTRIC METER",
            "NHPMTR"
        };
        
        final long MS_PER_DAY = 60000*60*24;
        long num_days = 1;
        Date start_date = new Date(2012, 2, 5);
        Date end_date = new Date(start_date.getTime() + (MS_PER_DAY * num_days));
        
        GenerateData.generatePseudoDataFile("testFile1", meter_names, 60, start_date, end_date);
    }
    
}
