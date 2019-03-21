package etf.santorini.kn160642d;

import java.util.ArrayList;
import etf.santorini.kn160642d.Game.Difficulty;

public class AlphaBetaBot extends Bot {

	public AlphaBetaBot(int p, Game g, Difficulty diff) {
		super(p, g, diff);
	}

	@Override
	public void makeTheMove() {
		if(game.getPlayerOnMove()!=playerId) return;
		alphaBetaMinimax(Integer.MIN_VALUE,Integer.MAX_VALUE,diffDepth[difficulty.ordinal()],diffDepth[difficulty.ordinal()],Board.currentState,true, playerId,null,null);
	}

	private int alphaBetaMinimax(int alpha, int beta, int initialDepth, int depth,Board game,boolean isMaximisingPlayer, int playerId, Board prevBoard, Move played) {
	    if(game.isGameOver(playerId)) {
	    	return isMaximisingPlayer? Integer.MIN_VALUE:Integer.MAX_VALUE;
	    }
		if (depth == 0) {
	        return improvedEvaluation(game,played);
	    }
		int nAlpha=Integer.MIN_VALUE;
		int nBeta=Integer.MAX_VALUE;
	    Move bestMove=null;
	    int bestRank;
	    ArrayList<Move> arrls=game.listOfAllMoves(playerId);
	    if (isMaximisingPlayer) {
			bestMove = null; 
			bestRank=Integer.MIN_VALUE;
	        for (Move move: arrls) {	
				Board playedMove=game.newBoardAfterPlayedMove(move);
				int rank = alphaBetaMinimax(Integer.max(alpha, nAlpha),beta,initialDepth, depth-1, playedMove,!isMaximisingPlayer, playerId==0?1:0, game, move);
				nAlpha=Integer.max(nAlpha, rank);
				if(rank>bestRank || bestMove==null) {
					bestRank=rank;
					bestMove=move;
				}
				if(nAlpha>=beta) {
					bestRank=nAlpha;
					bestMove=move;
					break;
				}
	        }
	    } else {
			bestMove= null;	
			bestRank=Integer.MAX_VALUE;
			for (Move move:arrls) {		
				Board playedMove=game.newBoardAfterPlayedMove(move);
				int rank = alphaBetaMinimax(alpha,Integer.min(beta, nBeta),initialDepth, depth-1, playedMove,!isMaximisingPlayer, playerId==0?1:0, game, move);
				nBeta=Integer.min(nBeta, rank);
				if(rank<bestRank || bestMove==null) {
					bestRank=rank;
					bestMove=move;
				}
				if(alpha>=nBeta) {
					bestRank=nAlpha;
					bestMove=move;
					break;
				}
	        }
	    }
	    if(depth==initialDepth) {
	    	this.game.playTheMove(bestMove);
	    	if(Board.numOfPlatesAt(bestMove.to.x, bestMove.to.y)==3) {		
				this.game.setWinner(this.game.getPlayerOnMove()==0? 1:2);
				this.game.setGameOver(true);
			}
	    }
        return bestRank;
	}
	
}
