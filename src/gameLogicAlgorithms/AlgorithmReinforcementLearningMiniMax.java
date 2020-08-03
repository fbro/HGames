package gameLogicAlgorithms;

import gameLogic.IStateOBJ;
import gameLogic.MiscTools;

public class AlgorithmReinforcementLearningMiniMax extends AlgorithmAb implements IGameAlgorithm {

	@Override
	public String getAlgorithmName() {return "R. L. M. M.";}

	@Override
	public int getAlgorithmID() {return 6;}

	@Override
	protected int utilValue(IStateOBJ state, int movingPlayer, int max, int min){
		Integer v = MiscTools.persistence.getLearnedGame(logic.getGameID()).get(MiscTools.gridStringBuilder(state.getGrid()));
		if(v == null){ // if reinforcement learning doesn't know that state then see if the evaluation method does
			return logic.evaluation(state, movingPlayer, max, min);
		}else{
			return v;
		} 
	}
}
