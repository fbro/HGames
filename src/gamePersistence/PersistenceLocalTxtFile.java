package gamePersistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gameLogic.MiscTools;

public class PersistenceLocalTxtFile implements IGamePersistence {

	// statFile.txt methods
	private ArrayList<GameTurn> gameTurnLocalList = new ArrayList<GameTurn>();
	@SuppressWarnings("unchecked")
	private ConcurrentHashMap<String, Integer>[] learnedGames = new ConcurrentHashMap[MiscTools.GAME_NAMES.length];
	private File logFile;

	@Override
	public boolean connectToDatabase(){
		
		getLearnedGame(1); // load first learning knowledge (tictactoe)
		
		logFile = new File("statFile.txt");
		if(!logFile.exists()){
			try{
				logFile.createNewFile();
			}catch(IOException e){
				System.out.println("logFile.createNewFile failed in createStatFile");
			}
			if(!logFile.exists()){
				System.out.println("file does not exist - and cannot be created!");
				return false;
			}
			resetStat();
		}
		return true;
	}

	@Override
	public String getAllStat(){ // primarily for testing - prints out the entire statFile.txt file
		boolean isCreated = connectToDatabase();
		if(!isCreated)
			return "";
		BufferedReader reader = null;
		String printStr = "";
		String newline = System.getProperty("line.separator");

		try{
			reader = new BufferedReader(new FileReader(logFile));
			String line;
			while ((line = reader.readLine()) != null) {
				printStr = printStr + line + newline;
			}
		}catch(IOException e){}
		finally{
			if(reader != null)
				try{reader.close();}catch(IOException e){}
		}
		return printStr;
	}

	@Override
	public boolean addNewStat(String name1, String name2){
		boolean isCreated = connectToDatabase();
		if(!isCreated)
			return false;
		BufferedWriter writer = null;
		BufferedReader reader = null;
		boolean hasAddedMatch = false;
		boolean hasFoundOldMatch = false;
		String newline = System.getProperty("line.separator");
		try{
			writer = new BufferedWriter(new FileWriter(logFile, true));
			reader = new BufferedReader(new FileReader(logFile));

			// check to see if the two names have already been added
			String line;
			while ((line = reader.readLine()) != null) {
				//line;
				if(line.toLowerCase().startsWith(name1.toLowerCase() + " " + name2.toLowerCase() + " ")){
					hasFoundOldMatch = true;
					break;
				}
			}
			// if the two names have not been added before then add them here
			if(!hasFoundOldMatch){
				String names = name1 + " " + name2;
				int v = 35 - names.length(); // custom tab for better overview, 17 characters is max 
				String extraSpaces = "";
				for (int j = 0; j < v; j++) {
					extraSpaces += " ";
				}
				writer.write(newline + names + extraSpaces + " [" + 0 + "] [" + 0 + "] [" + 0 + "] [" + 0 + "]");
				hasAddedMatch = true;
			}
		}catch(IOException e){}
		finally{
			if(reader != null)
				try{reader.close();writer.close();}catch(IOException e){}
		}
		return hasAddedMatch; // if return is false then it already exists
	}

	@Override
	public int[] getStat(String name1, String name2){
		boolean isCreated = connectToDatabase();
		if(!isCreated)
			return null;
		BufferedReader reader = null;
		int[] result = new int[4];
		boolean hasFoundOldMatch = false;
		try{
			reader = new BufferedReader(new FileReader(logFile));
			String line;
			while ((line = reader.readLine()) != null) 
			{
				if(line.toLowerCase().startsWith(name1.toLowerCase() + " " + name2.toLowerCase() + " ")){
					hasFoundOldMatch = true;
					Pattern r = Pattern.compile("\\[[0-9]+\\]"); // Create a Pattern object
					Matcher m = r.matcher(line); // Now create matcher object.
					// sets the two int value in the int array to the results of old matches
					if(m.find()){result[0] = Integer.parseInt(m.group().replaceAll("\\[|\\]", ""));}
					if(m.find()){result[1] = Integer.parseInt(m.group().replaceAll("\\[|\\]", ""));}
					if(m.find()){result[2] = Integer.parseInt(m.group().replaceAll("\\[|\\]", ""));}
					if(m.find()){result[3] = Integer.parseInt(m.group().replaceAll("\\[|\\]", ""));}
					//System.out.println("getMatchStat found result[]: " + result[0] + ", " + result[1]);
					break;
				}
			}
		}catch(IOException e){}
		finally{
			if(reader != null)
				try{reader.close();}catch(IOException e){}
		}
		if(hasFoundOldMatch){
			return result;
		}else{
			return null;
		}
	}

