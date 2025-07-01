package com.servlets.web;

import org.json.JSONObject;
import java.io.*;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/UpdateCandidateServlet")
public class UpdateCandidateByVoterIdServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Read JSON request body
            BufferedReader reader = request.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String requestData = sb.toString();
            System.out.println("Received JSON: " + requestData);

            // Parse JSON request
            JSONObject jsonObject = new JSONObject(requestData);

            // Validate input
            if (!jsonObject.has("id")) {
                out.write("{\"error\": \"Missing required field: id\"}");
                return;
            }

            // Read input data
            String voterId = jsonObject.getString("id");  // ✅ FIXED: Read voter ID as a String
            String firstName = jsonObject.getString("firstName");
            String lastName = jsonObject.getString("lastName");
            String dob = jsonObject.getString("dob");
            String mobile = jsonObject.getString("mobile");
            String gender = jsonObject.getString("gender");
            String party = jsonObject.getString("party");

            // Database connection
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/evoting", "root", "");

            // Update query
            String sql = "UPDATE candidates SET first_Name=?, last_Name=?, dob=?, mobile=?, gender=?, party=? WHERE voter_id=?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, dob);
            stmt.setString(4, mobile);
            stmt.setString(5, gender);
            stmt.setString(6, party);
            stmt.setString(7, voterId); // ✅ Use setString instead of setInt

            // Execute update
            int updated = stmt.executeUpdate();
            if (updated > 0) {
                out.write("{\"message\": \"Candidate updated successfully\"}");
            } else {
                out.write("{\"error\": \"Candidate not found\"}");
            }

            // Close resources
            stmt.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace(); // Log the full error
            out.write("{\"error\": \"Server error: " + e.getMessage() + "\"}");
        }
    }
}
