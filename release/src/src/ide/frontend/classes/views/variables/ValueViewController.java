package ide.frontend.classes.views.variables;

import ide.backend.model.variables.ValueModel;
import ide.frontend.classes.views.AbstractBlockController;

import java.util.Observable;

/**
 * Controller for the valueView to its Model
 * @author matthijs
 *
 */
public class ValueViewController extends AbstractBlockController{

	/**
	 * Create a new Controller to the given model.
	 * @param model mofel being interacted with.
	 */
	public ValueViewController(Observable model) {
		super(model);
	}

	/**
	 * Changes the content of the valueblcok to given value
	 * @param text text to change the content to.
	 */
	public void changeContent(String text) {
		((ValueModel)getModel()).setContent(text);
	}

	/**
	 * Returns the current content of the model
	 * @return current content of the model
	 */
	public String getContent() {
		return ((ValueModel)getModel()).getContent();
	}

}
