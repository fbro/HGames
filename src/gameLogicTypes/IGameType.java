package gameLogicTypes;

import java.awt.Point;
import java.util.ArrayList;

import gameLogic.IStateOBJ;

public interface IGameType {
	// there are three types of methods - methods used only by the computer/algorithms, by the human player, by both. 

	
	// methods used by both -----------------------------------------------------------------

	// change the grid according to the move, some moves do more than just placing a piece on a board
	// it changes more pieces on the board
	public IStateOBJ processMove(int x, int y, IStateOBJ state, int movingPlayer, int otherPlayer, Point selectedPoint);
	// This method gives a list of moves that are available in points
	public ArrayList<Point> possibleMoves(IStateOBJ state, int movingPlayer, int otherPlayer, int depth, Point selectedPoint);
	// a method that calculates the winner by looking at the current board
	// return -1 = tie, 0 = not finished, 1 = player one won, 2 = player two won.
	// in the grid, if 0 then unused, if 1 then player one, if 2 then player 2.
	public int calcWinner(IStateOBJ state, int movingPlayer, int otherPlayer);
	// setup at the start of a new match
	// Reversi has 4 pieces in the middle, chess has all the pieces from the start on, checkers has diagonal pieces...
	// input is 1, 2, 3. Easy, Medium, Hard for the parameter difficulty
	// method that decides what should be changed when the difficulty is set to something
	// output is the depth of the minimax algorithm. some games can manage a larger depth and some cannot. 
	public IStateOBJ setupNewMatch(int boardSizeX, int boardSizeY, int difficulty1, int difficulty2);
	// handle whose turn it is
	public int setPlayerTurn(int playerTurn);
	// prints a grid that helps the GUI determine what moves are available
	public boolean[][] prepareBoardForGUI(int movingPlayer, int otherPlayer);
	// get the grid
	public IStateOBJ getBoard();
	// set the grid
	public void setBoard(IStateOBJ state);
	// get stats on the current ongoing game like how many pieces have been placed
	public String getStats();
	// unique name of the game
	public String getGameName();
	// unique ID of the game
	public int getGameID();
	
	// methods used only by human players ---------------------------------------------------
	
	// this method is called every time some human player sets a point - for example sometimes the point is saved as a select
	// the human version of possibleGrids
	public IStateOBJ processMovePlayer(Point p, IStateOBJ state, int movingPlayer, int otherPlayer);
	
	
	// methods used only by computer/algorithms ---------------------------------------------
	
	// This method gives a list of moves that are available in grids
	// grids are needed to simplify the decisions on the algorithm
	public ArrayList<IStateOBJ> processMoveAlgorithm(IStateOBJ state, int movingPlayer, int otherPlayer, int depth);
	// heuristic evaluation function
	// manipulates the util value to help guide the minimax - or other algorithms in the right direction
	// movingPlayer is the player that has the next to move
	// max and min is originally 2 and 1
	public int evaluation(IStateOBJ state, int movingPlayer, int max, int min);
	// used at the beginning of the algorithm to helps with skipping calculating moves that are not needed
	// clear cut moves are forced through in the method to support the algorithm
	public ArrayList<IStateOBJ> narrowMoves(IStateOBJ state, int movingPlayer, int otherPlayer);
	// the algorithm needs to know how far it can go
	public int getDepth(int movingPlayer);
	// the algorithm may need to know the difficulty that has been set
	public int getDifficulty(int movingPlayer);
}