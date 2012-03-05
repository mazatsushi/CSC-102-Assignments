/**
 * @Student Name: Desmond Poh Lik Meng
 * @Lab Group: BCG1
 */

// Import classes not included in the default Java environment
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.Arrays;

/*
 * @Exception handling for this class is handled by itself
 * @This is to provide more granular control over the type of error messages displayed
 */
public class Database {

	/*
	 * @Private attributes for the class
	 */
	private Abbreviation [] abbreviations;
	private BufferedReader br;
	private StreamTokenizer input;

	/*
	 * @Default constructor
	 * @Accepts the name of the file containing the entire list of abbreviations
	 * @List of abbreviations is reconstructed, saved and stored on RAM while the main program runs
	 * @Does not create new abbreviations, and thus no need to save the list of abbreviations upon exiting
	 */
	public Database(String filename) throws IOException, NullPointerException {
		try {			
			/*
			 * @Create a new data input stream using a buffered reader
			 * @This buffered reader wraps around the resource-intensive file reader class
			 * @The file reader in turn wraps around a file class that represents the abbreviations database
			 * @The buffered reader is then wrapped with a stream tokenizer to read input as various tokens
			 * @Since the buffered reader is initialized separately, it can be closed once not needed
			 */
			br = new BufferedReader(new FileReader(new File(filename)));
			input = new StreamTokenizer(br);

			// Set boolean flags for special delimiters
			input.eolIsSignificant(true);
			input.ordinaryChar('"');

			// Declared here are local scope variables and flags to be used
			ArrayList<Abbreviation> al = new ArrayList<Abbreviation>();
			String abbr = null;
			StringBuffer abbrName = new StringBuffer();
			boolean isNewLine = true, isAfterHyphen = false;

			/*
			 * @Loop through the entire input file just once, reading one token at a time
			 * @The tokens are used to internally create a new abbreviation object once there is sufficient information
			 * @The entire database of abbreviations is temporarily stored in an array list while being reconstructed 
			 * @After reconstruction, the array list is converted into an abbreviation object array and saved
			 */
			while (input.nextToken() != StreamTokenizer.TT_EOF) {
				
				// Action to take if token is a string
				if (input.ttype == StreamTokenizer.TT_WORD) {

					// If the string is after a new line, then it is an abbreviation
					if (isNewLine) {
						abbr = new String(input.sval);
						isNewLine = false;
					}
					else {
						
						// If the string is after a '-', then it is the start of the abbreviation's name
						if (isAfterHyphen) {
							abbrName.append(input.sval);
							isAfterHyphen = false;
						}
						else {
							abbrName.append(" ");
							abbrName.append(input.sval);
						}
					}
				}

				// Action to take if token is a '-'
				else if (input.ttype == KeyEvent.VK_MINUS) {
					isAfterHyphen = true;
				}

				// Action to take if token is a ','
				else if (input.ttype == KeyEvent.VK_COMMA) {
					abbrName.append(",");
				}

				// Action to take if token is a '
				else if (input.ttype == KeyEvent.VK_RIGHT) {
					abbrName.append("'");
					abbrName.append(input.sval);
				}

				// Action to take if token is a new line
				else if (input.ttype == StreamTokenizer.TT_EOL) {
					add(al, abbr, abbrName);
					abbr = null;
					abbrName = new StringBuffer();
					isNewLine = true;
					isAfterHyphen = false;
				}
			}

			/*
			 * @As the loop exits upon hitting the end of file token, the last token is always not added
			 * @Fortunately the information needed to the last abbreviation object is already saved to the control variables
			 * @This line of code adds that last object to the array list
			 */
			add(al, abbr, abbrName);

			/*
			 * @Convert and store the array list of abbreviations into the attribute
			 * @The entire list is sorted as a prerequisite for the search algorithm implemented
			 */
			abbreviations = new Abbreviation[al.size()];
			al.toArray(abbreviations);
			sort(abbreviations, 0, abbreviations.length - 1);
		}
		
		// This exception is thrown when the abbreviation list file cannot be found
		catch (FileNotFoundException e) {
			System.out.print("\nCannot find \"abbreviations.txt\"!");
			System.out.print("\nPlease ensure that \"abbreviations.txt\" is in the same directory as the program before trying again.");
			System.out.print("\nThe program will now terminate.\n");
			System.exit(1);
		}
		
		// This exception is thrown when some error occurs while reading in from the abbreviation list
		catch (IOException e) {
			System.out.print("\nAn error occured while performing a file read from \"abbreviations.txt\".");
			System.out.print("\nPossilities for this error is due to the following: ");
			System.out.print("\n 1) Failure or interrupted read operation while reading from " + filename);
			System.out.print("\nPlease contact your system administrator for further assistance.");
		}
		
		finally {
			/*
			 * @Unlike most other variables, the buffered reader continues to hog system resources regardless of the success of the IO operation
			 * @It is therefore closed here explicitly to free system resources
			 */
			if (br != null) {
				br.close();
			}
		}
	}

	/*
	 * @Method that adds a new abbreviation object to the array list during reconstruction
	 * @First checks if the object to be added actually exists within the list
	 * @If exists, simply add a new name to the existing object
	 * @If does not exist, create and add to a new object to the list
	 */
	private void add(ArrayList<Abbreviation> al, String abbr, StringBuffer abbrName) {
		
		// Check for an empty array list, valid only for the first object added
		if (al.isEmpty()) {
			al.add(new Abbreviation(abbr, abbrName.toString()));
		}
		else {
			// Check to see if any previous abbreviation is the same
			int index = exists(al, abbr);
			if (index != -1) {
				Abbreviation temp = al.get(index);
				temp.addFullName(abbrName.toString());
			}
			else {
				al.add(new Abbreviation(abbr, abbrName.toString()));
			}
		}
	}

