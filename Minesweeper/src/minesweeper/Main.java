package minesweeper;

public class Main {

	public static void main(String[] args) {
		Tile[][] board = new Tile[30][16];
		int cols = 30;
		int rows = 16;
		for(int i = 0; i < cols; i++) {
			for(int j = 0; j < rows; j++) {
				board[i][j] = new Tile(i,j);
			}
		}
		System.out.println("------------------------------");
		for(int i = 0; i < rows; i++) {
			System.out.print("|");
			for(int j = 0; j < cols; j++) {
				if(board[j][i].getMine() == true) {
					System.out.print("m");
				}
				else {
					System.out.print("o");
				}
				System.out.print("|");
			}
			System.out.println();
			System.out.println("------------------------------");
		}
		System.out.println("------------------------------");
	}

}
