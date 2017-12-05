
public enum Suit {
	SPADE(0), CLUB(1), HEART(2), DIAMOND(3);

	private final int value;
	private Suit(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static Suit getSuit(int value) {
		switch (value) {
		case 0: 
			return SPADE;	
		case 1:
			return CLUB;
		case 2:
			return HEART;
		case 3:
			return DIAMOND;
		default:
			System.out.println("Not a suit");
			return null;
		}
	}
	
	
}
