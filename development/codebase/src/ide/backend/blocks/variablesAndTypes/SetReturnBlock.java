package ide.backend.blocks.variablesAndTypes;

import java.util.ArrayList;

import ide.backend.blocks.Block;
import ide.backend.core.Proces;
import ide.backend.exceptions.BreakException;
import ide.backend.exceptions.FunctionNotFoundException;
import ide.backend.exceptions.LockedException;
import ide.backend.exceptions.TypeException;
import ide.backend.exceptions.VariableNotFoundException;
import ide.backend.variables.Variable;
/**
 * This block is used to fetch the returnValues from the stack for these variable and set the variables in the function where another
 * function was called equal to these values.
 * @author Matthijs Kaminski
 *
 */
public class SetReturnBlock implements Block{
	/*FIELDS*/
	ArrayList<String> _returns;
	/**
	 * This block is used to fetch the returnValues from the stack for these variable and set the variables in the function where another
	 * function was called equal to these values.
	 * @param returns variable set to catch the returns.
	 */
	public SetReturnBlock(ArrayList<String> returns) {
		_returns = returns;
	}
	@Override
	public Variable execute(Proces p) throws TypeException, BreakException, LockedException, VariableNotFoundException, FunctionNotFoundException {
		//The returnValues are pushed in the order as set in the return. So they pop in reverse. Hence we fill in the return variables in reverse order.
		if (p.hasReturns() < _returns.size()) throw new VariableNotFoundException("<ReturnValue>");
		for (int i = _returns.size()-1; i >= 0; i--) {
			p.getVar(_returns.get(i)).setContent(p.popReturn());
		}
		
		p.clearReturns();
		return null;
	}

}
