package ide.backend.model.variables;


import ide.backend.model.BlockModel;
import ide.backend.variables.Variable.VariableType;

/**
 * This class represents the common base for  variables (MemberModel and VariableModel)
 * Common functions are defined in this class.
 * This class is abstract.
 * @author Matthijs Kaminski
 *
 */
public abstract class AbstractVariableModel extends BlockModel {

	/**FIELDS*/
	//name of the variable
	protected String _name;
	//type of the variable
	protected TypeModel _type;
	
	/**
	 * New AbstractVariableModel
	 * @param parent parent of this model.
	 */
	public AbstractVariableModel(BlockModel parent) {
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
	 * The name of the variable is changed and its views and references are notified.
	 * @param name the name to set
	 */
	public void setName(String name) {
		this._name = name;
		updateView();
	}

	/**
	 * Returns the type of the variable.
	 * @return the type
	 */
	public VariableType getType() {
		return _type.getType();
	}

	/**
	 * sets the type of the variable.
	 * @param type
	 */
	public void setType(VariableType type) {
		_type.setType(type);
		tellParentChanged(null, null);
		setChanged();
		notifyObservers("typechanged");
		
	}
	
	/**
	 * Creates a new reference to this EventModel.
	 * It's notified if changes are made to this model.
	 * @return the created reference
	 */
	public AbstractRefVariabelModel makeReference(BlockModel prnt){
		return null;
	}

	/**
	 * returns the state of the error
	 * @return if there is an error
	 */
	public boolean getError() {
		return false;
	}
	
	@Override
	public void tellParentChanged(BlockModel child,VariableType type) {
		if(getParent() != null)
			getParent().tellParentChanged(this,getType());
	}
		

}
