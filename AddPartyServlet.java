package com.servlets.web;

import java.io.File;
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
@WebServlet("/AddPartyServlet")
@MultipartConfig(maxFileSize = 1024 * 1024 * 10) // Max 10MB
public class AddPartyServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String partyNumber = request.getParameter("partyNumber");
        String partyName = request.getParameter("partyName");
        Part filePart = request.getPart("partyLogo");

        try {
            Connection conn = DBConnection.getConnection();
            String query = "INSERT INTO political_parties (party_number, name, logo) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, partyNumber);
            ps.setString(2, partyName);

            // Convert image to InputStream
            if (filePart != null) {
                ps.setBinaryStream(3, filePart.getInputStream(), (int) filePart.getSize());
            } else {
                ps.setNull(3, java.sql.Types.BLOB);
            }

            ps.executeUpdate();
            response.sendRedirect("add_party.html?success=Party Added Successfully");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("add_party.html?error=Error Adding Party");
        }
    }
}
