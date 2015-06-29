package ide.backend.blocks;

import ide.backend.core.Proces;
import ide.backend.exceptions.*;
import ide.backend.variables.Variable;


/**
 * The basic execution block
 * This interface forms the base for every execution block from the IDE
 * @author axelfaes
 *
 */
public interface Block {
	
	/**
	 * Execute this block
	 * @param p, the proces on which the block operates
	 * @return null, the result of an operation, an event to send
	 * @throws TypeException, thrown if types aren't matched
	 * @throws LockedException, thrown if the block tries to acces a locked variable
	 * @throws VariableNotFoundException, thrown if the block cant find a variable
	 * @throws FunctionNotFoundException, thrown if the block cant find a function
	 * @throws BreakException, thrown if the block has to break
	 */
	public Variable execute(Proces p) throws TypeException, LockedException, VariableNotFoundException, FunctionNotFoundException, BreakException;
}
