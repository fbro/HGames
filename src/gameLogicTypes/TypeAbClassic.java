package gameLogicTypes;

import gameLogic.IStateOBJ;
import gameLogic.MiscTools;
import gameLogic.StateOBJGrid;

import java.awt.Point;
import java.util.ArrayList;

// this abstract class only has one abstract method, the calcWinner, but other methods are expected to have different versions
// evaluation and narrowMoves are methods that are empty but can be specified to help the algorithm
// this abstract class holds game logic that is relevant for games such as Reversi, TicTacToe, FourInLine, ConnectFour
public abstract class TypeAbClassic implements IGameType {

	protected IStateOBJ board = null;
	protected int depth1 = 0;
	protected int depth2 = 0;
	
	protected int difficulty1 = 0;
	protected int difficulty2 = 0;

	@Override
	public IStateOBJ processMove(int x, int y, IStateOBJ state, int movingPlayer, int otherPlayer, Point selectedPoint) {
		state.getGrid()[x][y] = movingPlayer;
		return state;
	}

	@Override
	public ArrayList<Point> possibleMoves(IStateOBJ state, int movingPlayer, int otherPlayer, int depth, Point selectedPoint) {
		// This method gives the algorithm a list of moves that are available
		ArrayList<Point> moves = new ArrayList<Point>();
		for (int i = 0; i < state.getGrid().length; i++){
			for (int j = 0; j < state.getGrid()[0].length; j++){ // loop through all the childs
				if(state.getGrid()[i][j] == 0){
					moves.add(new Point(i, j)); // add all possible moves to this ArrayList and then later empty the list
				}
			}
		}
		return moves;
	}

	@Override
	public abstract int calcWinner(IStateOBJ state, int movingPlayer, int otherPlayer);

	@Override
	public IStateOBJ setupNewMatch(int boardSizeX, int boardSizeY, int difficulty1, int difficulty2) {
		this.difficulty1 = difficulty1;
		this.difficulty2 = difficulty2;
		if(difficulty1 == 1){
			depth1 = 2;
		}else if(difficulty1 == 2){
			depth1 = 4;
		}else if(difficulty1 == 3){
			depth1 = 6;
		}
		if(difficulty2 == 1){
			depth2 = 2;
		}else if(difficulty2 == 2){
			depth2 = 4;
		}else if(difficulty2 == 3){
			depth2 = 6;
		}
		IStateOBJ stateOBJ = new StateOBJGrid(new int[boardSizeX][boardSizeY]);
		return stateOBJ;
	}

	@Override
	public int setPlayerTurn(int playerTurn) {
		if(playerTurn == 2){
			return 1;
		}else{
			return 2;
		}
	}

	@Override
	public boolean[][] prepareBoardForGUI(int movingPlayer, int otherPlayer) {
		// the player is only allowed a set of moves, here we define what moves that are allowed
		ArrayList<Point> moves = possibleMoves(board, movingPlayer, otherPlayer, -1, null);
		int[][] grid = MiscTools.clone2DIntArray(board.getGrid());
		boolean[][] GUIGrid = new boolean[grid.length][grid[0].length];
		for (Point move: moves) {
			GUIGrid[move.x][move.y] = true;
		}
		return GUIGrid;
	}

	@Override
	public IStateOBJ getBoard() {
		return board;
	}

	@Override
	public void setBoard(IStateOBJ newState) {
		board = newState;
	}

	@Override
	public String getStats(){
		if(board == null){
			return "Game not yet started.";
		}else{
			int p1Count = 0, p2Count = 0, emptyCount = 0;
			for (int i = 0; i < board.getGrid().length; i++) {
				for (int j = 0; j < board.getGrid()[0].length; j++) {
					if(board.getGrid()[i][j] == 1){
						p1Count++;
					}else if(board.getGrid()[i][j] == 2){
						p2Count++;
					}else{
						emptyCount++;
					}
				}
			}
			return ""
			+ "Player1 tokens [" + p1Count + "]\n"
			+ "Player2 tokens [" + p2Count + "]\n"
			+ "Empty cells    [" + emptyCount + "]\n";
		}
	}

	@Override
	public IStateOBJ processMovePlayer(Point p, IStateOBJ state, int movingPlayer, int otherPlayer) {
		if(state.getGrid()[p.x][p.y] != 0){
			System.out.println("error - illegal spot pressed");
			return null;
		}
		return processMove(p.x, p.y, state, movingPlayer, otherPlayer, null);
	}

	@Override
	public ArrayList<IStateOBJ> processMoveAlgorithm(IStateOBJ state, int movingPlayer, int otherPlayer, int depth) {
		ArrayList<Point> moves = possibleMoves(state, movingPlayer, otherPlayer, depth, null);
		ArrayList<IStateOBJ> stateMoves = new ArrayList<IStateOBJ>();
		for (int i = 0; i < moves.size(); i++) { // loop through possible moves and convert the point to a state
			Point p = moves.get(i);
			stateMoves.add(processMove(p.x, p.y, new StateOBJGrid(MiscTools.clone2DIntArray(state.getGrid())), movingPlayer, otherPlayer, null));
		}
		return stateMoves;
	}

	@Override
	public int evaluation(IStateOBJ state, int movingPlayer, int max, int min) {
		// evaluation is not a must, it may be used to improve the algorithm
		return 0;
	}

	@Override
	public ArrayList<IStateOBJ> narrowMoves(IStateOBJ state, int movingPlayer, int otherPlayer) {
		// narrowMoves is not a must, it may be used to improve the algorithm
		return processMoveAlgorithm(state, movingPlayer, otherPlayer, -1);
	}

	@Override
	public int getDepth(int movingPlayer) {
		if(movingPlayer == 1){
			return depth1;
		}else if(movingPlayer == 2){
			return depth2;
		}
		return 0;
	}

	@Override
	public int getDifficulty(int movingPlayer){
		if(movingPlayer == 1){
			return difficulty1;
		}else if(movingPlayer == 2){
			return difficulty2;
		}
		return 0;
	}
}