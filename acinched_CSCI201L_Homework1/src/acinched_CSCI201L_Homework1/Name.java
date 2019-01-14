// Tomas Acin-Chediex
// 5967613148
// acinched@usc.edu

package acinched_CSCI201L_Homework1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Name 
{
	@SerializedName("Fname")
	@Expose
	private String first;
	
	@SerializedName("Lname")
	@Expose
	private String last;
	
	public Name(String first, String last) //Name constructor
	{
		this.first = first;
		this.last = last;
	}
	
	public String getName() //Returns name in format "First Last"
	{
		return first + " " + last;
	}
	
	public String getNameReverse() //returns name in format "Last, First"
	{
		return last + ", " + first;
	}
	
	public String getFirst() //returns first name
	{
		return first;
	}
	
	public String getLast() //returns last name
	{
		return last;
	}
}
