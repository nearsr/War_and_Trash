
public class WarPlayer extends Player {

	Pile hand;
	Pile winnings;
	Pile soldiers;
	
	//soldiers refers to the cards in battle
	//during an exchange in which the higher card wins
	//often, only two will be in play
	//in the case of a tie, more cards are stacked on top
	//and winner takes all
	
	public WarPlayer(int playerNum, String playerName) {
		super(playerNum, playerName);

		hand = new Pile();
		winnings = new Pile();
		soldiers = new Pile();
	}
	
	public int getNumCards() {
		return getHand().getCards().size() + this.getWinnings().getCards().size();
	}
	
	public Pile getHand() {
		return hand;
	}

	public void setHand(Pile hand) {
		this.hand = hand;
	}

	public Pile getWinnings() {
		return winnings;
	}

	public void setWinnings(Pile winnings) {
		this.winnings = winnings;
	}

	public Pile getSoldiers() {
		return soldiers;
	}

	public void setSoldiers(Pile soldiers) {
		this.soldiers = soldiers;
	}


}
