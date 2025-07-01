package com.servlets.web;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/AddElectionServlet")
public class AddElectionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String electionName = request.getParameter("electionName");
        String electionDate = request.getParameter("electionDate");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish database connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/evoting", "root", "");

            // Insert query to save election details
            String sql = "INSERT INTO elections (election_name, election_date, start_time, end_time) VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, electionName);
            pstmt.setString(2, electionDate);
            pstmt.setString(3, startTime);
            pstmt.setString(4, endTime);
            
            // Execute query
            int rowsInserted = pstmt.executeUpdate();
            
            // Redirect based on success or failure
            if (rowsInserted > 0) {
                response.sendRedirect("election-list.html?success=true");
            } else {
                response.sendRedirect("add-election.html?error=true");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("add-election.html?error=true");
        } finally {
            // Close resources
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
