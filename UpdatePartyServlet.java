package com.servlets.web;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import com.db.web.DBConnection;

@WebServlet("/UpdatePartyServlet")
@MultipartConfig(maxFileSize = 1024 * 1024 * 10) // Max 10MB for images
public class UpdatePartyServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get ID from the request
        int partyId = Integer.parseInt(request.getParameter("partyId"));
        String partyNumber = request.getParameter("partyNumber");
        String partyName = request.getParameter("partyName");
        Part filePart = request.getPart("partyLogo");

        try {
            Connection conn = DBConnection.getConnection();
            String query;

            // Check if new image is uploaded
            if (filePart != null && filePart.getSize() > 0) {
                query = "UPDATE political_parties SET party_number = ?, name = ?, logo = ? WHERE id = ?";
            } else {
                query = "UPDATE political_parties SET party_number = ?, name = ? WHERE id = ?";
            }

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, partyNumber);
            ps.setString(2, partyName);

            if (filePart != null && filePart.getSize() > 0) {
                ps.setBinaryStream(3, filePart.getInputStream(), (int) filePart.getSize());
                ps.setInt(4, partyId);
            } else {
                ps.setInt(3, partyId);
            }

            int rowsUpdated = ps.executeUpdate();
            ps.close();
            conn.close();

            if (rowsUpdated > 0) {
                // Redirect to party-list.html after successful update
                response.sendRedirect("partylist.html");
            } else {
                // Redirect back to edit page if update fails
                response.sendRedirect("edit_party.jsp?id=" + partyId);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("edit_party.jsp?id=" + partyId);
        }
    }
}
