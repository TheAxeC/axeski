package ide.frontend.classes.views.physicViews;

import ide.backend.model.BlockModel;
import ide.backend.model.physicModels.ChangeAppearanceModel;
import ide.frontend.classes.views.AbstractBlockController;
import ide.frontend.classes.views.BlockView;

import java.util.Observable;


/**
 * Controller to let the changeAppearanceView interact with its model.
 * 
 */
public class ChangeAppearanceController extends AbstractBlockController{

	/**
	 * Constructor
	 * @param model model to interact to
	 */
	public ChangeAppearanceController(Observable model) {
		super(model);
	}

	/**
	 * add content block to the model
	 * @param comp view of component being added.
	 */
	public void addContent(BlockView comp) {
		((ChangeAppearanceModel)getModel()).setContent((BlockModel)comp.getModel());
		
	}
	
	/**
	 * Remove content of the model
	 * @param comp view of component being removed.
	 */
	public void removeContent(BlockView comp){
		((ChangeAppearanceModel)getModel()).setContent(null);

	}
}
