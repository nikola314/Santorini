package etf.santorini.kn160642d;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.canvas.GraphicsContext;

public class Board {
	public static Board currentState=null;
	private byte board[][];
	
	private static final int evaluationDegree=10;
	
	public Position PlayerOnePosition1=null;
	public Position PlayerTwoPosition1=null;
	public Position PlayerOnePosition2=null;
	public Position PlayerTwoPosition2=null;	
	
	public Board() {
		board=new byte[5][5];
		for(int i=0;i<5;i++)
			for(int j=0;j<5;j++) {
				board[i][j]=0;
			}
	}
	
	public static void setCurrentState(Board board) {
		currentState=board;
	}
	
	public static byte numOfPlatesAt(int r,int c) {
		if(r<0 || r>4 || c<0 || c>4) return 1;
		if(currentState!=null) return currentState.board[r][c];
		return 0;
	}
	
	public byte numOfPlates(int r,int c) {
		if(r<0 || r>4 || c<0 || c>4) return 0;
		return board[r][c];
	}
	
	public void addPlate(int r,int c) {
		if(r<0 || r>4 || c<0 || c>4) return;
		if(board[r][c]<4) board[r][c]++;
	}
	
	public boolean emptyField(int r, int c) {
		if(PlayerOnePosition1 != null) {
			if(PlayerOnePosition1.on(r,c)) return false;
		}
		if(PlayerOnePosition2 != null) {
			if(PlayerOnePosition2.on(r,c)) return false;
		}
		if(PlayerTwoPosition1 != null) {
			if(PlayerTwoPosition1.on(r,c)) return false;
		}
		if(PlayerTwoPosition2 != null) {
			if(PlayerTwoPosition2.on(r,c)) return false;
		}
		return true;
	}
	
	public boolean emptyFieldHypothetical(int r, int c, Position previous) {
		if(PlayerOnePosition1 != null) {
			if(PlayerOnePosition1.on(r,c) && !PlayerOnePosition1.equals(previous)) return false;
		}
		if(PlayerOnePosition2 != null) {
			if(PlayerOnePosition2.on(r,c) && !PlayerOnePosition2.equals(previous)) return false;
		}
		if(PlayerTwoPosition1 != null) {
			if(PlayerTwoPosition1.on(r,c) && !PlayerTwoPosition1.equals(previous)) return false;
		}
		if(PlayerTwoPosition2 != null) {
			if(PlayerTwoPosition2.on(r,c) && !PlayerTwoPosition2.equals(previous)) return false;
		}
		return true;
	}
	
	public int distance(Position p1, Position p2) {
		return Math.abs(p1.x-p2.x)+Math.abs(p1.y-p2.y);
	}
	
	public int evaluateMinimaxMove(Position destination,Position plateDestination, int pid) {
		int m= board[destination.x][destination.y];
		int p1Dist=distance(PlayerOnePosition1,plateDestination)+distance(PlayerOnePosition2,plateDestination);
		int p2Dist=distance(PlayerTwoPosition1,plateDestination)+distance(PlayerTwoPosition2,plateDestination);
		int diff= pid==0? p1Dist-p2Dist:p2Dist-p1Dist;
		int l= diff*board[plateDestination.x][plateDestination.y];
		return m+l;
	}
	
	public int improvedEvaluation(Position destination, Position plateDestination,int pid) {	
		int p1Evaluation= (int) Math.pow(evaluationDegree,numOfPlates(PlayerOnePosition1.x,PlayerOnePosition1.y)+2);
		p1Evaluation = p1Evaluation+ (int) Math.pow(evaluationDegree,numOfPlates(PlayerOnePosition2.x,PlayerOnePosition2.y)+2);
		
		int p2Evaluation= (int) Math.pow(evaluationDegree,numOfPlates(PlayerTwoPosition1.x,PlayerTwoPosition1.y)+2);
		p2Evaluation = p2Evaluation+ (int) Math.pow(evaluationDegree,numOfPlates(PlayerTwoPosition2.x,PlayerTwoPosition2.y)+2);
		
		Position opponentp1=pid==0?PlayerOnePosition1:PlayerTwoPosition1;
		Position opponentp2=pid==0?PlayerTwoPosition1:PlayerTwoPosition1;
		
	/*	if(numOfPlates(plateDestination.x, plateDestination.y)>=2 && unreachable(plateDestination,opponentp1,opponentp2))
			if(pid==0) {
				p1Evaluation*=2;
			}
			else {
				p2Evaluation*=2;
			}*/
	//	p1Evaluation+= fieldsAroundEvaluation(PlayerOnePosition1, PlayerOnePosition2);
	//	p2Evaluation+= fieldsAroundEvaluation(PlayerTwoPosition1, PlayerTwoPosition2);
			
		return pid==0?p1Evaluation-p2Evaluation:p2Evaluation-p1Evaluation;
	}
	
	private boolean unreachable(Position plate, Position p1,Position p2) {
		int diffx=Math.abs(plate.x-p1.x);
		int diffy=Math.abs(plate.y-p1.y);
		if(diffx<=1 || diffy<=1) return false;
		diffx=Math.abs(plate.x-p2.x);
		diffy=Math.abs(plate.y-p2.y);
		if(diffx<=1 || diffy<=1) return false;
		return true;
	}

