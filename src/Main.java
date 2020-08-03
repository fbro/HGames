import java.util.Scanner;

import gameLogic.Controller;

public class Main {
	
	private static Scanner scan = new Scanner(System.in);
	private static int gui = 0;
	private static final int NUMBER_OF_GUI = 3;
	
	public static void main(String[] args) {
		
		boolean isNotValid = true;
		
		// ----- skip ------
		isNotValid = false; // skip this step
		gui = 2; // just set gui to console==0 or GUIGameSwing==2
		// ----- skip ------
		
		while(isNotValid){
			System.out.println("press 0 for console, 1 for swing, 2 for game swing");
			String gameS = scan.nextLine();
			try {
				gui = Integer.parseInt(gameS);
				if(gui < NUMBER_OF_GUI && gui >= 0){
					System.out.println("GUI is: " + gui);
					isNotValid = false;
				}else{
					System.out.println("Gui must be between 0 and " + (NUMBER_OF_GUI-1));
					isNotValid = true;
				}
			} catch (Exception e) {
				System.out.println("input: " + gameS + " is incorrect, input must be a number!");
				isNotValid = true;
			}
		}
		
		Controller control = new Controller(gui);
		control.start();
	}
}