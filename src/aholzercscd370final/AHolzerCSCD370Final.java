/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aholzercscd370final;

import fileio.ChallengeFile;
import gameboard.GameBoard;
import gameboard.ResultSet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Scanner;
import java.util.stream.Stream;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author short
 */
public class AHolzerCSCD370Final extends Application {

    //Member Variables
    private final static double X = 700, Y = 700;
    private final static int ROWS = 13, COLS = 13;
    private static int TIMER_TICK = 1000;

    private static final String HEADER_TEXT = "Adam J Holzer, CSCD 370 Final Project, Winter 2017";
    private String title = "Robot Factory", challengeTitle;
    private Stage me;
    private Label mStatus;
    private GridPane mainGrid;
    private Canvas mTempCanvas, mPermCanvas;
    private ToggleGroup buttonToggle;
    private VBox top;
    private ToolbarPos currentToolbarPos;
    private ToolBar myButtonBar;
    private BorderPane root;
    private boolean challengeLoaded, normalSpeed;
    private File mFile;
    private ChallengeFile challengeFile;
    private MenuItem menuItemOpen, menuItemGo, menuItemPause, menuItemFaster;
    private AnimationTimer timer;
    private long mPrevTime;
    private ImageView[][] allImageViews;
    private GameBoard gameBoard;
    private GameController gameController;

    //ImageLocs
    private String blankLoc, blueRobotLoc, redRobotLoc, robotLoc, startLoc, finishLoc,
	    upBeltLoc, downBeltLoc, leftBeltLoc, rightBeltLoc, upSwitchLoc, downSwitchLoc,
	    leftSwitchLoc, rightSwitchLoc, deleteLoc;

    //ImageViews for buttons
    private ImageView blank, blueRobot, redRobot, robot, start, finish,
	    beltUp, beltDown, beltLeft, beltRight, switchUp, switchDown, switchLeft,
	    switchRight, delete;

    private Image redRobotImage, blueRobotImage, robotImage;
    
    @Override
    public void start(Stage primaryStage) {
	//Constructor stuff
	normalSpeed = true;
	gameBoard = new GameBoard();
	gameController = new GameController(gameBoard);
	mPrevTime = System.currentTimeMillis();
	timer = new AnimationTimer() {
	    @Override
	    public void handle(long now) {
		//now = System.currentTimeMillis(); //update now to milliseconds
		onTimerTick(now);
	    }
	};
	allImageViews = new ImageView[ROWS][COLS];
	challengeFile = new ChallengeFile();
	me = primaryStage;
	challengeLoaded = false;
	//challengeLoaded = false;
	mFile = null;
	currentToolbarPos = ToolbarPos.Left;
	mStatus = new Label("Everything is copacetic");
	//mTempCanvas = new Canvas(X, Y);
	//mPermCanvas = new Canvas(X, Y);
	initImages();
	buttonToggle = new ToggleGroup();
	myButtonBar = buildToolBar();
	//myLines = new ArrayList<>();

	//event handlers using convenience methods
	//mPermCanvas.setOnMousePressed(mouseEvent -> onMousePressed(mouseEvent));
	//mPermCanvas.setOnMouseReleased(mouseEvent -> onMouseReleased(mouseEvent));
	//manually create event handler
	//mPermCanvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseEvent -> onMouseDragged(mouseEvent));
	//fillCanvas();
	ToolBar toolBar = new ToolBar(mStatus);

	//make root of scene graph
	root = new BorderPane();

	//stack pane
	//StackPane stack = new StackPane(mTempCanvas, mPermCanvas);
	//GridPane myGrid = buildGrid(COLS, ROWS);
	mainGrid = buildGrid(COLS, ROWS);

	top = new VBox(buildMenuBar());

	//configure pane
	//ScrollPane sp = new ScrollPane(myGrid);
	root.setCenter(mainGrid);
	root.setBottom(toolBar);
	root.setTop(top);
	root.setLeft(myButtonBar);
	// btn.prefWidthProperty().bind(primaryStage.widthProperty().divide(2));

	//set scene to the pane we want
	Scene scene = new Scene(root, X * 1.1, Y * 1.2);

	primaryStage.setResizable(false);
	primaryStage.setTitle(title);
	primaryStage.setScene(scene);
	primaryStage.setOnCloseRequest(event -> onExit(event));
	primaryStage.show();
    }

