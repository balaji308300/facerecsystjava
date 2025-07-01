package com.servlets.web;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

@WebServlet("/FetchVoterDataServlet")
public class FetchVoterDataServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String voterId = request.getParameter("voterId");
        JSONObject jsonResponse = new JSONObject();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/evoting", "root", "");

            String sql = "SELECT firstname, lastname, dob, mobile, gender FROM users WHERE proof_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, voterId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                jsonResponse.put("success", true);
                jsonResponse.put("firstName", rs.getString("firstname"));
                jsonResponse.put("lastName", rs.getString("lastname"));
                jsonResponse.put("dob", rs.getString("dob"));
                jsonResponse.put("mobile", rs.getString("mobile"));
                jsonResponse.put("gender", rs.getString("gender"));
            } else {
                jsonResponse.put("success", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.put("success", false);
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        response.setContentType("application/json");
        response.getWriter().write(jsonResponse.toString());
    }
}
