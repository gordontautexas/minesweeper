package minesweeper;

import java.util.*;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application{
	static int cols = 30;
	static int rows = 16;
	static int count;
	static Tile[][] board;
	public static void main(String[] args) {
		HashSet<Tile> tiles = new HashSet<Tile>();
		int mines = 99;
		//int cols = 30;
		//int rows = 16;
		board = new Tile[cols][rows];
		for(int i = 0; i < cols; i++) {
			for(int j = 0; j < rows; j++) {
				Tile space = new Tile(i,j);
				board[i][j] = space;
				tiles.add(space);
			}
		}
		int initialMines = cols*rows;
		while(initialMines > mines) {
			int row = (int) (Math.random()*rows);
			int col = (int) (Math.random()*cols);
			Tile t = board[col][row];
			if(tiles.remove(t)) {
				t.deMine();
				initialMines--;
			}
		}
		for(int c = 0; c < cols; c++) {
			for(int r = 0; r < rows; r++) {
				if(!board[c][r].getMine()) {
					int numAdj = 0;
					for(int currCol = c-1; currCol <= c+1 && currCol < cols; currCol++) {
						for(int currRow = r-1; currRow <= r+1 && currRow < rows; currRow++) {
							if(currCol >= 0 && currRow >= 0) {
								if(board[currCol][currRow].getMine() == true) {
									numAdj++;
								}
							}
						}
					}
					board[c][r].setAdjMines(numAdj);
				}
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
					System.out.print(board[j][i].getAdjMines());
				}
				System.out.print("|");
			}
			System.out.println();
			System.out.println("------------------------------");
		}
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		VBox vb = new VBox();
		vb.setAlignment(Pos.CENTER);
		vb.setPadding(new Insets(0, 0, 0, 50));
		vb.setSpacing(25);
		vb.getChildren().add(new Button());
		GridPane grid = new GridPane();
		int tileHeight = 750/rows;
		int tileWidth = 1500/cols;
		for(int i = 0; i < cols; i++) {
			ColumnConstraints column = new ColumnConstraints(tileWidth);
			column.setHgrow(Priority.SOMETIMES);
			grid.getColumnConstraints().add(column);
		}
		for(int i = 0; i < rows; i++) {
			RowConstraints row = new RowConstraints(tileHeight);
			row.setVgrow(Priority.SOMETIMES);
			grid.getRowConstraints().add(row);
		}
		for(int c = 0; c < cols; c++) {
			for(int r = 0; r < rows; r++) {
				StackPane tilePane = new StackPane();
				tilePane.setStyle("-fx-background-color: #000000, "+ "#FFFFFF" +"; -fx-background-insets: 0, 0 1 1 0;");
				if (c == 0 && r == 0) {
					tilePane.setStyle("-fx-background-color: black, "+ "#FFFFFF" +"; -fx-background-insets: 0, 1;");
				} else {
					if (c == 0) {
						tilePane.setStyle("-fx-background-color: black, "+ "#FFFFFF" +"; -fx-background-insets: 0, 0 1 1 1;");
					}
					if (r == 0) {
						tilePane.setStyle("-fx-background-color: black, "+ "#FFFFFF" +"; -fx-background-insets: 0, 1 1 1 0;");
					}
				}
				grid.add(tilePane, c, r);
				tilePane.setOnMouseClicked(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						PriorityQueue<Tile> changedTiles = new PriorityQueue<Tile>();
						ObservableMap<Object, Object> o = tilePane.getProperties();
						int col = (Integer) o.get("gridpane-column");
						int row = (Integer) o.get("gridpane-row");
						// left click to reveal number or mine
						if(event.getButton().equals(MouseButton.PRIMARY)) {
							if (!board[col][row].isFlagged()) {
								if (board[col][row].getMine() == true) {
									changedTiles.add(board[col][row]);
								}
								else {
									changedTiles.add(board[col][row]);
								} 
								board[col][row].reveal();
							}
						}
						// right click to flag/unflag
						else {
							if(board[col][row].isRevealed()) {
							}
							else {
								board[col][row].toggleFlag();
								changedTiles.add(board[col][row]);
							}
						}
						while(!changedTiles.isEmpty()) {
							Tile currTile = changedTiles.remove();
							int x = currTile.getX();
							int y = currTile.getY();
							StackPane sp = (StackPane) grid.getChildren().get(x*rows + y);
							if(currTile.getMine() == true && currTile.isRevealed() == true) {
								Polygon poly = new Polygon();
								poly.getPoints()
										.addAll(new Double[] { tileWidth * 2 / 12.0, tileHeight * 2 / 12.0,
												tileWidth * 1 / 2.0, tileHeight * 1 / 2.0, tileWidth * 10 / 12.0,
												tileHeight * 2 / 12.0, tileWidth * 1 / 2.0, tileHeight * 1 / 2.0,
												tileWidth * 10 / 12.0, tileHeight * 10 / 12.0, tileWidth * 1 / 2.0,
												tileHeight * 1 / 2.0, tileWidth * 2 / 12.0, tileHeight * 10 / 12.0,
												tileWidth * 1 / 2.0, tileHeight * 1 / 2.0, tileWidth * 1 / 20.0,
												tileHeight * 1 / 2.0, tileWidth * 19 / 20.0, tileHeight * 1 / 2.0,
												tileWidth * 1 / 2.0, tileHeight * 1 / 2.0, tileWidth * 1 / 2.0,
												tileHeight * 1 / 30.0, tileWidth * 1 / 2.0, tileHeight * 29 / 30.0,
												tileWidth * 1 / 2.0, tileHeight * 1 / 2.0 });
								poly.setFill(Color.BLACK);
								poly.setStroke(Color.BLACK);
								Shape sh = new Circle(tileHeight * 0.4);
								sh.setFill(Color.BLACK);
								sh.setStroke(Color.BLACK);
								sp.getChildren().add(poly);
								sp.getChildren().add(sh);
							}
							else if(currTile.isFlagged() && !currTile.isRevealed()) {
								Polygon poly = new Polygon();
								poly.getPoints().addAll(new Double[]{ 
										tileWidth*1/12.0, tileHeight*1/4.0, 
										tileWidth*1/2.0, tileHeight*1/12.0, 
										tileWidth*1/2.0, tileHeight*5/12.0,
										tileWidth*1/2.0, tileHeight*8/12.0,
										tileWidth*11/12.0, tileHeight*11/12.0,
										tileWidth*1/12.0, tileHeight*11/12.0,
										tileWidth*1/2.0, tileHeight*8/12.0,
										tileWidth*1/2.0, tileHeight*5/12.0,
										tileWidth*1/12.0, tileHeight*1/4.0}); 
								poly.setFill(Color.RED);
								poly.setStroke(Color.RED);
								sp.getChildren().add(poly);
							}
							else if(!currTile.isFlagged() && !currTile.isRevealed()) {
								sp.getChildren().clear();
							}
							else {
								int adjMines = board[x][y].getAdjMines();
								Text number = new Text();
								switch (adjMines) {
								case 1:
									number.setText("1");
									number.setFill(Color.BLUE);
									break;
								case 2:
									number.setText("2");
									number.setFill(Color.LIMEGREEN);
									break;
								case 3:
									number.setText("3");
									number.setFill(Color.RED);
									break;
								case 4:
									number.setText("4");
									number.setFill(Color.PURPLE);
									break;
								case 5:
									number.setText("5");
									number.setFill(Color.ORANGERED);
									break;
								case 6:
									number.setText("6");
									number.setFill(Color.TEAL);
									break;
								case 7:
									number.setText("7");
									number.setFill(Color.BROWN);
									break;
								case 8:
									number.setText("8");
									number.setFill(Color.BLACK);
									break;
								default:
									number.setText("0");
									for(int currCol = x-1; currCol <= x+1 && currCol < cols; currCol++) {
										for(int currRow = y-1; currRow <= y+1 && currRow < rows; currRow++) {
											if(currCol >= 0 && currRow >= 0) {
												if(!board[currCol][currRow].isRevealed() && !board[currCol][currRow].isFlagged()) {
													changedTiles.add(board[currCol][currRow]);
													board[currCol][currRow].reveal();
												}
											}
										}
									}
									break;
								}
								sp.getChildren().add(number);
							}
						}
					}
				});
			}
		}
		vb.getChildren().add(grid);
		Scene scene = new Scene(vb, 1600, 800);
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}
}
