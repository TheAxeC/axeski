package ide.backend.blocks.conditions;

import java.util.ArrayList;

import ide.backend.blocks.Block;
import ide.backend.core.Proces;
import ide.backend.exceptions.BreakException;
import ide.backend.exceptions.FunctionNotFoundException;
import ide.backend.exceptions.LockedException;
import ide.backend.exceptions.TypeException;
import ide.backend.exceptions.VariableNotFoundException;
import ide.backend.variables.BooleanVariable;
import ide.backend.variables.Variable;

/**
 * The while-Block will push it's entire body on the stack if it condition evalutes to true. But it will also push itself.
 * @author matthijs
 *
 */
public class WhileBlock implements Block{
	
	/**FIELDS**/
	/** Body of the whileBlock */
	private ArrayList<Block> _body;
	/**conditon of the ifblock**/
	private Block _condition;

	
	/**
	 * Creates an new whileBlock for execution.
	 * @param condition condition for the while block.
	 * @param body body of the while block.
	 */
	public WhileBlock(Block condition, ArrayList<Block> body) {
		_body = body;
		_condition = condition;
	}
	
	@Override
	public Variable execute(Proces p) throws TypeException, LockedException,
			VariableNotFoundException, FunctionNotFoundException, BreakException {
		Variable v = _condition.execute(p);
		if(BooleanVariable.parseVariable(v)){
			p.pushBlock(this);
			for(int i=_body.size()-1; i >= 0; i--) {
				Block b = _body.get(i);
				p.pushBlock(b);
			}
			
		}
		return null;
	}

}
