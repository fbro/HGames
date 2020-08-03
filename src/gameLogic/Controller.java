package gameLogic;

import java.awt.Point;
import java.util.ArrayList;

import gameGUI.GUIConsole;
import gameGUI.GUIGameSwing;
import gameGUI.GUISwing;
import gameGUI.IGameGUI;
import gameLogicAlgorithms.AlgorithmEvaluation;
import gameLogicAlgorithms.AlgorithmMiniMax;
import gameLogicAlgorithms.AlgorithmMiniMaxNoEvaluation;
import gameLogicAlgorithms.AlgorithmRandomPick;
import gameLogicAlgorithms.AlgorithmReinforcementLearning;
import gameLogicAlgorithms.AlgorithmReinforcementLearningMiniMax;
import gameLogicAlgorithms.IGameAlgorithm;
import gameLogicTypes.*;

//the idea with this object is that it persists throughout the programs lifetime- but does not persist when the program is closed
public class Controller {

	// final numbers
	private final int NUMBER_OF_GAMES = MiscTools.GAME_NAMES.length; // TicTacToe, FourInLine, ConnectFour, Reversi, Chess, Checkers, Mills, Go
	private final int NUMBER_OF_ALGORITHMS = MiscTools.ALGORITHM_NAMES.length; // MiniMax, RandomPick, eval, minimax no eval, Reinforcement Learning, R. L. M. M.
	private final IGameGUI GAME_GUI;

	// player information
	private String player1Name = "Player1";
	private String player2Name = "Player2";
	private int player1Type = 1;
	private int player2Type = 1;
	private int player1NumWins = 0;
	private int player2NumWins = 0;
	private int playerTurn = 1; // this value is either 1 or 2 for player1 or player2

	private IGameAlgorithm algo1 = new AlgorithmMiniMax();
	private IGameAlgorithm algo2 = new AlgorithmMiniMax();
	private IGameType logic = new TypeTicTacToe();
	private int numOfPlayedGames = 0; // counter for how many games have been played
	private int round = 1; // what round the current game is in
	private int numOfDraws = 0;
	private int minGridX = 3;
	private int minGridY = 3;
	private int maxGridX = 3;
	private int maxGridY = 3;
	private int suggestedGridX = 3;
	private int suggestedGridY = 3;
	private int gameOverResult = 0; // processTurn updates this field
	private String gameRules = "";
	private String currentGameStats = "";
	private String blueIcons[] = {"/pictures/circle.png"};
	private String redIcons[] = {"/pictures/cross.png"};
	private String blueIconsSmall[] = {"/pictures/circleSmall.png"};
	private String redIconsSmall[] = {"/pictures/crossSmall.png"};
	private boolean D_N_D = false; // Semaphore used to ignore button pressed when computer is making its move
	private boolean hasGameEnded = false;

	public Controller(int gui){
		if(gui == 1){
			GAME_GUI = new GUISwing(); // swing user friendly environment !contains errors!
		}else if(gui == 2){
			GAME_GUI = new GUIGameSwing(); // 2d game swing user friendly environment
		}else{
			GAME_GUI = new GUIConsole(); // console test friendly environment
		}
	}

	public void start(){
		MiscTools.connectToADatabase();
		GAME_GUI.startGameGui(this);
	}

