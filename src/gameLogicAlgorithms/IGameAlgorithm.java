package gameLogicAlgorithms;

import gameLogic.IStateOBJ;
import gameLogicTypes.IGameType;

public interface IGameAlgorithm {

	// starts the algorithm
	public IStateOBJ startAlgorithm(IStateOBJ state, int movingPlayer, int otherPlayer, IGameType logic);
	
	// returns the name of the algorithm
	public String getAlgorithmName();
	
	// returns the id of the algorithm
	public int getAlgorithmID();
}