package com.servlets.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import org.json.JSONArray;
import org.json.JSONObject;

import com.db.web.DBConnection;
import java.util.Base64;

@WebServlet("/AdminServlet")
public class AdminServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONArray usersArray = new JSONArray();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT  firstname, lastname, email, proof_id, role, face_image FROM users")) {
            
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                JSONObject user = new JSONObject();
                
                user.put("firstname", rs.getString("firstname"));
                user.put("lastname", rs.getString("lastname"));
                user.put("email", rs.getString("email"));
                user.put("voterId", rs.getString("proof_id")); 
                user.put("role", rs.getString("role"));

                // Convert image to Base64 if exists
                byte[] imageData = rs.getBytes("face_image");
                String base64Image = (imageData != null) 
                    ? "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(imageData) 
                    : "uploads/default.png"; // Default image path
                
                user.put("photo", base64Image);
                usersArray.put(user);
            }

            PrintWriter out = response.getWriter();
            out.print(usersArray.toString());
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error fetching users: " + e.getMessage());
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("delete".equals(action)) {
            int userId = Integer.parseInt(request.getParameter("id"));

            try (Connection connection = DBConnection.getConnection();
                 PreparedStatement ps = connection.prepareStatement("DELETE FROM users WHERE id = ?")) {

                ps.setInt(1, userId);
                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    response.getWriter().write("{\"message\": \"User deleted successfully.\"}");
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found.");
                }

            } catch (Exception e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error deleting user.");
            }
        }
    }
}
