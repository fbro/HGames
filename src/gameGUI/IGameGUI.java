package gameGUI;

import gameLogic.Controller;
import gameLogic.IStateOBJ;

public interface IGameGUI {

	// called by the controller once at startup. 
	void startGameGui(Controller control);

	// called by Controller.java, updates the graphical user interface (GUI) every time a move has gone through
	void setGUIBoard(boolean[][] GUIGrid, IStateOBJ state, int gameOverResult);

	// called by Controller.java when an ongoing game has reached its end
	void endGame(int gameOverResult);
}