package com.servlets.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

@WebServlet("/AddVoterServlet")
public class AddVoterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        String voterId = request.getParameter("voterId");

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/evoting", "root", "");
            String query = "INSERT INTO voters (VoterID) VALUES (?)";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, voterId);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                jsonResponse.put("message", "Voter added successfully!");
            } else {
                jsonResponse.put("message", "Failed to add voter.");
            }

            stmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            jsonResponse.put("message", "Error: " + e.getMessage());
        }

        out.print(jsonResponse);
        out.flush();
    }
}
