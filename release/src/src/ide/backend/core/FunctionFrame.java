package ide.backend.core;

import ide.backend.exceptions.VariableNotFoundException;
import ide.backend.variables.Variable;
import ide.backend.variables.VariableFactory;
import ide.backend.variables.Variable.VariableType;

import java.util.HashMap;

/**
 * Represents a stackframe from a function
 * @author Axel
 */
public class FunctionFrame {
	
	/** The mapping of the local variables within the function */
	private HashMap<String, Variable> _stack;

	public FunctionFrame() {
		_stack = new HashMap<>();
	}
	
	/**
	 * Getter for a variable from the functionFrame
	 * @param name, name of the variable to get
	 * @return the found variable
	 * @throws VariableNotFoundException, thrown if the variable is not found
	 */
	public Variable getVar(String name) throws VariableNotFoundException {
		Variable ret = _stack.get(name);
		if (ret == null)
			throw new VariableNotFoundException(name);
		return ret;
	}
	
	/**
	 * Creates a new Variable of type [type] and name [name]
	 * If a variable with name [name] should already exist, this
	 * 		function will override the variable
	 * Afterwards this variable is stored onto the functionframe
	 * @param name, name of the variable
	 * @param type, type of the variable
	 */
	public void pushVar(String name, VariableType type) {
		_stack.put(name, VariableFactory.create(type));
	}

}
