import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Scanner;

public class SIM {
	static SIM simWrapper;
	static Simulation sim;
	int numIntervals;
	DecimalFormat df;
	
	public SIM(String[] args) {
		sim = new Simulation();
		df = new DecimalFormat("#.#####");
		
		numIntervals = Integer.parseInt(args[0]);
		String filename = args[1];
		
		sim.beginReadingFile(filename);
		double[] results = sim.generate(false);
		System.out.println(Arrays.toString(results));
		
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
