package utilities;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

/**
 * API for creating and saving a values to a .csv file
 * @author cws55854
 */
public class CSVFileWriter {
    
    private String filename;
    private FileWriter csvWriter;
    private boolean atLineBeginning;
    
    /**
     * Initializes a new .csv file with the given filename.
     * If a .csv file with the given file name already exists, the file will be overwritten.
     * @param filename Name of the .csv file. Resulting file name will be [filename].csv
     * @throws IOException 
     */
    public CSVFileWriter(String filename) throws IOException
    {
        this(filename, false);
    }
    
    /**
     * Initializes a new .csv file with the given filename.
     * If the .csv file already exists, new values will be appended to the existing file.
     * @param filename Name of the .csv file. Resulting file name will be [filename].csv
     * @param append True to append values to an existing file. False to override any 
     * preexisting file with the same file name.
     * @throws IOException 
     */
    public CSVFileWriter(String filename, boolean append) throws IOException
    {
        this.filename = filename;
        if (this.filename == null) {
            return;
        }
        else if (!this.filename.contains(".csv")) {
            this.filename += ".csv";
        }
        atLineBeginning = true;
        
        System.out.println("CSVFileWriter: " + this.filename);
        csvWriter = new FileWriter(this.filename, append);
    }
    
    
    
    /**
     * Appends this value to the end of the current row. Will insert commas appropriately
     * to follow the .csv format.
     * @param value Value to write
     * @throws IOException 
     */
    public void writeValue(String value) throws IOException
    {
        if (csvWriter == null) {
            return;
        }
        
        if (atLineBeginning) {
            csvWriter.write(value);
        }
        else {
            csvWriter.write("," + value);
        }
        atLineBeginning = false;
    }
    
    /**
     * Appends the given values to the end of the current row. Will insert commas appropriately
     * to follow the .csv format.
     * @param values Values to write
     * @throws IOException 
     */
    public void writeValues(String[] values) throws IOException
    {
        if (values == null) {
            return;
        }
        for(String value : values)
        {
            writeValue(value);
        }
    }
    
    /**
     * Appends this value to the end of the current row. Will insert commas appropriately
     * to follow the .csv format.
     * @param value Value to write
     * @throws IOException 
     */
    public void writeValue(int value) throws IOException
    {
        CSVFileWriter.this.writeValue(Integer.toString(value));
    }
    
    /**
     * Appends this value to the end of the current row. Will insert commas appropriately
     * to follow the .csv format.
     * @param value Value to write
     * @throws IOException 
     */
    public void writeValue(double value) throws IOException
    {
        CSVFileWriter.this.writeValue(Double.toString(value));
    }
    
    /**
     * Starts a new row by writing a new-line ('\n') character.
     * @throws IOException 
     */
    public void writeLine() throws IOException
    {
        atLineBeginning = true;
        if (csvWriter == null) {
            return;
        }
        csvWriter.write("\n");
    }
    
    /**
     * Closes the associated .csv file.
     */
    public void close() 
    {
        if (csvWriter == null) {
            return;
        }
        try {
            csvWriter.flush();
            csvWriter.close();
        } catch (IOException ex) {
            
        }
    }
    
    /*
     * Writes some data to a .csv file using an instance of the CSVFile class.
     * The format is SIMILAR to the data we will be given but not exact. 
     */
    public static void main(String...args) throws IOException
    {
        CSVFileWriter testFile = new CSVFileWriter("csv_test1.csv");
        String[] headers1 = { "Key", "Name:Suffix", "Trend Definitions Used" };
        testFile.writeValues(headers1);
        testFile.writeLine();
        for(int i = 0; i < 15; i++)
        {
            writeTestMeterDefinitions(testFile, i);
        }
        
        testFile.writeLine();
        String[] headers2 = { "<>Date", "Time", "Point0", "Point1", "Point2", "Point3", "Point4" };
        testFile.writeValues(headers2);
        testFile.writeLine();
        for(int i = 0; i < 100; i++)
            writeTestMeterValues(testFile, 5);
        
        testFile.close();
    }
    
    public static void writeTestMeterDefinitions(CSVFileWriter csv, int n) throws IOException
    {
        csv.writeValue("Point" + n + ":");
        csv.writeValue("METER_NAME" + n);
        csv.writeValue("TIME_INTERVAL" + n);
        csv.writeLine();
    }
    
    private static Random random = new Random();
    public static void writeTestMeterValues(CSVFileWriter csv, int num_meters) throws IOException
    {
        // write random date
        int year = 2002 + random.nextInt(12);
        int month = 1+random.nextInt(12);
        int day = 1+random.nextInt(30);
        csv.writeValue("" + month + "/" + day + "/" + year);
        
        int hour = random.nextInt(24);
        int minute = random.nextInt(60);
        int second = random.nextInt(60);
        csv.writeValue("" + hour + ":" + minute + ":" + second);
        
        for(int i = 0; i < num_meters; i++)
        {
            csv.writeValue(random.nextDouble()*5000);
        }
        csv.writeLine();
    }
    
    
    
}
