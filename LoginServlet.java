package com.servlets.web;

import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/evoting", "root", "");

            // Fetch user details from the database
            String sql = "SELECT firstname, lastname, email, role, face_image FROM users WHERE email = ? AND password = ?";
            pst = conn.prepareStatement(sql);
            pst.setString(1, email);
            pst.setString(2, password);
            rs = pst.executeQuery();

            if (rs.next()) {
                // Store user details in session
                HttpSession session = request.getSession();
                session.setAttribute("userEmail", rs.getString("email"));
                session.setAttribute("userRole", rs.getString("role"));
                session.setAttribute("userFirstname", rs.getString("firstname"));
                session.setAttribute("userLastname", rs.getString("lastname"));
                session.setAttribute("userPhoto", rs.getString("face_image")); // Image path

                // Redirect to dashboard page
                response.sendRedirect("dashboard.html");
            } else {
                response.sendRedirect("login.html?error=Invalid credentials");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
