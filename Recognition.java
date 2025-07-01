package com.servlets.web;

import static org.bytedeco.javacpp.opencv_face.createLBPHFaceRecognizer;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_core.RectVector;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_face.FaceRecognizer;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.OpenCVFrameConverter;

public class Recognition {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/evoting";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private String candidateId;
    private String electionId;
    private int voterId;

    public Recognition(String candidateId, String electionId, int voterId) {
        this.candidateId = candidateId;
        this.electionId = electionId;
        this.voterId = voterId;
    }

    public void startRecognition() {
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        OpenCVFrameGrabber camera = new OpenCVFrameGrabber(0);

        try {
            HashMap<Integer, String> faceData = loadFaceDataFromDB();
            
            CascadeClassifier faceDetector = new CascadeClassifier("C:/Users/Administrator/eclipse-workspace/E-votingsyste/haarcascade_frontalface_default.xml");
            FaceRecognizer recognizer = createLBPHFaceRecognizer();
            recognizer.load("C:/Users/Administrator/eclipse-workspace/E-votingsyste/classifierLBPH.yml");
            recognizer.setThreshold(65.0);

            CanvasFrame frameWindow = new CanvasFrame("Face Recognition");
            frameWindow.setDefaultCloseOperation(CanvasFrame.DISPOSE_ON_CLOSE);

            
            camera.start();
            
            while (frameWindow.isVisible()) {
                Frame frame = camera.grab();
                if (frame == null) continue;

                Mat colorImage = converter.convert(frame);
                Mat grayImage = new Mat();
                opencv_imgproc.cvtColor(colorImage, grayImage, opencv_imgproc.COLOR_BGRA2GRAY);

                RectVector faces = new RectVector();
                faceDetector.detectMultiScale(grayImage, faces);

                processDetectedFaces(faces, grayImage, colorImage, faceData, recognizer);
                frameWindow.showImage(frame);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                camera.stop();
                camera.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private HashMap<Integer, String> loadFaceDataFromDB() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        HashMap<Integer, String> faceData = new HashMap<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT voter_id, name FROM facecapture")) {
            
            while (rs.next()) {
                faceData.put(rs.getInt("voter_id"), rs.getString("name"));
            }
        }
        return faceData;
    }

    private void processDetectedFaces(RectVector faces, Mat grayImage, Mat colorImage, 
                                     HashMap<Integer, String> faceData, FaceRecognizer recognizer) {
        for (int i = 0; i < faces.size(); i++) {
            Rect faceRect = faces.get(i);
            opencv_imgproc.rectangle(colorImage, faceRect, new Scalar(0, 255, 0, 0));

            Mat faceROI = new Mat(grayImage, faceRect);
            opencv_imgproc.resize(faceROI, faceROI, new Size(160, 160));

            IntPointer label = new IntPointer(1);
            DoublePointer confidence = new DoublePointer(1);
            recognizer.predict(faceROI, label, confidence);

            int predictedLabel = label.get(0);
            double conf = confidence.get(0);
            String recognitionResult = getRecognitionResult(predictedLabel, conf, faceData);

            if (!recognitionResult.equals("Unknown")) {
                saveVoterDetails(predictedLabel, faceData.get(predictedLabel));
            }

            drawRecognitionResult(colorImage, faceRect, recognitionResult);
        }
    }

    private String getRecognitionResult(int predictedLabel, double confidence, 
                                       HashMap<Integer, String> faceData) {
        if (predictedLabel == -1 || confidence > 65) {
            return "Unknown";
        }
        return faceData.getOrDefault(predictedLabel, "Unknown") + 
               String.format(" (%.1f)", confidence);
    }

    private void drawRecognitionResult(Mat image, Rect faceRect, String text) {
        Point textPosition = new Point(
            Math.max(faceRect.tl().x() - 10, 0),
            Math.max(faceRect.tl().y() - 10, 0)
        );
        
        opencv_imgproc.putText(image, text, textPosition, 
            opencv_core.FONT_HERSHEY_PLAIN, 1.4, new Scalar(255, 0, 0, 0));
    }

    private void saveVoterDetails(int voterId, String voterName) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String checkQuery = "SELECT COUNT(*) FROM votes WHERE voter_id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, voterId);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Voter ID " + voterId + " has already voted. Vote not stored.");
                    return;
                }
            }

            String insertQuery = "INSERT INTO votes (voter_id, voter_name, candidate_id, timestamp) VALUES (?, ?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setInt(1, voterId);
                insertStmt.setString(2, voterName);
                insertStmt.setString(3, candidateId);
                insertStmt.setString(4, timestamp);
                insertStmt.executeUpdate();
                System.out.println("Vote stored successfully for Voter ID: " + voterId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
