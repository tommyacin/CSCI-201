

import java.io.IOException;
import java.io.PrintWriter;

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
			String full_name = request.getParameter("name");
			String image_url = request.getParameter("image_url");
			session.setAttribute("UserName", full_name);
			session.setAttribute("ImageURL", image_url);
		}
		else if (field.equals("event"))
		{
			String eventTitle = request.getParameter("eventTitle");
			String startDate = request.getParameter("startDate");
			String endDate = request.getParameter("endDate");
			String startTime = request.getParameter("startTime");
			String endTime = request.getParameter("endTime");
			
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
		int sMin = Integer.valueOf(sTime.substring(3)).intValue();
		int eMin = Integer.valueOf(eTime.substring(3)).intValue();
		
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

}
