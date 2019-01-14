// Tomas Acin-Chediex
// 5967613148
// acinched@usc.edu

package acinched_CSCI201L_Homework1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Date 
{
	@SerializedName("Month")
	@Expose
	private String month;
	
	@SerializedName("Day")
	@Expose
	private int day;
	
	@SerializedName("Year")
	@Expose
	private int year;
	
	public Date(String month, int day, int year) //Date constructor
	{
		this.month = month;
		this.day = day;
		this.year = year;
	}
	
	public static int convertMonthToNum(String m) //internal method for converting a month to its numerical equivalent
	{
		if (m.equals("January"))
			return 1;
		else if (m.equals("Feburary"))
			return 2;
		else if (m.equals("March"))
			return 3;
		else if (m.equals("April"))
			return 4;
		else if (m.equals("May"))
			return 5;
		else if (m.equals("June"))
			return 6;
		else if (m.equals("July"))
			return 7;
		else if (m.equals("August"))
			return 8;
		else if (m.equals("September"))
			return 9;
		else if (m.equals("October"))
			return 10;
		else if (m.equals("November"))
			return 11;
		else
			return 12;
	}
	
	public int getMonth() //returns the month
	{
		return convertMonthToNum(month);
	}
	
	public int getDay() //returns the day
	{
		return day;
	}
	
	public int getYear() //returns the year
	{
		return year;
	}
	
	public String toString() //returns the whole date represented as a string
	{
		return month + " " + day + ", " + year;
	}
}
