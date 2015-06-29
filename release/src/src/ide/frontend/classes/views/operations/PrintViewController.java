package ide.frontend.classes.views.operations;

import ide.backend.model.BlockModel;
import ide.backend.model.variables.PrintModel;
import ide.frontend.classes.views.AbstractBlockController;
import ide.frontend.classes.views.BlockView;

import java.util.Observable;

/**
 *  Controller to let the print Controller to let the changeAppearanceView interact with its model.View interact with its model.
 * @author matthijs
 *
 */
public class PrintViewController extends AbstractBlockController{

	/**
	 * Constructor
	 * @param model model to interact to
	 */
	public PrintViewController(Observable model) {
		super(model);
	}

	/**
	 * add content block to the model
	 * @param comp view of component being added.
	 */
	public void addContent(BlockView comp) {
		((PrintModel)getModel()).setContent((BlockModel)comp.getModel());
		
	}

	/**
	 * Remove content of the model
	 * @param comp view of component being removed.
	 */
	public void removeContent(BlockView comp){
		((PrintModel)getModel()).setContent(null);

	}

}
