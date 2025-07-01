package com.servlets.web;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacpp.opencv_imgcodecs;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;

import java.io.IOException;
import java.sql.*;
import java.util.Base64;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/FaceCaptureServlet")
public class FaceCaptureServlet extends HttpServlet {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/evoting";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final String CASCADE_PATH = "C:/Users/Administrator/eclipse-workspace/facelbph/haarcascade_frontalface_default.xml";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject jsonData = gson.fromJson(request.getReader(), JsonObject.class);
        StringBuilder responseMessage = new StringBuilder();

        if (!jsonData.has("voterId") || !jsonData.has("images")) {
            response.getWriter().write("Error: Missing voterId or images");
            return;
        }

        String voterId = jsonData.get("voterId").getAsString();
        String sampleType = jsonData.has("sampleType") ? jsonData.get("sampleType").getAsString() : "training";

        CascadeClassifier faceDetector = new CascadeClassifier(CASCADE_PATH);
        if (faceDetector.empty()) {
            response.getWriter().write("Error: Could not load Haarcascade file!");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            PreparedStatement insertStmt = conn.prepareStatement(
                "INSERT INTO face_samples (voter_id, image_data, sample_type, capture_time) VALUES (?, ?, ?, CURRENT_TIMESTAMP)");
            
            int insertedCount = 0;

            for (JsonElement imageJson : jsonData.get("images").getAsJsonArray()) {
                String base64Image = imageJson.getAsString();
                if (base64Image.contains(",")) {
                    base64Image = base64Image.split(",")[1];
                }

                byte[] imageBytes = Base64.getDecoder().decode(base64Image);
                Mat image = opencv_imgcodecs.imdecode(new Mat(new BytePointer(imageBytes)), opencv_imgcodecs.IMREAD_COLOR);
                Mat grayImage = new Mat();
                opencv_imgproc.cvtColor(image, grayImage, opencv_imgproc.COLOR_BGR2GRAY);

                RectVector faces = new RectVector();
                faceDetector.detectMultiScale(grayImage, faces);

                if (faces.size() > 0) {
                    for (int i = 0; i < faces.size(); i++) {
                        Rect face = faces.get(i);
                        opencv_imgproc.rectangle(image, face, new Scalar(0, 255, 0, 1));
                    }

                    BytePointer buffer = new BytePointer();
                    opencv_imgcodecs.imencode(".jpg", image, buffer);
                    byte[] processedImageBytes = new byte[(int) buffer.limit()];
                    buffer.get(processedImageBytes);

                    insertStmt.setString(1, voterId);
                    insertStmt.setBytes(2, processedImageBytes);
                    insertStmt.setString(3, sampleType);
                    insertStmt.executeUpdate();
                    insertedCount++;
                }
            }

            responseMessage.append("Successfully stored ").append(insertedCount).append(" face samples with bounding boxes.");

            if (insertedCount > 0) {
                // Run normal Training and YaleTraining in separate threads
                new Thread(() -> {
                    try {
                        Training training = new Training();
                        training.training();
                        System.out.println("Normal Face training completed successfully.");
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("Normal Training failed: " + e.getMessage());
                    }
                }).start();

                new Thread(() -> {
                    try {
                        YaleTraining yaleTraining = new YaleTraining();
                        yaleTraining.yaleTraining();
                        System.out.println("Yale Face training completed successfully.");
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("Yale Training failed: " + e.getMessage());
                    }
                }).start();

                responseMessage.append("\nFace training (Normal & Yale) started...");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            responseMessage.append("Error storing face samples: ").append(e.getMessage());
        }

        response.getWriter().write(responseMessage.toString());
    }
}
