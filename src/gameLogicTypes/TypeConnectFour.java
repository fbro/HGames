package gameLogicTypes;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import gameLogic.IStateOBJ;
import gameLogic.StateOBJGrid;

public class TypeConnectFour extends TypeAbClassic implements IGameType{
	
	@Override
	public ArrayList<Point> possibleMoves(IStateOBJ state, int movingPlayer, int otherPlayer, int depth, Point selectedPoint){
		// This method gives the minimax algorithm a list of moves that are available
		ArrayList<Point> moves = new ArrayList<Point>();
		for (int i = 0; i < state.getGrid().length; i++){
			for (int j = state.getGrid()[0].length-1; j >= 0; j--){ // loop through all the childs
				if(state.getGrid()[i][j] == 0){
					moves.add(new Point(i, j)); // add all possible moves to this ArrayList and then later empty the list
					break;
				}
			}
		}
		return moves;
	}
	
	@Override
	public int calcWinner(IStateOBJ state, int movingPlayer, int otherPlayer){
		// return -1 = tie, 0 = not finished, 1 = player one won, 2 = player two won.
		// in the grid, if 0 then unused, if 1 then player one, if 2 then player 2.
		int x = state.getGrid()[0].length;
		int y = state.getGrid().length;
		int result = -1;
		for (int i = 0; i < state.getGrid().length; i++) { // i is y axis
			for (int j = 0; j < state.getGrid()[i].length; j++) { // j is x axis
				int spotColor = state.getGrid()[i][j]; // spot color: 0 unused, 1player(blue), 2player(red)
				if(spotColor == 0){ // if we just encounter one free spot it is not a tie anymore
					result = 0; // not finished
				}
				else{ // player 1 or player 2 has this spot.
					if(j+3 < x && (spotColor == state.getGrid()[i][j+1] && spotColor == state.getGrid()[i][j+2] && spotColor == state.getGrid()[i][j+3])){
						// Horizontal - from left to right
						return spotColor;
					}else if(i+3 < y && (spotColor == state.getGrid()[i+1][j] && spotColor == state.getGrid()[i+2][j] && spotColor == state.getGrid()[i+3][j])){
						// Vertical - downwards
						return spotColor;
					}else if(j+3 < x && i+3 < y && (spotColor == state.getGrid()[i+1][j+1] && spotColor == state.getGrid()[i+2][j+2] && spotColor == state.getGrid()[i+3][j+3])){
						// Diagonal - downwards - left to right
						return spotColor;
					}else if(j+3 < x && i-3 >= 0 && (spotColor == state.getGrid()[i-1][j+1] && spotColor == state.getGrid()[i-2][j+2] && spotColor == state.getGrid()[i-3][j+3])){
						// Diagonal - upwards - left to right
						return spotColor;
					}
				}
			}
		}
		return result; // a tie
	}

	@Override
	public IStateOBJ setupNewMatch(int boardSizeX, int boardSizeY, int difficulty1, int difficulty2){
		if(difficulty1 == 1){
			depth1 = 2;
		}else if(difficulty1 == 2){
			depth1 = 6;
		}else if(difficulty1 == 3){
			depth1 = 10;
		}
		if(difficulty2 == 1){
			depth2 = 2;
		}else if(difficulty2 == 2){
			depth2 = 6;
		}else if(difficulty2 == 3){
			depth2 = 10;
		}
		IStateOBJ state = new StateOBJGrid(new int[boardSizeX][boardSizeY]);
		
		return state;
	}
	
	@Override
	public String getGameName(){ return "ConnectFour"; }
	
	@Override
	public int getGameID(){ return 3; }
	
	@Override
	public IStateOBJ processMovePlayer(Point p, IStateOBJ state, int movingPlayer, int otherPlayer) {
		// changes the y coordinate to properly fall down on its place, adhering to the connect four rules
		boolean found = false;
		for (int i = state.getGrid()[0].length-1; i >= 0; i--) {
			if(state.getGrid()[p.x][i] == 0){
				p.y = i;
				found = true;
				break;
			}
		}
		if(!found){System.out.println("error - did not find a spot in the y aksis"); return null;}
		return processMove(p.x, p.y, state, movingPlayer, otherPlayer, null);
	}
	
