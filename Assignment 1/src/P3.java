/**
 * @Student Name: Desmond Poh Lik Meng
 * @Lab Group: BCG1
 */

/*
 * Importing the non default classes used in the program
 */
import java.util.Scanner;
import java.lang.Math;
import java.lang.Double;

public class P3 {
	
	/**
	 * @Global constants
	 */
	private static final double errorTolerance = 5e-5;

	/**
	 * @Global variables
	 */
	private static Scanner input;
	private static String inputHolder;
	private static double number, old, next, error, output;

	public static void main(String[] args) {
		
		// Initialize the Scanner object
		input = new Scanner(System.in);

		/*
		 *  The sentinel value of -1 for the input is used to end this program
		 */

		number = 0;

		// Start looping infinitely and asking user for required inputs

		for (; number != -1;) {

			try {

				/* 
				 * Read a number from the user then check for invalid ranges
				 */
				System.out.print("\nEnter a number: ");
				inputHolder = input.nextLine();
				number = Double.parseDouble(inputHolder);

				if (number == -1) {
					System.out.println("\nPrograming terminating...");
					break;
				} 
				else if (number < 1) {

					// Prompt user to enter a non-negative number
					System.out.println("\nCannot find square root of a negative number!");
				} 
				else {
					
					/* 
					 * Read approximation value from the user then check for
					 * invalid range
					 */
					System.out.print("\nEnter the appromixation: ");
					inputHolder = input.nextLine();
					old = Double.parseDouble(inputHolder);

					if (old == -1) {
						System.out.println("\nPrograming terminating...");
						break;
					} 
					else if (old < 1) {

						// Prompt user to enter valid approximation
						System.out.println("\nEnter a positive approximation!");
					}
					else {
						/* 
						 * Start calculating the square root of a number
						 */
						output = mySqrt(number, old);
						
						// Print out the square root value
						System.out.println("\nThe square root value by my own method: " + output);
						System.out.println("\nThe square root value by the Java Math class: " + Math.sqrt(number));
					}
				}
			} catch (NumberFormatException e) {
				System.out.println("\nEnter a valid number or approximation value!");
			}
		}
	}

	/**
	 * @Method that calculates square root value of a positive number
	 */
	public static double mySqrt(double number, double old) {
		
		// Square root of 0 is 0
		double sqrt = 0.0;
		
		// Square root of 1 is 1
		if (number == 1) {
			sqrt = 1.0;
		}
		else {
			
			// Calculate the next better approximation 
			next = ((old + (number / old)) / 2);
			
			// Calculate the relative error
			error = (Math.abs(next - old) / ((next + old) / 2));
			
			// If error is not within the tolerance range
			if (error > errorTolerance) {
				
				// Let the latest approximation be considered as old approximation
				old = next;
				
				// Calculate a new approximation
				next = ((next + (number / next)) / 2);
				sqrt = mySqrt(number, old);
			}
			// Error is within tolerance range, so latest approximation is square root value
			else {
				sqrt = next;
			}
		}
		return sqrt;
	}
}