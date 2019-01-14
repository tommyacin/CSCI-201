

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/ProfileServlet")
public class ProfileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		HttpSession session = request.getSession();
		String field = request.getParameter("field");
		if (field.equals("profile"))
		{
			String fname = request.getParameter("fname");
			String lname = request.getParameter("lname");
			String image_url = request.getParameter("image_url");
			String email = request.getParameter("email");
			session.setAttribute("UserName", fname + " " + lname);
			session.setAttribute("ImageURL", image_url);
			session.setAttribute("Email", email);
			
			Connection conn = null;
			PreparedStatement ps = null;
			try
			{
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/CalendarDatabase?user=root&password=root&useSSL=false");
				ps = conn.prepareStatement("INSERT IGNORE INTO Users (email, fname, lname, image_url) values (?, ?, ?, ?);");
				ps.setString(1, email);
				ps.setString(2, fname);
				ps.setString(3, lname);
				ps.setString(4, image_url);
				ps.execute();
				
				//Remove all events for this user to add them back later
				ps = conn.prepareStatement("DELETE FROM userEvents WHERE email=?;");
				ps.setString(1, email);
				ps.execute();
			}
			catch(SQLException sqle)
			{
				System.out.println("sqle: " + sqle.getMessage());
			}
			catch(ClassNotFoundException cnfe)
			{
				System.out.println("cnfe: " + cnfe.getMessage());
			}
			finally
			{
				try
				{
					if (conn != null)
						conn.close();
				}
				catch(SQLException sqle)
				{
					System.out.println("73sqle closing conn: " + sqle.getMessage());
				}
			}
		}
		else if (field.equals("event"))
		{
			String eventTitle = request.getParameter("eventTitle");
			String startDate = request.getParameter("startDate");
			String endDate = request.getParameter("endDate");
			String startTime = request.getParameter("startTime");
			String endTime = request.getParameter("endTime");
			if (startTime.length() > 5)
				startTime = startTime.substring(0,5);
			if (endTime.length() > 5)
				endTime = endTime.substring(0,5);
			
			PrintWriter out = response.getWriter();
			String error_msg = "";
			if (eventTitle == "" || eventTitle.trim().length() == 0)
				error_msg += "Please enter an event title\n";
			
			if (startDate == "" || endDate == "")
				error_msg += "Please enter both dates\n";
			else if (!compareDates(startDate, endDate))
				error_msg += "Invalid range of dates\n";
			
			if (startTime == "" || endTime == "")
				error_msg += "Please enter both times\n";
			else if (compareDates(startDate, endDate)) 
					if (!compareTimes(startDate, endDate, startTime, endTime))
						error_msg += "Invalid range of times\n";
			out.print(error_msg);
			out.flush();
			out.close();
			
			if (error_msg.length() == 0)
			{
				session.setAttribute("EventTitle", eventTitle);
				session.setAttribute("StartDate", startDate);
				session.setAttribute("EndDate", endDate);
				session.setAttribute("StartTime", startTime);
				session.setAttribute("EndTime", endTime);
			}
		}
		else if (field.equals("event_database"))
		{
			String email = (String) session.getAttribute("Email");
			String eventTitle = request.getParameter("eventTitle");
			String startDate = request.getParameter("startDate");
			String endDate = request.getParameter("endDate");
			String startTime = request.getParameter("startTime");
			String endTime = request.getParameter("endTime");
			
			Connection conn = null;
			PreparedStatement ps = null;
			try
			{
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/CalendarDatabase?user=root&password=root&useSSL=false");
				ps = conn.prepareStatement("INSERT IGNORE INTO userEvents (email, eventTitle, startDate, endDate, startTime, endTime) values (?, ?, ?, ?, ?, ?);");
				ps.setString(1, email);
				ps.setString(2, eventTitle);
				ps.setString(3, startDate);
				ps.setString(4, endDate);
				ps.setString(5, startTime);
				ps.setString(6, endTime);
				ps.execute();
			}
			catch(SQLException sqle)
			{
				System.out.println("sqle: " + sqle.getMessage());
			}
			catch(ClassNotFoundException cnfe)
			{
				System.out.println("cnfe: " + cnfe.getMessage());
			}
			finally
			{
				try
				{
					if (conn != null)
						conn.close();
				}
				catch(SQLException sqle)
				{
					System.out.println("154sqle closing conn: " + sqle.getMessage());
				}
			}
		}
		else if (field.equals("search"))
		{
			String query = request.getParameter("query");
			String email = (String) session.getAttribute("Email");
			Connection conn = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			try
			{
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/CalendarDatabase?user=root&password=root&useSSL=false");
				String[] queryArray = query.split(" ", 2);
				if (query.equals(""))
					ps = conn.prepareStatement("SELECT * FROM Users");
				else if (queryArray.length == 1)
					ps = conn.prepareStatement("SELECT * FROM Users WHERE fname LIKE '%" + queryArray[0] + "%' OR lname LIKE '%" + queryArray[0] + "%'");
				else
					ps = conn.prepareStatement("SELECT * FROM Users WHERE fname LIKE '%" + queryArray[0] + "%' OR lname LIKE '%" + queryArray[0] + "%' OR fname LIKE '%" + queryArray[1] + "%' OR lname LIKE '%" + queryArray[1] + "%'");
				rs = ps.executeQuery();
				PrintWriter out = response.getWriter();
				String search_result = "";
				while (rs.next()) 
				{
					String email_search = rs.getString("email");
					if (!email_search.equals(email))
					{
						String fname = rs.getString("fname");
						String lname = rs.getString("lname");
						String image_url = rs.getString("image_url");
						search_result += "#" + email_search + "&" + fname + "&" + lname + "&" + image_url;
					}
				}
				out.print(search_result);
				out.flush();
				out.close();			
			}
			catch(SQLException sqle)
			{
				System.out.println("196sqle: " + sqle.getMessage());
			}
			catch(ClassNotFoundException cnfe)
			{
				System.out.println("cnfe: " + cnfe.getMessage());
			}
			finally
			{
				try
				{
					if (conn != null)
						conn.close();
				}
				catch(SQLException sqle)
				{
					System.out.println("sqle closing conn: " + sqle.getMessage());
				}
			}
		}
		else if (field.equals("friend_page"))
		{
			String email = (String) session.getAttribute("Email");
			String friend_email = request.getParameter("friend_email");
			String friend_fname = request.getParameter("friend_fname");
			Connection conn = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			try
			{
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/CalendarDatabase?user=root&password=root&useSSL=false");
				PrintWriter out = response.getWriter();
				String tableString = "";
				
				ps = conn.prepareStatement("SELECT * FROM followLinks WHERE email_userA=? AND email_userB=?");
				ps.setString(1, email);
				ps.setString(2, friend_email);
				rs = ps.executeQuery();
				if (rs.next() == false)
				{
					tableString += "Follow^<tr>Follow " + friend_fname + " to view upcoming events</tr>";
				}
				else
				{
					tableString += "Unfollow^";
					ps = conn.prepareStatement("SELECT * FROM userEvents WHERE email=? ORDER BY startDate");
					ps.setString(1, friend_email);
					rs = ps.executeQuery();
					while (rs.next()) 
					{
						String eventTitle = rs.getString("eventTitle");
						String startDate = rs.getString("startDate");
						String endDate = rs.getString("endDate");
						String startTime = rs.getString("startTime");
						String endTime = rs.getString("endTime");
						tableString += "<tr onclick=\"copy_event('" + eventTitle + "','" + startDate + "','" + endDate + "','" + startTime + "','" + endTime + "')\"><td>";
						tableString += formatDate(startDate) + "</td><td>" + formatTime(startTime) + "</td><td>" + eventTitle + "</td></tr>";
					}
				}
				out.print(tableString);
				out.flush();
				out.close();
				
				ps = conn.prepareStatement("SELECT * FROM Users WHERE email=?");
				ps.setString(1, friend_email);
				rs = ps.executeQuery();
				rs.next();
				String friend_name = rs.getString("fname") + " " + rs.getString("lname");
				String friend_image_url = rs.getString("image_url");
				session.setAttribute("friendName", friend_name);
				session.setAttribute("friendImage", friend_image_url);
			}
			catch(SQLException sqle)
			{
				System.out.println("267sqle: " + sqle.getMessage());
			}
			catch(ClassNotFoundException cnfe)
			{
				System.out.println("cnfe: " + cnfe.getMessage());
			}
			finally
			{
				try
				{
					if (conn != null)
						conn.close();
				}
				catch(SQLException sqle)
				{
					System.out.println("sqle closing conn: " + sqle.getMessage());
				}
			}
		}
		else if (field.equals("follow_friend"))
		{
			String email = (String) session.getAttribute("Email");
			String friend_email = request.getParameter("friend_email");
			Connection conn = null;
			PreparedStatement ps = null;
			try
			{
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/CalendarDatabase?user=root&password=root&useSSL=false");
				ps = conn.prepareStatement("INSERT IGNORE INTO followLinks (email_userA, email_userB) VALUES (?, ?);");
				ps.setString(1, email);
				ps.setString(2, friend_email);
				ps.execute();
			}
			catch(SQLException sqle)
			{
				System.out.println("304sqle: " + sqle.getMessage());
			}
			catch(ClassNotFoundException cnfe)
			{
				System.out.println("cnfe: " + cnfe.getMessage());
			}
			finally
			{
				try
				{
					if (conn != null)
						conn.close();
				}
				catch(SQLException sqle)
				{
					System.out.println("sqle closing conn: " + sqle.getMessage());
				}
			}
		}
		else if (field.equals("unfollow_friend"))
		{
			String email = (String) session.getAttribute("Email");
			String friend_email = request.getParameter("friend_email");
			Connection conn = null;
			PreparedStatement ps = null;
			try
			{
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/CalendarDatabase?user=root&password=root&useSSL=false");
				ps = conn.prepareStatement("DELETE FROM followLinks WHERE email_userA=? AND email_userB=?;");
				ps.setString(1, email);
				ps.setString(2, friend_email);
				ps.execute();
			}
			catch(SQLException sqle)
			{
				System.out.println("340sqle: " + sqle.getMessage());
			}
			catch(ClassNotFoundException cnfe)
			{
				System.out.println("cnfe: " + cnfe.getMessage());
			}
			finally
			{
				try
				{
					if (conn != null)
						conn.close();
				}
				catch(SQLException sqle)
				{
					System.out.println("sqle closing conn: " + sqle.getMessage());
				}
			}
		}
		else if (field.equals("load_following"))
		{
			String email = (String) session.getAttribute("Email");
			Connection conn = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			try
			{
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/CalendarDatabase?user=root&password=root&useSSL=false");
				ps = conn.prepareStatement("SELECT * FROM followLinks f, Users u WHERE f.email_userA=? AND f.email_userB=u.email");
				ps.setString(1, email);
				rs = ps.executeQuery();
				PrintWriter out = response.getWriter();
				String allFollowing = "";
				while(rs.next())
				{
					String email_follow = rs.getString("email");
					String fname = rs.getString("fname");
					String lname = rs.getString("lname");
					String image_url = rs.getString("image_url");
					allFollowing += "#" + email_follow + "&" + fname + "&" + lname + "&" + image_url;
				}
				out.print(allFollowing);
				out.flush();
				out.close();
			}
			catch(SQLException sqle)
			{
				System.out.println("387sqle: " + sqle.getMessage());
			}
			catch(ClassNotFoundException cnfe)
			{
				System.out.println("cnfe: " + cnfe.getMessage());
			}
			finally
			{
				try
				{
					if (conn != null)
						conn.close();
				}
				catch(SQLException sqle)
				{
					System.out.println("sqle closing conn: " + sqle.getMessage());
				}
			}
		}
	}
	
	protected boolean compareDates(String sDate, String eDate) //true if valid
	{
		int sYear = Integer.valueOf(sDate.substring(0, 4)).intValue();
		int eYear = Integer.valueOf(eDate.substring(0, 4)).intValue();
		int sMonth = Integer.valueOf(sDate.substring(5, 7)).intValue();
		int eMonth = Integer.valueOf(eDate.substring(5, 7)).intValue();
		int sDay = Integer.valueOf(sDate.substring(8)).intValue();
		int eDay = Integer.valueOf(eDate.substring(8)).intValue();
		
		if (eYear < sYear)
			return false;
		else if (eYear == sYear)
		{
			if (eMonth < sMonth)
				return false;
			else if (eMonth == sMonth)
			{
				if (eDay < sDay)
					return false;
			}
		}
		return true;
	}
	
	protected boolean compareTimes(String sDate, String eDate, String sTime, String eTime)
	{
		int sHour = Integer.valueOf(sTime.substring(0, 2)).intValue();
		int eHour = Integer.valueOf(eTime.substring(0, 2)).intValue();
		int sMin = Integer.valueOf(sTime.substring(3, 5)).intValue();
		int eMin = Integer.valueOf(eTime.substring(3, 5)).intValue();
		
		if (!sDate.equals(eDate)) //not the same day
			return true;
		if (eHour < sHour)
			return false;
		else if (eHour == sHour)
		{
			if (eMin < sMin)
				return false;
		}
		return true;
	}

	protected String formatDate(String when)
	{
		String year = when.substring(0,4);
		String month = convertMonth(Integer.parseInt(when.substring(5,7)));
		String day = when.substring(8,10);
		return month + " " + day + ", " + year;
	}
	
	protected String formatTime(String when)
	{
		int timeIndex = when.indexOf('T');
		int hour = Integer.parseInt(when.substring(timeIndex+1, timeIndex+3));
		String minute = when.substring(timeIndex+4, timeIndex+6);
		if (hour >= 12)
		{
			if (hour != 12)
				hour -= 12;
			return hour + ":" + minute + " PM";
		}
		else
			return hour + ":" + minute + " AM";	
	}
	
	protected String convertMonth(int month)
	{
		String[] months = {"January", "February", "March", "April", "May", "June", "July",
		      		"August", "September", "October", "November", "December"};
		return months[month-1];
	}
}