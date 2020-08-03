package gameGUI;

import gameLogic.Controller;
import gameLogic.IStateOBJ;
import gameLogic.MiscTools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GUIGameSwing implements IGameGUI {

	boolean isSplashscreenShown = true; // only used at the beginning - can show a splashcreen - used to initialize all the gui at the beginning.
	String keyboardBuffer = ""; // used by the KeyListener to store input from keyboard
	// selections for keyboard typing:
	boolean isNameSelected1 = false;
	boolean isNameSelected2 = false;
	boolean isAlgorithmSelected1 = false;
	boolean isAlgorithmSelected2 = false;
	boolean isGameSelected = false;
	boolean isXSelected = false;
	boolean isYSelected = false;
	boolean isAIMsDelaySelected = false;
	boolean isNumberOfIterationsSelected = false;

	private int x = 3, xx; // max is 24. For small 70
	private int y = 3, yy; // max is 11. For small 32
	private int game = 1; // different games can be selected for play - this value corresponds to controller - IGameType logic : TicTacToe, FourInLine, ConnectFour, Reversi, Chess, Checkers, Mills...
	private String player1Name = "Normal-Computer1"; // player 1 name
	private String player2Name = "Normal-Computer2"; // player 2 name
	private int player1Type = 2; // 1 for human, 2 for ai
	private int player2Type = 2; // 1 for human, 2 for ai
	private int difficulty1 = 2; // player 1 selected difficulty - 1, 2, 3 - easy, medium, hard. MiniMax, RandomPick, eval, minimax no eval...
	private int difficulty2 = 2; // player 2 selected difficulty - 1, 2, 3 - easy, medium, hard. MiniMax, RandomPick, eval, minimax no eval...
	private int algorithm1 = 1; // player 1 selected algorithm - means nothing if player 1 is human
	private int algorithm2 = 1; // player 2 selected algorithm - means nothing if player 2 is human

	private Controller controller; // the controller object that holds the ai, game rules and player stats - everything
	private int AIMsDelay = 500; // the ms delay between moves - when testing a game it is also between games.
	private int numberOfIterations = 0; // when repeating a test-game the numberOfIterations decreases
	private int oldNumberOfIterations = 0; // this value holds the original desired number of iterations when running a test-game
	private int hoverMsgID = 0; // id for the last hover msg shown
	private boolean isTestingWithoutBoard = false; // when true - do not show board moves for faster processing
	private boolean isTesting = false; // when true - one can test iteratively - this means that an ai vs ai can play x times in a row, where x is numberOfIterations
	private Thread thread = null; // when an ai game is on a user should still be able to press on buttons on the gui - this thread is started for ai processing
	private int interruptedBy = 0; // 0 = no interrupt, -2 = give up, -3 reset - used when pressing the give up button or reset button when a game is on. 
	private boolean isGameOn = false; // only used to distinguish what buttons a player is allowed to press when the game is on or not.
	private Thread playerProcessingThread = null; // this thread is to update the timer and the graphical "thinking" icon - the timer is crucial, if a player runs out of time he looses.
	private JFrame frame; // the JFrame that will hold the board
	private Board board; // the board that will contain all the gui elements
	private String console = "program started"; // what the console gui window should display - new line is represented with \n 
	private long startTime = 0; // record time taken to adjust the sleep in setGUIBoard - sometimes it takes longer for ai - decrease sleep - and is used to record how much time have gone since start of a turn
	private long startOfGameTime = 0; // starts recording when a game starts and resets when a game ends - handles multiple button presses on the start game and give up btn
	private int lastPlayerTurn = 1; // startTime should only be reset when a new player has taken its turn. 
	private int[][] storedGrid = null; // the storedGrid is used to keep displaying the result of a game even after it finishes. 
	private boolean[][] storedSelectableGrid = null; // the storedGrid is used to keep displaying the possible moves of a game even after it finishes. 
	private int scrollStateScore = 0; // one can scroll through the score window this value is then increased
	private int scrollStateConsole = 0; // one can scroll through the console window this value is then increased
	// store images for later use
	private final Image imageStartButton        = new javax.swing.ImageIcon(getClass().getResource("/pictures/game/startButton.png")).getImage();
	private final Image imageGiveUpButton       = new javax.swing.ImageIcon(getClass().getResource("/pictures/game/giveUpButton.png")).getImage();
	private final Image imageClosed             = new javax.swing.ImageIcon(getClass().getResource("/pictures/closed.png")).getImage();
	private final Image imageEmpty              = new javax.swing.ImageIcon(getClass().getResource("/pictures/empty.png")).getImage();
	private final Image imageClosedSmall        = new javax.swing.ImageIcon(getClass().getResource("/pictures/closedSmall.png")).getImage();
	private final Image imageEmptySmall         = new javax.swing.ImageIcon(getClass().getResource("/pictures/emptySmall.png")).getImage();
	private final Image imageEasyUnselected     = new javax.swing.ImageIcon(getClass().getResource("/pictures/game/easyUnselected.png")).getImage();
	private final Image imageNormalUnselected   = new javax.swing.ImageIcon(getClass().getResource("/pictures/game/normalUnselected.png")).getImage();
	private final Image imageHardUnselected     = new javax.swing.ImageIcon(getClass().getResource("/pictures/game/hardUnselected.png")).getImage();
	private final Image imageEasySelected       = new javax.swing.ImageIcon(getClass().getResource("/pictures/game/easySelected.png")).getImage();
	private final Image imageNormalSelected     = new javax.swing.ImageIcon(getClass().getResource("/pictures/game/normalSelected.png")).getImage();
	private final Image imageHardSelected       = new javax.swing.ImageIcon(getClass().getResource("/pictures/game/hardSelected.png")).getImage();
	private final Image imageHumanSelected      = new javax.swing.ImageIcon(getClass().getResource("/pictures/game/humanSelected.png")).getImage();
	private final Image imageComputerSelected   = new javax.swing.ImageIcon(getClass().getResource("/pictures/game/computerSelected.png")).getImage();
	private final Image imageHumanUnselected    = new javax.swing.ImageIcon(getClass().getResource("/pictures/game/humanUnselected.png")).getImage();
	private final Image imageComputerUnselected = new javax.swing.ImageIcon(getClass().getResource("/pictures/game/computerUnselected.png")).getImage();
	private final Image imageNameFieldComputer  = new javax.swing.ImageIcon(getClass().getResource("/pictures/game/nameFieldComputer.png")).getImage();
	private final Image imageNameFieldHuman     = new javax.swing.ImageIcon(getClass().getResource("/pictures/game/nameFieldHuman.png")).getImage();
	private final Image imageNameFieldHumanS    = new javax.swing.ImageIcon(getClass().getResource("/pictures/game/nameFieldHumanS.png")).getImage();
	private final Image imageTwoLettersBox      = new javax.swing.ImageIcon(getClass().getResource("/pictures/game/twoLettersBox.png")).getImage();
	private final Image imageTwoLettersBoxS     = new javax.swing.ImageIcon(getClass().getResource("/pictures/game/twoLettersBoxS.png")).getImage();
	private final Image imageThreeLettersBox    = new javax.swing.ImageIcon(getClass().getResource("/pictures/game/threeLettersBox.png")).getImage();
	private final Image imageThreeLettersBoxS   = new javax.swing.ImageIcon(getClass().getResource("/pictures/game/threeLettersBoxS.png")).getImage();
	private final Image imageFourLettersBox     = new javax.swing.ImageIcon(getClass().getResource("/pictures/game/fourLettersBox.png")).getImage();
	private final Image imageFourLettersBoxS    = new javax.swing.ImageIcon(getClass().getResource("/pictures/game/fourLettersBoxS.png")).getImage();
	private final Image imageGUIOn              = new javax.swing.ImageIcon(getClass().getResource("/pictures/game/GUIOn.png")).getImage();
	private final Image imageGUIOff             = new javax.swing.ImageIcon(getClass().getResource("/pictures/game/GUIOff.png")).getImage();
	private final Image imageLoadingSmall       = new javax.swing.ImageIcon(getClass().getResource("/pictures/loadingSmall.gif")).getImage();
	private final Image imageClickable          = new javax.swing.ImageIcon(getClass().getResource("/pictures/clickable.png")).getImage();
	private final Image imageNotClickable       = new javax.swing.ImageIcon(getClass().getResource("/pictures/notClickable.png")).getImage();
	private final Image imageClickableSmall     = new javax.swing.ImageIcon(getClass().getResource("/pictures/clickableSmall.png")).getImage();
	private final Image imageNotClickableSmall  = new javax.swing.ImageIcon(getClass().getResource("/pictures/notClickableSmall.png")).getImage();
	private final Image imageAlphabet[]         = {new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/A.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/B.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/C.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/D.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/E.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/F.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/G.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/H.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/I.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/J.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/K.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/L.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/M.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/N.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/O.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/P.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/Q.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/R.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/S.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/T.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/U.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/V.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/W.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/X.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/Y.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/Z.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/0.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/1.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/2.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/3.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/4.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/5.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/6.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/7.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/8.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/9.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/space.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/hyphen.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/dot.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/leftBracket.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/rightBracket.png")).getImage(),
			new javax.swing.ImageIcon(getClass().getResource("/pictures/game/alphabet/placeholder.png")).getImage()};
	private Image imageBlueIcons[]              = null; // set in setGame method
	private Image imageRedIcons[]               = null; // set in setGame method
	private Image imageBlueIconsSmall[]         = null; // set in setGame method
	private Image imageRedIconsSmall[]          = null; // set in setGame method

	@Override
	public void startGameGui(final Controller control) {
		final GUIGameSwing g = this;
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUIGameSwing window = g;
					window.controller = control;
					window.initialize();
					window.frame.setVisible(true);
					window.frame.addComponentListener(new ComponentListener() {
						@Override
						public void componentHidden(ComponentEvent arg0) {}
						@Override
						public void componentMoved(ComponentEvent arg0) {}
						@Override
						public void componentResized(ComponentEvent arg0) {
							// when the window is resized the GUI will go back to the basic splashscreen.
							// by resetting this value one will redisplay all the information again by pressing any key
							window.isSplashscreenShown = true;
						}
						@Override
						public void componentShown(ComponentEvent arg0) {}
					});
				} catch (Exception e) {e.printStackTrace();}
			}
		});

	}

	@Override
	public void setGUIBoard(boolean[][] selectablegrid, IStateOBJ state, int gameOverResult) {
		// the move has to update the GUI
		if(isTestingWithoutBoard){
			// when testing we do not want to update anything for faster processing
			if(gameOverResult != 0){
				storedGrid = MiscTools.clone2DIntArray(state.getGrid());
				storedSelectableGrid = MiscTools.clone2DBooleanArray(selectablegrid);
			}
		}else{
			if(gameOverResult == 0){
				setBoard(state.getGrid(), selectablegrid, 0);
				populateCurrentGameStats();
				long endTime = System.nanoTime();
				long timeTakenInMilis = (endTime - startTime)/1000000; // how long did last move take?
				long sleepTime = (AIMsDelay - timeTakenInMilis)<0?0:(AIMsDelay - timeTakenInMilis); // adjust wait time
				try {Thread.sleep(sleepTime);} // sleep so human can see the changes
				catch(InterruptedException e){thread.interrupt();}
				if(lastPlayerTurn != controller.getPlayerTurn()){ // only update this time when another player has its turn
					startTime = System.nanoTime(); // start recording time taken for next move
					lastPlayerTurn = controller.getPlayerTurn();
				}
			}
			storedGrid = MiscTools.clone2DIntArray(state.getGrid());
			storedSelectableGrid = MiscTools.clone2DBooleanArray(selectablegrid);
		}
	}

	@Override
	public void endGame(int gameOverResult) {
		// restore grid size to original grid size - some games alter the grid to enable different features in the game
		x = xx;
		y = yy;
		isGameOn = false;
		startOfGameTime = System.nanoTime();
		Thread.interrupted(); // clear interrupt flag
		setImage(596, 631, imageStartButton);
		if(gameOverResult == -5){ // end game does not know why it was interrupted
			gameOverResult = interruptedBy;
		}
		setBoard(storedGrid, storedSelectableGrid, gameOverResult);
		populateCurrentGameStats();
		displayPlayerProcessing(false);

		storedGrid = null;
		storedSelectableGrid = null;

		if(isTesting && numberOfIterations > 0 && interruptedBy != -2 && interruptedBy != -3){
			setNumberOfIterations(numberOfIterations - 1); // decrement in the loop
			try {Thread.sleep(AIMsDelay);} // sleep so human can see the changes in between games
			catch(InterruptedException e){thread.interrupt();}
			start(); // loop
		}else if(isTesting && oldNumberOfIterations != 0){
			numberOfIterations = oldNumberOfIterations;
			oldNumberOfIterations = 0;
			console += "\nFinished iterating " + numberOfIterations + " times.";
			populateConsole(false);
			setNumberOfIterations(numberOfIterations);
		}
	}

	void displayValues(){
		isSplashscreenShown = false; // and remove splashscreen
		populateConsole(true);
		setDifficulty(1, difficulty1);
		setDifficulty(2, difficulty2);
		setPlayerType(1, player1Type);
		setPlayerType(2, player2Type);
		setAlgorithm(1, algorithm1, false, false);
		setAlgorithm(2, algorithm2, false, false);
		setGame(game, false, false);
		setAIMsDelay(AIMsDelay, false, false);
		setNumberOfIterations(0);
		setGUIUpdate(1);
		setTestingStatus(1);
		setName(1, player1Name);
		setName(2, player2Name);
		Graphics2D g2 = (Graphics2D) board.getGraphics();
		g2.setColor(new Color(195,195,195));
		g2.fillRect(685, 601, 118, 118);
		populateScore();
	}

	void deselect(){
		if(isNameSelected1){
			setImage(524, 629, imageNameFieldHuman);
			drawText(524+2, 629+2, "", board, 16);
			isNameSelected1 = false;
			setName(1, keyboardBuffer);
		}
		if(isNameSelected2){
			setImage(524, 687, imageNameFieldHuman);
			drawText(524+2, 687+2, "", board, 16);
			isNameSelected2 = false;
			setName(2, keyboardBuffer);
		}
		if(isAlgorithmSelected1){
			setImage(552, 648, imageTwoLettersBox);
			drawText(552+2, 648+2, genNum(algorithm1), board, 2);
			isAlgorithmSelected1 = false;
			try {setAlgorithm(1, Integer.parseInt(keyboardBuffer), false, false);}
			catch(Exception e){keyboardBuffer = "";}
		}
		if(isAlgorithmSelected2){
			setImage(552, 706, imageTwoLettersBox);
			drawText(552+2, 706+2, genNum(algorithm2), board, 2);
			isAlgorithmSelected2 = false;
			try {setAlgorithm(2, Integer.parseInt(keyboardBuffer), false, false);}
			catch(Exception e){keyboardBuffer = "";}
		}
		if(isGameSelected){
			setImage(494, 616, imageTwoLettersBox);
			drawText(494+2, 616+2, genNum(game), board, 2);
			isGameSelected = false;
			try {setGame(Integer.parseInt(keyboardBuffer), false, false);}
			catch(Exception e){keyboardBuffer = "";}
		}
		if(isXSelected){
			setImage(494, 634, imageTwoLettersBox);
			drawText(494+2, 634+2, genNum(x), board, 2);
			isXSelected = false;
			try {setX(Integer.parseInt(keyboardBuffer), false, false);}
			catch(Exception e){keyboardBuffer = "";}

		}
		if(isYSelected){
			setImage(494, 652, imageTwoLettersBox);
			drawText(494+2, 652+2, genNum(y), board, 2);
			isYSelected = false;
			try {setY(Integer.parseInt(keyboardBuffer), false, false);}
			catch(Exception e){keyboardBuffer = "";}
		}
		if(isAIMsDelaySelected){
			setImage(490, 670, imageFourLettersBox);
			drawText(490+2, 670+2, genNum(AIMsDelay), board, 4);
			isAIMsDelaySelected = false;
			try {setAIMsDelay(Integer.parseInt(keyboardBuffer), false, false);}
			catch(Exception e){keyboardBuffer = "";}
		}
		if(isNumberOfIterationsSelected){
			setImage(506, 690, imageThreeLettersBox);
			drawText(506+2, 690+2, genNum(numberOfIterations), board, 3);
			isNumberOfIterationsSelected = false;
			try {setNumberOfIterations(Integer.parseInt(keyboardBuffer));}
			catch(Exception e){keyboardBuffer = "";}
		}
		keyboardBuffer = "";
	}

	void drawText(int x, int y, String s, Board board, int maxChar){
		// abcdefghijklmnopqrstuvwxyz0123456789 []
		int i = 1;
		for (char c: s.toCharArray()) {
			Image img = null;
			if(c == 'a'||c == 'A'){img = imageAlphabet[0];}
			else if(c == 'b'||c == 'B'){img = imageAlphabet[1];}
			else if(c == 'c'||c == 'C'){img = imageAlphabet[2];}
			else if(c == 'd'||c == 'D'){img = imageAlphabet[3];}
			else if(c == 'e'||c == 'E'){img = imageAlphabet[4];}
			else if(c == 'f'||c == 'F'){img = imageAlphabet[5];}
			else if(c == 'g'||c == 'G'){img = imageAlphabet[6];}
			else if(c == 'h'||c == 'H'){img = imageAlphabet[7];}
			else if(c == 'i'||c == 'I'){img = imageAlphabet[8];}
			else if(c == 'j'||c == 'J'){img = imageAlphabet[9];}
			else if(c == 'k'||c == 'K'){img = imageAlphabet[10];}
			else if(c == 'l'||c == 'L'){img = imageAlphabet[11];}
			else if(c == 'm'||c == 'M'){img = imageAlphabet[12];}
			else if(c == 'n'||c == 'N'){img = imageAlphabet[13];}
			else if(c == 'o'||c == 'O'){img = imageAlphabet[14];}
			else if(c == 'p'||c == 'P'){img = imageAlphabet[15];}
			else if(c == 'q'||c == 'Q'){img = imageAlphabet[16];}
			else if(c == 'r'||c == 'R'){img = imageAlphabet[17];}
			else if(c == 's'||c == 'S'){img = imageAlphabet[18];}
			else if(c == 't'||c == 'T'){img = imageAlphabet[19];}
			else if(c == 'u'||c == 'U'){img = imageAlphabet[20];}
			else if(c == 'v'||c == 'V'){img = imageAlphabet[21];}
			else if(c == 'w'||c == 'W'){img = imageAlphabet[22];}
			else if(c == 'x'||c == 'X'){img = imageAlphabet[23];}
			else if(c == 'y'||c == 'Y'){img = imageAlphabet[24];}
			else if(c == 'z'||c == 'Z'){img = imageAlphabet[25];}
			else if(c == '0'){img = imageAlphabet[26];}
			else if(c == '1'){img = imageAlphabet[27];}
			else if(c == '2'){img = imageAlphabet[28];}
			else if(c == '3'){img = imageAlphabet[29];}
			else if(c == '4'){img = imageAlphabet[30];}
			else if(c == '5'){img = imageAlphabet[31];}
			else if(c == '6'){img = imageAlphabet[32];}
			else if(c == '7'){img = imageAlphabet[33];}
			else if(c == '8'){img = imageAlphabet[34];}
			else if(c == '9'){img = imageAlphabet[35];}
			else if(c == ' '){img = imageAlphabet[36];}
			else if(c == '-'){img = imageAlphabet[37];}
			else if(c == '.'){img = imageAlphabet[38];}
			else if(c == '['){img = imageAlphabet[39];}
			else if(c == ']'){img = imageAlphabet[40];}
			else {img = imageAlphabet[41];}
			board.getGraphics().drawImage(img, x + ((i * 4) - 4), y, null);
			i++;
			if(maxChar == i-1)break; // break if no more characters are allowed at the spot
		}
		while(maxChar >= i){ // the rest of the spots are filled with space
			board.getGraphics().drawImage(imageAlphabet[36], x + ((i * 4) - 4), y, null);
			i++;
		}
	}

	void hoverMessage(int x, int y){
		String msg = ""; // the message that should be hovered
		int msgID = getGUIElement(x, y); // the id of the message
		if(msgID >= 100000){ // pointing at a specific cell on the board
			msg = "cell " + (msgID - 100000);
		}
		else if(msgID == -1){ // give up
			msg = "Give up";
		}else if(msgID == -2){ // reset
			msg = "Abort game";
		}
		// difficulty
		else if(msgID == 1){ // player 1 is set to easy
			msg = "Set P1. to easy";
		}else if(msgID == 2){ // player 1 is set to normal
			msg = "Set P1. to normal";
		}else if(msgID == 3){ // player 1 is set to hard
			msg = "Set P1. to hard";
		}else if(msgID == 4){ // player 2 is set to easy
			msg = "Set P2. to easy";
		}else if(msgID == 5){ // player 2 is set to normal
			msg = "Set P2. to normal";
		}else if(msgID == 6){ // player 2 is set to hard
			msg = "Set P2. to hard";
		}
		// player type
		else if(msgID == 7){ // player 1 is set to human
			msg = "Set P1. to human";
		}else if(msgID == 8){ // player 1 is set to computer
			msg = "Set P1. to computer";
		}else if(msgID == 9){ // player 2 is set to human
			msg = "Set P2. to human";
		}else if(msgID == 10){ // player 2 is set to computer
			msg = "Set P2. to computer";
		}
		// input to name
		else if(msgID == 11){ // player 1 name is selected
			msg = "Select P1. name";
		}else if(msgID == 12){ // player 2 name is selected
			msg = "Select P2. name";
		}
		// player 1 algorithm
		else if(msgID == 13){ // player 1 algorithm left arrow pressed
			msg = "Algorithm 1 decrease";
		}else if(msgID == 14){ // player 1 algorithm is selected
			msg = "Algorithm 1 select";
		}else if(msgID == 15){ // player 1 algorithm right arrow pressed
			msg = "Algorithm 1 increase";
		}
		// player 2 algorithm
		else if(msgID == 16){ // player 2 algorithm left arrow pressed
			msg = "Algorithm 2 decrease";
		}else if(msgID == 17){ // player 2 algorithm is selected
			msg = "Algorithm 2 select";
		}else if(msgID == 18){ // player 2 algorithm right arrow pressed
			msg = "Algorithm 2 increase";
		}
		// game
		else if(msgID == 19){ // game type left arrow pressed
			msg = "Game decrease";
		}else if(msgID == 20){ // game type is selected
			msg = "Game select";
		}else if(msgID == 21){ // game type right arrow pressed
			msg = "Game increase";
		}else if(msgID == 22){ // game settings reset button have been pressed
			msg = "Reset to default";
		}else if(msgID == 23){ // reset match score button have been pressed
			msg = "Reset current score";
		}
		// x
		else if(msgID == 24){ // x left arrow pressed
			msg = "X decrease";
		}else if(msgID == 25){ // x is selected
			msg = "X select";
		}else if(msgID == 26){ // x right arrow pressed
			msg = "X increase";
		}
		// y
		else if(msgID == 27){ // y left arrow pressed
			msg = "Y decrease";
		}else if(msgID == 28){ // y is selected
			msg = "Y select";
		}else if(msgID == 29){ // y right arrow pressed
			msg = "Y increase";
		}
		// AIMsDelay
		else if(msgID == 30){ // AIMsDelay left arrow pressed
			msg = "Delay decrease";
		}else if(msgID == 31){ // AIMsDelay is selected
			msg = "Delay select";
		}else if(msgID == 32){ // AIMsDelay right arrow pressed
			msg = "Delay increase";
		}
		// start game button
		else if(msgID == 33){ // start game is pressed
			msg = "Start game";
		}
		// score
		else if(msgID == 34){ // score up button pressed
			msg = "Score scroll up";
		}else if(msgID == 35){ // score down button pressed
			msg = "Score scroll down";
		}else if(msgID == 36){ // score reset button pressed
			msg = "Score reset";
		}
		// console
		else if(msgID == 37){ // console up button pressed
			msg = "Console scroll up";
		}else if(msgID == 38){ // console down button pressed
			msg = "Console scroll down";
		}else if(msgID == 39){ // console reset button pressed
			msg = "Console reset";
		}
		// presets
		else if(msgID == 40){ // preset 1 button pressed
			msg = "Preset 1";
		}else if(msgID == 41){ // preset 2 button pressed
			msg = "Preset 2";
		}else if(msgID == 42){ // preset 3 button pressed
			msg = "Preset 3";
		}else if(msgID == 43){ // preset 4 button pressed
			msg = "Preset 4";
		}else if(msgID == 44){ // preset 5 button pressed
			msg = "Preset 5";
		}else if(msgID == 45){ // preset 6 button pressed
			msg = "Preset 6";
		}else if(msgID == 46){ // preset 7 button pressed
			msg = "Preset 7";
		}else if(msgID == 47){ // preset 8 button pressed
			msg = "Preset 8";
		}
		// setGUIUpdate
		else if(msgID == 48){ // GUI on or off button pressed
			msg = "Toggle GUI";
		}
		// setTestingStatus
		else if(msgID == 49){ // Test status on or off button pressed
			msg = "Toggle testing";
		}
		// select number of iterations
		else if(msgID == 50){ // Number of iterations is selected
			msg = "Number of iterations";
		}
		// fields - not active areas on the GUI
		// player fields
		else if(msgID == 51){ // field - player 1
			msg = "Player 1";
		}else if(msgID == 52){ // field - player 2
			msg = "Player 2";
		}
		// match field
		else if(msgID == 53){ // field - player 1 match score
			msg = "Player 1 score";
		}else if(msgID == 54){ // field - match score draw and number of played games
			msg = "Match statistics";
		}else if(msgID == 55){ // field - player 2 match score
			msg = "Player 2 score";
		}
		// score field
		else if(msgID == 56){ // field - score information
			msg = "Score info";
		}
		// console field
		else if(msgID == 57){ // field - console information
			msg = "Console info";
		}
		// rules field
		else if(msgID == 58){ // field - game rules information
			msg = "Game rules info";
		}
		// stats field
		else if(msgID == 59){ // field - game statistics information
			msg = "Game stats info";
		}
		// loading field
		else if(msgID == 60){ // field - time information
			msg = "Time info";
		}
		// board field
		else if(msgID == 61){ // field - board
			msg = "Board";
		}
		else{ // nothing is selected
			msg = "";
		}
		if(msgID != this.hoverMsgID){
			drawText(598, 603, centerText(msg, 21), board, 21);
			hoverMsgID = msgID;
		}
	}

	void buttonPressed(int x, int y){
		int msgID = getGUIElement(x, y); // the id of the message
		if(controller.getDoNotDisturb()){
			System.out.println("do not disturb the computer!");
		}
		deselect();
		// if splashscreen is shown then run displayValues()
		if(isSplashscreenShown){ // only run at first
			displayValues();
		}
		else if(msgID >= 100000){ // pointing at a specific cell on the board
			// human clicks on the board when a game is on
			if(isGameOn && !controller.getDoNotDisturb()){ // human player turn - then you are able to click
				if((controller.getPlayerTurn()==1 && player1Type == 1) ||
						(controller.getPlayerTurn()==2 && player2Type == 1)){
					thread = new Thread(){public void run(){controller.move(msgID - 100000);}};
					thread.start(); // start managing turns. if computer starts this line is neccesary
				}
			}
		}
		// abort game
		else if(msgID == -2){
			long runningTime = (System.nanoTime() - startOfGameTime)/1000000;
			if(thread != null && runningTime > 800){ // 800 ms delay before one can press this btn
				interruptedBy = -3;
				if(controller.getPlayerTurn()==1 && player1Type == 1 ||
						controller.getPlayerTurn()==2 && player2Type == 1){
					endGame(-5);
				}else{
					thread.interrupt(); // if a move is being calculated then ask it to stop because game is being reset
				}
			}
		}
		// give up
		else if(msgID == -1){
			long runningTime = (System.nanoTime() - startOfGameTime)/1000000;
			if(thread != null && runningTime > 800){ // 800 ms delay before one can press this btn
				System.out.println("gave up time: " + runningTime);
				interruptedBy = -2;
				if(controller.getPlayerTurn()==1 && player1Type == 1 ||
						controller.getPlayerTurn()==2 && player2Type == 1){
					endGame(-5);
				}else{
					thread.interrupt(); // if a move is being calculated then ask it to stop because game is being reset
				}
			}else{
				System.out.println("NOT ALLOWED TO GIVE UP YET time: " + runningTime);
			}
		}
		// nothing else should be clickable when game is on!
		else if(isGameOn){

		}
		// difficulty
		else if(msgID == 1){ // player 1 is set to easy
			setDifficulty(1, 1);
		}else if(msgID == 2){ // player 1 is set to normal
			setDifficulty(1, 2);
		}else if(msgID == 3){ // player 1 is set to hard
			setDifficulty(1, 3);
		}else if(msgID == 4){ // player 2 is set to easy
			setDifficulty(2, 1);
		}else if(msgID == 5){ // player 2 is set to normal
			setDifficulty(2, 2);
		}else if(msgID == 6){ // player 2 is set to hard
			setDifficulty(2, 3);
		}
		// player type
		else if(msgID == 7){ // player 1 is set to human
			setPlayerType(1, 1);
		}else if(msgID == 8){ // player 1 is set to computer
			setPlayerType(1, 2);
		}else if(msgID == 9){ // player 2 is set to human
			setPlayerType(2, 1);
		}else if(msgID == 10){ // player 2 is set to computer
			setPlayerType(2, 2);
		}
		// input to name
		else if(msgID == 11){ // player 1 name is selected
			if(player1Type==1){
				//imageNameFieldHumanS
				setImage(524, 629, imageNameFieldHumanS);
				isNameSelected1 = true;
			}
		}else if(msgID == 12){ // player 2 name is selected
			if(player2Type==1){
				setImage(524, 687, imageNameFieldHumanS);
				isNameSelected2 = true;
			}
		}
		// player 1 algorithm
		else if(msgID == 13){ // player 1 algorithm left arrow pressed
			setAlgorithm(1, -1, false, true);
		}else if(msgID == 14){ // player 1 algorithm is selected
			setImage(552, 648, imageTwoLettersBoxS);
			isAlgorithmSelected1 = true;
		}else if(msgID == 15){ // player 1 algorithm right arrow pressed
			setAlgorithm(1, -1, true, false);
		}
		// player 2 algorithm
		else if(msgID == 16){ // player 2 algorithm left arrow pressed
			setAlgorithm(2, -1, false, true);
		}else if(msgID == 17){ // player 2 algorithm is selected
			setImage(552, 706, imageTwoLettersBoxS);
			isAlgorithmSelected2 = true;
		}else if(msgID == 18){ // player 2 algorithm right arrow pressed
			setAlgorithm(2, -1, true, false);
		}
		// game
		else if(msgID == 19){ // game type left arrow pressed
			setGame(-1, false, true);
		}else if(msgID == 20){ // game type is selected
			setImage(494, 616, imageTwoLettersBoxS);
			isGameSelected = true;
		}else if(msgID == 21){ // game type right arrow pressed
			setGame(-1, true, false);
		}else if(msgID == 22){ // game settings reset button have been pressed
			resetValues();
		}else if(msgID == 23){ // reset match score button have been pressed
			resetMatchScore();
		}
		// x
		else if(msgID == 24){ // x left arrow pressed
			setX(-1, false, true);
		}else if(msgID == 25){ // x is selected
			setImage(494, 634, imageTwoLettersBoxS);
			isXSelected = true;
		}else if(msgID == 26){ // x right arrow pressed
			setX(-1, true, false);
		}
		// y
		else if(msgID == 27){ // y left arrow pressed
			setY(-1, false, true);
		}else if(msgID == 28){ // y is selected
			setImage(494, 652, imageTwoLettersBoxS);
			isYSelected = true;
		}else if(msgID == 29){ // y right arrow pressed
			setY(-1, true, false);
		}
		// AIMsDelay
		else if(msgID == 30){ // AIMsDelay left arrow pressed
			setAIMsDelay(-1, false, true);
		}else if(msgID == 31){ // AIMsDelay is selected
			setImage(490, 670, imageFourLettersBoxS);
			isAIMsDelaySelected = true;
		}else if(msgID == 32){ // AIMsDelay right arrow pressed
			setAIMsDelay(-1, true, false);
		}
		// start game button
		else if(msgID == 33){ // start game is pressed
			long runningTime = (System.nanoTime() - startOfGameTime)/1000000;
			if(runningTime > 800){ // 800 ms delay before one can press this btn
				oldNumberOfIterations = numberOfIterations;
				start();
			}else{
				System.out.println("Not allowed to start yet, time: " + runningTime);
			}
		}
		// score
		else if(msgID == 34){ // score up button pressed
			scrollScore(true, false);
		}else if(msgID == 35){ // score down button pressed
			scrollScore(false, true);
		}else if(msgID == 36){ // score reset button pressed
			resetScore();
		}
		// console
		else if(msgID == 37){ // console up button pressed
			scrollConsole(true, false);
		}else if(msgID == 38){ // console down button pressed
			scrollConsole(false, true);
		}else if(msgID == 39){ // console reset button pressed
			resetConsole();
		}
		// presets
		else if(msgID == 40){ // preset 1 button pressed
			presets(1);
		}else if(msgID == 41){ // preset 2 button pressed
			presets(2);
		}else if(msgID == 42){ // preset 3 button pressed
			presets(3);
		}else if(msgID == 43){ // preset 4 button pressed
			presets(4);
		}else if(msgID == 44){ // preset 5 button pressed
			presets(5);
		}else if(msgID == 45){ // preset 6 button pressed
			presets(6);
		}else if(msgID == 46){ // preset 7 button pressed
			presets(7);
		}else if(msgID == 47){ // preset 8 button pressed
			presets(8);
		}
		// setGUIUpdate
		else if(msgID == 48){ // GUI on or off button pressed
			setGUIUpdate(0);
		}
		// setTestingStatus
		else if(msgID == 49){ // Test status on or off button pressed
			setTestingStatus(0);
		}
		// select number of iterations
		else if(msgID == 50){ // Number of iterations is selected
			if(player1Type == 2 && player2Type == 2 && isTesting){ // both players must be computer
				setImage(506, 690, imageThreeLettersBoxS);
				isNumberOfIterationsSelected = true;
			}
		}
		// fields - not active areas on the GUI
		// player fields
		else if(msgID == 51){ // field - player 1

		}else if(msgID == 52){ // field - player 2

		}
		// match field
		else if(msgID == 53){ // field - player 1 match score

		}else if(msgID == 54){ // field - match score draw and number of played games

		}else if(msgID == 55){ // field - player 2 match score

		}
		// score field
		else if(msgID == 56){ // field - score information

		}
		// console field
		else if(msgID == 57){ // field - console information

		}
		// rules field
		else if(msgID == 58){ // field - game rules information

		}
		// stats field
		else if(msgID == 59){ // field - game statistics information

		}
		// loading field
		else if(msgID == 60){ // field - time information

		}
		// board field
		else if(msgID == 61){ // field - board

		}
		else{ // nothing is selected

		}
	}

	private void start(){
		// starts the game by calling the method in the controller
		boolean isStarted = controller.startGame(difficulty1, difficulty2, x, y, player1Name, player2Name, player1Type, player2Type, game, algorithm1, algorithm2);
		if(isStarted){
			int[][] grid = controller.getGrid();
			// the x and y here may not be the same that the logic have determined it to be. xx and yy holds the original grid sizes for later reversal in endGame
			xx = x;
			yy = y;
			x = grid.length;
			y = grid[0].length;
			interruptedBy = 0; // reset interruptedBy to make sure the timeout text wont reappear
			isGameOn = true;
			console += "\ngame started";
			populateConsole(false);
			setImage(596, 631, imageGiveUpButton);
			displayScore(0);
			startTime = System.nanoTime(); // start recording time taken
			startOfGameTime = System.nanoTime(); // start recording time for whole game
			displayPlayerProcessing(true);
			populateCurrentGameStats();
			thread = new Thread(){public void run(){controller.move(-1);}};
			thread.start(); // start managing turns. if computer starts this line is neccesary
		}else{
			console += "\ngame could not start\ndifficulty "+difficulty1+" "+difficulty2+" x"+x+" y"+y+" "+player1Name+" "+player2Name+" type1 "+player1Type+" type2 "+player2Type+"\ngame "+game+" AI1 "+algorithm1+" AI2 "+algorithm2;
			populateConsole(false);
		}
	}

	private void resetValues(){
		difficulty1 = 2;
		difficulty2 = 2;
		player1Type = 2;
		player2Type = 2;
		algorithm1 = 1;
		algorithm2 = 1;
		player1Name = controller.getName(difficulty1, player1Type==2?true:false, 1);
		player2Name = controller.getName(difficulty2, player2Type==2?true:false, 2);
		game = 1;
		AIMsDelay = 500;
		console += "\nSettings have been reset";
		scrollStateScore = 0;
		scrollStateConsole = 0;
		displayValues();
	}

	private void resetScore(){
		controller.resetAllStat();
		scrollStateScore = 0;
		populateScore();
		console += "\nAll score stats have been reset.";
		populateConsole(false);
		displayScore(0); // when everything have been reset remember to also update the score display
	}

	private void resetConsole(){
		console = "Console reset.";
		scrollStateConsole = 0;
		populateConsole(true);
	}

	private void scrollScore(boolean isScrollingUp, boolean isScrollingDown){
		if(isScrollingUp){
			scrollStateScore--;
		}else if(isScrollingDown){
			scrollStateScore++;
		}
		populateScore();
	}

	private void scrollConsole(boolean isScrollingUp, boolean isScrollingDown){
		if(isScrollingUp){
			scrollStateConsole--;
		}else if(isScrollingDown){
			scrollStateConsole++;
		}
		populateConsole(true);
	}

	private void setDifficulty(int player, int difficulty){
		int easyX = 0, easyY = 0, normalX = 0, normalY = 0, hardX = 0, hardY = 0, oldDif = 0;
		if(player == 1){
			easyX   = 525;
			easyY   = 611;
			normalX = 536;
			normalY = 611;
			hardX   = 547;
			hardY   = 611;
			oldDif = difficulty1;
		}else if(player == 2){
			easyX   = 525;
			easyY   = 669;
			normalX = 536;
			normalY = 669;
			hardX   = 547;
			hardY   = 669;
			oldDif = difficulty2;
		}else{return;} // error


		oldDif = difficulty;
		if(oldDif == 1){
			setImage(easyX, easyY, imageEasySelected);
		}else if(oldDif == 2){
			setImage(normalX, normalY, imageNormalSelected);
		}else if(oldDif == 3){
			setImage(hardX, hardY, imageHardSelected);
		}
		if(difficulty != 1){
			setImage(easyX, easyY, imageEasyUnselected);
		}
		if(difficulty != 2){
			setImage(normalX, normalY, imageNormalUnselected);
		}
		if(difficulty != 3){
			setImage(hardX, hardY, imageHardUnselected);
		}
		if(player == 1){
			if(difficulty1 != oldDif){ // only run if it has changed
				difficulty1 = oldDif;
				setName(1, controller.getName(difficulty1, player1Type==2?true:false, 1));
			}
		}else if(player == 2){
			if(difficulty2 != oldDif){ // only run if it has changed
				difficulty2 = oldDif;
				setName(2, controller.getName(difficulty2, player2Type==2?true:false, 2));
			}
		}
		console += "\nplayer"+player+" difficulty "+" set to "+difficulty;
		populateConsole(false);
	}

	private void setPlayerType(int player, int playerType){
		int humanTypeX = 0, humanTypeY = 0, computerTypeX = 0, computerTypeY = 0, nameFieldX = 0, nameFieldY = 0;
		if(player == 1){ // human
			humanTypeX = 559;
			humanTypeY = 602;
			computerTypeX = 559;
			computerTypeY = 612;
			nameFieldX = 524;
			nameFieldY = 629;
			if(player1Type != playerType){ // only run if it has changed
				player1Type = playerType;
				setName(1, controller.getName(difficulty1, player1Type==2?true:false, 1));
			}
		}else if(player == 2){ // computer
			humanTypeX = 559;
			humanTypeY = 660;
			computerTypeX = 559;
			computerTypeY = 670;
			nameFieldX = 524;
			nameFieldY = 687;
			if(player2Type != playerType){ // only run if it has changed
				player2Type = playerType;
				setName(2, controller.getName(difficulty2, player2Type==2?true:false, 2));	
			}
		}else{return;} // error

		if(playerType == 1){
			setImage(humanTypeX, humanTypeY, imageHumanSelected);
			setImage(computerTypeX, computerTypeY, imageComputerUnselected);
			setImage(nameFieldX, nameFieldY, imageNameFieldHuman);
			if(isTestingWithoutBoard){
				setGUIUpdate(1); // if a player is human and isTestingWithoutBoard is set then revert that
			}
			if(isTesting){
				setTestingStatus(1);
			}
			if(numberOfIterations != 0){
				setNumberOfIterations(0);
			}
		}else{
			setImage(humanTypeX, humanTypeY, imageHumanUnselected);
			setImage(computerTypeX, computerTypeY, imageComputerSelected);
			setImage(nameFieldX, nameFieldY, imageNameFieldComputer);
		}
		console += "\nplayer"+player+" type set to "+playerType;
		populateConsole(false);
	}

	private void setAlgorithm(int player, int specificAlgo, boolean isIncrementing, boolean isDecrementing){
		int algoNumX = 0, algoNumY = 0, algoNameX = 0, algoNameY = 0, oldAlgo = 0;
		String algoName = "";
		if(player == 1){
			algoNumX  = 552+2;
			algoNumY  = 648+2;
			algoNameX = 536;
			algoNameY = 640;
			oldAlgo = algorithm1;
		}else if(player == 2){
			algoNumX  = 552+2;
			algoNumY  = 706+2;
			algoNameX = 536;
			algoNameY = 698;
			oldAlgo = algorithm2;
		}else{return;} // error

		if(specificAlgo != -1){
			if(specificAlgo > 0 && specificAlgo <= controller.getNumberOfAlgorithms()){
				oldAlgo = specificAlgo;
			}else{
				return; // if input is incorrectly set then return instantly
			}
		}else if(isIncrementing){
			if(oldAlgo == controller.getNumberOfAlgorithms()){
				oldAlgo = 1;
			}else{oldAlgo++;}
		}else if(isDecrementing){
			if(oldAlgo == 1){
				oldAlgo = controller.getNumberOfAlgorithms();
			}else{oldAlgo--;}
		}

		if(player == 1){
			controller.setAlgorithm(oldAlgo, -1);
			algoName = controller.getAlgo1().getAlgorithmName();
			algorithm1 = oldAlgo;
		}else if(player == 2){
			controller.setAlgorithm(-1, oldAlgo);
			algoName = controller.getAlgo2().getAlgorithmName();
			algorithm2 = oldAlgo;
		}
		drawText(algoNumX, algoNumY, genNum(oldAlgo), board, 2);
		drawText(algoNameX, algoNameY, algoName, board, 14);
		console += "\nalgo"+player+" set to "+algoName;
		populateConsole(false);
	}

	private void setName(int player, String name){
		if(name.length() > 17){ // only select first 17 chars - no more room in the program
			name = name.substring(0, 17);
		}
		if(name.length() <= 0){ // check for otherwise invalid name
			console += "\nInvalid name";
			populateConsole(false);
			return;
		}
		int nameX, nameY;
		if(player == 1){
			nameX = 544;
			nameY = 622;
			player1Name = name;
		}else if(player == 2){
			nameX = 544;
			nameY = 680;
			player2Name = name;
		}else{return;} // error
		drawText(nameX, nameY, name, board, 12);
		console += "\nplayer "+player+" name set to "+name;
		populateConsole(false);
		displayScore(0);
	}

	private void setGame(int specificGame, boolean isIncrementing, boolean isDecrementing){
		if(specificGame != -1){
			if(specificGame > 0 && specificGame <= controller.getNumberOfGames()){
				game = specificGame;
			}else if(specificGame <= 0){
				game = 1;
			}else if(specificGame > controller.getNumberOfGames()){
				game = controller.getNumberOfGames();
			}
		}else if(isIncrementing){
			if(game == controller.getNumberOfGames()){
				game = 1;
			}else{game++;}
		}else if(isDecrementing){
			if(game == 1){
				game = controller.getNumberOfGames();
			}else{game--;}
		}
		controller.setGameMode(game);

		imageBlueIcons = new Image[controller.getBlueIcons().length];
		for (int i = 0; i < controller.getBlueIcons().length; i++) {
			imageBlueIcons[i] = new javax.swing.ImageIcon(getClass().getResource(controller.getBlueIcons()[i])).getImage();
		}
		imageBlueIconsSmall = new Image[controller.getBlueIconsSmall().length];
		for (int i = 0; i < controller.getBlueIconsSmall().length; i++) {
			imageBlueIconsSmall[i] = new javax.swing.ImageIcon(getClass().getResource(controller.getBlueIconsSmall()[i])).getImage();
		}
		imageRedIcons = new Image[controller.getRedIcons().length];
		for (int i = 0; i < controller.getRedIcons().length; i++) {
			imageRedIcons[i] = new javax.swing.ImageIcon(getClass().getResource(controller.getRedIcons()[i])).getImage();
		}
		imageRedIconsSmall = new Image[controller.getRedIconsSmall().length];
		for (int i = 0; i < controller.getRedIconsSmall().length; i++) {
			imageRedIconsSmall[i] = new javax.swing.ImageIcon(getClass().getResource(controller.getRedIconsSmall()[i])).getImage();
		}

		drawText(494+2, 616+2, genNum(game), board, 2);
		drawText(478, 609, controller.getGameModeName(), board, 11); // over the setGame settings
		drawText(598, 618, centerText(controller.getGameModeName(), 21), board, 21); // over the start button
		drawText(1103, 602, controller.getGameModeName(), board, 15); // over the rules

		setX(controller.getSuggestedGridX(), false, false);
		setY(controller.getSuggestedGridY(), false, false);
		setBoard(null, null, -4);
		console += "\nGame changed to "+controller.getGameModeName();
		populateConsole(false);
		populateCurrentGameStats();
		populateRules();
	}

	private void setX(int specificX, boolean isIncrementing, boolean isDecrementing){
		if(specificX != -1){
			if(specificX >= controller.getMinGridX() && specificX <= controller.getMaxGridX()){
				x = specificX;
			}else if(specificX < controller.getMinGridX()){
				x = controller.getMinGridX();
			}else if(specificX > controller.getMaxGridX()){
				x = controller.getMaxGridX();
			}
		}else if(isIncrementing){
			if(x == controller.getMaxGridX()){
				x = controller.getMinGridX();
			}else{x++;}
		}else if(isDecrementing){
			if(x == controller.getMinGridX()){
				x = controller.getMaxGridX();
			}else{x--;}
		}
		drawText(494+2, 634+2, genNum(x), board, 2);
		drawText(486, 627, genNum(x), board, 2);
		setBoard(null, null, -4);
	}

	private void setY(int specificY, boolean isIncrementing, boolean isDecrementing){
		if(specificY != -1){
			if(specificY >= controller.getMinGridY() && specificY <= controller.getMaxGridY()){
				y = specificY;
			}else if(specificY < controller.getMinGridY()){
				y = controller.getMinGridY();
			}else if(specificY > controller.getMaxGridY()){
				y = controller.getMaxGridY();
			}
		}else if(isIncrementing){
			if(y == controller.getMaxGridY()){
				y = controller.getMinGridY();
			}else{y++;}
		}else if(isDecrementing){
			if(y == controller.getMinGridY()){
				y = controller.getMaxGridY();
			}else{y--;}
		}
		drawText(494+2, 652+2, genNum(y), board, 2);
		drawText(486, 645, genNum(y), board, 2);
		setBoard(null, null, -4);
	}

	private void setAIMsDelay(int specificMsDelay, boolean isIncrementing, boolean isDecrementing){
		if(specificMsDelay != -1){
			if(specificMsDelay >= 0 && specificMsDelay <= 9999){
				AIMsDelay = specificMsDelay;
			}else{
				return; // if input is incorrectly set then return instantly
			}
		}else if(isIncrementing){
			if(AIMsDelay == 9999){
				AIMsDelay = 0;
			}else if(AIMsDelay >= 9900){
				AIMsDelay = 9999;
			}else{AIMsDelay += 100;}
		}else if(isDecrementing){
			if(AIMsDelay <= 99){
				AIMsDelay = 9999;
			}else{AIMsDelay -= 100;}
		}
		drawText(490+2, 670+2, genNum(AIMsDelay), board, 4);
		drawText(506, 663, genNum(AIMsDelay), board, 4);
		console += "\nAI ms. delay changed to "+AIMsDelay;
		populateConsole(false);
	}

	private void presets(int presetNumber){
		String p1Name = "", p2Name ="";// TicTacToe, FourInLine, ConnectFour, Reversi, Chess, Checkers, Mills
		int game = 0, x = 0, y = 0, AIMsDelay = 0, p1Type = 0, p2Type = 0, p1Algo = 0, p2Algo = 0, p1Diff = 0, p2Diff = 0, GUIUpdate = 0, isTesting = 0, numOfIter = 0;
		if(presetNumber == 1){
			game = 3; x = 9; y = 9;
			AIMsDelay = 0;
			p1Name = "preset-AI1"; p2Name = "preset-AI2";
			p1Type = 2; p2Type = 2;
			p1Algo = 1; p2Algo = 1;
			p1Diff = 2; p2Diff = 2;
			GUIUpdate = 2;
			isTesting = 2; numOfIter = 0;
		}else if(presetNumber == 2){
			game = 4; x = 8; y = 8;
			AIMsDelay = 0;
			p1Name = "preset-AI1"; p2Name = "preset-AI2";
			p1Type = 2; p2Type = 2;
			p1Algo = 1; p2Algo = 1;
			p1Diff = 2; p2Diff = 2;
			GUIUpdate = 2;
			isTesting = 2; numOfIter = 0;
		}else if(presetNumber == 3){
			game = 6; x = 8; y = 8;
			AIMsDelay = 0;
			p1Name = "preset-AI1"; p2Name = "preset-AI2";
			p1Type = 2; p2Type = 2;
			p1Algo = 1; p2Algo = 1;
			p1Diff = 2; p2Diff = 2;
			GUIUpdate = 2;
			isTesting = 2; numOfIter = 0;
		}else if(presetNumber == 4){
			game = 1; x = 3; y = 3;
			AIMsDelay = 0;
			p1Name = "preset-AI1"; p2Name = "preset-AI2";
			p1Type = 2; p2Type = 2;
			p1Algo = 1; p2Algo = 1;
			p1Diff = 2; p2Diff = 2;
			GUIUpdate = 2;
			isTesting = 2; numOfIter = 10;
		}else if(presetNumber == 5){
			game = 5; x = 8; y = 8;
			AIMsDelay = 0;
			p1Name = "preset-AI1"; p2Name = "preset-AI2";
			p1Type = 2; p2Type = 2;
			p1Algo = 1; p2Algo = 1;
			p1Diff = 2; p2Diff = 2;
			GUIUpdate = 2;
			isTesting = 2; numOfIter = 0;
		}else if(presetNumber == 6){
			game = 7; x = 8; y = 8;
			AIMsDelay = 0;
			p1Name = "preset-AI1"; p2Name = "preset-AI2";
			p1Type = 2; p2Type = 2;
			p1Algo = 1; p2Algo = 1;
			p1Diff = 2; p2Diff = 2;
			GUIUpdate = 2;
			isTesting = 2; numOfIter = 0;
		}else if(presetNumber == 7){
			game = 2; x = 70; y = 32;
			AIMsDelay = 0;
			p1Name = "preset-AI1"; p2Name = "preset-AI2";
			p1Type = 2; p2Type = 2;
			p1Algo = 3; p2Algo = 3;
			p1Diff = 2; p2Diff = 2;
			GUIUpdate = 2;
			isTesting = 2; numOfIter = 0;
		}else if(presetNumber == 8){
			game = 3; x = 70; y = 32;
			AIMsDelay = 0;
			p1Name = "preset-AI1"; p2Name = "preset-AI2";
			p1Type = 2; p2Type = 2;
			p1Algo = 3; p2Algo = 3;
			p1Diff = 2; p2Diff = 2;
			GUIUpdate = 2;
			isTesting = 2; numOfIter = 0;
		}else{return;}
		setGame(game, false, false);
		setX(x, false, false);
		setY(y, false, false);
		setAIMsDelay(AIMsDelay, false, false);
		player1Name = p1Name; // sets name before running the below methods - 
		player2Name = p2Name; // to cheat the update displayScore to not create extra blank scores Normal-Computer1 preset2 [][][][]
		setPlayerType(1, p1Type);
		setPlayerType(2, p2Type);
		setAlgorithm(1, p1Algo, false, false);
		setAlgorithm(2, p2Algo, false, false);
		setDifficulty(1, p1Diff);
		setDifficulty(2, p2Diff);
		setName(1, p1Name);
		setName(2, p2Name);
		setGUIUpdate(GUIUpdate);
		setTestingStatus(isTesting);
		setNumberOfIterations(numOfIter);
		console += "\nPreset "+controller.getGameModeName()+" set";
		populateConsole(false);
	}

	private void resetMatchScore(){
		controller.resetCurrentStat();
		displayScore(0);
		populateScore();
		console += "\nScore have been reset.";
		populateConsole(false);
	}

	private void setGUIUpdate(int set){
		// if set is 0 then toggle, if 1 then false if 2 then true
		if(set==1 || set==0 && isTestingWithoutBoard || player1Type==1 || player2Type==1){
			isTestingWithoutBoard = false;
			setImage(478, 681, imageGUIOn);
			console += "\nThe board will be shown";
			populateConsole(false);
		}else if((set==2 || set==0 && !isTestingWithoutBoard) && player1Type==2 && player2Type==2){ // if a player is human then one cannot disable gui updates
			isTestingWithoutBoard = true;
			setImage(478, 681, imageGUIOff);
			console += "\nThe board will not be shown";
			populateConsole(false);
		}
	}

	private void setTestingStatus(int set){
		// if set is 0 then toggle, if 1 then false if 2 then true
		if(set==1 || set==0 && isTesting || player1Type==1 || player2Type==1){
			isTesting = false;
			drawText(478+2, 690+2, " Play ", board, 6);
			console += "\nTesting inactive";
			populateConsole(false);
		}else if((set == 2 || set==0 && !isTesting) && player1Type==2 && player2Type==2){
			isTesting = true;
			drawText(478+2, 690+2, " Test ", board, 6);
			console += "\nTesting active";
			populateConsole(false);
		}
	}

	private void setNumberOfIterations(int specificNumberOfIterations) {
		if(specificNumberOfIterations != 0 && (specificNumberOfIterations > 1000 || specificNumberOfIterations < 0 || !isTesting)){
			drawText(506+2, 690+2, genNum(numberOfIterations), board, 3);
		}else{
			numberOfIterations = specificNumberOfIterations;
			drawText(506+2, 690+2, genNum(numberOfIterations), board, 3);
			console += "\nNumber of iterations changed to "+numberOfIterations;
			populateConsole(false);
		}
	}

	private int getGUIElement(int x, int y){
		int id = 0;
		// difficulty
		if(x >= 525 && y >= 611 && x <= 534 && y <= 620){ // player 1 is set to easy
			id = 1;
		}else if(x >= 536 && y >= 611 && x <= 545 && y <= 620){ // player 1 is set to normal
			id = 2;
		}else if(x >= 547 && y >= 611 && x <= 556 && y <= 620){ // player 1 is set to hard
			id = 3;
		}else if(x >= 525 && y >= 669 && x <= 534 && y <= 678){ // player 2 is set to easy
			id = 4;
		}else if(x >= 536 && y >= 669 && x <= 545 && y <= 678){ // player 2 is set to normal
			id = 5;
		}else if(x >= 547 && y >= 669 && x <= 556 && y <= 678){ // player 2 is set to hard
			id = 6;
		}
		// player type
		else if(x >= 560 && y >= 603 && x <= 592 && y <= 610){ // player 1 is set to human
			id = 7;
		}else if(x >= 560 && y >= 612 && x <= 592 && y <= 619){ // player 1 is set to computer
			id = 8;
		}else if(x >= 560 && y >= 661 && x <= 592 && y <= 668){ // player 2 is set to human
			id = 9;
		}else if(x >= 560 && y >= 670 && x <= 592 && y <= 677){ // player 2 is set to computer
			id = 10;
		}
		// input to name
		else if(x >= 524 && y >= 629 && x <= 591 && y <= 638){ // player 1 name is selected
			id = 11;
		}else if(x >= 524 && y >= 687 && x <= 591 && y <= 696){ // player 2 name is selected
			id = 12;
		}
		// player 1 algorithm
		else if(x >= 536 && y >= 648 && x <= 550 && y <= 657){ // player 1 algorithm left arrow pressed
			id = 13;
		}else if(x >= 552 && y >= 648 && x <= 562 && y <= 657){ // player 1 algorithm is selected
			id = 14;
		}else if(x >= 564 && y >= 648 && x <= 578 && y <= 657){ // player 1 algorithm right arrow pressed
			id = 15;
		}
		// player 2 algorithm
		else if(x >= 536 && y >= 706 && x <= 550 && y <= 715){ // player 2 algorithm left arrow pressed
			id = 16;
		}else if(x >= 552 && y >= 706 && x <= 562 && y <= 715){ // player 2 algorithm is selected
			id = 17;
		}else if(x >= 564 && y >= 706 && x <= 578 && y <= 715){ // player 2 algorithm right arrow pressed
			id = 18;
		}
		// game
		else if(x >= 478 && y >= 616 && x <= 492 && y <= 625){ // game type left arrow pressed
			id = 19;
		}else if(x >= 494 && y >= 616 && x <= 504 && y <= 625){ // game type is selected
			id = 20;
		}else if(x >= 506 && y >= 616 && x <= 520 && y <= 625){ // game type right arrow pressed
			id = 21;
		}else if(x >= 512 && y >= 683 && x <= 518 && y <= 688){ // game settings reset button have been pressed
			if(!isGameOn){id = 22;} // reset settings
			else{id = -2;} // abort game
		}else if(x >= 636 && y >= 686 && x <= 643 && y <= 691){ // reset match score button have been pressed
			id = 23;
		}
		// x
		else if(x >= 478 && y >= 634 && x <= 492 && y <= 643){ // x left arrow pressed
			id = 24;
		}else if(x >= 494 && y >= 634 && x <= 504 && y <= 643){ // x is selected
			id = 25;
		}else if(x >= 506 && y >= 634 && x <= 520 && y <= 643){ // x right arrow pressed
			id = 26;
		}
		// y
		else if(x >= 478 && y >= 652 && x <= 492 && y <= 661){ // y left arrow pressed
			id = 27;
		}else if(x >= 494 && y >= 652 && x <= 504 && y <= 661){ // y is selected
			id = 28;
		}else if(x >= 506 && y >= 652 && x <= 520 && y <= 661){ // y right arrow pressed
			id = 29;
		}
		// AIMsDelay
		else if(x >= 478 && y >= 670 && x <= 488 && y <= 679){ // AIMsDelay left arrow pressed
			id = 30;
		}else if(x >= 490 && y >= 670 && x <= 508 && y <= 679){ // AIMsDelay is selected
			id = 31;
		}else if(x >= 510 && y >= 670 && x <= 520 && y <= 679){ // AIMsDelay right arrow pressed
			id = 32;
		}
		// start game button
		else if(x >= 597 && y >= 632 && x <= 682 && y <= 687){ // start game is pressed
			if(!isGameOn){id = 33;} // start game
			else{id = -1;} // give up
		}
		// score
		else if(x >= 230 && y >= 609 && x <= 236 && y <= 613){ // score up button pressed
			id = 34;
		}else if(x >= 230 && y >= 713 && x <= 236 && y <= 717){ // score down button pressed
			id = 35;
		}else if(x >= 230 && y >= 602 && x <= 236 && y <= 607){ // score reset button pressed
			id = 36;
		}
		// console
		else if(x >= 468 && y >= 609 && x <= 474 && y <= 613){ // console up button pressed
			id = 37;
		}else if(x >= 468 && y >= 713 && x <= 474 && y <= 717){ // console down button pressed
			id = 38;
		}else if(x >= 468 && y >= 602 && x <= 474 && y <= 607){ // console reset button pressed
			id = 39;
		}
		// presets
		else if(x >= 480 && y >= 701 && x <= 486 && y <= 706){ // preset 1 button pressed
			id = 40;
		}else if(x >= 490 && y >= 701 && x <= 496 && y <= 706){ // preset 2 button pressed
			id = 41;
		}else if(x >= 480 && y >= 710 && x <= 486 && y <= 715){ // preset 3 button pressed
			id = 42;
		}else if(x >= 490 && y >= 710 && x <= 496 && y <= 715){ // preset 4 button pressed
			id = 43;
		}else if(x >= 502 && y >= 701 && x <= 508 && y <= 706){ // preset 5 button pressed
			id = 44;
		}else if(x >= 512 && y >= 701 && x <= 518 && y <= 706){ // preset 6 button pressed
			id = 45;
		}else if(x >= 502 && y >= 710 && x <= 508 && y <= 715){ // preset 7 button pressed
			id = 46;
		}else if(x >= 512 && y >= 710 && x <= 518 && y <= 715){ // preset 8 button pressed
			id = 47;
		}
		// setGUIUpdate
		else if(x >= 479 && y >= 682 && x <= 507 && y <= 689){ // GUI on or off button pressed
			id = 48;
		}
		// setTestingStatus
		else if(x >= 479 && y >= 691 && x <= 503 && y <= 698){ // Test status on or off button pressed
			id = 49;
		}
		// select number of iterations
		else if(x >= 507 && y >= 691 && x <= 519 && y <= 698){ // Number of iterations is selected
			id = 50;
		}
		// fields - not active areas on the GUI
		// player fields
		else if(x >= 522 && y >= 602 && x <= 593 && y <= 659){ // field - player 1
			id = 51;
		}else if(x >= 522 && y >= 660 && x <= 593 && y <= 717){ // field - player 2
			id = 52;
		}
		// match field
		else if(x >= 597 && y >= 691 && x <= 624 && y <= 717){ // field - player 1 match score
			id = 53;
		}else if(x >= 626 && y >= 691 && x <= 653 && y <= 717){ // field - match score draw and number of played games
			id = 54;
		}else if(x >= 655 && y >= 691 && x <= 682 && y <= 717){ // field - player 2 match score
			id = 55;
		}
		// score field
		else if(x >= 2 && y >= 609 && x <= 236 && y <= 717){ // field - score information
			id = 56;
		}
		// console field
		else if(x >= 240 && y >= 609 && x <= 474 && y <= 717){ // field - console information
			id = 57;
		}
		// rules field
		else if(x >= 1043 && y >= 609 && x <= 1277 && y <= 717){ // field - game rules information
			id = 58;
		}
		// stats field
		else if(x >= 805 && y >= 602 && x <= 1039 && y <= 717){ // field - game statistics information
			id = 59;
		}
		// loading field
		else if(x >= 685 && y >= 601 && x <= 802 && y <= 718){ // field - time information
			id = 60;
		}
		// board field
		else if(x >= 1 && y >= 1 && x <= 1278 && y <= 598){ // field - board
			Image imgEmpty;
			if(this.x > 24 || this.y > 11){ // the board is too big - use a smaller sized board
				imgEmpty = imageEmptySmall;
			}else{
				imgEmpty = imageEmpty;
			}
			int gapX = 3;
			int gapY = 3;
			int cellX = imgEmpty.getWidth(null)+gapX;
			int cellY = imgEmpty.getHeight(null)+gapY;
			int sizeX = (this.x*cellX);
			int sizeY = (this.y*cellY);
			int locationX = 640 - (sizeX/2);
			int locationY = 300 - (sizeY/2);
			for (int i = 0; i < this.x; i++) {
				for (int j = 0; j < this.y; j++) {
					if(x >= locationX+(cellX*(i)) && y >= locationY+(cellY*(j)) && 
							x <= locationX+(cellX*(i+1)) && y <= locationY+(cellY*(j+1))){
						id = 100000 + i + (j * this.x); // specific cell val
						break;
					}
				}
				if(id!=0){break;}
			}
			if(id==0){id = 61;} // board
		}
		else{ // nothing is selected
			id = 0;
		}

		return id;
	}

	private void displayPlayerProcessing(boolean isRunning){
		if(isRunning){
			playerProcessingThread = new Thread(){
				public void run(){
					while(true){
						try {
							sleep(200); // sleep because the thread only updates seconds and the loading icon
						} catch (InterruptedException e) {
							Graphics2D g2 = (Graphics2D) board.getGraphics();
							g2.setColor(new Color(195,195,195));
							g2.fillRect(685, 601, 118, 118); // reset area
							if(interruptedBy == -6){
								drawText(686, 712, centerText("Your time is out...", 29), board, 29);
							}
							return;
						}
						long timeTakenInMili = (System.nanoTime() - startTime)/1000000;
						int timeInSec = (int) timeTakenInMili/1000;
						String timeMSG = "";
						if(controller.getPlayerTurn()==1 && player1Type == 1 && difficulty1 != 1){ // player 1 is human and it is his turn
							int difficultyTime = 0;
							if(difficulty1==1){difficultyTime = -1;} // easy
							else if(difficulty1==2){difficultyTime = 60;} // normal
							else if(difficulty1==3){difficultyTime = 5;} // hard
							int timeLeft = difficultyTime - timeInSec;
							if(timeLeft >= 1){timeMSG = "Time Left " + timeLeft;}
							else{timeMSG = "Timeout";interruptedBy = -6;endGame(-5);}
						}else if(controller.getPlayerTurn()==2 && player2Type == 1 && difficulty2 != 1){ // player 2 is human and it is his turn
							int difficultyTime = 0;
							if(difficulty2==1){difficultyTime = -1;} // easy
							else if(difficulty2==2){difficultyTime = 60;} // normal
							else if(difficulty2==3){difficultyTime = 5;} // hard
							int timeLeft = difficultyTime - timeInSec;
							if(timeLeft >= 1){timeMSG = "Time Left " + timeLeft;}
							else{timeMSG = "Timeout";interruptedBy = -6;endGame(-5);}
						}else{ // computer or easy player
							timeMSG = "" + timeInSec;
						}
						setImage(694, 607, imageLoadingSmall);
						drawText(686, 712, centerText(timeMSG, 29), board, 29);
					}
				}
			};
			playerProcessingThread.start();
		}else{
			if(playerProcessingThread != null){
				playerProcessingThread.interrupt();
			}
		}
	}

	private void initialize(){
		frame = new JFrame();
		board = new Board(this);
		frame.add(board);
		frame.pack();
		frame.setTitle("HGames");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		board.requestFocusInWindow(); // makes sure that the KeyListener also works
	}

	private String genNum(int num){
		if(num > 9){return Integer.toString(num);}
		else{return "0" + Integer.toString(num);}
	}

	private void setImage(int x, int y, Image img){
		board.getGraphics().drawImage(img, x, y, null);
	}

	private void populateScore(){
		String allStats = controller.getAllStat();
		if(allStats == null){
			return;
		}
		String lines[] = allStats.split("\\r?\\n");
		int l = lines.length;
		if(lines.length > 15){ // if more than the maximum number of lines will be written
			l = l + scrollStateScore + -15;
			if(l < 0){l = 0; scrollStateScore++;}
			else if(l + 15 > lines.length){l = lines.length - 15;scrollStateScore--;}
			for (int i = l; i < 15+l; i++) {
				String print = "";
				print = lines[i];
				drawText(4, 611 + ((i-l)*7), print, board, 56);
			}
		}
		else{
			for (int i = 0; i < 15; i++) {
				String print = "";
				if(l > i){
					print = lines[i]; // if there are fewer than 15 lines then put in blank lines
				}
				drawText(4, 611 + (i*7), print, board, 56);
			}
		}
	}

	private void populateConsole(boolean rewrite){
		String lines[] = console.split("\\r?\\n");
		int l = lines.length;
		if(lines.length > 15){ // if more than the maximum number of lines will be written
			l = l + scrollStateConsole + -15;
			if(l < 0){l = 0; scrollStateConsole++;}
			else if(l + 15 > lines.length){l = lines.length - 15;scrollStateConsole--;}
			for (int i = l; i < 15+l; i++) {
				String print = "";
				print = lines[i];
				drawText(242, 611 + ((i-l)*7), print, board, 56);
			}
		}
		else if(rewrite){
			for (int i = 0; i < 15; i++) {
				String print = "";
				if(l > i){ // if there are fewer than 15 lines then put in blank lines
					print = lines[i];
				}
				drawText(242, 611 + (i*7), print, board, 56);
			}
		}
		else if(l <= 15){ // just write the newest line
			drawText(242, 611 + ((l-1)*7), lines[l-1], board, 56);
		}
	}

	private void populateRules(){
		String lines[] = controller.getGameRules().split("\\r?\\n|(?<=\\G.{58})"); // if there is a newline or we have reached the max amount of characters
		for (int i = 0; i < 15; i++) {
			String print = "";
			if(lines.length > i){
				print = lines[i]; // if there are fewer than 15 lines then put in blank lines
			}
			drawText(1045, 611 + (i*7), print, board, 58);
		}
	}

	private void populateCurrentGameStats(){
		String lines[] = controller.getCurrentGameStats().split("\\r?\\n|(?<=\\G.{58})"); // if there is a newline or we have reached the max amount of characters
		for (int i = 0; i < 16; i++) {
			String print = "";
			if(lines.length > i){
				print = lines[i]; // if there are fewer than 15 lines then put in blank lines
			}
			drawText(807, 604 + (i*7), print, board, 58);
		}
	}

	private void displayScore(int winner){
		// 0 = reset, -1 = draw, 1 = player1 wins, 2 player2 wins
		String blueText, redText, drawText;
		if(winner == -1){ // draw
			blueText = "draw";
			drawText = "draw";
			redText = "draw";
		}else if(winner == 1){ // player1 won
			blueText = "won";
			drawText = "";
			redText = "lost";
		}else if(winner == 2){ // player2 won
			blueText = "lost";
			drawText = "";
			redText = "won";
		}else{ // winner == 0 no results just set values to default
			blueText = "";
			drawText = "";
			redText = "";
		}
		controller.updatePlayers(player1Name, player2Name, player1Type, player2Type);
		drawText(608, 693, blueText, board, 4); // blue square
		drawText(628, 693, centerText(drawText, 6), board, 6); // draw square
		drawText(657, 693, redText, board, 4); // red square
		drawText(599, 710, genNum(controller.getPlayerNumWins(1)), board, 2); // blue won
		drawText(644, 710, genNum(controller.getNumOfDraws()), board, 2); // draws
		drawText(644, 703, genNum(controller.getNumOfPlayedGames()), board, 2); // sum
		drawText(674, 710, genNum(controller.getPlayerNumWins(2)), board, 2); // red won
	}

	private void setBoard(int[][] grid, boolean[][] selectableGrid, int gameOverResult){
		// gameOverResult can be: -1 = draw, 0 = game not over, 1 = player 1 won, 2 = player 2 won,
		// -2 = a player gave up, -3 = the reset button have been pressed, -4 = only interested in updating the board
		// display the grapics for the individual game
		// analyse size of the game - then center it
		int gapX;
		int gapY;
		Image imgEmpty;
		Image imgClosed;
		Image imgClickable;
		Image imgNotClickable;
		Image[] blueImages;
		Image[] redImages;
		if(x > 24 || y > 11){ // the board is too big - use a smaller sized board
			imgEmpty = imageEmptySmall;
			imgClosed = imageClosedSmall;
			imgClickable = imageClickableSmall;
			imgNotClickable = imageNotClickableSmall;
			blueImages = imageBlueIconsSmall;
			redImages = imageRedIconsSmall;
			gapX = 3;
			gapY = 3;
		}else{
			imgEmpty = imageEmpty;
			imgClosed = imageClosed;
			imgClickable = imageClickable;
			imgNotClickable = imageNotClickable;
			blueImages = imageBlueIcons;
			redImages = imageRedIcons;
			gapX = 3;
			gapY = 3;
		}
		int cellWidth = imgEmpty.getWidth(null);
		int cellHeight = imgClosed.getHeight(null);
		int sizeX = (x*(cellWidth+gapX));
		int sizeY = (y*(cellHeight+gapY));
		// size of board: 1280 600 center: 640 300
		int locationX = 640 - (sizeX/2);
		int locationY = 300 - (sizeY/2);
		Graphics2D g2 = (Graphics2D) board.getGraphics();
		if(storedGrid == null){
			g2.setColor(Color.gray);
			g2.fillRect(1, 1, 1278, 599); // reset the whole board
		}
		String resultText = "";
		if(gameOverResult == 0){ // game still on
			if(controller.getPlayerTurn()==1){
				g2.setColor(Color.blue);
				g2.fillRect(685, 601, 118, 110); // behind loading icon
				resultText = "player 1";
			}else if(controller.getPlayerTurn()==2){
				g2.setColor(Color.red);
				g2.fillRect(685, 601, 118, 110); // behind loading icon
				resultText = "player 2";
			}

			// top text
			String topText = "round "+controller.getRound()+" "+controller.getGameModeName();
			int wx = topText.length()*4;
			int wy = 6;
			int sx = 640-(wx/2);
			int sy = locationY-8;
			g2.setColor(new Color(195,195,195));
			g2.fillRect(sx, sy-2, wx, wy+2);
			drawText(sx, sy-1, topText, board, topText.length()); // top
		}else if(gameOverResult == 1){ // display player 1 won
			g2.setColor(Color.blue);
			g2.fillRect(1, 1, 1278, 599);
			resultText = player1Name+" won the "+controller.getGameModeName()+" game";
			console += "\ngame over player 1 won the "+controller.getGameModeName()+" game";
			populateConsole(false);
			populateScore(); // update score because values have changed when a game has ended
			displayScore(1);
		}else if(gameOverResult == 2){ // display player 2 won
			g2.setColor(Color.red);
			g2.fillRect(1, 1, 1278, 599);
			resultText = player2Name+" won the "+controller.getGameModeName()+" game";
			console += "\ngame over player 2 won the "+controller.getGameModeName()+" game";
			populateConsole(false);
			populateScore(); // update score because values have changed when a game has ended
			displayScore(2);
		}else if(gameOverResult == -1){ // display draw
			g2.setColor(Color.gray);
			g2.fillRect(1, 1, 1278, 599);
			resultText = "the game "+controller.getGameModeName()+" was a draw";
			console += "\ndraw";
			populateConsole(false);
			populateScore(); // update score because values have changed when a game has ended
			displayScore(-1);
		}else if(gameOverResult == -2){ // display give up
			int playerGaveUp = 0;
			if(controller.getPlayerTurn()==1){
				playerGaveUp = 1;
				resultText = player1Name+" gave up";
			}else if(controller.getPlayerTurn()==2){
				playerGaveUp = 2;
				resultText = player2Name+" gave up";
			}
			controller.setResult(playerGaveUp==1?2:1); // save results
			displayScore(playerGaveUp==1?2:1); // display results
			console += "\n" + resultText;
			populateConsole(false);
			return; // controller.setResult will display the rest of setBoard
		}else if(gameOverResult == -3){ // display reset game
			resultText = "the game "+controller.getGameModeName()+" has been reset";
			console += "\nGame interrupted - reset";
			populateConsole(false);
		}else if(gameOverResult == -6){ // timeout - same as give up just displays timeout instead of give up
			int playerTimedOut = 0;
			if(controller.getPlayerTurn()==1){
				playerTimedOut = 1;
				resultText = player1Name+" timed out";
			}else if(controller.getPlayerTurn()==2){
				playerTimedOut = 2;
				resultText = player2Name+" timed out";
			}
			controller.setResult(playerTimedOut==1?2:1); // save results
			displayScore(playerTimedOut==1?2:1); // display results
			console += "\n" + resultText;
			populateConsole(false);
			return; // controller.setResult will display the rest of setBoard
		}else if(gameOverResult == -4){ // do nothing - only update the board itself
			resultText = "press start button to start the game";
		}
		int wx = resultText.length()*4;
		int wy = 6;
		int sx = 640-(wx/2);
		int sy = locationY + sizeY;
		g2.setColor(new Color(195,195,195));
		g2.fillRect(sx, sy-1, wx, wy+2);
		drawText(sx, sy, resultText, board, resultText.length()); // bottom

		if(grid == null){ // blank - game not started
			for (int i = 0; i < x; i++) {
				for (int j = 0; j < y; j++) {
					setImage(locationX + (i*(cellWidth+gapX)), locationY + (j*(cellHeight+gapY)), imgClosed);
				}
			}
		}
		else{
			for (int i = 0; i < x; i++) {
				for (int j = 0; j < y; j++) {
					if(storedGrid != null && storedSelectableGrid != null &&
							storedGrid[i][j] == grid[i][j] && storedSelectableGrid[i][j] == selectableGrid[i][j] && gameOverResult == 0){
						// avoid updating something that has not changed
					}else{
						if(selectableGrid[i][j]){
							setImage(locationX + (i*(cellWidth+gapX))-1, locationY + (j*(cellHeight+gapY))-1, imgClickable);
						}else{
							setImage(locationX + (i*(cellWidth+gapX))-1, locationY + (j*(cellHeight+gapY))-1, imgNotClickable);
						}
						if(grid[i][j] == -1){ // this button cannot be clicked by the player
							setImage(locationX + (i*(cellWidth+gapX)), locationY + (j*(cellHeight+gapY)), imgClosed);
						}else if(grid[i][j] == 0){
							setImage(locationX + (i*(cellWidth+gapX)), locationY + (j*(cellHeight+gapY)), imgEmpty);
						}else if(grid[i][j] == 1){
							setImage(locationX + (i*(cellWidth+gapX)), locationY + (j*(cellHeight+gapY)), blueImages[0]);
						}else if(grid[i][j] == 2){
							setImage(locationX + (i*(cellWidth+gapX)), locationY + (j*(cellHeight+gapY)), redImages[0]);
						}
						// from 3 and upwards, and from -3 and downward on the grid we use unique icons
						else if(grid[i][j] >= 3){
							setImage(locationX + (i*(cellWidth+gapX)), locationY + (j*(cellHeight+gapY)), blueImages[grid[i][j]-3]);
						}
						else if(grid[i][j] <= -3){
							setImage(locationX + (i*(cellWidth+gapX)), locationY + (j*(cellHeight+gapY)), redImages[(grid[i][j]*-1)-3]);
						}
					}
				}
			}
		}
	}

	private String centerText(String text, int maxChar){
		int s = text.length();
		s = maxChar - s;
		if(s <= 0){
			return text;
		}else{
			s = s/2;
			String centerText = "";
			for (int i = 0; i < s; i++) {
				centerText += " ";
			}
			centerText += text;
			return centerText;
		}
	}
}

