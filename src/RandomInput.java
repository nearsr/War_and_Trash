import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;

public class RandomInput {

	String filename;

	//number of random lines that will last a whole simulation
	final int linesNeededForGame = 8200;
	int samplesPerTraceFile = 65536;
	private LineNumberReader reader;

	public RandomInput(String filename) {
		this.setFilename(filename);
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	//using it uses it up and we go to next line for read
	public double useRandomNum(){		
		//assume line is null until read successfully
		String line = null;

		//loop through all lines
		try {
			if((line = reader.readLine()) != null) {
				//System.out.println(line);

				String s = line.trim();
				double rand = Double.parseDouble(s);
				return rand;

			}  
			else {
				reader.close();
				throw new Exception("File ended prematurely");
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			System.exit(1);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
		return 0;
	}

	//Read input from a file
	//Once random input is used once in a simulation,
	//it cannot be used again in the same simulation. Go to next unused file index.
	public void beginReadingNumbers() {

		try {
			// create a reader to process the file
			FileReader fileReader = new FileReader(filename);

			// buffered reader goes on the outside of filereader
			reader = new LineNumberReader(fileReader);
		}
		catch(FileNotFoundException ex) {
			System.out.println(
					"Cannot open " + filename); 
			System.exit(1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void endReadingNumbers() {
		// must close file after reading
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			System.exit(1);
		}   
		
	}
}



