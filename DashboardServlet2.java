package com.servlets.web;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

@WebServlet("/DashboardServlet2") // Ensure this matches the AJAX request URL
public class DashboardServlet2 extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        int userCount = 0;
        int partyCount = 0;
        int candidateCount = 0; // Added for counting candidates

        try {
            // Database Connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/evoting", "root", "");

            // Count Users
            PreparedStatement userStmt = conn.prepareStatement("SELECT COUNT(*) FROM users");
            ResultSet userRs = userStmt.executeQuery();
            if (userRs.next()) {
                userCount = userRs.getInt(1);
            }
            userRs.close();
            userStmt.close();

            // Count Political Parties
            PreparedStatement partyStmt = conn.prepareStatement("SELECT COUNT(*) FROM political_parties");
            ResultSet partyRs = partyStmt.executeQuery();
            if (partyRs.next()) {
                partyCount = partyRs.getInt(1);
            }
            partyRs.close();
            partyStmt.close();
            
            // Count Candidates
            PreparedStatement candidateStmt = conn.prepareStatement("SELECT COUNT(*) FROM candidates"); // Assuming 'candidates' table exists
            ResultSet candidateRs = candidateStmt.executeQuery();
            if (candidateRs.next()) {
                candidateCount = candidateRs.getInt(1);
            }
            candidateRs.close();
            candidateStmt.close();

            // Close connection
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create JSON response
        JSONObject json = new JSONObject();
        json.put("userCount", userCount);
        json.put("partyCount", partyCount);
        json.put("candidateCount", candidateCount); // Added candidate count

        response.getWriter().write(json.toString());
    }
}
