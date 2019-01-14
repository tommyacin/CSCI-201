<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>

<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>User Profile</title>
		<meta name="google-signin-scope" content="profile email https://www.googleapis.com/auth/calendar">
		<!-- bootstrap -->
		<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css"/>
		<link rel="stylesheet" type="text/css" href="./style.css"/>
		<meta name="google-signin-client_id" content="770131881416-mcjm57654v12pe5nt7qh6fl4skf668oj.apps.googleusercontent.com">
		<script src="https://apis.google.com/js/platform.js" async defer></script>
		<script>
			function formatDate(when)
			{
				var year = when.substring(0,4);
				var month = convertMonth(when.substring(5,7));
				var day = when.substring(8,10);
				return month + " " + day + ", " + year;
			};
			
			function formatTime(when)
			{
				var timeIndex = when.indexOf('T');
				var hour = Number(when.substring(timeIndex+1, timeIndex+3));
				var minute = when.substring(timeIndex+4, timeIndex+6);
				if (hour >= 12)
					return (hour-12) + ":" + minute + " PM";
				else
					return hour + ":" + minute + " AM";	
			};
			
			function convertMonth(month)
			{
				var months = ['January', 'February', 'March', 'April', 'May', 'June', 'July',
					'August', 'September', 'October', 'November', 'December'];
				return months[month-1];
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
		<div class="main">
			<div class="row h-100 align-items-center justify-content-center" id="profile_content">
				<!-- upcoming events list -->
				<div class="col-8">
					<h1>Upcoming Events</h1><br /><br /><br />
					<table class="table" id="eventsTable"></table>
	
				    <script type="text/javascript">
				     // Client ID and API key from the Developer Console
				     var CLIENT_ID = '770131881416-mcjm57654v12pe5nt7qh6fl4skf668oj.apps.googleusercontent.com';
				     var API_KEY = 'AIzaSyCwNzkggdRgu6T_9Scg5ZMtjH8ZjHXdCqM';
				
				     // Array of API discovery doc URLs for APIs used by the quickstart
				     var DISCOVERY_DOCS = ["https://www.googleapis.com/discovery/v1/apis/calendar/v3/rest"];
				
				     // Authorization scopes required by the API; multiple scopes can be
				     // included, separated by spaces.
				     var SCOPES = "https://www.googleapis.com/auth/calendar";
				
				     /* On load, called to load the auth2 library and API client library. */
				     function handleClientLoad() 
				     {
				       gapi.load('client:auth2', initClient);
				     }
				
				     /* Initializes the API client library and sets up sign-in state listeners. */
				     function initClient() 
				     {
				       gapi.client.init({
				         apiKey: "",
				         clientId: CLIENT_ID,
				         discoveryDocs: DISCOVERY_DOCS,
				         scope: SCOPES
				       }).then(function () {
				    	  listUpcomingEvents();
				       });
				     }
				
				     /*
				      * Print the summary and start datetime/date of the next ten events in
				      * the authorized user's calendar. If no events are found an
				      * appropriate message is printed.
				      */
				     function listUpcomingEvents() 
				     {
				       gapi.client.calendar.events.list({
				         'calendarId': 'primary',
				         'timeMin': (new Date()).toISOString(),
				         'showDeleted': false,
				         'singleEvents': true,
				         'maxResults': 25,
				         'orderBy': 'startTime'
				       }).then(function(response) {
				         var events = response.result.items;				         
				         var tableString = document.getElementById("eventsTable");
				         tableString.innerHTML += "<tr><th>Date</th><th>Time</th><th>Event Summary</th></tr>";
				
				         if (events.length > 0)
				         {
				           for (i = 0; i < events.length; i++)
				           {
				             var event = events[i];
				             var when = event.start.dateTime;
				             if (!when)
				            	 when = event.start.date;
				             var newRow = "<tr><td>" + formatDate(when) + "</td><td>" + formatTime(when) 
				             	+ "</td><td>" + event.summary + "</td></tr>";
				             tableString.innerHTML += newRow;
				           }
				         }
				       });
				     }
				   </script>
				   <script async defer src="https://apis.google.com/js/api.js"
				     onload="this.onload=function(){};handleClientLoad()"
				     onreadystatechange="if (this.readyState === 'complete') this.onload()">
				   </script>
				</div>
				<!-- user info -->
				<div class="col">
					<figure>
						<img src="<%= request.getSession().getAttribute("ImageURL") %>" id="user_image"/>
						<figcaption id="user_name"><%= request.getSession().getAttribute("UserName") %></figcaption>
					</figure>
				</div>
			</div>
		</div>
		<!-- bottom black bar -->
		<div class="footer"></div>
	</body>
</html>