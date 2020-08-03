package gameLogicTypes;

import gameLogic.IStateOBJ;
import gameLogic.MiscTools;
import gameLogic.StateOBJGrid;

import java.awt.Point;
import java.util.ArrayList;

public class TypeCheckers implements IGameType {

	// pieces are ordered like this:
	// coin        Blue 0  3
	// double coin Blue 1  4

	// coin        Red  0 -3
	// double coin Red  1 -4

	private IStateOBJ board = null;
	private Point selPoint = null;
	private int depth1 = 0;
	private int depth2 = 0;
	private int difficulty1 = 0;
	private int difficulty2 = 0;
	private int numOfRounds = 0; // used in calcwinner to help end the game when stuck.
	private Point forceAnotherJump = null; // used by human player - processMoveAlgorithm has a similar thing. 

	@Override
	public IStateOBJ processMove(int x, int y, IStateOBJ state, int movingPlayer, int otherPlayer, Point selectedPoint) {
		if(selectedPoint == null){ // try to select
			if(state.getGrid()[x][y] == 0 || (state.getGrid()[x][y] <= -3 && movingPlayer == 1) || (state.getGrid()[x][y] >= 3 && movingPlayer == 2)){ // wrong piece
				System.out.println("illegal selected piece ("+x+","+y+")");
				return null;
			}
			else{ // correct piece selected
				ArrayList<Point> moves = possibleMoves(state, movingPlayer, otherPlayer, -1, new Point(x, y));
				if(moves.isEmpty()){ // deny select if the piece does not have any available moves
					System.out.println("no moves with selected piece ("+x+","+y+")");
					return null;
				}
				else{ // select
					return state;
				}
			}
		}
		else{ // try to move
			ArrayList<Point> moves = possibleMoves(state, movingPlayer, otherPlayer, -1, selectedPoint);
			boolean foundMove = false;
			for (int i = 0; i < moves.size(); i++) {
				if(moves.get(i).x == x && moves.get(i).y == y){
					foundMove = true;
					break;
				}
			}
			if(!foundMove){ // the move is not allowed with this selectedPoint
				System.out.println("move ("+x+","+y+") not allowed on selected point ("+selectedPoint.x+","+selectedPoint.y+")");
				return null;
			}
			else{ // move
				state.getGrid()[x][y] = state.getGrid()[selectedPoint.x][selectedPoint.y]; // set the moved coin
				state.getGrid()[selectedPoint.x][selectedPoint.y] = 0; // remove the coin from its old position
				// if the coin jumped over an adversary's coin, remove the adversary's coin
				if(x+2 == selectedPoint.x && y+2 == selectedPoint.y){
					state.getGrid()[x+1][y+1] = 0;
				}else if(x-2 == selectedPoint.x && y-2 == selectedPoint.y){
					state.getGrid()[x-1][y-1] = 0;
				}else if(x+2 == selectedPoint.x && y-2 == selectedPoint.y){
					state.getGrid()[x+1][y-1] = 0;
				}else if(x-2 == selectedPoint.x && y+2 == selectedPoint.y){
					state.getGrid()[x-1][y+1] = 0;
				}
				// if it moved to the back it converts to a double coin
				if(state.getGrid()[x][y] == 3 && y == 7){
					state.getGrid()[x][y] = 4;
				}else if(state.getGrid()[x][y] == -3 && y == 0){
					state.getGrid()[x][y] = -4;
				}
				return state;
			}
		}
	}

