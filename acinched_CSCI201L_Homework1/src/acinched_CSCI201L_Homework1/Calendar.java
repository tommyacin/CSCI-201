// Tomas Acin-Chediex
// 5967613148
// acinched@usc.edu

package acinched_CSCI201L_Homework1;

import java.util.ArrayList;
import java.util.Collections;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Calendar 
{
	@SerializedName("Users")
	@Expose
	private ArrayList<User> users;
	
	public void addUser(String first, String last) //adds user to calendar
	{
		users.add(new User(first, last));
	}
	
	public boolean userExists(String fullName) //checks if given user already exists
	{
		for (int i=0; i<users.size(); i++)
			if (users.get(i).getName().toLowerCase().equals(fullName.toLowerCase()))
				return true; //user exists
		return false; //user does not exist
	}
	
	public String printUsers() //return list of users w/out their events as a string
	{
		String result = "";
		for (int i=0; i<users.size(); i++)
			result += "\t" + (i+1) + ") " + users.get(i).getNameReverse() + "\n";
		return result;
	}
	
	public int numUsers() //returns the number of users in calendar
	{
		return users.size();
	}
	
	public void removeUser(int index) //removes user from calendar
	{
		System.out.println(users.get(index-1).getName() + " was removed from the calendar.");
		users.remove(index-1);
	}
	
	public void addEvent(int index, String title, String time, String month, int day, int year) //adds event to specific user
	{
		users.get(index-1).addEvent(title, time, month, day, year);
	}
	
	public void removeEvent(int userIndex, int eventIndex) //removes specific event from user
	{
		users.get(userIndex-1).removeEvent(eventIndex);
	}
	
	public boolean hasEvents(int index) //checks if a user has any events
	{
		return users.get(index-1).getEvents().size() > 0;
	}
	
	public void listEvents(int index) //prints out list of events for a user
	{
		ArrayList<Event> eventsList = users.get(index-1).getEvents();
		for (int i=0; i<eventsList.size(); i++)
			System.out.println("\t" + (i+1) + ") " + eventsList.get(i).toString());
		System.out.println();
	}
	
	public int numEvents(int index) //returns the number of events for a specific user
	{
		return users.get(index-1).getEvents().size();
	}
	
	public void sortUsers(String sortType) //true = ascending, false = descending
	{
		Collections.sort(users);
		if (sortType.equals("2"))
			Collections.reverse(users);
	}
	
	public String toString() //returns formatted list of users with their events
	{
		String result = "";
		for (int i=0; i<users.size(); i++)
		{
			result += "\t" + (i+1) + ") " + users.get(i).getNameReverse() + "\n";
			ArrayList<Event> userEvents = users.get(i).getEvents();
			for (int j=0; j<userEvents.size(); j++)
				result += "\t\t" + (char)('a'+ j) + ") " + userEvents.get(j).toString() + "\n";
		}
		if (result.equals(""))
			result = "Calendar is empty.";
		return result;
	}
}
