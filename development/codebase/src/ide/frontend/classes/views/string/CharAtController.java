package ide.frontend.classes.views.string;

import ide.backend.model.BlockModel;
import ide.frontend.classes.views.AbstractBlockController;
import ide.frontend.classes.views.BlockView;
import ide.backend.model.string.CharAtModel;

import java.util.Observable;

/**
 * Controller for the charat view
 * @author axelfaes
 *
 */
public class CharAtController extends AbstractBlockController{

	/**
	 * Constructor
	 * @param model model to interact to
	 */
	public CharAtController(Observable model) {
		super(model);
	}

	/**
	 * add index block to the model
	 * @param comp view of component being added.
	 */
	public void addIndex(BlockView comp) {
		((CharAtModel)getModel()).setIndex((BlockModel)comp.getModel());
		
	}

	/**
	 * Remove index of the model
	 * @param comp view of component being removed.
	 */
	public void removeIndex(BlockView comp){
		((CharAtModel)getModel()).setIndex(null);
	}
	
	/**
	 * add content block to the model
	 * @param comp view of component being added.
	 */
	public void addContent(BlockView comp) {
		((CharAtModel)getModel()).setContent((BlockModel)comp.getModel());
		
	}

	/**
	 * Remove content of the model
	 * @param comp view of component being removed.
	 */
	public void removeContent(BlockView comp){
		((CharAtModel)getModel()).setContent(null);
	}
	
	/**
	 * Returns the error state of the model
	 * @return if an error occurred
	 */
	public boolean getError() {
		return ((CharAtModel)getModel()).getError();
	}

}