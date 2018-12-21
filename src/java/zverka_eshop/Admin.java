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
@WebServlet(name = "Admin", urlPatterns = {"/admin"})
public class Admin extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");

        session = request.getSession();

        // skontroluje, či je používateľ prihlásený, ak nie je tak ho pošle na login servlet
        Integer user = (Integer) session.getAttribute("user_id");
        if (user == null || user_id == null) {
            response.sendRedirect("/eshop/login");
        } else {
            // ak používateľ nemá admin práva, pošle ho na index stránku eshopu
            if (!session.getAttribute("prava").equals("admin")) {
                response.sendRedirect("index");
            }
            user_id = user;
            username = (String) session.getAttribute("username");
        }

        if (request.getMethod().equals("POST")) {
            if (request.getParameter("formular").equals("zmenit_prava")) {
                String prava = request.getParameter("vybrat_prava");
                int id_pouzivatela = Integer.parseInt(request.getParameter("id_pouzivatela"));
                try {
                    if (prava != null) {
                        pstmt = con.prepareStatement("UPDATE pouzivatelia SET prava = ? WHERE ID = ?");
                        pstmt.setString(1, prava);
                        pstmt.setInt(2, id_pouzivatela);
                        pstmt.executeUpdate();

                        response.sendRedirect("admin");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } else {
                String stav = request.getParameter("zmenit_stav");
                String obj_cislo = request.getParameter("cislo_objednavky");
                try {
                    if (stav != null) {
                        pstmt = con.prepareStatement("UPDATE obj_zoznam SET stav = ? WHERE obj_cislo = ?");
                        pstmt.setString(1, stav);
                        pstmt.setString(2, obj_cislo);
                        pstmt.executeUpdate();

                        response.sendRedirect("admin");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }

        try (PrintWriter out = response.getWriter()) {
            Layout.vypis_html(Layout.ZACIATOK_HTML, out, "Admin");
            Layout.vypis_navbar(out, session);
            out.println("    <div class=\"jumbotron\">");
            out.println("        <h1 class=\"display-4\">Admin rozhranie</h1>");
            out.println("        <p class=\"lead\">Admin: " + username + "</p>");
            out.println("    </div>");
            vypis_objednavky(out);
            vypis_pouzivatelov(out);
            Layout.vypis_footer(out);
            Layout.vypis_html(Layout.KONIEC_HTML, out);
        }
    }

    private void vypis_objednavky(PrintWriter out) {
        out.println("        <p><h2>Objednávky</h2></p>");
        out.println("    <table class=\"table table-striped\">");
        out.println("        <thead>");
        out.println("            <tr>");
        out.println("                <th scope=\"col\">Číslo objednávky</th>");
        out.println("                <th scope=\"col\">Meno, Priezvisko</th>");
        out.println("                <th scope=\"col\">Adresa</th>");
        out.println("                <th scope=\"col\">Dátum objednávky</th>");
        out.println("                <th scope=\"col\">Suma</th>");
        out.println("                <th scope=\"col\">Stav</th>");
        out.println("                <th scope=\"col\">Zmeniť stav</th>");
        out.println("            </tr>");
        out.println("        </thead>");
        out.println("        <tbody>");
        try {
            pstmt = con.prepareStatement("SELECT * FROM obj_zoznam INNER JOIN pouzivatelia ON pouzivatelia.ID = obj_zoznam.ID_pouzivatela");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                out.println("                <tr>");
                out.println("                    <td class=\"align-middle\">" + rs.getString("obj_cislo") + "</td>");
                out.println("                    <td class=\"align-middle\">" + rs.getString("meno") + " " + rs.getString("priezvisko") + "</td>");
                out.println("                    <td class=\"align-middle\">" + rs.getString("adresa") + "</td>");
                out.println("                    <td class=\"align-middle\">" + rs.getDate("datum_objednavky") + "</td>");
                out.println("                    <td class=\"align-middle\">" + rs.getInt("suma") + "</td>");
                out.println("                    <td class=\"align-middle\">" + rs.getString("stav") + "</td>");
                out.println("                    <td><form action=\"admin\" method=\"post\" class=\"form-inline\"><select class=\"form-control mr-2 col-lg\" name=\"zmenit_stav\">");
                out.println("                       <option value=\"zaplatene\">zaplatené</option>");
                out.println("                       <option value=\"odoslane\">odoslané</option>");
                out.println("                       <option value=\"dorucene\">doručené</option>");
                out.println("                       </select>");
                out.println("                       <input type=\"hidden\" name=\"formular\" value=\"zmenit_stav\">");
                out.println("                       <input type=\"hidden\" name=\"cislo_objednavky\" value=\"" + rs.getString("obj_cislo") + "\">");
                out.println("                       <button class=\"btn btn-success form-control col-lg\" name=\"potvrdit\" type=\"submit\">Potvrdiť</button>");
                out.println("                       <button class=\"btn btn-secondary form-control clickable ml-3\" data-toggle=\"collapse\" data-target=\"." + rs.getString("obj_cislo") + "\" type=\"button\"><i class=\"fas fa-angle-down\"></i></button>");
                out.println("                       ");
                out.println("                       </form></td>");
                out.println("                </tr>");
                pstmt = con.prepareStatement("SELECT * FROM obj_polozky INNER JOIN sklad ON sklad.ID = ID_tovaru INNER JOIN obj_zoznam ON obj_zoznam.ID = obj_polozky.ID_objednavky WHERE obj_cislo = ?");
                pstmt.setString(1, rs.getString("obj_cislo"));
                ResultSet rs_tovar = pstmt.executeQuery();
                // TODO bude potrebné pridať ako samostatnú vnorenú tabuľku
                out.println("                <tr class=\"collapse " + rs.getString("obj_cislo") + "\">");
                out.println("                    <td colspan=\"2\"></td>");
                out.println("                    <td><b>Názov tovaru</b></td>");
                out.println("                    <td><b>Počet ks</b></td>");
                out.println("                    <td></td>");
                out.println("                    <td colspan=\"2\"><b>Cena</b></td>");
                out.println("                </tr>");
                while (rs_tovar.next()) {
                    out.println("                <tr class=\"collapse " + rs.getString("obj_cislo") + "\">");
                    out.println("                    <td><img src=\"" + getServletContext().getContextPath() + "\\static\\obrazky\\" + rs_tovar.getInt("ID_tovaru") + ".jpg\" height=\"73\"</td>");
                    out.println("                    <td></td>");
                    out.println("                    <td>" + rs_tovar.getString("nazov") + "</td>");
                    out.println("                    <td>" + rs_tovar.getInt("ks") + "</td>");
                    out.println("                    <td></td>");
                    out.println("                    <td colspan=\"2\">" + rs_tovar.getInt("cena") + "€</td>");
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

    private void vypis_pouzivatelov(PrintWriter out) {
        out.println("        <p><h2>Objednávky</h2></p>");
        out.println("    <table class=\"table table-striped\">");
        out.println("        <thead>");
        out.println("            <tr>");
        out.println("                <th scope=\"col\">Login</th>");
        out.println("                <th scope=\"col\">Mail</th>");
        out.println("                <th scope=\"col\">Práva</th>");
        out.println("                <th scope=\"col\">Zmeniť práva</th>");
        out.println("            </tr>");
        out.println("        </thead>");
        out.println("        <tbody>");
        try {
            pstmt = con.prepareStatement("SELECT ID, login, mail, prava FROM pouzivatelia");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String prava = rs.getString("prava");
                out.println("                <tr>");
                out.println("                    <td class=\"align-middle\">" + rs.getString("login") + "</td>");
                out.println("                    <td class=\"align-middle\">" + rs.getString("mail") + "</td>");
                out.println("                    <td class=\"align-middle\">" + rs.getString("prava") + "</td>");
                out.println("                    <td><form id=\"prava_pouzivatela\" action=\"admin\" method=\"post\" class=\"form-inline\"><select class=\"form-control mr-2 col-lg\" name=\"vybrat_prava\">");
                out.println("                       <option value=\"" + prava + "\" disabled selected>" + prava + "</option>");
                if (prava.equals("user")) {
                    out.println("                       <option value=\"admin\">admin</option>");
                } else {
                    out.println("                       <option value=\"user\">user</option>");
                }
                out.println("                       </select>");
                out.println("                       <input type=\"hidden\" name=\"formular\" value=\"zmenit_prava\">");
                out.println("                       <input type=\"hidden\" name=\"id_pouzivatela\" value=\"" + rs.getInt("ID") + "\">");
                out.println("                       <button class=\"btn btn-success form-control col-lg\" name=\"potvrdit\" type=\"submit\">Potvrdiť</button>");
                out.println("                       </form></td>");
                out.println("                </tr>");
            }
            pstmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        out.println("        </tbody>");
        out.println("    </table>");
    }

}
