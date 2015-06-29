package ide.frontend.classes.views.operations;

import ide.backend.model.BlockModel;
import ide.backend.model.variables.AbstractRefVariabelModel;
import ide.backend.model.variables.SetModel;
import ide.frontend.classes.views.AbstractBlockController;
import ide.frontend.classes.views.BlockView;

import java.util.Observable;

/**
 * Controller for the setBlockView to interact with its model
 * @author matthijs
 *
 */
public class SetBlockViewController extends AbstractBlockController{

	/**
	 * Creates a new SetBlockViewController controller
	 * @param model the Setmodel to interact with
	 */
	public SetBlockViewController(Observable model) {
		super(model);
		
	}
	
	/**
	 * Sets the left hand side of the set block
	 * @param view view of the reference being set on the left hand side.
	 */
	public void setReference(BlockView view){
		((SetModel)getModel()).setVariable((AbstractRefVariabelModel) view.getModel());
	}
	
	/**
	 * Removes the left hand side in the model
	 */
	public void removeReference(){
		((SetModel)getModel()).setVariable(null);
	}
	
	/**
	 * Sets the right hand side of the set model to the model of the given blockview
	 * @param view the given blockview
	 */
	public void setContent(BlockView view){
		((SetModel)getModel()).setContent((BlockModel)view.getModel());
	}
	
	/**
	 * Removes the right hand side from the model
	 */
	public void removeContent(){
		((SetModel)getModel()).setContent(null);
	}

	/**
	 * Returns the error state of the model
	 * @return
	 */
	public boolean getError() {
		return ((SetModel)getModel()).getError();
	}

}
