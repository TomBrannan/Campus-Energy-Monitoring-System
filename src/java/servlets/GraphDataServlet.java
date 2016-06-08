package servlets;

import common.Building;
import common.ConditionedReading;
import common.Meter;
import common.University;
import dataprocessing.ReadingCompressor;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import utilities.Debug;
import utilities.LocalDateTimeUtilities;

/**
 * Handles fetching data (conditioned readings, which are then smoothed),
 * serves data to the graph in JSON format.
 * @author tnb65846
 */
@WebServlet(name = "GraphDataServlet", urlPatterns = {"/fetchDataServlet"})
public class GraphDataServlet extends HttpServlet {

    private final boolean PADDING = true;
    private final boolean COMPRESSION = true;
    private boolean kilowatts = true;
    
    private static final int ROUND = 15;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /**
     * For testing.
     */
    public Collection<ConditionedReading> createRandomReadings(LocalDateTime begin, int numReadings, int meterId)
    {
        Random rand = new Random();
        Collection<ConditionedReading> readings = new LinkedList<>();
        double prev = 50;
        for(int i = 0; i < numReadings; i++)
        {
            ConditionedReading reading = new ConditionedReading(meterId, begin.plusMinutes(15*i), i, prev, 15);
            prev += rand.nextBoolean() ? rand.nextInt() : -rand.nextInt();
        }
        return readings;
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LocalDateTime beginDate = null, endDate = LocalDateTime.now();
        endDate = LocalDateTimeUtilities.roundDownDateTimeMinutes(endDate, ROUND);
        beginDate = endDate.minusDays(1);
        
        String newTitle = "Power Usage (" + LocalDateTimeUtilities.getEnglishDateTimeString(beginDate) + " - " + LocalDateTimeUtilities.getEnglishDateTimeString(endDate) + ")";
        String jsonString = "{\"title\":\"" + newTitle + "\",\"series\":[";
        
        int size = 500;
        
        Collection<ConditionedReading> readings1 = createRandomReadings(beginDate, size, 1);
        Collection<ConditionedReading> readings2 = createRandomReadings(beginDate, size, 2);
        
        jsonString += appendJSONSeries("Building 1", readings1);
        jsonString += ",";
        jsonString += appendJSONSeries("Building 2", readings2);

        int resolution = 1;
        boolean first = true;
        resolution = ReadingCompressor.getResolutionRequired(readings1.size());
        
        jsonString += "],\"resolution\":" + resolution;
        jsonString += ",\"kilowatts\":" + kilowatts;
        jsonString += ",\"firstDate\":\'" + LocalDateTimeUtilities.getZonedDateTimeString(beginDate) + "\'";
        jsonString += "}";

        JSONObject jsonObject = new JSONObject(jsonString);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().print(jsonObject);
    }

    /**
     * Given a list of ConditionedReadings, fits the list to a given date range.
     * If the range is smaller than the readings, they're cut off.  If it's 
     * bigger (on either side, that is, the beginDate is before the first reading
     * or the endDate is after the last reading), the resultant list of
     * ConditionedReadings will be padded with nulls to reflect the date
     * range more accurately.
     *
     * @param readings A <code>List</code> of <code>ConditionedReading</code>s to be padded
     * @param beginDate The lower bound of the date range (earliest)
     * @param endDate The upper bound of the date range (latest)
     * @param resolution minutes between readings given
     * @return
     */
    public static List<ConditionedReading> padReadingsByDateRange(List<ConditionedReading> readings, LocalDateTime beginDate, LocalDateTime endDate, int resolution) {
        List<ConditionedReading> paddedReadings = new LinkedList<>();
        if (readings == null || readings.isEmpty()) {
            return paddedReadings;
        }
        ConditionedReading first = readings.get(0);
        ConditionedReading last = readings.get(readings.size() - 1);

        int beginNulls = 0, endNulls = 0;
        int beginDiff = 0, endDiff = 0;

        if (beginDate.isBefore(first.getDateTime())) {
            beginDiff = LocalDateTimeUtilities.minuteDifference(beginDate, first.getDateTime());
        }
        if (endDate.isAfter(last.getDateTime())) {
            endDiff = LocalDateTimeUtilities.minuteDifference(endDate, last.getDateTime());
        }
        beginNulls = beginDiff / resolution;
        endNulls = endDiff / resolution;

        for (int i = 0; i < beginNulls; i++) {
            paddedReadings.add(null);
        }
        paddedReadings.addAll(readings);
        for (int i = 0; i < endNulls; i++) {
            paddedReadings.add(null);
        }

        return paddedReadings;
    }

    public static String appendJSONSeries(String name, Collection<ConditionedReading> readings) {
        StringBuilder str = new StringBuilder();
        str.append("{\"name\":\"").append(name).append("\",\"points\":[");
        boolean first = true;
        int i = 0;
        for (ConditionedReading r : readings) {
            if (i > 1000) {
                break;
            }
            if (!first) {
                str.append(",");
            } else {
                first = false;
            }
            if (r != null) {
                str.append(r.getKwh());
            } else {
                str.append("null");
            }
            i++;
        }
        str.append("]}");
        return str.toString();
    }

    private static ConditionedReading sumReadings(Collection<ConditionedReading> readings, boolean kw) {
        if (readings == null || readings.isEmpty()) {
            return null;
        }
        double kwh = 0, value = 0, deltaTime = 0;
        LocalDateTime date = null;

        //Should also check that the DateTimes are all the same
        for (ConditionedReading cr : readings) {
            //NULL#1
            if (cr == null) {
                //?
            } else {
                date = cr.getDateTime();
                deltaTime = cr.getDeltaTime();
                if (kw) {
                    kwh += cr.getKwh() * cr.getDeltaTime();
                } else {
                    kwh += cr.getKwh();
                }
                value += cr.getValue();
            }
        }
        if (kw && deltaTime != 0) {
            return new ConditionedReading(-1, date, value, kwh / deltaTime, deltaTime);
        }
        return new ConditionedReading(-1, date, value, kwh, deltaTime);
    }

    public static Collection<ConditionedReading> getMeterReadingSumByBuilding(int buildingID, LocalDateTime beginDate, LocalDateTime endDate, boolean paddingDesired, boolean kw) {
        database.MeterManager mm = database.Database.getDatabaseManagement().getMeterManager();
        database.ReadingManager rm = database.Database.getDatabaseManagement().getReadingManager();
        database.BuildingManager bm = database.Database.getDatabaseManagement().getBuildingManager();
        Building building = bm.getBuildingByID(buildingID);
        Collection<Meter> meters = mm.getMetersByBuilding(building);
        Collection<ConditionedReading> combinedReadings = new LinkedList<>();
        List<List<ConditionedReading>> allReadings = new LinkedList<>();
        for (Meter meter : meters) {
            Collection<ConditionedReading> theseReadings = rm.getAllConditionedReadingsByMeter(meter.getMETER_ID(), beginDate, endDate);
            if (paddingDesired) {
                allReadings.add(padReadingsByDateRange((List<ConditionedReading>) theseReadings, beginDate, endDate, 15));
            } else {
                allReadings.add((List<ConditionedReading>) theseReadings);
            }
        }

        int size = allReadings.get(0).size();

        for (List<ConditionedReading> list : allReadings) {
            Debug.println("List size: " + list.size());
        }
        Debug.println();

        for (int i = 0; i < size; i++) {
            Collection<ConditionedReading> temp = new LinkedList<>();
            for (List<ConditionedReading> readings : allReadings) {
                temp.add(readings.get(i));
            }
            combinedReadings.add(sumReadings(temp, kw));
        }

        return combinedReadings;
    }

}
