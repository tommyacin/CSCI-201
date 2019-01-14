<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>

<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Friend Page</title>
		<meta name="google-signin-scope" content="profile email https://www.googleapis.com/auth/calendar">
		<!-- bootstrap -->
		<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css"/>
		<link rel="stylesheet" type="text/css" href="./style.css"/>
		<meta name="google-signin-client_id" content="770131881416-mcjm57654v12pe5nt7qh6fl4skf668oj.apps.googleusercontent.com">
		<script>
			function load_profile()
			{
				var urlParams = new URLSearchParams(window.location.search);
				var friend_email = urlParams.get('email');
				var friend_fname = urlParams.get('fname');
				document.getElementById("friend_name").innerHTML = friend_fname + "\'s Upcoming Events";
				
				var request_str = "ProfileServlet?field=friend_page&friend_email=" + friend_email + "&friend_fname=" + friend_fname;
				
				var xhttp = new XMLHttpRequest();
				xhttp.open("GET", request_str, false);
				xhttp.send();
				
				var tableHeader = "<tr><th>Date</th><th>Time</th><th>Event Summary</th></tr>";
				var responseText = xhttp.responseText.trim().split('^');
				document.getElementById("follow_button").innerHTML = responseText[0];
				document.getElementById("eventsTable").innerHTML = tableHeader + responseText[1];
			};
			
			function change_follow_status()
			{
				var urlParams = new URLSearchParams(window.location.search);
				var friend_email = urlParams.get('email');
				var xhttp = new XMLHttpRequest();
	     		xhttp.open("POST", "ProfileServlet");
	     		xhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
				if (document.getElementById("follow_button").innerHTML === "Follow")	
					xhttp.send("field=follow_friend&friend_email=" + friend_email);
				else
					xhttp.send("field=unfollow_friend&friend_email=" + friend_email);
				load_profile();
			};
			
			function copy_event(eventTitle, startDate, endDate, startTime, endTime)
			{
				var confirmation = confirm("Would you like to add this event to your calendar?");
				if (confirmation == true)
				{
					var xhttp = new XMLHttpRequest();
					var requestStr = "field=event&eventTitle=" + eventTitle + "&startDate=" + startDate;
					requestStr += "&endDate=" + endDate + "&startTime=" + startTime + "&endTime=" + endTime;
					xhttp.open("POST", "ProfileServlet");
					xhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
					xhttp.send(requestStr);
					
					var xhttp2 = new XMLHttpRequest();
					xhttp2.open("POST", "ProfileServlet");
					xhttp2.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
					xhttp2.send(requestStr);
					
					document.location.href = "./success.jsp";
				}
			};
		</script>
		<script src="https://apis.google.com/js/platform.js" async defer onload="this.onload=function(){};load_profile()"></script>
	</head>
	<body>
		<!-- top black bar -->
		<div class="header">
			<div class="row">
				<!-- take to signed in page -->
				<div class="col-6>">
					<form action="./signedin.jsp" method="GET">
						<input type="submit" value="Sycamore Calendar" name="signed_in" id="signed_in" />
					</form>
				</div>
				<!-- search bar -->
				<div class="col">
					<form action="./search_results.jsp" method="GET">
						<input type="text" name="search" id="search" placeholder="Search Friends"/>
						<input type="image" src="search_bar.PNG" id="search_bar_image"/>
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
		<!--middle of search results page -->
		<div class="main home">
			<!-- row containing everything-->
			<div class="row h-100" id="friend_content">
				<div class="col-8">
					<h1 id="friend_name"></h1>
					<table class="table" id="eventsTable"></table>
				</div>
				<div class="col">
					<br/><br/><br/>
					<button type="button" id="follow_button" onclick="change_follow_status()"></button>
					<br/><br/><br/>
					<figure>
						<img src="<%= request.getSession().getAttribute("friendImage") %>" id="user_image"/>
						<figcaption id="user_name"><%= request.getSession().getAttribute("friendName") %></figcaption>
					</figure>
				</div>
			</div>
		</div>
		<!-- bottom black bar -->
		<div class="footer"></div>
	</body>
</html>