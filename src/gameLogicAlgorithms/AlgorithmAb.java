package gameLogicAlgorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import gameLogic.IStateOBJ;
import gameLogicTypes.IGameType;

public abstract class AlgorithmAb implements IGameAlgorithm {
	
	private final int HIGH_VAL = 1000000000; // value used in minimax
	private final int LOW_VAL = -1000000000; // value used in minimax
	private final ExecutorService exec = Executors.newWorkStealingPool(); // 4 cores - 8 logical processors
	protected IGameType logic; // this object differs from gametype to gametype
	private Thread thread = null;

	@Override
	public IStateOBJ startAlgorithm(IStateOBJ state, int movingPlayer, int otherPlayer, IGameType logic) {
		this.logic = logic;
		return algorithm(state, movingPlayer, otherPlayer);
	}
	
	// minimax with depth, alpha beta pruning and threaded
	private IStateOBJ algorithm(IStateOBJ state, int movingPlayer, int otherPlayer){ // must be at least two moves
		//long startTime = System.nanoTime(); // record time
		ArrayList<IStateOBJ> moves = logic.narrowMoves(state, movingPlayer, otherPlayer);
		if(moves.size() == 1){/*System.out.println("only one move");*/return moves.get(0);}
		ArrayList<Integer> utilityValues = new ArrayList<Integer>();
		int bestUtilityValuePointer = 0;
		Collections.shuffle(moves);

		utilityValues = concurrentMiniMax(moves, logic.getDepth(movingPlayer), otherPlayer, movingPlayer); // concurrent solution
		//		for(int i=0;i<moves.size();i++){ // sequential solution
		//			utilityValues.add(min(moves.get(i), depth-1, otherPlayer, movingPlayer, Integer.MIN_VALUE, Integer.MAX_VALUE));
		//		}

		for (int i = 1; i < utilityValues.size(); i++) { // choose best utility value
			if(utilityValues.get(bestUtilityValuePointer) < utilityValues.get(i)){
				bestUtilityValuePointer = i;
			}
		}
		if(utilityValues.size() == 0){return null;} // if we get interrupted
		IStateOBJ p = moves.get(bestUtilityValuePointer);
		//long endTime = System.nanoTime(); // record time
		//int u = utilityValues.get(bestUtilityValuePointer);String res=(u==HIGH_VAL)?"win":(u==LOW_VAL)?"lose":u+"";
		//System.out.println("Minimax. p" + movingPlayer + ", Size:" + grid.length + "-" + grid[0].length + ", utility: " + res + ",\ttook: " + ((endTime - startTime)/1000000) + " ms."); 
		return p;
	}
	
	private ArrayList<Integer> concurrentMiniMax(final ArrayList<IStateOBJ> states, final int depth, final int otherPlayer, final int movingPlayer) {
		thread = Thread.currentThread();
		ArrayList<Integer> utilityValues = new ArrayList<Integer>(); // list to save the results in
		List<Callable<Integer>> moves = new ArrayList<Callable<Integer>>(); // List to save the tasks in
		for (int i = 0; i < states.size(); i++) {
			final int e = i;
			moves.add(new Callable<Integer>(){ // create task
				public Integer call(){
					Integer res = min(states.get(e), depth-1, otherPlayer, movingPlayer, Integer.MIN_VALUE, Integer.MAX_VALUE);
					return res;
				}});
		}
		try {
			List<Future<Integer>> futures = exec.invokeAll(moves); // execute all the moves
			for (int k = 0; k < futures.size(); k++) {
				utilityValues.add(k, futures.get(k).get()); // extract the result
			}
		}catch (InterruptedException exn) {
			System.out.println("Interrupted: " + exn);
			Thread.currentThread().interrupt(); // a thread have been interrupted, now interrupt the current thread so that the controller will stop
		}catch (ExecutionException exn) {
			throw new RuntimeException(exn.getCause()); 
		}
		return utilityValues;
	}

	private int min(IStateOBJ state, int depth, int moveMin, int otherMax, int alpha, int beta){
		if(thread.isInterrupted()){ // interrupt if interrupt flag is set
			exec.shutdownNow();
		}
		ArrayList<IStateOBJ> moves = logic.processMoveAlgorithm(state, moveMin, otherMax, depth);
		int result = logic.calcWinner(state, moveMin, otherMax);
		if(depth <= 0 || result != 0 || moves.isEmpty()){
			if(result == otherMax){
				return HIGH_VAL;
			}else if(result == moveMin){
				return LOW_VAL;
			}else if(result == -1){
				return 0;
			}else{
				return utilValue(state, moveMin, otherMax, moveMin);
			}
		}
		int bestUtil = Integer.MAX_VALUE;
		for (int i = 0; i < moves.size(); i++) {
			int util = max(moves.get(i), depth-1, otherMax, moveMin, alpha, beta);
			if(bestUtil > util){bestUtil = util;}
			if(bestUtil < beta){beta = bestUtil;}
			if(beta <= alpha){return bestUtil;} // alpha cut-off
		}return bestUtil;
	}

	private int max(IStateOBJ state, int depth, int moveMax, int otherMin, int alpha, int beta){
		if(thread.isInterrupted()){ // interrupt if interrupt flag is set
			exec.shutdownNow();
		}
		ArrayList<IStateOBJ> moves = logic.processMoveAlgorithm(state, moveMax, otherMin, depth);
		int result = logic.calcWinner(state, moveMax, otherMin);
		if(depth <= 0 || result != 0 || moves.isEmpty()){
			if(result == moveMax){
				return HIGH_VAL;
			}else if(result == otherMin){
				return LOW_VAL;
			}else if(result == -1){
				return 0;
			}else{
				return utilValue(state, moveMax, moveMax, otherMin);
			}
		}
		int bestUtil = Integer.MIN_VALUE;
		for (int i = 0; i < moves.size(); i++) {
			int util = min(moves.get(i), depth-1, otherMin, moveMax, alpha, beta);
			if(bestUtil < util){bestUtil = util;}
			if(bestUtil > alpha){alpha = bestUtil;}
			if(alpha >= beta){return bestUtil;} // beta cut-off
		}return bestUtil;
	}
	
	protected int utilValue(IStateOBJ state, int movingPlayer, int max, int min){
		return logic.evaluation(state, movingPlayer, max, min);
	}
}
