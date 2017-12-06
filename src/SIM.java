import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Scanner;

public class SIM {

	static SIM simulation;

	Pile deck;
	RandomInput rand;

	boolean isWar;
	String filename;

	boolean debug = false;
	boolean debuggingWar = false;

	//N the total number of turns required to complete the game.
	int turnNumber = 0;

	//T the total number of winner transitions in the game.
	Player currentOverallWinner = null;
	int numWinnerTransitions = 0;

	//L when in the game the last winner transition occurred, measured as a fraction of N. 
	int turnOfLastWinnerTransition = 0;
	double fractionLastWinnerTransition = 0;

	private boolean stillPlaying;

	private boolean gameOver = false;


	public static void main(String[] args) {
		simulation = new SIM();

		if( args.length == 2 ) {
			simulation.getUserInput(args);
			simulation.initialize();
		}
		else {
			System.out.println("Invalid arguments");
			System.exit(1);
		}

	}

	public SIM() {

	}

	public void initialize() {
		//not officially a seed,
		//but serves the same function
		//of giving a different set of
		//random values
		//try seed 0,1,2...n

		int seed = 0;

		rand = new RandomInput(filename);
		rand.beginReadingNumbers();
		//rand.printRandomList();

		start();
		//System.out.println("Started");
	}

	private void start() {
		deck = new Pile();
		deck.generateFullDeck();

		//System.out.println("Fresh deck");
		//deck.printCards();

		deck.shuffle(rand);

		//System.out.println("Shuffled deck");
		//deck.printCards();

		if(isWar) {
			playWar();
		}
		else {
			playTrash();
		}
		
		rand.endReadingNumbers();
	}

	private void getUserInput(String[] args) {
		if (debug) {
			filename = "bin\\sim-traces-0-1\\uniform-0-1-00.dat";

			if (debuggingWar) isWar = true;
			else isWar = false;

			return;
		}

		/*
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter the word war or trash and the Path name of a file containing Uniform(0,1) samples: ");
		String whichGame = scanner.next();
		String filename = scanner.next();
		 */

		String whichGame = args[0];
		String filename = args[1];

		if (whichGame.equalsIgnoreCase("war")) {
			this.isWar = true;
		}
		else if (whichGame.equalsIgnoreCase("trash")) {
			this.isWar = false;
		}
		else {
			System.out.println("Unrecognized. Must enter war or trash, your choice of game to play.");
			System.out.println("Program terminated.");
			System.exit(1);
		}

		//Error checking on file is performed later
		this.filename = filename;
	}

	public Player getCurrentWinner() {
		return currentOverallWinner;
	}

	public void setCurrentWinner(Player winner) {	
		this.currentOverallWinner = winner;
		System.out.println(winner.getPlayerName() + " is now winning.");

	}


	//War code -----------------------------------------------------------------------------------
	private void playWar() {
		WarPlayer bob = new WarPlayer(1, "Bob");
		WarPlayer jon = new WarPlayer(2, "Jon");

		//grab half the deck, then the rest of the deck for the other player
		bob.getHand().grabCardsFrom(deck, 1, 52/2);
		jon.getHand().grabCardsFrom(deck);

		stillPlaying = true;

		while (stillPlaying) {
			fillAnyEmptyHands(bob, jon);

			if (checkIfGameOver(bob, jon) == true) {
				stillPlaying = false;
				checkIfWinnerChanged(bob, jon);
				System.out.println(currentOverallWinner.getPlayerName() + " won the game!");

				this.fractionLastWinnerTransition = ((double)turnOfLastWinnerTransition) / turnNumber;


				System.out.println();
				System.out.println("Turn of last winner transition: " + turnOfLastWinnerTransition);
				System.out.println();
				System.out.println("OUTPUT war turns " + turnNumber + " transitions " + 
						numWinnerTransitions + " last " + fractionLastWinnerTransition);

				break;
			}

			turnNumber++;

			//print
			System.out.println();
			System.out.println("Turn " + turnNumber);

			//both players remove top card
			Card bobsPlay = bob.getHand().removeTopCard();
			Card jonsPlay = jon.getHand().removeTopCard();

			//both players put them into their soldier piles
			bob.getSoldiers().giveCard(bobsPlay);
			jon.getSoldiers().giveCard(jonsPlay);

			//print
			System.out.println("Bob's play ");
			System.out.println(bobsPlay);
			System.out.println("Jon's play ");
			System.out.println(jonsPlay);

			WarPlayer roundWinner = determineRoundWinner(bob, bobsPlay, jon, jonsPlay);

			if (roundWinner != null) {
				Pile winnerTakeAll = new Pile();
				winnerTakeAll.grabCardsFrom(bob.getSoldiers());
				winnerTakeAll.grabCardsFrom(jon.getSoldiers());

				roundWinner.getWinnings().grabCardsFrom(winnerTakeAll);

				//print
				System.out.println(roundWinner.getPlayerName() + " takes the pot!");
				winnerTakeAll.printCards();

				System.out.println("Bob " + bob.getNumCards() + " : Jon " + jon.getNumCards());

				checkIfWinnerChanged(bob, jon);
			}
		}

		/*
		System.out.println("Bob");
		bob.getHand().printCards();
		System.out.println("Jon");
		jon.getHand().printCards();
		 */
	}