	public void setGameMode(int gameMode){
		switch (gameMode) {
		case -1:
			// if -1 is the gameMode then it means that the gui wants to update the algorithms only
			break;
		case 1: // Tic tac toe
			minGridX = 3;
			minGridY = 3;
			maxGridX = 3;
			maxGridY = 3;
			suggestedGridX = 3;
			suggestedGridY = 3;
			logic = new TypeTicTacToe();
			blueIcons = new String[]{"/pictures/circle.png"};
			redIcons  = new String[]{"/pictures/cross.png"};
			blueIconsSmall = new String[]{"/pictures/circleSmall.png"};
			redIconsSmall = new String[]{"/pictures/crossSmall.png"};
			gameRules = ""
					+ "Players take turn to place their tokens. The first player\n"
					+ "who has three of their tokens in a line wins the game.\n"
					+ "The board can only have the size of 3X3.";
			currentGameStats = logic.getStats();
			break;
		case 2: // Four in line
			minGridX = 4;
			minGridY = 4;
			maxGridX = 70;
			maxGridY = 32;
			suggestedGridX = 6;
			suggestedGridY = 6;
			logic = new TypeFourInLine();
			blueIcons = new String[]{"/pictures/circle.png"};
			redIcons  = new String[]{"/pictures/cross.png"};
			blueIconsSmall = new String[]{"/pictures/circleSmall.png"};
			redIconsSmall = new String[]{"/pictures/crossSmall.png"};
			gameRules = ""
					+ "Players take turn to place their tokens. The first player\n"
					+ "who has four of their tokens in a line wins the game.\n"
					+ "The board can have a size of 4X4 and up to 9X9.";
			currentGameStats = logic.getStats();
			break;
		case 3: // Connect four
			minGridX = 4;
			minGridY = 4;
			maxGridX = 70;
			maxGridY = 32;
			suggestedGridX = 7;
			suggestedGridY = 6;
			logic = new TypeConnectFour();
			blueIcons = new String[]{"/pictures/blueCoin.png"};
			redIcons  = new String[]{"/pictures/redCoin.png"};
			blueIconsSmall = new String[]{"/pictures/blueCoinSmall.png"};
			redIconsSmall = new String[]{"/pictures/redCoinSmall.png"};
			gameRules = ""
					+ "Players take turn to place their tokens. The first player\n"
					+ "who has four of their tokens in a line wins the game.\n"
					+ "The board can have a size of 4X4 and up to 9X9.\n"
					+ "The tokens can only be placed from the buttom and up\n"
					+ "where the tokens will stack on each other.";
			currentGameStats = logic.getStats();
			break;
		case 4: // Reversi
			minGridX = 8;
			minGridY = 8;
			maxGridX = 8;
			maxGridY = 8;
			suggestedGridX = 8;
			suggestedGridY = 8;
			logic = new TypeReversi();
			blueIcons = new String[]{"/pictures/blueCoin.png"};
			redIcons  = new String[]{"/pictures/redCoin.png"};
			blueIconsSmall = new String[]{"/pictures/blueCoinSmall.png"};
			redIconsSmall = new String[]{"/pictures/redCoinSmall.png"};
			gameRules = ""
					+ "Players take turn to place their tokens. When it is not\n"
					+ "possible to place any more tokens the game ends and the\n"
					+ "player with most tokens left on the board wins the game.\n"
					+ "The board can only have the size of 8X8.\n"
					+ "The tokens can only be placed when you enclose the\n"
					+ "opponents tokens in a line. That tokens in that line is\n"
					+ "then converted to your tokens.";
			currentGameStats = logic.getStats();
			break;
		case 5: // Chess
			minGridX = 8;
			minGridY = 8;
			maxGridX = 8;
			maxGridY = 8;
			suggestedGridX = 8;
			suggestedGridY = 8;
			logic = new TypeChess();
			blueIcons = new String[]{"/pictures/chess/pawnBlue.png", "/pictures/chess/rookBlue.png", "/pictures/chess/knightBlue.png", 
					"/pictures/chess/bishopBlue.png", "/pictures/chess/queenBlue.png", "/pictures/chess/kingBlue.png"};
			redIcons  = new String[]{"/pictures/chess/pawnRed.png", "/pictures/chess/rookRed.png", "/pictures/chess/knightRed.png", 
					"/pictures/chess/bishopRed.png", "/pictures/chess/queenRed.png", "/pictures/chess/kingRed.png"};
			gameRules = ""
					+ "Players take turn to move their pieces. When a players\n"
					+ "king is in a state where it is certain that it will die\n"
					+ "the next turn then that player is checkmate and the player\n"
					+ "looses the game.\n"
					+ "The board can only have the size of 8X8.\n"
					+ "The pieces can move in different ways depending on the\n"
					+ "type of the piece.";
			currentGameStats = logic.getStats();
			break;
		case 6: // Checkers
			minGridX = 8;
			minGridY = 8;
			maxGridX = 8;
			maxGridY = 8;
			suggestedGridX = 8;
			suggestedGridY = 8;
			logic = new TypeCheckers();
			blueIcons = new String[]{"/pictures/blueCoin.png", "/pictures/doubleBlueCoin.png"};
			redIcons  = new String[]{"/pictures/redCoin.png", "/pictures/doubleRedCoin.png"};
			gameRules = ""
					+ "Players take turn to move their pieces. When a player\n"
					+ "has run out of pieces he will loose the game.\n"
					+ "The board can only have the size of 8X8.\n"
					+ "The pieces can only move diagonally forward. If a piece\n"
					+ "gets to the end of the opponents side then the piece will\n"
					+ "be upgraded so that it also moves backwards. when an\n"
					+ "attack is available one must always attack. if it is\n"
					+ "possible to attack more than once with the attacking piece\n"
					+ "then it must also do that.";
			currentGameStats = logic.getStats();
			break;
		case 7: // Mills
			minGridX = 7;
			minGridY = 7;
			maxGridX = 7;
			maxGridY = 7;
			suggestedGridX = 7;
			suggestedGridY = 7;
			logic = new TypeMills();
			blueIcons = new String[]{"/pictures/blueCoin.png"};
			redIcons  = new String[]{"/pictures/redCoin.png"};
			blueIconsSmall = new String[]{"/pictures/blueCoinSmall.png"};
			redIconsSmall = new String[]{"/pictures/redCoinSmall.png"};
			gameRules = ""
					+ "Players take turn to place their tokens. The player who\n"
					+ "places three of their tokens in a line can remove one of\n"
					+ "the oponents pieces. A player starts with nine pieces.\n"
					+ "When a player is out of pieces he looses the game.\n"
					+ "The board can have a size of 7X7.";
			currentGameStats = logic.getStats();
			break;
		case 8: // Go
			minGridX = 9;
			minGridY = 9;
			maxGridX = 19;
			maxGridY = 19;
			suggestedGridX = 19;
			suggestedGridY = 19;
			logic = new TypeGo();
			blueIcons = new String[]{"/pictures/blueCoin.png", "/pictures/emptyBlue.png", "/pictures/pass.png"};
			redIcons  = new String[]{"/pictures/redCoin.png", "/pictures/emptyRed.png", "/pictures/pass.png"};
			blueIconsSmall = new String[]{"/pictures/blueCoinSmall.png", "/pictures/emptyBlueSmall.png", "/pictures/passSmall.png"};
			redIconsSmall = new String[]{"/pictures/redCoinSmall.png", "/pictures/emptyRedSmall.png", "/pictures/passSmall.png"};
			gameRules = ""
					+ "Players take turn to place their tokens. The player.\n"
					+ "who surrounds the other players tokens wins those tokens.\n"
					+ "The game ends when all tokens are placed.\n"
					+ "The winner is the one with the highest number og tokens.\n"
					+ "The board can only have the size of 19X19.";
			currentGameStats = logic.getStats();
			break;
		}
	}
	
