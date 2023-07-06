package minesweeper;

import java.util.*;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application{
	static int cols = 30;
	static int rows = 16;
	static int mines = 99;
	static int tilesToWin;
	static int openedTiles;
	static int mineDisplay;
	static Tile[][] board;
	static boolean lost;
	static boolean clockOn;
	static boolean firstClick;
	static Tile losingTile;
	static MediaPlayer musicPlayer;
	public static void main(String[] args) {
		resetBoard();
		firstClick = true;
		launch(args);
	}

	public static void resetBoard() {
		HashSet<Tile> tiles = new HashSet<Tile>();
		tilesToWin = cols*rows - mines;
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
		losingTile = null;
		mineDisplay = mines;
//		System.out.println("------------------------------");
//		for(int i = 0; i < rows; i++) {
//			System.out.print("|");
//			for(int j = 0; j < cols; j++) {
//				if(board[j][i].getMine() == true) {
//					System.out.print("m");
//				}
//				else {
//					System.out.print(board[j][i].getAdjMines());
//				}
//				System.out.print("|");
//			}
//			System.out.println();
//			System.out.println("------------------------------");
		System.out.println("" + tilesToWin);
//		}
	}
	
	public static void createPane(GridPane grid, int c, int r, Timeline tl, Text mineCount, int tileWidth, int tileHeight, ImageView resetView, Time tim) {
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
				if(!clockOn) {
					tl.play();
				}
				PriorityQueue<Tile> changedTiles = new PriorityQueue<Tile>();
				ObservableMap<Object, Object> o = tilePane.getProperties();
				int col = (Integer) o.get("gridpane-column");
				int row = (Integer) o.get("gridpane-row");
				// left click to reveal number or mine
				if(event.getButton().equals(MouseButton.PRIMARY)) {
					if(firstClick) {
						if(board[col][row].getAdjMines() != 0 || board[col][row].getMine() == true) {
							boolean isZero = false;
							while(!isZero) {
								resetBoard();
								if(board[col][row].getAdjMines() == 0 && board[col][row].getMine() == false) {
									isZero = true;
								}
							}
						}
					}
					firstClick = false;
					if (!board[col][row].isFlagged()) {
						changedTiles.add(board[col][row]);
						board[col][row].reveal();
					}
				}
				// right click to flag/unflag
				else {
					if(board[col][row].isRevealed()) {
						int numFlags = 0;
						for(int currCol = col-1; currCol <= col+1 && currCol < cols ; currCol++) {
							for(int currRow = row-1; currRow <= row+1 && currRow < rows; currRow++) {
								if(currCol >= 0 && currRow >= 0) {
									if(board[currCol][currRow].isFlagged()) {
										numFlags++;
									}
								}
							}
						}
						if(numFlags == board[col][row].getAdjMines()) {
							for(int currCol = col-1; currCol <= col+1 && currCol < cols ; currCol++) {
								for(int currRow = row-1; currRow <= row+1 && currRow < rows; currRow++) {
									if(currCol >= 0 && currRow >= 0) {
										if(!board[currCol][currRow].isFlagged() && !board[currCol][currRow].isRevealed()) {
											changedTiles.add(board[currCol][currRow]);
											board[currCol][currRow].reveal();
										}
									}
								}
							}
						}
					}
					else {
						if(!board[col][row].isFlagged()) {
							mineDisplay--;
						}
						else {
							mineDisplay++;
						}
						mineCount.setText("" + mineDisplay);
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
						Shape background = new Rectangle(tileWidth, tileHeight);
						background.setFill(Color.RED);
						background.setStroke(Color.RED);
						sp.getChildren().add(background);
						sp.getChildren().add(poly);
						sp.getChildren().add(sh);
						grid.setMouseTransparent(true);
						lost = true;
						losingTile = currTile;
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
						Shape rect = new Rectangle(tileWidth, tileHeight);
						rect.setFill(Color.LIGHTGRAY);
						rect.setStroke(Color.GRAY);
						sp.getChildren().add(rect);
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
						openedTiles++;
						System.out.println(""+openedTiles);
					}
				}
				if(openedTiles == tilesToWin) {
					Image wonImage = new Image(getClass().getResourceAsStream("Spheal4.png"));
					resetView.setImage(wonImage);
					openedTiles = 0;
					grid.setMouseTransparent(true);
					tl.stop();
					tim.resetTime();
				}
				if(lost) {
					for(int c = 0; c < cols; c++) {
						for(int r = 0; r < rows; r++) {
							if((board[c][r].getMine() && board[c][r] != losingTile) &&
								!board[c][r].isFlagged()) {
								StackPane sp = (StackPane) grid.getChildren().get(c*rows + r);
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
						}
					}
					Image lostImage = new Image(getClass().getResourceAsStream("Spheal3.png"));
					resetView.setImage(lostImage);
					tl.stop();
					tim.resetTime();
				}
			}
		});
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		VBox vb = new VBox();
		vb.setAlignment(Pos.CENTER);
		vb.setPadding(new Insets(0, 0, 0, 160));
		vb.setSpacing(25);
		HBox controlBox = new HBox();
		controlBox.setSpacing(50);
		Text difficulty = new Text("Game");
		difficulty.setOnMouseMoved(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				difficulty.setFill(Color.BLUE);
			}
		});
		difficulty.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				difficulty.setFill(Color.BLACK);
			}
		});
		Text controls = new Text("Controls");
		controls.setOnMouseMoved(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				controls.setFill(Color.BLUE);
			}
		});
		controls.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				controls.setFill(Color.BLACK);
			}
		});
		Text musicMenu = new Text("Music");
		musicMenu.setOnMouseMoved(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				musicMenu.setFill(Color.BLUE);
			}
		});
		musicMenu.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				musicMenu.setFill(Color.BLACK);
			}
		});
		GridPane diffPane = new GridPane();
		diffPane.setHgap(50);
		diffPane.setVgap(25);
		diffPane.add(new Text(), 0, 0);
		diffPane.add(new Text("Height"), 1, 0);
		diffPane.add(new Text("Width"), 2, 0);
		diffPane.add(new Text("Mines"), 3, 0);
		ToggleGroup group = new ToggleGroup();
	    RadioButton begButton = new RadioButton("Beginner");
	    begButton.setToggleGroup(group);
	    begButton.setSelected(true);
	    RadioButton intButton = new RadioButton("Intermediate");
	    intButton.setToggleGroup(group);
	    RadioButton expButton = new RadioButton("Expert");
	    expButton.setToggleGroup(group);
	    RadioButton custButton = new RadioButton("Custom");
	    custButton.setToggleGroup(group);
		Text beg1 = new Text("9");
		Text beg2 = new Text("9");
		Text beg3 = new Text("10");
		diffPane.add(begButton, 0, 1);
		diffPane.add(beg1, 1, 1);
		diffPane.add(beg2, 2, 1);
		diffPane.add(beg3, 3, 1);
		Text int1 = new Text("16");
		Text int2 = new Text("16");
		Text int3 = new Text("40");
		diffPane.add(intButton, 0, 2);
		diffPane.add(int1, 1, 2);
		diffPane.add(int2, 2, 2);
		diffPane.add(int3, 3, 2);
		Text exp1 = new Text("16");
		Text exp2 = new Text("30");
		Text exp3 = new Text("99");
		diffPane.add(expButton, 0, 3);
		diffPane.add(exp1, 1, 3);
		diffPane.add(exp2, 2, 3);
		diffPane.add(exp3, 3, 3);
		TextField cus1 = new TextField();
		TextField cus2 = new TextField();
		TextField cus3 = new TextField();
		diffPane.add(custButton, 0, 4);
		diffPane.add(cus1, 1, 4);
		diffPane.add(cus2, 2, 4);
		diffPane.add(cus3, 3, 4);
		Button newButton = new Button("New Game");
		diffPane.add(newButton, 0, 5);
		difficulty.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				Stage diffStage = new Stage();
				HBox gridBox = new HBox();
				gridBox.getChildren().add(diffPane);
				Scene diffScene = new Scene(gridBox, 900, 400);
				diffStage.setScene(diffScene);
				diffStage.show();
			}
		});
		VBox ctrl = new VBox();
		Text ctrl1 = new Text("Controls");
		Text ctrl2 = new Text();
		ctrl2.setText("* Left-click an empty square to reveal it. \n"
				+ "* Right-click an empty sqaure to flag it. \n"
				+ "* Right-click a number to reveal its adjacent squares. \n"
				+ "* Press the Spheal to start a new game.");
		ctrl.getChildren().add(ctrl1);
		ctrl.getChildren().add(ctrl2);
		controls.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Stage ctrlStage = new Stage();
				HBox ctrlBox = new HBox();
				ctrlBox.getChildren().add(ctrl);
				Scene ctrlScene = new Scene(ctrlBox, 500, 500);
				ctrlStage.setScene(ctrlScene);
				ctrlStage.show();
			}
		});
		Media music = new Media("file:///" + System.getProperty("user.dir").replace('\\', '/') + "/src/minesweeper/" + "S1.mp4");
		musicPlayer = new MediaPlayer(music);
		musicPlayer.play();
		musicPlayer.setOnEndOfMedia(new Runnable() {
			@Override
			public void run() {
					musicPlayer.seek(Duration.ZERO);
					musicPlayer.play();
			}
		});
		GridPane musicPane = new GridPane();
		musicPane.setVgap(20);
		musicPane.setHgap(20);
		ToggleGroup songToggle = new ToggleGroup();
		RadioButton song1 = new RadioButton();
		song1.setToggleGroup(songToggle);
		song1.setSelected(true);
		RadioButton song2 = new RadioButton();
		song2.setToggleGroup(songToggle);
		RadioButton song3 = new RadioButton();
		song3.setToggleGroup(songToggle);
		Text mus1 = new Text("I Believe");
		Text mus2 = new Text("Rivers in the Desert");
		Text mus3 = new Text("Affection");
		musicPane.add(song1, 0, 0);
		musicPane.add(mus1, 1, 0);
		musicPane.add(song2, 0, 1);
		musicPane.add(mus2, 1, 1);
		musicPane.add(song3, 0, 2);
		musicPane.add(mus3, 1, 2);
		Button changeBtn = new Button("OK");
		musicPane.add(changeBtn, 0, 3);
		changeBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Toggle tog = songToggle.getSelectedToggle();
				ObservableMap<Object, Object> prop = tog.getProperties();
				int row = (int) prop.get("gridpane-row");
				musicPlayer.stop();
				if(row == 0) {
					Media music = new Media("file:///" + System.getProperty("user.dir").replace('\\', '/') + "/src/minesweeper/" + "S1.mp4");
					musicPlayer = new MediaPlayer(music);
				}
				else if(row == 1) {
					Media music = new Media("file:///" + System.getProperty("user.dir").replace('\\', '/') + "/src/minesweeper/" + "S2.mp4");
					musicPlayer = new MediaPlayer(music);
				}
				else {
					Media music = new Media("file:///" + System.getProperty("user.dir").replace('\\', '/') + "/src/minesweeper/" + "S3.mp4");
					musicPlayer = new MediaPlayer(music);
				}
				musicPlayer.seek(Duration.ZERO);
				musicPlayer.play();
				musicPlayer.setOnEndOfMedia(new Runnable() {
					@Override
					public void run() {
							musicPlayer.seek(Duration.ZERO);
							musicPlayer.play();
					}
				});
				Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
				stage.close();
			}
		});
		musicMenu.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
					Stage musStage = new Stage();
					HBox musBox = new HBox();
					musBox.getChildren().add(musicPane);
					Scene musScene = new Scene(musBox, 200, 200);
					musStage.setScene(musScene);
					musStage.show();
			}
		});
		controlBox.getChildren().addAll(difficulty, controls, musicMenu);
		vb.getChildren().add(controlBox);
		HBox hb = new HBox();
		hb.setPadding(new Insets(0, 0, 0, 160));
		hb.setSpacing(600);
		hb.getStylesheets().add("minesweeper/TopVisuals.css");
		StackPane resetPane = new StackPane();
		GridPane grid = new GridPane();
		grid.getStylesheets().add("minesweeper/Tile.css");
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
		StackPane minePane = new StackPane();
		minePane.getStyleClass().add("numMines");
		Text mineCount = new Text();
		mineCount.setText("" + mines);
		minePane.getChildren().add(mineCount);
		hb.getChildren().add(minePane);
		Image resetImg = new Image(getClass().getResourceAsStream("Spheal.png"));
		ImageView resetView = new ImageView();
		resetView.setImage(resetImg);
		resetPane.getChildren().add(resetView);
		Text clock = new Text();
		clock.setText("0");
		hb.getChildren().add(resetPane);
		StackPane clockPane = new StackPane();
		clockPane.getStyleClass().add("clock");
		Time tim = new Time();
		Timeline tl = new Timeline(
				new KeyFrame(Duration.seconds(1), 
						e -> {
							tim.secPassed();
							clock.setText("" + tim.getTime());
						}));
		tl.setCycleCount(Timeline.INDEFINITE);
		clockPane.getChildren().add(clock);
		hb.getChildren().add(clockPane);
		vb.getChildren().add(hb);
		for(int c = 0; c < cols; c++) {
			for(int r = 0; r < rows; r++) {
				Main.createPane(grid, c, r, tl, mineCount, tileWidth, tileHeight, resetView, tim);
			}
		}
		resetPane.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Image pressedImg = new Image(getClass().getResourceAsStream("Spheal2.png"));
				resetView.setImage(pressedImg);
			}
			
		});
		resetPane.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				resetBoard();
				grid.setMouseTransparent(false);
				firstClick = true;
				lost = false;
				losingTile = null;
				mineDisplay = mines;
				mineCount.setText("" + mineDisplay);
				tl.stop();
				tim.resetTime();
				clock.setText("0");
				for(int c = 0; c < cols; c++) {
					for(int r = 0; r < rows; r++) {
						StackPane sp = (StackPane) grid.getChildren().get(c*rows + r);
						sp.getChildren().clear();
					}
				}
				resetView.setImage(resetImg);
			}
		});
		newButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
					try {
						Toggle tog = group.getSelectedToggle();
						ObservableMap<Object, Object> prop = tog.getProperties();
						int row = (int) prop.get("gridpane-row");
						if(row == 1) {
							cols = 9;
							rows = 9;
							mines = 10;
						}
						else if(row == 2) {
							cols = 16;
							rows = 16;
							mines = 40;
						}
						else if(row == 3){
							cols = 30;
							rows = 16;
							mines = 99;
						}
						else {
							cols = Integer.parseInt(cus2.getText());
							rows = Integer.parseInt(cus1.getText());
							mines = Integer.parseInt(cus3.getText());
							if(cols < 8 || rows < 2) {
								throw new NullPointerException();
							}
							if(mines >= cols*rows) {
								throw new ArithmeticException();
							}
						}
						resetBoard();
						grid.getChildren().clear();
						mineCount.setText("" + mines);
						tl.stop();
						tim.resetTime();
						clock.setText("0");
						for(int c = 0; c < cols; c++) {
							for(int r = 0; r < rows; r++) {
								Main.createPane(grid, c, r, tl, mineCount, tileWidth, tileHeight, resetView, tim);
							}
						}
						hb.setSpacing(cols*20);
						Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
						stage.close();
					} catch (NumberFormatException e) {
						Alert alert = new Alert(AlertType.ERROR, 
								"Enter a valid number");
						alert.showAndWait();
					}
					catch (NullPointerException e) {
						Alert alert = new Alert(AlertType.ERROR, 
								"Width must be at least 8 and rows must be at least 2");
						alert.showAndWait();
					}
					catch (ArithmeticException e) {
						Alert alert = new Alert(AlertType.ERROR, 
								"Too many mines");
						alert.showAndWait();
					}
			}
		});
		vb.getChildren().add(grid);
		primaryStage.setOnCloseRequest(windowEvent -> {
			musicPlayer.stop();
		});
		Scene scene = new Scene(vb, 1600, 800);
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}
}
