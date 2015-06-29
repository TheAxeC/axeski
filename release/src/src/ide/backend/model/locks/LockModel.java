package ide.backend.model.locks;

import ide.backend.exceptions.CompileException;
import ide.backend.model.BlockModel;
import ide.backend.model.variables.AbstractRefVariabelModel;
import ide.backend.runtime.Compiler;
import ide.backend.save.DataSaver;
import ide.backend.variables.Variable.VariableType;

/**
 * Represents a lock
 * @author axel
 *
 */
public class LockModel extends BlockModel {
	/**FIELDS**/
	private AbstractRefVariabelModel _name;

	/**
	 * Creates a new Lockmodel with no parent, nor a variable being locked.
	 */
	public LockModel() {
		super(null);
		_name = null;
	}
	
	/**
	 * Creates a new LockModel with given parent.
	 * No variable is given to be locked.
	 * @param prnt parent of the model.
	 */
	public LockModel(BlockModel prnt) {
		super(prnt);
		_name = null;
	}
	
	/**
	 * Get the variableName
	 * @return name of the variable being locked.
	 */
	public String getVariable() {
		return _name.getName();
	}
	
	/**
	 * Get the variable model
	 * @return Model of the variable being locked.
	 */
	public AbstractRefVariabelModel getVariableModel(){
		return _name;	
	}
	
	/**
	 * Set the variableModel [name]
	 * @param name, the variableModel
	 */
	public void setVariable(AbstractRefVariabelModel model) {
		this._name = model;
		if(_name != null){
			
			_name.changeParent(this);
		}
	}
	
	@Override
	public void changeParent(BlockModel p) {
		setParent(p);
		if(_name != null)
		_name.changeParent(this);
	}

	@Override
	public <T> T compile(Compiler<T> c) throws CompileException {
		return c.compileBlock(this);
	}
	
	@Override
	public <T> T save(DataSaver<T> s) {
		return s.saveBlock(this);
	}

	@Override
	public void tellParentChanged(BlockModel child,VariableType type) {
		//DO NOTHING
	}
}
