package com.servlets.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

@WebServlet("/FetchCandidatesServlet")
public class FetchCandidatesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        ArrayList<Candidate> candidates = new ArrayList<>();

        try {
            // Database connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/evoting", "root", "");

            // SQL query
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM candidates");

            // Fetch data
            while (rs.next()) {
                candidates.add(new Candidate(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("voter_id"),
                    rs.getString("dob"),
                    rs.getString("mobile"),
                    rs.getString("gender"),
                    rs.getString("party")
                ));
            }

            // Convert list to JSON
            String json = new Gson().toJson(candidates);
            out.print(json);
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"error\": \"Database error\"}");
        }
    }

    // Candidate class for JSON serialization
    class Candidate {
        int id;
        String firstName, lastName, voterId, dob, mobile, gender, party;

        public Candidate(int id, String firstName, String lastName, String voterId, String dob, String mobile, String gender, String party) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.voterId = voterId;
            this.dob = dob;
            this.mobile = mobile;
            this.gender = gender;
            this.party = party;
        }
    }
}
