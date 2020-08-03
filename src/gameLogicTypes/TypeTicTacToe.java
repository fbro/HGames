package gameLogicTypes;

import gameLogic.IStateOBJ;
import gameLogic.StateOBJGrid;

public class TypeTicTacToe extends TypeAbClassic implements IGameType {

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
					if(j+2 < x && (spotColor == state.getGrid()[i][j+1] && spotColor == state.getGrid()[i][j+2])){
						// Horizontal - from left to right
						return spotColor;
					}else if(i+2 < y && (spotColor == state.getGrid()[i+1][j] && spotColor == state.getGrid()[i+2][j])){
						// Vertical - downwards
						return spotColor;
					}else if(j+2 < x && i+2 < y && (spotColor == state.getGrid()[i+1][j+1] && spotColor == state.getGrid()[i+2][j+2])){
						// Diagonal - downwards - left to right
						return spotColor;
					}else if(j+2 < x && i-2 >= 0 && (spotColor == state.getGrid()[i-1][j+1] && spotColor == state.getGrid()[i-2][j+2])){
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
			depth1 = 4;
		}else if(difficulty1 == 3){
			depth1 = Integer.MAX_VALUE; // in tic tac toe the depth is not a problem
		}
		if(difficulty2 == 1){
			depth2 = 2;
		}else if(difficulty2 == 2){
			depth2 = 4;
		}else if(difficulty2 == 3){
			depth2 = Integer.MAX_VALUE; // in tic tac toe the depth is not a problem
		}
		IStateOBJ state = new StateOBJGrid(new int[boardSizeX][boardSizeY]);
		return state;
	}

	@Override
	public String getGameName(){ return "TicTacToe"; }
	
	@Override
	public int getGameID(){ return 1; }
	
	@Override
	public int evaluation(IStateOBJ state, int movingPlayer, int max, int min){
		// heuristic evaluation function
		// this map range is 3*3
		// 1 0 1
		// 0 2 0
		// 1 0 1
		int val = 0;
		for (int i = 0; i < state.getGrid().length; i++) {
			for (int j = 0; j < state.getGrid()[0].length; j++) {
				int c = 0;
				if(state.getGrid()[i][j] == max){c = 1;} // the analysed spot is yours
				else if(state.getGrid()[i][j] == min){c = -1;} // the analysed spot is the opponents
				if(c != 0){
					if((i == 0 || i == state.getGrid().length-1) && (j == 0 || j == state.getGrid()[0].length-1)){val += 5 * c;} // corners
					else if(i == (state.getGrid().length-1)/2 && j == (state.getGrid()[0].length-1)/2){val += 10 * c;} // middle
					else{val += 1 * c;}
				}

			}
		}
		return val; // return evaluated utility value
	}
}