	@Override
	public ArrayList<Point> possibleMoves(IStateOBJ state, int movingPlayer, int otherPlayer, int depth, Point selectedPoint) {
		ArrayList<Point> moves = new ArrayList<Point>();
		int[][] grid = state.getGrid();
		// check to see if isAttackAvailable = true
		ArrayList<Point> m = new ArrayList<Point>(); // if there are available attacks then they will be added to this list
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				if((grid[i][j] != 0) && ((grid[i][j] >= 3 && movingPlayer == 1) || (grid[i][j] <= -3 && movingPlayer == 2))){
					Point p = new Point(i, j);
					switch (grid[p.x][p.y]) {
					case 3: // coin blue
						if(grid[0].length > p.y+2 && grid.length > p.x+2 && grid[p.x+2][p.y+2] == 0 && grid[p.x+1][p.y+1] <= -3){m.add(p);break;}
						if(grid[0].length > p.y+2 && p.x-2 >= 0          && grid[p.x-2][p.y+2] == 0 && grid[p.x-1][p.y+1] <= -3){m.add(p);break;}
						break;
					case 4: // double coin blue
						if(grid[0].length > p.y+2 && grid.length > p.x+2 && grid[p.x+2][p.y+2] == 0 && grid[p.x+1][p.y+1] <= -3){m.add(p);break;}
						if(grid[0].length > p.y+2 && p.x-2 >= 0          && grid[p.x-2][p.y+2] == 0 && grid[p.x-1][p.y+1] <= -3){m.add(p);break;}
						if(p.y-2 >= 0             && grid.length > p.x+2 && grid[p.x+2][p.y-2] == 0 && grid[p.x+1][p.y-1] <= -3){m.add(p);break;}
						if(p.y-2 >= 0             && p.x-2 >= 0          && grid[p.x-2][p.y-2] == 0 && grid[p.x-1][p.y-1] <= -3){m.add(p);break;}
						break;
					case -3: // coin red
						if(p.y-2 >= 0             && grid.length > p.x+2 && grid[p.x+2][p.y-2] == 0 && grid[p.x+1][p.y-1] >=  3){m.add(p);break;}
						if(p.y-2 >= 0             && p.x-2 >= 0          && grid[p.x-2][p.y-2] == 0 && grid[p.x-1][p.y-1] >=  3){m.add(p);break;}
						break;
					case -4: // double coin red
						if(grid[0].length > p.y+2 && grid.length > p.x+2 && grid[p.x+2][p.y+2] == 0 && grid[p.x+1][p.y+1] >=  3){m.add(p);break;}
						if(grid[0].length > p.y+2 && p.x-2 >= 0          && grid[p.x-2][p.y+2] == 0 && grid[p.x-1][p.y+1] >=  3){m.add(p);break;}
						if(p.y-2 >= 0             && grid.length > p.x+2 && grid[p.x+2][p.y-2] == 0 && grid[p.x+1][p.y-1] >=  3){m.add(p);break;}
						if(p.y-2 >= 0             && p.x-2 >= 0          && grid[p.x-2][p.y-2] == 0 && grid[p.x-1][p.y-1] >=  3){m.add(p);break;}
						break;
					}
				}
			}
		}
		if(selectedPoint == null){ // select - this is used by the computer, the player knows what he can select
			if(m.size() == 0){ // no attacks available yet...
				for (int i = 0; i < grid.length; i++) {
					for (int j = 0; j < grid[0].length; j++) {
						if((grid[i][j] != 0) && ((grid[i][j] >= 3 && movingPlayer == 1) || (grid[i][j] <= -3 && movingPlayer == 2))){
							moves.add(new Point(i, j));
						}
					}
				}
			}else{ // attacks are available - choose between the available attacks
				for (int i = 0; i < m.size(); i++) {
					moves.add(m.get(i));
				}
			}
		}
		else{ // move
			boolean isAttackAvailable = false;
			if(m.size() != 0){ // if there is an attack available then check if selectedPoint is the correct selected
				for (int i = 0; i < m.size(); i++) {
					if(selectedPoint.x == m.get(i).x && selectedPoint.y == m.get(i).y){
						isAttackAvailable = true;
						break;
					}
				}
				if(!isAttackAvailable)
					return moves;
			}
			Point p = selectedPoint;
			switch (grid[p.x][p.y]) {
			case 3: // coin blue
				// move
				if(!isAttackAvailable){
					if(grid[0].length > p.y+1 && grid.length > p.x+1 && grid[p.x+1][p.y+1] == 0                            ){moves.add(new Point(p.x+1, p.y+1));}
					if(grid[0].length > p.y+1 && p.x-1 >= 0          && grid[p.x-1][p.y+1] == 0                            ){moves.add(new Point(p.x-1, p.y+1));}
				}
				// attack
				if(grid[0].length > p.y+2 && grid.length > p.x+2 && grid[p.x+2][p.y+2] == 0 && grid[p.x+1][p.y+1] <= -3){moves.add(new Point(p.x+2, p.y+2));}
				if(grid[0].length > p.y+2 && p.x-2 >= 0          && grid[p.x-2][p.y+2] == 0 && grid[p.x-1][p.y+1] <= -3){moves.add(new Point(p.x-2, p.y+2));}
				break;

			case 4: // double coin blue
				// move
				if(!isAttackAvailable){
					if(grid[0].length > p.y+1 && grid.length > p.x+1 && grid[p.x+1][p.y+1] == 0                            ){moves.add(new Point(p.x+1, p.y+1));}
					if(grid[0].length > p.y+1 && p.x-1 >= 0          && grid[p.x-1][p.y+1] == 0                            ){moves.add(new Point(p.x-1, p.y+1));}
					if(p.y-1 >= 0             && grid.length > p.x+1 && grid[p.x+1][p.y-1] == 0                            ){moves.add(new Point(p.x+1, p.y-1));}
					if(p.y-1 >= 0             && p.x-1 >= 0          && grid[p.x-1][p.y-1] == 0                            ){moves.add(new Point(p.x-1, p.y-1));}
				}
				// attack
				if(grid[0].length > p.y+2 && grid.length > p.x+2 && grid[p.x+2][p.y+2] == 0 && grid[p.x+1][p.y+1] <= -3){moves.add(new Point(p.x+2, p.y+2));}
				if(grid[0].length > p.y+2 && p.x-2 >= 0          && grid[p.x-2][p.y+2] == 0 && grid[p.x-1][p.y+1] <= -3){moves.add(new Point(p.x-2, p.y+2));}
				if(p.y-2 >= 0             && grid.length > p.x+2 && grid[p.x+2][p.y-2] == 0 && grid[p.x+1][p.y-1] <= -3){moves.add(new Point(p.x+2, p.y-2));}
				if(p.y-2 >= 0             && p.x-2 >= 0          && grid[p.x-2][p.y-2] == 0 && grid[p.x-1][p.y-1] <= -3){moves.add(new Point(p.x-2, p.y-2));}
				break;

			case -3: // coin red
				// move
				if(!isAttackAvailable){
					if(p.y-1 >= 0             && grid.length > p.x+1 && grid[p.x+1][p.y-1] == 0                            ){moves.add(new Point(p.x+1, p.y-1));}
					if(p.y-1 >= 0             && p.x-1 >= 0          && grid[p.x-1][p.y-1] == 0                            ){moves.add(new Point(p.x-1, p.y-1));}
				}
				// attack
				if(p.y-2 >= 0             && grid.length > p.x+2 && grid[p.x+2][p.y-2] == 0 && grid[p.x+1][p.y-1] >=  3){moves.add(new Point(p.x+2, p.y-2));}
				if(p.y-2 >= 0             && p.x-2 >= 0          && grid[p.x-2][p.y-2] == 0 && grid[p.x-1][p.y-1] >=  3){moves.add(new Point(p.x-2, p.y-2));}
				break;

			case -4: // double coin red
				// move
				if(!isAttackAvailable){
					if(p.y-1 >= 0             && grid.length > p.x+1 && grid[p.x+1][p.y-1] == 0                            ){moves.add(new Point(p.x+1, p.y-1));}
					if(p.y-1 >= 0             && p.x-1 >= 0          && grid[p.x-1][p.y-1] == 0                            ){moves.add(new Point(p.x-1, p.y-1));}
					if(grid[0].length > p.y+1 && grid.length > p.x+1 && grid[p.x+1][p.y+1] == 0                            ){moves.add(new Point(p.x+1, p.y+1));}
					if(grid[0].length > p.y+1 && p.x-1 >= 0          && grid[p.x-1][p.y+1] == 0                            ){moves.add(new Point(p.x-1, p.y+1));}
				}
				// attack
				if(grid[0].length > p.y+2 && grid.length > p.x+2 && grid[p.x+2][p.y+2] == 0 && grid[p.x+1][p.y+1] >=  3){moves.add(new Point(p.x+2, p.y+2));}
				if(grid[0].length > p.y+2 && p.x-2 >= 0          && grid[p.x-2][p.y+2] == 0 && grid[p.x-1][p.y+1] >=  3){moves.add(new Point(p.x-2, p.y+2));}
				if(p.y-2 >= 0             && grid.length > p.x+2 && grid[p.x+2][p.y-2] == 0 && grid[p.x+1][p.y-1] >=  3){moves.add(new Point(p.x+2, p.y-2));}
				if(p.y-2 >= 0             && p.x-2 >= 0          && grid[p.x-2][p.y-2] == 0 && grid[p.x-1][p.y-1] >=  3){moves.add(new Point(p.x-2, p.y-2));}
				break;
			}
		}
		return moves;
	}

	@Override
	public int calcWinner(IStateOBJ state, int movingPlayer, int otherPlayer) {
		int countBlue = 0;
		int countBlueLarge = 0;
		int countRed = 0;
		int countRedLarge = 0;
		boolean foundBlue = false;
		boolean foundRed = false;
		boolean foundMove = false;
		int[][] grid = state.getGrid(); 
		// if the game drags on for a long time, force it to end with a draw
		if(numOfRounds > 80){
			//System.out.println("forcing a draw in checkers");
			for (int i = 0; i < grid.length; i++) {
				for (int j = 0; j < grid[0].length; j++) {
					if(grid[i][j] == 3){
						countBlue++;
					}else if(grid[i][j] == 4){
						countBlueLarge++;
					}
					if(grid[i][j] == -3){
						countRed++;
					}else if(grid[i][j] == -4){
						countRedLarge++;
					}
				}
			}
			if((countBlueLarge * 2 + countBlue) < (countRedLarge * 2 + countRed)){ // more red than blue
				return 2; // blue lost
			}else if((countBlueLarge * 2 + countBlue) > (countRedLarge * 2 + countRed)){ // more blue than red
				return 1; // red lost
			}else{
				return -1; // draw
			}
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

		// analyse grid to see which pieces are present and act if something is missing
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				if(grid[i][j] == 3){
					foundBlue = true;
					countBlue++;
				}else if(grid[i][j] == 4){
					foundBlue = true;
					countBlueLarge++;
				}
				if(grid[i][j] == -3){
					foundRed = true;
					countRed++;
				}else if(grid[i][j] == -4){
					foundRed = true;
					countRedLarge++;
				}
				if((foundBlue && foundRed) && (countBlueLarge + countBlue > 1 && countRedLarge + countRed > 1)){
					return 0; // game continues
				}
			}
		}
		if(!foundBlue){
			return 2; // blue lost
		}else if(!foundRed){
			return 1; // red lost
		}else if((countBlueLarge * 2 + countBlue) < (countRedLarge * 2 + countRed)){ // more red than blue
			return 2; // blue lost
		}else if((countBlueLarge * 2 + countBlue) > (countRedLarge * 2 + countRed)){ // more blue than red
			return 1; // red lost
		}else{
			return -1; // draw
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
		int[][] grid = new int[boardSizeX][boardSizeY];
		// x 3 x 3 x 3 x 3 | x 0 x 0 x 0 x 0 blue/white
		// 3 x 3 x 3 x 3 x | 0 x 0 x 0 x 0 x
		// x 3 x 3 x 3 x 3 | x 0 x 0 x 0 x 0
		// 0 x 0 x 0 x 0 x | x x x x x x x x
		// x 0 x 0 x 0 x 0 | x x x x x x x x
		//-3 x-3 x-3 x-3 x | 0 x 0 x 0 x 0 x
		// x-3 x-3 x-3 x-3 | x 0 x 0 x 0 x 0
		//-3 x-3 x-3 x-3 x | 0 x 0 x 0 x 0 x red/black
		grid[0][0] = -1; grid[1][0] =  3; grid[2][0] = -1; grid[3][0] =  3; grid[4][0] = -1; grid[5][0] =  3; grid[6][0] = -1; grid[7][0] =  3;
		grid[0][1] =  3; grid[1][1] = -1; grid[2][1] =  3; grid[3][1] = -1; grid[4][1] =  3; grid[5][1] = -1; grid[6][1] =  3; grid[7][1] = -1;
		grid[0][2] = -1; grid[1][2] =  3; grid[2][2] = -1; grid[3][2] =  3; grid[4][2] = -1; grid[5][2] =  3; grid[6][2] = -1; grid[7][2] =  3;
		grid[1][3] = -1; grid[3][3] = -1; grid[5][3] = -1; grid[7][3] = -1;
		grid[0][4] = -1; grid[2][4] = -1; grid[4][4] = -1; grid[6][4] = -1;
		grid[0][5] = -3; grid[1][5] = -1; grid[2][5] = -3; grid[3][5] = -1; grid[4][5] = -3; grid[5][5] = -1; grid[6][5] = -3; grid[7][5] = -1;
		grid[0][6] = -1; grid[1][6] = -3; grid[2][6] = -1; grid[3][6] = -3; grid[4][6] = -1; grid[5][6] = -3; grid[6][6] = -1; grid[7][6] = -3;
		grid[0][7] = -3; grid[1][7] = -1; grid[2][7] = -3; grid[3][7] = -1; grid[4][7] = -3; grid[5][7] = -1; grid[6][7] = -3; grid[7][7] = -1;
		StateOBJGrid state = new StateOBJGrid(grid);
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
	public String getStats(){
		if(board == null){
			return "Game not yet started.";
		}else{
			int p1Count = 0, p1DCount = 0, p2Count = 0, p2DCount = 0;
			for (int i = 0; i < board.getGrid().length; i++) {
				for (int j = 0; j < board.getGrid()[0].length; j++) {
					if(board.getGrid()[i][j] == 3){
						p1Count++;
					}else if(board.getGrid()[i][j] == 4){
						p1DCount++;
					}else if(board.getGrid()[i][j] == -3){
						p2Count++;
					}else if(board.getGrid()[i][j] == -4){
						p2DCount++;
					}
				}
			}
			String msg =  ""
					+ "Player1 tokens        [" + p1Count + "]\n"
					+ "Player1 double tokens [" + p1DCount + "]\n"
					+ "Player2 tokens        [" + p2Count + "]\n"
					+ "Player2 double tokens [" + p2DCount + "]\n"
					+ "Number of rounds left [" + (80-numOfRounds) + "]\n";
			if(selPoint != null){
				msg += "Selected Point [" + selPoint.x + "][" + selPoint.y + "]\n";
			}
			return msg;
		}
	}

	@Override
	public String getGameName(){ return "Checkers"; }
	
	@Override
	public int getGameID(){ return 6; }
	
	@Override
	public IStateOBJ processMovePlayer(Point p, IStateOBJ state, int movingPlayer, int otherPlayer) {
		if(selPoint != null && selPoint.x == p.x && selPoint.y == p.y){
			System.out.println("selected same piece");
			selPoint = null;
			return null;
		}else if(state.getGrid()[p.x][p.y] <= -3 && movingPlayer == 1 || state.getGrid()[p.x][p.y] >= 3 && movingPlayer == 2){
			System.out.println("error - illegal spot pressed");
			selPoint = null;
			return null; // return the point, processMove handles this error
		}else if(selPoint != null && forceAnotherJump != null && (selPoint.x != forceAnotherJump.x || selPoint.y != forceAnotherJump.y)){
			System.out.println("when attacking multiple times one must attack with the same piece");
			selPoint = null;
			return null;
		}
		IStateOBJ s = processMove(p.x, p.y, state, movingPlayer, otherPlayer, selPoint);
		if(s == null){
			selPoint = null; // error
			return null;
		}

		if(selPoint == null){ // normal select
			selPoint = p;
		}else{ // move
			// before returning the grid - check if there are more jumps available
			Integer xDif = selPoint.x-p.x;
			if(xDif < 0){xDif = xDif * -1;} // make sure it is an attack
			if(possibleAttacks(p, s, movingPlayer).size() > 0 && xDif == 2){
				selPoint = p;
				forceAnotherJump = p;
				//System.out.println("forceAnotherJump set: " + forceAnotherJump.x + ", " + forceAnotherJump.y);
			}else{
				selPoint = null;
				forceAnotherJump = null;
				//System.out.println("forceAnotherJump reset");
			}
		}
		return s;
	}

	@Override
	public ArrayList<IStateOBJ> processMoveAlgorithm(IStateOBJ state, int movingPlayer, int otherPlayer, int depth) {
		// this method is only used by the computer
		Point selFieldPoint = null;
		ArrayList<Point> movingPieces = possibleMoves(state, movingPlayer, otherPlayer, depth, selFieldPoint);
		ArrayList<IStateOBJ> stateMoves = new ArrayList<IStateOBJ>();

		// loop through possible pieces that can move
		for (int i = 0; i < movingPieces.size(); i++) {
			// loop through the selected piece and add its move to gridMoves
			selFieldPoint = movingPieces.get(i);
			ArrayList<Point> moves = possibleMoves(state, movingPlayer, otherPlayer, depth, selFieldPoint);
			for (int j = 0; j < moves.size(); j++) { // loop through the possible moves and generate the possible grids
				Point p = moves.get(j); // get the Point that was selected to move from
				IStateOBJ g = processMove(p.x, p.y, new StateOBJGrid(MiscTools.clone2DIntArray(state.getGrid())), movingPlayer, otherPlayer, selFieldPoint);

				Integer xDif = selFieldPoint.x-p.x;
				if(xDif < 0){xDif = xDif * -1;} // check to see if last move was an attack - if that is true then enter code below to anaylyse further moves
				if(xDif == 2){ // it is an attack
					boolean isAttacksAvailable = true; // is false when we know that there are no more attacks available with the selected piece
					ArrayList<IStateOBJ> incompleteMoves = new ArrayList<IStateOBJ>();
					incompleteMoves.add(g);
					ArrayList<Point> incompleteSelects = new ArrayList<Point>();
					incompleteSelects.add(p);
					while(isAttacksAvailable){
						ArrayList<Point> points = possibleAttacks(p, incompleteMoves.get(0), movingPlayer);
						if(!points.isEmpty()){
							for (int k = 0; k < points.size(); k++) {
								incompleteMoves.add(processMove(points.get(k).x, points.get(k).y, new StateOBJGrid(MiscTools.clone2DIntArray(incompleteMoves.get(0).getGrid())), movingPlayer, otherPlayer, incompleteSelects.get(0)));
								incompleteSelects.add(points.get(k));
							}
						}else{
							stateMoves.add(incompleteMoves.get(0));
						}
						incompleteMoves.remove(0);
						incompleteSelects.remove(0);
						if(incompleteMoves.isEmpty()){
							isAttacksAvailable = false;
						}
					}
				}else{
					stateMoves.add(g);
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
				if(state.getGrid()[i][j] == 3){
					p1Val += 1;
				}else if(state.getGrid()[i][j] == 4){
					p1Val += 10;
				}else if(state.getGrid()[i][j] == -3){
					p2Val += 1;
				}else if(state.getGrid()[i][j] == -4){
					p2Val += 10;
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

	private ArrayList<Point> possibleAttacks(Point p, IStateOBJ state, int movingPlayer){
		int[][] g = state. getGrid();
		ArrayList<Point> m = new ArrayList<Point>();
		if(movingPlayer == 1){ // blue
			if(g[p.x][p.y] == 3){
				if(g[0].length > p.y+2 && g.length > p.x+2 && g[p.x+2][p.y+2] == 0 && g[p.x+1][p.y+1] <= -3){m.add(new Point(p.x+2, p.y+2));}
				if(g[0].length > p.y+2 && p.x-2 >= 0       && g[p.x-2][p.y+2] == 0 && g[p.x-1][p.y+1] <= -3){m.add(new Point(p.x-2, p.y+2));}
			}
			else if(g[p.x][p.y] == 4){
				if(g[0].length > p.y+2 && g.length > p.x+2 && g[p.x+2][p.y+2] == 0 && g[p.x+1][p.y+1] <= -3){m.add(new Point(p.x+2, p.y+2));}
				if(g[0].length > p.y+2 && p.x-2 >= 0       && g[p.x-2][p.y+2] == 0 && g[p.x-1][p.y+1] <= -3){m.add(new Point(p.x-2, p.y+2));}
				if(p.y-2 >= 0          && g.length > p.x+2 && g[p.x+2][p.y-2] == 0 && g[p.x+1][p.y-1] <= -3){m.add(new Point(p.x+2, p.y-2));}
				if(p.y-2 >= 0          && p.x-2 >= 0       && g[p.x-2][p.y-2] == 0 && g[p.x-1][p.y-1] <= -3){m.add(new Point(p.x-2, p.y-2));}
			}
		}
		else if(movingPlayer == 2){ // red
			if(g[p.x][p.y] == -3){
				if(p.y-2 >= 0          && g.length > p.x+2 && g[p.x+2][p.y-2] == 0 && g[p.x+1][p.y-1] >=  3){m.add(new Point(p.x+2, p.y-2));}
				if(p.y-2 >= 0          && p.x-2 >= 0       && g[p.x-2][p.y-2] == 0 && g[p.x-1][p.y-1] >=  3){m.add(new Point(p.x-2, p.y-2));}
			}
			else if(g[p.x][p.y] == -4){
				if(g[0].length > p.y+2 && g.length > p.x+2 && g[p.x+2][p.y+2] == 0 && g[p.x+1][p.y+1] >=  3){m.add(new Point(p.x+2, p.y+2));}
				if(g[0].length > p.y+2 && p.x-2 >= 0       && g[p.x-2][p.y+2] == 0 && g[p.x-1][p.y+1] >=  3){m.add(new Point(p.x-2, p.y+2));}
				if(p.y-2 >= 0          && g.length > p.x+2 && g[p.x+2][p.y-2] == 0 && g[p.x+1][p.y-1] >=  3){m.add(new Point(p.x+2, p.y-2));}
				if(p.y-2 >= 0          && p.x-2 >= 0       && g[p.x-2][p.y-2] == 0 && g[p.x-1][p.y-1] >=  3){m.add(new Point(p.x-2, p.y-2));}
			}
		}
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