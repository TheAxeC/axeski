package ide.frontend.classes.views.string;

import ide.backend.model.BlockModel;
import ide.backend.model.string.LengthModel;
import ide.frontend.classes.views.AbstractBlockController;
import ide.frontend.classes.views.BlockView;

import java.util.Observable;
/**
 * Controller for the LengthView to interact with a LengthModel
 * @author Axel
 *
 */
public class LengthController extends AbstractBlockController{

	/**
	 * Constructor
	 * @param model model to interact to
	 */
	public LengthController(Observable model) {
		super(model);
	}

	/**
	 * add content block to the model
	 * @param comp view of component being added.
	 */
	public void addContent(BlockView comp) {
		((LengthModel)getModel()).setContent((BlockModel)comp.getModel());
		
	}

	/**
	 * Remove content of the model
	 * @param comp view of component being removed.
	 */
	public void removeContent(BlockView comp){
		((LengthModel)getModel()).setContent(null);
	}
	
	/**
	 * Returns the error state of the model
	 * @return the error
	 */
	public boolean getError() {
		return ((LengthModel)getModel()).getError();
	}


}
