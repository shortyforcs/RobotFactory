/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboard;

import java.util.Queue;

/**
 *
 * @author short
 */
public class Robot {
    //Will instead use one robot per tape
    private Queue<String> tape;    
    private boolean shouldPass;
    
    public boolean isShouldPass() {
	return shouldPass;
    }
    public Robot(Queue<String> tape, boolean pass)
    {
	this.tape = tape;
	this.shouldPass = pass;
    }
    public String getNextTapeItem()
    {
	String rv = tape.poll();
	if(rv == null)
	    rv = "NONE";
	return rv;
    }

    public String seeNextTapeItem()
    {
	String rv = tape.peek();
	if(rv == null)
	    rv = "NONE";
	return rv;
    }
}
