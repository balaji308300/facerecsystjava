package com.servlets.web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/CaptureFace")
public class CaptureFace extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            capture captureObj = new capture(); // Create an instance of the capture class
            captureObj.capture(); // Call the capture method
            response.getWriter().println("Face capture completed successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error during face capture: " + e.getMessage());
        }
    }
}
