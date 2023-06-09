package minesweeper;

public class Tile {
	private int x;
	private int y;
	private int adjMines;
	private boolean isMine;
	private boolean isRevealed;
	private boolean isFlagged;
	
	public Tile(int x, int y) {
		this.x = x;
		this.y = y;
		adjMines = 0;
		isMine = true;
		isRevealed = false;
		isFlagged = false;
	}
	
	public void setAdjMines(int numMines) {
		adjMines += numMines;
	}
	
	public void deMine() {
		isMine = false;
	}
	
	public boolean getMine() {
		return isMine;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public boolean isRevealed() {
		return isRevealed;
	}
	
	public boolean isFlagged() {
		return isFlagged;
	}
	
	public int getAdjMines() {
		return adjMines;
	}
	
	public void reveal() {
		isRevealed = true;
	}
	
	public void toggleFlag() {
		if(isFlagged) {
			isFlagged = false;
		}
		else {
			isFlagged = true;
		}
	}
}
