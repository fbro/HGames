package gameLogicTypes;

import gameLogic.IStateOBJ;
import gameLogic.MiscTools;
import gameLogic.StateOBJGrid;

import java.awt.Point;
import java.util.ArrayList;

public class TypeChess implements IGameType {

	// TODO implement check
	// TODO implement checkmate
	// TODO implement Castling

	// pieces are ordered like this:
	// pawn   Blue 0  3
	// rook   Blue 1  4
	// knight Blue 2  5
	// bishop Blue 3  6
	// queen  Blue 4  7
	// king   Blue 5  8

	// pawn   Red  0 -3
	// rook   Red  1 -4
	// knight Red  2 -5
	// bishop Red  3 -6
	// queen  Red  4 -7
	// king   Red  5 -8

	private IStateOBJ board = null;
	private Point selPoint = null;
	private int depth1 = 0;
	private int depth2 = 0;
	private int difficulty1 = 0;
	private int difficulty2 = 0;
	private int numOfRounds = 0; // used in calcwinner to help end the game when stuck.

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
			for (int i = 0; i < moves.size(); i++) { // search through allowed moves to see if the move is allowed
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
				state.getGrid()[x][y] = state.getGrid()[selectedPoint.x][selectedPoint.y];
				state.getGrid()[selectedPoint.x][selectedPoint.y] = 0;
				// if it is a pawn that moved to the back, then turn it into a queen
				if(state.getGrid()[x][y] == 3 && y == 7){
					state.getGrid()[x][y] = 7;
				}else if(state.getGrid()[x][y] == -3 && y == 0){
					state.getGrid()[x][y] = -7;
				}
				return state;
			}
		}
	}

	@Override
	public ArrayList<Point> possibleMoves(IStateOBJ state, int movingPlayer, int otherPlayer, int depth, Point selectedPoint) {
		int[][] grid = state.getGrid();
		Point selFieldPoint = selectedPoint;
		int selFieldPiece = selFieldPoint!=null ? grid[selFieldPoint.x][selFieldPoint.y] : 0;
		boolean hasFieldSelected = selFieldPoint!=null ? true : false;

		ArrayList<Point> moves = new ArrayList<Point>();
		if(!hasFieldSelected){ // select
			for (int i = 0; i < grid.length; i++) {
				for (int j = 0; j < grid[0].length; j++) {
					if((grid[i][j] != 0) && ((grid[i][j] >= 3 && movingPlayer == 1) || (grid[i][j] <= -3 && movingPlayer == 2))){
						moves.add(new Point(i, j));
					}
				}
			}
		}else{ // move
			Point p = selFieldPoint;
			switch (selFieldPiece) {
			case 3: // pawn blue
				if(grid[0].length > p.y+1 && grid[p.x][p.y+1] == 0){moves.add(new Point(p.x, p.y+1));} // move down
				if(grid[0].length > p.y+1 && grid.length > p.x+1 && grid[p.x+1][p.y+1] <= -3){moves.add(new Point(p.x+1, p.y+1));} // attack diagonally
				if(grid[0].length > p.y+1 && p.x-1 >= 0 && grid[p.x-1][p.y+1] <= -3){moves.add(new Point(p.x-1, p.y+1));} // attack diagonally
				if(p.y == 1 && grid[p.x][p.y+1] == 0 && grid[p.x][p.y+2] == 0){moves.add(new Point(p.x, p.y+2));} // initial sprint
				break;
			case 4: // rook blue
				boolean b = false;
				for(int i=p.y-1;i>=0;i--){
					if(grid[p.x][i]>=3||b)break;
					if(grid[p.x][i]<=-3&&!b)b=true;else if(grid[p.x][i]<=-3&&b)break;
					moves.add(new Point(p.x, i));
				} // north
				b = false;
				for(int i=p.y+1;i<grid[0].length;i++){
					if(grid[p.x][i]>=3||b)break;
					if(grid[p.x][i]<=-3&&!b)b=true;else if(grid[p.x][i]<=-3&&b)break;
					moves.add(new Point(p.x, i));
				} // south
				b = false;
				for(int i=p.x+1;i<grid.length;i++){
					if(grid[i][p.y]>=3||b)break;
					if(grid[i][p.y]<=-3&&!b)b=true;else if(grid[i][p.y]<=-3&&b)break;
					moves.add(new Point(i, p.y));
				} // east
				b = false;
				for(int i=p.x-1;i>=0;i--){
					if(grid[i][p.y]>=3||b)break;
					if(grid[i][p.y]<=-3&&!b)b=true;else if(grid[i][p.y]<=-3&&b)break;
					moves.add(new Point(i, p.y));
				} // west
				break;
			case 5: // knight blue
				if(p.x+2<grid.length&&p.y+1<grid[0].length&&(grid[p.x+2][p.y+1]==0||grid[p.x+2][p.y+1]<=-3))moves.add(new Point(p.x+2, p.y+1));
				if(p.x+2<grid.length&&p.y-1>=0            &&(grid[p.x+2][p.y-1]==0||grid[p.x+2][p.y-1]<=-3))moves.add(new Point(p.x+2, p.y-1));
				if(p.x-2>=0         &&p.y+1<grid[0].length&&(grid[p.x-2][p.y+1]==0||grid[p.x-2][p.y+1]<=-3))moves.add(new Point(p.x-2, p.y+1));
				if(p.x-2>=0         &&p.y-1>=0            &&(grid[p.x-2][p.y-1]==0||grid[p.x-2][p.y-1]<=-3))moves.add(new Point(p.x-2, p.y-1));
				if(p.x-1>=0         &&p.y+2<grid[0].length&&(grid[p.x-1][p.y+2]==0||grid[p.x-1][p.y+2]<=-3))moves.add(new Point(p.x-1, p.y+2));
				if(p.x+1<grid.length&&p.y+2<grid[0].length&&(grid[p.x+1][p.y+2]==0||grid[p.x+1][p.y+2]<=-3))moves.add(new Point(p.x+1, p.y+2));
				if(p.x-1>=0         &&p.y-2>=0            &&(grid[p.x-1][p.y-2]==0||grid[p.x-1][p.y-2]<=-3))moves.add(new Point(p.x-1, p.y-2));
				if(p.x+1<grid.length&&p.y-2>=0            &&(grid[p.x+1][p.y-2]==0||grid[p.x+1][p.y-2]<=-3))moves.add(new Point(p.x+1, p.y-2));
				break;
			case 6: // bishop blue
				b = false;int e=1;
				while(p.x+e<grid.length&&p.y-e>=0){
					if(grid[p.x+e][p.y-e]>=3||b)break;
					if(grid[p.x+e][p.y-e]<=-3&&!b)b=true;else if(grid[p.x+e][p.y-e]<=-3&&b)break;
					moves.add(new Point(p.x+e, p.y-e));e++;
				} // north east
				b = false;e=1;
				while(p.x-e>=0&&p.y-e>=0){
					if(grid[p.x-e][p.y-e]>=3||b)break;
					if(grid[p.x-e][p.y-e]<=-3&&!b)b=true;else if(grid[p.x-e][p.y-e]<=-3&&b)break;
					moves.add(new Point(p.x-e, p.y-e));e++;
				} // north west
				b = false;e=1;
				while(p.x+e<grid.length&&p.y+e<grid[0].length){
					if(grid[p.x+e][p.y+e]>=3||b)break;
					if(grid[p.x+e][p.y+e]<=-3&&!b)b=true;else if(grid[p.x+e][p.y+e]<=-3&&b)break;
					moves.add(new Point(p.x+e, p.y+e));e++;
				} // south east
				b = false;e=1;
				while(p.x-e>=0&&p.y+e<grid[0].length){
					if(grid[p.x-e][p.y+e]>=3||b)break;
					if(grid[p.x-e][p.y+e]<=-3&&!b)b=true;else if(grid[p.x-e][p.y+e]<=-3&&b)break;
					moves.add(new Point(p.x-e, p.y+e));e++;
				} // south west
				break;
			case 7: // queen blue
				b = false;
				for(int i=p.y-1;i>=0;i--){
					if(grid[p.x][i]>=3||b)break;
					if(grid[p.x][i]<=-3&&!b)b=true;else if(grid[p.x][i]<=-3&&b)break;
					moves.add(new Point(p.x, i));
				} // north
				b = false;
				for(int i=p.y+1;i<grid[0].length;i++){
					if(grid[p.x][i]>=3||b)break;
					if(grid[p.x][i]<=-3&&!b)b=true;else if(grid[p.x][i]<=-3&&b)break;
					moves.add(new Point(p.x, i));
				} // south
				b = false;
				for(int i=p.x+1;i<grid.length;i++){
					if(grid[i][p.y]>=3||b)break;
					if(grid[i][p.y]<=-3&&!b)b=true;else if(grid[i][p.y]<=-3&&b)break;
					moves.add(new Point(i, p.y));
				} // east
				b = false;
				for(int i=p.x-1;i>=0;i--){
					if(grid[i][p.y]>=3||b)break;
					if(grid[i][p.y]<=-3&&!b)b=true;else if(grid[i][p.y]<=-3&&b)break;
					moves.add(new Point(i, p.y));
				} // west
				b = false;e=1;
				while(p.x+e<grid.length&&p.y-e>=0){
					if(grid[p.x+e][p.y-e]>=3||b)break;
					if(grid[p.x+e][p.y-e]<=-3&&!b)b=true;else if(grid[p.x+e][p.y-e]<=-3&&b)break;
					moves.add(new Point(p.x+e, p.y-e));e++;
				} // north east
				b = false;e=1;
				while(p.x-e>=0&&p.y-e>=0){
					if(grid[p.x-e][p.y-e]>=3||b)break;
					if(grid[p.x-e][p.y-e]<=-3&&!b)b=true;else if(grid[p.x-e][p.y-e]<=-3&&b)break;
					moves.add(new Point(p.x-e, p.y-e));e++;
				} // north west
				b = false;e=1;
				while(p.x+e<grid.length&&p.y+e<grid[0].length){
					if(grid[p.x+e][p.y+e]>=3||b)break;
					if(grid[p.x+e][p.y+e]<=-3&&!b)b=true;else if(grid[p.x+e][p.y+e]<=-3&&b)break;
					moves.add(new Point(p.x+e, p.y+e));e++;
				} // south east
				b = false;e=1;
				while(p.x-e>=0&&p.y+e<grid[0].length){
					if(grid[p.x-e][p.y+e]>=3||b)break;
					if(grid[p.x-e][p.y+e]<=-3&&!b)b=true;else if(grid[p.x-e][p.y+e]<=-3&&b)break;
					moves.add(new Point(p.x-e, p.y+e));e++;
				} // south west
				break;
			case 8: // king blue
				if(p.x+1<grid.length&&p.y+1<grid[0].length&&(grid[p.x+1][p.y+1]==0||grid[p.x+1][p.y+1]<=-3))moves.add(new Point(p.x+1, p.y+1));
				if(p.x-1>=0         &&p.y-1>=0            &&(grid[p.x-1][p.y-1]==0||grid[p.x-1][p.y-1]<=-3))moves.add(new Point(p.x-1, p.y-1));
				if(                   p.y+1<grid[0].length&&(grid[p.x  ][p.y+1]==0||grid[p.x  ][p.y+1]<=-3))moves.add(new Point(p.x  , p.y+1));
				if(                   p.y-1>=0            &&(grid[p.x  ][p.y-1]==0||grid[p.x  ][p.y-1]<=-3))moves.add(new Point(p.x  , p.y-1));
				if(p.x-1>=0                               &&(grid[p.x-1][p.y  ]==0||grid[p.x-1][p.y  ]<=-3))moves.add(new Point(p.x-1, p.y  ));
				if(p.x+1<grid.length                      &&(grid[p.x+1][p.y  ]==0||grid[p.x+1][p.y  ]<=-3))moves.add(new Point(p.x+1, p.y  ));
				if(p.x-1>=0         &&p.y+1<grid[0].length&&(grid[p.x-1][p.y+1]==0||grid[p.x-1][p.y+1]<=-3))moves.add(new Point(p.x-1, p.y+1));
				if(p.x+1<grid.length&&p.y-1>=0            &&(grid[p.x+1][p.y-1]==0||grid[p.x+1][p.y-1]<=-3))moves.add(new Point(p.x+1, p.y-1));
				break;
			case -3: // pawn red
				if(p.y-1 >= 0 && grid[p.x][p.y-1] == 0){moves.add(new Point(p.x, p.y-1));} // move down
				if(p.y-1 >= 0 && grid.length > p.x+1 && grid[p.x+1][p.y-1] >= 3){moves.add(new Point(p.x+1, p.y-1));} // attack diagonally
				if(p.y-1 >= 0 && p.x-1 >= 0 && grid[p.x-1][p.y-1] >= 3){moves.add(new Point(p.x-1, p.y-1));} // attack diagonally
				if(p.y == 6 && grid[p.x][p.y-1] == 0 && grid[p.x][p.y-2] == 0){moves.add(new Point(p.x, p.y-2));} // initial sprint
				break;
			case -4: // rook red
				b = false;
				for(int i=p.y-1;i>=0;i--){
					if(grid[p.x][i]<=-3||b)break;
					if(grid[p.x][i]>=3&&!b)b=true;else if(grid[p.x][i]>=3&&b)break;
					moves.add(new Point(p.x, i));
				} // north
				b = false;
				for(int i=p.y+1;i<grid[0].length;i++){
					if(grid[p.x][i]<=-3||b)break;
					if(grid[p.x][i]>=3&&!b)b=true;else if(grid[p.x][i]>=3&&b)break;
					moves.add(new Point(p.x, i));
				} // south
				b = false;
				for(int i=p.x+1;i<grid.length;i++){
					if(grid[i][p.y]<=-3||b)break;
					if(grid[i][p.y]>=3&&!b)b=true;else if(grid[i][p.y]>=3&&b)break;
					moves.add(new Point(i, p.y));
				} // east
				b = false;
				for(int i=p.x-1;i>=0;i--){
					if(grid[i][p.y]<=-3||b)break;
					if(grid[i][p.y]>=3&&!b)b=true;else if(grid[i][p.y]>=3&&b)break;
					moves.add(new Point(i, p.y));
				} // west
				break;
			case -5: // knight red
				if(p.x+2<grid.length&&p.y+1<grid[0].length&&(grid[p.x+2][p.y+1]==0||grid[p.x+2][p.y+1]>=3))moves.add(new Point(p.x+2, p.y+1));
				if(p.x+2<grid.length&&p.y-1>=0            &&(grid[p.x+2][p.y-1]==0||grid[p.x+2][p.y-1]>=3))moves.add(new Point(p.x+2, p.y-1));
				if(p.x-2>=0         &&p.y+1<grid[0].length&&(grid[p.x-2][p.y+1]==0||grid[p.x-2][p.y+1]>=3))moves.add(new Point(p.x-2, p.y+1));
				if(p.x-2>=0         &&p.y-1>=0            &&(grid[p.x-2][p.y-1]==0||grid[p.x-2][p.y-1]>=3))moves.add(new Point(p.x-2, p.y-1));
				if(p.x-1>=0         &&p.y+2<grid[0].length&&(grid[p.x-1][p.y+2]==0||grid[p.x-1][p.y+2]>=3))moves.add(new Point(p.x-1, p.y+2));
				if(p.x+1<grid.length&&p.y+2<grid[0].length&&(grid[p.x+1][p.y+2]==0||grid[p.x+1][p.y+2]>=3))moves.add(new Point(p.x+1, p.y+2));
				if(p.x-1>=0         &&p.y-2>=0            &&(grid[p.x-1][p.y-2]==0||grid[p.x-1][p.y-2]>=3))moves.add(new Point(p.x-1, p.y-2));
				if(p.x+1<grid.length&&p.y-2>=0            &&(grid[p.x+1][p.y-2]==0||grid[p.x+1][p.y-2]>=3))moves.add(new Point(p.x+1, p.y-2));
				break;
			case -6: // bishop red
				b = false;e=1;
				while(p.x+e<grid.length&&p.y-e>=0){
					if(grid[p.x+e][p.y-e]<=-3||b)break;
					if(grid[p.x+e][p.y-e]>=3&&!b)b=true;else if(grid[p.x+e][p.y-e]>=3&&b)break;
					moves.add(new Point(p.x+e, p.y-e));e++;
				} // north east
				b = false;e=1;
				while(p.x-e>=0&&p.y-e>=0){
					if(grid[p.x-e][p.y-e]<=-3||b)break;
					if(grid[p.x-e][p.y-e]>=3&&!b)b=true;else if(grid[p.x-e][p.y-e]>=3&&b)break;
					moves.add(new Point(p.x-e, p.y-e));e++;
				} // north west
				b = false;e=1;
				while(p.x+e<grid.length&&p.y+e<grid[0].length){
					if(grid[p.x+e][p.y+e]<=-3||b)break;
					if(grid[p.x+e][p.y+e]>=3&&!b)b=true;else if(grid[p.x+e][p.y+e]>=3&&b)break;
					moves.add(new Point(p.x+e, p.y+e));e++;
				} // south east
				b = false;e=1;
				while(p.x-e>=0&&p.y+e<grid[0].length){
					if(grid[p.x-e][p.y+e]<=-3||b)break;
					if(grid[p.x-e][p.y+e]>=3&&!b)b=true;else if(grid[p.x-e][p.y+e]>=3&&b)break;
					moves.add(new Point(p.x-e, p.y+e));e++;
				} // south west
				break;
			case -7: // queen red
				b = false;
				for(int i=p.y-1;i>=0;i--){
					if(grid[p.x][i]<=-3||b)break;
					if(grid[p.x][i]>=3&&!b)b=true;else if(grid[p.x][i]>=3&&b)break;
					moves.add(new Point(p.x, i));
				} // north
				b = false;
				for(int i=p.y+1;i<grid[0].length;i++){
					if(grid[p.x][i]<=-3||b)break;
					if(grid[p.x][i]>=3&&!b)b=true;else if(grid[p.x][i]>=3&&b)break;
					moves.add(new Point(p.x, i));
				} // south
				b = false;
				for(int i=p.x+1;i<grid.length;i++){
					if(grid[i][p.y]<=-3||b)break;
					if(grid[i][p.y]>=3&&!b)b=true;else if(grid[i][p.y]>=3&&b)break;
					moves.add(new Point(i, p.y));
				} // east
				b = false;
				for(int i=p.x-1;i>=0;i--){
					if(grid[i][p.y]<=-3||b)break;
					if(grid[i][p.y]>=3&&!b)b=true;else if(grid[i][p.y]>=3&&b)break;
					moves.add(new Point(i, p.y));
				} // west
				b = false;e=1;
				while(p.x+e<grid.length&&p.y-e>=0){
					if(grid[p.x+e][p.y-e]<=-3||b)break;
					if(grid[p.x+e][p.y-e]>=3&&!b)b=true;else if(grid[p.x+e][p.y-e]>=3&&b)break;
					moves.add(new Point(p.x+e, p.y-e));e++;
				} // north east
				b = false;e=1;
				while(p.x-e>=0&&p.y-e>=0){
					if(grid[p.x-e][p.y-e]<=-3||b)break;
					if(grid[p.x-e][p.y-e]>=3&&!b)b=true;else if(grid[p.x-e][p.y-e]>=3&&b)break;
					moves.add(new Point(p.x-e, p.y-e));e++;
				} // north west
				b = false;e=1;
				while(p.x+e<grid.length&&p.y+e<grid[0].length){
					if(grid[p.x+e][p.y+e]<=-3||b)break;
					if(grid[p.x+e][p.y+e]>=3&&!b)b=true;else if(grid[p.x+e][p.y+e]>=3&&b)break;
					moves.add(new Point(p.x+e, p.y+e));e++;
				} // south east
				b = false;e=1;
				while(p.x-e>=0&&p.y+e<grid[0].length){
					if(grid[p.x-e][p.y+e]<=-3||b)break;
					if(grid[p.x-e][p.y+e]>=3&&!b)b=true;else if(grid[p.x-e][p.y+e]>=3&&b)break;
					moves.add(new Point(p.x-e, p.y+e));e++;
				} // south west
				break;
			case -8: // king red
				if(p.x+1<grid.length&&p.y+1<grid[0].length&&(grid[p.x+1][p.y+1]==0||grid[p.x+1][p.y+1]>=3))moves.add(new Point(p.x+1, p.y+1));
				if(p.x-1>=0         &&p.y-1>=0            &&(grid[p.x-1][p.y-1]==0||grid[p.x-1][p.y-1]>=3))moves.add(new Point(p.x-1, p.y-1));
				if(                   p.y+1<grid[0].length&&(grid[p.x  ][p.y+1]==0||grid[p.x  ][p.y+1]>=3))moves.add(new Point(p.x  , p.y+1));
				if(                   p.y-1>=0            &&(grid[p.x  ][p.y-1]==0||grid[p.x  ][p.y-1]>=3))moves.add(new Point(p.x  , p.y-1));
				if(p.x-1>=0                               &&(grid[p.x-1][p.y  ]==0||grid[p.x-1][p.y  ]>=3))moves.add(new Point(p.x-1, p.y  ));
				if(p.x+1<grid.length                      &&(grid[p.x+1][p.y  ]==0||grid[p.x+1][p.y  ]>=3))moves.add(new Point(p.x+1, p.y  ));
				if(p.x-1>=0         &&p.y+1<grid[0].length&&(grid[p.x-1][p.y+1]==0||grid[p.x-1][p.y+1]>=3))moves.add(new Point(p.x-1, p.y+1));
				if(p.x+1<grid.length&&p.y-1>=0            &&(grid[p.x+1][p.y-1]==0||grid[p.x+1][p.y-1]>=3))moves.add(new Point(p.x+1, p.y-1));
			default:
				break; // error - this should be impossible to reach
			}
		}
		return moves;
	}

	@Override
	public int calcWinner(IStateOBJ state, int movingPlayer, int otherPlayer) {
		if(numOfRounds > 80){
			return -1; // draw if max number of rounds have been reached
		}
		// if a player is missing a king then he looses
		boolean foundP1King = false;
		boolean foundP2King = false;
		boolean foundNoneOther  = false;
		//		System.out.println(MiscTools.debugPrintBoard(grid));
		for (int i = 0; i < state.getGrid().length; i++) {
			for (int j = 0; j < state.getGrid()[0].length; j++) {
				if(!foundNoneOther && ((state.getGrid()[i][j] <= -4 && state.getGrid()[i][j] >= -7) || (state.getGrid()[i][j] >= 4 && state.getGrid()[i][j] <= 7))){
					foundNoneOther = true;
				}else if(state.getGrid()[i][j] == 8){
					foundP1King = true;
					if(foundP1King && foundP2King && foundNoneOther){return 0;}
				}else if(state.getGrid()[i][j] == -8){
					foundP2King = true;
					if(foundP1King && foundP2King && foundNoneOther){return 0;}
				}
			}
		}
		if(foundP1King && !foundP2King){
			return 1; // player one won
		}else if(!foundP1King && foundP2King){
			return 2; // player two won
		}else if(!foundNoneOther){
			return -1; // draw
		}else if(foundP1King && foundP2King && foundNoneOther){
			return 0; // game is still on
		}
		return -1; // draw
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
		int[][] grid = new int[boardSizeX][boardSizeY];
		// 4 5 6 8 7 6 5 4 |1 2 3 5 4 3 2 1 blue/white
		// 3 3 3 3 3 3 3 3 |0 0 0 0 0 0 0 0
		// 0 0 0 0 0 0 0 0 |x x x x x x x x
		// 0 0 0 0 0 0 0 0 |x x x x x x x x
		// 0 0 0 0 0 0 0 0 |x x x x x x x x
		// 0 0 0 0 0 0 0 0 |x x x x x x x x
		//-3-3-3-3-3-3-3-3 |0 0 0 0 0 0 0 0
		//-4-5-6-8-7-6-5-4 |1 2 3 5 4 3 2 1 red/black
		grid[0][0] = 4; grid[1][0] = 5; grid[2][0] = 6; grid[3][0] = 8; grid[4][0] = 7; grid[5][0] = 6; grid[6][0] = 5; grid[7][0] = 4;
		grid[0][1] = 3; grid[1][1] = 3; grid[2][1] = 3; grid[3][1] = 3; grid[4][1] = 3; grid[5][1] = 3; grid[6][1] = 3; grid[7][1] = 3;
		grid[0][6] =-3; grid[1][6] =-3; grid[2][6] =-3; grid[3][6] =-3; grid[4][6] =-3; grid[5][6] =-3; grid[6][6] =-3; grid[7][6] =-3;
		grid[0][7] =-4; grid[1][7] =-5; grid[2][7] =-6; grid[3][7] =-8; grid[4][7] =-7; grid[5][7] =-6; grid[6][7] =-5; grid[7][7] =-4;
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
	public String getStats(){
		if(board == null){
			return "Game not yet started.";
		}else{
			int p1Pawn = 0, p1Rook = 0, p1Knight = 0, p1Bishop = 0, p1Queen = 0, p1King = 0, 
					p2Pawn = 0, p2Rook = 0, p2Knight = 0, p2Bishop = 0, p2Queen = 0, p2King = 0;
			for (int i = 0; i < board.getGrid().length; i++) {
				for (int j = 0; j < board.getGrid()[0].length; j++) {
					if(board.getGrid()[i][j] == 3){
						p1Pawn++;
					}else if(board.getGrid()[i][j] == 4){
						p1Rook++;
					}else if(board.getGrid()[i][j] == 5){
						p1Knight++;
					}else if(board.getGrid()[i][j] == 6){
						p1Bishop++;
					}else if(board.getGrid()[i][j] == 7){
						p1Queen++;
					}else if(board.getGrid()[i][j] == 8){
						p1King++;
					}
					else if(board.getGrid()[i][j] == -3){
						p2Pawn++;
					}else if(board.getGrid()[i][j] == -4){
						p2Rook++;
					}else if(board.getGrid()[i][j] == -5){
						p2Knight++;
					}else if(board.getGrid()[i][j] == -6){
						p2Bishop++;
					}else if(board.getGrid()[i][j] == -7){
						p2Queen++;
					}else if(board.getGrid()[i][j] == -8){
						p2King++;
					}
				}
			}
			String msg =  ""
					+ "Pawns.   p1["+p1Pawn+"] p2["+p2Pawn+"]\n"
					+ "Rooks.   p1["+p1Rook+"] p2["+p2Rook+"]\n"
					+ "Knights. p1["+p1Knight+"] p2["+p2Knight+"]\n"
					+ "Bishops. p1["+p1Bishop+"] p2["+p2Bishop+"]\n"
					+ "Queens.  p1["+p1Queen+"] p2["+p2Queen+"]\n"
					+ "Kings.   p1["+p1King+"] p2["+p2King+"]\n"
					+ "Number of rounds left [" + (80-numOfRounds) + "]\n";
			if(selPoint != null){
				msg += "Selected Point [" + selPoint.x + "][" + selPoint.y + "]\n";
			}
			return msg;
		}
	}

	@Override
	public String getGameName(){ return "Chess"; }
	
	@Override
	public int getGameID(){ return 5; }
	
	@Override
	public IStateOBJ processMovePlayer(Point p, IStateOBJ state, int movingPlayer, int otherPlayer) {
		ArrayList<Point> moves = possibleMoves(state, movingPlayer, otherPlayer, -1, selPoint);
		boolean foundMove = false;
		for(Point point: moves){
			if(point.x == p.x && point.y == p.y){
				foundMove = true;
				break;
			}
		}
		if(!foundMove){
			if((state.getGrid()[p.x][p.y] >= 3 && movingPlayer == 1) || (state.getGrid()[p.x][p.y] <= -3 && movingPlayer == 2)){
				if(selPoint != null && selPoint.x == p.x && selPoint.y == p.y){
					System.out.println("selected same piece - deselecting");
					selPoint = null;
					return null;
				}else{
					System.out.println("selected another viable piece");
					selPoint = p;
					return null;
				}
			}else{
				System.out.println("illegal spot pressed");
				selPoint = null;
				return null;
			}
		}
		IStateOBJ s = processMove(p.x, p.y, state, movingPlayer, otherPlayer, selPoint);
		if(s==null){
			selPoint = p;
		}else if(selPoint != null){
			selPoint = null;
		}else{
			selPoint = p;
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
			for (int j = 0; j < moves.size(); j++) {
				stateMoves.add(processMove(moves.get(j).x, moves.get(j).y, new StateOBJGrid(MiscTools.clone2DIntArray(state.getGrid())), movingPlayer, otherPlayer, selFieldPoint));
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
				if(state.getGrid()[i][j] == 3){ // pawn
					p1Val += 1;
				}else if(state.getGrid()[i][j] == 4){ // rook
					p1Val += 15;
				}else if(state.getGrid()[i][j] == 5){ // knight
					p1Val += 10;
				}else if(state.getGrid()[i][j] == 6){ // bishop
					p1Val += 10;
				}else if(state.getGrid()[i][j] == 7){ // queen
					p1Val += 50;
				}else if(state.getGrid()[i][j] == 8){ // king
					p1Val += 100000;
				}else if(state.getGrid()[i][j] ==-3){ // pawn
					p2Val += 1;
				}else if(state.getGrid()[i][j] ==-4){ // rook
					p2Val += 15;
				}else if(state.getGrid()[i][j] ==-5){ // knight
					p2Val += 10;
				}else if(state.getGrid()[i][j] ==-6){ // bishop
					p2Val += 10;
				}else if(state.getGrid()[i][j] ==-7){ // queen
					p2Val += 50;
				}else if(state.getGrid()[i][j] ==-8){ // king
					p2Val += 100000;
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

	@SuppressWarnings("unused")
	private ArrayList<Point> ChessCheck(IStateOBJ state, int movingPlayer, int otherPlayer, Point selFieldPoint){
		// checks for a check in chess - returns a set of moves that are allowed to stop the check
		// if there is no allowed moves then checkmate - return empty list
		ArrayList<Point> p = new ArrayList<Point>();
		if(selPoint != null){ // moving player has selected - what available moves?
			p = ChessCheckSelected(state, movingPlayer, otherPlayer, selFieldPoint);
		}else{
			ArrayList<Point> points = possibleMoves(state, movingPlayer, otherPlayer, -1, selFieldPoint);
			for (int i = 0; i < points.size(); i++) { // moving selects
				selFieldPoint = points.get(i);
				ArrayList<Point> pp = ChessCheckSelected(state, movingPlayer, otherPlayer, selFieldPoint);
				if(!pp.isEmpty()){
					p.add(points.get(i));
				}
			}
		}
		return p;
	}

	private ArrayList<Point> ChessCheckSelected(IStateOBJ state, int movingPlayer, int otherPlayer, Point selFieldPoint){
		boolean fcInMove = false;
		ArrayList<Point> p = possibleMoves(state, movingPlayer, otherPlayer, -1, selFieldPoint);
		ArrayList<IStateOBJ> grids = new ArrayList<IStateOBJ>();
		for (int j = 0; j < p.size(); j++) { // moving moves
			IStateOBJ gg = processMove(p.get(j).x, p.get(j).y, new StateOBJGrid(MiscTools.clone2DIntArray(state.getGrid())), movingPlayer, otherPlayer, selFieldPoint);
			grids.add(gg);

			Point sfp = null;
			ArrayList<Point> ps = possibleMoves(gg, otherPlayer, movingPlayer, -1, sfp);
			for (int k = 0; k < ps.size(); k++) { // opponent selects
				sfp = ps.get(k);
				ArrayList<Point> pp = possibleMoves(gg, otherPlayer, movingPlayer, -1, sfp);
				ArrayList<IStateOBJ> gs = new ArrayList<IStateOBJ>();
				for (int e = 0; e < pp.size(); e++) { // opponent moves
					IStateOBJ g = processMove(pp.get(e).x, pp.get(e).y, new StateOBJGrid(MiscTools.clone2DIntArray(gg.getGrid())), otherPlayer, movingPlayer, sfp);
					gs.add(g);

					int result = calcWinner(g, movingPlayer, otherPlayer);
					if(result == otherPlayer){
						// check - not allowed move
						fcInMove = true;
						p.remove(j);
						break;
					}
				}
				if(fcInMove){
					break;
				}
			}
			fcInMove = false;
		}
		return p;
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