import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;

public class RandomInput {

	//Current index in file
	//Before this index, all numbers have been used
	int lineStartIndex;
	ArrayList<Double> randomList;
	
	String filename;
	
	//number of random lines that will last a whole simulation
	//determines the options for seeds
	final int linesNeededForGame = 8200;
	//final int linesNeededForGame = 5;
	
	int samplesPerTraceFile = 65536;

	public RandomInput(String filename) {
		this.setCurrentFileIndex(0);
		this.setFilename(filename);
	}
	
	//using it uses it up and it is removed from the array list
	public double useRandomNum(){
		if (randomList.size() == 0) {
			System.out.println("Premature end of random number list.");
			System.exit(1);
		}
		
		//remove and return the first random number in the array list
		return randomList.remove(0);
	}
	
	public void printRandomList() {
		for (Double d : randomList) {
			System.out.println(d);
		}
	}
	
	
	public String getFilename() {
		return filename;
	}



	public void setFilename(String filename) {
		this.filename = filename;
	}



	public int getCurrentFileIndex() {
		return lineStartIndex;
	}

	public void setCurrentFileIndex(int lineIndex) {
		this.lineStartIndex = lineIndex;
	}
	
	
	
	public ArrayList<Double> getRandomList() {
		return randomList;
	}

	public void setRandomList(ArrayList<Double> randomList) {
		this.randomList = randomList;
	}

	public void generateRandomList(int seed) {
		int numberOfPossibleSeeds = (int) Math.floor(samplesPerTraceFile / linesNeededForGame);
		
		if ((seed >= 0) && (seed < numberOfPossibleSeeds)) {
			lineStartIndex = linesNeededForGame * seed;
		}
		//invalid seed
		else {
			System.out.println("Seed for random numbers is outside file bounds.");
			System.out.println("Terminating program.");
			System.exit(1);
		}
		
		ArrayList<Double> newRandomList = makeRandomList(linesNeededForGame);
		
		this.setRandomList(newRandomList);
	}
	
	//Read input from a file
	//Once random input is used once in a simulation,
	//it cannot be used again in the same simulation. Go to next unused file index.
	private ArrayList<Double> makeRandomList(int randomNumQuantity) {

		ArrayList<Double> randomList = new ArrayList<>();
		
		int lineStartIndex = this.getCurrentFileIndex();
		int lineEndIndex = lineStartIndex + randomNumQuantity;

		try {
			// create a reader to process the file
			FileReader fileReader = new FileReader(filename);

			// buffered reader goes on the outside of filereader
			LineNumberReader reader = new LineNumberReader(fileReader);


			//assume line is null until read successfully
			String line = null;

			//loop through all lines
			while((line = reader.readLine()) != null) {
				//System.out.println(line);

				//if current line within bounds
				if ((reader.getLineNumber() > lineStartIndex) && (reader.getLineNumber() <= lineEndIndex)) {
					
					String s = line.trim();
					double rand = Double.parseDouble(s);
					randomList.add(rand);
				}
				else if (reader.getLineNumber() >= lineEndIndex) {
					//exit the while loop once past desired index
					break;
				}
			}   
			
			//we shouldnt get to the end of the file in this simulation
			//so make sure the program closes if we do
			if (line == null) {
				reader.close();
				throw new Exception("File ended prematurely");
			}

			// must close file after reading
			//for now, close it each time
			reader.close();       

			this.setCurrentFileIndex(lineEndIndex);
			return randomList;
		}
		catch(FileNotFoundException ex) {
			System.out.println(
					"Cannot open " + filename); 
			System.exit(1);
		}
		catch(IOException ex) {
			System.out.println(
					"Cannot read " + filename);     
			System.exit(1);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}

		//return null if try was not successful
		return null;
	}

}


