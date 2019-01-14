package client;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;

public class HangmanClient extends Thread
{
	private BufferedReader br;
	private PrintWriter pw;
	private static Scanner scan = new Scanner(System.in);
	private String wordfile = "";
	public HangmanClient(Properties prop)
	{
		String hostname = prop.getProperty("ServerHostname");
		int port = Integer.parseInt(prop.getProperty("ServerPort"));
		wordfile = prop.getProperty("SecretWordFile");
		Socket s = null;
		try
		{
			System.out.print("Trying to connect to server...");
			s = new Socket(hostname, port);
			System.out.println("Connected!\n");
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			pw = new PrintWriter(s.getOutputStream());
			this.start();
		}
		catch(IOException ioe)
		{
			System.out.println("Unable to connect to server " + hostname + " on port " + port);
		}
		finally
		{
			//close socket
		}
	}
	public void run()
	{
		try 
		{
			String login_response = "No";
			String username = "";
			String pass = "";
			while (true)
			{
				if (login_response.equalsIgnoreCase("No")) //try with new credentials
				{
					System.out.print("Username: ");
					username = scan.nextLine();
					System.out.print("Password: ");
					pass = scan.nextLine();
					System.out.println();
					pw.println(username);
					pw.println(pass);
					pw.flush();
					login_response = br.readLine();
				}
				else if (login_response.equalsIgnoreCase("no_account"))
				{
					System.out.println("No account exists with those credentials.");
					System.out.print("Would you like to create a new account(yes/no)? ");
					String new_account = scan.nextLine();
					while (!new_account.equalsIgnoreCase("yes") && !new_account.equalsIgnoreCase("no"))
					{
						System.out.println("No account exists with those credentials.");
						System.out.print("Would you like to create a new account(yes/no)? ");
						new_account = scan.nextLine();
					}
					if (new_account.equalsIgnoreCase("Yes")) //create a new account
					{
						//only reached for a unique password
						System.out.print("Would you like to use the username and password above (yes/no)? ");
						String same_credentials = scan.nextLine();
						while (!same_credentials.equalsIgnoreCase("yes") && !same_credentials.equalsIgnoreCase("no"))
						{
							System.out.print("Would you like to use the username and password above (yes/no)? ");
							same_credentials = scan.nextLine();
						}
						if (same_credentials.equalsIgnoreCase("Yes"))
						{
							//tell server thread to create account
							pw.println("create_account");
							pw.println(username);
							pw.println(pass);
							pw.flush();
							login_response = "logged_in";
						}
						else
						{
							login_response = "No";
							pw.println("sign_in");
							pw.flush();
						}
					}
					else
					{
						login_response = "No";
						pw.println("sign_in");
						pw.flush();
					}
				}
				else if (login_response.equals("incorrect_password"))
				{
					System.out.println("Incorrect password. Please try again.");
					login_response = "No";
					pw.println("sign_in");
					pw.flush();
				}
				else if (login_response.equals("logged_in")) //successful login
				{
					int wins = Integer.parseInt(br.readLine());
					int losses = Integer.parseInt(br.readLine());
					System.out.println("Great! You are now logged in as " + username);
					System.out.println("\n" + username + "'s Record");
					System.out.println("-------------------------------");
					System.out.println("Wins - " + wins);
					System.out.println("Losses - " + losses);
					login_response = "game_setup";
				}
				else if (login_response.equals("game_setup"))
				{
					System.out.println("\n\t1) Start a game");
					System.out.println("\t2) Join a game");
					System.out.print("Would you like to start a game or join a game? ");
					String game_choice = scan.nextLine();
					while (!game_choice.equals("1") && !game_choice.equals("2"))
					{
						System.out.println("\n\t1) Start a game");
						System.out.println("\t2) Join a game");
						System.out.print("Would you like to start a game or join a game? ");
						game_choice = scan.nextLine();
					}
					System.out.print("\nWhat is the name of the game? ");
					String game_name = scan.nextLine();
					if (game_choice.equals("1")) //start game
					{
						pw.println("game_start");
						pw.println(game_name);
						pw.flush();
						String attempt = br.readLine();
						if (attempt.equals("num_players"))
						{
							System.out.print("How many users will be playing (1-4)? ");
							String num_players = scan.nextLine();
							while (!num_players.equals("1") && !num_players.equals("2") && !num_players.equals("3") && !num_players.equals("4"))
							{
								System.out.println("\nA game can only have between 1-4 players");
								System.out.print("How many users will be playing (1-4)? ");
								num_players = scan.nextLine();
							}
							pw.println(num_players);
							pw.println(wordfile);
							pw.flush();
							login_response = "waiting_to_start";
						}
						else if (attempt.equals("already_exists"))
						{
							System.out.println(game_name + " already exists.");
						}
					}
					else if (game_choice.equals("2")) //join game
					{
						pw.println("game_join");
						pw.println(game_name);
						pw.flush();
						String attempt = br.readLine();
						if (attempt.equals("not_exists"))
						{
							System.out.println("There is no game with name " + game_name);
						}
						else if (attempt.equals("no_space"))
						{
							System.out.println("The game " + game_name + " does not have space for another user to join.");
						}
						else if (attempt.equals("joined"))
						{
							login_response = "waiting_to_start";
						}
					}
				}
				else if (login_response.equals("waiting_to_start"))
				{
					String game_start = br.readLine();
					if (game_start.equals("ready_to_play"))
					{
						System.out.println("\nAll users have joined.");
						login_response = "game_begin";
					}
					else if (game_start.equals("waiting"))
					{
						int num_left = Integer.parseInt(br.readLine());
						System.out.println("\nWaiting for " + num_left + " other users to join.");
					}
					else if (game_start.equals("player_joined"))
					{
						String user = br.readLine();
						int user_wins = Integer.parseInt(br.readLine());
						int user_losses = Integer.parseInt(br.readLine());
						int num_left = Integer.parseInt(br.readLine());
						
						System.out.println("\nUser " + user + " is in the game.\n");
						System.out.println(user + "'s Record");
						System.out.println("-------------------------------");
						System.out.println("Wins - " + user_wins);
						System.out.println("Losses - " + user_losses);
						
						if (num_left > 0)
							System.out.println("\nWaiting for " + num_left + " other users to join.");
					}
					else if (game_start.equals("user_record"))
					{
						String user = br.readLine();
						int user_wins = Integer.parseInt(br.readLine());
						int user_losses = Integer.parseInt(br.readLine());
						
						System.out.println("\nUser " + user + " is in the game.\n");
						System.out.println(user + "'s Record");
						System.out.println("-------------------------------");
						System.out.println("Wins - " + user_wins);
						System.out.println("Losses - " + user_losses);
					}
				}
				else if (login_response.equals("game_begin"))
				{
					System.out.println("\nDetermining secret word... ");
					System.out.println();
					login_response = "in_game";
				}
				else if (login_response.equals("in_game"))
				{
					String guess = br.readLine();
					if (guess.equals("guess"))
					{
						String wordSoFar = format(br.readLine());
						int guesses = Integer.parseInt(br.readLine());
						System.out.println("Secret Word: " + wordSoFar.toUpperCase());
						System.out.println("You have " + guesses + " incorrect guesses remaining.");
						System.out.println("\t1) Guess a letter");
						System.out.println("\t2) Guess the word");
						System.out.print("What would you like to do? ");
						String guessChoice = scan.nextLine();
						while (!guessChoice.equals("1") && !guessChoice.equals("2"))
						{
							System.out.println("\nYou have " + guesses + " incorrect guesses remaining.");
							System.out.println("\t1) Guess a letter");
							System.out.println("\t2) Guess the word");
							System.out.print("What would you like to do? ");
							guessChoice = scan.nextLine();
						}
						if (guessChoice.equals("1"))
						{
							System.out.print("Letter to guess: ");
							String letter = scan.nextLine();
							while (letter.length() != 1 || !isAlpha(letter))
							{
								System.out.print("\nPlease input a single letter: ");
								letter = scan.nextLine();
							}
							pw.println(letter);
							pw.println("letter");
							pw.flush();
							int inWord = Integer.parseInt(br.readLine());
							if (inWord >= 0)
								System.out.println("The letter " + letter + " is in the secret word.");
							else
								System.out.println("The letter " + letter + " is not in the secret word.");
						}
						else if (guessChoice.equals("2"))
						{
							System.out.print("What is the secret word? ");
							String word = scan.nextLine();
							pw.println(word);
							pw.println("word");
							pw.flush();
							String result = br.readLine();
							if (result.equals("loss"))
								System.out.println("\nThat is incorrect. You are now a spectator.\n");
						}
						System.out.println();
					}
					else if (guess.equals("other_guess_part1"))
					{
						String user = br.readLine();
						String wordSoFar = format(br.readLine());
						int guesses = Integer.parseInt(br.readLine());
						System.out.println("Secret Word: " + wordSoFar.toUpperCase());
						System.out.println("You have " + guesses + " incorrect guesses remaining.");
						System.out.println("Waiting for " + user + " to do something...");
					}
					else if (guess.equals("other_guess_part2"))
					{
						String user = br.readLine();
						String user_guess = br.readLine();
						String guess_type = br.readLine();
						String result = br.readLine();
						if (guess_type.equals("letter"))
						{
							System.out.println(user + " has guessed the letter " + user_guess);
							if (result.equals("success"))
								System.out.println("The letter " + user_guess + " is in the secret word.");
							else if (result.equals("failure"))
								System.out.println("The letter " + user_guess + " is not in the secret word.");
						}
						else if (guess_type.equals("word"))
						{
							System.out.println(user + " has guessed the word " + user_guess);
							if (result.equals("success"))
								System.out.println(user + " guessed the word correctly. You lose!");
							else
								System.out.println(user + " guessed the word incorrectly. He loses!");
						}
						System.out.println();
					}
					else if (guess.equals("win"))
					{
						System.out.println("That is correct! You win!");
						int wins = Integer.parseInt(br.readLine());
						int losses = Integer.parseInt(br.readLine());
						System.out.println("\n" + username + "'s Record");
						System.out.println("-------------------------------");
						System.out.println("Wins - " + wins);
						System.out.println("Losses - " + losses);
					}
					else if (guess.equals("lose"))
					{
						String winner = br.readLine();
						//if (winner.equals("other_winner"))
							//System.out.println("Someone else won. You lose!");
						if (winner.equals("no_winner"))
						{
							System.out.println("Nobody guessed the word. Everyone loses!");
							String secretWord = br.readLine();
							System.out.println("The secret word was " + secretWord);
						}
						int wins = Integer.parseInt(br.readLine());
						int losses = Integer.parseInt(br.readLine());
						System.out.println("\n" + username + "'s Record");
						System.out.println("-------------------------------");
						System.out.println("Wins - " + wins);
						System.out.println("Losses - " + losses);
					}
					else if (guess.equals("user_record"))
					{
						String user = br.readLine();
						int user_wins = Integer.parseInt(br.readLine());
						int user_losses = Integer.parseInt(br.readLine());
						
						System.out.println("\n" + user + "'s Record");
						System.out.println("-------------------------------");
						System.out.println("Wins - " + user_wins);
						System.out.println("Losses - " + user_losses);
					}
					else if (guess.equals("exit"))
					{
						System.out.println("\nThank you for playing Hangman!");
						break;
					}
				}
			}
		}
		catch (IOException ioe)
		{
			
		}
	}
	public String format(String wordSoFar) //format secret word progress for printing
	{
		String format = "";
		for (int i=0; i<wordSoFar.length(); i++)
			format += wordSoFar.charAt(i) + " ";
		return format;
	}
	public boolean isAlpha(String letter)
	{
		char let = letter.toLowerCase().charAt(0);
		return (let>='a' && let<='z');
	}
	public static void main(String [] args)
	{
		Properties prop = getConfig();
		String error = checkParameters(prop);
		if (error.length() > 0)
		{
			System.out.println(error);
			return;
		}
		printConfig(prop);
		new HangmanClient(prop);
	}
	public static Properties getConfig()
	{
		Properties prop = new Properties();
		String config = "";
		//Scanner scan = new Scanner(System.in);
		InputStream is = null;
		while (true)
		{
			System.out.print("What is the name of the configuration file? ");
			config = scan.nextLine();
			try 
			{
				is = new FileInputStream(config);
				prop.load(is);
				is.close();
				break;
			}
			catch(FileNotFoundException fnfe)
			{
				System.out.println("Configuration file " + config + " could not be found.");
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return prop;
	}
	public static String checkParameters(Properties prop)
	{
		String error = "";
		if (prop.getProperty("ServerHostname") == null)
			error = "ServerHostname is a required parameter in the configuration file.";
		else if (prop.getProperty("ServerPort") == null)
			error = "ServerPort is a required parameter in the configuration file.";
		else if (prop.getProperty("DBConnection") == null)
			error = "DBConnection is a required parameter in the configuration file.";
		else if (prop.getProperty("DBUsername") == null)
			error = "DBUsername is a required parameter in the configuration file.";
		else if (prop.getProperty("DBPassword") == null)
			error = "DBPassword is a required parameter in the configuration file.";
		else if (prop.getProperty("SecretWordFile") == null)
			error = "SecretWordFile is a required parameter in the configuration file.";
		return error;
	}
	public static void printConfig(Properties prop)
	{
		System.out.println("Server Hostname - " + prop.getProperty("ServerHostname"));
		System.out.println("Server Port - " + prop.getProperty("ServerPort"));
		System.out.println("Database Connection String - " + prop.getProperty("DBConnection"));
		System.out.println("Database Username - " + prop.getProperty("DBUsername"));
		System.out.println("Database Password - " + prop.getProperty("DBPassword"));
		System.out.println("Secret Word File - " + prop.getProperty("SecretWordFile"));
		System.out.println();
	}
}
