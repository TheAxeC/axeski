package ide.backend.blocks.eventsAndEmits;

import ide.backend.blocks.Block;
import ide.backend.core.Proces;
import ide.backend.exceptions.BreakException;
import ide.backend.exceptions.FunctionNotFoundException;
import ide.backend.exceptions.LockedException;
import ide.backend.exceptions.TypeException;
import ide.backend.exceptions.VariableNotFoundException;
import ide.backend.variables.Variable;
import ide.backend.variables.EventInstance;;
/**
 * This block is used in the IDE to get a variable in an given event given to the handler. 
 * It returns the asked variable if it exists in the given event. (if the event exists)
 * The eventInstance is stored on the current functionFrame.
 * @author matthijs
 *
 */
public class AccesBlock implements Block {

	/*FIELDS*/
	private String _event;
	private String _var;
	
	/**
	 * Creates a block to access a given variable name (name) [var] form a given event (name) [event]
	 * @param event the event to acces
	 * @param var, the name of the variable to access
	 */
	public AccesBlock(String event, String var) {
		_event = event;
		_var = var;
	}
	
	@Override
	public Variable execute(Proces p) throws TypeException, LockedException, VariableNotFoundException, FunctionNotFoundException, BreakException {
		return ((EventInstance)p.getVar(_event)).getVar(_var);
	
	}

}
