// Tomas Acin-Chediex
// 5967613148
// acinched@usc.edu

package acinched_CSCI201L_Homework1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Event implements Comparable<Event>
{
	@SerializedName("Title")
	@Expose
	private String title;
	
	@SerializedName("Time")
	@Expose
	private String time;
	
	@SerializedName("Date")
	@Expose
	private Date date;
	
	public Event(String title, String time, String month, int day, int year) //Event constructor
	{
		this.title = title;
		this.time = time;
		this.date = new Date(month, day, year);
	}
	
	public Date getDate() //returns the Date object
	{
		return date;
	}
	
	@Override
	public int compareTo(Event e) //overridden compare function used for sorting events
	{
		if (this.date.getYear() < e.getDate().getYear())
			return -1;
		else if (this.date.getYear() > e.getDate().getYear())
			return 1;
		
		if (this.date.getMonth() < e.getDate().getMonth())
			return -1;
		else if (this.date.getMonth() > e.getDate().getMonth())
			return 1;
		
		if (this.date.getDay() < e.getDate().getDay())
			return -1;
		else if (this.date.getDay() > e.getDate().getDay())
			return 1;
		
		return 0; //same date
	}	
	
	public String toString() //returns event represented as a string
	{
		return title + ", " + time + ", " + date.toString();
	}
}
