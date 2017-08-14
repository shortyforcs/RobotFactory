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
public class Belt implements TileType {

    private String direction;
    
    public Belt(String direction)
    {
	this.direction = direction;
    }
    
    public String getDirection()
    {
	return direction;
    }
    
    
    @Override
    public TileResult process(Robot r) {
	switch(direction.toUpperCase())
	{
	    case "UP":
		return TileResult.UP;
		
	    case "DOWN":
		return TileResult.DOWN;
		
	    case "LEFT":
		return TileResult.LEFT;
		
	    case "RIGHT":
		return TileResult.RIGHT;
	    default:
		return TileResult.DOWN;
	}
    }
    
}
