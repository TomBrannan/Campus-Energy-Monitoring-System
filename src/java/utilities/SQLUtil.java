
package utilities;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;



public class SQLUtil {
    /**
     * This method returns a database table formatted  as an HTML table. 
     * The resulting table has 
     * the colum names as specified in the database. 
     * 
     * @param results <code> ResultSet </code> containing the database table. 
     * @return <code> String </code> containing an HTML table containing the database
     * table data. 
     * @throws SQLException 
     */
    
    public static String getHtmlTableFromResultSet(ResultSet results)
            throws SQLException {
        
        StringBuilder htmlTable = new StringBuilder();
        ResultSetMetaData metaData = results.getMetaData();
        int columnCount = metaData.getColumnCount();

        htmlTable.append("<table>");

        // add header row
        htmlTable.append("<tr>");
        for (int i = 1; i <= columnCount; i++) {
            htmlTable.append("<th>");
            htmlTable.append(metaData.getColumnName(i));
            htmlTable.append("</th>");
        }
        htmlTable.append("</tr>");

        // add all other rows
        while (results.next()) {
            htmlTable.append("<tr>");
            for (int i = 1; i <= columnCount; i++) {
                htmlTable.append("<td>");
                htmlTable.append(results.getString(i));
                htmlTable.append("</td>");
            }
            htmlTable.append("</tr>");
        }

        htmlTable.append("</table>");
        return htmlTable.toString();
    }
}
