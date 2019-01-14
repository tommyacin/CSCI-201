<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>

<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Home</title>
		<meta name="google-signin-scope" content="profile email https://www.googleapis.com/auth/calendar">
		<!-- bootstrap -->
		<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css"/>
		<link rel="stylesheet" type="text/css" href="./style.css"/>
		<meta name="google-signin-client_id" content="770131881416-mcjm57654v12pe5nt7qh6fl4skf668oj.apps.googleusercontent.com">
		<script src="https://apis.google.com/js/platform.js" async defer></script>
		<script>
			function validateForm()
			{			     
				var request_str = "ProfileServlet?";
				request_str += "field=event";
				request_str += "&eventTitle=" + document.eventForm.eventTitle.value;
				request_str += "&startDate=" + document.eventForm.startDate.value;
				request_str += "&endDate=" + document.eventForm.endDate.value;
				request_str += "&startTime=" + document.eventForm.startTime.value;
				request_str += "&endTime=" + document.eventForm.endTime.value;
				
				var xhttp = new XMLHttpRequest();
				xhttp.open("GET", request_str, false);
				xhttp.send();
				
				if (xhttp.responseText.trim().length > 0)
				{
					/* document.getElementById("error_msg").innerHTML = xhttp.responseText; */
					alert(xhttp.responseText);
					return false;
				}
			    return true;
			};
		</script>
	</head>
	<body>
		<!-- top black bar -->
		<div class="header">
			<div class="row">
				<!-- take to signed in page -->
				<div class="col-8>">
					<form action="./signedin.jsp" method="GET">
						<input type="submit" value="Sycamore Calendar" name="signed_in" id="signed_in" />
					</form>
				</div>
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
		<div class="main home">
			<div class="row h-100 align-items-center justify-content-center" id="home_content">
				<!-- home content  -->
				<div class="col">
					<h1>Home</h1><br /><br /><br />
					<div id=home_page>
						<figure>
							<br />
							<img src="<%= request.getSession().getAttribute("ImageURL") %>" id="user_image"/>
							<figcaption id="user_name"><%= request.getSession().getAttribute("UserName") %></figcaption>
						</figure>
						<form id="eventForm" name="eventForm" method="GET" action="./success.jsp" onsubmit="return validateForm();">
							<br /><br />
							<input type="text" name="eventTitle" id="eventTitle" placeholder="Event Title"><br /><br />
							<input type="date" name="startDate" id="startDate" placeholder="Start Date">
							<input type="date" name="endDate" id="endDate" placeholder="End Date">
							<input type="submit" name="addEvent" value="Add Event" id="addEvent"><br /><br />
							<input type="time" name="startTime" id="startTime" placeholder="Start Time">
							<input type="time" name="endTime" id="endTime" placeholder="End Time"><br /><br />
							<!-- <p id="error_msg"></p> -->
						</form>
						<br /><br />
					</div>
				</div>
			</div>
		</div>
		<!-- bottom black bar -->
		<div class="footer"></div>
	</body>
</html>