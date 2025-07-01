package com.servlets.web;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.PrintWriter;

@WebServlet("/PartyListServlet")
public class PartyListServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Allow CORS for frontend requests (optional)
        response.setHeader("Access-Control-Allow-Origin", "*"); 
        response.setHeader("Access-Control-Allow-Methods", "GET");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        JSONArray partyList = new JSONArray();

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/evoting", "root", "")) {
            // Fetch party details with image
            String query = "SELECT id, logo, party_number, name FROM political_parties";
            try (PreparedStatement ps = conn.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    JSONObject party = new JSONObject();
                    party.put("id", rs.getInt("id"));
                    party.put("party_number", rs.getString("party_number"));
                    party.put("party_name", rs.getString("name"));

                    // Convert image to Base64
                    byte[] imgData = rs.getBytes("logo");
                    if (imgData != null && imgData.length > 0) {
                        String base64Image = Base64.getEncoder().encodeToString(imgData);
                        party.put("party_logo", "data:image/png;base64," + base64Image);
                    } else {
                        party.put("party_logo", "");  // Return empty if no image
                    }

                    partyList.put(party);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject errorObj = new JSONObject();
            errorObj.put("error", "Failed to fetch party data");
            response.getWriter().print(errorObj.toString());
            return;
        }

        PrintWriter out = response.getWriter();
        out.print(partyList);
        out.flush();
    }
}
