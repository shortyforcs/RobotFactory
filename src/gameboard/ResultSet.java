/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboard;

/**
 *
 * @author short
 */
public class ResultSet {
    boolean gameOver, accepted;
    Robot done;
    
    public ResultSet(boolean gameOver, boolean accepted, Robot done)
    {
	this.gameOver = gameOver;
	this.accepted = accepted;
	this.done = done;
    }

    public void setGameOver(boolean gameOver) {
	this.gameOver = gameOver;
    }

    public void setAccepted(boolean accepted) {
	this.accepted = accepted;
    }
    
    public boolean isCorrectRobotOutcome() {
	return accepted == done.isShouldPass();
    }

    public void setDone(Robot done) {
	this.done = done;
    }

    public boolean isGameOver() {
	return gameOver;
    }

    public boolean isAccepted() {
	return accepted;
    }

    public Robot getDone() {
	return done;
    }
}
