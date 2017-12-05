import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

//the first member of the arraylist is the bottom of the pile
public class Pile {
	ArrayList<Card> cards = new ArrayList<>();
	//Card topCard;
	//Card bottomCard; //index 0 card

	public Pile() {
		//default constructor is empty pile
		generateEmptyPile();
	}

	public Pile(boolean isFullDeck) {
		if (isFullDeck) {
			generateFullDeck();
		}
		//else pile starts empty
		else {
			generateEmptyPile();
		}
	}

	//shuffle the array of cards that calls this method
	public void shuffle(RandomInput rand) {
		int c = 0;
		int n = cards.size();
		
		while (c < n - 1) {
			//p is a random index in [c, n - 1] where n is cards.size()
			
			double r = rand.useRandomNum();
			int p = (int) Math.floor(r*(n-c) + c);
			
			//cards are no longer face up
			if (cards.get(c).isFaceUp()) {
				cards.get(c).setFaceUp(false);
			}
			if (cards.get(p).isFaceUp()) {
				cards.get(p).setFaceUp(false);
			}
			
			//swap card c and card p
			Collections.swap(cards, c, p);
			
			c++;
			
			//System.out.println(r);
		}
	}
	
	public void generateFullDeck() {
		Suit suit = null;

		//loop through all the suits
		for (Suit s : Suit.values()) {
			suit = s;

			//make all the cards in each suit
			for (int i = 1; i < 14; i++) {
				Card card = new Card();
				card.setValue(i);
				card.setSuitNum(suit.getValue());
				card.setFaceUp(false);
				this.giveCard(card);
			}
		}
	}

	public void generateEmptyPile() {
		this.setCards(new ArrayList<>());
	}

	public void printCards() {
		for (Card c : this.getCards()) {
			System.out.println(c.toString());
		}
	}

	public ArrayList<Card> getCards() {
		return cards;
	}
	public void setCards(ArrayList<Card> cards) {
		this.cards = cards;
	}

	public void giveCard(Card card) {
		this.getCards().add(card);
	}

	public void giveCards(ArrayList<Card> cards) {
		for (Card c: cards) {
			giveCard(c);
		}
	}

	/*
	public void takeTopCard(Pile pile) {

	}
	 */

	//Take all the cards from one pile and give to another
	public void transferCards(Pile lossPile, Pile gainPile) {

		for (Card c : lossPile.getCards()) {
			lossPile.getCards().remove(c);
			gainPile.giveCard(c);
		}
	}

	//0 is bottom card
	//most recently linked is top card
	//imagine dealing on cards while face up
	public Card removeTopCard() {
		
		int endIndex = getCards().size() - 1;
		Card topCard = getCards().remove(endIndex);
		return topCard;
	}
	
	public Card showTopCard() {
		
		int endIndex = getCards().size() - 1;
		Card topCard = getCards().get(endIndex);
		return topCard;
	}
	
	
	//Take all cards from another pile and give to this one
	public void grabCardsFrom(Pile lossPile) {

		//must declare end of deck ahead of time,
		//because the deck is going to shrink
		//each time you grab a card
		int endIndex = lossPile.getCards().size();
		
		//Must use a for loop instead of Card c: cards
		//or you will get concurrent modification exception
		for (int i = 0; i < endIndex; i++) {
			Card c = lossPile.getCards().get(0);

			lossPile.getCards().remove(c);
			this.giveCard(c);
		}
	}

	//Take some cards from a pile and give to this one
	public void grabCardsFrom(Pile lossPile, int startCard, int endCard) {

		int startIndex = startCard - 1;
		int endIndex = endCard - 1;

		//error catch
		if ((startIndex < 0) || (startIndex > lossPile.getCards().size()) ||
				(endCard < 0) || (endCard > lossPile.getCards().size())) {
			System.out.println("Invalid index.");
			System.exit(1);
		}

		for (int i = startIndex; i <= endIndex; i++) {
			//each time a card is grabbed, it is removed from the losspile
			//so always grab the first one at index 0
			Card c = lossPile.getCards().get(0);

			lossPile.getCards().remove(c);
			this.giveCard(c);
		}

	}

}
