package gameLogicTypes;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Stack;

import gameLogic.IStateOBJ;
import gameLogic.MiscTools;
import gameLogic.StateOBJGrid;

public class TypeGo extends TypeAbClassic implements IGameType {
	// TODO - many ways to count points when game over. - not that important
	// TODO handicap
	
	// Rule 1 (the rule of liberty) states that every stone remaining on the board must have at least one open "point" 
	// (an intersection, called a "liberty") directly next to it (up, down, left, or right), or must be part of a connected 
	// group that has at least one such open point ("liberty") next to it. Stones or groups of stones which lose their last
	// liberty are removed from the board.

	// Rule 2 (the "ko rule") states that the stones on the board must never repeat a previous position of stones. Moves which 
	// would do so are forbidden, and thus only moves elsewhere on the board are permitted that turn.

	// grid spots
	//  0 = empty - room for liberty
	// -1 = spot that is not used in the game
	//  1 = player 1 stone (blue)
	//  2 = player 2 stone (red)
	// -4 = ko rule - only player 2 may place on this spot
	//  4 = ko rule - only player 1 may place on this spot
	//  5 = pass button
	// -2 and -3 are used as temporary tagged spots for when determining groups in the findLiberty method and reversed in the processMove

	@Override
	public IStateOBJ processMove(int x, int y, IStateOBJ state, int movingPlayer, int otherPlayer, Point selectedPoint) {
		int[][] g = state.getGrid();
		if(g[x][y] == 5){ // pass button have been pressed
			int s = movingPlayer==1?4:-4;
			if(g[g.length-1][0] == -1){
				for (int i = 0; i < g[0].length/2; i++) {
					g[g.length-1][i] = s; // note who passed
				}
			}else{
				for (int i = g[0].length/2; i < g[0].length; i++) {
					g[g.length-1][i] = s; // note who passed
				}
			}
			
			g[g.length-1][(g[0].length-1)/2] = 5; // the pass button
			return state; // note the pass and return
		}

		// place the stone
		g[x][y] = movingPlayer;

		// first remove previous ko rules
		for (int i = 0; i < g.length-1; i++) {
			for (int j = 0; j < g[0].length; j++) {
				if(g[i][j] == -4 || g[i][j] == 4) {
					g[i][j] = 0;
				}
			}
		}
		if(g[g.length-1][0] != -1){ // only reset pass column to -1 spots if needed
			for (int i = 0; i < g[0].length; i++) { // reset the pass column back to -1 spots
				if(g[g.length-1][i] == -4 || g[g.length-1][i] == 4) {
					g[g.length-1][i] = -1;
				}
			}
		}
		int lookAtGroup = otherPlayer;
		while (true) { // iterate two times, one for each player
			for (int i = 0; i < g.length-1; i++) {
				for (int j = 0; j < g[0].length; j++) {
					if(g[i][j] == lookAtGroup){
						findLiberty(g, i, j);
					}
				}
			}
			if (lookAtGroup == movingPlayer){break;} // movingPlayer have already run. break out of the while loop now
			else{lookAtGroup = movingPlayer;} // movingPlayer is always last when calculating groups and liberties!
		}

		// reverse all negative tags from the findLiberty method to original stone values
		for (int i = 0; i < g.length-1; i++) {
			for (int j = 0; j < g[0].length; j++) {
				if(g[i][j] == -2){
					g[i][j] = 2;
				}else if(g[i][j] == -3){
					g[i][j] = 1;
				}
			}
		}
		return state;
	}

	@Override
	public ArrayList<Point> possibleMoves(IStateOBJ state, int movingPlayer, int otherPlayer, int depth, Point selectedPoint) {
		// This method gives the algorithm a list of moves that are available
		ArrayList<Point> moves = new ArrayList<Point>();
		for (int i = 0; i < state.getGrid().length-1; i++){
			for (int j = 0; j < state.getGrid()[0].length; j++){ // loop through all the childs
				if(state.getGrid()[i][j] == 0 || (movingPlayer == 1 && state.getGrid()[i][j] == 4) || (movingPlayer == 2 && state.getGrid()[i][j] == -4)){
					if(!isMoveSuicide(state.getGrid(), i, j, movingPlayer, otherPlayer)){
						moves.add(new Point(i, j)); // add all possible moves to this ArrayList and then later empty the list
					}
				}
			}
		}
		
		moves.add(new Point(state.getGrid().length-1, (state.getGrid()[0].length-1)/2)); // add pass move because one can always pass

		return moves;
	}

	public int calcWinner(IStateOBJ state, int movingPlayer, int otherPlayer) {
		int[][] g = state.getGrid();
		if((g[g.length-1][0] == -4 || g[g.length-1][0] == 4) && (g[g.length-1][g[0].length-1] == -4 || g[g.length-1][g[0].length-1] == 4) 
				|| possibleMoves(state, otherPlayer, movingPlayer, -1, null).isEmpty()){
			int p1Coins = 0;
			int p2Coins = 0;
			for (int i = 0; i < g.length-1; i++){ // count coins
				for (int j = 0; j < g[0].length; j++){
					if(g[i][j] == 1){p1Coins++;}
					if(g[i][j] == 2){p2Coins++;}
				}
			}
			if(p1Coins == p2Coins){
				return -1; // tie
			}if(p1Coins > p2Coins){
				return 1;
			}else{
				return 2;
			}
		}
		return 0; // game still on
	}

