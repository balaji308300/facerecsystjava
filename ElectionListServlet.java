package com.servlets.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ElectionListServlet")
public class ElectionListServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        List<Election> elections = new ArrayList<>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/evoting", "root", "");
            String sql = "SELECT * FROM elections";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                elections.add(new Election(
                    rs.getInt("id"),
                    rs.getString("election_name"),
                    rs.getString("election_date"),
                    rs.getString("start_time"),
                    rs.getString("end_time")
                ));
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        PrintWriter out = response.getWriter();
        out.write(new Gson().toJson(elections));
        out.close();
    }
}

// Election.java (POJO Class)
class Election {
    int id;
    String name, date, start_time, end_time;
    
    public Election(int id, String name, String date, String start_time, String end_time) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.start_time = start_time;
        this.end_time = end_time;
    }
}
