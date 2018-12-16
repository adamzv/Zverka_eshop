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
@WebServlet(name = "Objednavky", urlPatterns = {"/objednavky"})
public class Objednavky extends HttpServlet {

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

        // ak sem prišiel používateľ cez formulár, tak som si objednal tovar z košíka
        if (request.getMethod().equals("POST")) {
            try {
                pstmt = con.prepareStatement("DELETE FROM kosik WHERE (ID_pouzivatela = ?) AND (ID_tovaru = ?)");
                pstmt.setInt(1, user_id);
                System.out.println(request.getParameter("id_tovaru"));
                pstmt.setString(2, request.getParameter("id_tovaru"));
                pstmt.executeUpdate();

                pstmt.close();
                System.out.println("OK");
                response.sendRedirect("kosik");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        }

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            Layout.vypis_html(Layout.ZACIATOK_HTML, out, "Objednávky");
            Layout.vypis_navbar(out, session);
            vypis_objednavky(out);
            Layout.vypis_footer(out);
            Layout.vypis_html(Layout.KONIEC_HTML, out);
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

    private void vypis_objednavky(PrintWriter out) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