	public void checkIfWinnerChanged(WarPlayer bob, WarPlayer jon) {
		//the winner is the player with the most cards in the combination of their hand and winnings pile

		int bobNum = bob.getNumCards();
		int jonNum = jon.getNumCards();

		WarPlayer currentWinner = null;

		if(bobNum > jonNum) {
			currentWinner = bob;
		}
		else if(jonNum > bobNum) {
			currentWinner = jon;
		}

		if ((currentWinner != currentOverallWinner) && (currentWinner != null)) {
			setCurrentWinner(currentWinner);
			this.numWinnerTransitions++;
			this.turnOfLastWinnerTransition = turnNumber;
		}

	}


	public void fillAnyEmptyHands(WarPlayer bob, WarPlayer jon) {
		//if the player has cards left in winnings but none in hand, shuffle and transfer to hand
		if (bob.getHand().getCards().size() == 0 && bob.getWinnings().getCards().size() != 0) {
			bob.getHand().grabCardsFrom(bob.getWinnings());
			bob.getHand().shuffle(rand);
			System.out.println("Bob's hand is empty - he shuffles his winnings and puts them in hand.");
		}
		if (jon.getHand().getCards().size() == 0 && jon.getWinnings().getCards().size() != 0) {
			jon.getHand().grabCardsFrom(jon.getWinnings());
			jon.getHand().shuffle(rand);
			System.out.println("Jon's hand is empty - he shuffles his winnings and puts them in hand.");
		}
	}

	public boolean checkIfGameOver(WarPlayer bob, WarPlayer jon) {
		boolean isGameOver = false;

		if (bob.getHand().getCards().size() + bob.getWinnings().getCards().size() == 0 ||
				jon.getHand().getCards().size() + jon.getWinnings().getCards().size() == 0){
			isGameOver = true;
		}

		return isGameOver;
	}

	public WarPlayer determineRoundWinner(WarPlayer bob, Card bobsPlay, WarPlayer jon, Card jonsPlay) {
		//decide who won the round
		int higherCard = compareValue(bobsPlay, jonsPlay);
		WarPlayer roundWinner = null;

		if(higherCard == 0) {
			roundWinner = null;
		}
		else if (higherCard == 1) {
			roundWinner = bob;
		}
		else if (higherCard == 2) {
			roundWinner = jon;
		}

		return roundWinner;

	}

	//0 means equal in value
	//1 means first parameter greater
	//2 means second parameter greater
	//ace high rules
	public int compareValue(Card c1, Card c2) {

		int v1 = c1.getValue();
		int v2 = c2.getValue();

		//make the ace worth the most
		//if playing war
		if (isWar) {
			if (v1 == 1) v1 = 14;
			if (v2 == 1) v2 = 14;
		}

		if (v1 == v2) {
			return 0;
		}
		if (v1 > v2) {
			return 1;
		}
		//v1 < v2
		else {
			return 2;
		}
	}

