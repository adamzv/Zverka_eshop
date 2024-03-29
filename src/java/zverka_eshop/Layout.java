/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zverka_eshop;

import java.io.PrintWriter;
import javax.servlet.http.HttpSession;

/**
 *
 * @author adamzv
 */
public class Layout {

    public static final int ZACIATOK_HTML = 0;
    public static final int KONIEC_HTML = 1;

    private static final String TITLE = "";

    public static void vypis_html(int mod, PrintWriter out) {
        private_vypis_html(mod, out, TITLE);
    }

    public static void vypis_html(int mod, PrintWriter out, String title) {
        private_vypis_html(mod, out, "jShop Zverka | " + title);
    }

    public static void vypis_navbar(PrintWriter out, HttpSession session) {
        out.println("<nav class=\"navbar navbar-expand-md navbar-light bg-light border\">");
        out.println("    <a class=\"navbar-brand\" href=\"index\">jShop Zverka</a>");
        out.println("    <button aria-controls=\"navbar\" aria-expanded=\"false\" aria-label=\"Toggle navigation\" class=\"navbar-toggler\"");
        out.println("            data-target=\"#navbar\" data-toggle=\"collapse\" type=\"button\">");
        out.println("        <span class=\"navbar-toggler-icon\"></span>");
        out.println("    </button>");
        out.println("    <div class=\"collapse navbar-collapse\" id=\"navbar\">");
        if (session.getAttribute("user_id") != null) {
            if (session.getAttribute("prava") != null && !session.getAttribute("prava").equals("admin")) {
                out.println("            <ul class=\"navbar-nav mr-auto\">");
                out.println("                <li class=\"nav-item\"><a class=\"nav-link\" href=\"index\">Ponuka</a></li>");
                out.println("                <li class=\"nav-item\"><a class=\"nav-link\" href=\"kosik\">Košík</a></li>");
                out.println("                <li class=\"nav-item\"><a class=\"nav-link\" href=\"objednavky\">Objednávky</a></li>");
                out.println("            </ul>");
            }
        }
        out.println();
        out.println("            <ul class=\"navbar-nav ml-auto\">");
        if (session.getAttribute("user_id") != null) {
            out.println("                <li class=\"nav-item\"><a class=\"nav-link\" href=\"logout\">Odhlásiť sa</a></li>");
        } else {
            out.println("                <li class=\"nav-item\"><a class=\"nav-link\" href=\"login\">Prihlásiť sa</a></li>");
        }
        out.println("            </ul>");
        out.println("    </div>");
        out.println("</nav>");
    }

    public static void vypis_footer(PrintWriter out) {
        out.println("<footer class=\"small text-center text-muted\">");
        out.println("    Adam Zverka 2018 (KI/OT/15)");
        out.println("</footer>");
    }

    private static void private_vypis_html(int mod, PrintWriter out, String title) {
        switch (mod) {
            case 0:
                out.println("<!DOCTYPE html>");
                out.println("<html lang=\"sk\">");
                out.println("<head>");
                out.println("    <meta charset=\"utf-8\"/>");
                out.println("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\"/>");
                out.println();
                out.println("    <link href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta.2/css/bootstrap.min.css\" rel=\"stylesheet\"/>");
                out.println("    <link href=\"/static/styles.css\" rel=\"stylesheet\"/>");
                out.println("    <link rel=\"stylesheet\" href=\"https://use.fontawesome.com/releases/v5.6.3/css/all.css\" integrity=\"sha384-UHRtZLI+pbxtHCWp1t77Bi1L4ZtiqrqD80Kn4Z8NTSRyMA2Fd33n5dQ8lWUE00s/\" crossorigin=\"anonymous\">");
                
                out.println();
                out.println("    <script src=\"https://code.jquery.com/jquery-3.1.1.min.js\"></script>");
                out.println("    <script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.3/umd/popper.min.js\"></script>");
                out.println("    <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta.2/js/bootstrap.min.js\"></script>");
                out.println("    <title>" + title + "</title>");
                out.println("</head>");
                out.println();
                out.println("<body>");
                break;
            case 1:
                out.println("</body>");
                out.println("</html>");
                break;
        }
    }
}
