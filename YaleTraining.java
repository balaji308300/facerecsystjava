package com.servlets.web;

import static org.bytedeco.javacpp.opencv_face.createLBPHFaceRecognizer;

import java.nio.IntBuffer;
import java.sql.*;
import javax.swing.JOptionPane;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_face.FaceRecognizer;
import org.bytedeco.javacpp.opencv_imgcodecs;
import org.bytedeco.javacpp.opencv_imgproc;

public class YaleTraining {
    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/evoting";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public void yaleTraining() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Get total number of images
            int totalImages = getImageCount(conn);
            if (totalImages == 0) {
                JOptionPane.showMessageDialog(null, "No training images found!", "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }

            MatVector photos = new MatVector(totalImages);
            Mat labels = new Mat(totalImages, 1, opencv_core.CV_32SC1);
            IntBuffer labelsBuffer = labels.createBuffer();

            String query = "SELECT voter_id, image_data FROM face_samples";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                
                int counter = 0;
                while (rs.next() && counter < totalImages) {
                    byte[] imageData = rs.getBytes("image_data");
                    String voterId = rs.getString("voter_id");

                    if (imageData == null || voterId == null || voterId.trim().isEmpty()) {
                        System.err.println("Skipping null image or voter ID");
                        continue;
                    }

                    try {
                        // Extract numeric part of voter ID
                        String voterNumeric = voterId.replaceAll("\\D+", ""); // Remove non-numeric characters

                        if (voterNumeric.isEmpty()) {
                            System.err.println("Skipping voter ID with no numeric part: " + voterId);
                            continue;
                        }

                        int voterInt = Integer.parseInt(voterNumeric); // Convert cleaned voter ID to integer

                        // Decode image correctly
                        Mat photo = opencv_imgcodecs.imdecode(new Mat(imageData), opencv_imgcodecs.IMREAD_GRAYSCALE);

                        if (photo.empty()) {
                            System.err.println("Skipping invalid image for voter: " + voterId);
                            continue;
                        }

                        opencv_imgproc.resize(photo, photo, new Size(160, 160));
                        photos.put(counter, photo);
                        labelsBuffer.put(counter, voterInt);
                        counter++;
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping invalid voter ID format: " + voterId);
                    }
                }

                // Adjust size if some images were skipped
                if (counter == 0) {
                    JOptionPane.showMessageDialog(null, "No valid images found for training!", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                photos.resize(counter);
                labels.rows(counter);
            }

            if (photos.size() == 0) {
                JOptionPane.showMessageDialog(null, "No valid images found for training!", "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Train LBPH recognizer
            FaceRecognizer lbph = createLBPHFaceRecognizer(12, 10, 15, 15, 0);
            lbph.train(photos, labels);

            String modelPath = "C:/Users/Administrator/eclipse-workspace/E-votingsyste/classifierLBPHYale.yml";
            lbph.save(modelPath);

            JOptionPane.showMessageDialog(null, "Trained " + photos.size() + " samples successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getImageCount(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM face_samples")) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public static void main(String[] args) {
        new YaleTraining().yaleTraining();
    }
}