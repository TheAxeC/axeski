package ide.frontend.classes.views.other;

import ide.backend.model.BlockModel;
import ide.backend.model.other.SleepModel;
import ide.frontend.classes.views.AbstractBlockController;
import ide.frontend.classes.views.BlockView;

import java.util.Observable;

/**
 * Controller for the sleep block to interact with its model
 * @author axel
 *
 */
public class SleepController extends AbstractBlockController{

	/**
	 * Creates a new controller to interact with model
	 * @param model SleepModel to interact with.
	 */
	public SleepController(Observable model) {
		super(model);
		
	}
	
	/**
	 * Set the content of the block
	 * @param view view given the variable of which to model is set as content
	 */
	public void setContent(BlockView view){
		((SleepModel)getModel()).setContent((BlockModel) view.getModel());
	}
	
	/**
	 * Remove the content.
	 */
	public void removeContent(){
		((SleepModel)getModel()).setContent(null);
	}

}