/**
 * @Student Name: Desmond Poh Lik Meng
 * @Lab Group: BCG1
 */

// Import classes not included in the default Java environment
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.StringTokenizer;

//@Exception handling for this class is handled by the main method due to overlapping similarities
public class TranslateApp {

	/*
	 * @Private attributes for this class
	 */
	private Database database;
	private BufferedReader bin;
	private BufferedWriter bout;
	private StreamTokenizer input;

	// Default constructor
	public TranslateApp(Database database) {
		if (database != null) {
			this.database = database;
		}
		else {
			System.out.print("\nThe database of abbreviations cannot be found.");
			System.out.println("\nPlease ensure that the 'abbreviations.txt' file is in the same directory as the program before proceeding!");
		}
	}

	/*
	 * @Method for reading in a file the check for abbreviations used
	 * @The abbreviation meanings are written, with the entire text transcript, to a temporary string buffer
	 * @Further processing is done on the string buffer to format the output with respect to the maximum line width
	 */
	public int translateFile(String input_filename, String output_filename, int max_linewidth) throws IOException, FileNotFoundException {
		int translated = -1;

		// Double check that the line width is at least a minimum of 30 characters
		if (max_linewidth < 30) {
			System.out.println("\nEnter a minimum line width value of at least 30 characters!");
		}
		else {
			try {
				/*
				 * @Create a new data input stream using a series of wrapper classes to cut down on IO operations
				 * @Data is read in from the file as tokens to converse system resources
				 * @The buffered reader is created separately so that it can be explicitly closed
				 */
				bin = new BufferedReader(new FileReader(new File(input_filename)));
				input = new StreamTokenizer(bin);

				// Set boolean flags for special delimiters
				input.eolIsSignificant(true);
				input.ordinaryChar('/');
				input.ordinaryChar(',');
				input.ordinaryChar(' ');
				input.ordinaryChar('"');

				// Declared here are local scope variables to be used
				Abbreviation searchResult = null;
				StringBuffer outputHolder = new StringBuffer();
				Double someNumber;

				/*
				 * @Loop through the entire input file just once, reading one token at a time
				 * @The string tokens are specifically checked if they are abbreviations
				 * @If so, the abbreviation meaning is retrieved and appended next to its name in the text transcript
				 * @The entire output text is temporarily stored in a string buffer until it is written out
				 */
				while (input.nextToken() != StreamTokenizer.TT_EOF) {

					// Action to take if token is a string type
					if (input.ttype == StreamTokenizer.TT_WORD) {

						// Append first, check later
						outputHolder.append(input.sval);

						// Check if the string is an abbreviation
						if (isAbbreviation(input.sval)) {

							// Search for the existing abbreviation
							searchResult = database.queryByAbbrName(input.sval);

							// Code block for the possibility that the search returns a result
							if (searchResult != null) {
								appendAbbreviations(outputHolder, searchResult);

								// Reset the translation count if adding the first translation since it is initialized with the default error value of -1
								if (translated < 0) {
									translated = 0;
								}
								++translated;
							}
						}
					}

					// Action to take is token is a number
					else if (input.ttype == StreamTokenizer.TT_NUMBER) {
						someNumber = input.nval;
						outputHolder.append(someNumber.intValue());
					}

					// Action to take if token is a ',' 
					else if (input.ttype == KeyEvent.VK_COMMA) {
						outputHolder.append(",");
					}

					// Action to take if token is a '/'
					else if (input.ttype == KeyEvent.VK_SLASH) {
						outputHolder.append("/");
					}

					// Action to take if token is a whitespace
					else if (input.ttype == KeyEvent.VK_SPACE) {
						outputHolder.append(" ");
					}

					// Action to take if token is a '
					else if (input.ttype == KeyEvent.VK_RIGHT) {
						outputHolder.append("'");
						translated = digDeeper(outputHolder, input.sval, translated);
					}

					// Action to take if token is end of line
					else if (input.ttype == StreamTokenizer.TT_EOL) {
						outputHolder.append("\n");
					}
				}

				// Call another method to handle writing the string buffer to the output file
				writeToFile(outputHolder, max_linewidth, output_filename);
			}
			finally {
				/*
				 * @Unlike most other variables, the buffered reader and writer continue to hog system resources regardless of the success of the IO operation
				 * @They are therefore closed here explicitly to free system resources
				 */
				if (bin != null) {
					bin.close();
				}
				if (bout != null) {
					bout.close();
				}
			}
		}
		return translated;
	}

