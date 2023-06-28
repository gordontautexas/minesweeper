package minesweeper;

public class Time {
	private int time;
	public Time() {
		time = 0;
	}
	public int getTime() {
		return time;
	}
	public void secPassed() {
		time++;
	}
	public void resetTime() {
		time = 0;
	}
}
