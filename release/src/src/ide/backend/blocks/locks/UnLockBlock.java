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
 * This block unlocks the variable which it contains.
 * @author matthijs
 *
 */
public class UnLockBlock implements Block {

	/** name of the variable */
	private String _name;
	
	/**
	 * Creates a new unlock block. Unlocks given variable.
	 * @param var name of the variable being unlocked.
	 */
	public UnLockBlock(String var) {
		_name = var;
	}
	
	@Override
	public Variable execute(Proces p) throws TypeException, BreakException, LockedException,VariableNotFoundException, FunctionNotFoundException {
		p.unlock(_name);
		return null;
	}
}

