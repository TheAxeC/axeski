package ide.backend.blocks.conditions;

import ide.backend.blocks.Block;
import ide.backend.core.Proces;
import ide.backend.exceptions.BreakException;
import ide.backend.exceptions.FunctionNotFoundException;
import ide.backend.exceptions.LockedException;
import ide.backend.exceptions.TypeException;
import ide.backend.exceptions.VariableNotFoundException;
import ide.backend.variables.BooleanVariable;
import ide.backend.variables.Variable;

import java.util.ArrayList;

/**
 * Represents an if-else contruct
 * It build upon an if-block
 * @author axel
 *
 */
public class IfElseBlock implements Block {
	/**FIELDS**/
	/** Body of the ifBlock */
	private ArrayList<Block> _bodyIf;
	/** Body of the elseBlock **/
	private ArrayList<Block> _bodyElse;
	/**conditon of the ifblock**/
	private Block _condition;
	
	/**
	 * Creates a new IfElseBlock with given condition
	 * @param condition block representing the condition.
	 * @param bodyIf body of the if.
	 * @param bodyElse body of the else.
	 */
	public IfElseBlock(Block condition, ArrayList<Block> bodyIf, ArrayList<Block> bodyElse) {
		_bodyIf = bodyIf;
		_bodyElse = bodyElse;
		_condition = condition;
	}

	@Override
	public Variable execute(Proces p) throws TypeException, LockedException,
			VariableNotFoundException, FunctionNotFoundException, BreakException {
		Variable v = _condition.execute(p);

		if(BooleanVariable.parseVariable(v)){
			for(int i=_bodyIf.size()-1; i >= 0; i--) {
				Block b = _bodyIf.get(i);
				p.pushBlock(b);
			}
		} else {
			for(int i=_bodyElse.size()-1; i >= 0; i--) {
				Block b = _bodyElse.get(i);
				p.pushBlock(b);
			}
		}
		return null;
	}

}
