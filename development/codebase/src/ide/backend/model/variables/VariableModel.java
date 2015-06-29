package ide.backend.model.variables;

import ide.backend.exceptions.CompileException;
import ide.backend.model.BlockModel;
import ide.backend.model.function.TopLevelModel;
import ide.backend.runtime.Compiler;
import ide.backend.save.DataSaver;
import ide.backend.variables.Variable.VariableType;

/**
 * This class represents the model for a block creating a new variable.
 * @author matthijs
 *
 */
public class VariableModel extends AbstractVariableModel{

	/*FIELDS*/
	private boolean _error;
	
	public VariableModel(){
		super(null);
		_name = new String("");
		_type = new TypeModel(this, VariableType.NUMBER);
		initError(); 
	}
	
	/**
	 * Creates a new variableModel.
	 * By Default: Variable name is empty and the type is NUMBER.
	 * @param parent parent of the model
	 */
	public VariableModel(BlockModel parent) {
		super(parent);
		_name = new String("");
		_type = new TypeModel(this, VariableType.NUMBER);
		initError(); 
	}
	
	/**
	 * Creates a new variableModel.
	 * @param parent parent of the model
	 * @param name name of the variable
	 * @param type type of the variable
	 */
	public VariableModel(BlockModel parent,String name, VariableType type){
		super(parent);
		_name = name;
		_type = new TypeModel(this, type);
		initError(); 
		
	}
	/**
	 * Init the error variable
	 */
	public void initError(){
		_error = false;
	}
	

	/**
	 * The name of the variable is changed and its views and references are notified.
	 * @param name new name of the variable
	 */
	public void setName(String name) {
		TopLevelModel top;
		if (name.trim().equals("")){
			if ((top = searchTopLevel()) != null)
				top.removeVariable(_name);
			_name = "";
			_error =false;
			_name = "";
			return;
		}
		
		//if block is in an toplevelBlock and toplevel already contains the new name
		if(((top = searchTopLevel()) != null) && top.containsVariable(name)){
			
			
			if(!_error)
				top.removeVariable(_name);
			_error = true;
			
		}
		else if ((top = searchTopLevel()) != null && !top.containsVariable(name)){
			if(!_error)
				top.removeVariable(_name);
			top.addVariable(name);
			
			_error = false;
		}else{
			_error = false;
		}
			
		this._name = name;
		updateView();
	}


	
	/**
	 * Creates a new reference to this EventModel.
	 * It's notified if changes are made to this model.
	 * @return
	 */
	public AbstractRefVariabelModel makeReference(BlockModel prnt){
		RefVariabelModel out = new RefVariabelModel(prnt, this);
		this.addObserver(out);
		return out;
	}
	
	/**
	 * Searches the toplevelBlock (handler or function) which may contain this block.
	 * @return
	 */
	public TopLevelModel searchTopLevel(){
		TopLevelModel out = null;
		if(getParentModel() != null){
			
			BlockModel current = getParentModel();
			while(current != null && !(current instanceof TopLevelModel)){
				current = current.getParentModel();
				
			}
			out = (TopLevelModel) current;
			return out;
		}
		return out;
		
	}
	
	/**
	 * returns if the block has an error.
	 * @return
	 */
	public boolean getError(){
		return _error;
	}
	
	@Override
	public void changeParent(BlockModel p) {
		TopLevelModel top;
		//check if the toplevel if found if so remove this variable form
		// its variable if there was no error.
		if((top = searchTopLevel()) != null && !_error) {
			top.removeVariable(_name);
			setParent(p);
			return;
		}
		setParent(p);
		_error = true;
		setName(_name);
		//updateView();
		
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
		// Do nothing
		
	}

}
