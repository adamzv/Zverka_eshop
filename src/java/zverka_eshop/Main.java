/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zverka_eshop;

import java.io.PrintWriter;

/**
 *
 * @author adamzv
 */
public class Main {
    // upraviť
    public static void vypis_index(PrintWriter out, String path) {
        out.println("    <div class=\"jumbotron\">");
        out.println("        <h1 class=\"display-4\">Lorem Ipsum " + path + "</h1>");
        out.println("        <p class=\"lead\">This is a simple hero unit, a simple jumbotron-style component for calling extra attention to featured content or information.</p>");
        out.println("        <hr class=\"my-4\">");
        out.println("        <p>It uses utility classes for typography and spacing to space content out within the larger container.</p>");
        out.println("    </div>");
    }
}
