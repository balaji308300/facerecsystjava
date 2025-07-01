package com.servlets.web;

import java.io.IOException;
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

@WebServlet("/ResultsServlet")
public class ResultsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ArrayList<ElectionResult> resultsList = new ArrayList<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/evoting", "root", "");

            String sql = "SELECT c.first_name AS candidateName, c.party AS partyName, COUNT(v.candidate_id) AS votes " +
                         "FROM votes v " +
                         "JOIN candidates c ON v.candidate_id = c.id " +
                         "GROUP BY v.candidate_id";

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                resultsList.add(new ElectionResult(
                        rs.getString("candidateName"), 
                        rs.getString("partyName"), 
                        rs.getInt("votes")
                ));
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.getWriter().write(new Gson().toJson(resultsList));
    }

    class ElectionResult {
        String candidateName, partyName;
        int votes;

        public ElectionResult(String candidateName, String partyName, int votes) {
            this.candidateName = candidateName;
            this.partyName = partyName;
            this.votes = votes;
        }
    }
}
