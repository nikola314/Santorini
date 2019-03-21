package etf.santorini.kn160642d;

public abstract class Bot {
	protected int playerId; // 0 | 1
	protected Game game;
	Game.Difficulty difficulty;
	int diffDepth[]= {3,4,5};
	
	public Bot(int p,Game g, Game.Difficulty diff) {
		playerId=p;
		game=g;
		difficulty=diff;
	}	
	
	protected boolean isMyTurn() {
		if(game.isGameOver()) return false;
		if(game.moveNum%2 != playerId-1) return false;
		return true;
	}

	protected int evaluateBoard(Board prevBoard, Move move) {
		return prevBoard.evaluateMinimaxMove(move.to, move.plateField, move.playerId);
	}
	
	protected int improvedEvaluation(Board game, Move move) {
		return game.improvedEvaluation(move.to, move.plateField, move.playerId==0?1:0);
	}
	
	public abstract void makeTheMove();
}
