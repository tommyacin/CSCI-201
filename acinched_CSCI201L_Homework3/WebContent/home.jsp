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
			
			function load_following()
			{
				var request_str = "ProfileServlet?field=load_following";
				var xhttp = new XMLHttpRequest();
				xhttp.open("GET", request_str, false);
				xhttp.send();
	     		
	     		if (xhttp.responseText.trim().length == 0) //Not following anyone
	     		{
	     			document.getElementById("no_followers").innerHTML = "You are not following any users";
	     			return;
	     		}
	     		
	     		var users = xhttp.responseText.trim().split('#');
     			for (i=1; i<users.length; i++) //start at 1 b/c 0th index will always be blank due to response starting with '#'
     			{
     				users[i] = users[i].split('&');
     				var user_email = users[i][0];
     				var user_fname = users[i][1];
     				var user_lname = users[i][2];
     				var user_image_url = users[i][3];
     				var display_HTML = "<a href=\"./friend.jsp?email=" + user_email + "&fname=" + user_fname + "\">";
     				display_HTML += "<br/><figure><img src=\"" + user_image_url + "\" id=\"search_image\"/>";
     				display_HTML += "<figcaption id=\"search_name\">" + user_fname + " " + user_lname + "</figcaption></figure></a>";
     				var search_display = document.getElementById("following_col" + ((i-1)%4));
     				search_display.innerHTML += display_HTML;
     			}
			};
		</script>
		<script src="https://apis.google.com/js/platform.js" async defer onload="this.onload=function(){};load_following()"></script>
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
						</form><br/>
						<h1>Following</h1><br /><br /><br /><br />
						<div class="row">
							<h1 id="no_followers"></h1>
							<div class="col">
								<p id="following_col0"></p>
							</div>
							<div class="col">
								<p id="following_col1"></p>
							</div>
							<div class="col">
								<p id="following_col2"></p>
							</div>
							<div class="col">
								<p id="following_col3"></p>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- bottom black bar -->
		<div class="footer"></div>
	</body>
</html>