    private void onTimerTick(long now) {
	now = System.currentTimeMillis();
	long passed = now - mPrevTime;
	if (passed >= TIMER_TICK) {
	    mPrevTime = now;
	    ResultSet fooResult = gameController.doNext();

	    mainGrid.getChildren().remove(robot);

	    if (fooResult.isGameOver()) {
		pauseGame();

		System.out.println("current robot ended");

		Platform.runLater(() -> {
		    popupAlert(AlertType.INFORMATION, "TEST CASE RESULT",
			    fooResult.isCorrectRobotOutcome() ? "PASS" : "FAIL");

		    if (fooResult.isCorrectRobotOutcome()) {
			if (gameController.hasMoreRobots()) {
			    popupAlert(AlertType.INFORMATION, "NEXT TEST CASE", "Ready for next robot");
			    gameController.startGame();
			    timer.start();
			} else {
			    popupAlert(AlertType.CONFIRMATION, "SOLUTION RESULT", "Your solution is correct");

			    // TODO: maybe clear board because everything is done
			}
		    } else {
			gameController.buildRobotQueue();
		    }
		});
	    } else {
		setStatus(String.format("%s\nCurrent Tape: %s", challengeFile.getWinCondition(), gameBoard.getRobot().seeNextTapeItem()));
		switch (gameBoard.getRobot().seeNextTapeItem().toUpperCase()) {
		    case "R":
			robot.setImage(redRobotImage);
			break;
		    case "B":
			robot.setImage(blueRobotImage);
			break;
		    case "NONE":
			robot.setImage(robotImage);
			break;
		}
		mainGrid.add(robot, gameBoard.getxRobotCoord(), gameBoard.getyRobotCoord());
	    }

//	    GridPane myGrid = (GridPane)root.getCenter();
//	    myGrid.getChildren().remove(robot);
//	    myGrid.add(robot, 6, 1);
	    System.out.println("Time passed since last: " + passed);
	}

	//throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void initImages() {

	//define image locations
	blankLoc = "/icons/blank.png";
	blueRobotLoc = "/icons/bluerobot.png";
	redRobotLoc = "/icons/redrobot.png";
	robotLoc = "/icons/robot.png";
	startLoc = "/icons/src.png";
	finishLoc = "/icons/sink.png";
	upBeltLoc = "/icons/up.png";
	downBeltLoc = "/icons/down.png";
	leftBeltLoc = "/icons/left.png";
	rightBeltLoc = "/icons/right.png";
	upSwitchLoc = "/icons/sw_down.png";
	downSwitchLoc = "/icons/sw_up.png";
	leftSwitchLoc = "/icons/sw_right.png";
	rightSwitchLoc = "/icons/sw_left.png";
	deleteLoc = "/icons/x.png";

	blank = new ImageView(new Image(blankLoc));
	blank.setUserData("BLANK");
	blueRobot = new ImageView(new Image(blueRobotLoc));
	blueRobot.setUserData("BROBOT");
	redRobot = new ImageView(new Image(redRobotLoc));
	redRobot.setUserData("RROBOT");
	robot = new ImageView(new Image(robotLoc));
	robot.setScaleX(1.4);
	robot.setScaleY(1.4);
	robot.setUserData("ROBOT");
	start = new ImageView(new Image(startLoc));
	start.setUserData("SPAWN");
	finish = new ImageView(new Image(finishLoc));
	finish.setUserData("FINISH");
	beltUp = new ImageView(new Image(upBeltLoc));
	beltUp.setUserData("BELTUP");
	beltDown = new ImageView(new Image(downBeltLoc));
	beltDown.setUserData("BELTDOWN");
	beltLeft = new ImageView(new Image(leftBeltLoc));
	beltLeft.setUserData("BELTLEFT");
	beltRight = new ImageView(new Image(rightBeltLoc));
	beltRight.setUserData("BELTRIGHT");
	switchUp = new ImageView(new Image(upSwitchLoc));
	switchUp.setUserData("SWITCHUP");
	switchDown = new ImageView(new Image(downSwitchLoc));
	switchDown.setUserData("SWITCHDOWN");
	switchLeft = new ImageView(new Image(leftSwitchLoc));
	switchLeft.setUserData("SWITCHLEFT");
	switchRight = new ImageView(new Image(rightSwitchLoc));
	switchRight.setUserData("SWITCHRIGHT");
	delete = new ImageView(new Image(deleteLoc));
	delete.setUserData("DELETE");

	GridPane.setHalignment(robot, HPos.CENTER);
	GridPane.setHalignment(redRobot, HPos.CENTER);
	GridPane.setHalignment(blueRobot, HPos.CENTER);

	//robot images
	redRobotImage = new Image(redRobotLoc);
	blueRobotImage = new Image(blueRobotLoc);
	robotImage = new Image(robotLoc);
    }

