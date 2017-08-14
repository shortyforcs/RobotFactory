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
public class ResolutionTile implements TileType {
    private TileResult result;
    
    public ResolutionTile(TileResult t)
    {
	result = t;
    }
    @Override
    public TileResult process(Robot r) {
	return result;
//	throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
