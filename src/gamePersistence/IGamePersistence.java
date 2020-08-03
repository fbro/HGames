package gamePersistence;

import java.util.concurrent.ConcurrentHashMap;

// TODO all communication to persistence is synchronized - it should be async. maybe it should be handled in gui?

public interface IGamePersistence {
	
	// overall connection to the database and server
	public boolean connectToDatabase();

	// name against name table - this is used to keep track of two players competing against each other with wins, looses, and draws
	
	// primarily for testing - prints out the entire statFile.txt file
	public String getAllStat();
	
	// adds a new blank name against name row
	public boolean addNewStat(String name1, String name2);
	
	// gets the name against name row stats
	public int[] getStat(String name1, String name2);
	
	// sets the name against name row stats by looking after the corresponding names
	public boolean setStat(String name1, String name2, int stat1, int stat2, int stat3, int stat4);
	
	// clears the name against name table of all content 
	public boolean resetStat();
	
	
	// overall results for a single individual game played
	
	// save the results when the game is over
	public boolean recordGamePlayed(int GameType, String P1Name, String P2Name, int P1Difficulty, int P2Difficulty, 
			boolean isP1Computer, boolean isP2Computer, int P1Algo, int P2Algo, int Winner, int GameGridX, int GameGridY);
	
	// save the turn by turn results
	public boolean recordGameTurn(int[][] grid, int turn, boolean isPlayer1turn, boolean isSelecting);
	
	// loads the selected gameType but only when it havent been loaded before
	public ConcurrentHashMap<String, Integer> getLearnedGame(int gameType);
}
