/**
 * @Student Name: Desmond Poh Lik Meng
 * @Lab Group: BCG1
 */

// Import classes not included in the default Java environment
import java.util.Arrays;

public class Abbreviation {

	/*
	 * @Private attributes for the class
	 */
	private String abbrName;
	private String [] fullNames;

	/*
	 * @Default constructor. Creates a new object by receiving input from user
	 */
	public Abbreviation (String abbrName, String fullName) {
		this.abbrName = abbrName;
		fullNames = new String[1];
		this.fullNames[0] = fullName;
	}

	/*
	 * @Ad-hoc constructor. Creates a new object by copying data from another object of the same type
	 */
	public Abbreviation (Abbreviation abbr) {
		this.abbrName = abbr.getAbbrName();

		// Placeholder for the length of the fullNames array as it is referenced more than once
		int length = abbr.getNumFullNames();
		this.fullNames = new String [length];
		for (int i = 0; i < length; ++i) {
			this.fullNames[i] = abbr.getFullName(i);
		}
	}

	/*
	 * @Method that returns the abbreviation name
	 */
	public String getAbbrName() {

		// Create a new String object and return the pointer to call stack
		return (new String(abbrName));
	}

	/*
	 * @Method that returns the number of names that the abbreviation can mean
	 */
	public int getNumFullNames() {
		return (fullNames.length);
	}


	/*
	 * @Method that returns the full name of the abbreviation at specified index
	 */
	public String getFullName(int index) {

		// Create a new String object and return the pointer to call stack 
		return (new String(fullNames[index]));
	}


	/*
	 * @Method that returns a boolean value for an exact match of an abbreviation
	 * @Redundant since sequential search is not utilized
	 * @Implemented to fulfill UML class design requirement
	 */
	public boolean matchByAbbrName(String abbrName) {
		boolean exactMatch = false;

		/*
		 * @Compare the abbreviation name with the argument, ignoring both lettering cases
		 * @If the argument and abbreviation are exactly the same, API call will return zero
		 */
		if (abbrName.compareToIgnoreCase(abbrName) == 0) {
			exactMatch = true;
		}
		return exactMatch;
	}


	/*
	 * @Method that returns a boolean value for a prefix match of an abbreviation
	 */
	public boolean matchByAbbrPrefix(String abbrPrefix) {
		boolean prefixMatch = false;

		/* 
		 * @Find the argument's substring within the abbreviation
		 * @If it is a prefix, then the first character will always be zero
		 */
		if (abbrName.indexOf(abbrPrefix) == 0) {
			prefixMatch = true;
		}
		return prefixMatch;
	}

	/*
	 * @Method that adds a new full name to the array
	 */
	public void addFullName(String fullName) {

		/*
		 * @Create and copy a resized array of the fullNames attribute
		 * @The final element is padded with null and must be replaced by the argument thereafter
		 */
		this.fullNames = (Arrays.copyOf(fullNames, fullNames.length + 1));
		this.fullNames[fullNames.length - 1] = fullName;
	}


	/*
	 * @Method that prints all full names that the abbreviation can mean
	 */
	public void print() {
		System.out.print("\nAbbreviation: [" + abbrName + "]");
		for (int i = 0; i < fullNames.length; ++i) {
			System.out.print("\n\t[" + fullNames[i] + "]");
		}
		// Print an empty line
		System.out.print("\n");
	}
}