
package ide.backend.blocks.operator;

import ide.backend.blocks.Block;
import ide.backend.core.Proces;
import ide.backend.exceptions.BreakException;
import ide.backend.exceptions.FunctionNotFoundException;
import ide.backend.exceptions.LockedException;
import ide.backend.exceptions.TypeException;
import ide.backend.exceptions.VariableNotFoundException;
import ide.backend.variables.ArithOperators;
import ide.backend.variables.Variable;
import ide.backend.variables.Variable.VariableType;

/**
 * Represents an arithmetic operation
 * @author Axel
 */
public class ArithBlock implements Block {
	
	/** left block */
	private Block _left;
	
	/** right block */
	private Block _right;
	
	/** the operation to execute */
	private String _operation;

	
	/**
	 * The arithmetic operator
	 * @param left, the left side of the operation
	 * @param operation, the string representation of the operation
	 * @param right, the right side of the operation
	 */
	public ArithBlock(Block left, String operation, Block right) {
		_left = left;
		_operation = operation;
		_right = right;
	}

	@Override
	public Variable execute(Proces p) throws TypeException, BreakException, LockedException, VariableNotFoundException, FunctionNotFoundException {
		Variable l = _left.execute(p);
		Variable r = _right.execute(p);
		if (l.getType() == VariableType.VALUE)
			l = l.toStringVariable();
		return ArithOperators.operators.get(_operation).operation(l,r);
	}

}


