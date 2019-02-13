package online.dwResources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import online.configuration.TopTrumpsJSONConfiguration;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import CoreGameTopTrumps.GameManager;
import CoreGameTopTrumps.Human;
import CoreGameTopTrumps.InputReader;
import CoreGameTopTrumps.TurnStatsHelper;
import CoreGameTopTrumps.User;

@Path("/toptrumps") // Resources specified here should be hosted at http://localhost:7777/toptrumps
@Produces(MediaType.APPLICATION_JSON) // This resource returns JSON content
@Consumes(MediaType.APPLICATION_JSON) // This resource can take JSON content as input
/**
 * This is a Dropwizard Resource that specifies what to provide when a user
 * requests a particular URL. In this case, the URLs are associated to the
 * different REST API methods that you will need to expose the game commands
 * to the Web page.
 *
 * Below are provided some sample methods that illustrate how to create
 * REST API methods in Dropwizard. You will need to replace these with
 * methods that allow a TopTrumps game to be controled from a Web page.
 */
public class TopTrumpsRESTAPI {

	/** A Jackson Object writer. It allows us to turn Java objects
	 * into JSON strings easily. */
	ObjectWriter oWriter = new ObjectMapper().writerWithDefaultPrettyPrinter();
	GameManager gm;
	ArrayList<User> players;
	TurnStatsHelper turnStats;

	private int numberOfAIPlayers;
	private boolean isPlayerChoice;

	/**
	 * Contructor method for the REST API. This is called first. It provides
	 * a TopTrumpsJSONConfiguration from which you can get the location of
	 * the deck file and the number of AI players.
	 * @param conf
	 */
	public TopTrumpsRESTAPI(TopTrumpsJSONConfiguration conf) throws IOException{

		gm = new GameManager();
	}



	@GET
	@Path("/helloJSONList")
	/**
	 * Here is an example of a simple REST get request that returns a String.
	 * We also illustrate here how we can convert Java objects to JSON strings.
	 * @return - List of words as JSON
	 * @throws IOException
	 */
	public String helloJSONList() throws IOException {

		List<String> listOfWords = new ArrayList<String>();
		listOfWords.add("Hello");
		listOfWords.add("World!");

		// We can turn arbatory Java objects directly into JSON strings using
		// Jackson seralization, assuming that the Java objects are not too complex.
		String listAsJSONString = oWriter.writeValueAsString(listOfWords);

		return listAsJSONString;
	}

	@GET
	@Path("/helloWord")
	/**
	 * Here is an example of how to read parameters provided in an HTML Get request.
	 * @param Word - A word
	 * @return - A String
	 * @throws IOException
	 */
	public String helloWord(@QueryParam("Word") String Word) throws IOException {
		return "Hello "+Word;
	}

	/*
	 * NEW APIs BELOW
	 */

	@GET
	@Path("/setUpGame")
	/**
	 *  for setting up the game
	 */
	public void setUpGameREST(@QueryParam("numberOfPlayers") int numberOfPlayers) throws IOException{
		System.out.println("AI Players: " + numberOfPlayers);
		numberOfAIPlayers = numberOfPlayers;
		setUpGame(numberOfPlayers);
		determinPlayerChoice();
	}

	@GET
	@Path("/isNextPlayerHuman")
	public String determinNextPlayerREST() throws IOException{

		/* better if returns a string of 'false' if false, and a string of the players cards if true
		 *
		 */

		return Boolean.toString(isPlayerChoice);
	}

	@GET
	@Path("/numOpponents")
	public int numberofOpponents() throws IOException{

		return numberOfAIPlayers;
	}

	@GET
	@Path("/displayCards")
	// http://localhost:7777/toptrumps/displayCards
	public String displayCards() throws IOException {

		return generateCardsJSON();
	}

	@GET
	@Path("/userChoice")

	// test with: http://localhost:7777/toptrumps/userChoice?choice=2
	public void userChoice(@QueryParam("choice") int choice) throws IOException{
		setUserChoice(choice);
		System.out.println(choice);
	}

	@GET
	@Path("/playRound")
	public String playRoundREST() throws IOException{
		String currentTurnStats = playRound();

		System.out.println(currentTurnStats);

		String turnStatsJSON = "{ \"turnStats\": [ " + currentTurnStats/* oWriter.writeValueAsString(currentTurnStats)*/+ "], \"points\": [" + oWriter.writeValueAsString(gm.getPoints()) + "]}";

		System.out.println(turnStatsJSON);


		return turnStatsJSON;
	}
	
