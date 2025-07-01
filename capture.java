package com.servlets.web;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacpp.opencv_imgcodecs;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;
import org.bytedeco.javacv.*;

import javax.swing.JOptionPane;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

public class capture {
    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/evoting";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public static void main(String[] args) {
        capture captureObj = new capture();
        try {
            captureObj.capture();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void capture() throws Exception {
        OpenCVFrameConverter.ToMat convertMat = new OpenCVFrameConverter.ToMat();
        OpenCVFrameGrabber camera = new OpenCVFrameGrabber(0);
        camera.start();

        String cascadePath = "C:/Users/Administrator/eclipse-workspace/E-votingsyste/haarcascade_frontalface_default.xml";
        if (!Files.exists(Paths.get(cascadePath))) {
            System.err.println("Haarcascade file not found!");
            return;
        }

        CascadeClassifier faceDetector = new CascadeClassifier(cascadePath);
        if (faceDetector.empty()) {
            System.err.println("Failed to load cascade classifier");
            return;
        }

        CanvasFrame canvasFrame = new CanvasFrame("Face Capture");
        String name = JOptionPane.showInputDialog("Enter Face Name:");
        String personId = JOptionPane.showInputDialog("Enter Voter ID:");

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Insert or update user
            PreparedStatement userStmt = conn.prepareStatement(
                "INSERT INTO facecapture (voter_id, name) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE name = ?");
            userStmt.setString(1, personId);
            userStmt.setString(2, name);
            userStmt.setString(3, name);
            userStmt.executeUpdate();

            int sample = 1;
            while (sample <= 30 && canvasFrame.isVisible()) {
                Frame frame = camera.grab();
                Mat matFrame = convertMat.convert(frame);

                Mat grayFrame = new Mat();
                opencv_imgproc.cvtColor(matFrame, grayFrame, opencv_imgproc.COLOR_BGRA2GRAY);

                RectVector faces = new RectVector();
                faceDetector.detectMultiScale(grayFrame, faces);

                if (faces.size() > 0) {
                    Rect faceRect = faces.get(0);
                    Mat face = new Mat(grayFrame, faceRect);
                    opencv_imgproc.resize(face, face, new Size(160, 160));

                    // Convert face to byte array
                    BytePointer buffer = new BytePointer();
                    opencv_imgcodecs.imencode(".jpg", face, buffer);
                    byte[] imageData = new byte[(int) buffer.limit()];
                    buffer.get(imageData);

                    // Store in database
                    PreparedStatement imageStmt = conn.prepareStatement(
                        "INSERT INTO face_images (voter_id, image_data) VALUES (?, ?)");
                    imageStmt.setString(1, personId);
                    imageStmt.setBytes(2, imageData);
                    imageStmt.executeUpdate();

                    System.out.println("Stored sample " + sample + " in database");
                    sample++;
                }
                canvasFrame.showImage(frame);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            camera.stop();
            canvasFrame.dispose();
        }
    }
}
