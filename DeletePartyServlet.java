package com.servlets.web;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.db.web.DBConnection;

@WebServlet("/DeletePartyServlet")
public class DeletePartyServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int partyId = Integer.parseInt(request.getParameter("id"));
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection(); // Get a new connection
            if (conn == null || conn.isClosed()) {
                throw new Exception("Database connection is closed or not available.");
            }

            String query = "DELETE FROM political_parties WHERE id = ?";
            ps = conn.prepareStatement(query);
            ps.setInt(1, partyId);
            ps.executeUpdate();

            // Redirect back to party list after deletion
            response.sendRedirect("partylist.html");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("partylist.html?error=Could not delete party");
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