	//Trash code --------------------------------------------------------------------------------
	private void playTrash(){
		//grab first two arrarys, the 10 card ones,
		//for the players
		Pile array1 = new Pile();
		Pile array2 = new Pile();
		array1.grabCardsFrom(deck, 1, 10);
		array2.grabCardsFrom(deck, 1, 10);

		//set up discard deck,
		//draw 1 card face up
		Card discardStart = deck.removeTopCard();
		discardStart.setFaceUp(true);
		Pile discard = new Pile();
		discard.giveCard(discardStart);

		//remaining cards go to the draw pile
		Pile draw = new  Pile();
		draw.grabCardsFrom(deck);

		TrashPlayer yui = new TrashPlayer(1, "Yui");
		TrashPlayer kim = new TrashPlayer(2, "Kim");

		//allot the arrays to the players
		yui.setArray(array1);
		kim.setArray(array2);

		stillPlaying = true;

		while (stillPlaying) {
			doTurn(yui, draw, discard, kim);
			doTurn(kim, draw, discard, yui);

			//DEBUG
			//stillPlaying = false;
		}
	}

	public void printTurnProgress(TrashPlayer p, Pile draw, Pile discard, Card hand) {
		String board = "";

		if (hand == null) {
			board += " " + " | ";
		}
		else {
			board += hand.getValue() + " | ";
		}

		for (Card c: p.getArray().getCards()) {
			if (c.isFaceUp()) {
				board += "*"+c.getValue()+"*";
			}
			else {
				board += "("+c.getValue()+")";
			}
		}

		board += " | ";

		//should all be face up		
		for (Card c: discard.getCards()) {
			if (c.isFaceUp()) {
				board += " "+c.getValue()+" ";
			}
			else {
				board += "("+c.getValue()+")";
			}
		}

		board += "> | ";

		//should all be face down
		for (Card c: draw.getCards()) {
			if (c.isFaceUp()) {
				board += c.getValue();
			}
			else {
				board += "("+c.getValue()+")";
			}
		}

		board += ">";
		System.out.println(board);
	}

	public void arrayStatusCheck(TrashPlayer p, Pile draw, Pile discard) {
		//better check at beginning of each action if you can clear your array		
		if (isArrayComplete(p, draw, discard)) {
			clearArray(p, draw, discard);
			if(gameOver) return;
			System.out.println("Clear!");
			System.out.println("New array");
			printTurnProgress(p, draw, discard, null);
		}
	}


	//are all cards in array face up
	//and in proper order
	private boolean isArrayComplete(TrashPlayer p, Pile draw, Pile discard) {
		//assume true
		boolean isComplete = true;

		//if a single card is face down, its not complete
		for (Card c : p.getArray().getCards()) {
			if (c.isFaceUp() == false) {
				isComplete = false;
				break;
			}
		}

		//if a single card is out of order (index doesn't match value),
		//its not complete
		for (int i =0; i < p.getArray().getCards().size(); i++) {
			Card c = p.getArray().getCards().get(i);
			//if the value is out of order and it is not a jack
			if ((c.getValue() != i+1) && (c.getValue() != 11)){
				isComplete = false;
				break;
			}
		}

		return isComplete;
	}

	//function that shuffles
	//old array back in and deals a new one
	//that is smaller
	private void clearArray(TrashPlayer p, Pile draw, Pile discard) {
		//determine length of new array
		int size = p.getArray().getCards().size();
		int newSize = size - 1;

		//if new size for array to deal is zero
		//end the game
		if (newSize == 0) {
			//this player wins
			//they cleared their array first
			endGame(p);
			return;
		}

		//shuffle all the cards in their array, the discard and draw pile, 
		Pile combineCards = new Pile();
		combineCards.grabCardsFrom(p.getArray());
		combineCards.grabCardsFrom(discard);
		combineCards.grabCardsFrom(draw);

		combineCards.shuffle(rand);

		//deal their next array, and place the remaining cards into a new draw pile
		//(discard will be empty until they finish their turn)
		Pile newArray = new Pile();
		newArray.grabCardsFrom(combineCards, 1, newSize);
		p.setArray(newArray);

		draw.grabCardsFrom(combineCards);
	}

	public void endGame(TrashPlayer winner) {

		stillPlaying = false;
		System.out.println(currentOverallWinner.getPlayerName() + " won the game!");

		this.fractionLastWinnerTransition = ((double)turnOfLastWinnerTransition) / turnNumber;


		System.out.println();
		System.out.println("Turn of last winner transition: " + turnOfLastWinnerTransition);
		System.out.println();
		System.out.println("OUTPUT trash turns " + turnNumber + " transitions " + 
				numWinnerTransitions + " last " + fractionLastWinnerTransition);

		gameOver  = true;

	}

