package ide.frontend.models;

import java.util.HashMap;
import java.util.Observable;

import ide.backend.model.variables.AbstractRefVariabelModel;
import ide.backend.model.variables.AbstractVariableModel;
import ide.backend.model.variables.VariableModel;
import ide.backend.variables.Variable.VariableType;
import ide.frontend.classes.views.AbstractBlockController;

/**
 * Controller for a variableView to interact with its model
 * @author matthijs kaminski
 *
 */
public class VariableController extends AbstractBlockController {

	/**FIELDS**/
	//hashmap from strings to types
	private static HashMap<String, VariableType> _toTypes;
	//hashmap from types to strings.
	private static HashMap< VariableType, String> _fromTypes;
	
	/**
	 * Creates a new VariableController to interact with given model.
	 * @param model model to interact with.
	 */
	public VariableController(Observable model) {
		super(model);
		//init hashmaps if not yet created
		if(_toTypes == null){
			_toTypes = new HashMap<String, VariableType>();
			_toTypes.put("TypeNumber", VariableType.NUMBER);
			_toTypes.put("TypeBoolean", VariableType.BOOLEAN);
			_toTypes.put("TypeString", VariableType.STRING);
		}
		if(_fromTypes == null){
			_fromTypes = new HashMap<VariableType, String>();
			_fromTypes.put(VariableType.NUMBER, "TypeNumber");
			_fromTypes.put(VariableType.BOOLEAN, "TypeBoolean");
			_fromTypes.put(VariableType.STRING, "TypeString");
		}
	}

	/**
	 * return the name of the variable
	 * @return name
	 */
	public String getName() {
		return ((AbstractVariableModel)getModel()).getName();
	}

	/**
	 * returns the type of the variable as a String.
	 * @return type of the variable
	 */
	public String getType() {
		return _fromTypes.get(((AbstractVariableModel)getModel()).getType());
	}

	/**
	 * Sets the type of the variable to type defined by the given string
	 * @param string string defining type.
	 */
	public void setType(String string) {
		((AbstractVariableModel)getModel()).setType(_toTypes.get(string));
	}
	
	/**
	 * Changes the name of the variable to given name
	 * @param name new name of the variable
	 * @return if setting the name resulted in an error.
	 */
	public boolean changeContent(String name) {
		((AbstractVariableModel)getModel()).setName(name);
		return ((VariableModel)getModel()).getError();
	}

	/**
	 * Creates a reference to the model where this controller interacts with
	 * @return AbstractRefVariabelModel created reference
	 */
	public AbstractRefVariabelModel makeRef() {
		return ((AbstractVariableModel)getModel()).makeReference(null);
	}
	
	/**
	 * Returns the state of error of the model.
	 * @return state of error.
	 */
	public boolean getError(){
		return ((AbstractVariableModel)getModel()).getError();
		
	}


}
