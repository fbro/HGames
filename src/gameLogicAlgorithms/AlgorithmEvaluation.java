package gameLogicAlgorithms;

import java.util.ArrayList;
import java.util.Collections;

import gameLogic.IStateOBJ;
import gameLogicTypes.IGameType;

public class AlgorithmEvaluation implements IGameAlgorithm {

	@Override
	public IStateOBJ startAlgorithm(IStateOBJ state, int movingPlayer, int otherPlayer, IGameType logic) {
		return evaluationOnly(state, logic.getDepth(movingPlayer), movingPlayer, otherPlayer, logic);
	}

	@Override
	public String getAlgorithmName() {return "Eval. only";}
	
	@Override
	public int getAlgorithmID() {return 3;}

	private IStateOBJ evaluationOnly(IStateOBJ state, int depth, int movingPlayer, int otherPlayer, IGameType logic){ // must be at least two moves
		//long startTime = System.nanoTime(); // record time
		ArrayList<IStateOBJ> moves = logic.narrowMoves(state, movingPlayer, otherPlayer);
		if(moves.size() == 1){/*System.out.println("only one move");*/return moves.get(0);}
		ArrayList<Integer> utilityValues = new ArrayList<Integer>();
		int bestUtilityValuePointer = 0;
		Collections.shuffle(moves);

		// generate utility values only based on the evaluation function for the available moves
		for(int i=0;i<moves.size();i++){
			utilityValues.add(logic.evaluation(moves.get(i), movingPlayer, movingPlayer, otherPlayer));
		} // sequential solution

		for (int i = 1; i < utilityValues.size(); i++) { // choose best utility value
			if(utilityValues.get(bestUtilityValuePointer) < utilityValues.get(i)){
				bestUtilityValuePointer = i;
			}
		}
		IStateOBJ p = moves.get(bestUtilityValuePointer);
		//long endTime = System.nanoTime(); // record time
		//int res = utilityValues.get(bestUtilityValuePointer);
		//System.out.println("evaluation. p" + movingPlayer + ", Size:" + grid.length + "-" + grid[0].length + ", utility: " + res + ",\ttook: " + ((endTime - startTime)/1000000) + " ms."); 
		return p;
	}
}
