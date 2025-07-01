package com.servlets.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

@WebServlet("/DeleteCandidateServlet")
public class DeleteCandidateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();
        
        String candidateId = request.getParameter("id");

        if (candidateId == null || candidateId.isEmpty()) {
            jsonResponse.put("message", "Invalid Candidate ID");
            out.print(jsonResponse);
            return;
        }

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            // Database connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/evoting", "root", "");

            // SQL query to delete the candidate
            String sql = "DELETE FROM candidates WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(candidateId));

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                jsonResponse.put("message", "Candidate deleted successfully");
            } else {
                jsonResponse.put("message", "Candidate not found");
            }

        } catch (Exception e) {
            jsonResponse.put("message", "Error deleting candidate: " + e.getMessage());
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }

        out.print(jsonResponse);
    }
}