	public void setAlgorithm(int a1, int a2){
		// update the algorithm that was chosen in the GUI
		if(a1 > 0 && a1 <= NUMBER_OF_ALGORITHMS){
			if(a1 == 1){
				algo1 = new AlgorithmMiniMax();
			}else if(a1 == 2){
				algo1 = new AlgorithmRandomPick();
			}else if(a1 == 3){
				algo1 = new AlgorithmEvaluation();
			}else if(a1 == 4){
				algo1 = new AlgorithmMiniMaxNoEvaluation();
			}else if(a1 == 5){
				algo1 = new AlgorithmReinforcementLearning();
			}else if(a1 == 6){
				algo1 = new AlgorithmReinforcementLearningMiniMax();
			}else{
				algo1 = new AlgorithmMiniMax();
			}
		}
		if(a2 > 0 && a2 <= NUMBER_OF_ALGORITHMS){
			if(a2 == 1){
				algo2 = new AlgorithmMiniMax();
			}else if(a2 == 2){
				algo2 = new AlgorithmRandomPick();
			}else if(a2 == 3){
				algo2 = new AlgorithmEvaluation();
			}else if(a2 == 4){
				algo2 = new AlgorithmMiniMaxNoEvaluation();
			}else if(a2 == 5){
				algo2 = new AlgorithmReinforcementLearning();
			}else if(a2 == 6){
				algo2 = new AlgorithmReinforcementLearningMiniMax();
			}else{
				algo2 = new AlgorithmMiniMax();
			}
		}
	}

