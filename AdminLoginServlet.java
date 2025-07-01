package com.servlets.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.db.web.DBConnection ;

@WebServlet("/AdminLoginServlet")  // Annotation to avoid web.xml
public class AdminLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT * FROM admin WHERE username=? AND password=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                out.println("<h3>Login Successful! Welcome, " + username + ".</h3>");
                response.sendRedirect("admin_dashboard.html");
            } else {
                out.println("<h3>Invalid Credentials! Try again.</h3>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<h3>Database Connection Error!</h3>");
        }
    }
}
