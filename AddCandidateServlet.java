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

@WebServlet("/AddCandidateServlet")
public class AddCandidateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve form parameters
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String voterId = request.getParameter("voterId");
        String dob = request.getParameter("dob");
        String mobile = request.getParameter("mobile");
        String gender = request.getParameter("gender");
        String party = request.getParameter("party");

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            // Database connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/evoting", "root", "");

            // Insert query (No password field)
            String sql = "INSERT INTO candidates (first_name, last_name, voter_id, dob, mobile, gender, party) VALUES (?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, voterId);
            stmt.setString(4, dob);
            stmt.setString(5, mobile);
            stmt.setString(6, gender);
            stmt.setString(7, party);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                response.sendRedirect("admin_dashboard.html"); // Success redirect
            } else {
                response.sendRedirect("error.jsp?message=Insertion%20Failed"); // Error redirect
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp?message=Database%20Error");
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
