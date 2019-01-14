package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class ServerThread extends Thread
{
	private BufferedReader br;
	private PrintWriter pw;
	private Connection conn;
	private PreparedStatement ps = null;
	private ResultSet rs = null;
	private Socket s = null;
	private GameRoom gr;
	public String name = ""; //username
	public String game = ""; //name of game they're in
	public String secretWord = ""; //secret word
	public boolean inGame = true; //whether or not still in game
	public int wins = 0;
	public int losses = 0;
	public ServerThread(Socket s, Connection conn, GameRoom gr)
	{
		try
		{
			this.s = s;
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			pw = new PrintWriter(s.getOutputStream());
			this.conn = conn;
			this.gr = gr;
			this.start();
		}
		catch (IOException ioe)
		{
			
		} 
	}
	public void run()
	{
		boolean auth = true;
		try
		{
			String login_request = "sign_in";
			while(true)
			{
				if (login_request.equals("create_account")) //create new account
				{
					String username = br.readLine();
					String pass = br.readLine();
					ps = conn.prepareStatement("insert into Users(username, pass, wins, losses) values (?, ?, ?, ?);");
					ps.setString(1, username);
					ps.setString(2, pass);
					ps.setInt(3, 0);
					ps.setInt(4, 0);
					ps.execute();
					pw.println(0);
					pw.println(0);
					pw.flush();
					name = username;
					String message = name + " - created an account with password " + pass; //server output #5
					serverMessage(message);
					message = name + " - has record 0 wins and 0 losses."; //server output #6b
					serverMessage(message);
				}
				else if (login_request.equals("sign_in")) //try to sign in
				{
					String username = br.readLine();
					String pass = br.readLine();
					String message = username +  " - trying to log in with password " + pass; //server output #1
					serverMessage(message);
					ps = conn.prepareStatement("select * from Users where username=?");
					ps.setString(1, username);
					rs = ps.executeQuery();
					if (rs.next() == false) //no account with this username
					{
						pw.println("no_account");
						message = username + " - does not have an account so not successfully logged in."; //server output #3
						serverMessage(message);
					}
					else //user exists, now check password
					{
						ps = conn.prepareStatement("select * from Users where username=? and pass=?");
						ps.setString(1, username);
						ps.setString(2, pass);
						rs = ps.executeQuery();
						if (rs.next() == false) //incorrect password
						{
							pw.println("incorrect_password");
							message = username + " - has an account but not successfully logged in."; //server output #4
							serverMessage(message);
						}
						else
						{
							pw.println("logged_in");
							wins = rs.getInt("wins");
							losses = rs.getInt("losses");
							pw.println(wins);
							pw.println(losses);
							name = username;
							message = name + " - successfully logged in."; //server output #2
							serverMessage(message);
							message = name + " - has record " + wins + " wins and " + losses + " losses."; //server output #6a
							serverMessage(message);
						}
					}
					pw.flush();
				}
				else if (login_request.equals("game_start"))
				{
					String game_name = br.readLine();
					String message = name + " - wants to start a game called " + game_name; //server output #7
					serverMessage(message);
					ps = conn.prepareStatement("select * from Games where gameName=?");
					ps.setString(1,  game_name);
					rs = ps.executeQuery();
					if (rs.next() == false) //name not taken, game can be created
					{
						game = game_name;
						pw.println("num_players");
						pw.flush();
						int num_players = Integer.parseInt(br.readLine());
						String wordfile = br.readLine();
						String secretWord = chooseWord(wordfile);
						message = name + " - successfully started game " + game; //server output #10
						serverMessage(message);
						message = name + " - " + game + " needs " + num_players + " to start game."; //server output #13
						serverMessage(message);
						GameThread gt = new GameThread(game_name, num_players, s, conn, secretWord);
						gr.createGame(gt);
						int num_left = gr.addPlayer(this, game_name);
						if (num_left == 0)
						{
							pw.println("ready_to_play");
							gr.startGame(game_name);
						}
						else
						{
							pw.println("waiting");
							pw.println(num_left);
						}
						auth = false;
						login_request = "done";
					}
					else //name already taken
					{
						pw.println("already_exists");
						message = name + " - " + game_name + " already exists, so unable to start " + game_name; //server output #9
						serverMessage(message);
					}
					pw.flush();
				}
				else if (login_request.equals("game_join"))
				{
					String game_name = br.readLine();
					String message = name + " - wants to join a game called " + game_name; //server output #8
					serverMessage(message);
					ps = conn.prepareStatement("select * from Games where gameName=?");
					ps.setString(1, game_name);
					rs = ps.executeQuery();
					if (rs.next() == false) //game does not exist
					{
						pw.println("not_exists");
					}
					else //game exists; check if there is space to join
					{
						if (gr.hasSpace(game_name)) //there is space
						{
							message = name + " - succesfully joined game " + game; //server output #11
							serverMessage(message);
							int num_left = gr.addPlayer(this, game_name);
							pw.println("joined");
							game = game_name;
							gr.getAllRecords(game_name, this);
							if (num_left == 0)
							{
								pw.println("ready_to_play");
								gr.startGame(game_name);
							}
							else
							{
								pw.println("waiting");
								pw.println(num_left);
							}
							auth = false;
							login_request = "done";
						}
						else //there is not space
						{
							pw.println("no_space");
							message = name + " - " + game_name + " exists, but " + name + " unable to join because"
									+ " maximum number of players have already joined " + game_name; //server output #12
							serverMessage(message);
						}
					}
					pw.flush();
				}
				if (auth)
					login_request = br.readLine();
			}
		}
		catch (IOException ioe)
		{
			
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void sendJoinMessage(int numLeft, ServerThread player) //send client message of someone joining game
	{
		pw.println("player_joined");
		pw.println(player.name);
		pw.println(player.wins);
		pw.println(player.losses);
		pw.println(numLeft);
		if (numLeft == 0)
			pw.println("ready_to_play");
		pw.flush();
	}
	public void sendRecord(ServerThread user) //send client records of users already in game
	{
		pw.println("user_record");
		pw.println(user.name);
		pw.println(user.wins);
		pw.println(user.losses);
		pw.flush();
	}
	public void serverMessage(String output)
	{
		Date d = new Date();
		Timestamp ts = new Timestamp(d.getTime());
		System.out.println(ts + " " + output);
	}
	public String guess(String wordSoFar, int guesses)
	{
		try
		{
			pw.println("guess");
			pw.println(wordSoFar);
			pw.println(guesses);
			pw.flush();
			String guess = br.readLine();
			String guessType = br.readLine();
			if (guessType.equals("letter"))
			{
				pw.println(secretWord.indexOf(guess));
				pw.flush();
			}
			else if (guessType.equals("word"))
			{
				if (!guess.equalsIgnoreCase(secretWord))
					pw.println("loss");
				else
					pw.println("win");
				pw.flush();
			}
			return guess;
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	public void otherGuess_part1(ServerThread user, String wordSoFar, int guesses)
	{
		pw.println("other_guess_part1");
		pw.println(user.name);
		pw.println(wordSoFar);
		pw.println(guesses);
		pw.flush();
	}
	public void otherGuess_part2(ServerThread user, String guess, boolean guessType, boolean result)
	{
		//guessType -> true = letter, false = word
		//result -> true = success, false = failure
		pw.println("other_guess_part2");
		pw.println(user.name);
		pw.println(guess);
		if (guessType)
			pw.println("letter");
		else
			pw.println("word");
		if (result)
			pw.println("success");
		else
			pw.println("failure");
		pw.flush();
	}
	public void win()
	{
		try 
		{
			ps = conn.prepareStatement("select * from Users where username=?");
			ps.setString(1, name);
			rs = ps.executeQuery();
			rs.next();
			int wins = rs.getInt("wins");
			int losses = rs.getInt("losses");
			pw.println("win");
			pw.println(wins);
			pw.println(losses);
			gr.getAllRecords(game, this);
			pw.println("exit");
			pw.flush();
		} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void lose(boolean winner) //true = someone won, false = everybody lost
	{
		try
		{
			ps = conn.prepareStatement("select * from Users where username=?");
			ps.setString(1, name);
			rs = ps.executeQuery();
			rs.next();
			int wins = rs.getInt("wins");
			int losses = rs.getInt("losses");
			pw.println("lose");
			if (winner)
				pw.println("other_winner");
			else
			{
				pw.println("no_winner");
				pw.println(secretWord);
			}
			pw.println(wins);
			pw.println(losses);
			gr.getAllRecords(game, this);
			pw.println("exit");
			pw.flush();
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String chooseWord(String wordfile)
	{
		ArrayList<String> words = new ArrayList<String>();
		try 
		{
			Scanner scan = new Scanner(new File(wordfile));
			while (scan.hasNext()) 
				words.add(scan.nextLine());
			scan.close();
		} 
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Random r = new Random();
		return words.get(r.nextInt(words.size()));
	}
}
