package ide.backend.model.function;

import ide.backend.model.BlockModel;

import java.util.ArrayList;

/**
 * Base class for handlers and functions which contain local variables.
 * @author matthijs
 *
 */
public abstract class TopLevelModel extends BlockModel {
	/**FIELDS**/
	/*Array with names of the local variables.*/
	ArrayList<String> _localVariables;
	
	public TopLevelModel(BlockModel parent) {
		super(parent);
		_localVariables = new ArrayList<String>();
	}
	
	/**
	 * Add a variablebame to the toplevel
	 * @param name name of the variable
	 */
	public void addVariable(String name){
		_localVariables.add(name);
	}
	
	/**
	 * Remove a variable from the toplevel
	 * @param name name of the variable
	 */
	public void removeVariable(String name){
		_localVariables.remove(name);
	}
	
	/**
	 * Checks wheter a toplevel block contains this variable.
	 * @param name name of the variable
	 * @return true if contains, false otherwise.
	 */
	public boolean containsVariable(String name){
		return _localVariables.contains(name);
	}
	
	/**
	 * removes all the variables from the model.
	 */
	public void clearVariables(){
		_localVariables.clear();
	}
	
	

}
