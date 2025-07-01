

package com.servlets.web;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.json.JSONObject;

import com.db.web.DBConnection;

@WebServlet("/DashboardServlets")
public class DashboardServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String imageRequest = request.getParameter("image");

        if ("true".equals(imageRequest)) {
            serveUserImage(request, response, session);
            return;
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String email = (String) session.getAttribute("userEmail");

        if (email == null) {
            response.getWriter().write("{\"error\": \"User not logged in\"}");
            return;
        }

        JSONObject userData = new JSONObject();
        userData.put("firstname", session.getAttribute("userFirstname"));
        userData.put("lastname", session.getAttribute("userLastname"));
        
        userData.put("email", session.getAttribute("userEmail"));
        userData.put("role", session.getAttribute("userRole"));
        userData.put("photo", "DashboardServlet?image=true"); // This will be the image link

        response.getWriter().write(userData.toString());
    }

    private void serveUserImage(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        response.setContentType("image/jpeg");

        String email = (String) session.getAttribute("userEmail");

        if (email == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not logged in");
            return;
        }

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT face_image FROM users WHERE email = ?")) {
            
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                byte[] imageData = rs.getBytes("face_image");

                if (imageData != null && imageData.length > 0) {
                    OutputStream out = response.getOutputStream();
                    out.write(imageData);
                    out.flush();
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "No image found");
                }
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving image");
        }
    }
}
