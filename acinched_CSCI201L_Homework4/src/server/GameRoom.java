package server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;
import java.util.Vector;

public class GameRoom 
{
	private Vector<ServerThread> serverThreads;
	private Vector<GameThread> gameThreads;
	public GameRoom(Properties prop)
	{
		ServerSocket ss = null;
		Connection conn = null;
		int port = Integer.parseInt(prop.getProperty("ServerPort"));
		String DBconn = prop.getProperty("DBConnection");
		String DBuser = prop.getProperty("DBUsername");
		String DBpass = prop.getProperty("DBPassword");
		String DBinfo = DBconn + "?user=" + DBuser + "&password=" + DBpass + "&useSSL=false";
		try
		{
			ss = new ServerSocket(port);
			serverThreads = new Vector<ServerThread>();
			gameThreads = new Vector<GameThread>();
			System.out.print("Trying to connect to database...");
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DBinfo);
			System.out.println("Connected!\n");
			while (true)
			{
				Socket s = ss.accept();
				ServerThread st = new ServerThread(s, conn, this);
				serverThreads.add(st);
			}
		}
		catch(IOException ioe)
		{
			System.out.println("Unable to bind to port " + port);
		}
		catch(SQLException sqle)
		{
			System.out.println("sqle: " + sqle.getMessage());
		}
		catch(ClassNotFoundException cnfe)
		{
			System.out.println("Unable to connect to database " + DBconn + " with username " + DBuser
					+ " and password " + DBpass);
		}
		finally
		{
			//close socket and db connection
		}
	}
	
	public boolean hasSpace(String game_name)
	{
		for (GameThread gt : gameThreads) 
			if (gt.game_name.equals(game_name))
				return gt.players.size() < gt.size;
		return false;
	}
	
	public void createGame(GameThread gt)
	{
		gameThreads.add(gt);
	}
	public int addPlayer(ServerThread st, String game_name) //returns number of people waiting on
	{
		for (GameThread gt : gameThreads) 
		{
			if (gt.game_name.equals(game_name))
			{
				gt.players.add(st);
				waitingBroadcast(gt, st);
				if (gt.players.size() == gt.size)
				{
					String message = st.name + " - " + game_name + " has " + gt.size + " players so starting game. "
							+ "Secret word is " + gt.secretWord; //server output #14
					serverMessage(message);
					return 0;
				}
				return gt.size - gt.players.size();
			}
		}
		return -1;
	}
	public void serverMessage(String output)
	{
		Date d = new Date();
		Timestamp ts = new Timestamp(d.getTime());
		System.out.println(ts + " " + output);
	}
	public void startGame(String game_name)
	{
		for (GameThread gt : gameThreads) 
			if (gt.game_name.equals(game_name))
					gt.start();
	}
	public void waitingBroadcast(GameThread gt, ServerThread currST) //notify other users that this players has joined
	{
		for (ServerThread st : gt.players) 
			if (st != currST)
				st.sendJoinMessage(gt.size - gt.players.size(), currST);
	}
	public void getAllRecords(String game_name, ServerThread currST) //get records for players already in game
	{
		for (GameThread gt : gameThreads)
			if (game_name.equals(gt.game_name))
				for (ServerThread st : gt.players) 
					if (st != currST)
						currST.sendRecord(st);
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
		new GameRoom(prop);
	}
	public static Properties getConfig()
	{
		Properties prop = new Properties();
		String config = "";
		Scanner scan = new Scanner(System.in);
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
		scan.close();
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