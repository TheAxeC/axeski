package ide.backend.model.function;

import ide.backend.exceptions.CompileException;
import ide.backend.model.BlockModel;
import ide.backend.model.variables.AbstractRefVariabelModel;
import ide.backend.runtime.Compiler;
import ide.backend.save.DataSaver;
import ide.backend.variables.Variable.VariableType;

import java.util.ArrayList;




/**
 * This class represents a the model for a returnBlock in the ide.
 * Since a return statement can only exist out of an references it contains an array of references, which values are returned.
 * @author matthijs
 *
 */
public class ReturnModel extends BlockModel{
	/*FIELDS*/
	
	//array list of returns
	private ArrayList<AbstractRefVariabelModel> _returns;
	//function to which it refers
	private FunctionModel _func;
	//state of error.
	private boolean _error;
	
	/**
	 * Creates a new return model and search if its already in a function.
	 * @param parent parent of the model
	 */
	public ReturnModel(BlockModel parent) {
		super(parent);
		init();
	}
	

	/**
	 * Creates a new return model and search if its already in a function.
	 * No parent is set.
	 */
	public ReturnModel() {
		super(null);
		init();
		
	}

	/**
	 * init the model.
	 */
	private void init() {
		_returns = new  ArrayList<AbstractRefVariabelModel>();
		searchFunction();
	}
	
	private void searchFunction(){
		if(getParentModel() != null){
			BlockModel current = getParentModel();
			while(current != null && current.getClass() != FunctionModel.class  ){
				current = current.getParentModel();
			}
			_func = (FunctionModel) current;
		}
	}
	
	/**
	 * Returns all the names (Strings) of the variables being returned in the function.
	 * Note: Order is off importance.
	 * @return
	 */
	public ArrayList<AbstractRefVariabelModel> getReturnVars(){
		return _returns;
	}
	
	@Override
	public void changeParent(BlockModel p) {
		setParent(p);
		searchFunction();
		for (AbstractRefVariabelModel ret : _returns) {
			ret.changeParent(this);
		}
	}
	
	/**
	 * Adds a return value to the returns
	 * @param model return variable to be added.
	 */
	public void addReturnVar(AbstractRefVariabelModel model) {
		_returns.add(model);
		model.changeParent(this);
	}
	
	/**
	 * removes a given variable form the returns.
	 * @param model
	 */
	public void removeReturnVar(AbstractRefVariabelModel model) {
		_returns.remove(model);
	}
	
	/**
	 * Returns the state of error of this block 
	 * @return error state
	 */
	public boolean getError(){
		return _error;
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
		_error = false;
		//check whether the return type is equal to the type of the variable being returned.
		if(_func != null && _func.getAmountofReturns() > 0)
		for (AbstractRefVariabelModel ret : _returns) {
			if(_func.getReturnType(_returns.indexOf(ret)) != ret.getType()){
				_error = true;
			}
		}
		
		updateView();
	}

	

	
}
