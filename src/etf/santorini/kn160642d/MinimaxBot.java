package etf.santorini.kn160642d;

import java.util.ArrayList;

public class MinimaxBot extends Bot{
	
	public MinimaxBot(int pid, Game game,Game.Difficulty diff) {
		super(pid,game,diff);
	}

	@Override
	public void makeTheMove() {
		if(game.getPlayerOnMove()!=playerId) return;
		minimax(diffDepth[difficulty.ordinal()]-1,diffDepth[difficulty.ordinal()]-1,Board.currentState,true, playerId,null,null);
	}
	
	private int minimax(int initialDepth, int depth,Board game,boolean isMaximisingPlayer, int playerId, Board prevBoard, Move played) {
	    if(game.isGameOver(playerId)) {
	    	int retval=isMaximisingPlayer? Integer.MIN_VALUE:Integer.MAX_VALUE;
	    	return retval;
	    }
		if (depth == 0) {
			int evaluation=evaluateBoard(prevBoard,played);
	        return evaluation;       
	    }
	    Move bestMove=null;
	    int bestRank;
	    ArrayList<Move> arrls=game.listOfAllMoves(playerId);
	    if (isMaximisingPlayer) {
			bestMove = null; 
			bestRank=Integer.MIN_VALUE;
	        for (Move move: arrls) {	
				Board playedMove=game.newBoardAfterPlayedMove(move);
				int rank = minimax(initialDepth, depth-1, playedMove,!isMaximisingPlayer, playerId==0?1:0, game, move);
				if(rank>bestRank || bestMove==null) {
					bestRank=rank;
					bestMove=move;
				}
	        }
	    } else {
			bestMove= null;	
			bestRank=Integer.MAX_VALUE;
			for (Move move:arrls) {		
				Board playedMove=game.newBoardAfterPlayedMove(move);
				int rank = minimax(initialDepth, depth-1, playedMove,!isMaximisingPlayer, playerId==0?1:0, game, move);
				if(rank<bestRank || bestMove==null) {
					bestRank=rank;
					bestMove=move;
				}
	        }
	    }
	    if(depth==initialDepth) {
	    	this.game.playTheMove(bestMove);
	    	if(Board.numOfPlatesAt(bestMove.to.x, bestMove.to.y)==3) {
				this.game.setGameOver(true);
				this.game.setWinner(this.game.getPlayerOnMove()==0? 1:2);
			}
	    }
        return bestRank;
	}
}
