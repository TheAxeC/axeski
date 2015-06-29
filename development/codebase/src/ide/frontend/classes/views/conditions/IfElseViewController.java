package ide.frontend.classes.views.conditions;

import ide.backend.model.BlockModel;
import ide.backend.model.ConnectedBlocks;
import ide.backend.model.conditions.IfElseModel;
import ide.frontend.classes.views.AbstractBlockController;
import ide.frontend.classes.views.BlockView;

import java.util.Observable;

/**
 * Controller for IfElse view to the IfElseModel
 * @author axel
 *
 */
public class IfElseViewController extends AbstractBlockController{

	/**
	 * Creates a new controller to a IfElseModel
	 * @param model IfElseModel to interact with.
	 */
	public IfElseViewController(Observable model) {
		super(model);
	}

	/**
	 * Makes a new Body in the model for the if and returns the new model.
	 * @return the new model of the body
	 */
	public ConnectedBlocks resetBodyIf(){
		return ((IfElseModel)getModel()).resetBodyIf();
	}

	/**
	 * Returns the model of the body for the if.
	 * @return model of the body of the if.
	 */
	public Observable getBodymodelIf() {
		return ((IfElseModel)getModel()).getBodyIf();
	}
	
	/**
	 * Makes a new Body in the model for the else and returns the new model.
	 * @return new body of else block.
	 */
	public ConnectedBlocks resetBodyElse(){
		return ((IfElseModel)getModel()).resetBodyElse();
	}

	/**
	 * Returns the model of the body for the else .
	 * @return Model of the body of the else block
	 */
	public Observable getBodymodelElse() {
		return ((IfElseModel)getModel()).getBodyElse();
	}
	
	/**
	 * Sets the condition to the given block.
	 * @param Blockview whose model is set as condition.
	 */
	public void setCondition(BlockView v){
		((IfElseModel)getModel()).setCondition((BlockModel)v.getModel());
	}
	
	/**
	 * Removes the condition from the ifblock.
	 */
	public void removeCondition(){
		((IfElseModel)getModel()).setCondition(null);
	}
	
	/**
	 * Returns the error state of the condition. (typechecking)
	 * @return the error
	 */
	public boolean getError(){
		return ((IfElseModel)getModel()).getError();
	}
}

