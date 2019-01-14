// Tomas Acin-Chediex
// 5967613148
// acinched@usc.edu

package acinched_CSCI201L_Homework1;

import java.util.ArrayList;
import java.util.Collections;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User implements Comparable<User>
{
	@SerializedName("Name")
	@Expose
	private Name fullName;
	
	@SerializedName("Events")
	@Expose
	private ArrayList<Event> events;
	
	public User(String first, String last) //User constructor
	{
		fullName = new Name(first, last);
		events = new ArrayList<Event>();
	}
	
	public String getName() //returns name in format "First Last"
	{
		return fullName.getName();
	}
	
	public String getNameReverse() //returns name in format "Last, First"
	{
		return fullName.getNameReverse();
	}
	
	public void addEvent(String title, String time, String month, int day, int year) //adds event to user's calendar
	{
		events.add(new Event(title, time, month, day, year));
		System.out.println("Added: " + title + ", " + time + ", " + month + " " + day + ", " + year +
				" to " + getName() + "'s calendar.\n");
	}
	
	public void removeEvent(int eventIndex) //removes specific event from user's calendar
	{
		System.out.println(events.get(eventIndex-1).toString() + " has been removed from " 
				+ getName() + "'s calendar");
		events.remove(eventIndex-1);
	}
	
	public ArrayList<Event> getEvents() //returns list of user's events
	{
		Collections.sort(events);
		return events;
	}
	
	@Override
	public int compareTo(User u) //overridden compare function used for sorting users
	{
		if (this.fullName.getLast().compareTo(u.fullName.getLast()) < 0)
			return -1;
		else if (this.fullName.getLast().compareTo(u.fullName.getLast()) > 0)
			return 1;
		else //same last name
		{
			if (this.fullName.getFirst().compareTo(u.fullName.getFirst()) < 0)
				return -1;
			else if (this.fullName.getFirst().compareTo(u.fullName.getFirst()) > 0)
				return 1;
			return 0; //same name
		}
	}
}