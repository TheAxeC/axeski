package ide.backend.blocks.debug;

import ide.backend.blocks.Block;
import ide.backend.core.Proces;
import ide.backend.exceptions.BreakException;
import ide.backend.exceptions.FunctionNotFoundException;
import ide.backend.exceptions.LockedException;
import ide.backend.exceptions.TypeException;
import ide.backend.exceptions.VariableNotFoundException;
import ide.backend.variables.Variable;

/**
 * This no operation block execution doesn't do anything. Can be used for busy waiting
 * although this uses resources
 * @author axel
 *
 */
public class NOPBlock implements Block {
	
	/**
	 * Create a new No operation block.
	 */
	public NOPBlock() {}
	
	@Override
	public Variable execute(Proces p) throws TypeException, LockedException,
			VariableNotFoundException, FunctionNotFoundException, BreakException {
		return null;
	}

}
