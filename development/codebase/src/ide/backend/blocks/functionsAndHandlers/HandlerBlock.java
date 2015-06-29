package ide.backend.blocks.functionsAndHandlers;

import java.util.ArrayList;

import ide.backend.blocks.Block;
import ide.backend.blocks.variablesAndTypes.VariableBlock;
import ide.backend.core.Proces;
import ide.backend.exceptions.BreakException;
import ide.backend.exceptions.FunctionNotFoundException;
import ide.backend.exceptions.LockedException;
import ide.backend.exceptions.TypeException;
import ide.backend.exceptions.VariableNotFoundException;
import ide.backend.variables.EventInstance;
import ide.backend.variables.Variable;
import ide.backend.variables.Variable.VariableType;

/**
 * The handler block pushes its entire body onto the stack
 * It will also push it's variable "eventinstance" onto the first stackframe
 * @author Axel
 */
public class HandlerBlock implements Block {
	
	/** Body of the handler */
	private ArrayList<Block> _body;
	
	/** Eventinstance that is bound to this handler */
	private EventInstance _instance;
	
	/**
	 * Creates a new handlerblock with given body.
	 * @param body the body of the handler
	 */
	public HandlerBlock(ArrayList<Block> body) {
		_body = body;
		_instance = null;
	}
	
	/**
	 * Set the event instance
	 * @param instance EventInstance being handled by the handler.
	 */
	public void setEvent(EventInstance instance) {
		_instance = instance;
	}

	@Override
	public Variable execute(Proces p) throws TypeException, BreakException, LockedException, VariableNotFoundException, FunctionNotFoundException {
		p.pushStackFrame();
		
		p.pushBlock(new PopBlock());
		for(int i=_body.size()-1; i >= 0; i--) {
			Block b = _body.get(i);
			p.pushBlock(b);
		}
		
		(new VariableBlock(_instance.getEvent().getType(), VariableType.EVENT)).execute(p);
		Variable var = p.getVar(_instance.getEvent().getType());
		var.setContent(_instance);
		
		return null;
	}

}
