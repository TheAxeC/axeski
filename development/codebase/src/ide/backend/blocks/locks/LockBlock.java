package ide.backend.blocks.locks;

import ide.backend.blocks.Block;
import ide.backend.core.Proces;
import ide.backend.exceptions.BreakException;
import ide.backend.exceptions.FunctionNotFoundException;
import ide.backend.exceptions.LockedException;
import ide.backend.exceptions.TypeException;
import ide.backend.exceptions.VariableNotFoundException;
import ide.backend.variables.Variable;

/**
 * Block for locking a variable of the instance of the process in which this block is executed.
 * @author matthijs
 *
 */
public class LockBlock implements Block {

	/** name of the variable */
	private String _name;
	
	/**
	 * Creates a new lockblock that lock variable with given name.
	 * @param var
	 */
	public LockBlock(String var) {
		_name = var;
	}
	
	@Override
	public Variable execute(Proces p) throws TypeException, BreakException, LockedException,VariableNotFoundException, FunctionNotFoundException {
		p.lock(_name);
		return null;
	}
}
