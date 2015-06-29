package ide.backend.model.variables;

import java.util.Observer;

import ide.backend.model.BlockModel;
import ide.backend.variables.Variable.VariableType;

/**
 * This class represents the common base for references to variables (RefMemberModel and RefVariableModel)
 * Common functions are defined in this class.
 * This class is abstract.
 * @author Matthijs Kaminski
 *
 */
public abstract class AbstractRefVariabelModel extends BlockModel implements Observer{

	/*FIELDS*/
	//name of the variable
	protected String _name;
	//type of the variable
	protected VariableType _type;
	//error in reference.
	protected boolean _error;
	
	/**
	 * New AbstractRefVariabelModel
	 * @param parent parent of this model.
	 */
	public AbstractRefVariabelModel(BlockModel parent) {
		super(parent);
		
	}
	
	/**
	 * Returns the name of the variable.
	 * @return the name
	 */
	public String getName() {
		return _name;
	}

	/**
	 * Returns the type of the variable.
	 * @return the type
	 */
	public VariableType getType() {
		return _type;
	}
	
	/**
	 * Returns if there's an error.
	 * @return if there is an error
	 */
	public boolean getError(){
		return _error;
	}
	
	@Override
	public void tellParentChanged(BlockModel child,VariableType type) {
		if(getParent() != null)
			getParent().tellParentChanged(this,getType());
		
	}

}
