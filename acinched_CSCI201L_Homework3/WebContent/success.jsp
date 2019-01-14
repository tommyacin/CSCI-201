<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Success</title>
		<meta name="google-signin-scope" content="profile email https://www.googleapis.com/auth/calendar">
		<!-- bootstrap -->
		<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css"/>
		<link rel="stylesheet" type="text/css" href="./style.css"/>
		<meta name="google-signin-client_id" content="770131881416-mcjm57654v12pe5nt7qh6fl4skf668oj.apps.googleusercontent.com">
		<script src="https://apis.google.com/js/platform.js" async defer></script>
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
		<div class="main">
			<div class="row h-100 align-items-center justify-content-center" id="success_content">
				<!-- home content  -->
				<div class="col">
					<script>
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
					
				    <% String start = request.getSession().getAttribute("StartDate") + "T" + request.getSession().getAttribute("StartTime") + ":00-07:00"; %>
					<% String end = request.getSession().getAttribute("EndDate") + "T" + request.getSession().getAttribute("EndTime") + ":00-07:00"; %>
					var startDT = "<%= start %>";
					var endDT = "<%= end %>";
					var event = {
						'summary': "<%= request.getSession().getAttribute("EventTitle") %>",
						'start': {
							'dateTime': startDT,
							'timeZone': 'America/Los_Angeles'
						},
						'end': {
							'dateTime': endDT,
							'timeZone': 'America/Los_Angeles'
						}
					};
				    
				    /* Initializes the API client library and sets up sign-in state listeners. */
				    function initClient() {
				         gapi.client.init({
				         apiKey: "",
				         clientId: CLIENT_ID,
				         discoveryDocs: DISCOVERY_DOCS,
				         scope: SCOPES
				       }).then(function () {
				    		insertEvent();
				        });
				    }
				    
				    function insertEvent()
				    {
				    	var request = gapi.client.calendar.events.insert({
			        		 'calendarId': 'primary',
			        		 'resource': event
		    			});
				    	
				    	request.execute(function(event) {
							document.getElementById("success_msg").innerHTML += "Event successfully created<br />";
							document.getElementById("success_msg").innerHTML += "Event link: " + event.htmlLink + "<br />";
							document.getElementById("success_msg").innerHTML += "Continue navigating using the tabs at the top<br />";
						});
				    	
				    	var eventTitle = event.summary;
			            var startDate = event.start.dateTime.substring(0,10);
			            var startTime = event.start.dateTime.substring(11,19);
			            var endDate = event.end.dateTime.substring(0,10);
			            var endTime = event.end.dateTime.substring(11,19);
			            var xhttp = new XMLHttpRequest();
				   	    xhttp.open("POST", "ProfileServlet");
		     	  		xhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	  		     	    xhttp.send("field=event_database&eventTitle=" + eventTitle + "&startDate=" + startDate + 
				     			  "&endDate=" + endDate + "&startTime=" + startTime + "&endTime=" + endTime);
				    };
					</script>
					 <script async defer src="https://apis.google.com/js/api.js"
					     onload="this.onload=function(){};handleClientLoad()"
					     onreadystatechange="if (this.readyState === 'complete') this.onload()">
				   </script>
				   <p id="success_msg"></p>
				</div>
			</div>
		</div>
		<!-- bottom black bar -->
		<div class="footer"></div>
	</body>
</html>