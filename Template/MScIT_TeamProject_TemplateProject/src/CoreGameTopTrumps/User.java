package CoreGameTopTrumps;

import java.util.ArrayList;

/* This will be the superclass which human and aiuser will both inherit from
*/

abstract public class User {
	private String name;
	private ArrayList<Card> hand;

	public User() {
		this.hand = new ArrayList<Card>();
	}
	public User(String name, ArrayList<Card> hand) {
		this.name = name;
		this.hand = hand;
	}

	// get user's name (might not be necessary)
	public String getName() {
		return this.name;
	}
	abstract String getCriteriaName(Card topCard);
	abstract  int getIndexofCriteriaWithHighestValue(Card topCard);
	// set user's name (might not be necessary)
	public void setName(String name) {
		this.name = name;
	}

	// This method will get a player's hand
	public ArrayList<Card> getHand() {
		return this.hand;
	}

	// This method will set a player's hand
	public void setHand(ArrayList<Card> dealtCards) {
		this.hand.clear();
		this.hand = dealtCards;
	}
	
	abstract String getTopCardName();
//abstract public String playerChoosesMessage(Card topCard);
	public String playerChoosesMessage(Card topCard) {
		return this.name + " chooses attribute " + this.getIndexofCriteriaWithHighestValue(topCard) + ": " + this.getCriteriaName(topCard);
	}
	// This method will print the topCard
	public String showTopCard() {
		Card topCard = this.hand.get(0);
		return topCard.viewCard();
	}
	
	// This method will return the first card in the hand
	public Card getTopCard() {
		return this.hand.get(0);
	}

	// If a player wins, this method will add the pile of cards to the end of the player's hand
	public void addCards(ArrayList<Card> wonCards) {
		this.hand.addAll(wonCards);
	}
	// This method will add a single card to the back of a player's hand
	public void addSingleCard(Card card) {
		this.hand.add(card);
	}
	
	// This method will return the name of a criteria that is selected by a player
	public String selectCriteriaOfTopCard(int index) {
		Card topCard = this.hand.get(0);
		String attributeName = topCard.getCriteriaName(index);
		return attributeName;
	}
	
	// This method will return the number associated with a criteria 
	public int getValue(int criteriaNumber) {
		Card topCard = this.hand.get(0);
		return topCard.getAttribute(criteriaNumber);
	}

	// Return the name of the top card
	public String getNameOfTopCard() {
		return this.hand.get(0).getName();
	}

	//This method will remove the top card (i.e. the card at the start)
	public void discardTopCard() {
		this.hand.remove(0);
	}

	// Return the size of a player's hand
	public int getHandSize() {
		return this.hand.size();
	}
	//Added for testing - prints all cards to sys out
	public void displayEntireHand() {
		for(Card c: hand) {
			c.viewCard();
		}
	}

	// checks to see if a user's hand is empty
	// return true (i.e. a player is out).
	public boolean userLoses() {
		return this.hand.size() ==0;
	}
	
	public String toString() {
		return this.name + ": You have " + this.getHandSize() + " cards in your deck.";
	}
}
