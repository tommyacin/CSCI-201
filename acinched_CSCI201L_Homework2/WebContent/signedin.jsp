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
		<script>
	     	function signOut() {
			    var auth2 = gapi.auth2.getAuthInstance();
			    auth2.signOut();
			    document.location.href = "./signin.jsp";
	      	};
	    </script>
	</head>
	<body>
		<!-- top black bar -->
		<div class="header">
			<div class="row">
				<!-- empty column to push profile and home to the right -->
				<div class="col-8"></div>
				<!-- profile page button -->
				<div class="col">
					<form action="./profile.jsp" method="GET">
						<input type="submit" value="Profile" name="profile" id="profile" />
					</form>
				</div>
				<!-- home page button -->
				<div class="col">
					<form action="./home.jsp" method="GET">
						<input type="submit" value="Home" name="home" id="home" />
					</form>
				</div>
			</div>
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
					<div class="g-signin2" onclick="signOut();" data-theme="light"></div>
				</div>
			</div>
		</div>
		<!-- bottom black bar -->
		<div class="footer"></div>
	</body>
</html>