class Board extends JPanel {

	private static final long serialVersionUID = 1L;
	private final Image statusBar;
	private final MouseListener mouse;
	private final MouseMotionListener mouseMotion;
	private final KeyListener keyboard;
	private final GUIGameSwing guiGameSwing;

	public Board(GUIGameSwing guiGameSwing) {
		this.guiGameSwing = guiGameSwing;
		ImageIcon IIstatusBar = new javax.swing.ImageIcon(getClass().getResource("/pictures/game/statusBar.png"));
		statusBar = IIstatusBar.getImage();
		int statusBarWidth = statusBar.getWidth(this);
		int statusBarHeight =  statusBar.getHeight(this);
		setPreferredSize(new Dimension(statusBarWidth, statusBarHeight));

		mouse = new Mouse(this.guiGameSwing);
		addMouseListener(mouse);
		mouseMotion = new MouseMotion(this.guiGameSwing);
		addMouseMotionListener(mouseMotion);
		keyboard = new keyboard(this, this.guiGameSwing);
		addKeyListener(keyboard);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(statusBar, 0, 0, null);
	}
}

class Mouse implements MouseListener {

	private final GUIGameSwing g;

	public Mouse(GUIGameSwing g){
		this.g = g;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {}
	@Override
	public void mouseEntered(MouseEvent arg0) {}
	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) {
		g.buttonPressed(arg0.getX(), arg0.getY());
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {}

}

class MouseMotion implements MouseMotionListener{

