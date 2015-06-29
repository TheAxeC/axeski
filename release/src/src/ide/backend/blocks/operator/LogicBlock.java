
package ide.backend.blocks.operator;

import ide.backend.blocks.Block;
import ide.backend.core.Proces;
import ide.backend.exceptions.BreakException;
import ide.backend.exceptions.FunctionNotFoundException;
import ide.backend.exceptions.LockedException;
import ide.backend.exceptions.TypeException;
import ide.backend.exceptions.VariableNotFoundException;
import ide.backend.variables.BooleanVariable;
import ide.backend.variables.LogicOperators;
import ide.backend.variables.Variable;
import ide.backend.variables.Variable.VariableType;

/**
 * Represents an logic operation
 * @author Axel
 */
public class LogicBlock implements Block {
	
	/** left block */
	private Block _left;
	
	/** right block */
	private Block _right;
	
	/** the operation to execute */
	private String _operation;

	/**
	 * Creates a new LogicBlock for a logic operator.
	 * @param left operand
	 * @param operation operation
	 * @param right right operand
	 */
	public LogicBlock(Block left, String operation, Block right) {
		_left = left;
		_operation = operation;
		_right = right;
	}

	@Override
	public Variable execute(Proces p) throws TypeException, BreakException, LockedException, VariableNotFoundException, FunctionNotFoundException {
		Variable l = _left.execute(p);
		Variable r = _right.execute(p);
		if (l.getType() == VariableType.VALUE)
			l = new BooleanVariable(l);
		return LogicOperators.operators.get(_operation).operation(l,r);
	}
}