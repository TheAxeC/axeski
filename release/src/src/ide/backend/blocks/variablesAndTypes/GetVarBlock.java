package ide.backend.blocks.variablesAndTypes;

import ide.backend.blocks.Block;
import ide.backend.core.Proces;
import ide.backend.exceptions.BreakException;
import ide.backend.exceptions.FunctionNotFoundException;
import ide.backend.exceptions.LockedException;
import ide.backend.exceptions.TypeException;
import ide.backend.exceptions.VariableNotFoundException;
import ide.backend.variables.Variable;

/**
 * This Block is used to use the content of a variable.
 * The execute of the block will return the variable of the given name found in the top functionFrame or in the member of the instance of the process.
 * @author matthijs
 *
 */
public class GetVarBlock implements Block{

	/*FIELDS*/
	private String _name;
	
	/**
	 * Creates a new block for accessing a variable of the given name
	 * @param name name of the variable to get.
	 */
	public GetVarBlock(String name) {
		_name = name;
	}
	

	@Override
	public Variable execute(Proces p) throws TypeException, BreakException, LockedException, VariableNotFoundException, FunctionNotFoundException {
		return p.getVar(_name);
	}

}
