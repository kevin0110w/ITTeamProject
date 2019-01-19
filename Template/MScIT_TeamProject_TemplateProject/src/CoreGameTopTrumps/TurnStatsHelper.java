package CoreGameTopTrumps;

import java.util.ArrayList;

public class TurnStatsHelper {
	ArrayList<Card> cardsPlayed;
	ArrayList<User> players;
	
	int turnNumber;
	int winner;
	int attributeNumberPlayed;
	int communitySize;
	boolean isDraw;
	
	//This is what the user will see which summarises the round
	String roundString;
	
	public void addCardToCardsPlayed(Card card) {
		cardsPlayed.add(card);
	}
	
	public TurnStatsHelper(int turnNumber, int attributeNumberPlayed, ArrayList<User> players) {
		this.turnNumber = turnNumber;
		this.attributeNumberPlayed = attributeNumberPlayed;
		cardsPlayed = new ArrayList<Card>();
		this.players = new ArrayList<User>(players);
	}
	
	public void addPlayers(ArrayList<User> players) {
		this.players = new ArrayList<User>(players);
	}
	
	public void setWinner(int winner) {
		this.winner = winner;
	}
	
	public int getWinner() {
		return winner;
	}
	
	public boolean getIsDraw() {
		return isDraw;
	}
	
	public ArrayList<Card> passCardsPlayed(){
		return cardsPlayed;
	}
	
	// Returns -1 if its a draw, other wise the index of the winner
	// Loops through the chosen attribute of each top card and finds the highest/draw-y-est
	public int determineWinner() {
		int currentStat = 0;
		int highestStat = 0;		
		
		for(int i = 0; i < cardsPlayed.size(); i++) {
			
			currentStat = cardsPlayed.get(i).returnSelectedAttr(attributeNumberPlayed);
			
			if(currentStat > highestStat) {
				highestStat = currentStat;
				this.winner = i;
				isDraw = false;
			} else if (currentStat == highestStat) {
				this.winner = -1;
				isDraw = true;
			}
			
//			System.out.println("TurnStatsHelper.determineWinner currentStat : " + currentStat);
		}
		
		return winner;		
	}

	@Override
	public String toString() {
		return "TurnStatsHelper [cardsPlayed=" + cardsPlayed + ", turnNumber=" + turnNumber + ", winner=" + winner
				+ ", attributeNumberPlayed=" + attributeNumberPlayed + ", isDraw=" + isDraw + ", roundString="
				+ roundString + "]";
	}
	
	public String getRoundString() {
		
		// This is just a basic version right now because to make it properly user friendly
		// will probibly involve a lot more work

		return String.format("\nPlayer %d won using Attribute %d. \n\nCommunity deck size is currently:%d \n\nThe cards that everyone played are:\n%s ", winner, attributeNumberPlayed, communitySize, cardsPlayed);
	}

	public void setCommunitySize(int size) {
		this.communitySize = size;		
	}
	
	public String getTopCardByAttribute() {
		return cardsPlayed.get(this.winner).getAttribute(attributeNumberPlayed) + " : " + cardsPlayed.get(this.winner).getAttribute(attributeNumberPlayed);
	}
	
	public String getAnyCardTopAttribute(int playerIndex) {
		return cardsPlayed.get(playerIndex).getAttribute(attributeNumberPlayed) + " : " + cardsPlayed.get(playerIndex).getAttribute(attributeNumberPlayed);
	}
	
	public String getUserCardName(int index) {
		return cardsPlayed.get(index).getName();
	}
	
	public int getCardPlayedSize() {
		return cardsPlayed.size();
	}
	
	public User getPlayer(int index) {
		return players.get(index);
	}

}