package CoreGameTopTrumps;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/*
 * This class is responsible for connecting to the 'Top Trumps Game' database,
 * Retrieving statistics on the previous games played,
 * Creating a report based on the statistics,
 * Writing the report to a text file in the directory
 */
public class PreviousStats {
private DBConnect DBCon;
private Connection connectionToDatabase;

	private String numOfGames;
	private String numOfCPUWins;
	private String numOfHumanWins;
	private String aveOfGamesDrawn;
	private String highNumOfRounds;
		
	//This method establishes a connection to the 'Top Trumps Game' database,
	//It creates a StringBuilder report with a a title, a first column naming the statistics rows,
	//and a second columns showing the statistic values. 
	//The connection to the database is then closed
	public void fetchPreviousGameData() {
		DBCon = new DBConnect();
		connectionToDatabase = DBConnect.connectToTopTrumpsGameDataBase();
		if (connectionToDatabase != null) {
			setNumOfGames(executeTopTrumpsGameDataBaseQuery("SELECT COUNT(nos_rounds) FROM \"DBTrump\".game", "count"));
			setNumOfCPUWins(executeTopTrumpsGameDataBaseQuery("SELECT COUNT(nos_rounds) FROM \"DBTrump\".game WHERE winner = 'CPU'", "count"));
			setNumOfHumanWins(executeTopTrumpsGameDataBaseQuery("SELECT COUNT(nos_rounds) FROM \"DBTrump\".game WHERE winner = 'PLAYER'", "count"));
			setAveOfGamesDrawn(executeTopTrumpsGameDataBaseQuery("SELECT AVG(nos_draws) FROM \"DBTrump\".game", "avg").substring(0, 3));
			setHighNumOfRounds(executeTopTrumpsGameDataBaseQuery("SELECT MAX(nos_rounds) FROM \"DBTrump\".game", "max"));	
	
			DBCon.closeConnectionToTopTrumpsGameDataBase();
		}
	} 
	
	
	// Mock method for using the class while database connection not possible
	public void mockDBConnection() {
		setNumOfGames("111");
		setNumOfCPUWins("222");
		setNumOfHumanWins("333");
		setAveOfGamesDrawn("444");
		setHighNumOfRounds("555");			
	}
	
	
	//This method tells the database connection to create a new statement
	//Query parameter is the database query to be executed; the columnName query is the name of the column on which the query will be executed
	public String executeTopTrumpsGameDataBaseQuery(String query, String columnName) {
		StringBuilder stringBuilder = new StringBuilder();
		Statement stmt = null;
		try {
			stmt = connectionToDatabase.createStatement();
    		ResultSet rs = stmt.executeQuery(query);
    		while (rs.next()) {
    			stringBuilder.append(rs.getString(columnName));
            }
		}
		catch (SQLException | NullPointerException e ) {
			//e.printStackTrace();
		}
		return stringBuilder.toString();
	}
	
	// To String method for displaying to user
	@Override
	public String toString() {
		if (connectionToDatabase == null) {
			return ""; 		
		}
		return "\nStatistics Of Previous Games:\n\nNumber of Previous Games:\t\t"
				+ numOfGames + "\nNumber of CPU Wins:\t\t\t" + numOfCPUWins + "\nNumber of Human Wins:\t\t\t" + numOfHumanWins
				+ "\nAverage Number of Rounds Drawn:\t\t" + aveOfGamesDrawn + "\nHighest Round Played in Single Game:\t" + highNumOfRounds;
	}
	
	// Getters and setters, for mock method and Jackson serialisation
	public String getNumOfGames() {
		return numOfGames;
	}

	public void setNumOfGames(String numOfGames) {
		this.numOfGames = numOfGames;
	}

	public String getNumOfCPUWins() {
		return numOfCPUWins;
	}

	public void setNumOfCPUWins(String numOfCPUWins) {
		this.numOfCPUWins = numOfCPUWins;
	}

	public String getNumOfHumanWins() {
		return numOfHumanWins;
	}

	public void setNumOfHumanWins(String numOfHumanWins) {
		this.numOfHumanWins = numOfHumanWins;
	}

	public String getAveOfGamesDrawn() {
		return aveOfGamesDrawn;
	}

	public void setAveOfGamesDrawn(String aveOfGamesDrawn) {
		this.aveOfGamesDrawn = aveOfGamesDrawn;
	}

	public String getHighNumOfRounds() {
		return highNumOfRounds;
	}

	public void setHighNumOfRounds(String highNumOfRounds) {
		this.highNumOfRounds = highNumOfRounds;
	}
	
	
	
}

