package ide.frontend.classes.views;

import ide.backend.model.BlockModel;
import ide.backend.model.ConnectedBlocks;

import java.util.Observable;

/**
 * Controller for the anchor block
 * @author axelfaes
 *
 */
public class AnchorBlockController extends AbstractBlockController{

	/**
	 * Controller for the anchor
	 * @param model, model to obverse
	 */
	public AnchorBlockController(Observable model) {
		super(model);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Remove a block from the anchor.
	 * This removes that block and everything below that block
	 * @param block the block to remove
	 * @return the new connected block
	 */
	public ConnectedBlocks removeBlocks(BlockView block){
		return ((ConnectedBlocks)getModel()).removeBlocks((BlockModel)block.getModel());
	}
	
	/**
	 * Add a block to the anchor
	 * @param block the block to add
	 */
	public void addBlock(BlockView block){
		((ConnectedBlocks)getModel()).addBlock((BlockModel)block.getModel());
	}

}