	public Card swapCardWithArrayAt(int index, Card card, TrashPlayer p) {
		Card temp = card;
		card = p.getArray().getCards().get(index);
		card.setFaceUp(true);
		p.getArray().getCards().set(index, temp);

		return card;
	}

	public void doTurn(TrashPlayer p, Pile draw, Pile discard, TrashPlayer otherPlayer) {

		if (!stillPlaying) {
			return;
		}

		turnNumber++;

		//print
		System.out.println();
		System.out.println("Turn " + turnNumber);

		System.out.println(p.getPlayerName() + " turn start");
		printTurnProgress(p, draw, discard, null);

		//GAMEPLAY
		//If the draw pile is depleted at the beginning of a turn,
		//the discard pile (except for the top-most, "showing" card)
		//is shuffled to become the new draw pile.
		if (draw.getCards().size() == 0) {
			Card topCard = discard.removeTopCard();
			draw.grabCardsFrom(discard);
			draw.shuffle(rand);
			discard.giveCard(topCard);
		}

		//hand does not persist through next player's turn
		//so give a new one each time
		Card hand = null;

		//players can continue making actions
		//until unable to do anything new/useful
		//so we need another loop
		boolean canPlay = true;

		while(canPlay) {

			arrayStatusCheck(p, draw, discard);
			if(gameOver) return;

			//NOTES:
			//consider options
			//everything in discard is known
			//top card is active card
			//draw pile is last resort
			//so play the discard pile card if you can
			//otherwise take the draw pile card

			//values needed excludes jack from the count
			//so that it can be replaced by the real thing later on
			//however, when checking to see if the array list is complete,
			//jacks are considered
			ArrayList<Integer> valuesNeeded = new ArrayList<>();
			for(int i = 0; i < p.getArray().getCards().size(); i++) {
				Card c = p.getArray().getCards().get(i);
				if((c.isFaceUp() == false)) {
					valuesNeeded.add(i+1);
				}
				//else if (c.isFaceUp() && (c.getValue() == 11))  {
				else if (c.isFaceUp() && (c.getValue() == 11))  {
					//continue;
					valuesNeeded.add(i+1);
				}
			}

			//if no card in hand, get one
			if(hand == null) {
				//if discard pile card is needed, take it in hand
				if(valuesNeeded.contains(discard.showTopCard().getValue())) {
					hand = discard.removeTopCard();
				}
				//else get the one from the draw pile
				else {
					hand = draw.removeTopCard();
				}

				hand.setFaceUp(true);

				System.out.println("Draws card");
				printTurnProgress(p, draw, discard, hand);
			}

			//decide how to play the card in hand
			//if you can play it, go ahead
			if(valuesNeeded.contains(hand.getValue())) {

				int index = hand.getValue() - 1;
				//switch unknown card (or jack) with hand
				hand = swapCardWithArrayAt(index, hand, p);

				System.out.println("Swap");
			}
			//if it's a jack, the rules are different
			//and you have to choose
			else if(hand.getValue() == 11) {
				//first, see if there's only one place to put it
				//if there is put it there
				int numFaceDown = 0;
				int indexFaceDown = Integer.MAX_VALUE;
				for (int i = 0; i < p.getArray().getCards().size(); i++) {
					if ((p.getArray().getCards().get(i).isFaceUp() == false)) {
						numFaceDown++;
						indexFaceDown = i;
					}
				}
				if (numFaceDown == 1) {
					hand = swapCardWithArrayAt(indexFaceDown, hand, p);
					System.out.println("Swap.");
				}
				//otherwise, you have to decide where to put the jack
				else {


					/*
					//###FIX LATER TO MAKE OPTIMAL
					//right now, just gets first needed index thats not a jack
					int index = Integer.MAX_VALUE; 
					for (int i =0; i < valuesNeeded.size(); i++) {
						index = valuesNeeded.get(i)-1;
						if (p.getArray().getCards().get(index).getValue() != 11)
						{
							//break keeping earliest index that needs fillings
							//and is not a jack
							break;
						}
					}
					 */

					int index = decideJackIndex(p, otherPlayer, discard);

					//switch unknown card with jack in hand
					hand = swapCardWithArrayAt(index, hand, p);


					System.out.println("Swap~");
					System.out.println(index);

				}
			}
			//if you can't play it you have to discard
			//and end your turn
			else {
				discard.giveCard(hand);
				hand = null;
				canPlay = false;

				System.out.println("Discard");
			}

			//System.out.println("Action");
			printTurnProgress(p, draw, discard, hand);

			//DEBUG
			//canPlay = false;

		}//end actions

		System.out.println("Turn ends");

		checkIfWinnerChanged(p, otherPlayer);
	}

