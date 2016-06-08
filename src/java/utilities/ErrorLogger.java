package utilities;

import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class works for testing code. It shows the error message in NetBeans' 
 * output window and writes it to a file. 
 * The problem with production code is that the file is difficult to find 
 * when the application is deployed and no user is available to see console 
 * written output. 
 * 
 * @author cjones
 */
public class ErrorLogger {
    //Change the filename for your applications 
    private static final String errorFileName="Error_Log";
    //Set the following to false when the application is deployed 
    public static boolean showLogInErrorWindow = true; 
    
    private static Logger errorLogger;
    
    /**
     * Creates a new logger object, and
     * set it to only write to the console.  
     * The Logger object is saved in the static Logger object errorLogger.
     * 
     * @throws IOException If the property file specified does 
     *                     not exists or can not be read. 
     *
     */
    
    private static void initializeLogging()  {
            String logFile = getLogfileName();
            errorLogger = Logger.getLogger(logFile);
    }

    
    /**
     * Creates a new log file name, creates a new logger object, and
     * initializes a new FileHandler to handle the logger.  The Logger
     * object is saved in the static Logger object errorLogger.
     * 

     *
     * @param logDirectoryPath
     * @param errorLogName
     */
    
    public static void initializeLogging(String logDirectoryPath, String errorLogName)  {
        if(errorLogName==null || errorLogName.isEmpty() ){
           errorLogName = getLogfileName(errorFileName);  
        }
        else errorLogName = getLogfileName(errorLogName);
        
        String filePath = logDirectoryPath +"/"+ errorLogName;
        errorLogger = Logger.getLogger(errorLogName);
        Handler handler;
        try {
            handler = new FileHandler(filePath);        
            handler.setFormatter(new java.util.logging.SimpleFormatter());
            if(showLogInErrorWindow) errorLogger.setUseParentHandlers(true);
            else errorLogger.setUseParentHandlers(false);
            errorLogger.addHandler(handler);
        } catch (IOException | SecurityException ex) {
            errorLogger.log(Level.SEVERE, "Could not create file handler for error logger", ex);
        }

    }

    /**
     * Takes the given date and formats it into an easily readable String
     * representing this date. 
     * 
     * Date format: MM-dd-yyyy
     *
     * @param date The date we want to format.
     * @return An easily read, String representation of the given date.
     */
    private static String getFormattedDate(Date date) {
        
        DateFormat format;
        format = new SimpleDateFormat ("MM-dd-yyyy");

        return (format.format (date));
    }

    /**
     * Takes the given date, extracts the time out of it and creates a String
     * representation of this time that is easily read. 
     * 
     * Time format: HH.MM.AM/PM
     *
     * @param date The date from which we want to extract the time from.
     * @return A string representation of the time extracted from the given date
     * object.
     */
    private static String getFormattedTime(Date date) {
        DateFormat format;
        format = new SimpleDateFormat ("hh.mm.a");

        return (format.format (date));
    }

    /**
     * Uses the other utility methods in this class to create a date/time
     * string that is formatted in a way that is convenient for naming the
     * Weather Error file.
     * 
     * @param date The date we want to create string for.
     * @return A string that represents the data and time contained in the given
     * date.
     */
    private static String getDateTime(Date date) {
        String sDateTime = getFormattedDate(date) + "_" +
                           getFormattedTime(date);
        return sDateTime;
    }

    /**
     * Uses the current system time to create a log file name 
     * for logging errors. 
     *
     * @return A string log file name that will be unique to the minute that
     * this method was called.
     */
    private static String getLogfileName()    {
        return getNewLogFileName(errorFileName,".log");
    }

    private static String getLogfileName(String errorLogName) {
       return getNewLogFileName(errorLogName,".log");
    }
    
    
    /**
     * Uses the given log file base name and extension and the current system
     * time to create a log file name used for creating a logger object for
     * logging exceptions. 
     * 
     * 
     * @param logFileBase Base name for the log file.
     * @param logFileExt Extension for the log file.
     * @return A string log file name that will be unique to the minute that
     * this method was called.
     */
    private static String getNewLogFileName(String logFileBase, String logFileExt) {
        String logFile = logFileBase;
        Date date = new Date(System.currentTimeMillis());
        String sFormattedDateTime = getDateTime(date);
        logFile += "_" + sFormattedDateTime;
        logFile += logFileExt;
        return logFile;
    }

    /**
     * Log message and exception with timestamp at the given standard log level.
     *
     * @param level The Level of the message.
     * @param message The message to log with the exception.
     * @param ex The exception that is being logged.
     */
    public static void log(Level level, String message, Throwable ex) {
        if(errorLogger == null) initLogger();
        errorLogger.log(level, message, ex);
    }

    /**
     * Log message with timestamp at the given standard log level.
     *
     * @param level The Level of the log message.
     * @param message The message to log.
     */
    public static void log(Level level, String message){
        if(errorLogger == null){
            initLogger();
        }
        errorLogger.log(level, message);
    }

    /**
     * Initializes logging. If initialization fails, a default logger is
     * returned that will log messages to console output.
     */
    private static void initLogger() {
      initializeLogging();
    }
    
    public static void main(String [] args){
        ErrorLogger.log(Level.SEVERE, "Test error Message");
    }
}
