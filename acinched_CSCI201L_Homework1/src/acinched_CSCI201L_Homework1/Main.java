// Tomas Acin-Chediex
// 5967613148
// acinched@usc.edu

package acinched_CSCI201L_Homework1;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.io.PrintWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

public class Main 
{
	//function used to print menu each time
	public static void printMenuDisplay()
	{
		System.out.println("\t1) Display User's Calendar");
		System.out.println("\t2) Add User");
		System.out.println("\t3) Remove User");
		System.out.println("\t4) Add Event");
		System.out.println("\t5) Delete Event");
		System.out.println("\t6) Sort Users");
		System.out.println("\t7) Write file");
		System.out.println("\t8) Exit");
		System.out.println();
	}
	
	//internal method used to convert a number into its corresponding month
	public static String convertMonth (int month)
	{
		String[] months = {"January", "Feburary", "March", "April", "May", "June", 
				"July", "August", "September", "October", "November", "December"};
		return months[month-1];
	}
	
	public static void main (String [] args)
	{	
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		Calendar calendar; //wrapper class for gson
		Scanner scan = new Scanner(System.in);
		String inputFile = "";
		while(true) //FIX CATCH BLOCKS
		{
			try
			{	
				System.out.print("What is the name of the input file? ");
				inputFile = scan.nextLine(); //reading in input file
				FileReader fr = new FileReader(inputFile);
				calendar = gson.fromJson(fr, Calendar.class); //using gson to create calendar database
				System.out.println();
				break;
			}
			catch(FileNotFoundException fnfe)
			{
				System.out.println("That file could not be found\n");
			}
			catch (JsonParseException jpe)
			{
				System.out.println("That file is not a well-formed JSON file");
			}
		}
		
		boolean menu = true; //used for staying and exiting from calendar
		boolean changesMade = false; //used to detect if changes have been made to the file
		while(menu)
		{
			printMenuDisplay();	//initial display	
			System.out.print("What would you like to do? ");
			String choice = scan.nextLine();
			System.out.println();
			
			if (choice.equals("1")) //Display all users and their events
			{
				System.out.println(calendar);
			}
			else if (choice.equals("2")) //Add user
			{
				boolean validName = false; //used to ask for name until valid input
				String first = "";
				String last = "";
				while (!validName) //loop until valid name
				{
					System.out.print("What is the user's name? ");
					String fullName = scan.nextLine();
					int countSpaces = fullName.length() - fullName.replace(" ", "").length();
					if(countSpaces < 1) //a full name will have at least one space
						System.out.println("Invalid, must have first and last names\n");
					else
					{
						int space = fullName.indexOf(" ");
						first = fullName.substring(0,space);
						last = fullName.substring(space+1);
						validName = true;
					}
				}
				//check if name already exists in calendar
				if (!calendar.userExists(first + " " + last)) //new user so add to calendar
				{
					calendar.addUser(first, last);
					changesMade = true;
				}
				else
					System.out.println("User already exists\n");
			}
			else if (choice.equals("3")) //Remove user
			{
				boolean validChoice = false;
				if (calendar.numUsers() == 0) //check for empty calendar
				{
					System.out.println("Calendar is empty");
					validChoice = true;
				}
				while (!validChoice) //loop until valid user is chosen
				{
					System.out.println(calendar.printUsers() + "\n"); //display users to choose from
					System.out.print("Which user would you like to remove? ");
					String toRemove = scan.nextLine();
					System.out.println();
					try //user must input a number corresponding to a user
					{
						int toRemoveInt = Integer.parseInt(toRemove);
						if (toRemoveInt>0 && toRemoveInt<=calendar.numUsers()) //valid choice
						{
							calendar.removeUser(toRemoveInt);
							changesMade = true;
							validChoice = true;
						}
						else //choice not in range
							System.out.println("Invalid option\n");
					}
					catch (NumberFormatException nfe)
					{
						System.out.println("Invalid option\n");
					}
				}
			}
			else if (choice.equals("4")) //Add event
			{
				if (calendar.numUsers() == 0) //check for empty calendar
					System.out.println("Calendar is empty");
				else
				{
					boolean validUserChoice = false;
					while (!validUserChoice) //loop until valid user is chosen
					{
						System.out.println(calendar.printUsers()); //display users to choose from
						System.out.print("To which user would you like to add an event? ");
						String chosenUser = scan.nextLine();
						System.out.println();
						try //user must input a number corresponding to a user
						{
							int chosenUserInt = Integer.parseInt(chosenUser);
							if (chosenUserInt>0 && chosenUserInt<=calendar.numUsers()) //valid choice
							{	
								validUserChoice = true;
								//collecting info for event
								System.out.print("What is the title of the event? ");
								String title = scan.nextLine();
								System.out.println();
								
								System.out.print("What time is the event? ");
								String time = scan.nextLine();
								System.out.println();
								
								boolean validMonth = false;
								int month = 0;
								while (!validMonth) //loop until valid month provided
								{
									System.out.print("What month? ");
									month = Integer.parseInt(scan.nextLine());
									if (month<=12 && month>=1)
										validMonth = true;
									else
										System.out.println("Invalid month");
									System.out.println();
								}
								
								boolean validDay = false;
								int day = 0;
								while (!validDay) //loop until valid day provided
								{
									System.out.print("What day? ");
									day = Integer.parseInt(scan.nextLine());
									if (day<=31 && day>=1)
										validDay = true;
									else
										System.out.println("Invalid day");
									System.out.println();
								}
								
								System.out.print("What year? ");
								int year = Integer.parseInt(scan.nextLine());
								System.out.println();
								
								String monthString = convertMonth(month); //convert month from int to string
								calendar.addEvent(chosenUserInt, title, time, monthString, day, year); //add to calendar
								changesMade = true;
							}
							else
								System.out.println("Invalid option\n");
						}
						catch (NumberFormatException nfe)
						{
							System.out.println("Invalid option\n");
						}
					}
				}
			}
			else if (choice.equals("5")) //Delete event
			{
				if (calendar.numUsers() == 0) //checking for empty calendar
					System.out.println("Calendar is empty\n");
				else
				{
					boolean validUserChoice = false;
					while (!validUserChoice) //loop until valid user is chosen
					{
						System.out.println(calendar.printUsers()); //display users to choose from
						System.out.print("From which user would you like to remove an event? ");
						String chosenUser = scan.nextLine();
						System.out.println();
						try //user must input a number corresponding to a number
						{
							int chosenUserInt = Integer.parseInt(chosenUser);
							if (chosenUserInt>0 && chosenUserInt<=calendar.numUsers()) //valid choice
							{
								validUserChoice = true;
								if (!calendar.hasEvents(chosenUserInt)) //user has no events
									System.out.println("Calendar is empty.\n");
								else //user has at least one event
								{
									boolean eventChoice = false;
									while (!eventChoice) //loop until valid event is chosen
									{
										calendar.listEvents(chosenUserInt); //prints out events to choose from
										System.out.print("Which event would you like to remove? ");
										int eventToRemove = Integer.parseInt(scan.nextLine());
										System.out.println();
										if (eventToRemove>0 && eventToRemove<=calendar.numEvents(chosenUserInt)) //valid choice
										{
											calendar.removeEvent(chosenUserInt, eventToRemove); //remove chosen event from chosen user
											changesMade = true;
											eventChoice = true;
										}
										else
											System.out.println("Invalid option");
									}
								}
							}
							else
								System.out.println("Invalid option\n");
						}
						catch (NumberFormatException nfe)
						{
							System.out.println("Invalid option\n");
						}
					}
				}
			}
			else if (choice.equals("6")) //Sort users
			{
				if (calendar.numUsers() == 0)
					System.out.println("Calendar is empty\n");
				else
				{
					boolean validChoice = false;
					while (!validChoice) //loop until valid sort is chosen
					{
						System.out.println("1) Ascending (A-Z)");
						System.out.println("2) Descenidng (Z-A)\n");
						System.out.print("How would you like to sort? ");
						String sortType = scan.nextLine(); //choosing the sort order
						System.out.println();
						if (sortType.equals("1") || sortType.equals("2")) //1=ascending, 2=descending
						{
							calendar.sortUsers(sortType);
							System.out.println(calendar.printUsers());
							validChoice = true;
							changesMade = true;
						}
						else
							System.out.println("Invalid option\n");
					}
				}
			}
			else if (choice.equals("7")) //Write to file
			{
				String json = gson.toJson(calendar); //writing to JSON file
				try
				{
					PrintWriter pw = new PrintWriter(inputFile); //switch to input file
					pw.println(json);
					System.out.println("File has been saved");
					pw.close();
				}
				catch (FileNotFoundException fnfe)
				{
					System.out.println("File could not be saved properly, please try again");
				}
				changesMade = false; //file has been saved
			}
			else if (choice.equals("8")) //Exit
			{
				if(changesMade) //user has option to save
				{
					boolean validChoice = false;
					while (!validChoice) //loop until user chooses valid save option
					{
						System.out.println("Changes have been made since the file was last saved.");
						System.out.println("\t1) Yes");
						System.out.println("\t2) No");
						System.out.print("Would you like to save the file before exiting? ");
						String saveChoice = scan.nextLine();
						System.out.println();
						if (saveChoice.equals("1")) //do save
						{
							try //attempt to save file
							{
								String json = gson.toJson(calendar);
								PrintWriter pw = new PrintWriter(inputFile);
								pw.println(json);
								System.out.println("File has been saved\n");
								pw.close();
							}
							catch (FileNotFoundException fnfe)
							{
								System.out.println("File could not be saved properly");
							}
							validChoice = true;
						}
						else if (saveChoice.equals("2")) //don't save
						{
							System.out.println("File was not saved.\n");
							validChoice = true;
						}
						else //invalid choice
							System.out.println("Invalid option\n");	
					}
				}
				System.out.println("Thank you for using my calendar!");
				menu = false; //exit program
			}
			else
				System.out.println("That is not a valid option");
			System.out.println(); //extra newline for formatting CLI
		}
		scan.close();
	}
}