package utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Scans and retrieves delimited values from a given file.
 * @author Collin Shoop
 */
public class CSVFileReader {
    
    private static final String DEFAULT_DELIMITER = ",";
    
    private String path;
    private Scanner scanner;
    private String delimiter = DEFAULT_DELIMITER;
    
    /**
     * Creates a default CSVFileReader which will read values from the given
     * CSV file. This reader will interpret any file given as a .csv file.
     * By default, the delimiter used is a comma ",".
     * @param path Path of the file.
     * @throws FileNotFoundException Thrown if the file path given does not refer
     * to an existing file.
     */
    public CSVFileReader(String path) throws FileNotFoundException
    {
        this.path = path;
        scanner = new Scanner(new File(this.path));
    }
    
    /**
     * Set the delimiter used for splitting values.
     * @param delimiter The new delimiter.
     */
    public void setDelimiter(String delimiter)
    {
        if (delimiter != null) {
            this.delimiter = delimiter;
        }
    }
    
    /**
     * Scans the CSV file for all arguments in the next row. 
     * @return Return an array containing all values found in the next row. All 
     * values are trimmed using <code>String.trim()</code>.
     */
    public String[] getNextRow()
    {
        if (!scanner.hasNextLine()) {
            return null;
        }
        String line = scanner.nextLine() + " ";
        String[] args = line.split(delimiter);
        for(int i = 0; i < args.length; i++)
            args[i] = args[i].trim();
        return args;
    }
    
    /**
     * Detects whether or not there are more values in the CSV file.
     * @return True if there are no more values in the CSV file. Otherwise false.
     */
    public boolean EOF()
    {
        return !scanner.hasNext();
    }
    
    public void close(){
        if (scanner != null) scanner.close();
    }
    
    public static void main(String[] args)
    {
        Debug.setEnabled(true);
        CSVFileReader reader =null;
        try {
             reader = new CSVFileReader("testFile1.csv");
            while(!reader.EOF())
            {
                String[] row_values = reader.getNextRow();
                System.out.print("[");
                for(String s : row_values) {
                    System.out.print("\"" + s + "\"    ");
                }
                System.out.println("]");
            }
        } catch (FileNotFoundException ex) {
            Debug.println("File does not exist");
        }
        if(reader != null)  reader.close();
    }

    @Override
    public String toString() {
        return "CSVFileReader[Path:" + path + "]";
    }
    
    
    
}
