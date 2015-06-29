package ide.frontend.classes.views.variables;

import ide.backend.model.variables.AbstractRefVariabelModel;
import ide.frontend.classes.views.AbstractBlockController;

import java.util.Observable;
/**
 * Controller for a variableRefView to interact with its model
 * @author matthijs
 *
 */
public class VariableRefViewController extends AbstractBlockController{

	/**
	 * Creates a new controller for a view to interact with its model
	 * @param model model to interact with.
	 */
	public VariableRefViewController(Observable model) {
		super(model);
	}
	
	/**
	 * returns the name of the variable to which the reference refers.
	 * @return name of the refernce
	 */
	public String getName(){
		return ((AbstractRefVariabelModel)getModel()).getName();
	}

	/**
	 * Returns the status of the error flag of the referenceVariable
	 * @return state of error
	 */
	public boolean getError(){
		return ((AbstractRefVariabelModel)getModel()).getError();
	}
}
