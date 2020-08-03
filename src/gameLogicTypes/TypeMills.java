package gameLogicTypes;

import gameLogic.IStateOBJ;
import gameLogic.MiscTools;
import gameLogic.StateOBJGrid;

import java.awt.Point;
import java.util.ArrayList;

public class TypeMills implements IGameType {

	private IStateOBJ board = null;
	private Point selPoint = null;
	private int depth1 = 0;
	private int depth2 = 0;
	private int difficulty1 = 0;
	private int difficulty2 = 0;
	private int numOfRounds = 0; // used in calcwinner to help end the game when stuck.
	// TODO bug where when the game ends and the extra tiles are removed they are removed the wrong way
	/*
	 *	The game goes through three phases
	 *	        -  opening phase
	 *	           Players alternately place stones on an empty point.
	 *	        -  midgame phase
	 *	           After all stones are placed, players slide stones to
	 *	           any adjacent vacant point.
	 *	        -  endgame phase
	 *	           When a player has only three stones left, she may
	 *	           jump a stone to any vacant point.
	 *	
	 *	When closing a mill (three-in-a-row), any opponent's stone
	 *	which is not part of a mill may be removed. If all the
	 *	opponent's stones are part of mills, any stone may be removed.
	 *	
	 *	Closing two mills simultaneously (opening phase) only allows
	 *	one of the opponent's stones to be removed.
	 *	
	 *	        -  The first player who has less than three stones loses.
	 *	        -  The first player who cannot make a legal move loses.
	 */

