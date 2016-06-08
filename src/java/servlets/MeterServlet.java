/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import common.Building;
import common.Meter;
import database.BuildingManager;
import database.MeterManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.logging.Level;
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
public class MeterServlet extends HttpServlet {

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
        JSONObject jsonObject;
        database.DatabaseManagement dm = database.Database.getDatabaseManagement();
        MeterManager mm = dm.getMeterManager();
        BuildingManager bm = dm.getBuildingManager();

        String action = request.getParameter("action");
        String str_meterId;
        String str_buildingId;
        int meterId, buildingId;
        switch (action) {
            case "getBuildingMeters":
                jsonString.delete(0, jsonString.length());
                str_buildingId = request.getParameter("building");
                buildingId = 0;
                try {
                    if (str_buildingId != null && !str_buildingId.equals("")) {
                        buildingId = Integer.parseInt(str_buildingId);
                    }
                } catch (NumberFormatException ex) {
                    ErrorLogger.log(Level.WARNING, "BuildingServlet.process.modifyBuilding: parsing error: ID string_value(" + str_buildingId + ")", ex);
                }

                Building building = bm.getBuildingByID(buildingId);
                Collection<Meter> meters = mm.getMetersByBuilding(building);
                if (meters.isEmpty()) {
                    jsonString.append("{}");
                } else {
                    jsonString.append("{\"buildingMeters\":[");
                    boolean first = true;
                    for (Meter meter : meters) {
                        if (!first) {
                            jsonString.append(",");
                        } else {
                            first = false;
                        }
                        jsonString.append("{\"meterId\":\"").append(meter.getMETER_ID());
                        jsonString.append("\",\"meterName\":\"").append(meter.getMeterName());
                        jsonString.append("\"}");
                    }
                    jsonString.append("]}");
                }

                jsonObject = new JSONObject(jsonString.toString());

                response.setContentType("application/json");  // Set content type of the response so that jQuery knows what it can expect.
                out.print(jsonObject);
                break;

            case "getOtherMeters":
                jsonString.delete(0, jsonString.length());
                Collection<Meter> otherMeters = mm.getMetersByBuilding(bm.getBuildingByID(0));
                if (!otherMeters.isEmpty()) {
                    jsonString.append("{\"otherMeters\":[");
                    boolean first = true;
                    for (Meter meter : otherMeters) {
                        if (!first) {
                            jsonString.append(",");
                        } else {
                            first = false;
                        }
                        jsonString.append("{\"meterId\":\"").append(meter.getMETER_ID()).append("\",\"meterName\":\"").append(meter.getMeterName()).append("\"}");
                    }
                    jsonString.append("]}");
                }

                jsonObject = new JSONObject(jsonString.toString());

                response.setContentType("application/json");  // Set content type of the response so that jQuery knows what it can expect.
                out.print(jsonObject);
                break;

            case "removeMeter":
                str_meterId = request.getParameter("meter");
                meterId = -1;
                try {
                    if (str_meterId != null && !str_meterId.equals("")) {
                        meterId = Integer.parseInt(str_meterId);
                    }
                } catch (NumberFormatException ex) {

                }
                Meter meterToBeRemoved = mm.getMeterById(meterId);
                meterToBeRemoved.setBuildingId(0);
                String successString;
                if (mm.updateMeter(meterToBeRemoved)) {
                    successString = "true";
                } else {
                    successString = "false";
                }

                response.setContentType("text/plain");
                out.write(successString);
                break;
            case "addMeter":
                str_meterId = request.getParameter("meter");
                str_buildingId = request.getParameter("buildings");
                buildingId = -1;
                meterId = -1;
                try {
                    if (str_meterId != null && !str_meterId.equals("")) {
                        meterId = Integer.parseInt(str_meterId);
                    }
                    if (str_buildingId != null && !str_buildingId.equals("")) {
                        buildingId = Integer.parseInt(str_buildingId);
                    }
                } catch (NumberFormatException ex) {

                }
                Meter meterToBeModified = mm.getMeterById(meterId);
                meterToBeModified.setBuildingId(buildingId);
                if (mm.updateMeter(meterToBeModified)) {
                    successString = "true";
                } else {
                    successString = "false";
                }
                response.setContentType("text/plain");
                out.write(successString);
                break;
        }

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