	// when a new game is about to start all the input is verified and the board is called
	public boolean startGame(
			int difficulty1, 
			int difficulty2, 
			int sizeX, 
			int sizeY, 
			String player1Name, 
			String player2Name, 
			int player1Type, 
			int player2Type, 
			int gameMode, 
			int algo1, 
			int algo2){
		// method that initialises the gameLogic object and the player objects
		//System.out.println("\n-- starting new game --");
		// if gui does not run these methods then they are run here - which is not guaranteed because the GUI will only update the methods if it needs the information
		setGameMode(gameMode); 
		setAlgorithm(algo1, algo2);
		round = 1;
		hasGameEnded = true;
		gameOverResult = 0;
		if(		   difficulty1 > 0 && difficulty1 < 4
				&& difficulty2 > 0 && difficulty2 < 4
				&& sizeX >= minGridX && sizeX <= maxGridX
				&& sizeY >= minGridY && sizeY <= maxGridY
				&& !player1Name.isEmpty()
				&& !player2Name.isEmpty() 
				&& !player1Name.toLowerCase().equals(player2Name.toLowerCase()) // the names cannot be equal!
				&& (gameMode > 0 && gameMode <= NUMBER_OF_GAMES) // the allowed game modes
				&& (player1Type == 1 || player1Type == 2)
				&& (player2Type == 1 || player2Type == 2)
				&& (algo1 > 0 && algo1 <= NUMBER_OF_ALGORITHMS)
				&& (algo2 > 0 && algo2 <= NUMBER_OF_ALGORITHMS)
				){

			updatePlayers(player1Name, player2Name, player1Type, player2Type);

			logic.setBoard(logic.setupNewMatch(sizeX, sizeY, difficulty1, difficulty2));
			currentGameStats = logic.getStats();
			hasGameEnded = false;
			//System.out.println("" + gameLogic.toString()); // test print of a newly started game
			return true;
		}else{
			System.out.println("Input not set correct!");
			return false;
		}
	}

	public void move(int val) {
		//System.out.println(MiscTools.debugPrintBoard(logic.getBoard()));
		// called either with -1 which means it is a computer move
		// or with a btnID that will be used in the gameLogic.processTurn method (for example when in chess, one has to select a pawn and THEN move it)
		// the btnID have to be analysed, who is doing the move
		D_N_D = true; // Semaphore start - used to avoid illegal button presses on the GUI (DND - Do Not Disturb)
		if(gameOverResult != 0){
			D_N_D = false;
			return;
		}
		if(gameOverResult != 0 || player1Type==1 && playerTurn==1 && val==-1 || player2Type==1 && playerTurn==2 && val==-1){
			boolean[][] GUIGrid = logic.prepareBoardForGUI(playerTurn, playerTurn==1?2:1);
			GAME_GUI.setGUIBoard(GUIGrid, logic.getBoard(), gameOverResult);
			D_N_D = false;
			return;
		}
		if(val==-1){ // computer move
			processTurn(val, logic.getBoard(), player1Type, player2Type, playerTurn);
			if(hasGameEnded){D_N_D = false;return;} // if an interrupt happens, return
			boolean[][] GUIGrid = logic.prepareBoardForGUI(playerTurn, playerTurn==1?2:1);
			GAME_GUI.setGUIBoard(GUIGrid, logic.getBoard(), gameOverResult);
		}else if(val!=-1){ // player move
			processTurn(val, logic.getBoard(), player1Type, player2Type, playerTurn);
			boolean[][] GUIGrid = logic.prepareBoardForGUI(playerTurn, playerTurn==1?2:1);
			GAME_GUI.setGUIBoard(GUIGrid, logic.getBoard(), gameOverResult);
		}else{
			System.out.println("error in move - \"val\" is" + val);
		}
		if(Thread.currentThread().isInterrupted()){ // interrupted from the GUI
			System.out.println("Thread interrupted in the move method");
			GAME_GUI.endGame(-5);
			D_N_D = false;
			return;
		}
		// check if game over update statistics
		if(gameOverResult != 0){
			setResult(gameOverResult);
		}

		// recursive step
		else if(player1Type == 2 && player2Type == 2){ // if there are two computers
			move(-1);
		}else if(player1Type == 1 && player2Type == 1){ // if there are two human
			// do nothing
		}else if(val!=-1){ // if there is a human and a computer - and a human have just made its move
			move(-1);
		}
		D_N_D = false; // Semaphore end
	}

