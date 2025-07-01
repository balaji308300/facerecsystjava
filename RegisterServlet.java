package com.servlets.web;

import java.io.IOException;
import java.sql.*;
import java.util.Base64;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/RegisterServlet")
@MultipartConfig(maxFileSize = 16177215) // Allow image files up to 16MB
public class RegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        String proof_id = request.getParameter("proof_id");
        String password = request.getParameter("password");
        String mobile = request.getParameter("mobile");
        String dob = request.getParameter("dob");
        String email = request.getParameter("email");
        String gender = request.getParameter("gender");
        String role = request.getParameter("role");
        String faceImageBase64 = request.getParameter("face_image");

        Connection conn = null;
        PreparedStatement checkVoterStmt = null;
        PreparedStatement checkDuplicateStmt = null;
        PreparedStatement insertStmt = null;
        ResultSet rsVoter = null;
        ResultSet rsDuplicate = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/evoting", "root", "");

            // Check if Voter ID exists in the voters table
            String checkVoterSQL = "SELECT * FROM voters WHERE VoterID = ?";
            checkVoterStmt = conn.prepareStatement(checkVoterSQL);
            checkVoterStmt.setString(1, proof_id);
            rsVoter = checkVoterStmt.executeQuery();

            if (!rsVoter.next()) { // If no matching VoterID found
                response.setContentType("text/html");
                response.getWriter().println("<script>alert('Error: Voter ID not found in the database.'); window.location='user.html';</script>");
                return;
            }

            // Check if proof_id, email, or mobile already exists in users table
            String checkDuplicateSQL = "SELECT * FROM users WHERE proof_id = ? OR email = ? OR mobile = ?";
            checkDuplicateStmt = conn.prepareStatement(checkDuplicateSQL);
            checkDuplicateStmt.setString(1, proof_id);
            checkDuplicateStmt.setString(2, email);
            checkDuplicateStmt.setString(3, mobile);
            rsDuplicate = checkDuplicateStmt.executeQuery();

            if (rsDuplicate.next()) {
                response.setContentType("text/html");
                response.getWriter().println("<script>alert('Error: Proof ID, Email, or Mobile number already exists.'); window.location='user.html';</script>");
                return;
            }

            // Convert Base64 to Byte Array
            byte[] faceImageBytes = Base64.getDecoder().decode(faceImageBase64.split(",")[1]);

            // Insert into users table
            String insertSQL = "INSERT INTO users (firstname, lastname, proof_id, password, mobile, dob, email, gender, role, face_image) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            insertStmt = conn.prepareStatement(insertSQL);
            insertStmt.setString(1, firstname);
            insertStmt.setString(2, lastname);
            insertStmt.setString(3, proof_id);
            insertStmt.setString(4, password);
            insertStmt.setString(5, mobile);
            insertStmt.setString(6, dob);
            insertStmt.setString(7, email);
            insertStmt.setString(8, gender);
            insertStmt.setString(9, role);
            insertStmt.setBytes(10, faceImageBytes);

            int row = insertStmt.executeUpdate();
            if (row > 0) {
                response.sendRedirect("userlogin.html?message=Registration Successful");
            } else {
                response.setContentType("text/html");
                response.getWriter().println("<script>alert('Registration failed!'); window.location='user.html';</script>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("text/html");
            response.getWriter().println("<script>alert('Error: " + e.getMessage() + "'); window.location='user.html';</script>");
        } finally {
            try {
                if (rsVoter != null) rsVoter.close();
                if (rsDuplicate != null) rsDuplicate.close();
                if (checkVoterStmt != null) checkVoterStmt.close();
                if (checkDuplicateStmt != null) checkDuplicateStmt.close();
                if (insertStmt != null) insertStmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
