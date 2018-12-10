/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zverka_eshop;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author adamz
 */
@WebServlet(name = "Registracia", urlPatterns = {"/registracia"})
public class Registracia extends HttpServlet {

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

        HttpSession session = request.getSession();

        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            Layout.vypis_html(Layout.ZACIATOK_HTML, out, "Registracia");
            Layout.vypis_navbar(out, session);
            vypis_registraciu(out);
            Layout.vypis_footer(out);
            Layout.vypis_html(Layout.KONIEC_HTML, out);
        }
    }

    private void vypis_registraciu(PrintWriter out) {
        out.println("    <form action=\"/register\" method=\"post\">");
        out.println("        <div class=\"form-row\">");
        out.println("            <div class=\"col-md-4 mx-auto my-1\">");
        out.println("                <input autocomplete=\"off\" autofocus class=\"form-control\" name=\"username\" placeholder=\"Prihlasovacie meno\"");
        out.println("                       type=\"text\"/>");
        out.println("            </div>");
        out.println("        </div>");
        out.println("        <div class=\"form-row\">");
        out.println("            <div class=\"col-md-4 mx-auto my-1\">");
        out.println("                <input class=\"form-control\" name=\"heslo\" placeholder=\"Heslo\" type=\"password\"/>");
        out.println("            </div>");
        out.println("        </div>");
        out.println("        <div class=\"form-row\">");
        out.println("            <div class=\"col-md-4 mx-auto my-1\">");
        out.println("                <input autocomplete=\"off\" autofocus class=\"form-control\" name=\"mail\" placeholder=\"Mail\"");
        out.println("                       type=\"email\"/>");
        out.println("            </div>");
        out.println("        </div>");
        out.println("        <div class=\"form-row\">");
        out.println("            <div class=\"col-md-4 mx-auto my-1\">");
        out.println("                <input autocomplete=\"off\" autofocus class=\"form-control\" name=\"adresa\" placeholder=\"Adresa\"");
        out.println("                       type=\"text\"/>");
        out.println("            </div>");
        out.println("        </div>");
        out.println("        <div class=\"form-row\">");
        out.println("            <div class=\"col-md-4 mx-auto my-1\">");
        out.println("                <input autocomplete=\"off\" autofocus class=\"form-control\" name=\"meno\" placeholder=\"Meno\"");
        out.println("                       type=\"text\"/>");
        out.println("            </div>");
        out.println("        </div>");
        out.println("        <div class=\"form-row\">");
        out.println("            <div class=\"col-md-4 mx-auto my-1\">");
        out.println("                <input autocomplete=\"off\" autofocus class=\"form-control\" name=\"priezvisko\" placeholder=\"Priezvisko\"");
        out.println("                       type=\"text\"/>");
        out.println("            </div>");
        out.println("        </div>");
        out.println("        <div class=\"form-row\">");
        out.println("            <div class=\"mx-auto\">");
        out.println("                <button class=\"btn btn-primary m-1\" type=\"submit\">Register</button>");
        out.println();
        out.println("            </div>");
        out.println("        </div>");
        out.println("    </form>");
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