	private int fieldsAroundEvaluation(Position p1, Position p2) {
		int p1Size=board[p1.x][p1.y];
		int p2Size=board[p2.x][p2.y];
		int retval=0;
		for(int i=-1;i<=1;i++) {
			for(int j=-1;j<=1;j++) {
				if(p1.x + i <0 || p1.x+i >3 || p2.x+i<0 || p2.x+i>3 || p1.y +j <0 || p1.y+j>3 || p2.y+j<0 || p2.y+j>3) continue;
				if(board[p1.x+i][p1.y+j]>=p1Size) retval++;
				if(board[p2.x+i][p2.y+j]>=p2Size) retval++;		
			}
		}
		
		return (int) Math.pow(evaluationDegree, retval/3);
	}
	
	public ArrayList<Move> listOfAllMoves(int playerId) {
		Position pos[]= {playerId==0?PlayerOnePosition1:PlayerTwoPosition1,playerId==0?PlayerOnePosition2:PlayerTwoPosition2};
		ArrayList<Move> moves=new ArrayList<>();
		for(int k=0;k<2;k++) {
			Position p=pos[k];
			for(int i=-1;i<=1;i++) {
				for(int j=-1;j<=1;j++) {
					if(p.x+i <0 || p.x+i >4) continue;
					if(p.y+j <0 || p.y+j>4) continue;
					if(i==0 && j==0) continue;
					if(board[p.x+i][p.y+j]>3) continue;
					Position np= new Position((byte)(p.x+i),(byte)(p.y+j));
					if(!Game.isPossibleMove(this, p, np)) continue;
					
					for(int ii=-1;ii<=1;ii++) {
						for(int jj=-1;jj<=1;jj++) {
							if(np.x+ii <0 || np.x+ii >4) continue;
							if(np.y+jj <0 || np.y+jj>4) continue;
							if(board[np.x+ii][np.y+jj]>3) continue;
							if(ii==0 && jj==0) continue;
													
							Position platePos= new Position((byte)(np.x+ii),(byte)(np.y+jj));
							
							if(!canPutAPlateHypothetical(this, np,platePos, p)) continue;
							moves.add(new Move(p,np,platePos,playerId));
						}
					}
				}
			}		
		}
		return moves;
	}

	private boolean canPutAPlateHypothetical(Board board, Position from, Position to, Position earlier) {
		double rowDiff=Math.abs(from.x-to.x);
		double colDiff=Math.abs(from.y-to.y);
		if(rowDiff>1 || colDiff>1 || (rowDiff==0 && colDiff==0)) return false;
		if(!board.emptyFieldHypothetical(to.x, to.y,earlier)) return false;
		if(Board.numOfPlatesAt(to.x, to.y)>3) return false;
		return true;
	}
	
	public Board newBoardAfterPlayedMove(Move move) {
		Board b=null;
		b=(Board)this.clone();
		Position p1 = move.playerId==0?b.PlayerOnePosition1:b.PlayerTwoPosition1;
		Position p2 = move.playerId==0?b.PlayerOnePosition2:b.PlayerTwoPosition2;
		Position p;
		if(p1.equals(move.from))p=p1;
		else p=p2;
		p.x=move.to.x;
		p.y=move.to.y;
		b.board[move.plateField.x][move.plateField.y]++;
		return b;
	}	
	
	public Board clone() {
		Board b=new Board();
		for(int i=0;i<5;i++) {
			for(int j=0;j<5;j++)
				b.board[i][j]=board[i][j];
		}
		b.PlayerOnePosition1=new Position(PlayerOnePosition1);
		b.PlayerOnePosition2=new Position(PlayerOnePosition2);
		b.PlayerTwoPosition1=new Position(PlayerTwoPosition1);
		b.PlayerTwoPosition2=new Position(PlayerTwoPosition2);
		return b;
	}

	public boolean canMove(int pid) {
		ArrayList<Move> list=this.listOfAllMoves(pid);
		if(list.size()==0) return false;
		return true;
		
	}
	
	public boolean isGameOver(int pid) {
		if(PlayerOnePosition1==null || PlayerOnePosition2==null || PlayerTwoPosition1==null || PlayerTwoPosition2==null) return false;
		if(board[PlayerOnePosition1.x][PlayerOnePosition1.y]==3) return true;
		if(board[PlayerOnePosition2.x][PlayerOnePosition2.y]==3) return true;
		if(board[PlayerTwoPosition1.x][PlayerTwoPosition1.y]==3) return true;
		if(board[PlayerTwoPosition2.x][PlayerTwoPosition2.y]==3) return true;
		if(!canMove(pid)) return true;
		return false;
	}
	
}

class Position {
	public byte x;
	public byte y;
	Position(byte xx,byte yy){
		x=xx;
		y=yy;
	}
	Position(Position p){
		x=p.x;
		y=p.y;
	}
	
	public boolean on(int r,int c) {
		return (x==r && y==c);
	}
	
	public boolean equals(Position p) {
		return (x==p.x && y==p.y);
	}
	public void move(Position p) {
		x=p.x;
		y=p.y;
	}
	public String toString() {
		String s="";
		s+=Character.toString((char) ('A'+x));
		s+=Integer.toString(y);
		return s;
	}
	public Position clone() {
		return new Position(x,y);
	}
}

class Move{
	public Position from;
	public Position to;
	public Position plateField;
	public int playerId;
	public Move(Position f, Position t, Position p, int player) {
		from=new Position(f.x,f.y);
		to=new Position(t.x,t.y);
		plateField=new Position(p.x,p.y);
		playerId=player;
	}
	public Move clone() {
		return new Move(new Position(from.x,from.y),new Position(to.x,to.y),new Position(plateField.x,plateField.y),playerId);
	}
}