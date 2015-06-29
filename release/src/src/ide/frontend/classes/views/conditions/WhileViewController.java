package ide.frontend.classes.views.conditions;

import ide.backend.model.BlockModel;
import ide.backend.model.ConnectedBlocks;
import ide.backend.model.conditions.WhileModel;
import ide.frontend.classes.views.AbstractBlockController;
import ide.frontend.classes.views.BlockView;

import java.util.Observable;

public class WhileViewController extends AbstractBlockController{
	
	/**
	 * Controller for the WhileView to its model.
	 * @author matthijs
	 *
	 */
	public WhileViewController(Observable model) {
		super(model);
	}

	/**
	 * Makes a new Body in the model and returns the new model.
	 * @return new body of the model
	 */
	public ConnectedBlocks resetBody(){
		return ((WhileModel)getModel()).resetBody();
	}

	/**
	 * Returns the model of the body.
	 * @return body of the model
	 */
	public Observable getBodymodel() {
		return ((WhileModel)getModel()).getBody();
	}
	
	/**
	 * Sets the condition to the given block.
	 * @param BlockView whose model is set as condition.
	 */
	public void setCondition(BlockView v){
		((WhileModel)getModel()).setCondition((BlockModel)v.getModel());
	}
	
	/**
	 * Removes the condition from the whileblock.
	 */
	public void removeCondition(){
		((WhileModel)getModel()).setCondition(null);
	}
	
	/**
	 * Returns the error state of the condition. (typechecking)
	 * @return the error
	 */
	public boolean getError(){
		return ((WhileModel)getModel()).getError();
	}
}
