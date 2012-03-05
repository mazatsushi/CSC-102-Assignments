/**
 * @Student Name: Desmond Poh Lik Meng
 * @Lab Group: BCG1
 */

// Import classes not included in the default Java environment
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.Random;
import java.util.Scanner;

public class MainApp {

	/*
	 * @Global private attributes
	 */
	private static final String abbreviationsList = "abbreviations.txt";
	private static boolean isFirstRun = true;
	private static Scanner input;
	private static Database db;
	private static TranslateApp transAm;
	private static Abbreviation abb = null;
	private static Abbreviation [] abbArray = null;
	private static File inputFile, outputFile;
	private static String inputFileName, outputFileName, inputHolder;
	private static byte choice;
	private static int linewidth;

	public static void main(String[] args) throws IOException, NullPointerException {

		// Loop indefinitely until user asks to stop
		while (true) {
			try {

				// Action to take if program is running for the first time
				if (isFirstRun) {		
					
					// Initialize some of the private attributes
					input = new Scanner(System.in);
					db = new Database(abbreviationsList);
					transAm = new TranslateApp(db);
					
					// Display reading in of the abbreviation file and show the number of abbreviations reconstructed
					System.out.print("Reading data set from [" + abbreviationsList + "]");
					System.out.print("\n - " + db.getNumAbbreviations() + " abbreviations\n");
					isFirstRun = !isFirstRun;
				}

				// Print out the text based menu
				System.out.print("\nWelcome to Abbreviation System");
				System.out.print("\n==============================");
				System.out.print("\n0. Display this menu");
				System.out.print("\n1. List all abbreviations");
				System.out.print("\n2. Query abbreviation by exact match");
				System.out.print("\n3. Query abbreviation by prefix match");
				System.out.print("\n4. File translation");
				System.out.print("\n5. Abbreviation Quiz");
				System.out.print("\n9. Exit");
				System.out.print("\n");
				System.out.print("\nEnter your menu choice: ");

				// Do a type cast to check for invalid inputs, if there are any errors an exception will be thrown
				choice = Byte.parseByte(input.nextLine());
				switch(choice) {
				case 0:
					break;
				case 1:
					
					// Print all the abbreviations reconstructed from the abbreviation list file
					db.printAllAbbreviations();
					break;
				case 2:
					
					// Prompt user to enter the exact name of the abbreviation to search for
					System.out.print("\nEnter an exact abbreviation name for your query: ");
					inputHolder = input.nextLine();
					
					// Search for the abbreviation given the input user has provided
					abb = db.queryByAbbrName(inputHolder);
					
					// Print out information regarding the abbreviation if it is found
					if (abb != null) {
						System.out.print("\nAbbreviation: [" + abb.getAbbrName() + "]");
						for (byte i = 0; i < abb.getNumFullNames(); ++i) {
							System.out.print("\n  [" + abb.getFullName(i) + "]");
						}
						System.out.print("\n");
						abb = null;
					}
					
					// Return error message that no such abbreviation exists
					else {
						System.out.print(" - Abbreviation [" + inputHolder + "] not found in database\n");
					}
					inputHolder = null;
					break;
				case 3:
					
					// Prompt user to enter a prefix to search for
					System.out.print("\nEnter an abbreviation prefix for your query: ");
					inputHolder = input.nextLine().toUpperCase();
					
					// Search for all abbreviations that has the specified prefix
					abbArray = db.queryByAbbrPrefix(inputHolder);
					
					// Print out information regarding all the abbreviations found in the search
					if (abbArray != null) {
						abb = abbArray[0];
						for (byte i = 0; i < abbArray.length; ++i) {
							abb = abbArray[i];
							for (byte j = 0; j < abb.getNumFullNames(); ++j) {
								System.out.print("\nAbbreviation: [" + abb.getAbbrName() + "]");
								System.out.print("\n [" + abb.getFullName(j) + "]");
							}
						}
						System.out.print("\n");
						abb = null;
						abbArray = null;
					}
					
					// Return error message that no such abbreviations exist
					else {
						System.out.print(" - Abbreviation prefix [" + inputHolder + "] not found in database\n");
					}
					inputHolder = null;
					break;
				case 4:
					
					/*
					 * @Force program logic to clear three different checkpoints
					 * @Each checkpoint ensures different valid input ranges before allowing user to proceed
					 */
					if (checkPointOne()) {
						if (checkPointTwo()) {
							if (checkPointThree()) {
								System.out.print(" - Translation done: " + transAm.translateFile(inputFileName, outputFileName, linewidth) + " abbreviations found!\n");
							}
						}
					}
					inputHolder = outputFileName = inputFileName = null;
					outputFile = inputFile = null;
					break;
				case 5:
					
					byte finalScore = 0;
					
					// The random class is used for generating psuedo-random element numbers
					Random random = new Random();
					for (int i = 1; i < 6; ++i) {
						
						// Retrieve the abbreviation object at the randomly chosen index
						abb = db.getAbbreviationByIndex(random.nextInt(db.getNumAbbreviations() + 1));
						
						// Ask the question
						System.out.print("\nQ" + i + ": What is " + abb.getAbbrName() + "?");
						System.out.print("\nAns: ");
						
						// Get user answer and compare with the actual answer (might have more than one)
						inputHolder = input.nextLine();
						for (int k = 0; k < abb.getNumFullNames(); k++) {
							
							// Action to take is user answer is correct
							if (abb.getFullName(k).compareToIgnoreCase(inputHolder) == 0) {
								finalScore += 20;
								System.out.print(" Correct. You got " + finalScore + " points now.\n");
								break;
							}
							
							// Action to take if user answer is wrong
							else {
								System.out.print(" Incorrect. The answer(s) is/are");
								for (int l = 0; l < abb.getNumFullNames(); ++l) {
									if (l > 0) {
										System.out.print(" or");
									}
									System.out.print(" [" + abb.getFullName(l) + "]");
								}
								System.out.print("\n");
							}
						}
					}
					
					// Print out the final score
					System.out.print("\nYour final score is " + finalScore + "\n");
					inputHolder = null;
					random = null;
					break;
				case 9:
					
					// Close the scanner input stream and exit back to operating system
					input.close();
					System.out.print("\nProgram terminated.\n");
					System.exit(0);
				default:
					
					// Return error message if input is any other number
					System.out.print("  Err: Invalid selection!\n");
					break;
				}

			}

			// This exception is thrown whenever user enters something other than a number when prompted to
			catch (NumberFormatException e) {
				System.out.print("  Err: Invalid selection!\n");
			}

			// This exception is thrown when the file to be translated cannot be found in the same directory as the program
			catch (FileNotFoundException e) {
				System.out.print("\nFile [" + inputFileName + "] not found!\n");
			}		

			/*
			 * @This exception is thrown when some error occurs while reading in from the to-be-translated file,
			 * or when an error occurs while the translated text to the output file
			 */			
			catch (IOException e) {
				System.out.print("\nAn error occured while performing a file read or write operation.");
				System.out.print("\nPossilities for this error is due to the following: ");
				System.out.print("\n 1) Failure or interrupted read operation while reading from " + inputFileName);
				System.out.print("\n 2) Failure or interrupted read operation while reading from " + outputFileName);
				System.out.print("\nPlease contact your system administrator for further assistance.\n");
			}

			// This exception is thrown if the file to be translated has no data in it
			catch (NullPointerException e) {
				System.out.print("\nThere is no data within \"" + inputFileName + ".txt\".");
				System.out.print("\nPlease ensure that \"" + inputFileName + ".txt\" has data before trying again.\n");
			}
		}
	}
	
