package ide.frontend.classes.views.locks;

import ide.backend.model.locks.LockModel;
import ide.backend.model.variables.AbstractRefVariabelModel;
import ide.frontend.classes.views.AbstractBlockController;
import ide.frontend.classes.views.BlockView;

import java.util.Observable;

/**
 * Controller forLockView to interact with its model.
 * @author axel.
 *
 */
public class LockController extends AbstractBlockController{

	/**
	 * Creates a new controller to interact with given model.
	 * @param model model to interact with.
	 */
	public LockController(Observable model) {
		super(model);
		
	}
	/**
	 * Sets the variable to be locked to the model of the given view.
	 * @param view view of variable being locked.
	 */
	public void setReference(BlockView view){
		((LockModel)getModel()).setVariable((AbstractRefVariabelModel) view.getModel());
	}
	/**
	 * Removes the variabele that is being Locked.
	 */
	public void removeReference(){
		((LockModel)getModel()).setVariable(null);
	}

}