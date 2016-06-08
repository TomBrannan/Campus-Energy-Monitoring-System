package common;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import utilities.PropertyManager;

/**
 * This is a representation of the widget and all the information that it needs.
 * A widget displays the current power usage of a campus as compared to an
 * average based on the time of year and time of day.
 *
 * @author lap60658
 */
public class WidgetInformation {

    private ConditionedReading currentReading;
    private double average;
    private double percentage;
    private static String mainMeterName = "LCAMPUS.MAINMTR.KWH"; //default is the lower campus main meter
    private int daysInAverage = 30; //default is 30 days

    public WidgetInformation() {
    }

    /**
     * Gets the power (KW) associated with the current reading. Power is
     * calculated by dividing the deltaEnergy value (KWH) by the deltaTime in
     * hours.
     *
     * @return The current power value for the main meter.
     */
    public double getCurrentPower() {
        //convert the delta enery to power and return it
        return currentReading.getKwh() / (currentReading.getDeltaTime() / 60);
    }

    /**
     * Gets the average energy used around this time of day. Different averages
     * are calculated based on whether the current date is on a break or during
     * the semester.
     *
     * @return The average energy used around this time of day.
     */
    public double getAverage() {
        return average;
    }

    /**
     * Gets the percentage (in decimal format) that represents how much power is
     * currently being used compared to the average for this time of day. For
     * example, if current power is 180 KW and the average is 150 KW the
     * percentage returned would be .2 (20%) since power usage is 20% higher
     * than the average. If less energy is being used the percentage will be
     * negative.
     *
     * @return The percentage, in decimal format, of power used compared to the
     * average.
     */
    public double getPercentage() {
        return percentage;
    }

    /**
     * Gets the number of days currently being used to calculate averages. The
     * default for <code>daysInAverage</code> is 30 days. If there are fewer
     * days of data in the database then what <code>daysInAverage</code> is set
     * to, then the average will include the number of days available.
     *
     * @return The number of days that are calculated in the average.
     */
    public int getDaysInAverage() {
        return daysInAverage;
    }

    /**
     * Updates the widget information based on the current time. If the current
     * information is more than 15 minutes old the information is updated based
     * on the most recent raw reading found in the database. The average is
     * calculated based on the time of day and the time of year.
     */
    public void updateAll() {
        PropertyManager.configure("P:/webProjects/Lucid/web/WEB-INF/config/Web.properties");
        //get the meter current meter from the property file
        mainMeterName = PropertyManager.getProperty("Meter_current");
        //update daysInAverage based on the value stored in the properties file
        this.daysInAverage = Integer.parseInt(PropertyManager.getProperty("DaysInAverage_current"));
        
        if (currentReading == null || currentReading.getDateTime().isBefore(LocalDateTime.now().minusMinutes(15))) {
            database.ReadingManager rm = database.Database.getDatabaseManagement().getReadingManager();
            currentReading = rm.getLastConditionedReadingForMeterBeforeDateTime(getMainMeter(), LocalDateTime.now());

            //uses private methods to set the average 
            if (isBreak(currentReading.getDateTime())) {
                average = getBreakAverage();
            } else {
                average = getSemesterAverage();
            }
            percentage = getCurrentPower() / average - 1;
        }

    }

    /**
     * Calculates an average for the amount of power used at this time of day
     * for days not on a break for the number of days stated in
     * <code>daysInAverage</code>. If the current reading is on a weekend, only
     * weekends are included in the average and if it is a week day only
     * weekdays are included.
     *
     * @return A double is returned that represents the average power during the
     * semester at the same time of day as the <code>currentReading</code>.
     */
    private double getSemesterAverage() {
        database.ReadingManager rm = database.Database.getDatabaseManagement().getReadingManager();
        ConditionedReading tempReading;
        double sum = 0;

        LocalDateTime currentTime = currentReading.getDateTime(); //gets the time associated with the current reading
        int count = 0;  //The number of days that have been added to the sum
        for (int days = 1; count < daysInAverage; days++) {
            if (isBreak(currentTime.minusDays(days))) {
                continue;
            }

            tempReading = rm.getLastConditionedReadingForMeterBeforeDateTime(getMainMeter(), currentTime.minusDays(days));

            // if temp readings is null then there are no more values in the database so stop the average there
            if (tempReading == null) {
                break;
            }

            if (isWeekend(currentTime)) {
                if (isWeekend(currentTime.minusDays(days))) {
                    sum += tempReading.getKwh() / (currentReading.getDeltaTime() / 60);
                    count++;
                }
            } else {
                if (!isWeekend(currentTime.minusDays(days))) {
                    sum += tempReading.getKwh() / (currentReading.getDeltaTime() / 60);
                    count++;
                }
            }
        }
        return (sum / count);
    }

