
public class Card {
	int suitNum;
	int value;
	
	//for use with trash only
	//means the card has been seen
	//and its location in one of the decks
	//is known
	boolean faceUp;

	public boolean isFaceUp() {
		return faceUp;
	}
	public void setFaceUp(boolean faceUp) {
		this.faceUp = faceUp;
	}
	public int getSuitNum() {
		return suitNum;
	}
	public void setSuitNum(int suit) {
		this.suitNum = suit;
	}

	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		if ((value > 0) && (value < 14))
		{
			this.value = value;
		}
		else System.out.println("Card cannot have that value.");
	}
	
	@Override
	public String toString() {
		Suit suit = Suit.getSuit(suitNum);
		return "Card [" + value + " of " + suit.toString() + "]";
	}

	



}
