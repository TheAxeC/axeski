package ide.backend.blocks.variablesAndTypes;

import ide.backend.blocks.Block;
import ide.backend.core.Proces;
import ide.backend.exceptions.BreakException;
import ide.backend.exceptions.FunctionNotFoundException;
import ide.backend.exceptions.LockedException;
import ide.backend.exceptions.TypeException;
import ide.backend.exceptions.VariableNotFoundException;
import ide.backend.variables.Variable;
import ide.backend.variables.Variable.VariableType;

/**
 * Creates a new variable of a given type [type] and name [name] onto the current stackframe
 * @author Axel
 */
public class VariableBlock implements Block {
	
	/** Name of the variable to create */
	private String _name;
	
	/** type of the variable to create */
	private VariableType _type;
	
	/**
	 * Creates a new variable of a given type [type] and name [name] onto the current stackframe.
	 * @param name name of the variable.
	 * @param type type of the variable.
	 */
	public VariableBlock(String name, VariableType type) {
		_name = name;
		_type = type;
	}

	@Override
	public Variable execute(Proces p) throws TypeException, BreakException, LockedException, VariableNotFoundException, FunctionNotFoundException {
		p.pushVar(_name, _type);
		return null;
	}
	
	public String getName() {
		return _name;
	}

}
