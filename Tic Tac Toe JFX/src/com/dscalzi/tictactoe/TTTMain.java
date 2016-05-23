package com.dscalzi.tictactoe;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

public class TTTMain extends Application{
	
	private static final int DIM = 3;
	
	private volatile boolean order;
	private TTTButton[] buttons;
	
	private List<Integer> wincache;
	
	public static void main(String[] args){
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		goFirst();
		initialize();
		
		/* Primary Scene*/
		GridPane layout = new GridPane();
		layout.setId("gridpane-main");
		layout.setAlignment(Pos.CENTER);
		layout.setVgap(30/DIM);
		layout.setHgap(30/DIM);
		layout.setPadding(new Insets(0,0,0,0));
		for(int i=0; i<buttons.length; ++i)
			layout.add(buttons[i], i%DIM, i/DIM);
		Scene scene = new Scene(layout, 600, 600);
		scene.getStylesheets().add(getClass().getResource("styles/style.css").toExternalForm());
		
		primaryStage.setScene(scene);
		primaryStage.show();
		
		if(!order)
			makeRandomMove();
		
	}
	
	private void initialize(){
		this.wincache = new ArrayList<Integer>();
		this.buttons = new TTTButton[DIM*DIM];
		for(int i=0; i<buttons.length; ++i){
			this.buttons[i] = new TTTButton(this);
			this.buttons[i].setPrefSize(600/DIM, 600/DIM);
			this.buttons[i].getStyleClass().add("tttbutton");
			this.buttons[i].setStyle(this.buttons[i].getStyle() + "-fx-font-size:" + 120/DIM + ";");
		}
	}
	
	public void goFirst(){
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Tic Tac Toe");
		alert.setHeaderText("Would you like to go first?");
		alert.setContentText("Select whether you would like to go first or second. The user that goes first will be X, while the other will be O.");
		
		ButtonType btnTypeFirst = new ButtonType("First");
		ButtonType btnTypeSecond = new ButtonType("Second");
		ButtonType btnTypeCancel = new ButtonType("Quit Game", ButtonData.CANCEL_CLOSE);
		
		alert.getButtonTypes().setAll(btnTypeFirst, btnTypeSecond, btnTypeCancel);
		
		Optional<ButtonType> result = alert.showAndWait();
		result.ifPresent(e -> {
			if(e == btnTypeFirst)
				this.order = true;
			else if(e == btnTypeSecond)
				this.order = false;
			else
				System.exit(0);
		});
		
	}

	public boolean getOrder(){
		return this.order;
	}
	
	public void makeRandomMove(){
		boolean canContinue = false;
		for(TTTButton b : buttons)
			if(b.getState() == TTTButtonState.UNASSIGNED){
				canContinue = true;
				break;
			}
		
		while(canContinue){
			Random rn = new Random();
			int rand = rn.nextInt(buttons.length);
			if(buttons[rand].getState() == TTTButtonState.UNASSIGNED){
				buttons[rand].setState(!order ? TTTButtonState.X : TTTButtonState.O);
				canContinue = false;
			}
		}
		
		Pair<Boolean, TTTButtonState> control = isOver();
		if(control.getKey())
			printWin(control.getValue());
	}
	
	public void printWin(TTTButtonState s){
		
		//Game is over, turn off all buttons.
		for(TTTButton b : buttons)
			b.setDisable(true);
		
		//Player Wins
		if(s == (order ? TTTButtonState.X : TTTButtonState.O)){
			
			for(int i : wincache)
				this.buttons[i].getStyleClass().add("tttbutton-win");
			
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Tic Tac Toe");
			alert.setHeaderText("You win!");
			alert.setContentText("You have beaten the computer!");

			alert.showAndWait();
		}
		
		//Computer Wins
		if(s == (order ? TTTButtonState.O : TTTButtonState.X)){
			
			for(int i : wincache)
				this.buttons[i].getStyleClass().add("tttbutton-lose");
			
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Tic Tac Toe");
			alert.setHeaderText("You lost!");
			alert.setContentText("The computer wins.");

			alert.showAndWait();
		}
		
		if(s == TTTButtonState.UNASSIGNED){
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Tic Tac Toe");
			alert.setHeaderText("No one wins!");
			alert.setContentText("Niether player could get " + DIM + " in a row!");

			alert.showAndWait();
		}
		
	}
	
	
	public Pair<Boolean, TTTButtonState> isOver(){
        //Check Rows
        for(int i=0; i<buttons.length; i+=DIM){
        	int checker = 0;
        	for(int j=i; j<i+DIM; ++j){
        		wincache.add(j);
        		checker += buttons[j].getState().getVal();
        	}
        	if(checker == DIM) return new Pair<Boolean, TTTButtonState>(true, TTTButtonState.X);
        	if(checker == (-1*DIM)) return new Pair<Boolean, TTTButtonState>(true, TTTButtonState.O);
        	wincache.clear();
        }
        
        //Check Columns
        for(int i=0; i<DIM; ++i){
        	int checker = 0;
        	for(int j=i; j<buttons.length; j+=DIM){
        		wincache.add(j);
        		checker += buttons[j].getState().getVal();
        	}
        	if(checker == DIM) return new Pair<Boolean, TTTButtonState>(true, TTTButtonState.X);
        	if(checker == (-1*DIM)) return new Pair<Boolean, TTTButtonState>(true, TTTButtonState.O);
        	wincache.clear();
        }
        
        //Check Diagonal 1
        int diag1Check = 0;
        for(int i=0; i<buttons.length; i+=(DIM+1)){
        	wincache.add(i);
        	diag1Check += buttons[i].getState().getVal();
        }
        if(diag1Check == DIM) return new Pair<Boolean, TTTButtonState>(true, TTTButtonState.X);
    	if(diag1Check == (-1*DIM)) return new Pair<Boolean, TTTButtonState>(true, TTTButtonState.O);
    	wincache.clear();
        
    	//Check Diagonal 2
    	int diag2Check = 0;
    	for(int i=(DIM-1); i<buttons.length-(DIM-1); i+=(DIM-1)){
    		wincache.add(i);
        	diag2Check += buttons[i].getState().getVal();
    	}
    	if(diag2Check == DIM) return new Pair<Boolean, TTTButtonState>(true, TTTButtonState.X);
    	if(diag2Check == (-1*DIM)) return new Pair<Boolean, TTTButtonState>(true, TTTButtonState.O);
    	wincache.clear();
        
        //Check to see if the board is full
        int boardCheck = 0;
        for(TTTButton b : buttons){
            if(b.getState() == TTTButtonState.UNASSIGNED)
                    break;
            ++boardCheck;
        }
        if(boardCheck == buttons.length) return new Pair<Boolean, TTTButtonState>(true, TTTButtonState.UNASSIGNED);
        
        //If no other return statement is fired, the game is not over.
        return new Pair<Boolean, TTTButtonState>(false, null);
    }
	
}
