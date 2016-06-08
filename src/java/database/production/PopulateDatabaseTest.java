package database.production;

import common.Building;
import common.Campus;
import common.Meter;
import common.RawReading;
import common.University;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Scanner;
import utilities.PropertyManager;

/**
 *
 * @author cws55854
 */
public class PopulateDatabaseTest {

    public static void main(String[] args) {

        PropertyManager.configure("P:/CompSci480/Lucid/web/WEB-INF/config/General.properties");

        Scanner input = new Scanner(System.in);
        DatabaseManagement dm = new DatabaseManagement();
        University bloomu = dm.getUniversityManager().addUniversity("Bloomsburg University");
        if (bloomu != null) {

            // Add campus
            Campus BULowerCampus = dm.getCampusManager().addCampus("BU Lower Campus", 1, bloomu.getUniversityId());
            if (BULowerCampus != null) {
                // Add buildings
                Building hartline = dm.getBuildingManager().addBuilding("Harline Science Center2", 1, 500, 12000, BULowerCampus.getCampusId(), true);
                Building benFranklin = dm.getBuildingManager().addBuilding("Ben Franklin", 1, 200, 4500, BULowerCampus.getCampusId(), true);

                if (hartline != null) {

                    System.out.println("Adding 3 hartline meters...");
                    Meter hartlineMeter1 = dm.getMeterManager().addMeter("H_METER_1", "First Hartline Meter", hartline.getBuildingId());
                    Meter hartlineMeter2 = dm.getMeterManager().addMeter("H_METER_2", "Second Hartline Meter", hartline.getBuildingId());
                    Meter hardlineMeter3 = dm.getMeterManager().addMeter("H_METER_3", "Third Hartline Meter", hartline.getBuildingId());

                    System.out.println("Deleting 3 hartline meters..");
                    dm.getMeterManager().deleteMeter(hartlineMeter1);
                    dm.getMeterManager().deleteMeter(hartlineMeter2);
                    dm.getMeterManager().deleteMeter(hardlineMeter3);

                    System.out.println("Deleting hartline building...");
                    dm.getBuildingManager().deleteBuilding(hartline);
                }

                if (benFranklin != null) {
                    Meter benFranklinMeter = dm.getMeterManager().addMeter("BF_METER", "Meter for Ben Franklin Hall", benFranklin.getBuildingId());

                    if (benFranklinMeter != null) {

                        /*
                         TEST READING MANAGER
                         */
                        // Add ton of readings
                        Collection<RawReading> readings = new LinkedList<>();
                        database.ReadingManager readingManager = dm.getReadingManager(); // not null
                        LocalDateTime startTime = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
                        LocalDateTime endTime = LocalDateTime.of(2000, 6, 1, 0, 0, 0);
                        LocalDateTime time = startTime.plusMinutes(0);
                        double reading = 0.0;
                        int count = 0;
                        while (time.isBefore(endTime)) {
                            RawReading tempReading = new RawReading(benFranklinMeter.getMETER_ID(), time, (double) reading);
                            if (readingManager.addRawReading(tempReading)) {
                                count++;
                                if (count % 500 == 0) {
                                    System.out.println("Created " + count + " readings: -- " + time.toString());
                                }
                            }
                            time = time.plusMinutes(15);
                            reading += Math.random();
                        }

                        // Get some readings
                        Collection<RawReading> testReadings;
                        LocalDateTime test_startTime = startTime.plusMonths(2).plusDays(10).plusHours(5).plusMinutes(33);
                        LocalDateTime test_endTime = test_startTime.plusMonths(2).plusDays(5).plusHours(6).plusMinutes(12);
                        System.out.println("SELECTION TEST");
                        System.out.println("Start Time: " + test_startTime);
                        System.out.println("End Time: " + test_endTime);
                        
                        // wait
                        input.nextLine();
                        
                        long start = System.currentTimeMillis();
                        testReadings = readingManager.getAllRawReadingsByMeter(benFranklinMeter.getMETER_ID(), test_startTime, test_endTime);
                        long finish = System.currentTimeMillis();
                        long timeTaken = finish-start;
                        System.out.println("TIME TAKEN: " + timeTaken);
                        System.out.println("Rows selected: " + testReadings.size());
                        input.nextLine();

                        // Print all readings
//                        for (RawReading restReading : testReadings) {
//                            System.out.println("Test reading: " + restReading);
//                        }


                        /*
                         REMOVE READINGS ADDED
                         */
                        // remove readings
                        testReadings = readingManager.getAllRawReadingsByMeter(benFranklinMeter.getMETER_ID());
                        System.out.println("Remaining readings1: " + testReadings.size());
                        System.out.println("Removing remaining readings....");
                        int count2 = 0;
                        for (RawReading r : testReadings) {
                            count2++;
                            if (count2 % 500 == 0) {
                                System.out.println("Deleting readings: " + count2 + "/" + count);
                            }
                            readingManager.deleteRawReading(r);
                        }
                        System.out.println("Done removing readings...");
                        
                        testReadings = readingManager.getAllRawReadingsByMeter(benFranklinMeter.getMETER_ID());
                        System.out.println("Remaining readings2: " + testReadings.size());
                        input.nextLine();
                        
                        System.out.println("Deleting BF meter...");
                        dm.getMeterManager().deleteMeter(benFranklinMeter);
                    }
                    System.out.println("Deleting BF building...");
                    dm.getBuildingManager().deleteBuilding(benFranklin);
                }
                System.out.println("Deleting lower campus...");
                dm.getCampusManager().deleteCampus(BULowerCampus);
            }
            System.out.println("Deleting BU...");
            dm.getUniversityManager().deleteUniversity(bloomu);
        }
    }

}
