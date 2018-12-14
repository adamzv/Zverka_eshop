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

    String driver = "com.mysql.jdbc.Driver";
    Connection con = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    String db_username = "root";
    String db_password = "";
    String URL = "jdbc:mysql://localhost/zverka_eshop";

    HttpSession session;

    @Override
    public void init() {
        try {
            super.init();
            Class.forName(driver);
            con = DriverManager.getConnection(URL, db_username, db_password);
        } catch (Exception ex) {
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        try {
            con.close();
        } catch (SQLException ex) {
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

        session = request.getSession(false);
        
        if (request.getMethod().equals("POST")) {
            String username = request.getParameter("username");
            String heslo = request.getParameter("heslo");
            String mail = request.getParameter("mail");
            String adresa = request.getParameter("adresa");
            String meno = request.getParameter("meno");
            String priezvisko = request.getParameter("priezvisko");

            if (!username.isEmpty() && !heslo.isEmpty() && !mail.isEmpty()
                    && !adresa.isEmpty() && !meno.isEmpty() && !priezvisko.isEmpty()) {
                try {
                    String kontrola_username = "SELECT COUNT(ID) AS pocet FROM pouzivatelia WHERE mail = ? OR login = ?";
                    pstmt = con.prepareStatement(kontrola_username);
                    pstmt.setString(1, mail);
                    pstmt.setString(2, username);
                    rs = pstmt.executeQuery();
                    rs.next();
                    if (rs.getInt("pocet") >= 1) {
                        session.setAttribute("sprava", "Prihlasovacie meno je obsadené");
                        response.sendRedirect("/registracia");
                    } else {
                        int zlava = (int) (Math.random() * 51);
                        String registracia = "INSERT INTO pouzivatelia (login, heslo, mail, adresa, zlava, meno, priezvisko) VALUES (?, ?, ?, ?, ?, ?, ?)";
                        
                        PreparedStatement pstmta = con.prepareStatement(registracia);
                        pstmta.setString(1, username);
                        pstmta.setString(2, heslo);
                        pstmta.setString(3, mail);
                        pstmta.setString(4, adresa);
                        pstmta.setInt(5, zlava);
                        pstmta.setString(6, meno);
                        pstmta.setString(7, priezvisko);
                        pstmta.executeUpdate();
                        //con.commit();
                        pstmta.close();
                        response.sendRedirect("/login");
                    }
                    pstmt.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } else {
                response.sendRedirect("registracia");
            }
        }

        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            Layout.vypis_html(Layout.ZACIATOK_HTML, out, "Registracia");
            Layout.vypis_navbar(out, session);
            vypis_registraciu(out, session);
            Layout.vypis_footer(out);
            Layout.vypis_html(Layout.KONIEC_HTML, out);
        }
    }

    private void vypis_registraciu(PrintWriter out, HttpSession session) {
        out.println("    <form action=\"/registracia\" method=\"post\">");
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