	/*
	 * @Method that checks if a string token is an abbreviation
	 * @Abbreviations are usually entirely in uppercase letters
	 * @As there is a single character abbreviation, the first character onwards is checked
	 * @If any character is in lowercase, the check ends immediately
	 */
	public boolean isAbbreviation(String token) {
		boolean result = true;
		for (short i = 0; i < token.length(); ++i) {
			if (Character.isLowerCase(token.charAt(i))) {
				result = false;
				break;
			}
		}
		return result;
	}

	/*
	 * @Method that appends the string token stored in the stream tokenizer after a '
	 * @Also looks for additional abbreviations and appends them to the output string buffer
	 */
	private int digDeeper(StringBuffer outputHolder, String token, int translated) {

		// Create new temporary local variables
		StringTokenizer tokien = new StringTokenizer(token);
		String temp = null;

		/*
		 * @Loop through the mini-string, checking for abbreviations and appending their meanings as needed
		 * @Whitespace is manually appended since the string tokenizer is not set up to recognize them
		 */
		while (tokien.hasMoreTokens()) {
			temp = tokien.nextToken();
			outputHolder.append(temp);
			outputHolder.append(" ");

			// Action to take if an abbreviation is detected within the mini-string
			if (isAbbreviation(temp)) {
				outputHolder.deleteCharAt(outputHolder.length() - 1);
				appendAbbreviations(outputHolder, database.queryByAbbrName(temp));
				outputHolder.append(" ");
				
				// Reset the translation count if adding the first translation since it is initialized with the default error value of -1
				if (translated < 0) {
					translated = 0;
				}
				++translated;
			}
		}
		return translated;
	}

	/*
	 * @Method that appends the abbreviation full name(s) to the output string buffer
	 */
	private void appendAbbreviations(StringBuffer outputHolder, Abbreviation a) {
		outputHolder.append(" (");
		for (int i = 0; i < a.getNumFullNames(); ++i) {
			if (i > 0) {
				outputHolder.append(" or ");
			}
			outputHolder.append(a.getFullName(i));
		}
		outputHolder.append(")");
	}

