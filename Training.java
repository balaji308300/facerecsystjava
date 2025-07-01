package com.servlets.web;



import static org.bytedeco.javacpp.opencv_face.createLBPHFaceRecognizer;

import java.nio.IntBuffer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_face.FaceRecognizer;
import org.bytedeco.javacpp.opencv_imgcodecs;
import org.bytedeco.javacpp.opencv_imgproc;

public class Training {
    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/evoting";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public void training() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Retrieve all face images and labels from database
            List<FaceData> faceData = getFaceDataFromDB(conn);

            MatVector photos = new MatVector(faceData.size());
            Mat labels = new Mat(faceData.size(), 1, opencv_core.CV_32SC1);
            IntBuffer bufferLabels = labels.createBuffer();

            int counter = 0;
            for (FaceData data : faceData) {
                // Convert BLOB to Mat
                Mat photo = opencv_imgcodecs.imdecode(
                    new Mat(data.imageData), 
                    opencv_imgcodecs.IMREAD_GRAYSCALE
                );

                if (photo.empty()) {
                    System.err.println("Error decoding image for user: " + data.voter_id);
                    continue;
                }

                opencv_imgproc.resize(photo, photo, new Size(160, 160));
                photos.put(counter, photo);
                bufferLabels.put(counter, data.voter_id);
                counter++;
            }

            // Train LBPH recognizer
            FaceRecognizer lbph = createLBPHFaceRecognizer();
            lbph.train(photos, labels);
            lbph.save("C:/Users/Administrator/eclipse-workspace/E-votingsyste/classifierLBPH.yml");
            
            JOptionPane.showMessageDialog(null, 
                "Training completed with " + counter + " samples!", 
                "FACE RECOGNITION", 
                JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Database error: " + e.getMessage(), 
                "ERROR", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<FaceData> getFaceDataFromDB(Connection conn) throws SQLException {
        List<FaceData> faceData = new ArrayList<>();
        
        String query = "SELECT voter_id, image_data FROM face_images";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                int voter_id = rs.getInt("voter_id");
                byte[] imageData = rs.getBytes("image_data");
                faceData.add(new FaceData(voter_id, imageData));
            }
        }
        return faceData;
    }

    // Helper class to store face data
    private static class FaceData {
        int voter_id;
        byte[] imageData;

        FaceData(int voter_id, byte[] imageData) {
            this.voter_id = voter_id;
            this.imageData = imageData;
        }
    }

    public static void main(String[] args) {
        new Training().training();
    }
}