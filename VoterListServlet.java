package com.servlets.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

// Model class for Voter
class Voter {
    private String voterId;
    private String date;

    public Voter(String voterId, String date) {
        this.voterId = voterId;
        this.date = date;
    }
}

// Servlet to fetch voter list
@WebServlet("/VoterListServlet")
public class VoterListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        ArrayList<Voter> voterList = new ArrayList<>();

        try {
            // Database connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/evoting", "root", "");

            String query = "SELECT VoterID, created_at FROM voters"; // Adjust column names as per your DB
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                voterList.add(new Voter(rs.getString("VoterID"), rs.getString("created_at")));
            }

            // Convert list to JSON
            String json = new Gson().toJson(voterList);
            out.print(json);

            rs.close();
            pst.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Database error\"}");
        }
    }
}