	/*
	 * @Method that writes the translated text to an output file
	 * @Does some processing on the string buffer passed in and writes out, with respect to the maximum line width,
	 * the nicely formatted translated text
	 * @Code base is very heavily recycled from the translateFile method
	 */
	private void writeToFile(StringBuffer output, int limit, String output_filename) throws IOException  {

		/*
		 * @Create a new data input stream using a string reader, which wraps around the translated text
		 * @The string reader is then wrapped with a stream tokenizer to read the text as various tokens
		 * 
		 * @A new buffered writer is also wrapped around a file writer
		 * @The file writer itself wraps around a file object representing the output file
		 * @Since user has already given permission in the main method, any data in the output file is directly overwritten
		 */
		input = new StreamTokenizer(new StringReader(output.toString()));
		bout = new BufferedWriter(new FileWriter(new File(output_filename)));

		// Set boolean flags for special delimiters
		input.eolIsSignificant(true);
		input.ordinaryChar('/');
		input.ordinaryChar(',');
		input.ordinaryChar(' ');
		input.ordinaryChar('"');
		input.ordinaryChar('(');
		input.ordinaryChar(')');
		input.ordinaryChar('-');
		input.ordinaryChar('.');

		// Declared here are local scope variables to be used
		StringBuffer outputHolder = new StringBuffer();
		int previousBlank = 0;
		Integer numberHolder;
		Double someNumber;

		/*
		 * @Loop through the entire output text just once, reading one token at a time
		 * @Each token is appended to a temporary string buffer
		 * @At the end of every loop, the string buffer is checked to make sure it has not exceeded the number of characters permitted
		 * @If it does exceed, the string buffer is written to the output file up until the previous occurring whitespace 
		 * @Whatever which was just written out is deleted from the string buffer before the loop continues
		 * @Only new paragraphs are respected
		 */
		while (input.nextToken() != StreamTokenizer.TT_EOF) {

			// Action to take if token is a string type
			if (input.ttype == StreamTokenizer.TT_WORD) {
				outputHolder.append(input.sval);

				// If there is no whitespace after this string, manually append it
				input.nextToken();
				if ((input.ttype == StreamTokenizer.TT_WORD) || (input.ttype == StreamTokenizer.TT_NUMBER) || (input.ttype == KeyEvent.VK_DOWN)) {
					outputHolder.append(' ');
				}
				// Make the pointer reference stationary so that next token can be read like normal
				input.pushBack();
			}

			// Action to take is token is a number
			else if (input.ttype == StreamTokenizer.TT_NUMBER) {
				someNumber = input.nval;
				numberHolder = someNumber.intValue();
				outputHolder.append(numberHolder);

				// If there is no whitespace between a number and the next (, manually append it
				input.nextToken();
				if (input.ttype == KeyEvent.VK_DOWN) {
					outputHolder.append(' ');
				}
				// Make the pointer reference stationary so that next token can be read like normal
				input.pushBack();
			}

			// Action to take if token is ','
			else if (input.ttype == KeyEvent.VK_COMMA) {
				outputHolder.append(',');
				input.nextToken();

				// If there is no whitespace between a comma and the next word, number or end of line delimiter, manually append it
				if ((input.ttype == StreamTokenizer.TT_WORD) || (input.ttype == StreamTokenizer.TT_NUMBER) || (input.ttype == StreamTokenizer.TT_EOL)) {
					outputHolder.append(' ');
				}
				// Make the pointer reference stationary so that next token can be read like normal
				input.pushBack();
			}

			// Action to take if token is '/'
			else if (input.ttype == KeyEvent.VK_SLASH) {
				outputHolder.append('/');
			}

			// Action to take if token is a whitespace
			else if (input.ttype == KeyEvent.VK_SPACE) {
				outputHolder.append(' ');
			}

			// Action to take if token is '
			else if (input.ttype == KeyEvent.VK_RIGHT) {
				outputHolder.append("'");
				outputHolder.append(input.sval);
			}

			// Action to take if token is '-'
			else if (input.ttype == KeyEvent.VK_MINUS) {
				outputHolder.append('-');
			}

			// Action to take if token is '('
			else if (input.ttype == KeyEvent.VK_DOWN) {
				outputHolder.append('(');
			}

			// Action to take if token is '.'
			else if (input.ttype == KeyEvent.VK_PERIOD) {
				outputHolder.append(".");

				// If there is no whitespace between a period and the next word or number, manually append it
				input.nextToken();
				if ((input.ttype == StreamTokenizer.TT_WORD) || (input.ttype == StreamTokenizer.TT_NUMBER)) {
					outputHolder.append(' ');
				}
				// Make the pointer reference stationary so that next token can be read like normal
				input.pushBack();
			}

			// Action to take if token is ')'
			else if (input.ttype == 41) {
				outputHolder.append(')');
			}

			// Action to take if token is end of line
			else if (input.ttype == StreamTokenizer.TT_EOL) {

				// Read next token in advance
				input.nextToken();

				// Check for signs of a new paragraph and append accordingly
				if (input.ttype == StreamTokenizer.TT_EOL) {
					bout.write(outputHolder.toString());
					bout.newLine();
					bout.newLine();
					outputHolder.append("\n\n");
					outputHolder.delete(0, outputHolder.length());
				}

				// Make the pointer reference remain stationary so that the next token can be read like normal
				input.pushBack();
			}

			// Check that the string buffer has not exceeded the maximum number of characters allowed
			if (outputHolder.length() > limit) {
				previousBlank = outputHolder.lastIndexOf(" ", limit);
				bout.write(outputHolder.substring(0, previousBlank + 1));
				bout.newLine();
				outputHolder.delete(0, previousBlank + 1);
			}
		}

		/*
		 * @As the loop exits upon hitting the end of file token, the final line of text is always not written out
		 * @Fortunately that text is already saved to the control variables
		 * @This line of code writes that last line of text out to file
		 */
		bout.write(outputHolder.toString());
	}
}