    private ToolBar buildToolBar() {
	ToolBar rv = new ToolBar();
	rv.setOrientation(Orientation.VERTICAL);

	//build buttons
	ToggleButton upConvBtn = buildButton("Place an up conveyor", buttonToggle, beltUp);
	ToggleButton downConvBtn = buildButton("Place a down conveyor", buttonToggle, beltDown);
	ToggleButton leftConvBtn = buildButton("Place a left conveyor", buttonToggle, beltLeft);
	ToggleButton rightConvBtn = buildButton("Place a right conveyor", buttonToggle, beltRight);
	ToggleButton upSwitchBtn = buildButton("Place a bottom fed switch", buttonToggle, switchUp);
	ToggleButton downSwitchBtn = buildButton("Place a top fed switch", buttonToggle, switchDown);
	ToggleButton leftSwitchBtn = buildButton("Places a right fed switch", buttonToggle, switchLeft);
	ToggleButton rightSwitchBtn = buildButton("Places a left fed switch", buttonToggle, switchRight);
	ToggleButton deleteBtn = buildButton("Remove a tile", buttonToggle, delete);
	Button startBtn = buildButton("/icons/go.png", "Start the factory");
	startBtn.setOnMouseClicked(onClickEvent -> startGame());
	Button moveBtn = buildButton("/icons/Move.png", "Move the toolpar position");

	//set handlers for buttons
//        upConv.setOnAction(event -> newCanvas());
//        downConv.setOnAction(event -> openFile(event));
//        leftConv.setOnAction(event -> onSave(false)); //save, not save-as
//        rightConv.setOnAction(event -> setPixels(-1));
//        upSwitch.setOnAction(event -> setColor());
	moveBtn.setOnAction(event -> onMove());

	//add all items to collection
	rv.getItems().addAll(upConvBtn, downConvBtn, leftConvBtn, rightConvBtn, new Separator(Orientation.HORIZONTAL),
		upSwitchBtn, downSwitchBtn, leftSwitchBtn, rightSwitchBtn, new Separator(Orientation.HORIZONTAL),
		deleteBtn, new Separator(Orientation.HORIZONTAL), startBtn, new Separator(Orientation.HORIZONTAL), moveBtn);

	return rv;
    }

    private Button buildButton(String location, String tooltip) {
	Button rv = new Button("", new ImageView(new Image(getClass().getResource(location).toString())));
	rv.setTooltip(new Tooltip(tooltip));
	return rv;
    }

    private ToggleButton buildButton(String tooltip, ToggleGroup group, ImageView img) {
	//ToggleButton rv = new ToggleButton("", new ImageView(new Image(getClass().getResource(location).toString())));
	ToggleButton rv = new ToggleButton("", img);
	rv.setTooltip(new Tooltip(tooltip));
	rv.setToggleGroup(group);
	rv.setOnDragDetected(dragDetectedEvent -> onDragDetected(dragDetectedEvent));
	return rv;
    }

    private ToggleButton buildButton(String location, String tooltip, ToggleGroup group) {
	ToggleButton rv = new ToggleButton("", new ImageView(new Image(getClass().getResource(location).toString())));
	rv.setTooltip(new Tooltip(tooltip));
	rv.setToggleGroup(group);
	return rv;
    }

