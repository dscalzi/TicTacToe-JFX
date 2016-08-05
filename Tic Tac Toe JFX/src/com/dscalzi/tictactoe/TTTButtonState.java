package com.dscalzi.tictactoe;

public enum TTTButtonState {

	X(1),
	O(-1),
	UNASSIGNED(0);
	
	private int v;
	
	TTTButtonState(int v){
		this.v = v;
	}
	
	public int getVal(){
		return this.v;
	}
	
}
