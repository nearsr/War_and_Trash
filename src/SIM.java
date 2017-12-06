import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Scanner;

public class SIM {
	static SIM simWrapper;
	static Simulation sim;
	int numIntervals;
	DecimalFormat df;
	
	double lastTurnBound = .8; //Pr(L > .8)
	double numTurnBoundWar = 700; //Pr(N>700)
	double numTurnBoundTrash = 125; //Pr(N>125)
	
	boolean verbose;
	
	public SIM(String[] args) {
		sim = new Simulation();
		df = new DecimalFormat("#.#####");
		
		numIntervals = Integer.parseInt(args[0]);
		String filename = args[1];
		
		sim.beginReadingFile(filename);		
		
		//double[] results = sim.generate(false);
		//System.out.println(Arrays.toString(results));
		
		runTrials();
		sim.endReadingFile();
	}

	public enum Game {
		WAR,
		TRASH;
	}
	public enum Value {
		LAST_TURN,
		NUM_TURNS;
	}
	
	private void runTrials() {
		for (int i = 0; i < numIntervals; i++) {
			findConfidenceInterval(Game.WAR, Value.LAST_TURN);
			findConfidenceInterval(Game.WAR, Value.NUM_TURNS);
			findConfidenceInterval(Game.TRASH, Value.LAST_TURN);
			findConfidenceInterval(Game.TRASH, Value.NUM_TURNS);
		}
		
	}
	
	private void findConfidenceInterval(Game game, Value val) {
		//---------------------
		//Determine settings for loop
		boolean isWar;
		int resultIndex;
		double bound;
		String outputTag = "";
		if (game == Game.WAR) {
			isWar = true;
			outputTag += ":war-";
		}
		else {
			isWar = false;
			outputTag += ":trash-";
		}
		if (val == Value.LAST_TURN) {
			resultIndex = 2;
			outputTag += "last:";
			bound = lastTurnBound;
		}
		else {
			resultIndex = 0;
			outputTag += "n:";
			if (game == Game.WAR) {
				bound = numTurnBoundWar;
			}
			else {
				bound = numTurnBoundTrash;
			}
		}
		
		double gen = sim.generate(isWar)[resultIndex];
		double prob;
		if (gen > bound) {
			prob = 1;
		}
		else {
			prob = 0;
		}
		//---------------------
		
		double x_bar = prob;
		double n = 1;
		double v = 0;
		double t = 1.645;
		
		do {
			gen = sim.generate(isWar)[resultIndex];
			if (gen > bound) {
				prob = 1;
			}
			else {
				prob = 0;
			}
			
			double x = prob;
			n = n+1;
			double d = x - x_bar;
			v = v + ((n-1)/n)*Math.pow(d, 2);
			x_bar = x_bar + d/n;
		//} while((n < 40) || ((1/5) < (t/Math.sqrt(n-1))));
		} while((n<40) || t * Math.sqrt(v / n) > ( Math.sqrt(v/n)/5 * Math.sqrt(n - 1)));
		double s = Math.sqrt(v/n);
		
		double w = s/5; //confidence interval is x_bar +- w
		double low = x_bar - w;
		double high = x_bar + w;
		
		System.out.println("OUTPUT " + outputTag + " " + df.format(low) + " " 
							+ df.format(x_bar) + " " + df.format(high));
	}

	//For debug
	public void waitForUser(){
		   System.out.println("Press \"ENTER\" to continue...");
		   Scanner scanner = new Scanner(System.in);
		   scanner.nextLine();
		}
	
	public static void main(String[] args) {
		if( args.length == 2 ) {
			simWrapper = new SIM(args);
		}
		else {
			System.out.println("Invalid arguments");
			System.exit(1);
		}

	}
}
