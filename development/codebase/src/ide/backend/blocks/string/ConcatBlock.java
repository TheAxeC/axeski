package ide.backend.blocks.string;

import ide.backend.blocks.Block;
import ide.backend.core.Proces;
import ide.backend.exceptions.BreakException;
import ide.backend.exceptions.FunctionNotFoundException;
import ide.backend.exceptions.LockedException;
import ide.backend.exceptions.TypeException;
import ide.backend.exceptions.VariableNotFoundException;
import ide.backend.variables.ArithOperators;
import ide.backend.variables.Variable;

/**
 * Represents the concatonation of two strings
 * @author axel
 *
 */
public class ConcatBlock implements Block {

	/** left block */
	private Block _left;
	
	/** right block */
	private Block _right;
	
	/**
	 * Create a concat block
	 * @param left the left string block
	 * @param right the right string block
	 */
	public ConcatBlock(Block left, Block right) {
		_left = left;
		_right = right;
	}

	@Override
	public Variable execute(Proces p) throws TypeException, BreakException, LockedException, VariableNotFoundException, FunctionNotFoundException {
		Variable l = _left.execute(p);
		Variable r = _right.execute(p);
		
		return ArithOperators.operators.get("+").operation(l.toStringVariable(),r).toStringVariable();
	}
}
