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

@WebServlet("/DeleteElectionServlet")
public class DeleteElectionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        
        if (id == null || id.isEmpty()) {
            response.sendRedirect("election-list.html");
            return;
        }
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/evoting", "root", "");
            
            String sql = "DELETE FROM elections WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(id));
            stmt.executeUpdate();
            
            stmt.close();
            conn.close();
            
            response.sendRedirect("election-list.html"); // Redirect back to election list
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error deleting election.");
        }
    }
}
