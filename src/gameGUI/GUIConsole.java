package gameGUI;

import java.util.Scanner;

import gameLogic.Controller;
import gameLogic.IStateOBJ;
import gameLogic.MiscTools;

public class GUIConsole implements IGameGUI{

	private Controller controller;
	private Scanner scan = new Scanner(System.in);
	private int player1Type = 0;
	private int player2Type = 0;
	private String player1Name = "";
	private String player2Name = "";
	private int difficulty1 = 0;
	private int difficulty2 = 0;
	private int gameMode = 0;
	private int x = 0;
	private int y = 0;
	private IStateOBJ stateOBJ = null;
	private int game = 0;
	private boolean isGameOver = false;
	private int p1Algo = 0;
	private int p2Algo = 0;

	@Override
	public void startGameGui(Controller control) {
		controller = control;
		System.out.println("Console GUI start");
		boolean isNotValid = true;
		while(isNotValid){ // decide if we play the full game or use some presets
			System.out.println("press 0 for full game, 1 for presets");
			String gameS = scan.nextLine();
			try {
				game = Integer.parseInt(gameS);
				if(game == 0 || game == 1){
					System.out.println("game is: " + game);
					isNotValid = false;
				}else{
					System.out.println("game must be 0 or 1");
					isNotValid = true;
				}
			} catch (Exception e) {
				System.out.println("input: " + gameS + " is incorrect, input must be a number!");
				isNotValid = true;
			}
		}

		if(game == 0){ // start normal game
			fullGame(); // start a game where one sets up the game manually
		}
		else if(game == 1){ // start a preset game
			presetGame(); // only works with computer vs computer
		}
	}

	@Override
	public void setGUIBoard(boolean[][] GUIGrid, IStateOBJ state, int gameOverResult) {
		stateOBJ = state;
		if(game == 0)
			System.out.println(MiscTools.debugPrintBoard(state));
	}

	@Override
	public void endGame(int gameOverResult) {
		isGameOver = true;
		if(game == 0)
			System.out.println("game over. result is: " + gameOverResult);
	}


	private void presetGame() { // only works with computer vs computer
		// edit these values for testing:
		int iterationCount = 5;
		player1Type = 2;
		player2Type = 2;
		player1Name = "preset-AI1";
		player2Name = "preset-AI2";
		difficulty1 = 3;
		difficulty2 = 3;
		gameMode = 3;
		x = 7;
		y = 6;
		p1Algo = 1;
		p2Algo = 6;

		System.out.println("Starting a preset game: difficulty[" + difficulty1 + ", " + difficulty2 + "], gameMode: " + gameMode + ", x" + x + ", y" + y + ", algorithm[" + p1Algo + ", " + p2Algo + "]");
		for (int i = 1; i <= iterationCount; i++) {
			boolean b = controller.startGame(difficulty1, difficulty2, x, y, player1Name, player2Name, player1Type, player2Type, gameMode, p1Algo, p2Algo);
			if(b){
				controller.move(-1);
				System.out.print(" " + i);
			}else{
				System.out.println("preset startGame failed to start...");
			}
		}
		System.out.println("\n\n--------- console test over ---------");
		System.out.println("setup: difficulty[" + difficulty1 + ", " + difficulty2 + "], gameMode: " + gameMode + ", x" + x + ", y" + y + ", algorithm[" + p1Algo + ", " + p2Algo + "]");
		System.out.println("number of games: " + controller.getNumOfPlayedGames());
		System.out.println("number of draws: " + controller.getNumOfDraws());
		System.out.println(player1Name + " wins: " + controller.getPlayerNumWins(1));
		System.out.println(player2Name + " wins: " + controller.getPlayerNumWins(2));
		controller.resetCurrentStat();
	}

