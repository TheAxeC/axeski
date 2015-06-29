package ide.backend.model.function;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import ide.backend.exceptions.CompileException;
import ide.backend.exceptions.TypeException;
import ide.backend.model.BlockModel;
import ide.backend.model.variables.AbstractRefVariabelModel;
import ide.backend.runtime.Compiler;
import ide.backend.save.DataSaver;
import ide.backend.variables.Variable.VariableType;

/**
 * This model represents a functionCall Block in the IDE.
 * It observers the Function which it calls.
 * @author matthijs
 *
 */
public class FunctionCallModel extends BlockModel implements Observer  {

	/*FIELDS*/
	private FunctionModel _func;
	private ArrayList<AbstractRefVariabelModel> _params;
	private ArrayList<AbstractRefVariabelModel> _returns;
	private boolean _error;
	
	/**
	 * Creates a new functionCall for the given function [func] and sets the parent of the model to the given parent [parent]
	 * The created functionCall model observes the function for changes.
	 * @param parent
	 * @param func
	 */
	public FunctionCallModel(BlockModel parent, FunctionModel func) {
		super(parent);
		_func = func;
		if(func != null)
			func.addObserver(this);
		_params = new ArrayList<AbstractRefVariabelModel>();
		_returns = new ArrayList<AbstractRefVariabelModel>();
		if(_func != null){
			for(int i= 0; i < _func.getAmountofParams(); i++){
				_params.add(null);
			}
			for(int i= 0; i < _func.getAmountofReturns(); i++){
				_returns.add(null);
			}
		}
		
	}
	
	/**
	 * Sets the functionCall to call a different Function.
	 * @param m the new function model
	 */
	public void changeFunction(FunctionModel m){
		if(_func != null)
			_func.deleteObserver(this);
		_func = m;
		if(_func != null){
			for(int i= _params.size(); i < _func.getAmountofParams(); i++){
				_params.add(null);
			}
			for(int i= _returns.size(); i < _func.getAmountofReturns(); i++){
				_returns.add(null);
			}
		}
		m.addObserver(this);
		updateView();
	}
	
	/**
	 * Returns the VariableType of the parameter at the given index in the parameter list of in the function definition.
	 * @param index the index to access
	 * @return the type
	 */
	public VariableType getParamType(int index){
		return _func.getParamType(index);
	}
	
	/**
	 * Returns the amount of parameters of the function being called.
	 * @return the amount of parameters
	 */
	public int amountOfParameters(){
		return _func.getAmountofParams();
	}
	
	/**
	 * Adds a new value parameter [var] to the functioncall at a given index [index].
	 * If the type does not match the type of the parameter in the definition an TypeException is thrown.
	 * @param model the model to add
	 * @param index the index where to add the parameter
	 * @throws TypeException if the type is incorrect
	 */
	public void addParam(AbstractRefVariabelModel model, int index) {
		if (index >= _params.size())
			_params.add(model);
		else
			_params.set(index, model);
		model.changeParent(this);
	}
	
	/**
	 * removes parameter [var] to the functioncall at a given index [index].
	 * If the type does not match the type of the parameter in the definition an TypeException is thrown.
	 * @param index the index to remove
	 */
	public void removeParam(int index) {
		_params.set(index, null);
		tellParentChanged(null, null);
	}
	
	/**
	 * Returns an arrayList Containing the names of the variables being used as value parameters.
	 * @return list of parameter names
	 */
	public ArrayList<String> getParamNames(){
		ArrayList<String> out = new ArrayList<String>();
		for(AbstractRefVariabelModel var : _params) {
			if (var != null)
				out.add(var.getName());
		}
		return out;
	}
	
	/**
	 * Returns the value parameters of the functionCall. 
	 * @return list of parameter models
	 */
	public ArrayList<AbstractRefVariabelModel> getParams(){
		return _params;
	}
	/**
	 * Returns the VariableType of the return at the given index in the return list of in the function definition.
	 * @param index the index to access
	 * @return the type
	 */
	public VariableType getReturnType(int index){
		return _func.getReturnType(index);
	}
	
	/**
	 * Adds a new value parameter [var] to the functioncall at a given index [index].
	 * If the type does not match the type of the parameter in the definition an TypeException is thrown.
	 * @param abstractRefVariabelModel the model of the return
	 * @param index the index to add the returb
	 */
	public void addreturn(AbstractRefVariabelModel abstractRefVariabelModel, int index) {
		if (index >= _returns.size())
			_returns.add(abstractRefVariabelModel);
		else
			_returns.set(index, abstractRefVariabelModel);
		abstractRefVariabelModel.changeParent(this);
		updateView();
	}
	
	/**
	 * Returns an arrayList Containing the names of the variables being used as returns.
	 * @return list of return names
	 */
	public ArrayList<String> getReturnNames(){
		ArrayList<String> out = new ArrayList<String>();
		for(AbstractRefVariabelModel var : _returns) {
			if(var != null)
				out.add(var.getName());
		}
		return out;
	}
	
	/**
	 * Returns the returns of the functionCall. 
	 * @return list of return models
	 */
	public ArrayList<AbstractRefVariabelModel> getReturns(){
		return _returns;
	}
	
	/**
	 * Returns the name of the function being called.
	 * @return the function name
	 */
	public String getFuncName(){
		return _func.getName();
	}
	
	/**
	 * Returns the error state of the functioncall (typechecking).
	 * @return error
	 */
	public boolean getError(){
		return _error;
	}
	
	/**
	 * returns the amount of returns
	 * @return amount 
	 */
	public int getAmountOfReturns() {
		return _func.getAmountofReturns();
	}

	/**
	 * removes given variable from the returns.
	 * @param ret variable being removed
	 */
	public void removeReturn(AbstractRefVariabelModel ret) {
		_returns.set(_returns.indexOf(ret), null);
		tellParentChanged(null, null);
	}
	
	@Override
	public void changeParent(BlockModel p) {
		setParent(p);
		for (AbstractRefVariabelModel para : _params) {
			if (para != null)
				para.changeParent(this);
		}
		
		for (AbstractRefVariabelModel ret : _returns) {
			if (ret != null)
				ret.changeParent(this);
		}
		
		
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
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void tellParentChanged(BlockModel child,VariableType type) {
		_error = false;
		if (_func != null) {
			for (AbstractRefVariabelModel para : _params) {
				if(para != null && para.getType() != getParamType(_params.indexOf(para)))
					_error = true;
			}
			
			for (AbstractRefVariabelModel ret : _returns) {
				if(ret != null && ret.getType() != getReturnType(_returns.indexOf(ret)))
					_error = true;
			}
		}
		updateView();
		
	}


	

}
