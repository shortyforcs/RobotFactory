/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboard;

import aholzercscd370final.TileResult;

/**
 *
 * @author short
 */
public class GameBoard {

    private static final int SIZE = 13;
    private TileType[][] board;
    private int xRobotCoord, yRobotCoord;
    private Robot robot;

    public GameBoard() {
	xRobotCoord = 6;
	yRobotCoord = 0;
	board = new TileType[SIZE][SIZE];
//	for (int i = 0; i < SIZE; i++) {
//	    board[6][i] = new Belt("DOWN");
//	}
    }

    //TODO: need to make sure my maps coorelate with one another lol
    public void setBoard(String[][] map) {
	for (int i = 0; i < SIZE; i++) {
	    for (int j = 0; j < SIZE; j++) {
		switch (map[i][j]) {
		    case "BELTUP":
			board[i][j] = new Belt("UP");
			break;
		    case "BELTDOWN":
		    case "SPAWN":
			board[i][j] = new Belt("DOWN");
			break;
		    case "BELTLEFT":
			board[i][j] = new Belt("LEFT");
			break;
		    case "BELTRIGHT":
			board[i][j] = new Belt("RIGHT");
			break;
		    case "SWITCHUP":
			board[i][j] = new Switch("UP");
			break;
		    case "SWITCHDOWN":
			board[i][j] = new Switch("DOWN");
			break;
		    case "SWITCHLEFT":
			board[i][j] = new Switch("LEFT");
			break;
		    case "SWITCHRIGHT":
			board[i][j] = new Switch("DOWN");
			break;
		    case "FINISH":
			board[i][j] = new ResolutionTile((TileResult.PASS));
			break;
		    default:
			board[i][j] = new ResolutionTile(TileResult.FAIL);
			break;
		}
	    }
	}
    }

    public void startRobot(Robot r) {
	robot = r;
	xRobotCoord = 6;
	yRobotCoord = 0;
    }

    public Robot getRobot() {
	return robot;
    }

    public TileResult doNext() {
	TileResult result = board[xRobotCoord][yRobotCoord].process(robot);
	switch (result) {
	    case UP:
		yRobotCoord--;
		break;
	    case DOWN:
		yRobotCoord++;
		break;
	    case LEFT:
		xRobotCoord--;
		break;
	    case RIGHT:
		xRobotCoord++;
		break;
	}

	return result;
    }

    public int getxRobotCoord() {
	return xRobotCoord;
    }

    public int getyRobotCoord() {
	return yRobotCoord;
    }

}
