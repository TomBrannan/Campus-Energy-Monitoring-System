package servlets;

import datadownload.ReadingDataReport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedList;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import utilities.Debug;

@WebServlet(name = "DownloadDataServlet", urlPatterns = {"/downloadDataServlet"})
public class DownloadDataServlet extends HttpServlet {

    
    /**
     * 
     * @param request
     * @param response
     * @throws IOException
     */
    public void processRequest(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        // get relative path 
        String relativePath = getServletContext().getRealPath("");
        
        Debug.setEnabled(false);
        
        Collection<Integer> meter_ids = new LinkedList<>();
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        
        /*
        START VOLITILE CODE
        */
        
        // 
        for(int i = 1; i <= 3; i++ ){
            meter_ids.add(i);
        }
        startTime = LocalDateTime.now().minusDays(1);
        endTime = startTime.plusDays(1);
        
        /*
        END VOLITILE CODE
        */
        
        // Create new reading report
        ReadingDataReport report = new ReadingDataReport("test", meter_ids, startTime, endTime);
        // populate with raw readings
        report.populateRawReadings();
        // write the file locally
        report.writeFile(relativePath);
        // download the file (send it to the browser for download)
        downloadRelativeFile(request, response, report.getFilePath());
        // delete the file
        report.deleteFile();
    }
    
    
    /*
     SORCES FOR downloadRelativeFile
     http://www.codejava.net/java-ee/servlet/java-servlet-download-file-example
     http://www.mkyong.com/servlet/servlet-code-to-download-text-file-from-website-java/
     */
    
    private void downloadRelativeFile(HttpServletRequest request,
            HttpServletResponse response, String filePath) throws IOException
    {
        File downloadFile = new File(filePath);
        FileInputStream inStream = new FileInputStream(downloadFile);

        // obtains ServletContext
        ServletContext context = getServletContext();

        // gets MIME type of the file
        String mimeType = context.getMimeType(filePath);
        if (mimeType == null) {
            // set to binary type if MIME mapping not found
            mimeType = "application/octet-stream";
        }
        System.out.println("MIME type: " + mimeType);

        // modifies response
        response.setContentType(mimeType);
        response.setContentLength((int) downloadFile.length());

        // forces download
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
        response.setHeader(headerKey, headerValue);

        // obtains response's output stream
        OutputStream outStream = response.getOutputStream();

        byte[] buffer = new byte[4096];
        int bytesRead = -1;

        while ((bytesRead = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }

        inStream.close();
        outStream.close();
    }

    public void doGet(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

}
