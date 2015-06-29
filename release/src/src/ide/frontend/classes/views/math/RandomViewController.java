package ide.frontend.classes.views.math;

import java.util.Observable;

import ide.frontend.classes.views.AbstractBlockController;

/**
 * Controller for random view to interact with it's model.
 * @author matthijs Kaminski
 *
 */
public class RandomViewController extends AbstractBlockController{

	/**
	 * Creates a new controller.
	 * @param model model to interact with.
	 */
	public RandomViewController(Observable model) {
		super(model);
	}

}
