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
public class Switch implements TileType {

    String fedFromDirection;

    public Switch(String direction) {
	fedFromDirection = direction;
    }

    @Override
    public TileResult process(Robot r) {

	switch (r.getNextTapeItem()) {
	    case "R":
		return determineRedMove(fedFromDirection);

	    case "B":
		return determineBlueMove(fedFromDirection);
		
	    default:
		return determineDefaultMove(fedFromDirection);
	    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
    }

    private TileResult determineRedMove(String direction) {
	switch (fedFromDirection) {
	    case "UP":
		return TileResult.RIGHT;
	    case "DOWN":
		return TileResult.LEFT;
	    case "LEFT":
		return TileResult.UP;
	    case "RIGHT":
		return TileResult.DOWN;
	}
	return null;
    }

    private TileResult determineBlueMove(String direction) {
	switch (fedFromDirection) {
	    case "UP":
		return TileResult.LEFT;
	    case "DOWN":
		return TileResult.RIGHT;
	    case "LEFT":
		return TileResult.DOWN;
	    case "RIGHT":
		return TileResult.UP;
	}
	return null;
    }

    private TileResult determineDefaultMove(String direction) {
	switch (fedFromDirection) {
	    case "UP":
		return TileResult.DOWN;
	    case "DOWN":
		return TileResult.UP;
	    case "LEFT":
		return TileResult.RIGHT;
	    case "RIGHT":
		return TileResult.LEFT;
	}
	return null;
    }
}
