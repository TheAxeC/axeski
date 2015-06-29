package ide.backend.model.variables;

import ide.backend.exceptions.CompileException;
import ide.backend.model.BlockModel;
import ide.backend.runtime.Compiler;
import ide.backend.save.DataSaver;
import ide.backend.variables.Variable.VariableType;

/**
 * Set model
 * This model will set the variable [name] to the result of block [content]
 * @author axel
 */
public class SetModel extends BlockModel {
	
	/**FIELDS**/
	//right side of the set block
	private BlockModel _content;
	//left side of the set block
	private AbstractRefVariabelModel _name;
	//type of the right side
	private VariableType _contentType;
	//state of error of the model
	private boolean _error;
	
	/**
	 * Creates a new set model with parent set to null.
	 */
	public SetModel() {
		super(null);
		initFields();
	}
	
	/**
	 * Creates a new set model parent set to given block prnt.
	 * @param prnt parent of the model to be set.
	 */
	public SetModel(BlockModel prnt) {
		super(prnt);
		initFields();	
	}
	
	private void initFields(){
		_content = null;
		_name = null;
		_error = false;
	}

	/**
	 * Getter for the content
	 * @return
	 */
	public BlockModel getContent() {
		return _content;
	}

	/**
	 * Setter for the content
	 * @param content Block being set as righthand.
	 */
	public void setContent(BlockModel content) {
		this._content = content;
		if(_content != null)
			content.changeParent(this);
		else{
			_contentType = null;
			tellParentChanged(null, null);
		}
			
		
	}
	
	/**
	 * Get the variableName
	 * @return name of the lefthand variable.
	 */
	public String getVariable() {
		return _name.getName();
	}
	
	/**
	 * Get the variable model
	 * @return the lefthand variable.
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
		}else{
			tellParentChanged(null, null);
		}
	}
	
	/**
	 * Returns the state of error of the model (type checking)
	 * @return state of error
	 */
	public boolean getError(){
		return _error;
	}
	
	@Override
	public void changeParent(BlockModel p) {
		setParent(p);
		if(_content != null)
		_content.changeParent(this);
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
		if(child == _content){
			_contentType = type;
		}
		if(_name != null && _contentType != null){
			if(_contentType == _name.getType() || _contentType == VariableType.VALUE ){
				_error = false;
			}else{
				_error = true;
			}
		}else{
			_error = false;
		}
		updateView();
	}
}

