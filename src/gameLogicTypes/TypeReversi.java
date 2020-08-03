package gameLogicTypes;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import gameLogic.IStateOBJ;
import gameLogic.StateOBJGrid;

public class TypeReversi extends TypeAbClassic implements IGameType{

	@Override
	public IStateOBJ processMove(int x, int y, IStateOBJ state, int movingPlayer, int otherPlayer, Point selectedPoint){
		state.getGrid()[x][y] = movingPlayer;
		for (int i = 1; i <= 8; i++) { // loop through the directions
			boolean possibleDirection = true;
			int foundOpponentPiece = 0;
			ArrayList<int[]> foundOpponentCoords = new ArrayList<int[]>();
			for (int k = 1; possibleDirection; k++) { // loop in a direction
				possibleDirection = false;
				int xx = 0, yy = 0; // the values that checks in the given direction
				switch (i) {
				case 1: if(x+k < state.getGrid().length                           ){possibleDirection = true; xx = k; yy = 0;}break; // east
				case 2: if(x-k >= 0                                    ){possibleDirection = true; xx =-k; yy = 0;}break; // west
				case 3: if(                        y+k < state.getGrid()[0].length){possibleDirection = true; xx = 0; yy = k;}break; // south
				case 4: if(                        y-k >= 0            ){possibleDirection = true; xx = 0; yy =-k;}break; // north
				case 5: if(x+k < state.getGrid().length    && y-k >= 0            ){possibleDirection = true; xx = k; yy =-k;}break; // north east
				case 6: if(x-k >= 0             && y-k >= 0            ){possibleDirection = true; xx =-k; yy =-k;}break; // north west
				case 7: if(x+k < state.getGrid()[0].length && y+k < state.getGrid().length   ){possibleDirection = true; xx = k; yy = k;}break; // south east
				case 8: if(x-k >= 0             && y+k < state.getGrid().length   ){possibleDirection = true; xx =-k; yy = k;}break; // south west

				}if(!possibleDirection){break;}
				if(state.getGrid()[x+xx][y+yy] == 0){
					break;
				}else if(state.getGrid()[x+xx][y+yy] == otherPlayer){
					foundOpponentPiece++;
					foundOpponentCoords.add(new int[]{x+xx, y+yy});
				}else if(state.getGrid()[x+xx][y+yy] == movingPlayer){
					if(foundOpponentPiece == 0){break;}
					else{
						for(int[] coord: foundOpponentCoords){
							state.getGrid()[coord[0]][coord[1]] = movingPlayer;
						}
						break;
					}
				}
			}
		}
		return state;
	}

	@Override
	public ArrayList<Point> possibleMoves(IStateOBJ state, int movingPlayer, int otherPlayer, int depth, Point selectedPoint){
		ArrayList<Point> moves = new ArrayList<Point>();
		for (int x = 0; x < state.getGrid().length; x++) {
			for (int y = 0; y < state.getGrid()[0].length; y++) { // loop on the 2d grid
				if(state.getGrid()[x][y] == 0){
					for (int i = 1; i <= 8; i++) { // loop through the directions
						boolean possibleDirection = true;
						int foundOpponentPiece = 0;
						for (int k = 1; possibleDirection; k++) { // loop in a direction
							possibleDirection = false;
							int xx = 0, yy = 0; // the values that checks in the given direction
							switch (i) {
							case 1: if(x+k < state.getGrid().length                           ){possibleDirection = true; xx = k; yy = 0;}break; // east
							case 2: if(x-k >= 0                                    ){possibleDirection = true; xx =-k; yy = 0;}break; // west
							case 3: if(                        y+k < state.getGrid()[0].length){possibleDirection = true; xx = 0; yy = k;}break; // south
							case 4: if(                        y-k >= 0            ){possibleDirection = true; xx = 0; yy =-k;}break; // north
							case 5: if(x+k < state.getGrid().length    && y-k >= 0            ){possibleDirection = true; xx = k; yy =-k;}break; // north east
							case 6: if(x-k >= 0             && y-k >= 0            ){possibleDirection = true; xx =-k; yy =-k;}break; // north west
							case 7: if(x+k < state.getGrid()[0].length && y+k < state.getGrid().length   ){possibleDirection = true; xx = k; yy = k;}break; // south east
							case 8: if(x-k >= 0             && y+k < state.getGrid().length   ){possibleDirection = true; xx =-k; yy = k;}break; // south west

							}if(!possibleDirection){break;}
							if(state.getGrid()[x+xx][y+yy] == 0){
								break;
							}else if(state.getGrid()[x+xx][y+yy] == otherPlayer){
								foundOpponentPiece++;
							}else if(state.getGrid()[x+xx][y+yy] == movingPlayer){
								if(foundOpponentPiece == 0){break;}
								else{
									moves.add(new Point(x, y));
									break;
								}
							}
						}
					}
				}
			}
		}
		return moves;
	}

