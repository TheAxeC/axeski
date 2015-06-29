package ide.backend.model.conditions;

import ide.backend.exceptions.CompileException;
import ide.backend.model.BlockModel;
import ide.backend.model.ConnectedBlocks;
import ide.backend.runtime.Compiler;
import ide.backend.save.DataSaver;
import ide.backend.variables.Variable.VariableType;
/**
 * Forever block a loop that runs from the moment is in entered until
 * the end of the program
 * @author Matthijs Kaminski
 *
 */
public class ForeverModel extends BlockModel{
	/**FIELDS**/
	//Body of the forever-block.
	private ConnectedBlocks _body;
	
	/**
	 * Creates a new forevermodel. 
	 */
	public ForeverModel() {
		super(null);
		init();
	}
	
	/**
	 * Creates an new ifblock model
	 * @param parent paren mdel
	 */
	public ForeverModel(BlockModel parent) {
		super(parent);
		init();
	}

	/**
	 * Init the body and the condition
	 */
	private void init() {
		_body = new ConnectedBlocks(this);
	}

	
	/**
	 * Adds a block to the body of the if.
	 * @param m, block to be added.
	 */
	public void addBlock(BlockModel m){
		_body.addBlock(m);
	}
	
	/**
	 * Returns the body of the ifblock.
	 * @return the body
	 */
	public ConnectedBlocks getBody(){
		return _body;
	}
	
	
	/**
	 * Makes the body of the ifblock empty and returns the new model.
	 * @return the new body
	 */
	public ConnectedBlocks resetBody(){
		return _body = new ConnectedBlocks(this);
	}

	@Override
	public void changeParent(BlockModel p) {
		setParent(p);
		_body.changeParent(this);
	}


	/**
	 * Set the body
	 * @param procesBlocks the new body
	 */
	public void setBody(ConnectedBlocks procesBlocks) {
		_body = procesBlocks;	
	}
	
	@Override
	public void clearDebugFlags() {
		_debug = false;
		_body.clearDebugFlags();
		updateView();
	}

	@Override
	public <T> T compile(Compiler<T> c) throws CompileException {
		return c.compileBlock(this);
	}

	@Override
	public <T> T save(DataSaver<T> s) {
		return s.saveBlock(this);
	}
	

	@Override
	public void tellParentChanged(BlockModel child,VariableType type) {
		//DO NOTHING
	}
}
