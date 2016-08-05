package com.dscalzi.tictactoe;

import javafx.scene.control.Button;
import javafx.util.Pair;

public class TTTButton extends Button{

	private TTTMain game;
	private TTTButtonState state;
	
	public TTTButton(TTTMain game){
		super("-");
		this.state = TTTButtonState.UNASSIGNED;
		this.game = game;
		this.assignHandle();
	}
	
	private void assignHandle(){
		this.setOnAction(e -> {
			
			if(game.getOrder()) this.setState(TTTButtonState.X);
			else this.setState(TTTButtonState.O);
			
			Pair<Boolean, TTTButtonState> control = game.isOver();
			
			if(!control.getKey()) game.makeRandomMove();
			else game.printWin(control.getValue());
			
		});
	}
	
	public void setState(TTTButtonState s){
		this.state = s;
		switch(s){
		case X:
			this.setText("X");
			this.setDisable(true);
			break;
		case O:
			this.setText("O");
			this.setDisable(true);
			break;
		case UNASSIGNED:
			this.setText("-");
			this.setDisable(false);
			break;
		}
	}
	
	public TTTButtonState getState(){
		return this.state;
	}
}
