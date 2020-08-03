package gameGUI;

import gameLogic.Controller;
import gameLogic.IStateOBJ;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GUISwing implements IGameGUI{

	private JFrame frame;
	private JPanel panel_Center = new JPanel();
	private JPanel panel_rightSide = new JPanel();
	private JPanel panel_leftSide = new JPanel();
	private Controller controller;
	private Image blueIcons[];
	private Image redIcons[];
	private Image empty;
	private Image closed;
	private JButton[] buttons;
	private final JLabel lblTitle = new JLabel("Welcome to the Tic Tac Toe game");
	private final JLabel lblPlayerTurn = new JLabel("Player turn");
	private final JButton btnResetGame = new JButton("Reset Game");
	private final JPanel panel_settings11 = new JPanel();
	private final ButtonGroup buttonGroup_player1 = new ButtonGroup();
	private final ButtonGroup buttonGroup_player2 = new ButtonGroup();
	private final int frameWidth = 330;
	private final int frameHeight = 250;
	private Thread thread = null; // create new thread when entering the controller

	@Override
	public void startGameGui(final Controller control) {
		final GUISwing g = this;
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUISwing window = g;
					window.controller = control;
					window.initialize();
					window.frame.setVisible(true);
				} catch (Exception e) {e.printStackTrace();}
			}
		});
	}

	@Override
	public void setGUIBoard(boolean[][] GUIgrid, IStateOBJ state, int gameOverResult){
		// the move has to update the GUI
		if(state.getGrid() != null){
			for (int i = 0; i < state.getGrid()[0].length; i++) {
				for (int j = 0; j < state.getGrid().length; j++) {
					int selectedBTN = (j)+(i*state.getGrid().length);
					if(state.getGrid()[j][i] == -1){ // this button cannot be clicked by the player
						buttons[selectedBTN].setIcon(new ImageIcon(closed));
						buttons[selectedBTN].setToolTipText(selectedBTN + " Closed");
					}else if(state.getGrid()[j][i] == 0){
						buttons[selectedBTN].setIcon(new ImageIcon(empty));
						buttons[selectedBTN].setToolTipText(selectedBTN + " Empty");
					}else if(state.getGrid()[j][i] == 1){
						buttons[selectedBTN].setIcon(new ImageIcon(blueIcons[0]));
						buttons[selectedBTN].setToolTipText("Blue");
					}else if(state.getGrid()[j][i] == 2){
						buttons[selectedBTN].setIcon(new ImageIcon(redIcons[0]));
						buttons[selectedBTN].setToolTipText("Red");
					}
					// from 3 and upwards, and from -3 and downward on the grid we use unique icons
					else if(state.getGrid()[j][i] >= 3){
						buttons[selectedBTN].setIcon(new ImageIcon(blueIcons[state.getGrid()[j][i]-3]));
						buttons[selectedBTN].setToolTipText("Blue");
					}
					else if(state.getGrid()[j][i] <= -3){
						buttons[selectedBTN].setIcon(new ImageIcon(redIcons[(state.getGrid()[j][i]*-1)-3]));
						buttons[selectedBTN].setToolTipText("Red");
					}
				}
			}
		}
		// does not set playerTurn, but updates the GUI to show whose turn it is
		// update GUI info for player turn
		if(controller.getPlayerTurn()==1){
			lblPlayerTurn.setText(controller.getPlayerName(1) + "'s turn.");
			lblPlayerTurn.setForeground(Color.BLUE);
			panel_rightSide.setBackground(Color.BLUE);
			panel_leftSide.setBackground(Color.BLUE);
			panel_Center.setBackground(Color.BLUE);
			if(controller.getPlayerType(1) == 2 && state.getGrid() != null){
				panel_settings11.setVisible(true); // loading icon
			}else{
				panel_settings11.setVisible(false); // loading icon
			}
		}
		else if(controller.getPlayerTurn()==2){
			lblPlayerTurn.setText(controller.getPlayerName(2) + "'s turn.");
			lblPlayerTurn.setForeground(Color.RED);
			panel_rightSide.setBackground(Color.RED);
			panel_leftSide.setBackground(Color.RED);
			panel_Center.setBackground(Color.RED);
			if(controller.getPlayerType(2) == 2 && state.getGrid() != null){
				panel_settings11.setVisible(true); // loading icon
			}else{
				panel_settings11.setVisible(false); // loading icon
			}
		}

		if(controller.getNumOfDraws() <= 0){ // display how many games that have been played
			lblTitle.setText("Round: " + controller.getRound() + " Game: " + controller.getNumOfPlayedGames());
		}else{
			lblTitle.setText("Round: " + controller.getRound() + " Game: " + controller.getNumOfPlayedGames() + " Draws: " + controller.getNumOfDraws());
		}
		// enable non closed buttons
		for (int i = 0; i < buttons.length; i++) {
			if(!buttons[i].getToolTipText().endsWith("Closed")){
				buttons[i].setEnabled(true);
			}else{
				buttons[i].setEnabled(false);
			}
		}
	}

	@Override
	public void endGame(int player) {
		panel_settings11.setVisible(false); // loading icon
		if(player == 1){
			lblTitle.setText("Game over, " + controller.getPlayerName(1) + " won.");
			lblPlayerTurn.setText("Game over, " + controller.getPlayerName(1) + " won.");
			lblPlayerTurn.setForeground(Color.BLUE);
			panel_rightSide.setBackground(Color.BLUE);
			panel_leftSide.setBackground(Color.BLUE);
			panel_Center.setBackground(Color.BLUE);
		}else if(player == 2){
			lblTitle.setText("Game over, " + controller.getPlayerName(2) + " won.");
			lblPlayerTurn.setText("Game over, " + controller.getPlayerName(2) + " won.");
			lblPlayerTurn.setForeground(Color.RED);
			panel_rightSide.setBackground(Color.RED);
			panel_leftSide.setBackground(Color.RED);
			panel_Center.setBackground(Color.RED);
		}else{
			lblTitle.setText("Game over, it is a draw.");
			lblPlayerTurn.setText("Game over, it is a draw.");
			lblPlayerTurn.setForeground(Color.GRAY);
			panel_rightSide.setBackground(Color.GRAY);
			panel_leftSide.setBackground(Color.GRAY);
			panel_Center.setBackground(Color.GRAY);
		}
		btnResetGame.setText("New Game");
		for (int i = 0; i < buttons.length; i++) {
			if(buttons[i].getToolTipText().endsWith("Empty") || buttons[i].getToolTipText().endsWith("Closed")){
				buttons[i].setEnabled(false);
			}
		}
	}

	
	private void initialize() {
		// setup the frame
		frame = new JFrame();
		frame.setBounds(0, 0, frameWidth, frameHeight);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		frame.setTitle("Tic Tac Toe");
		frame.setLocationRelativeTo(null); // Centre the frame on the computer screen
		// create all the panels
		final JPanel panel_title      = new JPanel();
		final JPanel panel_settings   = new JPanel();
		final JPanel panel_settings1  = new JPanel();
		final JPanel panel_settings12 = new JPanel();
		final JPanel panel_settings2  = new JPanel();
		final JPanel panel_buttons    = new JPanel();
		final JPanel panel_difficulty = new JPanel();
		final JPanel panel_player1    = new JPanel();
		final JPanel panel_player2    = new JPanel();
		// add the panels to the frame
		frame.getContentPane().add(panel_title,     BorderLayout.NORTH);
		frame.getContentPane().add(panel_rightSide, BorderLayout.EAST);
		frame.getContentPane().add(panel_leftSide,  BorderLayout.WEST);
		frame.getContentPane().add(panel_Center,    BorderLayout.CENTER);
		frame.getContentPane().add(panel_settings,  BorderLayout.SOUTH);
		// setLayout on all the panels
		panel_title.setLayout     (new FlowLayout(FlowLayout.CENTER));
		panel_rightSide.setLayout (new FlowLayout(FlowLayout.CENTER));
		panel_leftSide.setLayout  (new FlowLayout(FlowLayout.CENTER));
		panel_settings.setLayout  (new GridLayout(2, 1));
		panel_settings1.setLayout (new FlowLayout(FlowLayout.CENTER));
		panel_settings11.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel_settings12.setLayout(new GridLayout(2, 1));
		panel_settings2.setLayout (new GridLayout(2, 1));
		panel_buttons.setLayout   (new FlowLayout(FlowLayout.CENTER));
		panel_difficulty.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panel_player1.setLayout   (new FlowLayout(FlowLayout.LEFT));
		panel_player2.setLayout   (new FlowLayout(FlowLayout.LEFT));
		// create buttons, radioButtons, labels and textfields
		final JSpinner spinner_boardSizeX  = new JSpinner();
		final JSpinner spinner_boardSizeY  = new JSpinner();
		final JSpinner spinner_gameMode    = new JSpinner();
		final JSpinner spinner_p1Algo      = new JSpinner();
		final JSpinner spinner_p2Algo      = new JSpinner();
		final JButton btnStartGame         = new JButton("Start Game");
		final JButton button_question      = new JButton();
		final JRadioButton rdbtnEasy       = new JRadioButton("Easy");
		final JRadioButton rdbtnMedium     = new JRadioButton("Medium");
		final JRadioButton rdbtnHard       = new JRadioButton("Hard");
		final JRadioButton rdbtnHuman_1    = new JRadioButton("Human");
		final JRadioButton rdbtnComputer_1 = new JRadioButton("Computer");
		final JRadioButton rdbtnHuman_2    = new JRadioButton("Human");
		final JRadioButton rdbtnComputer_2 = new JRadioButton("Computer");
		final JButton button_loading       = new JButton();
		final JTextField txtPlayerName1    = new JTextField();
		final JTextField txtPlayerName2    = new JTextField();
		final JLabel lblPlayer1Status      = new JLabel("Player 1");
		final JLabel lblPlayer2Status      = new JLabel("Player 2");
		final ButtonGroup buttonGroup_diff = new ButtonGroup();
		// add stuff to the panels
		panel_title.add(lblTitle);
		panel_settings.add(panel_settings1);
		panel_settings.add(panel_settings2);
		panel_settings11.add(button_loading);
		panel_settings1.add(panel_settings11);
		panel_settings1.add(panel_settings12);
		panel_settings12.add(panel_buttons);
		panel_settings12.add(panel_difficulty);
		panel_settings2.add(panel_player1);
		panel_settings2.add(panel_player2);
		panel_buttons.add(btnStartGame);
		panel_buttons.add(btnResetGame);
		panel_buttons.add(spinner_gameMode);
		panel_difficulty.add(rdbtnEasy);
		panel_difficulty.add(rdbtnMedium);
		panel_difficulty.add(rdbtnHard);
		panel_difficulty.add(spinner_boardSizeX);
		panel_difficulty.add(spinner_boardSizeY);
		panel_difficulty.add(button_question);
		panel_difficulty.add(lblPlayerTurn);
		panel_player1.add(rdbtnHuman_1);
		panel_player1.add(rdbtnComputer_1);
		panel_player1.add(txtPlayerName1);
		panel_player1.add(spinner_p1Algo);
		panel_player1.add(lblPlayer1Status);
		panel_player2.add(rdbtnHuman_2);
		panel_player2.add(rdbtnComputer_2);
		panel_player2.add(txtPlayerName2);
		panel_player2.add(spinner_p2Algo);
		panel_player2.add(lblPlayer2Status);
		// add stuff
		buttonGroup_diff.add(rdbtnEasy);
		buttonGroup_diff.add(rdbtnMedium);
		buttonGroup_diff.add(rdbtnHard);
		buttonGroup_player1.add(rdbtnHuman_1);
		buttonGroup_player1.add(rdbtnComputer_1);
		buttonGroup_player2.add(rdbtnHuman_2);
		buttonGroup_player2.add(rdbtnComputer_2);
		// set stuff
		rdbtnMedium.setSelected(true);
		spinner_boardSizeX.setValue(3);
		spinner_boardSizeY.setValue(3);
		spinner_gameMode.setValue(1);
		spinner_p1Algo.setValue(1);
		spinner_p1Algo.setVisible(false);
		spinner_p2Algo.setValue(1);
		spinner_p2Algo.setVisible(false);
		txtPlayerName1.setText("Player1");
		txtPlayerName1.setColumns(10);
		txtPlayerName1.setVisible(false);
		txtPlayerName2.setText("Player2");
		txtPlayerName2.setColumns(10);
		txtPlayerName2.setVisible(false);
		btnResetGame.setVisible(false);
		panel_Center.setVisible(false);
		lblPlayerTurn.setVisible(false);
		lblPlayer1Status.setVisible(false);
		lblPlayer2Status.setVisible(false);
		rdbtnHuman_1.setToolTipText("Set player 1 to human");
		rdbtnHuman_2.setToolTipText("Set player 2 to human");
		rdbtnComputer_1.setToolTipText("Set player 1 to computer");
		rdbtnComputer_2.setToolTipText("Set player 2 to computer");    
		rdbtnHuman_1.setForeground(Color.BLUE);
		rdbtnHuman_2.setForeground(Color.RED);
		rdbtnComputer_1.setForeground(Color.BLUE);
		rdbtnComputer_2.setForeground(Color.RED);
		txtPlayerName1.setForeground(Color.BLUE);
		txtPlayerName2.setForeground(Color.RED);
		lblPlayer1Status.setForeground(Color.BLUE);
		lblPlayer2Status.setForeground(Color.RED);
		button_question.setToolTipText("Click here for help");
		button_question.setText("?");

		panel_settings11.setVisible(false); // loading icon
		button_loading.setMargin(new Insets(0, 0, 0, 0)); // remote the spacing between the image and button's borders
		button_loading.setBorder(null); // to remove the border
		button_loading.setIcon(new ImageIcon("src/pictures/loadingMicro.gif"));
		button_loading.setToolTipText("Computer is calculating the next move...");

		txtPlayerName1.setToolTipText("Set player 1 name");
		txtPlayerName2.setToolTipText("Set player 2 name");
		btnStartGame.setToolTipText("Start new game");
		btnResetGame.setToolTipText("Reset game");
		spinner_gameMode.setToolTipText("Game mode");
		spinner_boardSizeX.setToolTipText("Size of x-axis");
		spinner_boardSizeY.setToolTipText("Size of y-axis");
		spinner_p1Algo.setToolTipText("Set the algorithm for computer 1");
		spinner_p2Algo.setToolTipText("Set the algorithm for computer 2");
		rdbtnEasy.setToolTipText("Easy computer opponent");
		rdbtnMedium.setToolTipText("Medium computer opponent");
		rdbtnHard.setToolTipText("Hard computer opponent");

		// listeners
		btnResetGame.addActionListener(new ActionListener() { // listener for resetGame button
			public void actionPerformed(ActionEvent arg0) {
				thread.interrupt(); // if a move is being calculated then ask it to stop because game is being reset
				if(isonlyHumanPlayers()){rdbtnEasy.setVisible(false);rdbtnMedium.setVisible(false);rdbtnHard.setVisible(false);}
				else{rdbtnEasy.setVisible(true);rdbtnMedium.setVisible(true);rdbtnHard.setVisible(true);}
				btnStartGame.setVisible(true);
				spinner_gameMode.setVisible(true);
				btnResetGame.setVisible(false);
				panel_settings11.setVisible(false); // loading icon
				btnResetGame.setText("Reset Game");
				lblTitle.setVisible(true);
				lblTitle.setText("Welcome to the Tic Tac Toe game");
				if(!rdbtnComputer_1.isSelected()){ // player 1 is human
					txtPlayerName1.setVisible(true);
					lblPlayer1Status.setVisible(false);
				}else{ // player 1 is computer
					spinner_p1Algo.setVisible(true);
					lblPlayer1Status.setVisible(true);
					lblPlayer1Status.setText(controller.getAlgo1().getAlgorithmName());
				}if(!rdbtnComputer_2.isSelected()){ // player 2 is human
					txtPlayerName2.setVisible(true);
					lblPlayer2Status.setVisible(false);
				}else{ // player 2 is computer
					spinner_p2Algo.setVisible(true);
					lblPlayer2Status.setVisible(true);
					lblPlayer2Status.setText(controller.getAlgo2().getAlgorithmName());
				}
				rdbtnHuman_1.setVisible(true);
				rdbtnHuman_2.setVisible(true);
				rdbtnComputer_1.setVisible(true);
				rdbtnComputer_2.setVisible(true);
				spinner_boardSizeX.setVisible(true);
				spinner_boardSizeY.setVisible(true);
				panel_Center.setVisible(false);
				panel_rightSide.setVisible(false);
				panel_leftSide.setVisible(false);
				lblPlayerTurn.setVisible(false);
				button_question.setVisible(true);
				for (int i = 0; i < buttons.length; i++) {
					buttons[i].setIcon(new ImageIcon(empty));
					buttons[i].setEnabled(true);
					panel_Center.remove(buttons[i]);
					buttons[i].remove(buttons[i]);
				}
				int difHeight = frame.getHeight() - frameHeight;
				int difWidth = frame.getWidth() - frameWidth;
				frame.setBounds(frame.getX() + (difWidth/2), frame.getY() + (difHeight/2), frameWidth, frameHeight); // resize frame when buttons are removed
			}
		});

		btnStartGame.addActionListener(new ActionListener() { // listener for startGame button
			public void actionPerformed(ActionEvent arg0) {
				if(controller.startGame(getSelectedDifficulty(buttonGroup_diff), getSelectedDifficulty(buttonGroup_diff),
						(Integer) spinner_boardSizeX.getValue(), 
						(Integer) spinner_boardSizeY.getValue(), 
						txtPlayerName1.getText(), 
						txtPlayerName2.getText(), 
						getSelectedPlayerType(buttonGroup_player1),
						getSelectedPlayerType(buttonGroup_player2),
						(Integer) spinner_gameMode.getValue(),
						(Integer) spinner_p1Algo.getValue(),
						(Integer) spinner_p2Algo.getValue())){ // call the startGame method that creates the gameLogic
					try {
						blueIcons = new Image[controller.getBlueIcons().length];
						for (int i = 0; i < controller.getBlueIcons().length; i++) {
							blueIcons[i] = ImageIO.read(new File(controller.getBlueIcons()[i])); // blue
						}
						redIcons = new Image[controller.getRedIcons().length];
						for (int i = 0; i < controller.getRedIcons().length; i++) {
							redIcons[i]  = ImageIO.read(new File(controller.getRedIcons()[i])); // red
						}
						empty    = ImageIO.read(new File("src/pictures/empty.png")); // empty
						closed   = ImageIO.read(new File("src/pictures/closed.png")); // closed
					}catch (IOException e){e.printStackTrace();}
					lblPlayer1Status.setText(controller.getPlayerName(1) + ", " + controller.getPlayerNumWins(1) + " wins " + controller.getPlayerNumWins(2) + " lost");
					lblPlayer2Status.setText(controller.getPlayerName(2) + ", " + controller.getPlayerNumWins(2) + " wins " + controller.getPlayerNumWins(1) + " lost");
					btnStartGame.setVisible(false);
					btnResetGame.setVisible(true);
					txtPlayerName1.setVisible(false);
					txtPlayerName2.setVisible(false);
					rdbtnHuman_1.setVisible(false);
					rdbtnHuman_2.setVisible(false);
					rdbtnComputer_1.setVisible(false);
					rdbtnComputer_2.setVisible(false);
					rdbtnEasy.setVisible(false);
					rdbtnMedium.setVisible(false);
					rdbtnHard.setVisible(false);
					spinner_gameMode.setVisible(false);
					spinner_boardSizeX.setVisible(false);
					spinner_boardSizeY.setVisible(false);
					spinner_p1Algo.setVisible(false);
					spinner_p2Algo.setVisible(false);
					button_question.setVisible(false);
					panel_Center.setVisible(true);
					panel_rightSide.setVisible(true);
					panel_leftSide.setVisible(true);
					if(controller.getNumOfDraws() <= 0){ // display how many games that have been played
						lblTitle.setText("Round: " + controller.getRound() + " Game: " + controller.getNumOfPlayedGames());
					}else{
						lblTitle.setText("Round: " + controller.getRound() + " Game: " + controller.getNumOfPlayedGames() + " Draws: " + controller.getNumOfDraws());
					}
					lblPlayerTurn.setVisible(true);
					lblPlayer1Status.setVisible(true);
					lblPlayer2Status.setVisible(true);

					// loop that creates x number of buttons - tic tac toe = 9
					// panel_Center layout can first be set when the user has set the spinner_boardSize
					panel_Center.setLayout(new GridLayout((Integer) spinner_boardSizeY.getValue(), (Integer) spinner_boardSizeX.getValue()));
					int windowSizeX = ((Integer) spinner_boardSizeX.getValue()*50)+80;
					int windowSizeY = ((Integer) spinner_boardSizeY.getValue()*50)+250;
					int difHeight = frame.getHeight() - windowSizeY;
					int difWidth  = frame.getWidth() - windowSizeX;
					frame.setBounds(frame.getX() + (difWidth/2), frame.getY() + (difHeight/2), windowSizeX, windowSizeY); // resize frame when buttons are added
					buttons = new JButton[(Integer) spinner_boardSizeX.getValue() * (Integer) spinner_boardSizeY.getValue()];
					for (int i = 0; i < buttons.length; i++) {
						final int btn = i;
						buttons[i] = new JButton("");
						panel_Center.add(buttons[i]);
						buttons[i].setToolTipText(i + " Empty");
						buttons[i].setIcon(new ImageIcon(empty));
						buttons[i].addActionListener(new ActionListener() {
							int btnID = btn;
							public void actionPerformed(ActionEvent arg0) {
								if(!controller.getDoNotDisturb()){ // semaphor
									thread = new Thread(){public void run(){controller.move(btnID);}};
									thread.start(); // when the button is pressed then start recording the move in the controller
								}
							}
						});
					}
					thread = new Thread(){public void run(){controller.move(-1);}};
					thread.start(); // start managing turns. if computer starts this line is neccesary

				}
			}
		});

		button_question.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(frame, "When starting a new game first set unique player names \nthen select difficulty when fighting against a computer.");
			}
		});

		rdbtnEasy.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(rdbtnComputer_1.isSelected()){
					txtPlayerName1.setText(controller.getName(getSelectedDifficulty(buttonGroup_diff), true, 1));
				}
				if(rdbtnComputer_2.isSelected()){
					txtPlayerName2.setText(controller.getName(getSelectedDifficulty(buttonGroup_diff), true, 2));
				}
			}
		});

		rdbtnMedium.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(rdbtnComputer_1.isSelected()){
					txtPlayerName1.setText(controller.getName(getSelectedDifficulty(buttonGroup_diff), true, 1));
				}
				if(rdbtnComputer_2.isSelected()){
					txtPlayerName2.setText(controller.getName(getSelectedDifficulty(buttonGroup_diff), true, 2));
				}
			}
		});

		rdbtnHard.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(rdbtnComputer_1.isSelected()){
					txtPlayerName1.setText(controller.getName(getSelectedDifficulty(buttonGroup_diff), true, 1));
				}
				if(rdbtnComputer_2.isSelected()){
					txtPlayerName2.setText(controller.getName(getSelectedDifficulty(buttonGroup_diff), true, 2));
				}
			}
		});

		rdbtnHuman_1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(isonlyHumanPlayers()){rdbtnEasy.setVisible(false);rdbtnMedium.setVisible(false);rdbtnHard.setVisible(false);}
				else{rdbtnEasy.setVisible(true);rdbtnMedium.setVisible(true);rdbtnHard.setVisible(true);}
				if(rdbtnHuman_1.isSelected()){
					if(txtPlayerName1.getText().contains(controller.getName(0, true, 1))){//"Computer1")){
						txtPlayerName1.setText(controller.getName(getSelectedDifficulty(buttonGroup_diff), false, 1));//"Player1");
					}
					txtPlayerName1.setVisible(true);
					spinner_p1Algo.setValue(1);
					spinner_p1Algo.setVisible(false);
					lblPlayer1Status.setVisible(false);
				}else if(!rdbtnHuman_1.isSelected()){
					txtPlayerName1.setText(controller.getName(getSelectedDifficulty(buttonGroup_diff), true, 1));//"Computer1");
					txtPlayerName1.setVisible(false);
					spinner_p1Algo.setVisible(true);
					lblPlayer1Status.setText(controller.getAlgo1().getAlgorithmName());
					lblPlayer1Status.setVisible(true);
				}
				frame.revalidate();
			}
		});

		rdbtnHuman_2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(isonlyHumanPlayers()){rdbtnEasy.setVisible(false);rdbtnMedium.setVisible(false);rdbtnHard.setVisible(false);}
				else{rdbtnEasy.setVisible(true);rdbtnMedium.setVisible(true);rdbtnHard.setVisible(true);}
				if(rdbtnHuman_2.isSelected()){
					if(txtPlayerName2.getText().contains(controller.getName(0, true, 2))){//"Computer2")){
						txtPlayerName2.setText(controller.getName(getSelectedDifficulty(buttonGroup_diff), false, 2));//"Player2");
					}
					txtPlayerName2.setVisible(true);
					spinner_p2Algo.setValue(1);
					spinner_p2Algo.setVisible(false);
					lblPlayer2Status.setVisible(false);
				}else if(!rdbtnHuman_2.isSelected()){
					txtPlayerName2.setText(controller.getName(getSelectedDifficulty(buttonGroup_diff), true, 2));//"Computer2");
					txtPlayerName2.setVisible(false);
					spinner_p2Algo.setVisible(true);
					lblPlayer2Status.setText(controller.getAlgo2().getAlgorithmName());
					lblPlayer2Status.setVisible(true);
				}
				frame.revalidate();
			}
		});

		rdbtnComputer_1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(isonlyHumanPlayers()){rdbtnEasy.setVisible(false);rdbtnMedium.setVisible(false);rdbtnHard.setVisible(false);}
				else{rdbtnEasy.setVisible(true);rdbtnMedium.setVisible(true);rdbtnHard.setVisible(true);}
				if(rdbtnComputer_1.isSelected()){
					txtPlayerName1.setText(controller.getName(getSelectedDifficulty(buttonGroup_diff), true, 1));//"Computer1");
					txtPlayerName1.setVisible(false);
					spinner_p1Algo.setVisible(true);
					lblPlayer1Status.setText(controller.getAlgo1().getAlgorithmName());
					lblPlayer1Status.setVisible(true);
				}else if(!rdbtnComputer_1.isSelected()){
					if(txtPlayerName1.getText().contains(controller.getName(0, true, 1))){//"Computer1")){
						txtPlayerName1.setText(controller.getName(getSelectedDifficulty(buttonGroup_diff), false, 1));//"Player1");
					}
					txtPlayerName1.setVisible(true);
					spinner_p1Algo.setValue(1);
					spinner_p1Algo.setVisible(false);
					lblPlayer1Status.setVisible(false);
				}
				frame.revalidate();
			}
		});

		rdbtnComputer_2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(isonlyHumanPlayers()){rdbtnEasy.setVisible(false);rdbtnMedium.setVisible(false);rdbtnHard.setVisible(false);}
				else{rdbtnEasy.setVisible(true);rdbtnMedium.setVisible(true);rdbtnHard.setVisible(true);}
				if(rdbtnComputer_2.isSelected()){
					txtPlayerName2.setText(controller.getName(getSelectedDifficulty(buttonGroup_diff), true, 2));//"Computer2");
					txtPlayerName2.setVisible(false);
					spinner_p2Algo.setVisible(true);
					lblPlayer2Status.setText(controller.getAlgo2().getAlgorithmName());
					lblPlayer2Status.setVisible(true);
				}else if(!rdbtnComputer_2.isSelected()){
					if(txtPlayerName2.getText().contains(controller.getName(0, true, 2))){//"Computer2")){
						txtPlayerName2.setText(controller.getName(getSelectedDifficulty(buttonGroup_diff), false, 2));//"Player2");
					}
					txtPlayerName2.setVisible(true);
					spinner_p2Algo.setValue(1);
					spinner_p2Algo.setVisible(false);
					lblPlayer2Status.setVisible(false);
				}
				frame.revalidate();
			}
		});

		spinner_gameMode.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int val = (int) spinner_gameMode.getValue();
				if(!(val >= 1 && val <= controller.getNumberOfGames())){
					if(val < 1){
						val = controller.getNumberOfGames();
					}else if(val > controller.getNumberOfGames()){
						val = 1;
					}
				}
				controller.setGameMode(val); // updates the controller to match the selected gamemode, the GUI will then fetch the new gamemode information
				spinner_gameMode.setValue(val);
				spinner_boardSizeX.setValue(controller.getSuggestedGridX());
				spinner_boardSizeY.setValue(controller.getSuggestedGridY());
				frame.setTitle(controller.getGameModeName());
				lblTitle.setText("Welcome to the " + controller.getGameModeName() + " game");
			}
		});

		spinner_boardSizeX.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int val = (int) spinner_boardSizeX.getValue();
				if(!(val >= controller.getMinGridX() && val <= controller.getMaxGridX())){
					if(val < controller.getMinGridX()){
						val = controller.getMaxGridX();
					}else if(val > controller.getMaxGridX()){
						val = controller.getMinGridX();
					}
				}
				spinner_boardSizeX.setValue(val);
			}
		});

		spinner_boardSizeY.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int val = (int) spinner_boardSizeY.getValue();
				if(!(val >= controller.getMinGridY() && val <= controller.getMaxGridY())){
					if(val < controller.getMinGridY()){
						val = controller.getMaxGridY();
					}else if(val > controller.getMaxGridY()){
						val = controller.getMinGridY();
					}
				}
				spinner_boardSizeY.setValue(val);
			}
		});

		spinner_p1Algo.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int val = (int) spinner_p1Algo.getValue();
				if(val > controller.getNumberOfAlgorithms() || val <= 0){
					if(val <= 0){
						val = controller.getNumberOfAlgorithms();
					}else if(val > controller.getNumberOfAlgorithms()){
						val = 1;
					}
				}
				controller.setAlgorithm(val, -1); // only update the selected algorithm
				spinner_p1Algo.setValue(val);
				lblPlayer1Status.setText(controller.getAlgo1().getAlgorithmName());
			}
		});

		spinner_p2Algo.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int val = (int) spinner_p2Algo.getValue();
				if(val > controller.getNumberOfAlgorithms() || val <= 0){
					if(val <= 0){
						val = controller.getNumberOfAlgorithms();
					}else if(val > controller.getNumberOfAlgorithms()){
						val = 1;
					}
				}
				controller.setAlgorithm(-1, val); // only update the selected algorithm
				spinner_p2Algo.setValue(val);
				lblPlayer2Status.setText(controller.getAlgo2().getAlgorithmName());
			}
		});
	}

	private int getSelectedDifficulty(ButtonGroup buttonGroup) {
		for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
			AbstractButton button = buttons.nextElement();
			if (button.isSelected()) {
				button.getText();
				String difficultyText = button.getText();
				int difficulty;
				switch (difficultyText) {
				case "Easy"  :  difficulty = 1;break;
				case "Medium":  difficulty = 2;break;
				case "Hard"  :  difficulty = 3;break;
				default: difficulty = 0;break;
				}
				return difficulty;
			}
		}
		return 0;
	}

	private int getSelectedPlayerType(ButtonGroup buttonGroup) {
		for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
			AbstractButton button = buttons.nextElement();
			if (button.isSelected()) {
				button.getText();
				String difficultyText = button.getText();
				int playerType;
				switch (difficultyText) {
				case "Human"  :  playerType = 1;break;
				case "Computer":  playerType = 2;break;
				default: playerType = 0;break;
				}
				return playerType;
			}
		}
		return 0;
	}

	private boolean isonlyHumanPlayers(){
		if(getSelectedPlayerType(buttonGroup_player1) == 1 && getSelectedPlayerType(buttonGroup_player2) == 1){
			return true;
		}else{
			return false;
		}
	}

}
