/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import utilities.EmailUtility;

/**
 *
 * @author tll92617
 */
@WebServlet(name = "ForgotPasswordServlet", urlPatterns = {"/ForgotPasswordServlet"})
public class ForgotPasswordServlet extends HttpServlet {

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
            throws ServletException, IOException 
    {
       
        String email = request.getParameter("emailAddress");
        
        database.UserManager um = database.Database.getDatabaseManagement().getUserManager();
        Collection<common.User> userList = um.getAllUsersWithEmailAddress(email);
        
        if(!userList.isEmpty())
        {
            String loginName="";
            String password="";
            String message = "Dear User,\n\nYour password was requested through the "
                    + "GreenPowerProject at Bloomsburg Univeristy. The following "
                    + "username/password combination(s) are associated with your "
                    + "email address:\n";
            Iterator<common.User> iter = userList.iterator();
            while (iter.hasNext())
            {
                common.User user = iter.next();
                loginName = user.getLoginName();
                password = user.getUserPassword();
                message += "\tUsername: " + loginName + "\n\tPassword: " + password + "\n\n";
            }
            message += "Please don't reply to this email.";
            EmailUtility eu = new EmailUtility();
            eu.email(email, message, "Requested Password");
            getServletContext()
                    .getRequestDispatcher("/loginScreen.jsp")
                    .forward(request, response);
        }
        else
        {
            request.setAttribute("message", "Invalid Email.");
            
            getServletContext()
                    .getRequestDispatcher("/ForgotPassword.jsp")
                    .forward(request, response);
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
