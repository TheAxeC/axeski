package ide.backend.model;

import ide.backend.exceptions.CompileException;
import ide.backend.runtime.Compiler;
import ide.backend.save.DataSaver;
import ide.backend.variables.Variable.VariableType;

import java.util.ArrayList;

/**
 * This class is used to represent a group of connectedBlocks in the IDE.
 * Every basic block (not Functions or Handlers) will be inside a ConnectedBlock when created. So they can be added to other connectedBlocks
 * or a connectBlock can be added to them.
 * A connected model however has no mapping in the execution. 
 * @author matthijs
 *
 */
public class ConnectedBlocks extends BlockModel{
	
	/*FIELDS*/
	/**Blocks in this connectedblock**/
	private ArrayList<BlockModel> _blocks;
	
	/**
	 * Constructor for a connectedBlock with a parameter for a block [block] that it contains.
	 * Like the base Class it has a parent block.
	 * @param parent the parent block
	 * @param block the first content of the connected block
	 */
	public ConnectedBlocks(BlockModel parent, BlockModel block) {
		super(parent);
		_blocks = new ArrayList<BlockModel>();
		_blocks.add(block);
		block.changeParent(this);
		updateView();
	}
	
	/**
	 * Constructor for creating a empty connectedBlock.
	 * @param parent the parent
	 */
	public ConnectedBlocks(BlockModel parent) {
		super(parent);
		_blocks = new ArrayList<BlockModel>();
		updateView();
	}
	
	/**
	 * Adding a connectedBlock [other] to this connectingBlock
	 * @post all the blocks in other will be moved to this block, with other resulting empty.
	 * @param other other connected block
	 */
	public void addConnectedBlock(ConnectedBlocks other){
		_blocks.addAll(other._blocks);
		for (BlockModel block : other._blocks) {
			block.changeParent(this);
		}
		other.removeBlocks();
		updateView();
	}
	
	/**
	 * Adding a block [other] to this connectingBlock
	 * @param other the block to add
	 */
	public void addBlock(BlockModel other){
		_blocks.add(other);
		other.changeParent(this);
		updateView();
	}
	
	/**
	 * Remove all blocks contained by this ConnectedBlock.
	 */
	public void removeBlocks(){
		_blocks.clear();
		updateView();
	}
	
	/**
	 * Removes and returns all blocks in this connectblock starting from the given block [block].
	 * @param block, the block which is the top of the removedblocks.
	 * @return a new connectedblockContaining all blocks.
	 */
	public ConnectedBlocks removeBlocks(BlockModel block){
		int index = _blocks.indexOf(block);
		ConnectedBlocks out = new ConnectedBlocks(null,block);
		index++;
		for(; index < _blocks.size(); index++){
			out.addBlock(_blocks.get(index));
		}
		_blocks.removeAll(out._blocks);
		updateView();
		return out;
	}
	
	@Override
	public void changeParent(BlockModel p) {
		
		for (BlockModel model : _blocks) {
			model.changeParent(this);
		}
		setParent(p);
		for (BlockModel model : _blocks) {
			model.changeParent(this);
		}
		
	}
	
	/**
	 * Returns the InnerBlocks contained by this ConnectedBlock.
	 * @return list of all blocks
	 */
	public ArrayList<BlockModel> getBlocks(){
		return _blocks;
	}
	
	@Override
	public <T> T compile(Compiler<T> c) throws CompileException {
		return c.compileBlock(this);
	}
	
	@Override
	public <T> T save(DataSaver<T> s) {
		return s.saveBlock(this);
	}

	/**
	 * Cleares all the debug flags for its childs.
	 */
	public void clearDebugFlags() {
		_debug = false;
		for(BlockModel m: _blocks)
			m.clearDebugFlags();
		updateView();
	}
	
	@Override
	public void tellParentChanged(BlockModel child,VariableType type) {
		//DO NOTHING
	}
}
