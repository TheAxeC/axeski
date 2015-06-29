package ide.frontend.classes.views.operations;

import ide.backend.model.BlockModel;
import ide.backend.model.operator.UnOperatorModel;
import ide.frontend.classes.views.AbstractBlockController;
import ide.frontend.classes.views.BlockView;

import java.util.ArrayList;
import java.util.Observable;

/**
 * Controller for the UnOperator view to interact with its model.
 * @author matthijs
 *
 */
public class UnOperatorController extends AbstractBlockController{

	/**
	 * Creates a new controller
	 * @param model model to interact with
	 */
	public UnOperatorController(Observable model) {
		super(model);
		
	}
	
	/**
	 * Returns all possible operators for this block.
	 * @return
	 */
	public String[] getPossibleOperators(){
		ArrayList<String> in =((UnOperatorModel)getModel()).getPossibleOperators();
		String[] out = new String[in.size()];
		return (String[])in.toArray(out);
	}

	/**
	 * Change the operator of the model to given operator.
	 * @param operator new operator
	 */
	public void setOperator(String operator) {
		((UnOperatorModel)getModel()).setOperator(operator);
	}

	/**
	 * add lefthand side of the operator.
	 * @param comp component being added on the right
	 */
	public void addLeft(BlockView comp) {
		((UnOperatorModel)getModel()).setLeft((BlockModel)comp.getModel());
		
	}


	/**
	 * remove lefthand side of the operator.
	 */
	public void removeLeft() {
		((UnOperatorModel)getModel()).setLeft(null);
		
	}


	/**
	 * Returns the current operator of the model.
	 * @return
	 */
	public Object getOperator() {
		return ((UnOperatorModel)getModel()).getOperator();
	}
	
	/**
	 * Returns the error state of the model
	 * @return
	 */
	public boolean getError() {
		return ((UnOperatorModel)getModel()).getError();
	}

}