	private void fullGame(){
		while(true){ // loop to enable more games than one
			isGameOver = false;

			// -- set up game --

			boolean isNotValid = true;
			while(isNotValid){
				System.out.println("player1 - human or computer? 1 or 2");
				String player1TypeS = scan.nextLine();
				try {
					player1Type = Integer.parseInt(player1TypeS);
					if(player1Type == 1 || player1Type == 2){
						System.out.println("player1 type is: " + player1Type);
						isNotValid = false;
					}else{
						System.out.println("type must be 1 or 2");
						isNotValid = true;
					}
				} catch (Exception e) {
					System.out.println("input: " + player1TypeS + " is incorrect, input must be a number!");
					isNotValid = true;
				}
			}

			isNotValid = true;
			while(isNotValid){
				System.out.println("player2 - human or computer? 1 or 2");
				String player2TypeS = scan.nextLine();
				try {
					player2Type = Integer.parseInt(player2TypeS);
					if(player2Type == 1 || player2Type == 2){
						System.out.println("player2 type is: " + player2Type);
						isNotValid = false;
					}else{
						System.out.println("type must be 1 or 2");
						isNotValid = true;
					}
				} catch (Exception e) {
					System.out.println("input: " + player2TypeS + " is incorrect, input must be a number!");
					isNotValid = true;
				}
			}

			if(player1Type == 2){
				isNotValid = true;
				while(isNotValid){
					System.out.println("choose difficulty 1, 2, 3 for player 1");
					String difficultyS = scan.nextLine();
					try {
						difficulty1 = Integer.parseInt(difficultyS);
						if(difficulty1 == 1 || difficulty1 == 2 || difficulty1 == 3){
							System.out.println("difficulty is: " + difficulty1);
							isNotValid = false;
						}else{
							System.out.println("difficulty must be 1, 2 or 3");
							isNotValid = true;
						}
					} catch (Exception e) {
						System.out.println("input: " + difficultyS + " is incorrect, input must be a number!");
						isNotValid = true;
					}
				}
			}
			
			if(player2Type == 2){
				isNotValid = true;
				while(isNotValid){
					System.out.println("choose difficulty 1, 2, 3 for player 2");
					String difficultyS = scan.nextLine();
					try {
						difficulty2 = Integer.parseInt(difficultyS);
						if(difficulty2 == 1 || difficulty2 == 2 || difficulty2 == 3){
							System.out.println("difficulty is: " + difficulty2);
							isNotValid = false;
						}else{
							System.out.println("difficulty must be 1, 2 or 3");
							isNotValid = true;
						}
					} catch (Exception e) {
						System.out.println("input: " + difficultyS + " is incorrect, input must be a number!");
						isNotValid = true;
					}
				}
			}

			player1Name = (player1Type==1)?"player1":controller.getName(difficulty1, true, 1);//"computer1";
			player2Name = (player2Type==1)?"player2":controller.getName(difficulty2, true, 2);//"computer2";
			if(!(player1Type == 2 && player2Type == 2)){ // skip naming when there are two computers
				isNotValid = true;
				while(isNotValid){
					if(!(player1Type == 2)){
						System.out.println("give player 1 a name.");
						String holder1 = scan.nextLine();
						if(!holder1.isEmpty()){
							player1Name = holder1;
						}
					}if(!(player2Type == 2)){
						System.out.println("give player 2 a name.");
						String holder2 = scan.nextLine();
						if(!holder2.isEmpty()){
							player2Name = holder2;
						}
					}
					if(player1Name.equals(player2Name)){
						System.out.println("playernames must not be equal");
						isNotValid = true;
					}else{
						System.out.println("player1 name is: " + player1Name);
						System.out.println("player2 name is: " + player2Name);
						isNotValid = false;
					}
				}
			}

			if(player1Type == 2){
				isNotValid = true;
				while(isNotValid){
					System.out.println("for player 1, choose an algorithm between 1 and " + controller.getNumberOfAlgorithms());
					String algorithmS = scan.nextLine();
					try {
						p1Algo = Integer.parseInt(algorithmS);
						if(p1Algo > 0 && p1Algo <= controller.getNumberOfAlgorithms()){
							System.out.println("algorithm is: " + p1Algo);
							isNotValid = false;
						}else{
							System.out.println("algorithm must be between 1 and " + controller.getNumberOfAlgorithms());
							isNotValid = true;
						}
					} catch (Exception e) {
						System.out.println("input: " + algorithmS + " is incorrect, input must be a number!");
						isNotValid = true;
					}
				}
			}else{p1Algo = 1;}

			if(player2Type == 2){
				isNotValid = true;
				while(isNotValid){
					System.out.println("for player 2, choose an algorithm between 1 and " + controller.getNumberOfAlgorithms());
					String algorithmS = scan.nextLine();
					try {
						p2Algo = Integer.parseInt(algorithmS);
						if(p2Algo > 0 && p2Algo <= controller.getNumberOfAlgorithms()){
							System.out.println("algorithm is: " + p2Algo);
							isNotValid = false;
						}else{
							System.out.println("algorithm must be between 1 and " + controller.getNumberOfAlgorithms());
							isNotValid = true;
						}
					} catch (Exception e) {
						System.out.println("input: " + algorithmS + " is incorrect, input must be a number!");
						isNotValid = true;
					}
				}
			}else{p2Algo = 1;}



			isNotValid = true;
			while(isNotValid){
				System.out.println("choose gamemode between 1 and " + controller.getNumberOfGames());
				String gameModeS = scan.nextLine();
				try {
					gameMode = Integer.parseInt(gameModeS);
					if(gameMode > 0 && gameMode <= controller.getNumberOfGames()){
						controller.setGameMode(gameMode);
						controller.setAlgorithm(p1Algo, p2Algo);
						System.out.println("gameMode is: " + gameMode + ": " + controller.getGameModeName());
						isNotValid = false;
					}else{
						System.out.println("gameMode must be between 1 and " + controller.getNumberOfGames());
						isNotValid = true;
					}
				} catch (Exception e) {
					System.out.println("input: " + gameModeS + " is incorrect, input must be a number!");
					isNotValid = true;
				}
			}

			isNotValid = true;
			while(isNotValid){
				System.out.println("choose x axis between " + controller.getMinGridX() + " and " + controller.getMaxGridX());
				String xS = scan.nextLine();
				try {
					x = Integer.parseInt(xS);
					if(x >= controller.getMinGridX() && x <= controller.getMaxGridX()){
						System.out.println("x is: " + x);
						isNotValid = false;
					}else{
						System.out.println("x must be between " + controller.getMinGridX() + " and " + controller.getMaxGridX());
						isNotValid = true;
					}
				} catch (Exception e) {
					System.out.println("input: " + xS + " is incorrect, input must be a number!");
					isNotValid = true;
				}
			}

			isNotValid = true;
			while(isNotValid){
				System.out.println("choose y axis between " + controller.getMinGridY() + " and " + controller.getMaxGridY());
				String yS = scan.nextLine();
				try {
					y = Integer.parseInt(yS);
					if(y >= controller.getMinGridY() && y <= controller.getMaxGridY()){
						System.out.println("y is: " + y);
						isNotValid = false;
					}else{
						System.out.println("y must be between " + controller.getMinGridY() + " and " + controller.getMaxGridY());
						isNotValid = true;
					}
				} catch (Exception e) {
					System.out.println("input: " + yS + " is incorrect, input must be a number!");
					isNotValid = true;
				}
			}

			// -- setup done -- 
			boolean playAgain = true;
			while(playAgain){
				playAgain = false;

				System.out.println("starting game from console");
				if(controller.startGame(difficulty1, difficulty2, x, y, player1Name, player2Name, player1Type, player2Type, gameMode, p1Algo, p2Algo)){
					controller.move(-1); // if it is computer vs computer then this is the only line of code that is needed
					if(!(player1Type == 2 && player2Type == 2)){ // if there is a human player
						while(!isGameOver){ // loop through moves
							isNotValid = true;
							int x = 0;
							int y = 0;
							while(isNotValid){
								System.out.println("x: ");
								String xS = scan.nextLine();
								System.out.println("y: ");
								String yS = scan.nextLine();
								try {
									x = Integer.parseInt(xS);
									y = Integer.parseInt(yS);
									if(stateOBJ.getGrid()[x][y] == 0){
										System.out.println("(" + x + "," + y + ")");
										controller.move(x + (y * stateOBJ.getGrid().length));
										isNotValid = false;
									}else{
										System.out.println("this move is not available");
										isNotValid = true;
									}
								} catch (Exception e) {
									System.out.println("input: " + xS + " and " + yS + " is incorrect, input must be a number!");
									isNotValid = true;
								}
							}
						}
					}
					System.out.println("game over");
				}
				System.out.println("press 0 to end, or 1 to play again, or any key to setup new game");
				String endCurrentGame = scan.nextLine();
				if(endCurrentGame.equals("0")){System.out.println("Terminating");return;}
				else if(endCurrentGame.equals("1")){playAgain = true;isGameOver = false;}
			}
		}
	}
}
