package ide.backend.blocks.variablesAndTypes;

import ide.backend.blocks.Block;
import ide.backend.core.Proces;
import ide.backend.exceptions.BreakException;
import ide.backend.exceptions.FunctionNotFoundException;
import ide.backend.exceptions.LockedException;
import ide.backend.exceptions.TypeException;
import ide.backend.exceptions.VariableNotFoundException;
import ide.backend.variables.Variable;
import ide.backend.variables.ValueVariable;

/**
 * A block which creates a new valueVariable containing the value the user provided via userinput in the IDE.
 * @author matthijs
 *
 */
public class ValueBlock implements Block{
	/*FIELDS*/
	private String _content;
	
	/*
	 * Creates a block with the value [content] given by the user.
	 */
	public ValueBlock(String content) {
		_content = content;
	}
	
	@Override
	public Variable execute(Proces p) throws TypeException, BreakException, LockedException, VariableNotFoundException, FunctionNotFoundException {
		return new ValueVariable(_content);
	}

}
