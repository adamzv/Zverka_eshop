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
 * @author adamz
 */
@WebServlet(name = "Kosik", urlPatterns = {"/kosik"})
public class Kosik extends HttpServlet {

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
    Integer zlava = 0;

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

        session = request.getSession();

        // skontroluje, či je používateľ prihlásený, ak nie je tak ho pošle na login servlet
        Integer user = (Integer) session.getAttribute("user_id");
        if (user == null || user_id == null) {
            response.sendRedirect("/eshop/login");
        } else {
            user_id = user;
            username = (String) session.getAttribute("username");
            zlava = Integer.parseInt(session.getAttribute("zlava").toString());
        }

        // ak sem prišiel používateľ cez formulár, tak vymaž danú položku z košíka
        if (request.getMethod().equals("POST")) {
            try {
                pstmt = con.prepareStatement("DELETE FROM kosik WHERE (ID_pouzivatela = ?) AND (ID_tovaru = ?)");
                pstmt.setInt(1, user_id);
                System.out.println(request.getParameter("id_tovaru"));
                pstmt.setString(2, request.getParameter("id_tovaru"));
                pstmt.executeUpdate();

                pstmt.close();
                response.sendRedirect("kosik");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        }

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            Layout.vypis_html(Layout.ZACIATOK_HTML, out, "Košík");
            Layout.vypis_navbar(out, session);
            vypis_kosik(out);
            Layout.vypis_footer(out);
            Layout.vypis_html(Layout.KONIEC_HTML, out);
        }
    }

    private void vypis_kosik(PrintWriter out) {
        out.println("    <table class=\"table table-striped\">");
        out.println("        <tbody>");
        int celkovaCenaBezZlavy = 0;
        int pocet_poloziek = 0;
        try {
            pstmt = con.prepareStatement("SELECT * FROM kosik INNER JOIN sklad ON sklad.ID = ID_tovaru WHERE ID_pouzivatela = ?");
            pstmt.setInt(1, user_id);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                out.println("                <tr>");
                out.println("                    <td><img src=\"" + getServletContext().getContextPath() + "\\static\\obrazky\\" + rs.getInt("ID_tovaru") + ".jpg\" height=\"73\"</td>");
                out.println("                    <td>" + rs.getString("nazov") + "</td>");
                out.println("                    <td>" + rs.getInt("ks") + "</td>");
                out.println("                    <td>" + rs.getInt("cena") + "€</td>");
                out.println("    <td><form action=\"kosik\" method=\"post\">");
                out.println("        <input type=\"hidden\" name=\"cena_tovaru\" value=\"" + rs.getInt("cena") + "\">");
                out.println("        <input type=\"hidden\" name=\"id_tovaru\" value=\"" + rs.getInt("ID_tovaru") + "\">");
                out.println("        <button class=\"btn btn-danger m-1\" name=\"odobrat\" type=\"submit\">x</button>");
                out.println("    </form></td>");
                out.println("                </tr>");
                celkovaCenaBezZlavy += (rs.getInt("cena") * rs.getInt("ks"));
                pocet_poloziek += 1;
            }
            pstmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        int celkovaCenaSoZlavou = (int) (celkovaCenaBezZlavy * ((100 - zlava) / 100.0));
        out.println("        </tbody>");
        out.println("    </table>");
        out.println("    <h4>Cena so zľavou: " + celkovaCenaSoZlavou + "€</h4>");
        out.println("    <h6>Cena bez zľavy: " + celkovaCenaBezZlavy + "€</h6>");
        out.println("    <h6>Zľava: " + session.getAttribute("zlava") + "<span class=\"glyphicon glyphicon-remove\" aria-hidden=\"true\"></span>%</h6>");
        out.println("    <form action=\"objednavky\" method=\"post\">");
        out.println("        <input type=\"hidden\" name=\"cena_tovaru\" value=\"" + celkovaCenaSoZlavou + "\">");
        if (pocet_poloziek > 0) {
            out.println("        <button class=\"btn btn-success m-1\" name=\"objednat\" type=\"submit\">Objednať</button>");
        } else {
            out.println("        <button class=\"btn btn-secondary m-1\" name=\"objednat\" type=\"submit\" disabled>Objednať</button>");
        }
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