	@Override
	public boolean setStat(String name1, String name2, int stat1, int stat2, int stat3, int stat4){
		boolean isCreated = connectToDatabase();
		if(!isCreated)
			return false;
		BufferedWriter writer = null;
		String oldFile = getAllStat(); // extract the whole file
		boolean isEdited = false;
		String newline = System.getProperty("line.separator");
		String lines[] = oldFile.split(System.getProperty("line.separator"));
		for (int i = 0; i < lines.length; i++) {
			if(lines[i].toLowerCase().startsWith(name1.toLowerCase() + " " + name2.toLowerCase() + " ")){
				//System.out.println("setMatchStat to be edited to: " + name1 + " " + name2 + " " + stat1 + " " + stat2);
				String names = name1 + " " + name2;
				int v = 35 - names.length(); // custom tab for better overview, 17 characters is max 
				String extraSpaces = "";
				for (int j = 0; j < v; j++) {
					extraSpaces += " ";
				}
				lines[i] = names + extraSpaces + " [" + stat1 + "] [" + stat2 + "] [" + stat3 + "] [" + stat4 + "]";
				isEdited = true;
				break;
			}
		}
		if(isEdited){
			oldFile = "";
			for (int i = 0; i < lines.length; i++) {
				oldFile = oldFile + lines[i];
				if(i+1 != lines.length){
					oldFile = oldFile + newline;
				}
			}
			try{
				writer = new BufferedWriter(new FileWriter(logFile, false)); // overwrite
				writer.write(oldFile);
			}catch(IOException e){System.out.println("failed to overwrite in setStat");}
			finally{
				if(writer != null)
					try{writer.close();}catch(IOException e){System.out.println("failed to close writer in setStat");}
			}
			return isEdited;
		}else{ // the stat does not exists - create it and try again
			isCreated = addNewStat(name1, name2);
			if(isCreated){
				boolean isSet = setStat(name1, name2, stat1, stat2, stat3, stat4);
				if(isSet){
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean resetStat(){
		boolean isCreated = connectToDatabase();
		if(!isCreated)
			return false;
		boolean isReset = false;
		BufferedWriter writer = null;
		try{
			writer = new BufferedWriter(new FileWriter(logFile, false)); // reset
			writer.write(
					"Easy-Computer1 Easy-Computer2       [0] [0] [0] [0]\n" + 
							"Normal-Computer1 Normal-Computer2   [0] [0] [0] [0]\n" +
							"Hard-Computer1 Hard-Computer2       [0] [0] [0] [0]\n" +
							"--\n" +
							"Player1 Player2                     [0] [0] [0] [0]\n" +
							"Player1 Easy-Computer2              [0] [0] [0] [0]\n" +
							"Player1 Normal-Computer2            [0] [0] [0] [0]\n" +
							"Player1 Hard-Computer2              [0] [0] [0] [0]\n" +
							"Easy-Computer1 Player2              [0] [0] [0] [0]\n" +
							"Normal-Computer1 Player2            [0] [0] [0] [0]\n" +
							"Hard-Computer1 Player2              [0] [0] [0] [0]\n" +
							"--\n" +
							"preset-AI1 preset-AI2               [0] [0] [0] [0]\n" +
					"--");
			isReset = true;
		}catch(IOException e){}
		finally{
			if(writer != null)
				try{writer.close();}catch(IOException e){}
		}
		return isReset;
	}

	@Override
	public boolean recordGamePlayed(int GameType, String P1Name, String P2Name, int P1Difficulty, int P2Difficulty,
			boolean isP1Computer, boolean isP2Computer, int P1Algo, int P2Algo, int Winner, int GameGridX,
			int GameGridY) {
		getLearnedGame(GameType);
		
		int count = gameTurnLocalList.size();
		GameTurn gameTurn;
		int value = Winner==1?1:Winner==2?-1:0;
		for (int i = 0; i < count; i++) {
			gameTurn = gameTurnLocalList.get(i);
			learnedGames[GameType-1].put(gameTurn.grid, value); // update local memory along with persistence
		}
		System.out.println("learnedGame " + (GameType-1) + ", size " + learnedGames[GameType-1].size());
		return false;
	}

	@Override
	public boolean recordGameTurn(int[][] grid, int turn, boolean isPlayer1turn, boolean isSelecting) {
		GameTurn gameTurn = null;
		String gridString = MiscTools.gridStringBuilder(grid);
		gameTurn = new GameTurn(gridString, turn, isPlayer1turn, isSelecting);
		gameTurnLocalList.add(gameTurn);
		return true;
	}

	@Override
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
		return true;
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
