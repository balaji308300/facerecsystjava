package com.servlets.web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/facereg")
public class FaceRecognitionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        // Extract parameters from request
        String candidateId = request.getParameter("candidateId"); 
        String electionId = request.getParameter("electionId"); 
        String voterIdStr = request.getParameter("voterId");

        if (candidateId == null || electionId == null || voterIdStr == null) {
            response.getWriter().write("Missing parameters.");
            return;
        }

        int voterId;
        try {
            voterId = Integer.parseInt(voterIdStr);
        } catch (NumberFormatException e) {
            response.getWriter().write("Invalid voterId.");
            return;
        }

        // Pass extracted values explicitly to Recognition class
        new Thread(() -> {
            Recognition recognition = new Recognition(candidateId, electionId, voterId);
            recognition.startRecognition();
        }).start();

        response.getWriter().write("Face Recognition started...");
    }
}