	@Override
	public int evaluation(IStateOBJ state, int movingPlayer, int max, int min){
		int[][] grid = state.getGrid();
		// heuristic evaluation function
		// this map is dynamic, it ranges from 4*3 or 3*4 to something like 9*9
		int val = 0;
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				int v1 = 0, v2 = 0, v3 = 0, v4 = 0, v5 = 0;
				for (int k = 0; k < 4; k++) { // loop through the 3 different directions
					boolean isAllowed = false;
					if(k==0 && (i-2 >= 0 && i+2 <= grid.length-1)){ // Horizontal outOfBounds check
						v1 = grid[i-2][j  ];
						v2 = grid[i-1][j  ];
						v3 = grid[i  ][j  ];
						v4 = grid[i+1][j  ];
						v5 = grid[i+2][j  ];
						isAllowed = true;
					}else if(k==0 && (i-2 >= 0 && i+1 <= grid.length-1)){
						v1 = grid[i-2][j  ];
						v2 = grid[i-1][j  ];
						v3 = grid[i  ][j  ];
						v4 = grid[i+1][j  ];
						v5 = -1;
						isAllowed = true;
					}
					if(k==1 && (j-2 >= 0 && j+2 <= grid[0].length-1)){ // vertical outOfBounds check
						v1 = grid[i  ][j-2];
						v2 = grid[i  ][j-1];
						v3 = grid[i  ][j  ];
						v4 = grid[i  ][j+1];
						v5 = grid[i  ][j+2];
						isAllowed = true;
					}else if(k==1 && (j-2 >= 0 && j+1 <= grid[0].length-1)){
						v1 = grid[i  ][j-2];
						v2 = grid[i  ][j-1];
						v3 = grid[i  ][j  ];
						v4 = grid[i  ][j+1];
						v5 = -1;
						isAllowed = true;
					}
					if(k==2 && (i-2 >= 0 && j-2 >= 0 && i+2 <= grid.length-1 && j+2 <= grid[0].length-1)){ // diagonal down outOfBounds check
						v1 = grid[i-2][j-2];
						v2 = grid[i-1][j-1];
						v3 = grid[i  ][j  ];
						v4 = grid[i+1][j+1];
						v5 = grid[i+2][j+2];
						isAllowed = true;
					}else if(k==2 && (i-2 >= 0 && j-2 >= 0 && i+1 <= grid.length-1 && j+1 <= grid[0].length-1)){
						v1 = grid[i-2][j-2];
						v2 = grid[i-1][j-1];
						v3 = grid[i  ][j  ];
						v4 = grid[i+1][j+1];
						v5 = -1;
						isAllowed = true;
					}
					if(k==3 && (i-2 >= 0 && j-2 >= 0 && i+2 <= grid.length-1 && j+2 <= grid[0].length-1)){ // diagonal up outOfBounds check
						v1 = grid[i+2][j-2];
						v2 = grid[i+1][j-1];
						v3 = grid[i  ][j  ];
						v4 = grid[i-1][j+1];
						v5 = grid[i-2][j+2];
						isAllowed = true;
					}else if(k==3 && (i-1 >= 0 && j-2 >= 0 && i+2 <= grid.length-1 && j+1 <= grid[0].length-1)){
						v1 = grid[i+2][j-2];
						v2 = grid[i+1][j-1];
						v3 = grid[i  ][j  ];
						v4 = grid[i-1][j+1];
						v5 = -1;
						isAllowed = true;
					}
					if(isAllowed){
						int s = max;
						int h = min;
						for (int l = 0; l < 2; l++) { // first look for max placement then min placement
							// 5 in a line
							if(v5 != -1){
								if(       (v1==0&&v2==s&&v3==s&&v4==s&&v5==s) // 0xxxx
										||(v1==s&&v2==s&&v3==s&&v4==s&&v5==0) // xxxx0
										||(v1==h&&v2==s&&v3==s&&v4==s&&v5==s) // -xxxx
										||(v1==s&&v2==s&&v3==s&&v4==s&&v5==h) // xxxx-
										){
									if(s==max){val +=10000;}else{val +=-10000;}
								}else if( (v1==0 &&v2==s&&v3==s&&v4==s&&v5==0)){ // 0xxx0
									if(s==max){val +=  500;}else{val +=  -600;}
								}else if( (v1==s&&v2==0&&v3==s&&v4==0&&v5==s) // x0x0x
										||(v1==s&&v2==0&&v3==0&&v4==s&&v5==s) // x00xx
										||(v1==s&&v2==s&&v3==0&&v4==0&&v5==s) // xx00x
										||(v1==0&&v2==s&&v3==s&&v4==0&&v5==h) // 0xx0-
										||(v1==h&&v2==0&&v3==s&&v4==s&&v5==0) // -0xx0
										){
									if(s==max){val +=  100;}else{val +=  -200;}
								}else if( (v1==h&&v2==s&&v3==s&&v4==s&&v5==0) // -xxx0
										||(v1==0&&v2==s&&v3==s&&v4==s&&v5==h) // 0xxx-
										||(v1==s&&v2==s&&v3==s&&v4==0&&v5==0) // xxx00
										||(v1==0&&v2==0&&v3==s&&v4==s&&v5==s) // 00xxx
										||(v1==s&&v2==s&&v3==0&&v4==s&&v5==0) // xx0x0
										||(v1==0&&v2==s&&v3==0&&v4==s&&v5==s) // 0x0xx
										||(v1==0&&v2==s&&v3==s&&v4==0&&v5==s) // 0xx0x
										||(v1==s&&v2==0&&v3==s&&v4==s&&v5==0) // x0xx0
										||(v1==s&&v2==s&&v3==0&&v4==s&&v5==h) // xx0x-
										||(v1==h&&v2==s&&v3==0&&v4==s&&v5==s) // -x0xx
										||(v1==h&&v2==s&&v3==s&&v4==0&&v5==s) // -xx0x
										||(v1==s&&v2==0&&v3==s&&v4==s&&v5==h) // x0xx-
										){
									if(s==max){val +=  250;}else{val +=  -350;}
								}else if( (v1==0&&v2==s&&v3==s&&v4==0&&v5==0) // 0xx00
										||(v1==0&&v2==0&&v3==s&&v4==s&&v5==0) // 00xx0
										){
									if(s==max){val +=  100;}else{val +=  -300;}
								}
							}
							// there is only room for a 4 in line to be analysed at this spot
							else if(v5 == -1){
								if(v1==s&&v2==s&&v3==s&&v4==s){ // xxxx
									if(s==max){val +=10000;}else{val +=-10000;}
								}else if( (v1==0&&v2==s&&v3==s&&v4==s) // 0xxx
										||(v1==s&&v2==s&&v3==s&&v4==0) // xxx0
										||(v1==s&&v2==0&&v3==s&&v4==s) // x0xx
										||(v1==s&&v2==s&&v3==0&&v4==s) // xx0x
										){
									if(s==max){val +=  250;}else{val +=  -350;}
								}else if( (v1==0&&v2==s&&v3==s&&v4==0) // 0xx0
										||(v1==0&&v2==s&&v3==0&&v4==s) // 0x0x
										||(v1==0&&v2==0&&v3==s&&v4==s) // 00xx
										||(v1==s&&v2==0&&v3==s&&v4==0) // x0x0
										||(v1==s&&v2==0&&v3==0&&v4==s) // x00x
										||(v1==s&&v2==s&&v3==0&&v4==0) // xx00
										){
									if(s==max){val +=    5;}else{val +=   -10;}
								}
							}
							s = min;
							h = max;
						}
					}
				}
			}
		}
		return val; // return evaluated utility value
	}

	@Override
	public ArrayList<IStateOBJ> narrowMoves(IStateOBJ state, int movingPlayer, int otherPlayer) {
		ArrayList<IStateOBJ> moves;
		// do a default move if the board is blank
		boolean isBlank = true;
		for (int i = 0; i < state.getGrid().length; i++){ // check to see if it is a blank grid
			for (int j = 0; j < state.getGrid()[0].length; j++){
				if(state.getGrid()[i][j] != 0){
					isBlank = false;
					break;
				}
			}
			if(!isBlank){break;}
		}
		if(isBlank){
			moves = new ArrayList<IStateOBJ>();
			Random rand = new Random();
			int randX = rand.nextInt(3);
			moves.add(processMove((state.getGrid().length/2+1)-randX, state.getGrid()[0].length-1, state, movingPlayer, otherPlayer, null)); // place a piece in the middle, three places
			return moves;
		}
		moves = processMoveAlgorithm(state, movingPlayer, otherPlayer, -1);
		return moves;
	}
}
