package ide.frontend.classes.views.physicViews;

import java.util.Observable;

import ide.backend.model.BlockModel;
import ide.backend.model.physicModels.MoveModel;
import ide.frontend.classes.views.AbstractBlockController;
import ide.frontend.classes.views.BlockView;

/**
 * Controller for moveView to interact with its model.
 * @author Matthijs Kaminski
 *
 */
public class MoveController extends AbstractBlockController{

	/**
	 * Constructor
	 * @param model movemodel to interact with
	 */
	public MoveController(Observable model) {
		super(model);
	}
	
	/**
	 * Sets the x block to the model of the given view.
	 * @param x the given model whose model is set as X
	 */
	public void addBlockX(BlockView x){
		((MoveModel) getModel()).setX((BlockModel) x.getModel());
	}
	
	/**
	 * Removes the x block from the model.
	 */
	public void removeBlockX(){
		((MoveModel) getModel()).setX(null);
	}
	
	/**
	 * Sets the y block to the model of the given view.
	 * @param y the given model whose model is set as X
	 */
	public void addBlockY(BlockView y){
		((MoveModel) getModel()).setY((BlockModel) y.getModel());
	}
	
	/**
	 * Removes the y block from the model.
	 */
	public void removeBlockY(){
		((MoveModel) getModel()).setY(null);
	}
	
	/**
	 * Returns the error state of the model
	 * @return
	 */
	public boolean getError() {
		return ((MoveModel)getModel()).getError();
	}
	
	

}
