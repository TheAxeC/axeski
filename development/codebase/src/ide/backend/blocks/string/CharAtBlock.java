package ide.backend.blocks.string;

import ide.backend.blocks.Block;
import ide.backend.core.Proces;
import ide.backend.exceptions.BreakException;
import ide.backend.exceptions.FunctionNotFoundException;
import ide.backend.exceptions.LockedException;
import ide.backend.exceptions.TypeException;
import ide.backend.exceptions.VariableNotFoundException;
import ide.backend.variables.NumberVariable;
import ide.backend.variables.StringVariable;
import ide.backend.variables.Variable;

/**
 * A charat block
 * Returns the char at the indicated index
 * @author axel
 *
 */
public class CharAtBlock implements Block {

	/** string block */
	private Block _str;
	
	private Block _index;
	
	/**
	 * Create a char at block
	 * @param str string to access
	 * @param index index to access
	 */
	public CharAtBlock(Block str, Block index) {
		_str = str;
		_index = index;
	}

	@Override
	public Variable execute(Proces p) throws TypeException, BreakException, LockedException, VariableNotFoundException, FunctionNotFoundException {
		Variable str = _str.execute(p);
		double ind = NumberVariable.parseVariable(_index.execute(p));
		
		return new StringVariable("" + str.toStringVariable().toString().charAt((int) ind));
	}
}