	// when a game ends this method is called to save the results in persistence and in the controller results
	public void setResult(int gameOverResult){
		round = 0; // reset round counter
		if(gameOverResult == 1){
			player1NumWins++;
		}else if(gameOverResult == 2){
			player2NumWins++;
		}else{numOfDraws++;}
		numOfPlayedGames++;
		MiscTools.persistence.setStat(player1Name, player2Name, player1NumWins, player2NumWins, numOfDraws, numOfPlayedGames); // update the statFile.txt
		currentGameStats = logic.getStats();
		MiscTools.persistence.recordGamePlayed(
				logic.getGameID(), 
				player1Name, 
				player2Name, 
				logic.getDifficulty(1), 
				logic.getDifficulty(2), 
				player1Type==2?true:false, 
				player2Type==2?true:false, 
				algo1.getAlgorithmID(), 
				algo2.getAlgorithmID(), 
				gameOverResult, 
				logic.getBoard().getGrid().length, 
				logic.getBoard().getGrid()[0].length); // save game results to persistence
		GAME_GUI.endGame(gameOverResult);
	}

	public void updatePlayers(String player1Name, String player2Name, int player1Type, int player2Type){
		this.player1Name = player1Name;
		this.player2Name = player2Name;
		this.player1Type = player1Type;
		this.player2Type = player2Type;
		playerTurn = 1;
		// extract numbers from statFile.txt or MySQL
		int[] playerStats = MiscTools.persistence.getStat(player1Name, player2Name);
		if(playerStats == null){ // error handling - getStat failed - just don't use the statistic
			player1NumWins = 0;
			player2NumWins = 0;
			numOfDraws = 0;
			numOfPlayedGames = 0;
		}else{
			player1NumWins = playerStats[0];
			player2NumWins = playerStats[1];
			numOfDraws = playerStats[2];
			numOfPlayedGames = playerStats[3];
		}
	}

	public IStateOBJ processTurn(int s, IStateOBJ state, int player1Type, int player2Type, int playerTurn){
		int movingPlayer;
		int otherPlayer;
		boolean isComputer;
		if((playerTurn == 1) && player1Type == 2){ // it is computer turn  player 1
			isComputer = true; movingPlayer = 1; otherPlayer = 2;
		}
		else if((playerTurn == 2) && player2Type == 2){ // it is computer turn  player 2
			isComputer = true; movingPlayer = 2; otherPlayer = 1;
		}
		else if((playerTurn == 1) && player1Type == 1){ // it is human turn  player 1
			isComputer = false; movingPlayer = 1; otherPlayer = 2;
		}
		else if((playerTurn == 2) && player2Type == 1){ // it is human turn  player 2
			isComputer = false; movingPlayer = 2; otherPlayer = 1;
		}else{ // error
			System.out.println("error: did not find player turn");
			return null;
		}

		if(isComputer){ // computer
			ArrayList<IStateOBJ> moves = logic.processMoveAlgorithm(state, movingPlayer, otherPlayer, logic.getDepth(movingPlayer));
			if(moves.size() == 1){ // extra logic to skip the algorithm if there is only one move left
				//System.out.println("only one move left");
				state = moves.get(0);
			}
			else if(moves.size() > 1){ // more than one moves left
				IStateOBJ newState = null;
				if(movingPlayer == 1)
					newState = algo1.startAlgorithm(state, movingPlayer, otherPlayer, logic);
				else if(movingPlayer == 2)
					newState = algo2.startAlgorithm(state, movingPlayer, otherPlayer, logic);

				if(newState == null){ // illegal move - take the first option and just go with that
					System.out.println("illegal move - Num of moves left: " + moves.size());
					newState = moves.get(0);
				}
				if(Thread.currentThread().isInterrupted()){
					System.out.println("Thread interrupted in processTurn method");
					return null;
				}
				state = newState;
			}
			else{System.out.println("No moves left");return null;}
		}
		else{ // human
			Point point = new Point((s % state.getGrid().length), (s / state.getGrid().length));
			state = logic.processMovePlayer(point, state, movingPlayer, otherPlayer);
			if(state  == null){return null;} // illegal move - return nothing - ignore move
		}
		gameOverResult = logic.calcWinner(state, movingPlayer, otherPlayer); // -1=draw, 0=game still on, 1=winner one, 2=winner two
		//if(gameOverResult != 0){
		//System.out.println("Game over." + ((gameOverResult == 1) ? " Player 1 won" : ((gameOverResult == 2) ? " Player 2 won" : " Draw")));
		//} // console information print

		if(this.playerTurn == 2){
			round++; // increment round
		}
		this.playerTurn = logic.setPlayerTurn(playerTurn);
		logic.setBoard(state);
		currentGameStats = logic.getStats();
		MiscTools.persistence.recordGameTurn(state.getGrid(), round, playerTurn==1?true:false, false);
		return state;
	}

