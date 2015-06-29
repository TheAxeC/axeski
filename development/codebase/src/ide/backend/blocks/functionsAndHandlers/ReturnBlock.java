package ide.backend.blocks.functionsAndHandlers;

import java.util.ArrayList;

import ide.backend.blocks.Block;
import ide.backend.core.Proces;
import ide.backend.exceptions.BreakException;
import ide.backend.exceptions.FunctionNotFoundException;
import ide.backend.exceptions.LockedException;
import ide.backend.exceptions.TypeException;
import ide.backend.exceptions.VariableNotFoundException;
import ide.backend.variables.Variable;

/**
 * ReturnBlock pushes the return values on the stack in order as they appear in the array of blocks.
 * Then it pops block of the stack until it has found a popBlock. Which it executes.
 * @author matthijs
 *
 */
public class ReturnBlock implements Block{
	
	/*FIELDS*/
	/** Array of block representing the return values.**/
	private ArrayList<Block> _innerBlock;
	
	/**
	 * Creates a new returnBlock. With the return values given by the execution of the blocks [innerblocks] in the returnblock.
	 * @param innerBlock blocks holding values to be returned.
	 */
	public ReturnBlock(ArrayList<Block> innerBlock) {
		_innerBlock = innerBlock;
	}
	
	
	@Override
	public Variable execute(Proces p) throws TypeException, BreakException, LockedException,VariableNotFoundException, FunctionNotFoundException {
		//push return values.
		for (Block block : _innerBlock) {
			p.pushReturn(block.execute(p));
		}
		
		//pop until popBlock found.
		Block b = p.popBlock();
		while(b.getClass() != PopBlock.class && b != null)
			b = p.popBlock();
		//excute popBlock.
		if(b != null)
			b.execute(p);
		
		return null;
	}

}