	/*
	 * @Method that serves as a first checkpoint before allowing user to proceed with the translation
	 * @Checks that the file to read in exists, and has read permissions
	 * @For good measure in case the user forgets to add in the .txt file extension, it is added for them
	 */
	private static boolean checkPointOne() {
		boolean allClear = false;
		
		// Prompt user to enter the name of the target input file
		System.out.print("\nInput file: ");
		inputFileName = input.nextLine().toLowerCase();
		
		// Help user append the .txt file typename if it is missing
		if (inputFileName.lastIndexOf(".txt") == -1) {
			inputFileName = inputFileName.concat(".txt");
		}
		
		// Create a new file object representing this supposed input file
		inputFile = new File(inputFileName);
		
		// Check that the target input file exists and has read permissions
		if (inputFile.exists() && inputFile.canRead()) {
			allClear = !allClear;
		}
		
		// Return error message if the input file does not exist
		else if (!inputFile.exists()) {
			System.out.print(" - Error: File [" + inputFileName + "] not found!\n");
		}
		
		// Return error message if the input file has no read permissions
		else if (!inputFile.canRead()) {
			System.out.print(" - Error: File [" + inputFileName + "] cannot be read from!\n");
		}
		
		return allClear;
	}
	
	/*
	 * @Method that serves as a second checkpoint before allowing user to proceed with the translation
	 * @Obtains permission from the user to overwrite the destination output file if it already exists
	 * @Thoroughly checks for invalid input every step of the way for better fault tolerance
	 * @For good measure in case the user forgets to add in the .txt file extension, it is added for them
	 */
	private static boolean checkPointTwo() {
		boolean allClear = false;
		
		// Prompt user to enter name of the desired output file
		System.out.print("\nOutput file: ");
		outputFileName = input.nextLine().toLowerCase();
		
		// Help user append the .txt file typename if it is missing
		if (outputFileName.lastIndexOf(".txt") == -1) {
			outputFileName = outputFileName.concat(".txt");
		}
		
		// Create a new file object representing this supposed output file
		outputFile = new File(outputFileName);
		
		// Check whether the desired output file currently exists
		if (outputFile.exists()) {
			
			// Code block that continually asks user for permission to overwrite the data in an existing file
			inputHolder = new String();
			boolean morePrompts = true;

			// Keep prompting until user has entered in a valid single character
			while (inputHolder.length() > 1 || inputHolder.isEmpty() || morePrompts) {
				System.out.print("\nFile: [" + outputFileName + "] already exists! Do you want to overwrite it?");
				System.out.print("\nY/N: ");
				inputHolder = input.nextLine().toLowerCase();
				
				// Make sure that any character is within the expected ranges
				if (inputHolder.length() == 1) {
					switch (inputHolder.charAt(0)) {
					case 'y':
						allClear = !allClear;
						morePrompts = !morePrompts;
						break;
					case 'n':
						morePrompts = !morePrompts;
						break;
					default:
						break;
					}
				}					
			}
		}
		
		// If the desired output file does not currently exist, checkpoint two is cleared
		else {
			allClear = !allClear;
		}

		return allClear;
	}
	
	/*
	 * @Method that serves as the final checkpoint before allowing the user to proceed with translation
	 * @Obtains the maximum line width that the output file should have
	 * @Thoroughly checks for invalid input every step of the way for better fault tolerance
	 */
	private static boolean checkPointThree() {
		boolean allClear = false, morePrompts = true;
		linewidth = 0;

		// Keep prompting until user has entered in a valid line width value
		while (linewidth < 30 || morePrompts) {
			try {
				// Prompt user to enter the maximum line width for the output file
				System.out.print("\nEnter max number of characters per line (at least 30): ");
				inputHolder = input.nextLine();
				linewidth = Integer.parseInt(inputHolder);
				
				// Make sure that any number values entered is at least the minimum required
				if (linewidth > 29) {
					morePrompts = !morePrompts;
					allClear = !allClear;
				}
				else {
					throw new NumberFormatException();
				}
			}
			catch (NumberFormatException e) {
				System.out.print(" - Error: Invalid input value!\n");
			}
		}
		return allClear;
	}
}