import java.util.Optional;
import java.util.Random;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

public class Main extends Application implements EventHandler<ActionEvent>{
	
	private static final int DIM = 3;
	
	private volatile boolean order;
	private Button[] buttons;
	
	int[] wincache = new int[DIM];
	
	public static void main(String[] args){
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		goFirst();
		initialize();
		
		/* Primary Scene*/
		GridPane layout = new GridPane();
		layout.setStyle("-fx-background-color: black");
		layout.setAlignment(Pos.CENTER);
		layout.setVgap(10);
		layout.setHgap(10);
		layout.setPadding(new Insets(0,0,0,0));
		for(int i=0; i<buttons.length; ++i){
			layout.add(buttons[i], i/DIM, i%DIM);
		}
		
		Scene scene = new Scene(layout, 600, 600);
		
		primaryStage.setScene(scene);
		primaryStage.show();
		
		if(!order)
			makeRandomMove();
		
	}
	
	private void initialize(){
		this.buttons = new Button[DIM*DIM];
		for(int i=0; i<buttons.length; ++i){
			this.buttons[i] = new Button("-");
			this.buttons[i].setPrefSize(600/DIM, 600/DIM);
			this.buttons[i].setStyle("-fx-font-size:" + 120/DIM + "; -fx-base: #FFFFFF;");
			this.buttons[i].setOnAction(this);
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

	@Override
	public void handle(ActionEvent e) {
		Button b = (Button)e.getSource();
		b.setText(order ? "X" : "O");
		disableButton(b);
		
		Pair<Boolean, String> control = isOver();
		
		if(!control.getKey())
			makeRandomMove();
		else
			printWin(control.getValue());
			
	}
	
	public void makeRandomMove(){
		boolean canContinue = false;
		for(Button b : buttons){
			if(!(b.isDisabled())){
				canContinue = true;
				break;
			}
		}
		while(canContinue){
			Random rn = new Random();
			int rand = rn.nextInt(buttons.length);
			if(!(buttons[rand].isDisabled())){
				buttons[rand].setText(!order ? "X" : "O");
				disableButton(buttons[rand]);
				canContinue = false;
			}
		}
		
		Pair<Boolean, String> control = isOver();
		if(control.getKey())
			printWin(control.getValue());
	}
	
	public void disableButton(Button b){
		b.setDisable(true);
		b.setStyle("-fx-opacity: 1.0; -fx-font-size:" + 120/DIM + "; -fx-base: #FFFFFF;");
	}
	
	public void printWin(String s){
		
		for(Button b : buttons)
			disableButton(b);
		
		//Player Wins
		if((order && s.equals("X")) || (!order && s.equals("O"))){
			
			for(int i : wincache)
				this.buttons[i].setStyle(this.buttons[i].getStyle() + "-fx-base: #DDFFDB;");
			
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Tic Tac Toe");
			alert.setHeaderText("You win!");
			alert.setContentText("You have beaten the computer!");

			alert.showAndWait();
		}
		
		//Computer Wins
		if((order && s.equals("O")) || (!order && s.equals("X"))){
			
			for(int i : wincache)
				this.buttons[i].setStyle(this.buttons[i].getStyle() + "-fx-base: #FFDBDD;");
			
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Tic Tac Toe");
			alert.setHeaderText("You lost!");
			alert.setContentText("The computer wins.");

			alert.showAndWait();
		}
		
		if(s.equals("FULL")){
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Tic Tac Toe");
			alert.setHeaderText("No one wins!");
			alert.setContentText("Niether player could get " + DIM + " in a row!");

			alert.showAndWait();
		}
		
	}
	
	public Pair<Boolean, String> isOver(){
        int checker;
        //Check Columns
        for(int i=0; i<buttons.length-(DIM-1); i+=DIM){
        	checker = 0;
        	if(!(buttons[i].getText().equals("-"))){
        		String str = buttons[i].getText();
        		for(int z=0; z<DIM; ++z){
        			wincache[checker] = i+z;
        			if(buttons[i+z].getText().equals(str))
        				++checker;
        		}
        		if(checker == DIM){
        			return new Pair<Boolean, String>(true, str);
        		}
        	}
        }
        
        //Check Rows
        for(int i=0; i<DIM; ++i){
        	checker = 0;
        	if(!(buttons[i].getText().equals("-"))){
        		String str = buttons[i].getText();
        		for(int z=i; z<buttons.length; z+=DIM){
        			wincache[checker] = z;
        			if(buttons[z].getText().equals(str))
        				++checker;
        		}
        		if(checker == DIM){
        			return new Pair<Boolean, String>(true, str);
        		}
        	}
        }
        
        //Check diagonal 1
        checker = 0;
        String str1 = buttons[0].getText();
        for(int i=0; i<buttons.length; i+=(DIM+1)){
            if(!(buttons[i].getText().equals("-"))){
            	if(buttons[i].getText().equals(str1)){
            		wincache[checker] = i;
            		++checker;
            	}
            }
        }
        if(checker == DIM){
    		return new Pair<Boolean, String>(true, str1);
		}
        
        //Check diagonal 2
        checker = 0;
        String str2 = buttons[DIM-1].getText();
        for(int i=(DIM-1); i<buttons.length-(DIM-1); i+=(DIM-1)){
            if(!(buttons[i].getText().equals("-"))){
            	if(buttons[i].getText().equals(str2)){
            		wincache[checker] = i;
            		++checker;
            	}
            }
        }
        if(checker == DIM){
			return new Pair<Boolean, String>(true, str2);
		}
        
        //Check to see if the board is full
        checker = 0;
        for(Button b : buttons){
            if(!(b.isDisabled()))
                    break;
            ++checker;
        }
	
        if(checker == buttons.length)
        	return new Pair<Boolean, String>(true, "FULL");
        
        return new Pair<Boolean, String>(false, null);
    }
	
}
