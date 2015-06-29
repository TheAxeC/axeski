package ide.backend.blocks.functionsAndHandlers;

import ide.backend.blocks.Block;
import ide.backend.core.Proces;
import ide.backend.exceptions.BreakException;
import ide.backend.exceptions.FunctionNotFoundException;
import ide.backend.exceptions.LockedException;
import ide.backend.exceptions.TypeException;
import ide.backend.exceptions.VariableNotFoundException;
import ide.backend.variables.Variable;

/**
 * The pop block pops the top stackframe from a given proces
 * @author Axel
 */
public class PopBlock implements Block {

	/**
	 * Creates a new PopBlock.
	 */
	public PopBlock() {
	}

	@Override
	public Variable execute(Proces p) throws TypeException, BreakException, LockedException, VariableNotFoundException, FunctionNotFoundException {
		p.popStackFrame();
		return null;
	}

}
