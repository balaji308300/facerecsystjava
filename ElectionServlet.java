package com.servlets.web;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

@WebServlet("/ElectionServlet")
public class ElectionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        List<Map<String, String>> elections = new ArrayList<>();
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/evoting", "root", "");
            String sql = "SELECT * FROM elections";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, String> election = new HashMap<>();
                election.put("id", rs.getString("id"));
                election.put("election_name", rs.getString("election_name"));
                election.put("created_at", rs.getString("created_at"));
                election.put("election_date", rs.getString("election_date"));
                election.put("start_time", rs.getString("start_time"));
                election.put("end_time", rs.getString("end_time"));
                elections.add(election);
            }
            
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write(new Gson().toJson(Map.of("error", "Database connection error.")));
            return;
        }
        
        response.getWriter().write(new Gson().toJson(elections));
    }
}
