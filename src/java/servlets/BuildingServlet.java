package servlets;

import common.Building;
import common.Campus;
import common.Meter;
import common.University;
import database.BuildingManager;
import database.CampusManager;
import database.MeterManager;
import database.UniversityManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import utilities.ErrorLogger;

/**
 *
 * @author cmr98507
 */
public class BuildingServlet extends HttpServlet {

    //get managers
    database.DatabaseManagement dm = database.Database.getDatabaseManagement();
    UniversityManager um = dm.getUniversityManager();
    CampusManager cm = dm.getCampusManager();
    BuildingManager bm = dm.getBuildingManager();
    MeterManager mm = dm.getMeterManager();

    private enum BuildingChangeStatus {

        ADDED, DELETED, MODIFIED, UNCHANGED, FAILED
    };
    private BuildingChangeStatus changeStatus = BuildingChangeStatus.UNCHANGED;

    private final LinkedList<Building> buildings = new LinkedList<>();
    Collection<Building> visibleBuildings = new ArrayList();

    University uni;

    private static final String DEFAULT_UNIVERSITY = "Bloomsburg University of Pennsylvania";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config); //To change body of generated methods, choose Tools | Templates.
        ErrorLogger.log(Level.INFO, "BUILDING SERVLET INIT CALLED");

        // Get Bloomu
        uni = um.getUniversityByName(DEFAULT_UNIVERSITY);
        if (uni != null) {
            //add buildings from database.
            loadBuildingsFromDatabase(uni);
        }

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

        // string sent to the page
        StringBuilder jsonString = new StringBuilder();
        JSONObject jsonObject = new JSONObject();
        // Check for empty, not for null.
        if (!buildings.isEmpty()) {
            String action = request.getParameter("action");
            switch (action) {

                case "getAllBuildings":
                    jsonString.delete(0, jsonString.length());
                    // Make sure the building list is not empty before doing this
                    if (!buildings.isEmpty()) {
                        jsonString.append("{\"buildings\":[");
                        // Add text to the jsonstring for each building
                        boolean first = true;
                        for (Building b : buildings) {
                            if (!first) {
                                jsonString.append(",");
                            } else {
                                first = false;
                            }
                            jsonString.append("{\"buildingId\":\"").append(b.getBuildingId()).append("\",\"buildingName\":\"").append(b.getBuildingName()).append("\"}");
                        }
                        jsonString.append("]");
                        jsonString.append(",\"status\" : \"").append(changeStatus).append("\"");
                        jsonString.append("}");

                        jsonObject = new JSONObject(jsonString.toString());

                        //change the tip status back to unchanged after the jsp page is loaded.
                        changeStatus = BuildingChangeStatus.UNCHANGED;
                    }
                    break;

                case "getAllVisibleBuildings":
                    jsonString.delete(0, jsonString.length());
                    // Make sure the building list is not empty before doing this
                    if (!visibleBuildings.isEmpty()) {
                        jsonString.append("{\"buildings\":[");
                        // Add text to the jsonstring for each building
                        boolean first = true;
                        for (Building b : visibleBuildings) {
                            if (!first) {
                                jsonString.append(",");
                            } else {
                                first = false;
                            }
                            jsonString.append("{\"buildingId\":\"").append(b.getBuildingId()).append("\",\"buildingName\":\"").append(b.getBuildingName()).append("\"}");
                        }
                        jsonString.append("]");
                        jsonString.append(",\"status\" : \"").append(changeStatus).append("\"");
                        jsonString.append("}");

                        jsonObject = new JSONObject(jsonString.toString());

                        //change the tip status back to unchanged after the jsp page is loaded.
                        changeStatus = BuildingChangeStatus.UNCHANGED;
                    }
                    break;

                case "getBuilding": {
                    jsonString.delete(0, jsonString.length());
                    String str_buildingId = request.getParameter("buildings");
                    int buildingId = 0;
                    if (str_buildingId != null) {
                        buildingId = Integer.parseInt(str_buildingId);
                    }
                    Building building = bm.getBuildingByID(buildingId);
                    jsonString.append("{\"building\":");
                    if (building != null) { // shouldn't be null, but just in case.
                        jsonString.append("{\"buildingId\":\"").append(building.getBuildingId());
                        jsonString.append("\",\"buildingName\":\"").append(building.getBuildingName());
                        jsonString.append("\"," + "\"campusName\":\"").append(building.getCampusId());
                        jsonString.append("\",\"sqFt\":\"").append(building.getSqFootage());
                        jsonString.append("\",\"occupancy\":\"").append(building.getOccupancy());
                        jsonString.append("\",\"visible\":\"").append(building.getVisible()).append("\"}");
                    } else {
                        jsonString.append("null");
                    }
                    jsonString.append("}");
                    jsonObject = new JSONObject(jsonString.toString());
                    break;
                }

                case "modifyBuildings": {
                    String buttonName = request.getParameter("buttonName");
                    String str_buildingId = request.getParameter("buildings");
                    int buildingId = 0;
                    try{
                    if (str_buildingId != null) {
                        buildingId = Integer.parseInt(str_buildingId);
                    }
                    }catch(NumberFormatException ex){
                        ErrorLogger.log(Level.WARNING, "BuildingServlet.process.modifyBuilding: parsing error: ID string_value("+str_buildingId+")", ex);
                    }
                    if (buttonName.equals("Delete Building")) {
                        Building building = bm.getBuildingByID(buildingId);
                        if (building != null) {
                            if (bm.deleteBuilding(building)) {
                                changeStatus = BuildingChangeStatus.DELETED;
                            } else {
                                changeStatus = BuildingChangeStatus.FAILED;
                            }
                        }
                        loadBuildingsFromDatabase(uni);
                        request.getRequestDispatcher("building.jsp").forward(request, response);
                    } else {
                        String buildingName = request.getParameter("name");
                        String campus = request.getParameter("campus");
                        String sqft = request.getParameter("sqft");
                        String occ = request.getParameter("occ");
                        boolean visible = false;
                        if (request.getParameter("visible") != null) {
                            visible = true;
                        }
                        int campusId = -1;
                        int squareFootage = -1;
                        int occupancy = -1;
                        try {
                            if (campus != null && !campus.equals("")) {
                                campusId = Integer.parseInt(campus);
                            }
                            if (sqft != null && !sqft.equals("")) {
                                squareFootage = Integer.parseInt(sqft);
                            }
                            if (occ != null && !occ.equals("")) {
                                occupancy = Integer.parseInt(occ);
                            }
                        } catch (NumberFormatException ex) {
                            // Just ignore this one
                            ErrorLogger.log(Level.WARNING, "BuildingServlet.process.modifyBuilding: parsing error: ID string_value", ex);
                        }

                        if (buildingId != -1) {
                            Building buildingToBeModified = bm.getBuildingByID(buildingId);
                            if (buildingToBeModified != null) {
                                buildingToBeModified.setBuildingName(buildingName);
                                buildingToBeModified.setCampusId(campusId);
                                buildingToBeModified.setgImageId(1);
                                buildingToBeModified.setSqFootage(squareFootage);
                                buildingToBeModified.setOccupancy(occupancy);
                                buildingToBeModified.setVisible(visible);
                                if (bm.updateBuilding(buildingToBeModified)) {
                                    changeStatus = BuildingChangeStatus.MODIFIED;
                                } else {
                                    changeStatus = BuildingChangeStatus.FAILED;
                                }
                            }
                        } else if (buildingId == -1) {
                            if (bm.addBuilding(buildingName, 1, occupancy, squareFootage, campusId, visible) != null) {
                                changeStatus = BuildingChangeStatus.ADDED;
                            } else {
                                changeStatus = BuildingChangeStatus.FAILED;
                            }
                        }

                        loadBuildingsFromDatabase(uni);
                        request.getRequestDispatcher("building.jsp").forward(request, response);
                    }
                    break;
                }

                default:
                    jsonString.append("NONE");
                    break;
            }

        } else {
            //jsonString += "<option>No Buildings loaded in database</option>";
        }

        response.setContentType("application/json");  // Set content type of the response so that jQuery knows what it can expect.
        out.print(jsonObject);
    }

    private void loadBuildingsFromDatabase(University uni) {
        Collection<Campus> campusList = cm.getAllCampusInUniversity(uni.getUniversityId());
        Collection<Building> tempBuildings = new LinkedList();
        Collection<Building> tempVisibleBuildings = new LinkedList();
        if (!campusList.isEmpty()) {
            for (Campus c : campusList) {
                {
                    tempVisibleBuildings.addAll(bm.getAllVisibleBuildingsByCampus(c));
                    tempBuildings.addAll(bm.getAllBuildingsByCampus(c));
                }
            }
        }
        buildings.clear();
        buildings.addAll(tempBuildings);
        visibleBuildings.clear();
        visibleBuildings.addAll(tempVisibleBuildings);
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
