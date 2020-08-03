package gamePersistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import gameLogic.MiscTools;

public class PersistenceMySQLLocal implements IGamePersistence {
	
	private final String DATABASE_DRIVER = "com.mysql.jdbc.Driver";
	private final String DATABASE_URL = "jdbc:mysql://localhost:3306/hgames";
	private final String USERNAME = "hulmir";
	private final String PASSWORD = "fred1101";
	private final String MAX_POOL = "250"; // set your own limit
	private Connection connectionMySQL; // init connection object
	private Properties propertiesMySQL; // init properties object
	
	@Override
	public boolean connectToDatabase(){
		propertiesMySQL = new Properties();
		propertiesMySQL.setProperty("user", USERNAME);
		propertiesMySQL.setProperty("password", PASSWORD);
		propertiesMySQL.setProperty("MaxPooledStatements", MAX_POOL);

		try {
			Class.forName(DATABASE_DRIVER);
			connectionMySQL = DriverManager.getConnection(DATABASE_URL, propertiesMySQL);
			// Do something with the Connection
			System.out.println("connected to MySQL server successfully");
			MiscTools.isConnectedToMySQL = true;
			return true;
		} catch (ClassNotFoundException | SQLException e) {
			// handle any errors
			System.out.println("MySQL link failed... ");
			MiscTools.isConnectedToMySQL = false;
			return false;
		}
	}
	
	@Override
	public String getAllStat(){
		String query = "SELECT P1Name, P2Name, P1Wins, P2Wins, Draws FROM hgames.game_results LIMIT 0, 100";
		String str = "";
		String newline = System.getProperty("line.separator");
		try {
			Statement st = connectionMySQL.createStatement();
			ResultSet rs = st.executeQuery(query);
			while (rs.next()){
				str = str + rs.getString("P1Name") + " " + rs.getString("P2Name") + " "
						+ rs.getInt("P1Wins")  + " " + rs.getInt("P2Wins") + " "  + rs.getInt("Draws")
						+ newline;
			}
			st.close();
			System.out.println("successfully executed selectAllFromGameResults");
			return str;
		} catch (SQLException e) {
			System.out.println("failed selectAllFromGameResults");
			return null;
		}
	}
	
	@Override
	public boolean addNewStat(String P1Name, String P2Name){
		int P1Wins = 0, P2Wins = 0, Draws = 0;
		String query = "INSERT INTO hgames.game_results (P1Name, P2Name, P1Wins, P2Wins, Draws)" + "VALUES (?, ?, ?, ?, ?)";
		try {
			PreparedStatement preparedStatement = connectionMySQL.prepareStatement(query);
			preparedStatement.setString(1, P1Name);
			preparedStatement.setString(2, P2Name);
			preparedStatement.setInt(3, P1Wins);
			preparedStatement.setInt(4, P2Wins);
			preparedStatement.setInt(5, Draws);
			preparedStatement.executeUpdate();
			System.out.println("successfully executed insertIntoGameResults");
			return true;
		} catch (SQLException e) {
			System.out.println("failed insertIntoGameResults");
			return false;
		} 
	}
	
	@Override
	public int[] getStat(String name1, String name2){
		int[] result = new int[4];
		String query = "SELECT P1Wins, P2Wins, Draws FROM hgames.game_results "
				+ "WHERE P1Name = \"" + name1 + "\" AND P2Name = \"" + name2 + "\"";
		boolean isFound = false;
		try {
			Statement st = connectionMySQL.createStatement();
			ResultSet rs = st.executeQuery(query);
			while (rs.next()){
				isFound = true;
				result[0] = rs.getInt("P1Wins");
				result[1] = rs.getInt("P2Wins");
				result[2] = rs.getInt("Draws");
				result[3] = result[0] + result[1] + result[2];
			}
			st.close();
			if (!isFound) {
				System.out.println("selectFromGameResults did not find the entry - call insertIntoGameResults");
				if(addNewStat(name1, name2)){
					result = getStat(name1, name2); // retry
				}
			}else{
				System.out.println("successfully executed selectFromGameResults " + name1 + " " + name2);
			}
			return result;
		} catch (SQLException e) {
			System.out.println("failed selectFromGameResults " + name1 + " " + name2);
			return null;
		}
	}
	
	@Override
	public boolean setStat(String name1, String name2, int stat1, int stat2, int stat3, int stat4){
		String query = "UPDATE hgames.game_results SET P1Wins = ?, P2Wins = ?, Draws = ? WHERE P1Name = ? AND P2Name = ?";
		PreparedStatement preparedStmt = null;
		try {
			preparedStmt = connectionMySQL.prepareStatement(query);
			preparedStmt.setInt   (1, stat1);
			preparedStmt.setInt   (2, stat2);
			preparedStmt.setInt   (3, stat3);
			preparedStmt.setString(4, name1);
			preparedStmt.setString(5, name2);
			int ret = preparedStmt.executeUpdate();
			System.out.println(ret + " successfully executed updateFromGameResults " + preparedStmt.toString());
			preparedStmt.close();
			return true;
		} catch (SQLException e) {
			System.out.println("failed updateFromGameResults " + preparedStmt.toString());
			return false;
		}
	}
	
	@Override
	public boolean resetStat(){
		String query = "DELETE FROM hgames.game_results";
		try {
			PreparedStatement preparedStmt = connectionMySQL.prepareStatement(query);
			boolean rs = preparedStmt.execute();
			preparedStmt.close();
			System.out.println("successfully executed deleteFromGameResults " + rs);
			return true;
		} catch (SQLException e) {
			System.out.println("failed deleteFromGameResults");
			return false;
		}
	}
	
	@Override
	public boolean recordGamePlayed(int GameType, String P1Name, String P2Name, int P1Difficulty, int P2Difficulty, 
			boolean isP1Computer, boolean isP2Computer, int P1Algo, int P2Algo, int Winner, int GameGridX, int GameGridY){
		if(!MiscTools.isConnectedToMySQL) // do not run this method if there is no connection to MySQL
			return false;
		String query = "INSERT INTO hgames.game_played (GameType, P1Name, P2Name, P1Difficulty, P2Difficulty, "
				+ "isP1Computer, isP2Computer, P1Algo, P2Algo, Winner, GameGridX, GameGridY)"
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connectionMySQL.prepareStatement(query);
			preparedStatement.setInt(1, GameType);
			preparedStatement.setString(2, P1Name);
			preparedStatement.setString(3, P2Name);
			preparedStatement.setInt(4, P1Difficulty);
			preparedStatement.setInt(5, P2Difficulty);
			preparedStatement.setBoolean(6, isP1Computer);
			preparedStatement.setBoolean(7, isP2Computer);
			preparedStatement.setInt(8, P1Algo);
			preparedStatement.setInt(9, P2Algo);
			preparedStatement.setInt(10, Winner);
			preparedStatement.setInt(11, GameGridX);
			preparedStatement.setInt(12, GameGridY);
			preparedStatement.executeUpdate();
			System.out.println("successfully executed recordGamePlayed \n" + preparedStatement.toString());
			return true;
		} catch (SQLException e) {
			System.out.println("failed recordGamePlayed \n" + preparedStatement.toString());
			return false;
		}
	}

	@Override
	public boolean recordGameTurn(int[][] grid, int turn, boolean isPlayer1turn, boolean isSelecting) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ConcurrentHashMap<String, Integer> getLearnedGame(int gameType) {
		// TODO Auto-generated method stub
		return null;
	}

}
