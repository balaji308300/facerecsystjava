package com.servlets.web;

import java.io.IOException;
import java.io.PrintWriter;
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

@WebServlet("/FetchCandidateByVoterIdServlet")
public class FetchCandidateByVoterIdServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        String voterId = request.getParameter("voterId");
        if (voterId == null || voterId.isEmpty()) {
            out.write("{\"error\": \"Voter ID is required\"}");
            return;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Database connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/evoting", "root", "");

            // SQL Query
            String query = "SELECT * FROM candidates WHERE voter_id  = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, voterId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                JSONObject candidate = new JSONObject();
                candidate.put("firstName", rs.getString("first_Name"));
                candidate.put("lastName", rs.getString("last_Name"));
                candidate.put("dob", rs.getString("dob"));
                candidate.put("mobile", rs.getString("mobile"));
                candidate.put("gender", rs.getString("gender"));
                candidate.put("party", rs.getString("party"));
                
                out.write(candidate.toString());
            } else {
                out.write("{\"error\": \"Candidate not found\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.write("{\"error\": \"Server error occurred\"}");
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }
}
