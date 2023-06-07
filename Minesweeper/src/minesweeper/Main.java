package minesweeper;

import java.util.*;

import javafx.application.Application;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

public class Main extends Application{
	static int cols = 30;
	static int rows = 16;
	static int count;
	public static void main(String[] args) {
		HashSet<Tile> tiles = new HashSet<Tile>();
		int mines = 99;
		//int cols = 30;
		//int rows = 16;
		Tile[][] board = new Tile[cols][rows];
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
			else {
			}
		}
//		System.out.println("------------------------------");
//		for(int i = 0; i < rows; i++) {
//			System.out.print("|");
//			for(int j = 0; j < cols; j++) {
//				if(board[j][i].getMine() == true) {
//					System.out.print("m");
//				}
//				else {
//					System.out.print("o");
//				}
//				System.out.print("|");
//			}
//			System.out.println();
//			System.out.println("------------------------------");
//		}
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
				tilePane.setOnMouseClicked(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						ObservableMap<Object, Object> o = tilePane.getProperties();
						if(event.getButton().equals(MouseButton.PRIMARY)) {
							Polygon polygon3 = new Polygon();
	     					//polygon3.getPoints().addAll(new Double[]{ 4.2,5.1,7.1,9.1}); 
	     					polygon3.setFill(Color.AQUA);
	     					polygon3.setStroke(Color.YELLOW);
							tilePane.getChildren().add(polygon3);
						}
						else {
							Polygon polygon3 = new Polygon();
	     					//polygon3.getPoints().addAll(new Double[]{ 4.2,5.1,7.1,9.1}); 
	     					polygon3.setFill(Color.AQUA);
	     					polygon3.setStroke(Color.YELLOW);
							tilePane.getChildren().add(polygon3);
						}
						int i1 = (Integer) o.get("gridpane-column");
						int i2 = (Integer) o.get("gridpane-row");
					}
				});
				grid.add(tilePane, c, r);
			}
		}
		vb.getChildren().add(grid);
		Scene scene = new Scene(vb, 1600, 800);
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}
}
