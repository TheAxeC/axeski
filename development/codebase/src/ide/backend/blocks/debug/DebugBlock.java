package ide.backend.blocks.debug;

import ide.backend.blocks.Block;
import ide.backend.core.Proces;
import ide.backend.exceptions.BreakException;
import ide.backend.exceptions.FunctionNotFoundException;
import ide.backend.exceptions.LockedException;
import ide.backend.exceptions.TypeException;
import ide.backend.exceptions.VariableNotFoundException;
import ide.backend.model.BlockModel;
import ide.backend.variables.Variable;

/**
 * Block used for debugging
 * Can set and unset debugging and set break points
 * @author axelfaes
 *
 */
public class DebugBlock implements Block {
	/**FIELDS**/
	/** Body of the ifBlock */
	private BlockModel _model;
	
	/** Do we need to turn debug on or off */
	private boolean _condition;
	
	/** Turned on if this block needs to be a breakpoint */ 
	private boolean _break;
	
	/**
	 * @param model model to notify changes to
	 * @param condition debug status
	 * @param brk break condition
	 */
	public DebugBlock(BlockModel model, boolean condition, boolean brk) {
		_model = model;
		_condition = condition;
		_break = brk;
	}
	
	/**
	 * Set the break status
	 * @param brk break status
	 */
	public void setBreak(boolean brk) {
		_break = brk;
	}

	@Override
	public Variable execute(Proces p) throws TypeException, LockedException,
			VariableNotFoundException, FunctionNotFoundException, BreakException {
		_model.setDebug(_condition);

		if (_break)
			throw new BreakException();
		
		return null;
	}

}