	private final GUIGameSwing g;

	public MouseMotion(GUIGameSwing g){
		this.g = g;
	}

	@Override
	public void mouseDragged(MouseEvent e) {}

	@Override
	public void mouseMoved(MouseEvent e) {
		g.hoverMessage(e.getX(), e.getY());
	}
}

class keyboard implements KeyListener{

	private final Board board;
	private final GUIGameSwing g;

	public keyboard(Board board, GUIGameSwing g){
		this.board = board;
		this.g = g;
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		String c = "";

		if(g.isSplashscreenShown){ // only run at first
			g.displayValues();
			return;
		}
		else if(arg0.getKeyChar() == KeyEvent.VK_ENTER){
			System.out.println("Enter button have been pressed");
			g.deselect();
		}
		else if(arg0.getKeyChar() == KeyEvent.VK_BACK_SPACE){
			if(g.keyboardBuffer.length() > 0){
				g.keyboardBuffer = g.keyboardBuffer.substring(0, g.keyboardBuffer.length()-1); // delete last one
			}
			c = "";
		}
		else if(arg0.getKeyChar() != KeyEvent.CHAR_UNDEFINED){
			c = "" + arg0.getKeyChar();
		}

		if(g.isNameSelected1){
			g.keyboardBuffer += c;
			g.drawText(524+2, 629+2, g.keyboardBuffer, board, 16);
		}else if(g.isNameSelected2){
			g.keyboardBuffer += c;
			g.drawText(524+2, 687+2, g.keyboardBuffer, board, 16);
		}else if(g.isAlgorithmSelected1){
			g.keyboardBuffer += c;
			g.drawText(552+2, 648+2, g.keyboardBuffer, board, 2);
		}else if(g.isAlgorithmSelected2){
			g.keyboardBuffer += c;
			g.drawText(552+2, 706+2, g.keyboardBuffer, board, 2);
		}else if(g.isGameSelected){
			g.keyboardBuffer += c;
			g.drawText(494+2, 616+2, g.keyboardBuffer, board, 2);
		}else if(g.isXSelected){
			g.keyboardBuffer += c;
			g.drawText(494+2, 634+2, g.keyboardBuffer, board, 2);
		}else if(g.isYSelected){
			g.keyboardBuffer += c;
			g.drawText(494+2, 652+2, g.keyboardBuffer, board, 2);
		}else if(g.isAIMsDelaySelected){
			g.keyboardBuffer += c;
			g.drawText(490+2, 670+2, g.keyboardBuffer, board, 4);
		}else if(g.isNumberOfIterationsSelected){
			g.keyboardBuffer += c;
			g.drawText(506+2, 690+2, g.keyboardBuffer, board, 3);
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {}
	@Override
	public void keyTyped(KeyEvent arg0) {}
}