	private int decideJackIndex(TrashPlayer p, TrashPlayer otherPlayer, Pile discard) {
		ArrayList<Integer> numPlayed = new ArrayList<>();

		//empty array. index + 1 is card value it is counting
		for (int i = 0; i < 10; i++) {
			numPlayed.add(0);
		}

		//count discards that were seen previously
		for (Card c : discard.getCards()) {
			int cardValue = c.getValue();

			//dont care about trash cards
			if(cardValue <= 10) {
				int count = numPlayed.get(cardValue - 1);
				count++;
				numPlayed.set(cardValue - 1, count);
			}
		}

		//this does not actually matter to the play
		//because you can't place a jack onto a face up card
		//but this loop allows the played card counts to be technically correct
		//count face up in my array
		//but not jacks
		for (Card c : p.getArray().getCards()) {
			if (c.isFaceUp() && c.getValue() != 11) {
				int cardValue = c.getValue();

				int count = numPlayed.get(cardValue - 1);
				count++;
				numPlayed.set(cardValue - 1, count);
			}
		}

		//count face up in opponent array
		//but not the jacks
		for (Card c : otherPlayer.getArray().getCards()) {
			if (c.isFaceUp() && c.getValue() != 11) {
				int cardValue = c.getValue();

				int count = numPlayed.get(cardValue - 1);
				count++;
				numPlayed.set(cardValue - 1, count);
			}
		}

		int scarcestValue = -1;
		int leastAmountLeft = Integer.MAX_VALUE;

		//look at all the not face up cards in my array
		//decide which is at a position that is scarce
		for (int i = 0; i < p.getArray().getCards().size(); i++) {
			Card c = p.getArray().getCards().get(i);

			if (c.isFaceUp() == false) {
				int valueNeeded = i+1;

				int howManyPlayed = numPlayed.get(i);
				//4 of each value exist
				int howManyLeft = 4 - howManyPlayed;

				if (howManyLeft < leastAmountLeft) {
					leastAmountLeft = howManyLeft;
					scarcestValue = valueNeeded;
				}
			}
		}

		return scarcestValue - 1;
	}

	//num of cards in array that are face up
	//and thus in order
	public int getNumFaceUp (TrashPlayer p) {
		int n = 0;

		for (Card c : p.getArray().getCards()) {
			if (c.isFaceUp()) {
				n++;
			}
		}

		return n;
	}

	public void checkIfWinnerChanged(TrashPlayer yui, TrashPlayer kim) {
		//winner is the one with the shorter array
		//or if same size the most populated one (most face up cards)
		TrashPlayer currentWinner = null;

		int yuiArraySize = yui.getArray().getCards().size();
		int kimArraySize = kim.getArray().getCards().size();

		if(yuiArraySize < kimArraySize) {
			currentWinner = yui;
		}
		else if(kimArraySize < yuiArraySize) {
			currentWinner = kim;
		}
		//arrays equal
		else {
			int yuiFaceUp = getNumFaceUp(yui);
			int kimFaceUp = getNumFaceUp(kim);

			if(yuiFaceUp > kimFaceUp) {
				currentWinner = yui;
			}
			else if (kimFaceUp > yuiFaceUp) {
				currentWinner = kim;
			}
			//face up equal
			//means they are tied
			else {
				currentWinner = null;
			}
		}

		if ((currentWinner != currentOverallWinner) && (currentWinner != null)) {
			setCurrentWinner(currentWinner);
			this.numWinnerTransitions++;
			this.turnOfLastWinnerTransition = turnNumber;
		}


	}

}
