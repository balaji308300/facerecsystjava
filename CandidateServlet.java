package com.servlets.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.db.web.DBConnection;

@WebServlet("/CandidateServlet")
public class CandidateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try (Connection con = DBConnection.getConnection()) {
            String query = "SELECT id, first_name, last_name, voter_id, dob, mobile, gender, party FROM candidates";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            
            ArrayList<Candidate> candidates = new ArrayList<>();
            while (rs.next()) {
                Candidate candidate = new Candidate(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("voter_id"),
                    rs.getString("dob"),
                    rs.getString("mobile"),
                    rs.getString("gender"),
                    rs.getString("party")
                );
                candidates.add(candidate);
            }
            String json = new Gson().toJson(candidates);
            out.write(json);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\": \"Database Error: " + e.getMessage() + "\"}");
        }
    }
}
