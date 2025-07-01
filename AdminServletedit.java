package com.servlets.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.json.JSONObject;
import java.util.Base64;

@WebServlet("/AdminServletedit")
@MultipartConfig(maxFileSize = 16177215) // Handle large images
public class AdminServletedit extends HttpServlet {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/evoting";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "";

    // Fetch User Data (GET Request)
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String voterId = request.getParameter("voterId");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
            String sql = "SELECT * FROM users WHERE proof_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, voterId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                JSONObject user = new JSONObject();
                user.put("voterid", rs.getString("proof_id"));
                user.put("firstname", rs.getString("firstname"));
                user.put("lastname", rs.getString("lastname"));
                user.put("email", rs.getString("email"));
                user.put("mobile", rs.getString("mobile"));
                user.put("dob", rs.getString("dob"));
                user.put("gender", rs.getString("gender"));
                user.put("role", rs.getString("role"));

                byte[] imgData = rs.getBytes("face_image");
                if (imgData != null) {
                    String base64Image = "data:image/png;base64," + Base64.getEncoder().encodeToString(imgData);
                    user.put("face_image", base64Image);
                } else {
                    user.put("face_image", "");
                }

                out.print(user.toString());
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    // Update or Delete User (POST Request)
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        
        String action = request.getParameter("action"); // Check if delete action is requested
        String voterId = request.getParameter("voterId");

        if ("delete".equals(action)) {
            deleteUser(response, voterId);
            return;
        }

        // Update user data if delete action is not requested
        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        String email = request.getParameter("email");
        String mobile = request.getParameter("mobile");
        String dob = request.getParameter("dob");
        String gender = request.getParameter("gender");
        String role = request.getParameter("role");
        String faceImageBase64 = request.getParameter("face_image");
        
        byte[] imageBytes = null;
        if (faceImageBase64 != null && !faceImageBase64.isEmpty()) {
            imageBytes = Base64.getDecoder().decode(faceImageBase64.split(",")[1]);  // Removing the prefix "data:image/png;base64,"
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
            String sql = "UPDATE users SET firstname=?, lastname=?, email=?, mobile=?, dob=?, gender=?, role=?, face_image=? WHERE proof_id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, firstname);
            stmt.setString(2, lastname);
            stmt.setString(3, email);
            stmt.setString(4, mobile);
            stmt.setString(5, dob);
            stmt.setString(6, gender);
            stmt.setString(7, role);
            if (imageBytes != null) {
                stmt.setBytes(8, imageBytes);
            } else {
                stmt.setNull(8, java.sql.Types.BLOB);
            }
            stmt.setString(9, voterId);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                out.print("User updated successfully.");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("User not found.");
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    // Delete User (DELETE Request)
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String voterId = request.getParameter("voterId");
        deleteUser(response, voterId);
    }

    // Method to handle user deletion
    private void deleteUser(HttpServletResponse response, String voterId) throws IOException {
        PrintWriter out = response.getWriter();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
            String sql = "DELETE FROM users WHERE proof_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, voterId);

            int result = stmt.executeUpdate();
            if (result > 0) {
                out.print("User deleted successfully.");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("User not found.");
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
