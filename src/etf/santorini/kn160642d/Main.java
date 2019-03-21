package etf.santorini.kn160642d;
	

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.VPos;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;


public class Main extends Application {
	public static Move test;
	
	static Stage pStage;
	static Image iBackground;
	static Image iGround;
	static Image[] buttons=null;
	static Image iPlate;
	static Image iFinalPlate;
	
	static final double offsetRatioX=7;
	static final double offsetRatioY=6;
	static final double fieldWidthRatio=8;
	static final double fieldHeightRatio=7;
	static final double buttonWidthRatio = 5;
	static final double buttonHeightRatio= 10;
	static final double startx = 25;
	static final double starty=25;
	static final int plateOffset=5;
	
	private enum Action{START, QUIT, DIFFICULTY, MODE, NONE, WRITE };
	
	private Game game;
	
	private void initElements(Scene scene) {
		buttons=new Image[5];
		try {
			game=new Game();
			iGround=new Image(new FileInputStream("res/img/ground.jpg"));
			iPlate=new Image(new FileInputStream("res/img/ground.jpg"));
			iFinalPlate=new Image(new FileInputStream("res/img/finalPlate.jpg"));
			iBackground=new Image(new FileInputStream("res/img/santorini.jpg"));
			buttons[0]=new Image(new FileInputStream("res/img/startGame.png"));
			buttons[1]=new Image(new FileInputStream("res/img/difficulty.png"));
			buttons[2]=new Image(new FileInputStream("res/img/gameMode.png"));
			buttons[3]=new Image(new FileInputStream("res/img/quitGame.png"));
			buttons[4]=new Image(new FileInputStream("res/img/writeToFile.png"));
			Board.setCurrentState(new Board());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void start(Stage primaryStage) {
		try {
			pStage=primaryStage;
			BorderPane borderPane = new BorderPane();
			
			Pane wrapperPane = new Pane();
		    borderPane.setCenter(wrapperPane);
		    Canvas canvas = new Canvas();
		    wrapperPane.getChildren().add(canvas);
		 // Bind the width/height property to the wrapper Pane
		    canvas.widthProperty().bind(wrapperPane.widthProperty());
		    canvas.heightProperty().bind(wrapperPane.heightProperty());
		    // redraw when resized
		    canvas.widthProperty().addListener(event -> draw(canvas));
		    canvas.heightProperty().addListener(event -> draw(canvas));
		    canvas.setOnMouseClicked(event->{
		    	double x= event.getX();
		    	double y=event.getY();
		    	onClick(canvas, x,y);
		    });

		    canvas.setOnDragOver(new EventHandler<DragEvent>() {
	            @Override
	            public void handle(DragEvent event) {
	                Dragboard db = event.getDragboard();
	                if (db.hasFiles()) {
	                    event.acceptTransferModes(TransferMode.COPY);
	                } else {
	                    event.consume();
	                }
	            }
	            
	        });
	        
	        // Dropping over surface
	        canvas.setOnDragDropped(new EventHandler<DragEvent>() {
	            @Override
	            public void handle(DragEvent event) {
	                Dragboard db = event.getDragboard();
	                boolean success = false;
	                if (db.hasFiles()) {
	                    success = true;
	                    String filePath = null;
	                    readGameFromFile( db.getFiles().get(0));
	                    draw(canvas);
	                }
	                event.setDropCompleted(success);
	                event.consume();
	            }
	        });
		  
		    draw(canvas);		    
			primaryStage.setTitle("Santorini");
			primaryStage.setMinHeight(480);
			primaryStage.setMinWidth(640);
			primaryStage.getIcons().add(new Image("https://image.flaticon.com/icons/png/512/1256/1256055.png"));
			Scene scene = new Scene(borderPane);
	        primaryStage.setScene(scene);
	        
	        primaryStage.show();
			initElements(scene);
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void readGameFromFile(File f) {
		try {
			game.readGameFromFile(f);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void onClick(Canvas canvas, double x, double y) {
		// TODO: actions for buttons and players
		int width = (int) canvas.getWidth();
        int height = (int) canvas.getHeight();
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        int row=getClickedRow(width,height,x,y);
        int column=getClickedColumn(width,height,x,y);
        takeButtonActions(width,height,x,y);
               
        if(column!= -1 && row!=-1) {
        	// TODO: do the selection logic here
        	game.clicked(row, column);
        	
        	draw(canvas);
        }else {
        	
        	draw(canvas);
        }
        
       
	}
	
	private void takeButtonActions(int width,int height,double x,double y) {
		switch(getAction(width,height,x,y)) {
			case NONE: return;
			case START: game.startGame(); break;
			case QUIT: quitGame(); break;
			case DIFFICULTY: game.changeDifficulty(); break;
			case MODE: game.changeGameMode(); break;
			case WRITE: game.writeToFile();
		}
	}
	
	private Action getAction(int width,int height,double x,double y) {	
        double offsetx=width/offsetRatioX;
        double offsety= height/offsetRatioY;
        double w=width/buttonWidthRatio;
        double h=height/buttonHeightRatio;
        double boardEnd = startx+5*offsetx;
        double buttonX= boardEnd+(width-boardEnd)/2 -w/2;
        for(int i=0;i<5;i++) {
        	if(x>=buttonX && x<=buttonX+w && y>=starty+i*offsety && y<=starty+i*offsety+h ) {
        		switch(i) {
	        		case 0: return Action.START;
	        		case 2: return Action.MODE;
	        		case 1: return Action.DIFFICULTY;
	        		case 3: return Action.QUIT;
	        		case 4: return Action.WRITE;
        		}
        	}
        } 	
		return Action.NONE;
	}
	
	private void quitGame() {
		Platform.exit();
	}
	
	private int getClickedRow(int width,int height,double x,double y) {
        double offsety= height/offsetRatioY;
        for(int i=0;i<5;i++) {
        	if(y>=starty+i*offsety && y<=starty+(i+1)*offsety) return i;
        }
        return -1;
	}
	
	private int getClickedColumn(int width,int height,double x,double y) {
		double offsetx=width/offsetRatioX;
        for(int i=0;i<5;i++) {
        	if(x>=startx+i*offsetx && x<=startx+(i+1)*offsetx) return i;
        }
        return -1;
	}
	
	private void draw(Canvas canvas) {	
		int width = (int) canvas.getWidth();
        int height = (int) canvas.getHeight();
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);

        gc.drawImage(iBackground, 0, 0, width, height);
        drawBoard(gc,width,height);
        drawPlayers(gc,width,height);
        drawButtons(gc,width,height);    
        drawStats(gc,width,height);
        
	}
	
	private void drawBoard(GraphicsContext gc, int width,int height) {
        double offsetx=width/offsetRatioX;
        double offsety= height/offsetRatioY;
        double w=width/fieldWidthRatio;
        double h=height/fieldHeightRatio;       
		for(int i=0;i<5;i++) {
        	for(int j=0;j<5;j++) {    
        		int minusOffset=plateOffset;
        		gc.drawImage(iGround, startx+j*offsetx,starty+i*offsety,w,h);
        		int num = Board.numOfPlatesAt(i, j);
        		boolean finalPlate = num==4;
        		while(num!=0) {
        			if(finalPlate&& num==1) {
        				gc.drawImage(iFinalPlate, startx+j*offsetx-minusOffset,starty+i*offsety-minusOffset,w,h);
        				break;
        			}
        			gc.drawImage(iPlate, startx+j*offsetx-minusOffset,starty+i*offsety-minusOffset,w,h);
        			minusOffset+=plateOffset;
        			num--;
        		}
        	}
        }
	}
	
	// Returns left coordinate of the highest plate
	private int getXforRow(int width,int height, int row,int col) {
		return (int)( startx+col*(width/offsetRatioX)-Board.currentState.numOfPlatesAt(row, col)*plateOffset);
	}
	
	// Returns top coordinate of the highest plate
	private int getYforColumn(int width,int height,int row,int col) {
		return (int)(starty + row*(height/offsetRatioY)-Board.currentState.numOfPlatesAt(row, col)*plateOffset);
	}
	
	private void drawPlayers(GraphicsContext gc, int width,int height) {
		if(Board.currentState==null) return;
		Position p11=Board.currentState.PlayerOnePosition1;
		Position p12=Board.currentState.PlayerOnePosition2;
		Position p21=Board.currentState.PlayerTwoPosition1;
		Position p22=Board.currentState.PlayerTwoPosition2;
		double w=(width/fieldWidthRatio)/4;
        double h=(height/fieldHeightRatio)/4;  
        double wp=(width/fieldWidthRatio)/2;
        double hp=(height/fieldHeightRatio)/2;  
        
		if(p11!=null) {
			gc.setFill(Color.RED);
			gc.fillOval(getXforRow(width,height,p11.x,p11.y)+w, getYforColumn(width,height,p11.x,p11.y)+h, wp, hp);
		}else {
			
		}
		if(p12!=null) {
			gc.setFill(Color.RED);
			gc.fillOval(getXforRow(width,height,p12.x,p12.y)+w, getYforColumn(width,height,p12.x,p12.y)+h, wp, hp);
		}else {
			
		}		
		if(p21!=null) {
			gc.setFill(Color.BLUE);
			gc.fillOval(getXforRow(width,height,p21.x,p21.y)+w, getYforColumn(width,height,p21.x,p21.y)+h, wp, hp);
		}else {
			
		}		
		if(p22!=null) {
			gc.setFill(Color.BLUE);
			gc.fillOval(getXforRow(width,height,p22.x,p22.y)+w, getYforColumn(width,height,p22.x,p22.y)+h, wp, hp);
		}else {
			
		}
	}
	
	private void drawButtons(GraphicsContext gc,int width,int height) {
		if(buttons==null) return;
		
        double offsetx=width/offsetRatioX;
        double offsety= height/offsetRatioY;
        double w=width/buttonWidthRatio;
        double h=height/buttonHeightRatio;
        double boardEnd = startx+5*offsetx;
        double buttonX= boardEnd+(width-boardEnd)/2 -w/2;
        for(int i=0;i<5;i++) {
        	gc.drawImage(buttons[i], buttonX, starty+i*offsety, w, h);
        } 
	}
	
	private void drawStats(GraphicsContext gc, int width,int height) {
		if(game==null) return;
		if(game.isGameOver()) {
			gc.setStroke(Color.YELLOW);
			gc.setFont(Font.font("Verdana", FontWeight.BOLD, 40));
			gc.setTextAlign(TextAlignment.CENTER);
	        gc.setTextBaseline(VPos.CENTER);
			gc.setFill(game.getWinner()==1? Color.RED:Color.BLUE);		
			gc.fillText("Game Over! Player "+Integer.toString(game.getWinner())+" won!",width/2,height/2);
			gc.strokeText("Game Over! Player "+Integer.toString(game.getWinner())+" won!",width/2,height/2);
		}
		else {
			gc.setFill(Color.RED);
			gc.setStroke(Color.YELLOW);
			gc.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
			gc.setTextAlign(TextAlignment.CENTER);
			gc.setTextBaseline(VPos.CENTER);
			gc.fillText("Player "+Integer.toString(game.getPlayerOnMove()+1)+" on move! Difficulty: "+game.getDifficultyString()+" Game mode: "+game.getGameModeString(), width/2, height - 20);
			gc.strokeText("Player "+Integer.toString(game.getPlayerOnMove()+1)+" on move! Difficulty: "+game.getDifficultyString()+" Game mode: "+game.getGameModeString(), width/2, height - 20);
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}

// TODO: PROVERA DA LI JE VALIDAN POTEZ!!!
// TODO: PRINTANJE POBEDNIKA POGRESNO