	@Override
	public IStateOBJ processMove(int x, int y, IStateOBJ state, int movingPlayer, int otherPlayer, Point selectedPoint) {
		// elimination move
		if(state.getGrid()[1][5] == 0 || state.getGrid()[13][1] == 0){
			if(movingPlayer == 1){
				if(state.getGrid()[x][y] == 2){
					state.getGrid()[x][y] = 0;
				}else{return null;}
				state.getGrid()[1][5] = -1;
			}else if(movingPlayer == 2){
				if(state.getGrid()[x][y] == 1){
					state.getGrid()[x][y] = 0;
				}else{return null;}
				state.getGrid()[13][1] = -1;
			}
			return state;
		}

		// normal move
		boolean isFound = false;
		if(state.getGrid()[14][6] == 2){ // if this cell has the value 2 then we know that we are still in phase 1
			if(movingPlayer==1){
				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < state.getGrid()[0].length-4; j++) {
						if(state.getGrid()[i][j] == 1){
							state.getGrid()[i][j] = -1;
							isFound = true;
							break;
						}
					}
					if(isFound){break;}
				}
			}else if(movingPlayer==2){
				for (int i = 12; i < state.getGrid().length; i++) {
					for (int j = 4; j < state.getGrid()[0].length; j++) {
						if(state.getGrid()[i][j] == 2){
							state.getGrid()[i][j] = -1;
							isFound = true;
							break;
						}
					}
					if(isFound){break;}
				}
			}
			if(isFound){ // phase 1 move
				state.getGrid()[x][y] = movingPlayer;
			}
		}
		else{ // no pieces outside of the board - phase 2
			state.getGrid()[selectedPoint.x][selectedPoint.y] = 0;
			state.getGrid()[x][y] = movingPlayer;
		}
		boolean isThreeInLine = detectThreeInLine(x, y, state);
		if(isThreeInLine){
			if(movingPlayer == 1){
				state.getGrid()[1][5] = 0;
			}else{
				state.getGrid()[13][1] = 0;
			}
		}

		return state;
	}

	@Override
	public ArrayList<Point> possibleMoves(IStateOBJ state, int movingPlayer, int otherPlayer, int depth, Point selectedPoint) {
		ArrayList<Point> moves = new ArrayList<Point>();
		// elimination move
		if(state.getGrid()[1][5] == 0 || state.getGrid()[13][1] == 0){
			// only remove a piece that is not part of a mill
			for (int i = 3; i < state.getGrid().length-4; i++) {
				for (int j = 0; j < state.getGrid()[0].length; j++) {
					if(state.getGrid()[i][j] == otherPlayer && !detectThreeInLine(i, j, state)){
						moves.add(new Point(i, j));
					}
				}
			}
			// all opponent pieces are part of a mill - one can remove any of them
			if(moves.isEmpty()){
				for (int i = 3; i < state.getGrid().length-4; i++) {
					for (int j = 0; j < state.getGrid()[0].length; j++) {
						if(state.getGrid()[i][j] == otherPlayer){
							moves.add(new Point(i, j));
						}
					}
				}
			}
			return moves;
		}
		if(state.getGrid()[14][6] == 2){ // phase 1
			for (int i = 3; i < state.getGrid().length-4; i++){
				for (int j = 0; j < state.getGrid()[0].length; j++){
					if(state.getGrid()[i][j] == 0){
						moves.add(new Point(i, j));
					}
				}
			}
		}else{ // phase 2
			if(selectedPoint == null){ // selecting
				for (int i = 3; i < state.getGrid().length-4; i++){
					for (int j = 0; j < state.getGrid()[0].length; j++){
						if(state.getGrid()[i][j] == movingPlayer && !possibleAdjacentMoves(state, i, j, null).isEmpty()){
							moves.add(new Point(i, j));
						}
					}
				}
			}else{ // moving a selected piece
				if(state.getGrid()[selectedPoint.x][selectedPoint.y] == movingPlayer){
					moves = possibleAdjacentMoves(state, selectedPoint.x, selectedPoint.y, moves);
				}
			}
		}
		return moves;
	}

	@Override
	public int calcWinner(IStateOBJ state, int movingPlayer, int otherPlayer) {
		boolean foundMove = false;
		int p1Pieces = 0;
		int p2Pieces = 0;

		if(numOfRounds > 80){ // if the game drags on for a long time, force it to end with a draw
			return -1; // draw
		}

		// check to see if the movingPlayer has any available moves at all - if not then he loses
		ArrayList<Point> selects = possibleMoves(state, otherPlayer, movingPlayer, 0, null);
		for (int i = 0; i < selects.size(); i++) {
			ArrayList<Point> moves = possibleMoves(state, otherPlayer, movingPlayer, 0, selects.get(i));
			if(moves.size() != 0){
				foundMove = true;
				break;
			}
		}
		if(!foundMove){ // any available moves?
			if(movingPlayer == 1){
				return 1; // blue lost
			}else if(movingPlayer == 2){
				return 2; // red lost
			}
		}

		for (int i = 0; i < state.getGrid().length; i++) {
			for (int j = 0; j < state.getGrid()[0].length; j++) {
				if(state.getGrid()[i][j] == 1){
					p1Pieces++;
				}else if(state.getGrid()[i][j] == 2){
					p2Pieces++;
				}
				if(p1Pieces > 2 && p2Pieces > 2){
					return 0;
				}
			}
		}
		if(p1Pieces < 3){
			return 2;
		}else if(p2Pieces < 3){
			return 1;
		}else{
			return 0;
		}
	}

	@Override
	public IStateOBJ setupNewMatch(int boardSizeX, int boardSizeY, int difficulty1, int difficulty2) {
		if(difficulty1 == 1){
			depth1 = 4;
		}else if(difficulty1 == 2){
			depth1 = 8;
		}else if(difficulty1 == 3){
			depth1 = 10;
		}
		if(difficulty2 == 1){
			depth2 = 4;
		}else if(difficulty2 == 2){
			depth2 = 8;
		}else if(difficulty2 == 3){
			depth2 = 10;
		}
		int[][] grid = new int[boardSizeX+8][boardSizeY];
		// 1 1 1 x 0 x x 0 x x 0 x x x x
		// 1 1 1 x x 0 x 0 x 0 x x x x x
		// 1 1 1 x x x 0 0 0 x x x x x x
		// x x x x 0 0 0 x 0 0 0 x x x x
		// x x x x x x 0 0 0 x x x 2 2 2
		// x x x x x 0 x 0 x 0 x x 2 2 2
		// x x x x 0 x x 0 x x 0 x 2 2 2
		grid[0][0] =  1; grid[1][0] =  1; grid[2][0] =  1; grid[3][0] = -1; grid[4][0] =  0; grid[5][0] = -1; grid[6][0] = -1; grid[7][0] =  0; grid[8][0] = -1; grid[9][0] = -1; grid[10][0] =  0; grid[11][0] = -1; grid[12][0] = -1; grid[13][0] = -1; grid[14][0] = -1;
		grid[0][1] =  1; grid[1][1] =  1; grid[2][1] =  1; grid[3][1] = -1; grid[4][1] = -1; grid[5][1] =  0; grid[6][1] = -1; grid[7][1] =  0; grid[8][1] = -1; grid[9][1] =  0; grid[10][1] = -1; grid[11][1] = -1; grid[12][1] = -1; grid[13][1] = -1; grid[14][1] = -1;
		grid[0][2] =  1; grid[1][2] =  1; grid[2][2] =  1; grid[3][2] = -1; grid[4][2] = -1; grid[5][2] = -1; grid[6][2] =  0; grid[7][2] =  0; grid[8][2] =  0; grid[9][2] = -1; grid[10][2] = -1; grid[11][2] = -1; grid[12][2] = -1; grid[13][2] = -1; grid[14][2] = -1;
		grid[0][3] = -1; grid[1][3] = -1; grid[2][3] = -1; grid[3][3] = -1; grid[4][3] =  0; grid[5][3] =  0; grid[6][3] =  0; grid[7][3] = -1; grid[8][3] =  0; grid[9][3] =  0; grid[10][3] =  0; grid[11][3] = -1; grid[12][3] = -1; grid[13][3] = -1; grid[14][3] = -1;
		grid[0][4] = -1; grid[1][4] = -1; grid[2][4] = -1; grid[3][4] = -1; grid[4][4] = -1; grid[5][4] = -1; grid[6][4] =  0; grid[7][4] =  0; grid[8][4] =  0; grid[9][4] = -1; grid[10][4] = -1; grid[11][4] = -1; grid[12][4] =  2; grid[13][4] =  2; grid[14][4] =  2;
		grid[0][5] = -1; grid[1][5] = -1; grid[2][5] = -1; grid[3][5] = -1; grid[4][5] = -1; grid[5][5] =  0; grid[6][5] = -1; grid[7][5] =  0; grid[8][5] = -1; grid[9][5] =  0; grid[10][5] = -1; grid[11][5] = -1; grid[12][5] =  2; grid[13][5] =  2; grid[14][5] =  2;
		grid[0][6] = -1; grid[1][6] = -1; grid[2][6] = -1; grid[3][6] = -1; grid[4][6] =  0; grid[5][6] = -1; grid[6][6] = -1; grid[7][6] =  0; grid[8][6] = -1; grid[9][6] = -1; grid[10][6] =  0; grid[11][6] = -1; grid[12][6] =  2; grid[13][6] =  2; grid[14][6] =  2;
		IStateOBJ state = new StateOBJGrid(grid);
		return state;
	}

	@Override
	public int setPlayerTurn(int playerTurn) {
		if(selPoint!=null){
			return playerTurn;
		}
		if(playerTurn == 2){
			numOfRounds++;// count number of turns - help counter long fights
			return 1;
		}else{
			return 2;
		}
	}

	@Override
	public boolean[][] prepareBoardForGUI(int movingPlayer, int otherPlayer) {
		// the player is only allowed a set of moves, here we define what moves that are allowed
		ArrayList<Point> moves = possibleMoves(board, movingPlayer, otherPlayer, -1, selPoint);
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
	public String getStats() {
		if(board == null){
			return "Game not yet started.";
		}else{
			int p1Count = 0, p2Count = 0;
			int availablePiecesPlayer1 = 0, availablePiecesPlayer2 = 0;
			for (int i = 3; i < board.getGrid().length-4; i++) {
				for (int j = 0; j < board.getGrid()[0].length; j++) {
					if(board.getGrid()[i][j] == 1){
						p1Count++;
					}else if(board.getGrid()[i][j] == 2){
						p2Count++;
					}
				}
			}
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < board.getGrid()[0].length-4; j++) {
					if(board.getGrid()[i][j] == 1){
						availablePiecesPlayer1++;
					}
				}
			}
			for (int i = 12; i < board.getGrid().length; i++) {
				for (int j = 4; j < board.getGrid()[0].length; j++) {
					if(board.getGrid()[i][j] == 2){
						availablePiecesPlayer2++;
					}
				}
			}
			String msg =  ""
					+ "Player1 tokens [" + p1Count + "]\n"
					+ "Player2 tokens [" + p2Count + "]\n"
					+ "Number of rounds left [" + (80-numOfRounds) + "]\n";
			if(availablePiecesPlayer1 > 0){
				msg += "Player1 tokens unplaced [" + availablePiecesPlayer1 + "]\n";
			}
			if(availablePiecesPlayer2 > 0){
				msg += "Player2 tokens unplaced [" + availablePiecesPlayer2 + "]\n";
			}
			if(availablePiecesPlayer1 > 0 || availablePiecesPlayer2 > 0){
				msg += "Place coins\n";
			}else{
				msg += "Move coins.\n";
			}
			if(selPoint != null && selPoint.x != 0 && selPoint.y != 0){
				msg += "Selected Point [" + selPoint.x + "][" + selPoint.y + "]\n";
			}else if(selPoint != null && selPoint.x == 0 && selPoint.y == 0){
				msg += "Elimination move. Remove an opponent piece.\n";
			}
			return msg;
		}
	}

	@Override
	public String getGameName(){ return "Mills"; }
	
	@Override
	public int getGameID(){ return 7; }
	
	@Override
	public IStateOBJ processMovePlayer(Point p, IStateOBJ state, int movingPlayer, int otherPlayer) {
		ArrayList<Point> moves = possibleMoves(state, movingPlayer, otherPlayer, -1, selPoint);
		boolean moveExists = false;
		for (int i = 0; i < moves.size(); i++) {
			if(moves.get(i).x == p.x && moves.get(i).y == p.y){
				moveExists = true;
				break;
			}
		}
		if(!moveExists){ // it is allowed to click on another player if it is an elimination move
			System.out.println("error - illegal spot pressed");
			selPoint = null;
			return null;
		}

		if(state.getGrid()[p.x][p.y] == movingPlayer){
			selPoint = p;
			return state;
		}else{
			IStateOBJ s = processMove(p.x, p.y, state, movingPlayer, otherPlayer, selPoint);
			// preparing next turn to be an elimination move
			if(s.getGrid()[1][5] == 0 || s.getGrid()[13][1] == 0){
				selPoint = new Point(0, 0); // set this to force the player to make another move - eliminiation move
			}else{
				selPoint = null;
			}
			return s;
		}
	}

	@Override
	public ArrayList<IStateOBJ> processMoveAlgorithm(IStateOBJ state, int movingPlayer, int otherPlayer, int depth) {
		ArrayList<Point> moves = possibleMoves(state, movingPlayer, otherPlayer, depth, null);
		ArrayList<IStateOBJ> stateMoves = new ArrayList<IStateOBJ>();
		for (int i = 0; i < moves.size(); i++) {
			Point p = moves.get(i);
			if(state.getGrid()[p.x][p.y] == movingPlayer){ // phase 2 - select
				Point selP = new Point(p.x, p.y);
				ArrayList<Point> m = possibleMoves(state, movingPlayer, otherPlayer, depth, selP);
				for (int j = 0; j < m.size(); j++) {
					IStateOBJ s = processMove(m.get(j).x, m.get(j).y, new StateOBJGrid(MiscTools.clone2DIntArray(state.getGrid())), movingPlayer, otherPlayer, selP);
					if(s.getGrid()[1][5] == 0 || s.getGrid()[13][1] == 0){ // elimination
						ArrayList<Point> mm = possibleMoves(s, movingPlayer, otherPlayer, depth, null);
						for (int k = 0; k < mm.size(); k++) {
							IStateOBJ gg = processMove(mm.get(k).x, mm.get(k).y, new StateOBJGrid(MiscTools.clone2DIntArray(s.getGrid())), movingPlayer, otherPlayer, null);
							stateMoves.add(gg);
						}
					}else{
						stateMoves.add(s);
					}
				}
			}else{ // phase 1
				IStateOBJ s = processMove(p.x, p.y, new StateOBJGrid(MiscTools.clone2DIntArray(state.getGrid())), movingPlayer, otherPlayer, null);
				if(s.getGrid()[1][5] == 0 || s.getGrid()[13][1] == 0){ // elimination
					ArrayList<Point> m = possibleMoves(s, movingPlayer, otherPlayer, depth, null);
					for (int k = 0; k < m.size(); k++) {
						IStateOBJ gg = processMove(m.get(k).x, m.get(k).y, new StateOBJGrid(MiscTools.clone2DIntArray(s.getGrid())), movingPlayer, otherPlayer, null);
						stateMoves.add(gg);
					}
				}else{
					stateMoves.add(s);
				}
			}
		}
		return stateMoves;
	}

	@Override
	public int evaluation(IStateOBJ state, int movingPlayer, int max, int min) {
		int val = 0;
		int p1Val = 0;
		int p2Val = 0;
		for (int i = 0; i < state.getGrid().length; i++) {
			for (int j = 0; j < state.getGrid()[0].length; j++) {
				if(state.getGrid()[i][j] == 1){
					p1Val += 1;
				}else if(state.getGrid()[i][j] == 2){
					p2Val += 1;
				}
			}
		}
		if(movingPlayer == 1){
			val = (p1Val - p2Val); // blue
		}else{
			val = (p2Val - p1Val); // red
		}
		return val;
	}

	@Override
	public ArrayList<IStateOBJ> narrowMoves(IStateOBJ state, int movingPlayer, int otherPlayer) {
		ArrayList<IStateOBJ> moves = processMoveAlgorithm(state, movingPlayer, otherPlayer, -1);
		return moves;
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

	private boolean detectThreeInLine(int x, int y, IStateOBJ state){
		int[][] grid = state.getGrid();
		// y0 - - - - 0 x x 0 x x 0 - - - -
		// y1 - - - - x 0 x 0 x 0 x - - - -
		// y2 - - - - x x 0 0 0 x x - - - -
		// y3 - - - - 0 0 0 x 0 0 0 - - - -
		// y4 - - - - x x 0 0 0 x x - - - -
		// y5 - - - - x 0 x 0 x 0 x - - - -
		// y6 - - - - 0 x x 0 x x 0 - - - -
		// x  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4
		boolean ret = false; // return 0 for no three in line, 1 for one three in line and 2 for two three in line
		int p = grid[x][y];
		// horizontal
		if(x >= 4 && 10 >= x && y ==  0 && grid[4][0] == p && grid[7][0] == p && grid[10][0] == p){
			ret = true;
		}else if(x >= 5 &&  9 >= x && y ==  1 && grid[5][1] == p && grid[7][1] == p && grid[ 9][1] == p){
			ret = true;
		}else if(x >= 6 &&  8 >= x && y ==  2 && grid[6][2] == p && grid[7][2] == p && grid[ 8][2] == p){
			ret = true;
		}else if(x >= 4 &&  6 >= x && y ==  3 && grid[4][3] == p && grid[5][3] == p && grid[ 6][3] == p){
			ret = true;
		}else if(x >= 8 && 10 >= x && y ==  3 && grid[8][3] == p && grid[9][3] == p && grid[10][3] == p){
			ret = true;
		}else if(x >= 6 &&  8 >= x && y ==  4 && grid[6][4] == p && grid[7][4] == p && grid[ 8][4] == p){
			ret = true;
		}else if(x >= 5 &&  9 >= x && y ==  5 && grid[5][5] == p && grid[7][5] == p && grid[ 9][5] == p){
			ret = true;
		}else if(x >= 4 && 10 >= x && y ==  6 && grid[4][6] == p && grid[7][6] == p && grid[10][6] == p){
			ret = true;
		}
		// vertical
		else if(y >= 0 &&  6 >= y && x ==  4 && grid[ 4][0] == p && grid[ 4][3] == p && grid[ 4][6] == p){
			ret = true;
		}else if(y >= 1 &&  5 >= y && x ==  5 && grid[ 5][1] == p && grid[ 5][3] == p && grid[ 5][5] == p){
			ret = true;
		}else if(y >= 2 &&  4 >= y && x ==  6 && grid[ 6][2] == p && grid[ 6][3] == p && grid[ 6][4] == p){
			ret = true;
		}else if(y >= 0 &&  2 >= y && x ==  7 && grid[ 7][0] == p && grid[ 7][1] == p && grid[ 7][2] == p){
			ret = true;
		}else if(y >= 4 &&  6 >= y && x ==  7 && grid[ 7][4] == p && grid[ 7][5] == p && grid[ 7][6] == p){
			ret = true;
		}else if(y >= 2 &&  4 >= y && x ==  8 && grid[ 8][2] == p && grid[ 8][3] == p && grid[ 8][4] == p){
			ret = true;
		}else if(y >= 1 &&  5 >= y && x ==  9 && grid[ 9][1] == p && grid[ 9][3] == p && grid[ 9][5] == p){
			ret = true;
		}else if(y >= 0 &&  6 >= y && x == 10 && grid[10][0] == p && grid[10][3] == p && grid[10][6] == p){
			ret = true;
		}

		return ret;
	}

	private ArrayList<Point> possibleAdjacentMoves(IStateOBJ state, int x, int y, ArrayList<Point> m){
		// y0 - - - - 0 x x 0 x x 0 - - - -
		// y1 - - - - x 0 x 0 x 0 x - - - -
		// y2 - - - - x x 0 0 0 x x - - - -
		// y3 - - - - 0 0 0 x 0 0 0 - - - -
		// y4 - - - - x x 0 0 0 x x - - - -
		// y5 - - - - x 0 x 0 x 0 x - - - -
		// y6 - - - - 0 x x 0 x x 0 - - - -
		// x  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4
		if(m == null){
			m = new ArrayList<Point>();
		}
		int p = state.getGrid()[x][y];
		int count = 0;
		boolean isPhase3 = true;
		for (int i = 3; i < state.getGrid().length-4; i++){ // check to see if we are in phase 2 or 3
			for (int j = 0; j < state.getGrid()[0].length; j++){
				if(p == state.getGrid()[i][j]){
					count++;
				}
				if(count > 3){
					isPhase3 = false;
					break;
				}
			}
			if(!isPhase3){break;}
		}
		if(isPhase3){
			for (int i = 3; i < state.getGrid().length-4; i++){
				for (int j = 0; j < state.getGrid()[0].length; j++){
					if(state.getGrid()[i][j] == 0){
						m.add(new Point(i,j));
					}
				}
			}
		}
		else if(x == 4 && y == 0){if(state.getGrid()[4][3]==0){m.add(new Point(4,3));}if(state.getGrid()[7][0]==0){m.add(new Point(7,0));}}
		else if(x == 7 && y == 0){if(state.getGrid()[4][0]==0){m.add(new Point(4,0));}if(state.getGrid()[7][1]==0){m.add(new Point(7,1));}if(state.getGrid()[10][0]==0){m.add(new Point(10,0));}}
		else if(x ==10 && y == 0){if(state.getGrid()[7][0]==0){m.add(new Point(7,0));}if(state.getGrid()[10][3]==0){m.add(new Point(10,3));}}
		else if(x == 5 && y == 1){if(state.getGrid()[7][1]==0){m.add(new Point(7,1));}if(state.getGrid()[5][3]==0){m.add(new Point(5,3));}}
		else if(x == 7 && y == 1){if(state.getGrid()[7][0]==0){m.add(new Point(7,0));}if(state.getGrid()[5][1]==0){m.add(new Point(5,1));}if(state.getGrid()[9][1]==0){m.add(new Point(9,1));}if(state.getGrid()[7][2]==0){m.add(new Point(7,2));}}
		else if(x == 9 && y == 1){if(state.getGrid()[7][1]==0){m.add(new Point(7,1));}if(state.getGrid()[9][3]==0){m.add(new Point(9,3));}}
		else if(x == 6 && y == 2){if(state.getGrid()[7][2]==0){m.add(new Point(7,2));}if(state.getGrid()[6][3]==0){m.add(new Point(6,3));}}
		else if(x == 7 && y == 2){if(state.getGrid()[6][2]==0){m.add(new Point(6,2));}if(state.getGrid()[8][2]==0){m.add(new Point(8,2));}if(state.getGrid()[7][1]==0){m.add(new Point(7,1));}}
		else if(x == 8 && y == 2){if(state.getGrid()[7][2]==0){m.add(new Point(7,2));}if(state.getGrid()[8][3]==0){m.add(new Point(8,3));}}
		else if(x == 4 && y == 3){if(state.getGrid()[4][0]==0){m.add(new Point(4,0));}if(state.getGrid()[5][3]==0){m.add(new Point(5,3));}if(state.getGrid()[4][6]==0){m.add(new Point(4,6));}}
		else if(x == 5 && y == 3){if(state.getGrid()[4][3]==0){m.add(new Point(4,3));}if(state.getGrid()[5][1]==0){m.add(new Point(5,1));}if(state.getGrid()[6][3]==0){m.add(new Point(6,3));}if(state.getGrid()[5][5]==0){m.add(new Point(5,5));}}
		else if(x == 6 && y == 3){if(state.getGrid()[5][3]==0){m.add(new Point(5,3));}if(state.getGrid()[6][2]==0){m.add(new Point(6,2));}if(state.getGrid()[6][4]==0){m.add(new Point(6,4));}}
		else if(x == 8 && y == 3){if(state.getGrid()[8][2]==0){m.add(new Point(8,2));}if(state.getGrid()[8][4]==0){m.add(new Point(8,4));}if(state.getGrid()[9][3]==0){m.add(new Point(9,3));}}
		else if(x == 9 && y == 3){if(state.getGrid()[8][3]==0){m.add(new Point(8,3));}if(state.getGrid()[9][1]==0){m.add(new Point(9,1));}if(state.getGrid()[9][5]==0){m.add(new Point(9,5));}if(state.getGrid()[10][3]==0){m.add(new Point(10,3));}}
		else if(x ==10 && y == 3){if(state.getGrid()[9][3]==0){m.add(new Point(9,3));}if(state.getGrid()[10][0]==0){m.add(new Point(10,0));}if(state.getGrid()[10][6]==0){m.add(new Point(10,6));}}
		else if(x == 6 && y == 4){if(state.getGrid()[6][3]==0){m.add(new Point(6,3));}if(state.getGrid()[7][4]==0){m.add(new Point(7,4));}}
		else if(x == 7 && y == 4){if(state.getGrid()[6][4]==0){m.add(new Point(6,4));}if(state.getGrid()[7][5]==0){m.add(new Point(7,5));}if(state.getGrid()[8][4]==0){m.add(new Point(8,4));}}
		else if(x == 8 && y == 4){if(state.getGrid()[7][4]==0){m.add(new Point(7,4));}if(state.getGrid()[8][3]==0){m.add(new Point(8,3));}}
		else if(x == 5 && y == 5){if(state.getGrid()[5][3]==0){m.add(new Point(5,3));}if(state.getGrid()[7][5]==0){m.add(new Point(7,5));}}
		else if(x == 7 && y == 5){if(state.getGrid()[5][5]==0){m.add(new Point(5,5));}if(state.getGrid()[7][6]==0){m.add(new Point(7,6));}if(state.getGrid()[9][5]==0){m.add(new Point(9,5));}if(state.getGrid()[7][4]==0){m.add(new Point(7,4));}}
		else if(x == 9 && y == 5){if(state.getGrid()[7][5]==0){m.add(new Point(7,5));}if(state.getGrid()[9][3]==0){m.add(new Point(9,3));}}
		else if(x == 4 && y == 6){if(state.getGrid()[4][3]==0){m.add(new Point(4,3));}if(state.getGrid()[7][6]==0){m.add(new Point(7,6));}}
		else if(x == 7 && y == 6){if(state.getGrid()[4][6]==0){m.add(new Point(4,6));}if(state.getGrid()[7][5]==0){m.add(new Point(7,5));}if(state.getGrid()[10][6]==0){m.add(new Point(10,6));}}
		else if(x ==10 && y == 6){if(state.getGrid()[7][6]==0){m.add(new Point(7,6));}if(state.getGrid()[10][3]==0){m.add(new Point(10,3));}}

		return m;
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
