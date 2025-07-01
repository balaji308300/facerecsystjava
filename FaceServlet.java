package com.servlets.web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/face")
public class FaceServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;


    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        startFaceTraining(response);
    }

    private void startFaceTraining(HttpServletResponse response) throws IOException {
        new Thread(() -> {
            Training training = new Training();
            training.training(); // Calls the training method
        }).start();

        response.getWriter().println("Face training started...");
    }
}
