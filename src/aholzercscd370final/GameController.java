/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aholzercscd370final;

import fileio.ChallengeFile;
import gameboard.Robot;
import gameboard.GameBoard;
import gameboard.ResultSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 *
 * @author short
 */
public class GameController {

    private GameBoard myBoard;
    private Queue<Robot> robotQueue;
    private ChallengeFile myChallenge;
    private ResultSet resultSet;
    
    public GameController(GameBoard board) {
	myBoard = board;
    }

    public void setChallengeFile(ChallengeFile c)
    {
	myChallenge = c;
    }
    public void startGame() {
	//Robot robot = new Robot("TEMP", "TEMP");
	//myBoard.startRobot(robot);
	//myBoard.setBoard(map);

	myBoard.startRobot(robotQueue.poll());
    }
    
    public void buildRobotQueue()
    {
	int cases = myChallenge.getCases();
	robotQueue = new LinkedList<Robot>();
	for(int i = 0; i < cases; i++)
	{
	    //base components from challenge file
	    String caseCondition = myChallenge.getCaseCondition()[i];
	    String tape = myChallenge.getTapes()[i];
	    //convert to robot components (tape queue and condition)
	    boolean pass = ((caseCondition.equalsIgnoreCase("P")) ? true : false);
	    Queue newTape = new LinkedList<>();
	    for(int j = 0; j < tape.length(); j++)
	    {
		newTape.add(tape.charAt(j) + "");
	    }
	    Robot r = new Robot(newTape, pass);
	    robotQueue.add(r);
	}
    }
    
    public boolean hasMoreRobots() {
	return robotQueue.peek() != null;
    }

    // Might do this
    public ResultSet doNext() {
	TileResult result = myBoard.doNext();
	
	if(resultSet == null)
	    resultSet = new ResultSet(false, false, null);
	
	resultSet.setAccepted(result == TileResult.PASS);
	resultSet.setDone(myBoard.getRobot());
	resultSet.setGameOver(result == TileResult.PASS || result == TileResult.FAIL);
	
	return resultSet;
    }

}
