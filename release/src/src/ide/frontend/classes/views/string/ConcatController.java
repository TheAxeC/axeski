package ide.frontend.classes.views.string;

import ide.backend.model.BlockModel;
import ide.backend.model.string.ConcatModel;

import ide.frontend.classes.views.AbstractBlockController;
import ide.frontend.classes.views.BlockView;

import java.util.Observable;

/**
 * Controller for the concat view
 * @author axel
 *
 */
public class ConcatController extends AbstractBlockController{

	/**
	 * Constructor
	 * @param model model to interact to
	 */
	public ConcatController(Observable model) {
		super(model);
	}

	/**
	 * add left content block to the model
	 * @param comp view of component being added.
	 */
	public void addLeft(BlockView comp) {
		((ConcatModel)getModel()).setLeft((BlockModel)comp.getModel());
		
	}

	/**
	 * Remove left content of the model
	 * @param comp view of component being removed.
	 */
	public void removeLeft(BlockView comp){
		((ConcatModel)getModel()).setLeft(null);
	}
	
	/**
	 * add right content block to the model
	 * @param comp view of component being added.
	 */
	public void addRight(BlockView comp) {
		((ConcatModel)getModel()).setRight((BlockModel)comp.getModel());
		
	}

	/**
	 * Remove right content of the model
	 * @param comp view of component being removed.
	 */
	public void removeRight(BlockView comp){
		((ConcatModel)getModel()).setRight(null);
	}
	
	/**
	 * Returns the error state of the model
	 * @return the error
	 */
	public boolean getError() {
		return ((ConcatModel)getModel()).getError();
	}

}