    /**
     * Calculates an average for the amount of power used at this time of day on
     * a break for the number of days determined by <code>daysInAverage</code>.
     * If the current reading is on a weekend, only weekends are included in the
     * average and if it is a week day only weekdays are included.
     *
     * @return A double is returned that represents the average power during the
     * breaks around the current time.
     */
    private double getBreakAverage() {
        database.ReadingManager rm = database.Database.getDatabaseManagement().getReadingManager();
        ConditionedReading tempReading;
        double sum = 0;

        LocalDateTime currentTime = currentReading.getDateTime(); //gets the time associated with the current reading
        int count = 0;  //The number of days that have been added to the sum
        for (int days = 1; count < daysInAverage; days++) {
            //continue to the next day if this day isn't on a break it shouldn't be part of the average
            if (!isBreak(currentTime.minusDays(days))) {
                continue;
            }

            tempReading = rm.getLastConditionedReadingForMeterBeforeDateTime(getMainMeter(), currentTime.minusDays(days));
            // if temp readings is null then there are no more values in the database so stop the average there
            if (tempReading == null) {
                break;
            }

            if (isWeekend(currentTime)) {
                if (isWeekend(currentTime.minusDays(days))) {
                    sum += tempReading.getKwh() / (currentReading.getDeltaTime() / 60);
                    count++;
                }
            } else {
                if (!isWeekend(currentTime.minusDays(days))) {
                    sum += tempReading.getKwh() / (currentReading.getDeltaTime() / 60);
                    count++;
                }
            }
        }
        return sum / count;
    }

    /**
     * Gets the identification number of the main meter so that the readings for
     * this meter can be retrieved from the database.
     *
     * @return The identification number of the main meter.
     */
    private int getMainMeter() {
        database.MeterManager mm = database.Database.getDatabaseManagement().getMeterManager();
        return mm.getMeterByName(mainMeterName).getMETER_ID();
    }

    /**
     * Determines if the given date is during school break. A break is
     * considered to be any time between December 15th and January 20th or May
     * 15th and August 25th.
     *
     * @param time The date to check whether or not it is on a break.
     * @return True is returned if school is currently on a break.
     */
    private boolean isBreak(LocalDateTime time) {
        return (time.getMonth() == Month.DECEMBER && time.getDayOfMonth() >= 15
                || time.getMonth() == Month.JANUARY && time.getDayOfMonth() <= 20
                || time.getMonth() == Month.MAY && time.getDayOfMonth() >= 15
                || time.getMonth() == Month.JUNE
                || time.getMonth() == Month.JULY
                || time.getMonth() == Month.AUGUST && time.getDayOfMonth() <= 25);
    }

    /**
     * Determines if the given day is on the weekend. A weekend is when the day
     * of the week is either Saturday or Sunday.
     *
     * @param time The LocalDateTime to check if it is a weekend.
     * @return True is returned if it is a weekend.
     */
    private boolean isWeekend(LocalDateTime time) {
        return (time.getDayOfWeek() == DayOfWeek.SATURDAY
                || time.getDayOfWeek() == DayOfWeek.SUNDAY);
    }

    public static void main(String args[]) {
        PropertyManager.configure("P:/WebProjects/Lucid/web/WEB-INF/config/General.properties");
        WidgetInformation wi = new WidgetInformation();
        wi.updateAll();
        System.out.print("Main Meter id: ");
        System.out.println(wi.getMainMeter());

        //testing if isBreak returns the proper value for different days
//        LocalDateTime time = LocalDateTime.now();
//        System.out.print(time + "   " + time.getDayOfWeek()  + "\n\tis break?\t");
//        System.out.println(wi.isBreak(time));
//        System.out.print("\tis weekend?\t");
//        System.out.println(wi.isWeekend(time));        
//        System.out.print(time.minusMonths(2) + "   " + time.minusMonths(2).getDayOfWeek()  + "\n\tis break?\t");
//        System.out.println(wi.isBreak(time.minusMonths(2)));
//        System.out.print("\tis weekend?\t");
//        System.out.println(wi.isWeekend(time.minusMonths(2)));        
//        System.out.print(time.minusMonths(3) + "   " + time.minusMonths(3).getDayOfWeek()  + "\n\tis break?\t");
//        System.out.println(wi.isBreak(time.minusMonths(3)));
//        System.out.print("\tis weekend?\t");
//        System.out.println(wi.isWeekend(time.minusMonths(3)));        
//        System.out.print(time.minusMonths(4) + "   " + time.minusMonths(4).getDayOfWeek()  + "\n\tis break?\t");
//        System.out.println(wi.isBreak(time.minusMonths(4)));
//        System.out.print("\tis weekend?\t");
//        System.out.println(wi.isWeekend(time.minusMonths(4))); 
//        System.out.print(time.minusMonths(7) + "   " + time.minusMonths(7).getDayOfWeek() + "\n\tis break?\t");
//        System.out.println(wi.isBreak(time.minusMonths(7)));
//        System.out.print("\tis weekend?\t");
//        System.out.println(wi.isWeekend(time.minusMonths(7)));        
//        System.out.print(time.minusMonths(11) + "   " + time.minusMonths(11).getDayOfWeek()  + "\n\tis break?\t");
//        System.out.println(wi.isBreak(time.minusMonths(11))); 
//        System.out.print("\tis weekend?\t");
//        System.out.println(wi.isWeekend(time.minusMonths(11)));       
        System.out.println("Current reading: " + wi.currentReading);
        System.out.println("Current energy:\t" + wi.getCurrentPower());
        System.out.println("Average:\t" + wi.getAverage());
        System.out.println("Percentage:\t" + wi.getPercentage());
    }
}
