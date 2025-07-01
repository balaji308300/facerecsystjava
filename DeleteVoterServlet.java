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
import com.google.gson.JsonObject;

@WebServlet("/DeleteVoterServlet")
public class DeleteVoterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String voterId = request.getParameter("voterId");
        JsonObject jsonResponse = new JsonObject();

        if (voterId == null || voterId.isEmpty()) {
            jsonResponse.addProperty("error", "Invalid Voter ID");
            out.print(jsonResponse.toString());
            return;
        }

        try {
            // Database connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/evoting", "root", "");

            String query = "DELETE FROM voters WHERE VoterID = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, voterId);

            int affectedRows = pst.executeUpdate();

            if (affectedRows > 0) {
                jsonResponse.addProperty("message", "Voter deleted successfully");
            } else {
                jsonResponse.addProperty("error", "Voter ID not found");
            }

            out.print(jsonResponse.toString());

            pst.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.addProperty("error", "Database error");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(jsonResponse.toString());
        }
    }
}
