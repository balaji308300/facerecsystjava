<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.sql.*" %>
<%@ page import="java.util.Base64" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Face Training - E-Voting</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <style>
        body {
            font-family: Arial, sans-serif;
        }
        .sidebar {
            height: 100vh;
            width: 250px;
            position: fixed;
            background: #343a40;
            color: white;
            padding: 20px;
        }
        .sidebar a {
            color: white;
            text-decoration: none;
            display: block;
            padding: 10px;
            margin-bottom: 5px;
            border-radius: 5px;
        }
        .sidebar a:hover {
            background: #495057;
        }
        .content {
            margin-left: 260px;
            padding: 20px;
        }
        img {
            width: 100px;
            height: 100px;
            border-radius: 10px;
        }
    </style>
</head>
<body>

<!-- Sidebar -->
<div class="sidebar">
    <h3>E-Voting Admin</h3>
    <a href="admin_dashboard.html">🏠 Home</a>
    <a href="userlist.html">👥 User List</a>
    <a href="add_party.html">➕ Add Political Party</a>
    <a href="partylist.html">📜 Political Party List</a>
    <a href="addcandidate.html">➕ Add Candidate</a>
    <a href="view_candidate.html">📜 Candidate List</a>
    <a href="addelection.html">🗳️ Add Election</a>
    <a href="election-list.html">📜 Election List</a>
    <a href="facetraining.jsp">📸 Face Training</a>
    <a href="#">📊 View Results</a>
    <a href="openpage.html" class="text-danger">🚪 Logout</a>
</div>

<!-- Content -->
<div class="content text-center">
    <h2>Face Training</h2>
    <p>Click the buttons below to capture your face and start training.</p>

    <button id="captureFaceBtn" class="btn btn-warning mt-3">Capture Face</button>
    <button id="startTrainingBtn" class="btn btn-primary mt-3">Start Face Training</button>

    <div id="statusMessage" class="mt-3"></div>
    
    <h3 class="mt-5">Captured Faces</h3>
    <table class="table table-bordered mt-3">
        <thead class="table-dark">
            <tr>
                <th>Voter ID</th>
                <th>Face Image</th>
            </tr>
        </thead>
        <tbody>
            <% 
                Connection con = null;
                PreparedStatement ps = null;
                ResultSet rs = null;
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    con = DriverManager.getConnection("jdbc:mysql://localhost:3306/evoting", "root", "");
                    String query = "SELECT distinct fc.voter_id, fi.image_data FROM facecapture fc JOIN face_images fi ON fc.voter_id = fi.voter_id order by fc.voter_id desc";
                    ps = con.prepareStatement(query);
                    rs = ps.executeQuery();
                    while (rs.next()) {
                        String voterId = rs.getString("voter_id");
                        byte[] imageData = rs.getBytes("image_data");
                        String base64Image = Base64.getEncoder().encodeToString(imageData);
            %>
                        <tr>
                            <td><%= voterId %></td>
                            <td><img src="data:image/jpeg;base64,<%= base64Image %>" alt="Face Image"></td>
                        </tr>
            <% 
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (rs != null) rs.close();
                    if (ps != null) ps.close();
                    if (con != null) con.close();
                }
            %>
        </tbody>
    </table>
</div>

<script>
$(document).ready(function () {
    $("#captureFaceBtn").click(function () {
        $("#statusMessage").html("<p class='text-info'>Capturing face...</p>");
        
        $.ajax({
            url: "CaptureFace",
            type: "POST",
            success: function (response) {
                $("#statusMessage").html("<p class='text-success'>Face captured successfully!</p>");
                location.reload();
            },
            error: function (xhr, status, error) {
                $("#statusMessage").html("<p class='text-danger'>Error capturing face.</p>");
                console.log("Error:", error);
            }
        });
    });

    $("#startTrainingBtn").click(function () {
        $("#statusMessage").html("<p class='text-info'>Training in progress...</p>");
        
        $.ajax({
            url: "face",
            type: "POST",
            success: function (response) {
                $("#statusMessage").html("<p class='text-success'>Face training completed successfully!</p>");
            },
            error: function (xhr, status, error) {
                $("#statusMessage").html("<p class='text-danger'>Error starting training.</p>");
                console.log("Error:", error);
            }
        });
    });
});
</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>
