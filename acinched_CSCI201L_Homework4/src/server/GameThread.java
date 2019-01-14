package server;

import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;

public class GameThread extends Thread
{
	public Vector<ServerThread> players;
	public int size;
	private Connection conn;
	private PreparedStatement ps = null;
	public String secretWord = "";
	public String wordSoFar = "";
	public String game_name = "";
	public int guesses = 7;
	public GameThread (String game_name, int size, Socket s, Connection conn, String secretWord)
	{
		try
		{
			players = new Vector<ServerThread>();
			this.size = size;
			this.game_name = game_name;
			this.secretWord = secretWord;
			for (int i=0; i<secretWord.length(); i++)
				wordSoFar += "_";
			this.conn = conn;
			ps = conn.prepareStatement("insert into Games(gameName, numUsers) values (?, ?);");
			ps.setString(1, game_name);
			ps.setInt(2, size);
			ps.execute();
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	public void run()
	{
		for (ServerThread st : players)
			st.secretWord = this.secretWord;
		int index = size;
		boolean gameWon = false;
		boolean gameLost = false;
		while(!gameWon && !gameLost)
		{
			ServerThread currPlayer = players.get(index%size);
			if (currPlayer.inGame)
			{
				guessBroadcast_part1(currPlayer);
				String guess = currPlayer.guess(wordSoFar, guesses);
				if (guess.length() == 1) //letter
				{
					String message = game_name + " " + currPlayer.name + " - guesses letter " + guess; //server output #15
					serverMessage(message);
					if (secretWord.indexOf(guess) >= 0) //letter exists
					{
						Vector<Integer> pos = new Vector<Integer>(); //vector with positions in which letter is found
						for (int i=0; i<secretWord.length(); i++) 
						{
							if (secretWord.charAt(i) == guess.charAt(0))
							{
								pos.add(i);
								if (i != wordSoFar.length()-1)
									wordSoFar = wordSoFar.substring(0, i) + guess + wordSoFar.substring(i+1);
								else
									wordSoFar = wordSoFar.substring(0, i) + guess;
							}
						}
						String positionOutput = formatPos(pos);
						message = game_name + " " + currPlayer.name + " - " + guess + " is in " + secretWord
								+ " in positions " + positionOutput + ". Secret word now shows " + format(wordSoFar); //server output #16
						serverMessage(message);
						guessBroadcast_part2(currPlayer, guess, true, true);
					}
					else //letter doesn't exist
					{
						guesses--;
						message = game_name + " " + currPlayer.name + " - " + guess + " is not in " + secretWord
								+ ". " + game_name + " now has " + guesses + " guesses remaining."; //server output #17
						serverMessage(message);
						if (guesses == 0) //out of guesses so game over
							gameLost = true;
						guessBroadcast_part2(currPlayer, guess, true, false);
					}
				}
				else //word
				{
					String message = game_name + " " + currPlayer.name + " - guessed word " + guess; //server output #18
					serverMessage(message);
					if (guess.equalsIgnoreCase(secretWord)) //win
					{
						String losers = formatLosers(currPlayer);
						message = game_name + " " + currPlayer.name + " - " + guess + " is correct. " //server output #20
								+ currPlayer.name + " wins game. ";
						if (!losers.equals(""))
							message += losers + "have lost the game.";
						serverMessage(message);
						gameWon = true;
						guessBroadcast_part2(currPlayer, guess, false, true);
					}
					else //lose
					{
						message = game_name + " " + currPlayer.name + " - " + guess + " is incorrect. "
								+ currPlayer.name + " has lost and is no longer in the game."; //server output #19
						serverMessage(message);
						currPlayer.inGame = false;
						guesses--;
						if (guesses == 0) //out of guesses so game over
							gameLost = true;
						if (allEliminated()) //all players are out so game over
							gameLost = true;
						guessBroadcast_part2(currPlayer, guess, false, false);
					}
				}
			}
			if (gameWon) //current players is winner
			{
				for (ServerThread st : players)
				{
					if (st == currPlayer)
						updateStats(currPlayer, true);
					else
						updateStats(st, false);
				}
				for (ServerThread st : players)
				{
					if (st == currPlayer)
						currPlayer.win();
					else
						st.lose(true);
				}
				deleteGame();
			}
			else if (gameLost) //everyone lost
			{
				for (ServerThread st : players)
					updateStats(st, false);
				for (ServerThread st : players)
					st.lose(false);
				deleteGame();
			}
			else
				index++;	
		}
	}
	public void serverMessage(String output)
	{
		Date d = new Date();
		Timestamp ts = new Timestamp(d.getTime());
		System.out.println(ts + " " + output);
	}
	public boolean hasSpace()
	{
		return players.size() < size;
	}
	public String format(String wordSoFar) //format secret word progress for printing
	{
		String format = "";
		for (int i=0; i<wordSoFar.length(); i++)
			format += wordSoFar.charAt(i) + " ";
		return format;
	}
	public String formatPos(Vector<Integer> pos) //format positions of letter for printing
	{
		String res = "";
		for (int i=0; i<pos.size(); i++)
		{
			if (i == pos.size()-1)
				res += pos.get(i);
			else
				res += pos.get(i) + ", ";
		}
		return res;
	}
	public String formatLosers(ServerThread winner)
	{
		String res = "";
		for (int i=0; i<players.size(); i++)
		{
			if (players.get(i) != winner)
			{
				if (i == players.size()-1)
					res += players.get(i).name + " ";
				else
					res += players.get(i).name + ", ";
			}
		}
		return res;
	}
	public void guessBroadcast_part1(ServerThread curr)
	{
		for (ServerThread st : players)
			if (st != curr)
				st.otherGuess_part1(curr, this.wordSoFar, this.guesses);
	}
	public void guessBroadcast_part2(ServerThread curr, String guess, boolean guessType, boolean result)
	{
		for (ServerThread st : players)
			if (st != curr)
				st.otherGuess_part2(curr, guess, guessType, result);
	}
	public boolean allEliminated()
	{
		for (ServerThread st : players)
			if (st.inGame == true)
				return false;
		return true;
	}	
	public void updateStats(ServerThread st, boolean result) //true = win, false = win
	{
		try 
		{
			if (result)
			{
				ps = conn.prepareStatement("update Users set wins=wins+1 where username=?;");
				st.wins++;
			}
			else
			{
				ps = conn.prepareStatement("update Users set losses=losses+1 where username=?;");
				st.losses++;
			}
			ps.setString(1, st.name);
			ps.execute();
		} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void deleteGame()
	{
		try 
		{
			ps = conn.prepareStatement("delete from Games where gameName=?;");
			ps.setString(1, game_name);
			ps.execute();
		} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
