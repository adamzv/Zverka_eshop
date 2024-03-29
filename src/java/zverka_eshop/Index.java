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
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author adamzv
 */
@WebServlet(name = "Index", urlPatterns = {"/index"})
public class Index extends HttpServlet {

    String driver = "com.mysql.jdbc.Driver";
    Connection con = null;
    Statement stmt = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    String db_username = "root";
    String db_password = "";
    String URL = "jdbc:mysql://localhost/zverka_eshop";

    HttpSession session;
    Integer user_id = 0;
    String username = null;

    @Override
    public void init() {
        try {
            super.init();
            Class.forName(driver);
            con = DriverManager.getConnection(URL, db_username, db_password);
        } catch (Exception ex) {
            ex.printStackTrace();
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
        response.setContentType("text/html;charset=UTF-8");
        session = request.getSession();

        // skontroluje, či je používateľ prihlásený, ak nie je tak ho pošle na login servlet
        Integer user = (Integer) session.getAttribute("user_id");
        if (user == null || user_id == null) {
            response.sendRedirect("/eshop/login");
        } else {
            user_id = user;
            username = (String) session.getAttribute("username");
            if (session.getAttribute("prava").equals("admin")) response.sendRedirect("admin");
        }

        if (request.getMethod().equals("POST")) {
            ZapisDoKosika(user_id, request.getParameter("id_tovaru"), request.getParameter("cena_tovaru"), request.getParameter("pocet"));
            // spraví redirect, aby používateľ refreshnutím stránky nepridal ďalší tovar do košíka
            response.sendRedirect("index");
        }
        
        try (PrintWriter out = response.getWriter()) {
            Layout.vypis_html(Layout.ZACIATOK_HTML, out, "Index");
            Layout.vypis_navbar(out, session);
            vypis_index(out, username);
            Layout.vypis_footer(out);
            Layout.vypis_html(Layout.KONIEC_HTML, out);
        }
    }

    private void vypis_index(PrintWriter out, String username) {
        out.println("    <div class=\"jumbotron\">");
        out.println("        <h1 class=\"display-4\">Vitajte, " + username +".</h1>");
        out.println("        </br>");
        out.println("        <hr class=\"my-4\">");
        out.println("    </div>");
        out.println("    <div class=\"container\">");
        out.println("    <table class=\"table table-striped table-bordered\">");
        out.println("        <thead>");
        out.println("            <tr>");
        out.println("                <th scope=\"col\"></th>");
        out.println("                <th scope=\"col\">Názov</th>");
        out.println("                <th scope=\"col\">Mierka</th>");
        out.println("                <th scope=\"col\">Výrobca</th>");
        out.println("                <th scope=\"col\">Počet ks</th>");
        out.println("                <th scope=\"col\">Cena</th>");
        out.println("                <th scope=\"col\"></th>");
        out.println("            </tr>");
        out.println("        </thead>");
        out.println("        <tbody>");
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM sklad");
            while (rs.next()) {
                out.println("                <tr>");
                out.println("                    <td><img src=\"" + getServletContext().getContextPath() + "\\static\\obrazky\\" + rs.getInt("ID") + ".jpg\" height=\"73\"</td>");
                out.println("                    <td>" + rs.getString("nazov") + "</td>");
                out.println("                    <td>1:" + rs.getInt("mierka") + "</td>");
                out.println("                    <td>" + rs.getString("vyrobca") + "</td>");
                out.println("                    <td>" + rs.getInt("ks") + "</td>");
                out.println("                    <td>" + rs.getInt("cena") + "€</td>");
                out.println("    <td><form action=\"index\" method=\"post\" class=\"form-inline\">");
                out.println("        <input type=\"number\" name=\"pocet\" class=\"form-control\" min=\"1\" value=\"1\">");
                out.println("        <input type=\"hidden\" name=\"cena_tovaru\" value=\"" + rs.getInt("cena") + "\">");
                out.println("        <input type=\"hidden\" name=\"id_tovaru\" value=\"" + rs.getInt("ID") + "\">");
                out.println("        <button class=\"btn btn-primary m-1\" name=\"pridat\" type=\"submit\">Do košíka</button>");
                out.println("    </form></td>");
                out.println("                </tr>");
            }
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        out.println("        </tbody>");
        out.println("    </table>");
        out.println("    </div>");
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

    private void ZapisDoKosika(Integer user_id, String id_tovaru, String cena_tovaru, String pocet_ks) {
        try {
            pstmt = con.prepareStatement("SELECT COUNT(ID) AS pocet FROM kosik WHERE (ID_pouzivatela = ?) AND (ID_tovaru = ?)");
            pstmt.setInt(1, user_id);
            pstmt.setString(2, id_tovaru);
            rs = pstmt.executeQuery();
            
            rs.next();
            int pocet = rs.getInt("pocet");
            if (pocet == 0) {
                pstmt = con.prepareStatement("INSERT INTO kosik (ID_pouzivatela, ID_tovaru, cena, ks) VALUES (?, ?, ?, ?)");
                pstmt.setInt(1, user_id);
                pstmt.setString(2, id_tovaru);
                pstmt.setString(3, cena_tovaru);
                pstmt.setString(4, pocet_ks);
                
                pstmt.executeUpdate();
            } else {
                pstmt = con.prepareStatement("UPDATE kosik SET ks = ? + 1 where (ID_pouzivatela = ?) AND (ID_tovaru = ?)");
                pstmt.setString(1, pocet_ks);
                pstmt.setInt(2, user_id);
                pstmt.setString(3, id_tovaru);
                
                pstmt.executeUpdate();
            }
            
            pstmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
