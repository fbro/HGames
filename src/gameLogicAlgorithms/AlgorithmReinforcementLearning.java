package gameLogicAlgorithms;

import java.util.ArrayList;
import java.util.Random;

import gameLogic.IStateOBJ;
import gameLogic.MiscTools;
import gameLogicTypes.IGameType;

public class AlgorithmReinforcementLearning implements IGameAlgorithm {
	private final Random RANDOM = new Random();
	
	@Override
	public IStateOBJ startAlgorithm(IStateOBJ state, int movingPlayer, int otherPlayer, IGameType logic) {
		return reinforcementLearning(state, movingPlayer, otherPlayer, logic);
	}

	@Override
	public String getAlgorithmName() {return "Reinforcement Learning";}

	@Override
	public int getAlgorithmID() {return 5;}
	
	private IStateOBJ reinforcementLearning(IStateOBJ state, int movingPlayer, int otherPlayer, IGameType logic) {
		ArrayList<IStateOBJ> moves = logic.narrowMoves(state, movingPlayer, otherPlayer);
		if(moves.size() == 1){/*System.out.println("only one move");*/return moves.get(0);}
		ArrayList<Integer> utilityValues = new ArrayList<Integer>();
		int bestUtilityValuePointer = 0;
		
		int d = logic.getDifficulty(movingPlayer);
		if(RANDOM.nextInt(100) < (d==1?20:d==2?5:0)){ // 20% for easy 5% for medium 0% for hard - explore instead of doing known moves
			return moves.get(RANDOM.nextInt(moves.size()));
		}
		
		// utility values for the available moves
		for(int i=0;i<moves.size();i++){
			// look for a known scenario in persistence
			Integer v = MiscTools.persistence.getLearnedGame(logic.getGameID()).get(MiscTools.gridStringBuilder(moves.get(i).getGrid()));
			if(v != null){
					utilityValues.add(movingPlayer==1?v:(v*-1)); // if movingPlayer is 2 then mirror value to represent good moves for player 2
			}else{
				utilityValues.add(RANDOM.nextInt(moves.size())*-1);
			}
		}

		for (int i = 1; i < utilityValues.size(); i++) { // choose best utility value
			if(utilityValues.get(bestUtilityValuePointer) < utilityValues.get(i)){
				bestUtilityValuePointer = i;
			}
		}

		IStateOBJ p = moves.get(bestUtilityValuePointer);
		return p;
	}

}
