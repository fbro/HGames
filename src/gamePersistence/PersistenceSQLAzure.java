package gamePersistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import gameLogic.MiscTools;

public class PersistenceSQLAzure implements IGamePersistence {

	private ArrayList<GameTurn> gameTurnLocalList = new ArrayList<GameTurn>();
	@SuppressWarnings("unchecked")
	private ConcurrentHashMap<String, Integer>[] learnedGames = new ConcurrentHashMap[MiscTools.GAME_NAMES.length];

	// SQL Azure methods

	// connection string JDBC
	// jdbc:sqlserver://hulmir.database.windows.net:1433;
	// database=Hulmir;
	// user=hulmir@hulmir;
	// password=.....;
	// encrypt=true;
	// trustServerCertificate=false;
	// hostNameInCertificate=*.database.windows.net;
	// loginTimeout=30;

	// Declare the JDBC objects.
	private Connection connectionSQLAzure = null;

	@Override
	public boolean connectToDatabase(){
		// Connect to your database.
		// Replace server name, username, and password with your credentials
		String connectionString = "jdbc:sqlserver://hulmir.database.windows.net:1433;"
				+ "database=Hulmir;"
				+ "user=hulmir@hulmir;"
				+ "password=Fred1101;"
				+ "encrypt=true;"
				+ "trustServerCertificate=false;"
				+ "hostNameInCertificate=*.database.windows.net;"
				+ "loginTimeout=30;";
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver"); 
			DriverManager.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver()); // make sure the correct driver is being used: sqljdbc4.jar 
			connectionSQLAzure = DriverManager.getConnection(connectionString);
			MiscTools.isConnectedToSQLAzure = connectionSQLAzure.isValid(0);
			if(MiscTools.isConnectedToSQLAzure){
				System.out.println("connection to SQL azure database successful");

				boolean isGameResultsCreated = createGameResultsTable();
				System.out.println("table GameResults created/already created: " + isGameResultsCreated);
				boolean isGamePlayedCreated = createGamePlayedTable();
				System.out.println("table GamePlayed created/already created: " + isGamePlayedCreated);
				boolean isGameTurnCreated = createGameTurnTable();
				System.out.println("table GameTurn created/already created: " + isGameTurnCreated);
				boolean isGameLearningCreated = createGameLearningTables();
				System.out.println("table GameLearning created/already created: " + isGameLearningCreated);

				boolean isGameLearningLoaded = loadGameLearning(1); // load first learning knowledge (tictactoe)
				System.out.println("GameLearning loaded: " + isGameLearningLoaded);
			}
		}catch (Exception e) {
			System.out.println("failed connection to SQL azure database");
		}return MiscTools.isConnectedToSQLAzure;
	}

	@Override
	public String getAllStat(){
		String query = "SELECT P1Name, P2Name, P1Wins, P2Wins, Draws FROM dbo.game_results";
		String str = "";
		String newline = System.getProperty("line.separator");
		Statement statement = null; 
		ResultSet resultSet = null;
		try {
			statement = connectionSQLAzure.createStatement();
			resultSet = statement.executeQuery(query);
			while (resultSet.next()){
				str = str + resultSet.getString("P1Name") + " " + resultSet.getString("P2Name") + " "
						+ resultSet.getInt("P1Wins")  + " " + resultSet.getInt("P2Wins") + " " + resultSet.getInt("Draws")
						+ newline;
			}
			statement.close();
			//System.out.println("successfully executed selectAllFromGameResults");
			return str;
		}catch (SQLException e) {
			System.out.println("failed selectAllFromGameResults");
			return null;
		}finally{
			closeConnection(null, resultSet, statement, null);
		}
	}

	@Override
	public boolean addNewStat(String P1Name, String P2Name) {
		int P1Wins = 0, P2Wins = 0, Draws = 0;
		ResultSet resultSet = null;
		PreparedStatement preparedStmt = null;
		// Create and execute an INSERT SQL prepared statement.
		String query = "INSERT INTO dbo.game_results (P1Name, P2Name, P1Wins, P2Wins, Draws)" + "VALUES (?, ?, ?, ?, ?)";
		try {
			preparedStmt = connectionSQLAzure.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			preparedStmt.setString(1, P1Name);
			preparedStmt.setString(2, P2Name);
			preparedStmt.setInt(3, P1Wins);
			preparedStmt.setInt(4, P2Wins);
			preparedStmt.setInt(5, Draws);
			preparedStmt.execute();
			resultSet = preparedStmt.getGeneratedKeys(); // Retrieve the generated key from the insert.
			while (resultSet.next()) { // Print the ID of the inserted row.
				//System.out.println("Generated: " + resultSet.getString(1));
			}
			return true;
		}catch (Exception e) {
			System.out.println("failed addNewStat: " + P1Name + ", " + P2Name);
			return false;
		}finally {
			// Close the connections after the data has been handled.
			closeConnection(preparedStmt, resultSet, null, null);
		}
	}

	@Override
	public int[] getStat(String name1, String name2){
		Statement statement = null; 
		ResultSet resultSet = null;
		int[] result = new int[4];
		boolean isFound = false;
		try {
			// Create and execute a SELECT SQL statement.
			String query = "SELECT P1Wins, P2Wins, Draws FROM dbo.game_results "
					+ "WHERE P1Name = '" + name1 + "' AND P2Name = '" + name2 + "'";
			//System.out.println("print query: " + query);
			statement = connectionSQLAzure.createStatement();
			resultSet = statement.executeQuery(query);
			// Print results from select statement
			while (resultSet.next()) 
			{
				isFound = true;
				result[0] = resultSet.getInt("P1Wins");
				result[1] = resultSet.getInt("P2Wins");
				result[2] = resultSet.getInt("Draws");
				result[3] = result[0] + result[1] + result[2];
			}
			if (!isFound) {
				//System.out.println("selectFromGameResults did not find the entry - call insertIntoGameResults");
				if(addNewStat(name1, name2)){
					result = getStat(name1, name2); // retry
				}
			}else{
				//System.out.println("successfully executed selectFromGameResults " + name1 + " " + name2);
			}
			return result;
		}catch (Exception e) {
			System.out.println("failed selectFromGameResults " + name1 + " " + name2);
			return null;
		}finally{
			closeConnection(null, resultSet, statement, null);
		}
	}

	@Override
	public boolean setStat(String name1, String name2, int stat1, int stat2, int stat3, int stat4){
		String query = "UPDATE dbo.game_results SET P1Wins = ?, P2Wins = ?, Draws = ? WHERE P1Name = ? AND P2Name = ?";
		PreparedStatement preparedStmt = null;
		try {
			preparedStmt = connectionSQLAzure.prepareStatement(query);
			preparedStmt.setInt   (1, stat1);
			preparedStmt.setInt   (2, stat2);
			preparedStmt.setInt   (3, stat3);
			preparedStmt.setString(4, name1);
			preparedStmt.setString(5, name2);
			preparedStmt.executeUpdate();
			//System.out.println(successfully executed updateFromGameResults " + preparedStmt.toString());
			preparedStmt.close();
			return true;
		} catch (SQLException e) {
			System.out.println("failed updateFromGameResults " + preparedStmt.toString());
			return false;
		}finally{
			closeConnection(preparedStmt, null, null, null);
		}
	}

	@Override
	public boolean resetStat(){
		String query = "DELETE FROM dbo.game_results";
		PreparedStatement preparedStmt = null;
		try {
			preparedStmt = connectionSQLAzure.prepareStatement(query);
			boolean rs = preparedStmt.execute();
			preparedStmt.close();
			System.out.println("successfully executed deleteFromGameResults " + !rs); // if rs is false then game_results have been emptied
			return true;
		}catch (SQLException e) {
			System.out.println("failed deleteFromGameResults");
			return false;
		}finally{
			closeConnection(preparedStmt, null, null, null);
		}

	}

	// save both the info on the match and the info on every turn in that match
	@Override
	public boolean recordGamePlayed(int GameType, String P1Name, String P2Name, int P1Difficulty, int P2Difficulty, 
			boolean isP1Computer, boolean isP2Computer, int P1Algo, int P2Algo, int Winner, int GameGridX, int GameGridY){
		
		getLearnedGame(GameType);

		String query = "INSERT INTO dbo.game_played (GameType, P1Name, P2Name, P1Difficulty, P2Difficulty, "
				+ "isP1Computer, isP2Computer, P1Algo, P2Algo, Winner, GameGridX, GameGridY)"
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement preparedStmt = null;
		int GamePlayedID = 0;
		ResultSet resultSet = null;
		try {
			preparedStmt = connectionSQLAzure.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			preparedStmt.setInt(1, GameType);
			preparedStmt.setString(2, P1Name);
			preparedStmt.setString(3, P2Name);
			preparedStmt.setInt(4, P1Difficulty);
			preparedStmt.setInt(5, P2Difficulty);
			preparedStmt.setBoolean(6, isP1Computer);
			preparedStmt.setBoolean(7, isP2Computer);
			preparedStmt.setInt(8, P1Algo);
			preparedStmt.setInt(9, P2Algo);
			preparedStmt.setInt(10, Winner);
			preparedStmt.setInt(11, GameGridX);
			preparedStmt.setInt(12, GameGridY);
			int affectedRows = preparedStmt.executeUpdate();
			if(affectedRows == 0){
				System.out.println("ERROR! PersistenceSQLAzure.recordGamePlayed preparedStmt.executeUpdate() had no effect");
				return false;
			}
			resultSet = preparedStmt.getGeneratedKeys();
			resultSet.next();
			GamePlayedID = (int) resultSet.getLong(1);
			if(GamePlayedID == 0){
				System.out.println("ERROR! PersistenceSQLAzure.recordGamePlayed preparedStmt.getGeneratedKeys() no ID obtained");
				return false;
			}
			//System.out.println("successfully executed recordGamePlayed: " + preparedStmt.toString() + ", played game ID: " + GamePlayedID);
		}catch (SQLException e) {
			System.out.println("failed recordGamePlayed \n" + preparedStmt.toString());
			gameTurnLocalList = new ArrayList<GameTurn>();
			return false;
		}finally{
			closeConnection(preparedStmt, null, null, null);
		}

		// now save the batch of individual moves
		query = "INSERT INTO dbo.game_turn (GamePlayedID, grid, turn, isPlayer1turn, isSelecting)"
				+ "VALUES (?, ?, ?, ?, ?)";
		try {
			preparedStmt = connectionSQLAzure.prepareStatement(query);
			int count = gameTurnLocalList.size();
			GameTurn gameTurn;
			for (int i = 0; i < count; i++) {
				gameTurn = gameTurnLocalList.get(i);
				preparedStmt.setInt(1, GamePlayedID);
				preparedStmt.setString(2, gameTurn.grid);
				preparedStmt.setInt(3, gameTurn.turn);
				preparedStmt.setBoolean(4, gameTurn.isPlayer1turn);
				preparedStmt.setBoolean(5, gameTurn.isSelecting);
				preparedStmt.addBatch();
				if (i % 1000 == 0 || i == count-1) {
					preparedStmt.executeBatch(); // Execute every 1000 items or when the last item have been added
				}
			}
			//System.out.println("successfully executed all game turns \n" + preparedStmt.toString());
		}catch (SQLException e) {
			System.out.println("failed recordGameTurn \n" + preparedStmt.toString());
			gameTurnLocalList = new ArrayList<GameTurn>();
			return false;
		}finally{
			closeConnection(preparedStmt, null, null, null);
		}

		// update the game learning
		int value = Winner==1?1:Winner==2?-1:0;
		query = "IF EXISTS(SELECT * FROM dbo.game_learning_"+MiscTools.GAME_NAMES[GameType-1]+" WHERE Grid =?) "
				+ "UPDATE dbo.game_learning_"+MiscTools.GAME_NAMES[GameType-1]+" SET Value = Value + ? WHERE Grid =? "
				+ "ELSE "
				+ "INSERT INTO dbo.game_learning_"+MiscTools.GAME_NAMES[GameType-1]+" (Grid, Value) VALUES (?, ?) ";
		try {
			preparedStmt = connectionSQLAzure.prepareStatement(query);
			int count = gameTurnLocalList.size();
			GameTurn gameTurn;
			for (int i = 0; i < count; i++) {
				gameTurn = gameTurnLocalList.get(i);
				Integer oldGameTurnValue = learnedGames[GameType-1].get(gameTurn.grid); // update local memory along with persistence
				if(oldGameTurnValue == null){
					learnedGames[GameType-1].put(gameTurn.grid, value);
				}else{
					learnedGames[GameType-1].replace(gameTurn.grid, value + oldGameTurnValue);
				}
				preparedStmt.setString(1, gameTurn.grid);
				preparedStmt.setInt(2, value);
				preparedStmt.setString(3, gameTurn.grid);
				preparedStmt.setString(4, gameTurn.grid);
				preparedStmt.setInt(5, value);

				preparedStmt.addBatch();
				if (i % 1000 == 0 || i == count-1) {
					preparedStmt.executeBatch(); // Execute every 1000 items or when the last item have been added
				}
			}
			//System.out.println("successfully executed all game learnings \n" + preparedStmt.toString());
		}catch (SQLException e) {
			System.out.println("failed recordGameTurn \n" + preparedStmt.toString());
			return false;
		}finally{
			gameTurnLocalList = new ArrayList<GameTurn>();
			closeConnection(preparedStmt, null, null, null);
		}

		return true;
	}

	// save the turn by turn results locally
	@Override
	public boolean recordGameTurn(int[][] grid, int turn, boolean isPlayer1turn, boolean isSelecting){
		GameTurn gameTurn = null;
		String gridString = MiscTools.gridStringBuilder(grid);
		gameTurn = new GameTurn(gridString, turn, isPlayer1turn, isSelecting);
		gameTurnLocalList.add(gameTurn);
		return true;
	}

	@Override
	// load a games data only when needed
	public ConcurrentHashMap<String, Integer> getLearnedGame(int gameID) {
		gameID = gameID-1; // to fit in arrays
		if(learnedGames[gameID] == null){
			boolean isLoaded = loadGameLearning(gameID);
			if(isLoaded){
				return learnedGames[gameID];
			}else{
				System.out.println("failed to load game " + gameID);
				return null;
			}
		}else{			
			return learnedGames[gameID];
		}
	}

	// private methods

	private boolean loadGameLearning(int gameID){
		learnedGames[gameID] = new ConcurrentHashMap<String, Integer>(); // Initialise learnedGames

		Statement statement = null; 
		ResultSet resultSet = null;
		try {
			String query = "SELECT Grid, Value FROM dbo.game_learning_"+MiscTools.GAME_NAMES[gameID];
			statement = connectionSQLAzure.createStatement();
			resultSet = statement.executeQuery(query);
			while (resultSet.next()) // loop for every row
			{
				String gridString = resultSet.getString("Grid");
				learnedGames[gameID].put(gridString, resultSet.getInt("Value")); // save to a hash dictionary for later retrieval
			}
		}catch (Exception e) {
			System.out.println("failed loadGameLearning");
			System.out.println(e);
			learnedGames = null;
			return false;
		}finally{
			closeConnection(null, resultSet, statement, null);
		}
		return true;
	}

	private boolean createGameResultsTable(){
		Statement statement = null; 
		try {
			String sqlString = "IF OBJECT_ID ('dbo.game_results', 'U') is null " +
					"BEGIN " +
					"CREATE TABLE game_results (" + 
					"[GameID] [int] IDENTITY(1,1) NOT NULL," +
					"[P1Name] [nvarchar](50) NOT NULL," +
					"[P2Name] [nvarchar](50) NOT NULL," +
					"[P1Wins] [int] NOT NULL," +
					"[P2Wins] [int] NOT NULL," +
					"[Draws] [int] NOT NULL) " +
					"END";
			statement = connectionSQLAzure.createStatement(); // Use the connection to create the SQL statement.
			statement.executeUpdate(sqlString); // Execute the statement.
			return true;
		}catch (Exception e) {
			System.out.println("failed createGameResultsTable");
			return false;
		}finally{
			closeConnection(null, null, statement, null);
		}
	}

	private boolean createGamePlayedTable(){
		//game_played (GameType, P1Name, P2Name, P1Difficulty, P2Difficulty, isP1Computer, isP2Computer, P1Algo, P2Algo, Winner, GameGridX, GameGridY)"
		Statement statement = null; 
		try {
			String sqlString = "IF OBJECT_ID ('dbo.game_played', 'U') is null " +
					"BEGIN " +
					"CREATE TABLE game_played (" + 
					"[GamePlayedID] [int] IDENTITY(1,1) NOT NULL," +
					"[GameType] [int] NOT NULL," +
					"[P1Name] [nvarchar](50) NOT NULL," +
					"[P2Name] [nvarchar](50) NOT NULL," +
					"[P1Difficulty] [int] NOT NULL," +
					"[P2Difficulty] [int] NOT NULL," +
					"[isP1Computer] [BIT] NOT NULL," +
					"[isP2Computer] [BIT] NOT NULL," +
					"[P1Algo] [int] NOT NULL," +
					"[P2Algo] [int] NOT NULL," +
					"[Winner] [int] NOT NULL," +
					"[GameGridX] [int] NOT NULL," +
					"[GameGridY] [int] NOT NULL) " +
					"END";
			statement = connectionSQLAzure.createStatement(); // Use the connection to create the SQL statement.
			statement.executeUpdate(sqlString); // Execute the statement.
			return true;
		}catch (Exception e) {
			System.out.println("failed createGamePlayedTable");
			return false;
		}finally{
			closeConnection(null, null, statement, null);
		}
	}

	private boolean createGameTurnTable(){
		Statement statement = null; 
		try {
			String sqlString = "IF OBJECT_ID ('dbo.game_turn', 'U') is null " +
					"CREATE TABLE game_turn (" + 

					"[GameTurnID] [int] IDENTITY(1,1) NOT NULL," +
					"[GamePlayedID] [int] NOT NULL," +
					"[Grid] [nvarchar](MAX) NOT NULL," +
					"[Turn] [int] NOT NULL," +
					"[isPlayer1turn] [BIT] NOT NULL, " +
					"[isSelecting] [BIT] NOT NULL)";
			statement = connectionSQLAzure.createStatement(); // Use the connection to create the SQL statement.
			statement.executeUpdate(sqlString); // Execute the statement.
			return true;
		}catch (Exception e) {
			System.out.println("failed createGameTurnTable");
			return false;
		}finally{
			closeConnection(null, null, statement, null);
		}
	}

	private boolean createGameLearningTables(){
		Statement statement = null;
		try {
			for (int i = 0; i < MiscTools.GAME_NAMES.length; i++) {
				String query = "IF OBJECT_ID ('dbo.game_learning_"+MiscTools.GAME_NAMES[i]+"', 'U') is null "
						+ "CREATE TABLE game_learning_"+MiscTools.GAME_NAMES[i]+" ("
						+ "[GameLearningID] [int] IDENTITY(1,1) NOT NULL,"
						+ "[Grid] [nvarchar](MAX) NOT NULL,"
						+ "[Value] [int] NOT NULL"
						+ ")";
				statement = connectionSQLAzure.createStatement();
				statement.execute(query);
			}
			return true;
		}catch (Exception e) {
			System.out.println("failed createGameLearningTable");
			return false;
		}finally{
			closeConnection(null, null, statement, null);
		}
	}

	@SuppressWarnings("unused")
	private boolean dropGameResultsTable(){
		Statement statement = null; 
		try {
			String sqlString = "DROP TABLE game_results";
			statement = connectionSQLAzure.createStatement(); // Use the connection to create the SQL statement.
			statement.executeUpdate(sqlString);// Execute the statement.
			return true;
		}catch (Exception e) {
			System.out.println("failed dropGameResultsTable");
			return false;
		}finally{
			closeConnection(null, null, statement, null);
		}
	}

	@SuppressWarnings("unused")
	private boolean dropGamePlayedTable(){
		Statement statement = null; 
		try {
			String sqlString = "DROP TABLE game_played";
			statement = connectionSQLAzure.createStatement(); // Use the connection to create the SQL statement.
			statement.executeUpdate(sqlString);// Execute the statement.
			return true;
		}catch (Exception e) {
			System.out.println("failed dropGamePlayedTable");
			return false;
		}finally{
			closeConnection(null, null, statement, null);
		}
	}

	@SuppressWarnings("unused")
	private boolean dropGameTurnTable(){
		Statement statement = null; 
		try {
			String sqlString = "DROP TABLE game_turn";
			statement = connectionSQLAzure.createStatement(); // Use the connection to create the SQL statement.
			statement.executeUpdate(sqlString);// Execute the statement.
			return true;
		}catch (Exception e) {
			System.out.println("failed dropGameTurnTable");
			return false;
		}finally{
			closeConnection(null, null, statement, null);
		}
	}

	@SuppressWarnings("unused")
	private boolean dropGameLearningTable(){
		Statement statement = null; 
		try {
			String sqlString = "DROP TABLE game_learning";
			statement = connectionSQLAzure.createStatement(); // Use the connection to create the SQL statement.
			statement.executeUpdate(sqlString);// Execute the statement.
			return true;
		}catch (Exception e) {
			System.out.println("failed dropGameLearningTable");
			return false;
		}finally{
			closeConnection(null, null, statement, null);
		}
	}

	private boolean closeConnection(PreparedStatement preparedStmt, ResultSet resultSet, Statement statement, Connection connection){
		int i = 0;
		// Close the connections after the data has been handled.
		if (preparedStmt != null) try { preparedStmt.close(); i++; } catch(Exception e) {i = -10;}
		if (resultSet    != null) try { resultSet.close();    i++; } catch(Exception e) {i = -10;}
		if (statement    != null) try { statement.close();    i++; } catch(Exception e) {i = -10;}
		if (connection   != null) try { connection.close();   i++; } catch(Exception e) {i = -10;}
		if(i > 0)
			return true;
		else
			return false;
	}

	// inner class

	private class GameTurn{
		public String grid;
		public int turn;
		public boolean isPlayer1turn;
		public boolean isSelecting;

		public GameTurn(String grid, int turn, boolean isPlayer1turn, boolean isSelecting){
			this.grid = grid;
			this.turn = turn;
			this.isPlayer1turn = isPlayer1turn;
			this.isSelecting = isSelecting;
		}
	}

}
