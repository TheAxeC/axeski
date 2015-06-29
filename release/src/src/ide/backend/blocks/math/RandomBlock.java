package ide.backend.blocks.math;

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
 * Returns a new numbervariable with random content between 0.0 and 1.0 as defined by java.lang.Math.random()
 * @author Matthijs Kaminski
 *
 */
public class RandomBlock implements Block{

	/**
	 * Creates a new randomBlock to be executed.
	 */
	public RandomBlock() {
	}
	@Override
	public Variable execute(Proces p) throws TypeException, LockedException,
			VariableNotFoundException, FunctionNotFoundException,
			BreakException {
		return new NumberVariable(Math.random());
	}

}
