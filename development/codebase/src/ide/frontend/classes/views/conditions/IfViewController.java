package ide.frontend.classes.views.conditions;

import ide.backend.model.BlockModel;
import ide.backend.model.ConnectedBlocks;
import ide.backend.model.conditions.IfBlockModel;
import ide.frontend.classes.views.AbstractBlockController;
import ide.frontend.classes.views.BlockView;

import java.util.Observable;

/**
 * Controller for the ifView to its model.
 * @author matthijs
 *
 */
public class IfViewController extends AbstractBlockController{

	/**
	 * Creates a new controller to an IfModel.
	 * @param model IfModel to interact with.
	 */
	public IfViewController(Observable model) {
		super(model);
	}

	/**
	 * Makes a new Body in the model and returns the new model.
	 * @return the new body of the ifblock
	 */
	public ConnectedBlocks resetBody(){
		return ((IfBlockModel)getModel()).resetBody();
	}

	/**
	 * Returns the model of the body.
	 * @return model of the body.
	 */
	public Observable getBodymodel() {
		return ((IfBlockModel)getModel()).getBody();
	}
	
	/**
	 * Sets the condition to the given block.
	 * @param BlockView whose model is set to be the condition.
	 */
	public void setCondition(BlockView v){
		((IfBlockModel)getModel()).setCondition((BlockModel)v.getModel());
	}
	
	/**
	 * Removes the condition from the ifblock.
	 */
	public void removeCondition(){
		((IfBlockModel)getModel()).setCondition(null);
	}
	
	/**
	 * Returns the error state of the condition. (typechecking)
	 * @return the error
	 */
	public boolean getError(){
		return ((IfBlockModel)getModel()).getError();
	}
}
