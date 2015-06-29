package ide.frontend.classes.views.locks;

import ide.backend.model.locks.UnLockModel;
import ide.backend.model.variables.AbstractRefVariabelModel;
import ide.frontend.classes.views.AbstractBlockController;
import ide.frontend.classes.views.BlockView;

import java.util.Observable;

/**
 * Controller for UnLockView to interact with its model.
 * @author axel.
 *
 */
public class UnLockController extends AbstractBlockController{

	/**
	 * Creates a new controller to interact with given model.
	 * @param model model to interact with.
	 */
	public UnLockController(Observable model) {
		super(model);	
	}
	/**
	 * Sets the variable to be Unlocked to the model of the given view.
	 * @param view view of variable being unlocked.
	 */
	public void setReference(BlockView view){
		((UnLockModel)getModel()).setVariable((AbstractRefVariabelModel) view.getModel());
	}
	
	/**
	 * Removes the variabele that is being UnLocked.
	 */
	public void removeReference(){
		((UnLockModel)getModel()).setVariable(null);
	}

}