	@Override
	public IStateOBJ setupNewMatch(int boardSizeX, int boardSizeY, int difficulty1, int difficulty2) {
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
		IStateOBJ stateOBJ = new StateOBJGrid(new int[boardSizeX+1][boardSizeY]); // +1 on the x axis to make room for the pass feature
		for (int i = 0; i < boardSizeY; i++) {
			stateOBJ.getGrid()[boardSizeX][i] = -1; // spot is not used in the game
		}
		stateOBJ.getGrid()[boardSizeX][(boardSizeY-1)/2] = 5; // the pass button
		return stateOBJ;
	}

	@Override
	public String getGameName(){ return "Go"; }
	
	@Override
	public int getGameID(){ return 8; }
	
	@Override
	public IStateOBJ processMovePlayer(Point p, IStateOBJ state, int movingPlayer, int otherPlayer) {
		ArrayList<Point> allowedMoves = possibleMoves(state, movingPlayer, otherPlayer, -1, null);
		for (int i = 0; i < allowedMoves.size(); i++) {
			if(allowedMoves.get(i).x == p.x && allowedMoves.get(i).y == p.y){
				return processMove(p.x, p.y, state, movingPlayer, otherPlayer, null);
			}
		}
		System.out.println("error - illegal spot pressed");
		return null;
	}

	@Override
	public int evaluation(IStateOBJ state, int movingPlayer, int max, int min) {
		int p = 0;
		for (int i = 0; i < state.getGrid().length-1; i++){
			for (int j = 0; j < state.getGrid()[0].length; j++){
				if(state.getGrid()[i][j] == 0){
					// do nothing
			    }else if(state.getGrid()[i][j] == movingPlayer){
					p += 2;
				}else if(state.getGrid()[i][j] == (movingPlayer==1?2:1)){
					p -= 2;
				}else if(movingPlayer == 1 && state.getGrid()[i][j] ==  4){
					p += 5;
				}else if(movingPlayer == 2 && state.getGrid()[i][j] == -4){
					p += 5;
				}else if(movingPlayer == 2 && state.getGrid()[i][j] ==  4){
					p -= 5;
				}else if(movingPlayer == 1 && state.getGrid()[i][j] == -4){
					p -= 5;
				}
			}
		}
		return p;
	}

	private boolean isMoveSuicide(int[][] g, int x, int y, int movingPlayer, int otherPlayer){
		// if movingPlayer places a stone that results in the movingPlayer to loose the group - then it is a suicide and is not allowed
		int[][] gg = MiscTools.clone2DIntArray(g);
		gg[x][y] = movingPlayer;

		int lookAtGroup = otherPlayer;
		while (true) { // iterate two times, one for each player
			for (int i = 0; i < gg.length-1; i++) {
				for (int j = 0; j < gg[0].length; j++) {
					if(gg[i][j] == lookAtGroup){
						findLiberty(gg, i, j);
					}
				}
			}
			if (lookAtGroup == movingPlayer){break;} // movingPlayer have already run. break out of the while loop now
			else{lookAtGroup = movingPlayer;} // movingPlayer is always last when calculating groups and liberties!
		}

		if(gg[x][y] == -4 || gg[x][y] == 4 || gg[x][y] == 0){ // the group have been removed - it was a suicide...
			return true;
		}else{			
			return false;
		}
	}

	private void findLiberty(int[][] g, int x, int y){ // a single stone - find its group - determine if the group has any liberties or not and if there are no liberties then remove the stones and set the Ko rule inside
		Stack<Point> s = new Stack<Point>(); // new stones that are in the active group
		Stack<Point> ss = new Stack<Point>(); // old stones that are saved for later handling
		s.push(new Point(x, y)); // put the initial stone into the stack
		final int spot = g[x][y];
		boolean foundLiberty = false;
		g[x][y]-=4; // spot 1 is converted to -3 and spot 2 is converted to -2. These two values are used to tag the spot as searched
		// look after all the stones in the group
		while (true) { // loop until the whole group is detected
			Point p = s.pop();
			ss.push(p); // keep record over all old stones
			x = p.x; y = p.y;
			if(g.length-1 != x    && (g[x+1][y]==0||g[x+1][y]==-4||g[x+1][y]==4)){foundLiberty = true;} else if(g.length-1 != x    && g[x+1][y] == spot){g[x+1][y]-=4;s.push(new Point(x+1, y));}
			if(g[0].length-1 != y && (g[x][y+1]==0||g[x][y+1]==-4||g[x][y+1]==4)){foundLiberty = true;} else if(g[0].length-1 != y && g[x][y+1] == spot){g[x][y+1]-=4;s.push(new Point(x, y+1));}
			if(0 != x             && (g[x-1][y]==0||g[x-1][y]==-4||g[x-1][y]==4)){foundLiberty = true;} else if(0 != x             && g[x-1][y] == spot){g[x-1][y]-=4;s.push(new Point(x-1, y));}
			if(0 != y             && (g[x][y-1]==0||g[x][y-1]==-4||g[x][y-1]==4)){foundLiberty = true;} else if(0 != y             && g[x][y-1] == spot){g[x][y-1]-=4;s.push(new Point(x, y-1));}

			if(s.isEmpty()){ // group is now completely fleshed out
				if(foundLiberty){ // if there was a single liberty found then break
					break;
				}
				// there where no liberties found in this group. remove all the stones and set ko rule if there is only one stone removed on the spot
				int koSpot = (spot==1)?-4:4;
				Point koP;
				if(ss.size() > 1){koSpot = 0;} // only remove the stones, do not set the ko rule because there is more than one stone removed
				while(!ss.isEmpty()){
					koP = ss.pop();
					g[koP.x][koP.y] = koSpot;
				}
				break; // all stones have been changed to ko spots
			}
		}
	}

}