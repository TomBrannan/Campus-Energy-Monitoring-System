package servlets;

import common.GreenTip;
import database.GreenTipManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Level;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import utilities.ErrorLogger;

/**
 * GreenTipServlet is the Servlet in charge of interaction between the website
 * and GreenTips.
 *
 * @author cmr98507
 */
public class GreenTipServlet extends HttpServlet {

    private final database.DatabaseManagement dm = database.Database.getDatabaseManagement();
    private final GreenTipManager tipManager = dm.getGreenTipManager();
    private final LinkedList<GreenTip> greenTips = new LinkedList<>();
    private final Random random = new Random();

    private static final int DEFAULT_IMAGE_ID = -1;

    private enum TipChangeStatus {

        UNCHANGED, ADDED, DELETED, FAILED
    };
    private TipChangeStatus changeStatus = TipChangeStatus.UNCHANGED;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config); //To change body of generated methods, choose Tools | Templates.
        ErrorLogger.log(Level.INFO, "BUILDING SERVLET INIT CALLED");
        // Add green tips from the database
        loadTipsFromDatabase();
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
        PrintWriter out = response.getWriter();
        StringBuilder jsonString = new StringBuilder();
        JSONObject jsonObject = new JSONObject();
        String action = request.getParameter("action");
        switch (action) {
            case "getAllTips":
                jsonString.append("{\"tips\":[");
                boolean first = true;
                for (GreenTip greenTip : greenTips) {
                    if (!first) {
                        jsonString.append(",");
                    } else {
                        first = false;
                    }
                    jsonString.append("{\"tipId\":\"").append(greenTip.getGreenTipId()).append("\",\"tipMsg\":\"").append(greenTip.getTipText()).append("\"}");
                }
                jsonString.append("]");
                jsonString.append(",\"status\" : \"").append(changeStatus).append("\"");
                jsonString.append("}");
                jsonObject = new JSONObject(jsonString.toString());
                //change the tip status back to unchanged after the jsp page is loaded.
                changeStatus = TipChangeStatus.UNCHANGED;

                response.setContentType("application/json");  // Set content type of the response so that jQuery knows what it can expect.
                out.print(jsonObject);
                break;
            case "getRandTip":
                String str_currentTipId = request.getParameter("currentTip");
                int currentId = -1;
                try{
                    if(str_currentTipId!=null && !str_currentTipId.equals("")){
                        currentId = Integer.parseInt(str_currentTipId);
                    }
                }catch(NumberFormatException ex){
                    //do nothing
                }
                
                int randomIndex;
                do{
                    randomIndex = random.nextInt(greenTips.size());
                }while(randomIndex==currentId);
                
                GreenTip randomTip = greenTips.get(randomIndex);
                jsonString.append("{\"tipId\":\"").append(randomTip.getGreenTipId()).append("\",\"tipMsg\":\"").append(randomTip.getTipText()).append("\"}");
                jsonObject = new JSONObject(jsonString.toString());

                response.setContentType("application/json");  // Set content type of the response so that jQuery knows what it can expect.
                out.print(jsonObject);
                break;
            case "deleteTip":
                String[] tipIDsToBeDeleted = request.getParameterValues("tips");
                if (tipIDsToBeDeleted != null) {
                    for (String str_id : tipIDsToBeDeleted) {
                        int tipId = -1;
                        try {
                            tipId = Integer.parseInt(str_id);
                        } catch (NumberFormatException ex) {
                            // Just ignore this one
                            ErrorLogger.log(Level.WARNING, "GreenTipServlet.process.deleteTip: tipId parsing error: ID string_value(" + str_id + ")", ex);
                        }
                        // No null errors or anything with this.
                        //if deleteGreenTip returns true, tip was deleted succesfully
                        if (tipManager.deleteGreenTip(tipManager.getGreenTipById(tipId))) {
                            changeStatus = TipChangeStatus.DELETED;
                        } else {
                            changeStatus = TipChangeStatus.FAILED;
                        }
                    }
                    // Reload tips after deleting one or more tips.
                    loadTipsFromDatabase();
                }
                request.getRequestDispatcher("tip.jsp").forward(request, response);
                break;
            case "addTip":
                String newTip = request.getParameter("tipMessage");
                String picId = request.getParameter("tippic");
                int imageId = 1;
                try {
                    if (picId != null && !picId.equals("")) {
                        imageId = Integer.parseInt(picId);
                    } else {
                        imageId = DEFAULT_IMAGE_ID;
                    }
                } catch (NumberFormatException ex) {
                    // Just ignore this one
                    ErrorLogger.log(Level.WARNING, "GreenTipServlet.process.addTip: picId parsing error: ID string_value(" + picId + ")", ex);
                }
                if (newTip != null) {
                    //if addGreenTip does not returns null, tip was added succesfully
                    if (tipManager.addGreenTip(newTip, imageId) != null) {
                        changeStatus = TipChangeStatus.ADDED;
                    } else {
                        changeStatus = TipChangeStatus.FAILED;
                    }
                    //Reload tips after adding a tip.
                    loadTipsFromDatabase();
                }
                request.getRequestDispatcher("tip.jsp").forward(request, response);
                break;
        }

    }

    /**
     * Interface with the database to load all GreenTips into the greenTips
     * LinkedList. Clears greenTips before adding.
     */
    private void loadTipsFromDatabase() {
        Collection<GreenTip> tips = tipManager.getAllGreenTips();
        greenTips.clear();
        greenTips.addAll(tips);
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
