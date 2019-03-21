package etf.santorini.kn160642d;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Game {
	public int moveNum=0;
	private boolean playerOneTurn=true;
	private enum GameMode {PvsP, BvsB, PvsB};
	public enum Difficulty{EASY,MEDIUM,HARD};
	private GameMode gameMode;
	private Difficulty difficulty=Difficulty.EASY;
	private int selected=0;
	private Position lastPos=null;
	private Position moved=null;
	private boolean gameOver=false;
	private int winner=0;
	public Bot bot=null;
	public Bot bot1=null;
	private static String rowStrings[]= {"0", "1","2","3","4"};
	private static String colStrings[]= {"A","B","C","D","E"};
	private StringBuilder builder;
	private Bot bots[]=null;
	
	public Game() {
		gameMode=GameMode.PvsP;
		startGame();
		bot=new MinimaxBot(1,this,Difficulty.EASY);
	}
	
	public int getPlayerOnMove() {
		if(playerOneTurn) return 0;
		else return 1;
	}
	
	public void setGameOver(boolean b) {
		gameOver=b;
	}
	
	public void setWinner(int winner) {
		this.winner=winner;
	}
	
	public String getDifficultyString() {
		if(gameMode.equals(GameMode.PvsP) || bot==null) return "/";
		String s="";
		if(bot instanceof MinimaxBot) s="Minimax";
		else if(bot instanceof AlphaBetaBot) s="AlphaBeta";
		switch(difficulty) {
			case EASY: return s+"(EASY)";
			case MEDIUM: return s+"(MEDIUM)";
			case HARD: return s+"(HARD)";
		}		
		return "";
	}
	
	public String getGameModeString() {
		switch(gameMode) {
			case PvsP: return "PvsP";
			case BvsB: return "BvsB";
			case PvsB: return "PvsB";
		}
		return "";
	}
	
	public void startGame() {
		Board.setCurrentState(new Board());
		moveNum=0;
		winner=0;
		gameOver=false;
		selected=0;
		playerOneTurn=true;
		moved=null;
		builder=new StringBuilder();
	}
	
	public void writeToFile() {
		//TODO: implement writing from builder!
		File file = new File("output_log.txt");
		BufferedWriter writer = null;
		try {
		    writer = new BufferedWriter(new FileWriter(file));
		    writer.write(builder.toString());
		} 
		catch(Exception e) {
			
		}
		finally {
		    if (writer != null)
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	public void changeDifficulty() {
		boolean switchBot=false;
		if(difficulty.ordinal()==2) switchBot=true;
		difficulty= Difficulty.values()[(difficulty.ordinal()+1)%3];
		if(gameMode==GameMode.PvsB) {
			if(switchBot) {
				if(bot instanceof MinimaxBot){
					bot=new AlphaBetaBot(1,this,difficulty);
				}
				else bot=new MinimaxBot(1,this,difficulty);
			}
			else {
				if(bot instanceof MinimaxBot){
					bot=new MinimaxBot(1,this,difficulty);
				}
				else bot=new AlphaBetaBot(1,this,difficulty);
			}
		}
		else {
			if(switchBot) {
				if(bot instanceof MinimaxBot) {
					bot=new AlphaBetaBot(0,this,difficulty);
					bot1=new AlphaBetaBot(1,this,difficulty);
				} else {
					bot=new MinimaxBot(0,this,difficulty);
					bot1=new MinimaxBot(1,this,difficulty);
				}
			}
			else {
				if(bot instanceof MinimaxBot) {
					bot=new MinimaxBot(0,this,difficulty);
					bot1=new MinimaxBot(1,this,difficulty);
				} else {
					bot=new AlphaBetaBot(0,this,difficulty);
					bot1=new AlphaBetaBot(1,this,difficulty);
				}
			}

		}
	}
	

	
	public void changeGameMode() {
		gameMode= GameMode.values()[(gameMode.ordinal()+1)%3];
		if(gameMode==GameMode.PvsB) {
			bot=new MinimaxBot(1,this,difficulty);
		}
		else {
			bot=new MinimaxBot(0,this,difficulty);
			bot1=new MinimaxBot(1,this,difficulty);
		}
	}
	
	public void clicked(int row, int column) {
		if(gameOver) return;
		if(row==-1 || column==-1) return;
		int prevMove=moveNum;
		if((gameMode==GameMode.PvsB) && playerOneTurn) {
			playerVsBot(row,column);
		}
		else if(gameMode==GameMode.PvsP) {
			playerVsPlayer(row,column);
		}
		if(gameMode==GameMode.BvsB) {
			botVsBot(row,column);
		}
		if(moveNum==4 && moveNum!=prevMove) writeStartPositions();
	}
	
	private void playerVsBot(int clickedRow, int clickedCol) {
		playerVsPlayer(clickedRow,clickedCol);
		if(!playerOneTurn) bot.makeTheMove();
		checkForGameOver();
	}
	
	private void playerVsPlayer(int clickedRow,int clickedCol) {
		if(moveNum<4) {
			if(!Board.currentState.emptyField(clickedRow,clickedCol)) return;
			switch(moveNum) {
				case 0: Board.currentState.PlayerOnePosition1=new Position((byte)clickedRow,(byte)clickedCol); break;
				case 1: Board.currentState.PlayerOnePosition2=new Position((byte)clickedRow,(byte)clickedCol); break;
				case 2: Board.currentState.PlayerTwoPosition1=new Position((byte)clickedRow,(byte)clickedCol); break;
				case 3: Board.currentState.PlayerTwoPosition2=new Position((byte)clickedRow,(byte)clickedCol); break;
			}
			moveNum++;
			return;
		}
		else {
			if(moved==null) {
				if(selected==0) {
					if(Board.currentState.emptyField(clickedRow, clickedCol)) return;
					if(playerOneTurn) {
						if(Board.currentState.PlayerOnePosition1.on(clickedRow, clickedCol)) {
							selected=1;
							lastPos=Board.currentState.PlayerOnePosition1;
						}
						else if(Board.currentState.PlayerOnePosition2.on(clickedRow, clickedCol)) {
							selected=2;
							lastPos=Board.currentState.PlayerOnePosition2;
						}
					}
					else {
						if(Board.currentState.PlayerTwoPosition1.on(clickedRow, clickedCol)) {
							selected=1;
							lastPos=Board.currentState.PlayerTwoPosition1;
						}
						else if(Board.currentState.PlayerTwoPosition2.on(clickedRow, clickedCol)) {
							selected=2;
							lastPos=Board.currentState.PlayerTwoPosition2;
						}
					}
				}
				else {
					if(possibleMove(clickedRow,clickedCol)) {
						moved=new Position((byte)clickedRow,(byte)clickedCol);
						if(playerOneTurn) {
							if(selected==1) {
								Board.currentState.PlayerOnePosition1=new Position((byte)clickedRow,(byte)clickedCol);					
							}
							else {
								Board.currentState.PlayerOnePosition2=new Position((byte)clickedRow,(byte)clickedCol);
							}
						}
						else {
							if(selected==1) {
								Board.currentState.PlayerTwoPosition1=new Position((byte)clickedRow,(byte)clickedCol);
							}
							else {
								Board.currentState.PlayerTwoPosition2=new Position((byte)clickedRow,(byte)clickedCol);
							}
						}
						if(Board.numOfPlatesAt(clickedRow, clickedCol)==3) {
							gameOver=true;
							winner= playerOneTurn? 1:2;
						}
					}
					selected=0;
				}
			}
			else {
				if(canPutAPlate(Board.currentState,moved,new Position((byte)clickedRow,(byte)clickedCol))) {
					writeMove(new Move(lastPos,moved,new Position((byte)clickedRow,(byte)clickedCol),playerOneTurn?0:1));
					moved=null;
					Board.currentState.addPlate(clickedRow, clickedCol);
					selected=0;
					moveNum++;				
					playerOneTurn=!playerOneTurn;
				//	checkForGameOver();
				}
			}
		}
	}
	
	private void botVsBot(int row,int col) {
		if(moveNum<4) {
			if(!Board.currentState.emptyField(row,col)) return;
			switch(moveNum) {
				case 0: Board.currentState.PlayerOnePosition1=new Position((byte)row,(byte)col); break;
				case 1: Board.currentState.PlayerOnePosition2=new Position((byte)row,(byte)col); break;
				case 2: Board.currentState.PlayerTwoPosition1=new Position((byte)row,(byte)col); break;
				case 3: Board.currentState.PlayerTwoPosition2=new Position((byte)row,(byte)col); break;
			}
			moveNum++;
			return;
		}		
		if(playerOneTurn) bot.makeTheMove();
		else bot1.makeTheMove();
		checkForGameOver();
	}
	
	private void checkForGameOver() {
		if(Board.currentState.isGameOver(playerOneTurn?0:1)) {
			gameOver=true;
			winner=playerOneTurn?2:1;
		}
	}
	
	private void writeStartPositions() {
		Board board=Board.currentState;
		builder.append(board.PlayerOnePosition1.toString()).append(" ")
		.append(board.PlayerOnePosition2.toString()).append(System.lineSeparator());
		builder.append(board.PlayerTwoPosition1.toString()).append(" ")
		.append(board.PlayerTwoPosition2.toString()).append(System.lineSeparator());
	}
	
	private void writeMove(Move move) {
		builder.append(move.from.toString()+" ").append(move.to.toString()+" ").append(move.plateField.toString()).append(System.lineSeparator());
	}
	
	private boolean possibleMove(int row,int col) {
		if(selected==0) return false;
		
		Position position = playerOneTurn?
				selected==1?
						Board.currentState.PlayerOnePosition1:
						Board.currentState.PlayerOnePosition2:
				selected==1?
						Board.currentState.PlayerTwoPosition1:
						Board.currentState.PlayerTwoPosition2;
		
		return isPossibleMove(Board.currentState,position,new Position((byte)row,(byte) col));
	}
	
	public static boolean isPossibleMove(Board board, Position from, Position to) {
		double rowDiff=Math.abs(from.x-to.x);
		double colDiff=Math.abs(from.y-to.y);
		if(rowDiff>1 || colDiff>1 || (rowDiff==0 && colDiff==0)) return false;
		if(board.numOfPlates(to.x, to.y)>board.numOfPlates(from.x, from.y)+1) return false;
		if(!board.emptyField(to.x, to.y))  return false;
	//	if(!existsFieldToPutAPlate(board, to.x,to.y,from)) return false;
		return true;
	}
	
	// Ista kao i metoda ispod samo ne gleda da li vec ima neka figura nat om polju
	private static boolean mightPutAPlate(Board board, Position from, Position to) {
		double rowDiff=Math.abs(from.x-to.x);
		double colDiff=Math.abs(from.y-to.y);
		if(rowDiff>1 || colDiff>1 || (rowDiff==0 && colDiff==0)) return false;
		if(Board.numOfPlatesAt(to.x, to.y)>3) return false;	
		return true;
	}
	
	public static boolean canPutAPlate(Board board, Position from, Position to) {
		double rowDiff=Math.abs(from.x-to.x);
		double colDiff=Math.abs(from.y-to.y);
		if(rowDiff>1 || colDiff>1 || (rowDiff==0 && colDiff==0)) return false;
		if(!board.emptyField(to.x, to.y)) return false;
		if(Board.numOfPlatesAt(to.x, to.y)>3) return false;
		return true;
	}
	
	public boolean isGameOver() {
		return gameOver;
	}
	
	public int getWinner() {
		return winner;
	}
	
	public void playTheMove(Move mov) {
		Move move=mov.clone();
		if(move.playerId!= (playerOneTurn?0:1)) return;
		if(!isPossibleMove(Board.currentState,move.from,move.to)) return;
		if(!mightPutAPlate(Board.currentState,move.to,move.plateField)) return;
		if(move.playerId==0) {
			if(Board.currentState.PlayerOnePosition1.equals(move.from)) {
				Board.currentState.PlayerOnePosition1.move(move.to);
			}
			else {
				Board.currentState.PlayerOnePosition2.move(move.to);
			}
		}
		else {
			if(Board.currentState.PlayerTwoPosition1.equals(move.from)) {
				Board.currentState.PlayerTwoPosition1.move(move.to);
			}
			else {
				Board.currentState.PlayerTwoPosition2.move(move.to);
			}
		}
		Board.currentState.addPlate(move.plateField.x, move.plateField.y);
		playerOneTurn=!playerOneTurn;
		writeMove( move);
	}
	
	public void readGameFromFile(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader (file));
	    String         line = null;
	    try {
	    	startGame();
	        while((line = reader.readLine()) != null) {
	        	String [] sep= line.split(" ");
	        	if(moveNum<4) {
	        		Position p1=decodePosition(sep[0]);
	        		Position p2=decodePosition(sep[1]);
	        		if(moveNum<2) {
		        		Board.currentState.	PlayerOnePosition1=p1;
		        		Board.currentState.	PlayerOnePosition2=p2;
	        		}
	        		else {
	        	 		Board.currentState.	PlayerTwoPosition1=p1;
		        		Board.currentState.	PlayerTwoPosition2=p2;
	        		}  		
	        		moveNum++;
	        		playerOneTurn=!playerOneTurn;
	        	}else {
	        		Position[] pos=new Position[3];
	        		for(int i=0;i<3;i++) {
	        			pos[i]=decodePosition(sep[i]);
	        		}
	        		Move move=new Move(pos[0],pos[1],pos[2],playerOneTurn?0:1);
	        		playTheMove(move);	        	
	        	}        	
	        	moveNum++;
	        }        
	    } finally {
				reader.close();
	    }    
	}
	
	private Position decodePosition(String s) {
		int x,y;
		x=Math.abs('A'-s.charAt(0));
		y= Integer.parseInt(Character.toString(s.charAt(1)));
		return new Position((byte)x,(byte)y);
	}

}
