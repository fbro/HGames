package gameLogic;

import gamePersistence.IGamePersistence;
import gamePersistence.PersistenceLocalTxtFile;
import gamePersistence.PersistenceMySQLLocal;
import gamePersistence.PersistenceSQLAzure;

public class MiscTools {
	public static final String[] GAME_NAMES = {"TicTacToe", "FourInLine", "ConnectFour", "Reversi", "Chess", "Checkers", "Mills", "Go"};
	public static final String[] ALGORITHM_NAMES = {"MiniMax", "Random Pick", "Eval. only", "MiniMax No Eval.", "Reinforcement Learning", "R. L. M. M."};
	
	// the persistence object - connection to something that is persistent
	public static IGamePersistence persistence = null;

	// static general tools that are used throughout the program

	public static String debugPrintBoard(IStateOBJ state){
		// used in the toString and is good for testing
		String s = "";
		for (int i = 0; i < state.getGrid()[0].length; i++) {
			s += "\n";
			for (int j = 0; j < state.getGrid().length; j++) {
				if(state.getGrid()[j][i] >= 0 && state.getGrid()[j][i] < 10){
					s += " ";
				}
				s += " " + state.getGrid()[j][i] + " ";
			}
			s += "y" + i;
		}
		s += "\n";
		for (int e = 0; e < state.getGrid().length; e++) {
			s += " x" + e + " ";
		}
		return s;
	}

	public static int[][] clone2DIntArray(int[][] oldGrid){
		int[][] newGrid = new int[oldGrid.length][oldGrid[0].length];
		for(int i=0; i<oldGrid.length; i++){
			for(int j=0; j<oldGrid[0].length; j++){
				newGrid[i][j]=oldGrid[i][j];
			}
		}return newGrid;
	}

	public static boolean[][] clone2DBooleanArray(boolean[][] oldGrid){
		boolean[][] newGrid = new boolean[oldGrid.length][oldGrid[0].length];
		for(int i=0; i<oldGrid.length; i++){
			for(int j=0; j<oldGrid[0].length; j++){
				newGrid[i][j]=oldGrid[i][j];
			}
		}return newGrid;
	}
	
	public static String gridStringBuilder(int[][] grid){
		String gridString = ""; // build the grid string as small as possible
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				gridString += grid[i][j] + " ";
			}
			gridString += "r";
		}
		return gridString;
	}

	
	// logic concerning the persistence that holds information about older matches and other statistics
	public static boolean isConnectedToLocalTxtFile = false; // if true use local txt file is available
	public static boolean isConnectedToMySQL = false; // if true MySQL is available
	public static boolean isConnectedToSQLAzure = false; // if true SQLAzure is available

	// called from controller.start()
	public static boolean connectToADatabase() {
//		persistence = new PersistenceLocalTxtFile(); // TODO
//		boolean isConnected = persistence.connectToDatabase();
//		return isConnected;
		
		// first try to connect to azure in the cloud
		persistence = new PersistenceSQLAzure();
		boolean isConnected = persistence.connectToDatabase();
		
		if(!isConnected){ // then if that fails try to connect to local MySQL database
			System.out.println("Azure connection failed - trying to connect to MySQL");
			persistence = new PersistenceMySQLLocal();
			isConnected = persistence.connectToDatabase();
			if(!isConnected){ // if that fails then try to setup a .txt file at local file location
				System.out.println("MySQL connection failed - trying to create local .txt file");
				persistence = new PersistenceLocalTxtFile();
				isConnected = persistence.connectToDatabase();
				if(!isConnected){
					System.out.println("local txt file failed");
				}else{System.out.println("local txt file succeeded");}
			}else{System.out.println("MySQL connection succeeded");}
		}else{System.out.println("SQL Azure connection succeeded");}
		persistence = new PersistenceLocalTxtFile();
		return isConnected;
	}
}