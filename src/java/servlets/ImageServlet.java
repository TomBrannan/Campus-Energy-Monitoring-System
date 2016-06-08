/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import common.GImage;
import common.GreenTip;
import database.GImageManager;
import database.GreenTipManager;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import utilities.ErrorLogger;

/**
 *
 * @author cmr98507
 */
public class ImageServlet extends HttpServlet {

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
        ServletOutputStream out = response.getOutputStream();
        database.DatabaseManagement dm = database.Database.getDatabaseManagement();
        GImageManager gm = dm.getGImageManager();
        GreenTipManager gtm = dm.getGreenTipManager();
        String str_greenTipId = request.getParameter("currentTip");
        int greenTipId = 7;
        try {
            if (str_greenTipId != null && !str_greenTipId.equals("")) {
                greenTipId = Integer.parseInt(str_greenTipId);
            }
        } catch (NumberFormatException ex) {
            ErrorLogger.log(Level.WARNING, "GreenTipServlet.process.addTip: picId parsing error: ID string_value(" + str_greenTipId + ")", ex);
        }
        GreenTip tip = gtm.getGreenTipById(greenTipId);
        GImage gImage;
        if (tip.getGimageId() != -1) {
            gImage = gm.getGImageById(tip.getGimageId());
            Image image = gImage.getImage();
            BufferedImage bi = (BufferedImage) image;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bi, "png", baos);
            try (InputStream in = new ByteArrayInputStream(baos.toByteArray())) {
                int length = baos.toByteArray().length;
                int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];
                response.setContentType("image/png");
                while ((length = in.read(buffer)) != -1) {
                    out.write(buffer, 0, length);
                }
            }
        }
        out.flush();

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