	@GET
	@Path("/autoFinishGame")
	public String autoFinishGame() throws IOException{
		String currentTurnStats;
		
		do {
			currentTurnStats = playRound();
			
		}while(!gm.gameOver());
		
		String turnStatsJSON = "{ \"turnStats\": [ " + currentTurnStats/* oWriter.writeValueAsString(currentTurnStats)*/+ "], \"points\": [" + oWriter.writeValueAsString(gm.getPoints()) + "]}";
		
		return turnStatsJSON;
	}

	@GET
	@Path("/previousGameStats")
	public String getPreviousGameStats() throws IOException {
		String previousGameStats = oWriter.writeValueAsString(gm.getPreviousGameStats());
		return previousGameStats;
	}

	//TODO: Delete this - for testing only
	private void playGame(int numberOfAIPlayers) {
		gm.deal(numberOfAIPlayers);

		System.out.println("GAME STARTED");

		do {
			players = gm.getPlayers();

			if(gm.determinNextPlayer()) {
				gm.setCurrentChoice(2);
			}else {
//				System.out.println("Not user turn");
				gm.applyAICardChoice();
			}

			gm.playRound();

			players = gm.getPlayers();
			turnStats = gm.getTurnStatsHelper();


			gm.handleEndOfRound();

		}while(!gm.gameOver());

		System.out.println("GAME ENDED");

	}

	private void setUpGame(int numberOfAIPlayers) {
		gm = new GameManager();
		gm.deal(numberOfAIPlayers);
		System.out.println("GAME STARTED");
	}

	private void determinPlayerChoice() {

		players = gm.getPlayers();

		if(gm.determinNextPlayer()) {
			this.isPlayerChoice = true;
		}else {
			this.isPlayerChoice =  false;
		}

	}

	private void setUserChoice(int choice) {
		gm.setCurrentChoice(choice);
	}


	private String generateCardsJSON() {
		StringBuffer buffer = new StringBuffer();
		players = gm.getPlayers();

		buffer.append("{\n\"Stats\" : [{ \"roundNumber\" :\""+(gm.getTotalRounds())+"\", "
					+ "\n  \"isHumanChoice\" : \""+ isPlayerChoice +"\","
					+ "\n  \"numOfPlayers\" : \""+ players.size() +"\","
					+ "\n  \"nameOfNextPlayer\" : \""+ players.get(gm.getLastWinner()).getName() +"\"  }]," + System.lineSeparator() );

		for(int i = 0; i < players.size(); i++) {
			try {
				buffer.append(System.lineSeparator() + " \"" + players.get(i).getName()
						+"\" : [" + oWriter.writeValueAsString(players.get(i).getTopCard()) +  " ]");
				//puts commas in the appropriate place
				if(i<players.size()-1) {
					buffer.append(",");
				}


			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}

		buffer.append(System.lineSeparator() + "}");

		System.out.println(buffer);

		return buffer.toString();
	}

	//will return a turnstats!
	private String playRound() {

		gm.applyAICardChoice();

		gm.playRound();

		displayRoundSummery();

		gm.handleEndOfRound();

		turnStats = gm.getTurnStatsHelper();

	//	TurnStatsHelper current = turnStats.get(turnStats.size()-1);

	//	System.out.println("fromREST API current turnstats : " + current);

		determinPlayerChoice();

		if(gm.gameOver()) {
			return "{ \"GAME\" : \"OVER\",\n \"MY\" : \"DUDE\"}";
		}

		String turnStatsJSON = "";
		try {
			turnStatsJSON = oWriter.writeValueAsString(turnStats);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return turnStatsJSON;
	}

	/// for helping to debug - to be deleted!

	private void displayRoundSummery() {

		players = gm.getPlayers();
		turnStats = gm.getTurnStatsHelper();

		// 1)
	//	int currentTurnStats = turnStats;

		// 2)
		for(int i = 0; i < players.size(); i++) {
			System.out.printf("%s played....\t\t%s with %s\t\t\t\t(Remaining Cards : %d (%s))\n",
					turnStats.getPlayer(i).getName(),
					turnStats.getUserCardName(i),
					turnStats.getAnyCardTopAttribute(i),
					players.get(i).getHandSize(),
					turnStats.returnDifferenceHandSize(players.get(i), i));
		}

		// 3)
		//TODO Implement a GameStats here to convey a points system for each player

		String roundString = "";

		// 4)
		if(turnStats.getIsDraw()) {
			roundString = String.format("\nIts a draw!! Cards added to Community... "
					+ "\n\nCommunity deck size is currently: %d",
					gm.getCommunity().size());
		} else {
			roundString = String.format("\n%s won using %s with %s. "
					+ "\n\nCommunity deck size is currently: %d",
					players.get(gm.getLastWinner()).getName(), turnStats.getWinningCardName(), turnStats.getTopCardByAttribute(), gm.getCommunity().size());
		}

		System.out.println(roundString);

	}

}
