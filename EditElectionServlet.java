package com.servlets.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

@WebServlet("/EditElectionServlet")
public class EditElectionServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        int electionId = Integer.parseInt(request.getParameter("id"));
        JSONObject jsonResponse = new JSONObject();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/evoting", "root", "");
            String sql = "SELECT * FROM elections WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, electionId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                jsonResponse.put("id", rs.getInt("id"));
                jsonResponse.put("name", rs.getString("election_name"));
                jsonResponse.put("date", rs.getString("election_date"));
                jsonResponse.put("start_time", rs.getString("start_time"));
                jsonResponse.put("end_time", rs.getString("end_time"));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                jsonResponse.put("error", "Election not found");
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("error", "Database error: " + e.getMessage());
        }

        PrintWriter out = response.getWriter();
        out.print(jsonResponse.toString());
        out.flush();
    }
}
