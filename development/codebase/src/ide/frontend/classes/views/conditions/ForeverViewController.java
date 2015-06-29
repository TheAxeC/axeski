package ide.frontend.classes.views.conditions;

import ide.backend.model.ConnectedBlocks;
import ide.backend.model.conditions.ForeverModel;

import ide.frontend.classes.views.AbstractBlockController;


import java.util.Observable;
/**
 * Controller for the ForeverView to its model.
 * @author matthijs
 *
 */
public class ForeverViewController extends AbstractBlockController{
	
	
	public ForeverViewController(Observable model) {
		super(model);
	}

	/**
	 * Makes a new Body in the model and returns the new model.
	 * @return the new body
	 */
	public ConnectedBlocks resetBody(){
		return ((ForeverModel)getModel()).resetBody();
	}

	/**
	 * Returns the model of the body.
	 * @return the model
	 */
	public Observable getBodymodel() {
		return ((ForeverModel)getModel()).getBody();
	}
	
}
