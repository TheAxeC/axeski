package ide.frontend.classes.views.functions;

import java.util.Observable;

import ide.backend.model.function.ReturnModel;
import ide.backend.model.variables.AbstractRefVariabelModel;
import ide.frontend.classes.views.AbstractBlockController;
import ide.frontend.classes.views.BlockView;

/**
 * Controller for the return view to interact with its model
 * @author matthijs Kaminski
 *
 */
public class ReturnViewController extends AbstractBlockController{

	public ReturnViewController(Observable model) {
		super(model);
	}

	/**
	 * adds a returns value to the model
	 * @param comp the view of the return variable
	 */
	public void addVar(BlockView comp) {
		((ReturnModel)getModel()).addReturnVar((AbstractRefVariabelModel) comp.getModel());
	}
	
	/**
	 * Removes a return variable from the return block.
	 * @param comp view whose model is being removed
	 */
	public void removeVar(BlockView comp){
		((ReturnModel)getModel()).removeReturnVar((AbstractRefVariabelModel) comp.getModel());
	}

	/**
	 * Returns the state of error of the model
	 * @return error state
	 */
	public boolean getError() {
		return ((ReturnModel)getModel()).getError();
	}
	
}
