package ide.frontend.classes.views.functions;

import java.util.Observable;

import ide.backend.model.function.FunctionCallModel;
import ide.backend.model.variables.AbstractRefVariabelModel;
import ide.frontend.classes.views.AbstractBlockController;
import ide.frontend.classes.views.BlockView;

/**
 * Controller for functionCallview to interact with its model
 * @author matthijs kaminski
 *
 */
public class FunctionCallController extends AbstractBlockController{

	public FunctionCallController(Observable model) {
		super(model);
		
	}
	
	/**
	 * Amount of parameters of the function being called.
	 * @return amount
	 */
	public int getAmountOfParameters(){
		return ((FunctionCallModel)getModel()).amountOfParameters();
		
	}

	/**
	 * returns the amount of returns
	 * @return amount 
	 */
	public int getAmountOfReturns(){
		return ((FunctionCallModel)getModel()).getAmountOfReturns();
	}
	
	

	/**
	 * add a parameter to the call. at given position
	 * @param insert position of the parameter in the call.
	 * @param comp parameterview
	 */
	public void addParam(int _insert, BlockView comp) {
		((FunctionCallModel)getModel()).addParam((AbstractRefVariabelModel) comp.getModel(), _insert);
		
	}

	/**
	 * remove parameter at given position
	 * @param index index of parameter to remove.
	 */
	public void removeParam(int index) {
		((FunctionCallModel)getModel()).removeParam(index);
		
	}
	
	/**
	 * Returns the name of the function.
	 * @return name of the function.
	 */
	public String getFuncName(){
		return ((FunctionCallModel)getModel()).getFuncName();
	}

	/**
	 * returns the state of the error of the function call.
	 * @return the error
	 */
	public boolean getError() {
		return ((FunctionCallModel)getModel()).getError();
	}

	/**
	 * removes the return variable of the functioncall
	 * @param out
	 */
	public void removeReturn(BlockView out) {
		((FunctionCallModel)getModel()).removeReturn((AbstractRefVariabelModel) out.getModel());
		
	}

	/**
	 * adds the return variable of the functioncall
	 * @param comp view of the variable
	 */
	public void addReturn(BlockView comp) {
		((FunctionCallModel)getModel()).addreturn((AbstractRefVariabelModel) comp.getModel(), 0);
		
	}
	
}
