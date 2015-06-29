package ide.backend.blocks.variablesAndTypes;

import ide.backend.blocks.Block;
import ide.backend.core.Proces;
import ide.backend.exceptions.BreakException;
import ide.backend.exceptions.FunctionNotFoundException;
import ide.backend.exceptions.LockedException;
import ide.backend.exceptions.TypeException;
import ide.backend.exceptions.VariableNotFoundException;
import ide.backend.variables.Variable;

/*
 * Sets the variable with name [name] to the result of block [content]
 */
public class SetBlock implements Block {
	
	/** name of the variable */
	private String _name;
	
	/** content of the variable */
	private Block _content;

	/**
	 * Sets the variable with name [name] to the result of block [content]
	 * @param name name of the variable
	 * @param content content whose executed value is set to be the value of the lefthand variable.
	 */
	public SetBlock(String name, Block content) {
		_name = name;
		_content = content;
	}

	@Override
	public Variable execute(Proces p) throws TypeException, BreakException, LockedException, VariableNotFoundException, FunctionNotFoundException {
		Variable var = p.getVar(_name);
		var.setContent(_content.execute(p));
		return null;
	}

}