	public String getName(int difficulty, boolean isComputer, int playerNumber){
		// returns the default name a given player should have
		String name = "";
		if(isComputer){
			if(difficulty == 0){ // Computer name
				name = "Computer";
			}else if(difficulty == 1){ // Easy-Computer name
				name = "Easy-Computer";
			}else if(difficulty == 2){ // Normal-Computer name
				name = "Normal-Computer";
			}else if(difficulty == 3){ // Hard-Computer name
				name = "Hard-Computer";
			}
		}else{
			name = "Player";
		}

		if(playerNumber == 1){ // 1
			name += "1";
		}else if(playerNumber == 2){ // 2
			name += "2";
		}

		return name;
	}

	public void resetAllStat(){
		MiscTools.persistence.resetStat();
	}

	public void resetCurrentStat(){
		MiscTools.persistence.setStat(player1Name, player2Name, 0, 0, 0, 0);
	}

	public String getAllStat(){
		return MiscTools.persistence.getAllStat();
	}

	// public info that lies in this controller that is used by the gui
	public int            getNumOfPlayedGames()  {return numOfPlayedGames;}
	public int            getNumOfDraws()        {return numOfDraws;}
	public int            getPlayerTurn()        {return playerTurn;}
	public String         getPlayerName(int p)   {return p==1?player1Name:player2Name;}
	public int            getPlayerType(int p)   {return p==1?player1Type:player2Type;}
	public int            getPlayerNumWins(int p){return p==1?player1NumWins:player2NumWins;}
	public int            getNumberOfAlgorithms(){return NUMBER_OF_ALGORITHMS;}
	public int            getNumberOfGames()     {return NUMBER_OF_GAMES;}
	public String         getGameModeName()      {return logic.getGameName();}
	public int            getMinGridX()          {return minGridX;}
	public int            getMinGridY()          {return minGridY;}
	public int            getMaxGridX()          {return maxGridX;}
	public int            getMaxGridY()          {return maxGridY;}
	public int            getSuggestedGridX()    {return suggestedGridX;}
	public int            getSuggestedGridY()    {return suggestedGridY;}
	public IGameAlgorithm getAlgo1()             {return algo1;}
	public IGameAlgorithm getAlgo2()             {return algo2;}
	public String[]       getBlueIcons()         {return blueIcons;}
	public String[]       getRedIcons()          {return redIcons;}
	public String[]       getBlueIconsSmall()    {return blueIconsSmall;}
	public String[]       getRedIconsSmall()     {return redIconsSmall;}
	public String         getGameRules()         {return gameRules;}
	public String         getCurrentGameStats()  {return currentGameStats;}
	public int            getRound()             {return round;}
	public boolean        getDoNotDisturb()      {return D_N_D;}
	public int[][]		  getGrid()				 {return logic.getBoard().getGrid();}

	public String toString(){ // the toString prints the board out + other information about the game.
		String s = "difficulty: " + logic.getDifficulty(1) + " " + logic.getDifficulty(2) + ", depth: " + logic.getDepth(1) + " " + logic.getDepth(2) + ", board Size: " + logic.getBoard().getGrid().length + "-" + logic.getBoard().getGrid()[0].length
				+ "\ngame mode: " + logic.getGameName() + ", games played: " + (numOfPlayedGames-1) + ", draws: " + numOfDraws + ", round: " + round
				+ "\nplayer1: " + player1Name + ", type: " + player1Type + ", wins: " + player1NumWins + ", lost: " + player2NumWins
				+ "\nplayer2: " + player2Name + ", type: " + player2Type + ", wins: " + player2NumWins + ", lost: " + player1NumWins;
		return s + MiscTools.debugPrintBoard(logic.getBoard());
	}
}