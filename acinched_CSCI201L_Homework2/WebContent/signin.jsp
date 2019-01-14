<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>

<html>
	<head> 
		<meta charset="ISO-8859-1">
		<meta name="google-signin-scope" content="profile email https://www.googleapis.com/auth/calendar">
		<meta name="google-signin-client_id" content="770131881416-mcjm57654v12pe5nt7qh6fl4skf668oj.apps.googleusercontent.com">
		<title>Sycamore Sign-In</title>
		<!-- bootstrap -->
		<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css"/>
		<link rel="stylesheet" type="text/css" href="./style.css"/>
		<script src="https://apis.google.com/js/platform.js" async defer></script>
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
		<script>
	     	function onSignIn(googleUser) 
	     	{
			    // The ID token you need to pass to your backend:
			    let GoogleAuth = gapi.auth2.getAuthInstance();
				// Retrieve the GoogleUser object for the current user.
				var GoogleUser = GoogleAuth.currentUser.get();
				GoogleUser.grant({'scope':'https://www.googleapis.com/auth/calendar'})
				
		        var profile = googleUser.getBasicProfile();
	     		var xhttp = new XMLHttpRequest();
	     		xhttp.open("POST", "ProfileServlet");
	     		xhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	     		xhttp.send("field=profile&name=" + profile.getName() + "&image_url=" + profile.getImageUrl());
		        document.location.href = "./profile.jsp";
	      	};
	    </script>
	</head>
	<body>
		<!-- top black bar -->
		<div class="header">
			<div class="row"></div>
		</div>
		<!--middle of login page -->
		<div class="main">
			<!-- row containing everything-->
			<div class="row h-100 align-items-center justify-content-center" id="login_content">
				<!-- leaf image -->
				<div class="col-5" id="leaf_col">
					<img src="leaf.png" alt="Sycamore Leaf" id="leaf"/>
				</div>
				<!-- title and sign in/out buttons -->
				<div class="col-7" id="title_col">
					<h1 id="sycamore_text">Sycamore Calendar</h1>
					<br /><br /><br /><br /><br /><br />
					<div class="g-signin2" data-onsuccess="onSignIn" data-theme="light"></div>
				</div>
			</div>
		</div>
		<!-- bottom black bar -->
		<div class="footer"></div>
	</body>
</html>