	@Override
	public int calcWinner(IStateOBJ state, int movingPlayer, int otherPlayer){
		// return -1 = tie, 0 = not finished, 1 = player one won, 2 = player two won.
		// in the grid, if 0 then unused, if 1 then player one, if 2 then player 2.
		int player1 = 0;
		int player2 = 0;
		for (int i = 0; i < state.getGrid()[0].length; i++) {
			for (int j = 0; j < state.getGrid().length; j++) {
				if(state.getGrid()[j][i] == 1){
					player1++;
				}else if(state.getGrid()[j][i] == 2){
					player2++;
				}else{ // an empty spot
					ArrayList<Point> moves = possibleMoves(state, otherPlayer, movingPlayer, -1, null);
					if(!moves.isEmpty()){
						return 0; // game not finished
					}
				}
			}
		}
		if(player1 > player2){ // player1 wins
			return 1;
		}else if(player2 > player1){ // player2 wins
			return 2;
		}else{ // tie
			return -1;
		}
	}

	@Override
	public IStateOBJ setupNewMatch(int boardSizeX, int boardSizeY, int difficulty1, int difficulty2){
		// reversi is an 8*8 board, with this starting board:
		//  0  0  0  0  0  0  0  0 y0
		//  0  0  0  0  0  0  0  0 y1
		//  0  0  0  0  0  0  0  0 y2
		//  0  0  0  1  2  0  0  0 y3
		//  0  0  0  2  1  0  0  0 y4
		//  0  0  0  0  0  0  0  0 y5
		//  0  0  0  0  0  0  0  0 y6
		//  0  0  0  0  0  0  0  0 y7
		// x0 x1 x2 x3 x4 x5 x6 x7
		if(difficulty1 == 1){
			depth1 = 2;
		}else if(difficulty1 == 2){
			depth1 = 4;
		}else if(difficulty1 == 3){
			depth1 = 10;
		}
		if(difficulty2 == 1){
			depth2 = 2;
		}else if(difficulty2 == 2){
			depth2 = 4;
		}else if(difficulty2 == 3){
			depth2 = 10;
		}
		int[][] board = new int[boardSizeX][boardSizeY];
		board[3][3] = 1; // the unique starting position for reversi
		board[4][4] = 1;
		board[3][4] = 2;
		board[4][3] = 2;
		IStateOBJ state = new StateOBJGrid(board);
		return state;
	}

	@Override
	public String getGameName(){ return "Reversi"; }
	
	@Override
	public int getGameID(){ return 4; }
	
	@Override
	public IStateOBJ processMovePlayer(Point p, IStateOBJ state, int movingPlayer, int otherPlayer) {
		ArrayList<Point> moves = possibleMoves(state, movingPlayer, otherPlayer, -1, null);
		boolean foundMove = false;
		for(Point point: moves){
			if(point.x == p.x && point.y == p.y){
				foundMove = true;
			}
		}
		if(!foundMove){
			System.out.println("error - illegal spot pressed");
			return null;
		}
		return processMove(p.x, p.y, state, movingPlayer, otherPlayer, null);
	}

	@Override
	public int evaluation(IStateOBJ state, int movingPlayer, int max, int min){
		// heuristic evaluation function
		// this map range is 8*8
		// 8 3 3 3 3 3 3 8
		// 3 0 0 0 0 0 0 3
		// 3 0 0 0 0 0 0 3
		// 3 0 0 0 0 0 0 3
		// 3 0 0 0 0 0 0 3
		// 3 0 0 0 0 0 0 3
		// 3 0 0 0 0 0 0 3
		// 8 3 3 3 3 3 3 8
		int val = 0;
		for (int i = 0; i < state.getGrid().length; i++) {
			for (int j = 0; j < state.getGrid()[0].length; j++) {
				int c = 0;
				if(state.getGrid()[i][j] == max){c = 1;} // the analysed spot is yours
				else if(state.getGrid()[i][j] == min){c = -1;} // the analysed spot is the opponents
				if(c != 0){
					if((i == 0 || i == state.getGrid().length-1) && (j == 0 || j == state.getGrid()[0].length-1)){val += 10 * c;} // corners
					else if(i == 0 || i == state.getGrid().length-1 || j == 0 || j == state.getGrid()[0].length-1){val += 3 * c;} // sides
				}
			}
		}

		int countYourPieces = 0;
		int countOpponentPieces = 0;
		for (int i = 0; i < state.getGrid().length; i++) {
			for (int j = 0; j < state.getGrid()[0].length; j++) {
				if(state.getGrid()[i][j] == max)
					countYourPieces++;
				else if(state.getGrid()[i][j] == min)
					countOpponentPieces++;
			}
		}
		val += ((countYourPieces - countOpponentPieces)*1);

		return val; // return evaluated utility value
	}

	@Override
	public ArrayList<IStateOBJ> narrowMoves(IStateOBJ state, int movingPlayer, int otherPlayer) {
		ArrayList<IStateOBJ> moves = processMoveAlgorithm(state, movingPlayer, otherPlayer, -1);;
		// do a default move if the board is blank
		int countBlank = 0;
		for (int i = 0; i < state.getGrid().length; i++){ // check to see if it is a blank grid
			for (int j = 0; j < state.getGrid()[0].length; j++){
				if(state.getGrid()[i][j] != 0){
					countBlank++;
					if(countBlank > 4)break;
				}
			}
			if(countBlank > 4)break;
		}
		if(countBlank <= 4){
			Random rand = new Random();
			IStateOBJ move = moves.get(rand.nextInt(4));
			ArrayList<IStateOBJ> singleMove = new ArrayList<IStateOBJ>();
			singleMove.add(move);
			return singleMove;
		}
		return moves;
	}
}