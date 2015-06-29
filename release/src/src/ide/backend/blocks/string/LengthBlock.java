package ide.backend.blocks.string;

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
 * Represents the length of a string.
 * @author axel
 *
 */
public class LengthBlock implements Block {

	/** string block */
	private Block _str;
	
	/**
	 * Creates a new lengthBlock which returns the length of given string in a NumberVariable.
	 * @param str Str whose length is calculated.
	 */
	public LengthBlock(Block str) {
		_str = str;
	}

	@Override
	public Variable execute(Proces p) throws TypeException, BreakException, LockedException, VariableNotFoundException, FunctionNotFoundException {
		Variable l = _str.execute(p);
		return new NumberVariable(l.toStringVariable().toString().length());
	}
}