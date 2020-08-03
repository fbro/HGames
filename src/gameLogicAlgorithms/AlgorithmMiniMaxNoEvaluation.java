package gameLogicAlgorithms;

import gameLogic.IStateOBJ;

public class AlgorithmMiniMaxNoEvaluation extends AlgorithmAb implements IGameAlgorithm{

	@Override
	public String getAlgorithmName() {return "MiniMax No Eval.";}
	
	@Override
	public int getAlgorithmID() {return 4;}
	
	@Override
	protected int utilValue(IStateOBJ state, int movingPlayer, int max, int min){
		return 0;
	}
}