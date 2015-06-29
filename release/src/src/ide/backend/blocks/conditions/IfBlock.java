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
 * The if-Block will push it's entire body on the stack if it condition evalutes to true.
 * @author matthijs
 *
 */
public class IfBlock implements Block{
	/**FIELDS**/
	/** Body of the ifBlock */
	private ArrayList<Block> _body;
	/**conditon of the ifblock**/
	private Block _condition;
	
	/**
	 * Creates a new ifBlock with given Condition and body
	 * @param condition condition of the if.
	 * @param body body of the if.
	 */
	public IfBlock(Block condition, ArrayList<Block> body) {
		_body = body;
		_condition = condition;
	}

	@Override
	public Variable execute(Proces p) throws TypeException, LockedException,
			VariableNotFoundException, FunctionNotFoundException, BreakException {
		Variable v = _condition.execute(p);

		if(BooleanVariable.parseVariable(v)){
			for(int i=_body.size()-1; i >= 0; i--) {
				Block b = _body.get(i);
				p.pushBlock(b);
			}
		}
		return null;
	}

}
