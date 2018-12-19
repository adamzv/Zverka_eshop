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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    SimpleDateFormat dateFormat;
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
            dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            // číslo objednávky
            String obj_cislo = dateFormat.format(new Date());
            try {
                synchronized (this) {
                    // získanie tovaru z košíka
                    pstmt = con.prepareStatement("SELECT ID_pouzivatela, ID_tovaru, kosik.cena, kosik.ks, sklad.ks AS sklad_ks FROM kosik INNER JOIN sklad ON kosik.ID_tovaru = sklad.ID WHERE ID_pouzivatela = ?");
                    pstmt.setInt(1, user_id);
                    rs = pstmt.executeQuery();

                    boolean dostatok_tovaru = true;

                    // skontrolovanie stavu v sklade
                    while (rs.next()) {
                        int kosik_ks = rs.getInt("ks");
                        int sklad_ks = rs.getInt("sklad_ks");
                        if (kosik_ks > sklad_ks) {
                            dostatok_tovaru = false;
                            break;
                        }
                    }

                    // ak nie je dostatok tovaru na sklade, nedokonči objednávku
                    if (dostatok_tovaru) {
                        int cena_tovaru = Integer.parseInt(request.getParameter("cena_tovaru"));
                        // vloženie informácii o objednávke do obj_zoznam
                        pstmt = con.prepareStatement("INSERT INTO obj_zoznam (obj_cislo, datum_objednavky, ID_pouzivatela, suma, stav) VALUES (?, ?, ?, ?, ?)");
                        pstmt.setString(1, obj_cislo);
                        pstmt.setDate(2, new java.sql.Date(dateFormat.parse(obj_cislo).getTime()));
                        pstmt.setInt(3, user_id);
                        pstmt.setInt(4, cena_tovaru);
                        pstmt.setString(5, "objednane");
                        pstmt.executeUpdate();

                        // získanie id objednávky
                        pstmt = con.prepareStatement("SELECT ID from obj_zoznam WHERE obj_cislo = ?");
                        pstmt.setString(1, obj_cislo);
                        ResultSet rs_cislo = pstmt.executeQuery();
                        rs_cislo.next();
                        int obj_id = rs_cislo.getInt("ID");

                        // zápis tovaru do obj_polozky
                        rs.beforeFirst();
                        while (rs.next()) {
                            int id_tovaru = rs.getInt("ID_tovaru");
                            int cena = rs.getInt("cena");
                            int ks = rs.getInt("ks");
                            int sklad_ks = rs.getInt("sklad_ks");
                            pstmt = con.prepareStatement("INSERT INTO obj_polozky (ID_objednavky, ID_tovaru, cena, ks) VALUES (?, ?, ?, ?)");
                            pstmt.setInt(1, obj_id);
                            pstmt.setInt(2, id_tovaru);
                            pstmt.setInt(3, cena);
                            pstmt.setInt(4, ks);
                            pstmt.executeUpdate();

                            pstmt = con.prepareStatement("UPDATE sklad SET ks = ? WHERE ID = ?");
                            pstmt.setInt(1, sklad_ks - ks);
                            pstmt.setInt(2, id_tovaru);
                            pstmt.executeUpdate();
                        }

                        // vymazanie košíka
                        pstmt = con.prepareStatement("DELETE FROM kosik WHERE ID_pouzivatela = ?");
                        pstmt.setInt(1, user_id);
                        pstmt.executeUpdate();

                        pstmt.close();
                        response.sendRedirect("objednavky");
                    } else {
                        pstmt.close();
                        response.sendRedirect("kosik");
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (ParseException ex) {
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
        out.println("    <table class=\"table table-striped\">");
        out.println("        <thead>");
        out.println("            <tr>");
        out.println("                <th scope=\"col\">Číslo objednávky</th>");
        out.println("                <th scope=\"col\">Dátum objednávky</th>");
        out.println("                <th scope=\"col\">Suma</th>");
        out.println("                <th scope=\"col\">Stav</th>");
        out.println("            </tr>");
        out.println("        </thead>");
        out.println("        <tbody>");
        try {
            pstmt = con.prepareStatement("SELECT * FROM obj_zoznam WHERE ID_pouzivatela = ?");
            pstmt.setInt(1, user_id);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                out.println("                <tr data-toggle=\"collapse\" data-target=\"." + rs.getString("obj_cislo") + "\" class=\"clickable\">");
                out.println("                    <td>" + rs.getString("obj_cislo") + "</td>");
                out.println("                    <td>" + rs.getDate("datum_objednavky") + "</td>");
                out.println("                    <td>" + rs.getInt("suma") + "</td>");
                out.println("                    <td>" + rs.getString("stav") + "</td>");
                out.println("                </tr>");
                pstmt = con.prepareStatement("SELECT * FROM obj_polozky INNER JOIN sklad ON sklad.ID = ID_tovaru INNER JOIN obj_zoznam ON obj_zoznam.ID = obj_polozky.ID_objednavky WHERE (ID_pouzivatela = ?) AND (obj_cislo = ?)");
                pstmt.setInt(1, user_id);
                pstmt.setString(2, rs.getString("obj_cislo"));
                ResultSet rs_tovar = pstmt.executeQuery();
                // TODO bude potrebné pridať ako samostatnú vnorenú tabuľku
                while (rs_tovar.next()) {
                    out.println("                <tr class=\"collapse " + rs.getString("obj_cislo") + "\">");
                    out.println("                    <td><img src=\"" + getServletContext().getContextPath() + "\\static\\obrazky\\" + rs_tovar.getInt("ID_tovaru") + ".jpg\" height=\"73\"</td>");
                    out.println("                    <td>" + rs_tovar.getString("nazov") + "</td>");
                    out.println("                    <td>" + rs_tovar.getInt("ks") + "</td>");
                    out.println("                    <td>" + rs_tovar.getInt("cena") + "€</td>");
                    out.println("                </tr>");
                }
            }
            pstmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        out.println("        </tbody>");
        out.println("    </table>");
    }

}
