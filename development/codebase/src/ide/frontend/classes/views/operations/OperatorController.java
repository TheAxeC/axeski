package ide.frontend.classes.views.operations;

import java.util.ArrayList;
import java.util.Observable;

import ide.backend.model.BlockModel;
import ide.backend.model.operator.OperatorModel;
import ide.frontend.classes.views.AbstractBlockController;
import ide.frontend.classes.views.BlockView;

/**
 * Controller for the binaryoperator view to interact with its model.
 * @author matthijs
 *
 */
public class OperatorController extends AbstractBlockController{

	/**
	 * Creates a new controller
	 * @param model model to interact with
	 */
	public OperatorController(Observable model) {
		super(model);
		
	}
	
	/**
	 * Returns all possible operators for this block.
	 * @return
	 */
	public String[] getPossibleOperators(){
		ArrayList<String> in =((OperatorModel)getModel()).getPossibleOperators();
		in.remove("!");
		in.remove("sqrt");
		String[] out = new String[in.size()];
		return (String[])in.toArray(out);
	}

	/**
	 * Change the operator of the model to given operator.
	 * @param operator new operator.
	 */
	public void setOperator(String operator) {
		((OperatorModel)getModel()).setOperator(operator);
	}

	/**
	 * add lefthand side of the operator.
	 * @param comp component being added on the right
	 */
	public void addLeft(BlockView comp) {
		((OperatorModel)getModel()).setLeft((BlockModel)comp.getModel());
		
	}
	
	/**
	 * add righthand side of the operator.
	 * @param comp component being added on the right
	 */
	public void addRight(BlockView comp) {
		((OperatorModel)getModel()).setRight((BlockModel)comp.getModel());
		
	}

	/**
	 * remove lefthand side of the operator.
	 */
	public void removeLeft() {
		((OperatorModel)getModel()).setLeft(null);
		
	}
	/**
	 * remove righthand side of the operator.
	 */
	public void removeRight() {
		((OperatorModel)getModel()).setRight(null);
		
	}

	/**
	 * Returns the current operator of the model.
	 * @return
	 */
	public Object getOperator() {
		return ((OperatorModel)getModel()).getOperator();
	}
	
	/**
	 * Returns the error state of the model
	 * @return
	 */
	public boolean getError() {
		return ((OperatorModel)getModel()).getError();
	}

}