	/*
	 * @Method that searches for an existing abbreviation during attribute reconstruction
	 * @Unfortunately as the array list is not yet sorted at this point of time the search has to done sequentially
	 * @This on top of being called once for every new abbreviation object added to the array list
	 * @Efforts will be made to implement the insertion sort algorithm in future to try reduce this overhead
	 */
	private int exists(ArrayList<Abbreviation> al, String abbr) {
		int index = -1;
		for (int i = 0; i < al.size(); ++i) {
			if (abbr.compareToIgnoreCase(al.get(i).getAbbrName()) == 0) {
				index = i;
				break;
			}
		}
		return index;
	}

	/*
	 * @Method that returns the total number of abbreviations
	 */
	public int getNumAbbreviations() {
		return (abbreviations.length);
	}

	/*
	 * @Method that returns an Abbreviation object from a valid index
	 */
	public Abbreviation getAbbreviationByIndex(int index) {
		Abbreviation a = null;
		if ((getNumAbbreviations() > 0) && (index > -1) && (index < abbreviations.length)) {
			a = new Abbreviation(abbreviations[index]);
		}
		return a;
	}

	/*
	 * @Method that prints all abbreviations in the array
	 */
	public void printAllAbbreviations() {
		if (getNumAbbreviations() < 1) {
			System.out.println("\nThere are no abbreviations to print!");
		}
		else {
			System.out.print("\nAll available abbreviations (in alphabetical order): ");
			for (int i = 0; i < getNumAbbreviations(); ++i) {
				System.out.print("\n" + abbreviations[i].getAbbrName());
			}
			System.out.print("\n");
		}
	}

	/*
	 * @Recursive method that sorts the elements in the attribute after initialization
	 * @Calls another method that implements the actual sorting algorithm
	 */
	private void sort(Abbreviation [] a, int start, int end) {
		int index = quickSort(a, start, end);
		if (index - 1 > start) {
			sort(a, start, index - 1);
		}
		if (index < end) {
			sort(a, index, end);
		}
	}

	/*
	 * @Method that sorts the elements in the attribute using quick sort
	 */
	private int quickSort(Abbreviation [] a, int start, int end) {
		int left = start;
		int right = end;
		Abbreviation ab = abbreviations[(start + end) / 2];
		while (left <= right) {

			// Keep advancing so long as current string is smaller than pivot string
			while ((abbreviations[left].getAbbrName().compareToIgnoreCase(ab.getAbbrName())) < 0) {
				++left;
			}

			// Keep retreating so long as current string is larger than pivot string
			while ((abbreviations[right].getAbbrName().compareToIgnoreCase(ab.getAbbrName())) > 0){
				--right;
			}

			/*
			 * @At this point, left string is larger in value than pivot string
			 * @At this point, right string is also larger in value than pivot string
			 * @Check that left the left string position is smaller than right string position
			 * @All three conditions fulfilled means that both strings should be swapped
			 */
			if (left <= right) {
				Abbreviation temp = abbreviations[right];
				abbreviations[right] = abbreviations[left];
				abbreviations[left] = temp;
				++left;
				--right;
			}
		}
		return left;
	}

	/*
	 * @Method that searches for an exact match of the specified abbreviation
	 * @Calls another method that implements the actual search algorithm
	 * @The abbreviations attribute will already have been sorted beforehand
	 */
	public Abbreviation queryByAbbrName(String target) {
		// Placeholder for the pivot element as it is referenced more than once
		return BinarySearch(abbreviations, target, 0, (getNumAbbreviations() - 1));
	}

	/*
	 * @Recursive method that searches for exact abbreviation match using binary search
	 * @Initial UML design hints at using sequential search which has O(n) performance
	 * @This method can do the same in O(log n) performance
	 */
	private Abbreviation BinarySearch(Abbreviation [] abbr, String target, int start, int end) {

		// Code block for possibility of not finding anything, starting from next line
		if (start > end) {
			return null;
		}

		// Code block for possibility of finding target value
		int pivot = (start + end) / 2;

		/*
		 * @Placeholder for whether target value is the same, larger, smaller than compared value
		 * @Declared and used as the variable is reference more than once
		 */
		int comparator = target.compareToIgnoreCase(abbreviations[pivot].getAbbrName());

		// Exact match found
		if (comparator == 0) {
			return (new Abbreviation(abbreviations[pivot]));
		}

		// Target value is to the right of the pivot
		else if (comparator > 0) {
			return BinarySearch(abbr, target, (pivot + 1), end);
		}

		// Target value is to the left of the pivot
		else {
			return BinarySearch(abbr, target, start, (pivot - 1));
		}
	}

	/*
	 * @Method that returns all abbreviations whose prefix matches the argument
	 * @Array is searched through sequentially as there can be more than one result
	 * @To reduce overhead, in future maybe this portion can use multi-threaded access
	 */
	public Abbreviation [] queryByAbbrPrefix(String abbrPrefix) {

		// Code block to identify the first and last elements of the matching prefixes
		int end = -2, start = -1;
		for (int i = 0; i < getNumAbbreviations(); ++i) {
			if (abbreviations[i].matchByAbbrPrefix(abbrPrefix)) {

				// Start element has not yet been identified
				if (start < 0) {
					start = end = i;
				}

				// Start element has already been identified
				else {
					end = i;
				}
			}
		}
		if (!(start > end)) {
			return Arrays.copyOfRange(abbreviations, start, end + 1);
		}
		return null;
	}
}