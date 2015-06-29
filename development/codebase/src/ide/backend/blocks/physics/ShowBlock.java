package ide.backend.blocks.physics;

import ide.backend.blocks.Block;
import ide.backend.core.Proces;
import ide.backend.exceptions.BreakException;
import ide.backend.exceptions.FunctionNotFoundException;
import ide.backend.exceptions.LockedException;
import ide.backend.exceptions.TypeException;
import ide.backend.exceptions.VariableNotFoundException;
import ide.backend.variables.Variable;

/**
 * Changes the visibility of the instance of the process executing this block.
 * @author Matthijs Kaminski
 */
public class ShowBlock implements Block{
	
	/**
	 * FIELDS
	 */
	private boolean _visibility;
	
	/**
	 * creates a block which execution will set the visibility of the instance executing 
	 * this block to the given visibility.
	 * @param visibility given visibility.
	 */
	public ShowBlock(boolean visibility) {
		_visibility = visibility;
	}

	@Override
	public Variable execute(Proces p) throws TypeException, LockedException,
			VariableNotFoundException, FunctionNotFoundException,
			BreakException {
		p.setVisibilityInstance(_visibility);
		return null;
	}

}
