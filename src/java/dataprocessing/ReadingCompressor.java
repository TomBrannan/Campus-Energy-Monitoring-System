package dataprocessing;

import common.ConditionedReading;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * A utility class used for compressing lists of readings into smaller lists.
 * This class is used so that the graph won't be overwhelmed with too many
 * points.
 *
 * @author tnb65846
 */
public class ReadingCompressor {

    //The maximum number of points that should be in a compressed list
    //(i.e. The maximum allowed for a single series when graphed)
    public static final int MAX_POINTS = 300;

    //15min, 30min, 1hr, 3hr, 6hr, 1day, 1week, 1month
    private static final int[] RESOLUTIONS = {1, 2, 4, 12, 24, 96, 672, 2688};

    /**
     * Compresses a List of ConditionedReadings into a smaller List. If the
     * given List's size is smaller than MAX_POINTS, the List is returned
     * unchanged. Otherwise, the List combines its ConditionedReadings such that
     * the size of the returned List is less than MAX_POINTS.
     *
     * @param readings The readings which may or may not need to be compressed.
     * @param kilowatts A toggle to allow values to be calculated as kW instead
     * of kWh.
     * @return A List of ConditionedReadings which accurately represents the
     * original List, but of a smaller size.
     */
    public static List<ConditionedReading> compressReadings(List<ConditionedReading> readings, boolean kilowatts) {
        List<ConditionedReading> compressed = new LinkedList<>();

        //Number of 15-minute chunks in our resolution 
        //(e.g. 12 for 3 hour resolution)
        int resolution = getResolutionRequired(readings.size());

        //Loop until we find an appropriate resolution 
        for (int r : RESOLUTIONS) {
            if (Math.ceil(readings.size() / (double) r) <= MAX_POINTS) {
                resolution = r;
                break;
            }
        }

        for (int i = 0; i < readings.size(); i += resolution) {
            if (i + resolution > readings.size()) {
                resolution = readings.size() - i;
            }
            ConditionedReading cr = combineReadings(readings.subList(i, i + resolution));

            if (kilowatts && cr != null) 
            {
                cr.setKwh(cr.getKwh() / (cr.getDeltaTime() / 60.0));
            }

            compressed.add(cr);
        }
        return compressed;
    }

   /**
     * Combines multiple ConditionedReading objects into a single
     * ConditionedReading. The meterId is retained and the last reading's date
     * and reading value is used; all other attributes (kwh and delta time) are
     * added together. Assumes that all readings have the same meter ID.
     *
     * Concerning kW calculation: For simplicity, we reuse the kWh field. The
     * division is done later.
     *
     * @param readings A list of ConditionedReadings to be combined.
     * @return A ConditionedReading representing the combination of one or more
     * other ConditionedReadings. Returns a null pointer if the readings list
     * given is empty or null.
     */
    private static ConditionedReading combineReadings(List<ConditionedReading> readings) {
            
        if (readings == null || readings.isEmpty()) {
            return null;
        }
        
        Integer meterID = null;
        Double value = null;
        LocalDateTime latestDateTime = LocalDateTime.MIN;
        for(ConditionedReading reading : readings) {
            if (reading != null) {
                meterID = reading.getMeterId();
                value = reading.getValue();
                LocalDateTime readingTime = reading.getDateTime();
                if (readingTime != null && readingTime.isAfter(latestDateTime)) {
                    latestDateTime = readingTime;
                }
            }
        }
        
        if (value == null || latestDateTime.equals(LocalDateTime.MIN) || meterID == null) {
            // All readings given are null so just return null
            return null;
        }

        double sum_kwh = 0, deltaTime = 0;
        for (ConditionedReading cr : readings) {
            // Skip any null readings
            if(cr == null) continue;
            sum_kwh += cr.getKwh();
            deltaTime += cr.getDeltaTime();
        }
        return new ConditionedReading(meterID, latestDateTime, value, sum_kwh, deltaTime);
    }

    public static int getResolutionRequired(int numReadings) {
        for (int r : RESOLUTIONS) {
            if (Math.ceil(numReadings / (double) r) <= MAX_POINTS) {
                return r;
            }
        }
        return 1;
    }

    private static List<ConditionedReading> getRandomReadings(int num) {
        LinkedList<ConditionedReading> list = new LinkedList<>();
        LocalDateTime now = LocalDateTime.now();
        Random rand = new Random();
        for (int i = 0; i < num; i++) {
            list.add(new ConditionedReading(0, now, rand.nextDouble(), rand.nextDouble(), 15));
            now = now.plusMinutes(15);
        }
        return list;
    }

    private static double sum(List<ConditionedReading> readings) {
        double sum = 0;
        for (ConditionedReading cr : readings) {
            System.out.println("READING: " + cr);
            sum += cr.getKwh();
        }
        return sum;
    }

    public static void main(String[] args) {
        List<ConditionedReading> random = getRandomReadings(301);
        List<ConditionedReading> readings = compressReadings(random, false);
        System.out.println("RANDOM SUM");
        System.out.println("random sum: " + sum(random));
        System.out.println("COMPRESSED SUM");
        System.out.println("compressed sum: " + sum(readings));
    }

}
