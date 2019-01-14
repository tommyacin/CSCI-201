<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>

<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Search Results</title>
		<meta name="google-signin-scope" content="profile email https://www.googleapis.com/auth/calendar">
		<!-- bootstrap -->
		<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css"/>
		<link rel="stylesheet" type="text/css" href="./style.css"/>
		<meta name="google-signin-client_id" content="770131881416-mcjm57654v12pe5nt7qh6fl4skf668oj.apps.googleusercontent.com">
		<script>
			function search_query()
			{
				var urlParams = new URLSearchParams(window.location.search);
				var search_terms = urlParams.get('search');
				var request_str = "ProfileServlet?field=search&query=" + search_terms;
				var xhttp = new XMLHttpRequest();
				xhttp.open("GET", request_str, false);
				xhttp.send();
	     		
	     		if (xhttp.responseText.trim().length == 0) //No users found
	     		{
	     			document.getElementById("blank_search_result").innerHTML = "No users found";
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
     				var search_display = document.getElementById("search_result_col" + ((i-1)%3));
     				search_display.innerHTML += display_HTML;
     			}
			};
		</script>
		<script src="https://apis.google.com/js/platform.js" async defer onload="this.onload=function(){};search_query()"></script>
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
			<div class="row h-100" id="search_content">
				<h1 id="blank_search_result"></h1>
				<div class="col">
					<p id="search_result_col0"></p>
				</div>
				<div class="col">
					<p id="search_result_col1"></p>
				</div>
				<div class="col">
					<p id="search_result_col2"></p>
				</div>
			</div>
		</div>
		<!-- bottom black bar -->
		<div class="footer"></div>
	</body>
</html>