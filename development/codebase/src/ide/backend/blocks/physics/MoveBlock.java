package ide.backend.blocks.physics;

import ide.backend.blocks.Block;
import ide.backend.core.Proces;
import ide.backend.exceptions.BreakException;
import ide.backend.exceptions.FunctionNotFoundException;
import ide.backend.exceptions.LockedException;
import ide.backend.exceptions.TypeException;
import ide.backend.exceptions.VariableNotFoundException;
import ide.backend.variables.NumberVariable;
import ide.backend.variables.Variable;

/**
 * Increments the x and y position of the instance where this proces is executed with,
 * with the variables given by the execution of the x and y blocks. 
 * Both need to be of type: VariableType.NUMBER or a VariableType.VALUE that can be parsed as a number.
 * If not a relevant type exception is thrown.
 * @author Matthijs Kaminski
 *
 */
public class MoveBlock implements Block{
	
	/**FIELDS**/
	private Block _x;
	private Block _y;
	
	public MoveBlock(Block x, Block y) {
		_x = x;
		_y = y;
	}

	@Override
	public Variable execute(Proces p) throws TypeException, LockedException,
			VariableNotFoundException, FunctionNotFoundException,
			BreakException {
		Variable computedX = _x.execute(p);
		Variable computedY = _y.execute(p);
		p.moveInstance(NumberVariable.parseVariable(computedX),NumberVariable.parseVariable(computedY) );
		return null;
	}

}
