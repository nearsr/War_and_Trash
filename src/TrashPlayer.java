//This player is trash
public class TrashPlayer extends Player {
	Pile array;
	
	public TrashPlayer(int playerNum, String playerName) {
		super(playerNum, playerName);
		
		Pile array = new Pile();
	}

	public Pile getArray() {
		return array;
	}


	public void setArray(Pile array) {
		this.array = array;
	}
}
