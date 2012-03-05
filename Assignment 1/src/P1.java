/**
 * @Student Name: Desmond Poh Lik Meng
 * @Lab Group: BCG1
 */

/*
 * Importing the non default classes used in the program
 */
import java.util.Scanner;
import java.lang.Double;

public class P1 {

	/**
	 * @Global constants
	 */
	private static final double discount = 0.85;
	private static final double GST = 0.07;
	private static final double luxuryTax = 0.10;
	private static final int[] COE = new int[5];

	/**
	 * @Global variables
	 */
	private static Scanner input;
	private static short category;
	private static String inputHolder;
	private static double listPrice, discountPrice, grossPrice, taxes;

	public static void main(String[] args) {

		/*
		 *  The sentinel value of -1 for either of the either prompted input is
		 *  used to end this program
		 */

		listPrice = 0;
		category = 0;

		// Initialize the Scanner object and COE price array
		input = new Scanner(System.in);
		initializeCOE();

		// Start looping infinitely and asking user for required inputs

		for (; listPrice != -1 || category != -1;) {

			try {

				/* 
				 * Read vehicle list price from the user then check for
				 * invalid ranges
				 */
				System.out.print("\nEnter the vehicle's list price: $");
				inputHolder = input.nextLine();
				listPrice = Double.parseDouble(inputHolder);

				if (listPrice == -1) {
					System.out.println("\nPrograming terminating...");
					break;
				} 
				else if (listPrice < 1) {

					// Prompt user to enter valid list price
					System.out.println("\nEnter a list price more than $0!");
				} 
				else {

					/* 
					 * Read vehicle category from the user then check for
					 * invalid range
					 */
					System.out.print("\nEnter the vehicle's category (1 to 5): ");
					inputHolder = input.nextLine();
					category = Short.parseShort(inputHolder);

					if (category == -1) {
						System.out.println("\nPrograming terminating...");
						break;
					} 
					else if (category < 1 || category > 5) {

						// Prompt user to enter valid list price
						System.out.println("\nEnter a category between 1 and 5!");
					}
					else {
						/* 
						 * Start calculating the gross price upon determining correct
						 * input types & values
						 */
						calculate();
						// Print out the gross price of vehicle
						System.out.println("\nYour actual cost of purchasing this vehicle is: ");
						System.out.println("Listed Price: $" + listPrice);
						System.out.println("Applicable Taxes: $" + taxes);
						System.out.println("Gross Price: $" + grossPrice);
					}
				}
			} catch (NumberFormatException e) {
				System.out.println("\nEnter a valid list price or vehicle category!");
			}
		}
	}

	/**
	 * @Method that initializes COE prices in the array
	 */
	public static void initializeCOE() {
		for (int i = 0; i < COE.length; i++) {
			switch (i) {
			case 0:
				COE[i] = 18000;
				break;
			case 1:
				COE[i] = 15000;
				break;
			case 2:
				COE[i] = 13500;
				break;
			case 3:
				COE[i] = 11000;
				break;
			case 4:
				COE[i] = 600;
				break;
			}
		}
	}

	/**
	 * @Method that calculates gross price of the vehicle
	 */
	public static void calculate() {
		
		discountPrice = listPrice * discount;

		// Calculation for luxury priced vehicles
		if (listPrice > 120000) {
			taxes = ((discountPrice - 120000) * luxuryTax) + (discountPrice * GST) + COE[category - 1];
		}
		// Calculation for non-luxury priced vehicles
		else {
			taxes = (discountPrice * GST) + COE[category - 1];
		}
		grossPrice = discountPrice + taxes;
	}
}