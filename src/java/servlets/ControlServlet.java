package servlets;

import common.User;
import database.UserManager;
import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.time.LocalDateTime;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import utilities.Debug;
import utilities.ErrorLogger;
import utilities.PropertyManager;

@WebServlet(name = "ControlServlet", urlPatterns = {"/ControlServlet"})
public class ControlServlet extends HttpServlet {

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        // Parameters available to all servlets are in the ServletContext object
        ServletContext sc = getServletContext();
        //How to get the actual path to a relative directory
        String actualPath = sc.getRealPath("/WEB-INF");

        Debug.println("ACTUAL PATH: " + actualPath);

        //Get parameters from web.xml
        String propertyFileLocation = servletConfig.getInitParameter("PropertyFilePath");//Only available to this servlet
        String propertyFilePath = sc.getRealPath(propertyFileLocation);
        String encryptKeyLocation = servletConfig.getInitParameter("EncryptKeyPath");//Only available to this servlet
        String encryptFilePath = sc.getRealPath(encryptKeyLocation);
        String logFileLocation = servletConfig.getInitParameter("LogFileLocation");
        String logFilePath = sc.getRealPath(logFileLocation);
        String logFileName = servletConfig.getInitParameter("LogFileName");
        String configuration = servletConfig.getInitParameter("Configuration");

        //Configure system
        ErrorLogger.initializeLogging(logFilePath, logFileName);
        PropertyManager.configure(propertyFilePath);

        //Encryption.initialize(encryptFilePath);
        PropertyManager.setProperty("WEB-INF Real Path", actualPath);
        PropertyManager.setProperty("PropertyFilePath", propertyFilePath);
        PropertyManager.setProperty("LogFileLocation", logFilePath);
        PropertyManager.setProperty("LogFileName", logFileName);
        PropertyManager.setProperty("Configuration", configuration);

        database.DatabaseManagement db = database.Database.getDatabaseManagement();
        db.initializeDatabaseManagement();
        
        //Log to server logs
        log("******************* Green Power Web Application: Real Path is " + actualPath + " ********************");
        //Log to our log file
    }

    @Override
    public void destroy() {
        //Reclaim any resources
        /*
         Called by the servlet container to indicate to a servlet that the servlet 
         is being taken out of service. 
         This method is only called once all threads within the servlet's service 
         method have exited or after a timeout period has passed. After the servlet 
         container calls this method, it will not call the service method again on this servlet.

         This method gives the servlet an opportunity to clean up any resources 
         that are being held (for example, memory, file handles, threads) and make 
         sure that any persistent state is synchronized with the servlet's current 
         state in memory. 
         */
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        
        
//        EmailUtility.emailAdmin("Testing email from servlet", "test");  //Tested and worked on 3/15/2015 
        String username = request.getParameter("username").trim();
        String password = request.getParameter("password").trim();// good idea to always trim strings
//        if (command == null) { //Always protect against null 
//            //Forward to index.html
//            getServletContext()
//                .getRequestDispatcher("/ISIS/waitlist/loginScreen.html")
//                .forward(request, response);
//        }

        UserManager um = database.Database.getDatabaseManagement().getUserManager();
        User user = um.validateUser(username, password);
        HttpSession session = request.getSession(true);
        
        if(user != null){                         
             user.setLoginCount((user.getLoginCount()+1));             
             LocalDateTime now = LocalDateTime.now();
             user.setLastLogin(now);
             session.setAttribute("user", user);             

//             if( user.getUserRole() == common.UserRole.SystemAdmin)
//             {
//                 request.setAttribute("organization","ADMIN");
//             }
                               
             um.updateUser(user);
             
             getServletContext()
                    .getRequestDispatcher("/graphs.jsp")
                    .forward(request, response);             
             
        }else{
           request.setAttribute("errorMessage", "Invalid username or password.");
           request.setAttribute("username", username);
            
           getServletContext()
                    .getRequestDispatcher("/loginScreen.jsp")
                    .forward(request, response);
        }
        
        //String JSP_URL ="/Error.jsp"; //default command -- don't forget the forward slash in front of JSP file name. 
       // String errorMessage ="An Error Occurred. Please retry the application."; //default error errorMessage 
        
//        if (command.equals("MainOptions")) {
//            String option = request.getParameter("MainOptions").trim(); // good idea to always trim strings  
//            
//            if(option==null){
//              //Forward to index.html if something goes wrong
//                  getServletContext()
//                    .getRequestDispatcher("/ISIS/waitlist/loginScreen.html")
//                    .forward(request, response);
//                  
//            }
//            if(option.equals("Login")){
//                
//                //test code.
//                PrintWriter out =response.getWriter();
//                out.print("<html><head><title>haha</title></head><body><h1>"+option+"</h1></body></html>");
//                
//            }
//            
//       }

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
