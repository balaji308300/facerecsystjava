<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Face Recognition - E-Voting</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <style>
        .sidebar {
            width: 250px;
            height: 100vh;
            position: fixed;
            left: 0;
            top: 0;
            background-color: #343a40;
            padding-top: 20px;
        }
        .sidebar a {
            color: white;
            padding: 10px 20px;
            display: block;
            text-decoration: none;
        }
        .sidebar a:hover {
            background-color: #495057;
        }
        .content {
            margin-left: 260px;
            padding: 20px;
        }
    </style>
</head>
<body class="bg-light">
    
    <!-- Sidebar -->
    <div class="sidebar">
        <h4 class="text-white text-center">E-Voting</h4>
        <a href="dashboard.html">Home</a>
        <a href="election.html">Election</a>
        <a href="results.html">Results</a>
        <a href="edituserprofile.html" id="editProfileLink">Edit Profile</a>
        <a href="openpage.html" class="btn btn-danger btn-sm w-100 mt-2">Logout</a>
    </div>
    
    <!-- Main Content -->
    <div class="content">
        <div class="container mt-4">
            <h2 class="text-center">Face Recognition</h2>
            <p class="text-center">Click the button to start Face Recognition</p>
            <form id="faceRecForm" class="text-center">
                <button type="button" class="btn btn-primary" id="startRecognition">Start Recognition</button>
            </form>
            <div id="recognitionResult" class="text-center mt-3"></div>
        </div>
    </div>
	    <script>
    // Function to get query parameters from URL
    function getQueryParam(param) {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get(param);
    }

    $(document).ready(function() {
        $("#startRecognition").click(function() {
            let candidateId = getQueryParam("candidateId"); 
            let electionId = getQueryParam("electionId"); 
            let voterId = getQueryParam("voterId"); 

            if (!candidateId || !electionId || !voterId) {
                $("#recognitionResult").html("<p class='text-danger'>Missing parameters in URL.</p>");
                return;
            }

            $("#recognitionResult").html("<p class='text-success'>Face Recognition Started...</p>");
            
            $.ajax({
                url: "facereg",
                method: "POST",
                data: { 
                    candidateId: candidateId, 
                    electionId: electionId,
                    voterId: voterId
                },
                success: function(response) {
                    $("#recognitionResult").html("<p class='text-success'>" + response + "</p>");
                },
                error: function() {
                    $("#recognitionResult").html("<p class='text-danger'>Error starting face recognition.</p>");
                }
            });
        });
    });
</script>
	    
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
