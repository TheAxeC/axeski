package ide.frontend.classes.views.functions;

import java.util.Observable;

import ide.backend.model.ConnectedBlocks;
import ide.backend.model.function.HandlerModel;
import ide.frontend.classes.views.AbstractBlockController;
import ide.frontend.classes.views.BlockView;

/**
 * Controller for the handlerView to interact with its model.
 * @author matthijs
 *
 */
public class HandlerController extends AbstractBlockController {

	/**
	 * Creates a new controller to interact with a given model
	 * @param model model to interact with
	 */
	public HandlerController(Observable model) {
		super(model);
	}
	
	/**
	 * Changes the name of the handler to the given name.
	 * @param name name of the handler
	 */
	public void changeName(String name){
		((HandlerModel)getModel()).setName(name);
	}
	
	/**
	 * Get the name of the handler
	 * @return the name of the handler
	 */
	public String getName() {
		return ((HandlerModel)getModel()).getName();
	}
	
	/**
	 * Returns whether the handler handles an event.
	 * @return true if it has a handler, else false.
	 */
	public boolean hasEvent(){
		return ((HandlerModel)getModel()).hasEventRef();
	}
	
	/**
	 * adds the model of a given blockview to the body of the handler
	 * @param m blockview of the block being added.
	 */
	public void addBlock(BlockView m){
		((HandlerModel)getModel()).addBlock((ConnectedBlocks)m.getModel());
	}

	/**
	 * Resets the total body of the handler 
	 * @return the new reset body of the handler
	 */
	public ConnectedBlocks resetBody(){
		return ((HandlerModel)getModel()).resetBody();
	}

	/**
	 * Returns the model of the body of the handler
	 * @return model of the body of the handler
	 */
	public Observable getBodymodel() {
		
		return ((HandlerModel)getModel()).getConnectedBlocks();
	}


}
