package ide.backend.blocks.eventsAndEmits;

import java.util.HashMap;
import java.util.Map.Entry;

import ide.backend.blocks.Block;
import ide.backend.core.Proces;
import ide.backend.exceptions.BreakException;
import ide.backend.exceptions.FunctionNotFoundException;
import ide.backend.exceptions.LockedException;
import ide.backend.exceptions.TypeException;
import ide.backend.exceptions.VariableNotFoundException;
import ide.backend.variables.EventInstance;
import ide.backend.variables.Variable;

/**
 * Emits an event
 * @author Axel
 *
 */
public class EmitBlock implements Block {

	private String _name;
	
	private HashMap<String, Block> _members;
	
	public EmitBlock(String name, HashMap<String, Block> members) {
		_name = name;
		_members = members;
	}

	@Override
	public Variable execute(Proces p) throws TypeException, LockedException, VariableNotFoundException, FunctionNotFoundException, BreakException {
		EventInstance inst = p.getEvent(_name).makeEventInstance();
		
		for(Entry<String, Block> entry : _members.entrySet()) {
		    String key = entry.getKey();
		    Block b = entry.getValue();

		    inst.getVar(key).setContent(b.execute(p));
		}

		return inst;
	}

}
