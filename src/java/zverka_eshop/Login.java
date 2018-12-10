/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zverka_eshop;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
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
@WebServlet(name = "Login", urlPatterns = {"/login"})
public class Login extends HttpServlet {

    String driver = "com.mysql.jdbc.Driver";
    Connection con = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    String db_username = "root";
    String db_password = "";
    String URL = "jdbc:mysql://localhost/zverka_eshop";
    
    HttpSession session;
    Integer user_id = 0;
    
    @Override
    public void init() {
        try {
            super.init();
            Class.forName(driver);
            con = DriverManager.getConnection(URL, db_username, db_password);
        } catch (Exception ex) {
            
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
        
        if (request.getMethod().equals("POST")) {
            System.out.println("OK");
            String username = request.getParameter("username");
            String heslo = request.getParameter("heslo");
            
            if (user_id == 0) {
                user_id = OverUsera(username, heslo);
                if (user_id == 0) {
                    response.sendRedirect("/login");
                }
                ZapamatajUdajeOUserovi(user_id);
            }
        }
        
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            Layout.vypis_html(Layout.ZACIATOK_HTML, out, "Login");
            Layout.vypis_navbar(out);
            vypis_login(out);
            Layout.vypis_footer(out);
            Layout.vypis_html(Layout.KONIEC_HTML, out);
        }
    }

    private Integer OverUsera(String username, String heslo) {
        int vysledok = 0;
        try {
            String SQL = "SELECT MAX(ID) AS iid, COUNT(ID) AS pocet FROM pouzivatelia "
                    + "WHERE login = ? AND heslo = ?";
            pstmt = con.prepareStatement(SQL);
            pstmt.setString(0, username);
            pstmt.setString(1, heslo);
            rs = pstmt.executeQuery();
            
            rs.first();
            if (rs.getInt("pocet") == 1) {
                vysledok = rs.getInt("iid");
            }
            pstmt.close();
        } catch (Exception ex) {
            return 0;
        }
        return vysledok;
    }
    
    private void ZapamatajUdajeOUserovi(Integer user_id) {
        try {
            // dopisat dalsie udaje do session?
            pstmt = con.prepareStatement("SELECT login FROM pouzivatelia WHERE ID = ?");
            pstmt.setInt(0, user_id);
            rs = pstmt.executeQuery();
            
            rs.first();
            
            session.setAttribute("user_id", (Integer) user_id);
            session.setAttribute("username", rs.getString("login"));
            
            pstmt.close();
        } catch (SQLException ex) {
        }
    }
    
    private void vypis_login(PrintWriter out) {
        out.println("    <form action=\"/login\" method=\"post\">");
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
        out.println("            <div class=\"mx-auto\">");
        out.println("                <button class=\"btn btn-primary m-1\" type=\"submit\">Prihlásiť sa</button>");
        out.println("                <p class=\"m-1\">");
        out.println("                    <a href=\"/register\">Vytvoriť nový účet</a>");
        out.println("                </p>");
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
