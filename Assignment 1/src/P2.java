/**
 * @Student Name: Desmond Poh Lik Meng
 * @Lab Group: BCG1
 */

/*
 * Importing the non default classes used in the program
 */
import java.util.Scanner;
import java.lang.Long;

public class P2 {

	/**
	 * @Global variables
	 */
	private static Scanner input;
	private static String inputHolder;
	private static long number;

	public static void main(String[] args) {

		/*
		 *  The sentinel value of -1 for either of the either prompted input is
		 *  used to end this program
		 */

		number = 0;

		// Initialize the Scanner object
		input = new Scanner(System.in);

		// Start looping infinitely and asking user for required inputs

		for (; number != -1;) {

			try {

				/* 
				 * Read vehicle list price from the user then check for
				 * invalid ranges
				 */
				System.out.print("\nEnter a number: ");
				inputHolder = input.nextLine();
				number = Long.parseLong(inputHolder);
				
				// Check that entered value range is correct
				if (number == -1) {
					System.out.println("\nPrograming terminating...");
				}
				else if (number < -1) {

					// Prompt user to enter valid list price
					System.out.println("\nEnter a number value of at least 0!");
				} 
				else if (number > -1) {
					// Start calculating
					System.out.println("\nResult: " + calculate(number));
				}
			}
			catch (NumberFormatException e) {
				System.out.println("\nNot an integer number, try again.");
			}
		}
	}

	/**
	 * @Recursive method that calculates factorial
	 */
	public static long calculate(long number) {
		long result = 1;
		if (number > 1) {
			result = number * (calculate(--number));
		}
		return result;
	}
}