    private MenuItem buildMenuItem(String name, KeyCodeCombination combo) {
	MenuItem rv = new MenuItem(name);
	rv.setAccelerator(combo);
	return rv;
    }

    private RadioMenuItem buildMenuItem(String name, ToggleGroup who, boolean isSelected) {
	RadioMenuItem rv = new RadioMenuItem(name);
	rv.setToggleGroup(who);
	rv.setSelected(isSelected);
	return rv;
    }

    private MenuBar buildMenuBar() {
	//build a menu bar to all all my components to
	MenuBar menuBar = new MenuBar();

	//Build Menu Categories using Menus
	Menu fileMenu = new Menu("File");
	Menu widthMenu = new Menu("Game");
	Menu helpMenu = new Menu("Help");
	menuBar.getMenus().addAll(fileMenu, widthMenu, helpMenu);

	//Build Menu Subcategories
	//File
	MenuItem menuItemReset = buildMenuItem("Reset", new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
	menuItemOpen = buildMenuItem("Open Challenge/Solution", new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
	MenuItem menuItemSave = buildMenuItem("Save Solution", new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
	//MenuItem menuItemLoad = buildMenuItem("Load", new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN));
	MenuItem menuItemQuit = buildMenuItem("Quit", new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));
	fileMenu.getItems().addAll(menuItemReset, menuItemOpen, menuItemSave, new SeparatorMenuItem(), menuItemQuit);

	//Game MenuItem Declarations
	menuItemGo = buildMenuItem("Go", new KeyCodeCombination(KeyCode.G, KeyCodeCombination.CONTROL_DOWN));
	menuItemPause = buildMenuItem("Pause", new KeyCodeCombination(KeyCode.P, KeyCodeCombination.CONTROL_DOWN));
	menuItemPause.setDisable(true);
	menuItemFaster = buildMenuItem("Faster", new KeyCodeCombination(KeyCode.UP));
	widthMenu.getItems().addAll(menuItemGo, menuItemPause, menuItemFaster);

	//Help
	MenuItem aboutMenuItem = new MenuItem("_About");
	helpMenu.getItems().add(aboutMenuItem);

	//Assign Eventhandlers
	//File
	menuItemReset.setOnAction(actionEvent -> resetGame());
	menuItemOpen.setOnAction(actionEvent -> openFile(actionEvent));
	//menuItemSave.setOnAction(actionEvent -> onSave(false));
	//menuItemLoad.setOnAction(actionEvent -> onSave(true));
	menuItemQuit.setOnAction(actionEvent -> onExit(actionEvent));

	//Game
	menuItemGo.setOnAction(actionEvent -> startGame());
	menuItemPause.setOnAction(actionEvent -> pauseGame());
	menuItemFaster.setOnAction(actionEvent -> adjustSpeed());

	//About
	aboutMenuItem.setOnAction(actionEvent -> onAbout());

	return menuBar;
    }

    //REQUIRED IN ALL ASSIGNMENTS
    private void onAbout() {
	Alert alert = new Alert(Alert.AlertType.INFORMATION);
	alert.setTitle("About");
	alert.setHeaderText(HEADER_TEXT);
	alert.showAndWait();
    }

    private void setStatus(String status) {
	mStatus.setText(status);
    }

    private void fillCanvas() {
	GraphicsContext temp = mTempCanvas.getGraphicsContext2D();
	temp.setFill(Color.WHITE);
	temp.fillRect(0, 0, X, Y);

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//	GameBoard board = new GameBoard();
//	GameController controller = new GameController(board);
//	controller.startGAme();

	// TODO: instantiate the objects as above
	// 1. draw initial state of board (predefined belts from top to bottom)
	// 2. start the game, query for and draw initial position of robot
	// 3. start a timer, on each tick make next move and draw results
	// TODO future: load initial data from file into necessary objects
	launch(args);
    }

    private void onSave(boolean saveAs) {
	//dont forget to set hasChanged = false
	File selectedFile = mFile;
	if (saveAs || mFile == null) {
	    //setStatus("Save-as Clicked!");
	    FileChooser fc = new FileChooser();
	    fc.setTitle("Save Line File");
	    fc.getExtensionFilters().addAll(
		    new ExtensionFilter("Line Files", "*.line"),
		    new ExtensionFilter("All Files", "*.*"));
	    fc.setInitialDirectory(new File("."));
	    if (mFile != null) {
		fc.setInitialFileName(mFile.getName());
	    }
	    selectedFile = fc.showSaveDialog(me);
	    //mFile = selectedFile;
	    //show save-as dialog
	}
	if (selectedFile != null) {
	    //setStatus("Save Clicked!");
	    try {
		//todo: open a stream and write stuff
		ObjectOutputStream fout = new ObjectOutputStream(new FileOutputStream(selectedFile));
		//fout.writeObject(myLines);

	    } catch (IOException e) {
		//todo report the error
		e.printStackTrace();
	    }
	    mFile = selectedFile;
	    me.setTitle(mFile.getName());
	    //challengeLoaded = false;
	}
    }

    private void setTitle(String s) {
	me.setTitle(s);
    }

    private void openFile(ActionEvent e) {
	//if (checkHasChanged(e)) {
	FileChooser fc = new FileChooser();
	fc.setTitle("Open a .line file");
	fc.setInitialDirectory(new File("."));
	fc.getExtensionFilters().addAll(
		new ExtensionFilter("Challenge File", "*.rbt"),
		new ExtensionFilter("Solution File", "*.grd"));
	File selectedFile = fc.showOpenDialog(me);
	if (selectedFile == null) {
	    return;
	}
	try {
	    //TODO: open a stream, read the stuff, close the stream
	    //ObjectInputStream fin = new ObjectInputStream(new FileInputStream(selectedFile));
	    Scanner fin = new Scanner(selectedFile);
	    if (selectedFile.getName().toLowerCase().endsWith(".rbt")) {
		//processing challenge file
		System.out.println("Opening challenge file");
		challengeFile.setFileName(selectedFile.getName());
		challengeFile.setWinCondition(fin.nextLine()); //first line
		challengeFile.setCases(Integer.parseInt(fin.nextLine())); //number of cases (second line)
		challengeFile.setContent(fin);
		challengeTitle = String.format("%s - %s", title, challengeFile.getFileName());
		setTitle(challengeTitle);
		setStatus(challengeFile.getWinCondition());
		gameController.setChallengeFile(challengeFile);
		//System.out.println("NEW TITLE: " + challengeTitle);

		//System.out.printf("Condition: %s\nCases: %d\n", challengeFile.getContent(), challengeFile.getCases());
		challengeLoaded = true;
	    } else if (selectedFile.getName().toLowerCase().endsWith(".grd")) {
		//processing map file
		System.out.println("Opening map file");
		mainGrid = buildLoadedMap(fin);
		root.setCenter(mainGrid);
		//root.setCenter(buildLoadedMap(fin));

	    }

	} catch (IOException ex) {
	    ex.printStackTrace();
	}
    }

    //TODO: refactor this to use same imageview references?
    private GridPane buildLoadedMap(Scanner fin) {
	GridPane rv = new GridPane();
	int rows, cols;
	//first 2 lines of input are xy demensions. we ignore
	rows = Integer.parseInt(fin.nextLine());
	cols = Integer.parseInt(fin.nextLine());
	System.out.printf("ROWS: %d -- COLS: %d\n", rows, cols);
	int[][] map = new int[cols][rows];

	for (int i = 0; i < ROWS; i++) {
	    for (int j = 0; j < COLS; j++) {
		//lay unchangable base floor (so placements dont remove grid)
		rv.add(buildTile(blankLoc, "FLOOR"), j, i);
		ImageView im;
		int code = fin.nextInt();
		switch (code) {
		    case 0:
			im = buildTile(blankLoc, "BLANK");
			rv.add(im, j, i);
			allImageViews[j][i] = im;
			break;
		    case 1:
			im = buildTile(startLoc, "SPAWN");
			rv.add(im, j, i);
			rv.add(robot, j, i);
			allImageViews[j][i] = im;
			break;
		    case 2:
			im = buildTile(finishLoc, "FINISH");
			rv.add(im, j, i);
			allImageViews[j][i] = im;
			break;
		    case 3:
			im = buildTile(upBeltLoc, "BELTUP");
			rv.add(im, j, i);
			allImageViews[j][i] = im;
			break;
		    case 4:
			im = buildTile(downBeltLoc, "BELTDOWN");
			rv.add(im, j, i);
			allImageViews[j][i] = im;
			break;
		    case 5:
			im = buildTile(leftBeltLoc, "BELTLEFT");
			rv.add(im, j, i);
			allImageViews[j][i] = im;
			break;
		    case 6:
			im = buildTile(rightBeltLoc, "BELTRIGHT");
			rv.add(im, j, i);
			allImageViews[j][i] = im;
			break;
		    case 8:
			im = buildTile(upSwitchLoc, "SWITCHUP");
			rv.add(im, j, i);
			allImageViews[j][i] = im;
			break;

		}
		//System.out.printf("%d ", map[i][j]);

//		if (i == 6 && j == 0) //center of top row
//		{
//		    rv.add(buildTile(startLoc, "SPAWN: " + i + ", " + j), i, j);
//		    rv.add(buildTile(robotLoc, "ROBOT"), i, j);
//		} else if (i == 6 && j == 12) //center of bottom row
//		{
//		    rv.add(buildTile(finishLoc, "FINISH: " + i + ", " + j), i, j);
//		} else {
//		    rv.add(buildTile(blankLoc, "BLANK: " + i + ", " + j), i, j);
//		}
		//System.out.printf("Adding tile to loc (%d, %d)\n", i, j);
	    }
	    //System.out.println();
	}
	rv.setOnMouseClicked(mouseEvent -> onGridClicked(mouseEvent));

	//center gameboard
	//rv.setAlignment(Pos.CENTER);
	return rv;
	//throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void onMove() {
	setStatus("onMove clicked");
	switch (currentToolbarPos) {
	    case Left:
		root.getChildren().remove(myButtonBar);
		top.getChildren().add(myButtonBar);
		myButtonBar.setOrientation(Orientation.HORIZONTAL);
		currentToolbarPos = ToolbarPos.Top;
		break;
	    case Top:
		top.getChildren().remove(myButtonBar);
		root.setRight(myButtonBar);
		myButtonBar.setOrientation(Orientation.VERTICAL);
		currentToolbarPos = ToolbarPos.Right;
		break;
	    case Right:
		root.getChildren().remove(myButtonBar);
		root.setLeft(myButtonBar);
		currentToolbarPos = ToolbarPos.Left;
		break;
	}
    }

    private void log(String log) {
	System.out.println(log);
    }

    private void onExit(WindowEvent e) {
	//if (checkHasChanged(e)) {
	Platform.exit();
	//}
    }

    private void onExit(ActionEvent e) {
	//if (checkHasChanged(e)) {
	Platform.exit();
	//}
    }

    private GridPane buildGrid(int cols, int rows) {
	GridPane rv = new GridPane();
	for (int i = 0; i < cols; i++) {
	    for (int j = 0; j < rows; j++) {
		//lay unchangable base floor (so placements dont remove grid)
		rv.add(buildTile(blankLoc, "FLOOR"), i, j);

		if (i == 6 && j == 0) //center of top row
		{
		    ImageView im = buildTile(startLoc, "SPAWN");
		    rv.add(im, i, j);
		    allImageViews[i][j] = im;
		    rv.add(robot, i, j);
		} else if (i == 6 && j == 12) //center of bottom row
		{
		    ImageView im = buildTile(finishLoc, "FINISH");
		    rv.add(im, i, j);
		    allImageViews[i][j] = im;
		} else {
		    ImageView im = buildTile(blankLoc, "BLANK");
		    rv.add(im, i, j);
		    allImageViews[i][j] = im;
		}

		//System.out.printf("Adding tile to loc (%d, %d)\n", i, j);
	    }
	}
	rv.setOnMouseClicked(mouseEvent -> onGridClicked(mouseEvent));

	//center gameboard
	//rv.setAlignment(Pos.CENTER);
	return rv;
    }

    private void resetGame() {
	//GridPane myGrid = buildGrid(COLS, ROWS);
	mainGrid = buildGrid(COLS, ROWS);
	root.setCenter(mainGrid);
	menuItemOpen.setDisable(false);
	menuItemGo.setDisable(false);
    }

    private ImageView buildTile(String src, String usrData) {
	ImageView rv = new ImageView(new Image(getClass().getResource(src).toString()));
	rv.setUserData(usrData);
	rv.setOnDragOver(dragOverEvent -> onDragOver(dragOverEvent));
	rv.setOnDragDropped(dragDroppedEvent -> onDragDropped(dragDroppedEvent));
	GridPane.setHalignment(rv, HPos.CENTER);
	GridPane.setValignment(rv, VPos.CENTER);

	//rv.setOnDragOver(dragOverEvent -> onDragOver(dragOverEvent));
	//rv.setOnDragDropped(dragDroppedEvent -> onDragDropped(dragDroppedEvent));
	//rv.getStyleClass().add("cell");
	return rv;
    }

    private void onGridClicked(MouseEvent mouseEvent) {
	//cast target click back into imageview
	ImageView target = (ImageView) mouseEvent.getTarget();
	String targetString = target.getUserData().toString();
	//check target tile type, protect safe tiles
	if (targetString.startsWith("SPAWN") || targetString.startsWith("FINISH") || targetString.startsWith("ROBOT")) {
	    popupAlert(AlertType.WARNING, "Invalid Location", "That is a reserved spot. It cannot be replaced or removed!");
	} else if (buttonToggle.getSelectedToggle() == null) {
	    popupAlert(AlertType.ERROR, "No tool selected", "Please select a tool before trying to make a placement!");
	} else {

	    System.out.printf("User clicked: %s\n", target.getUserData().toString()); //for debug
	    ToggleButton temp = (ToggleButton) buttonToggle.getSelectedToggle();
	    canPlace(temp.getGraphic().getUserData().toString(), target);

	}
	//System.out.println(target.getUserData().toString());
    }

    private boolean canPlace(String targetName, ImageView target) {
	switch (targetName.toUpperCase()) {
	    case "BELTUP":
		target.setImage(beltUp.getImage());
		target.setUserData(beltUp.getUserData());
		break;
	    case "BELTDOWN":
		target.setImage(beltDown.getImage());
		target.setUserData(beltDown.getUserData());
		break;
	    case "BELTLEFT":
		target.setImage(beltLeft.getImage());
		target.setUserData(beltLeft.getUserData());
		break;
	    case "BELTRIGHT":
		target.setImage(beltRight.getImage());
		target.setUserData(beltRight.getUserData());
		break;
	    case "SWITCHUP":
		target.setImage(switchUp.getImage());
		target.setUserData(switchUp.getUserData());
		break;
	    case "SWITCHDOWN":
		target.setImage(switchDown.getImage());
		target.setUserData(switchDown.getUserData());
		break;
	    case "SWITCHLEFT":
		target.setImage(switchLeft.getImage());
		target.setUserData(switchLeft.getUserData());
		break;
	    case "SWITCHRIGHT":
		target.setImage(switchRight.getImage());
		target.setUserData(switchRight.getUserData());
		break;
	    case "DELETE":
		target.setImage(blank.getImage());
		target.setUserData(blank.getUserData());
		break;
	    default:
		return false;
	}

	return true;
    }

    private void popupAlert(AlertType type, String header, String msg) {
	Alert alert = new Alert(type);
	alert.setHeaderText(header);
	alert.setContentText(msg);
	alert.showAndWait();

    }

    private void onDragDetected(MouseEvent dragDetectedEvent) {
	//setStatus("onDragDetected");
	ToggleButton draggedButton = (ToggleButton) dragDetectedEvent.getSource();
	ImageView draggedImage = (ImageView) draggedButton.getGraphic();
	Dragboard myBoard = draggedImage.startDragAndDrop(TransferMode.COPY);
	myBoard.setDragView(draggedImage.getImage(), draggedImage.getImage().getWidth() / 2, draggedImage.getImage().getHeight() / 2);
	ClipboardContent clip = new ClipboardContent();
	clip.putString((String) draggedImage.getUserData());
	myBoard.setContent(clip);
    }

    private void onDragOver(DragEvent dragOverEvent) {
	//setStatus("onDragOver");
	ImageView targetImageView = (ImageView) dragOverEvent.getTarget();
	if (checkDragOverTile(targetImageView)) {
	    Dragboard myBoard = dragOverEvent.getDragboard(); //targetImageView.startDragAndDrop(TransferMode.COPY);
	    if (checkDragContent(myBoard)) {
		dragOverEvent.acceptTransferModes(TransferMode.COPY);
	    }
	}
    }

    private boolean checkDragOverTile(ImageView target) {
	String word = target.getUserData().toString();
	if (word.startsWith("BLANK") || word.startsWith("BELT") || word.startsWith("SWITCH")) {
	    return true;
	}
	return false;
    }

    private boolean checkDragContent(Dragboard d) {
	String content = d.getString();
	if (content.startsWith("BLANK") || content.startsWith("BELT") || content.startsWith("SWITCH")) {
	    return true;
	}
	return false;
    }

    private void onDragDropped(DragEvent dragDroppedEvent) {
	//setStatus("onDragDropped");
	ImageView targetImageView = (ImageView) dragDroppedEvent.getTarget();
	Dragboard myBoard = dragDroppedEvent.getDragboard();

	dragDroppedEvent.setDropCompleted(canPlace(myBoard.getString(), targetImageView));
    }

    private void startGame() {
	if (!challengeLoaded) {
	    popupAlert(AlertType.WARNING, "Game not ready!", "Please load a challenge file before starting the game");
	} else {
	    System.out.println("THE GAME IS STARTED");
	    Stage fake = new Stage();
	    mainGrid.requestFocus();
	    menuItemOpen.setDisable(true);
	    menuItemGo.setDisable(true);
	    menuItemPause.setDisable(false);
	    gameController.buildRobotQueue();
	    gameController.startGame();
	    //TODO: send GUI gameboard data to model for processing
	    gameBoard.setBoard(buildMap());
	    timer.start();

	    //gameStarted.set(!gameStarted.getValue());
	}
	//throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void pauseGame() {
	menuItemGo.setDisable(false);
	menuItemOpen.setDisable(false);
	menuItemPause.setDisable(true);
	timer.stop();

    }

    private String[][] buildMap() {
	String[][] map = new String[COLS][ROWS];
	for (int i = 0; i < COLS; i++) {
	    for (int j = 0; j < ROWS; j++) {
		//TODO: keep track 
		map[i][j] = allImageViews[i][j].getUserData().toString();
		//System.out.printf("IMG: %d = %s\n", i * 13 + j, map[i][j]);
		//try to get index from gridpane
//		ImageView imageView = (ImageView)mainGrid.getChildren().get(i*13 + j);
//		map[i][j] = imageView.getUserData().toString();
	    }
	}
	return map;
    }

    private void adjustSpeed() {
	if (normalSpeed) {
	    //speed it up, halve the tick interval
	    speedUp();
	} else {
	    //slow it back down, double the tick interval
	    slowDown();
	}
	normalSpeed = !normalSpeed;
    }

    private void speedUp() {
	if (normalSpeed) {
	    TIMER_TICK = TIMER_TICK / 2;
	    menuItemFaster.setText("Slower");
	    menuItemFaster.setAccelerator(new KeyCodeCombination(KeyCode.DOWN));
	    System.out.println("Speeding up...");
	} else {
	    System.out.println("Already going faster.");
	}
    }

    private void slowDown() {
	if (!normalSpeed) {
	    TIMER_TICK = TIMER_TICK * 2;
	    menuItemFaster.setText("Faster");
	    menuItemFaster.setAccelerator(new KeyCodeCombination(KeyCode.UP));
	    System.out.println("Slowing down...");
	} else {
	    System.out.println("Already going normal speed");
	}
    }
}
