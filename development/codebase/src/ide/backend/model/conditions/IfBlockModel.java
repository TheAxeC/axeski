package ide.backend.model.conditions;

import ide.backend.exceptions.CompileException;
import ide.backend.model.BlockModel;
import ide.backend.model.ConnectedBlocks;
import ide.backend.runtime.Compiler;
import ide.backend.save.DataSaver;
import ide.backend.variables.Variable.VariableType;
/**
 * This Model represent an ifBlock in the IDE. 
 * @author matthijs
 *
 */
public class IfBlockModel extends BlockModel{
	/**FIELDS**/
	//conditon of the if-block.
	private BlockModel _condition;
	//Body of the if-block.
	private ConnectedBlocks _body;
	//error state of condition
	private boolean _error;
	
	/**
	 * Creates a IfBlockModel with no parent.
	 */
	public IfBlockModel() {
		super(null);
		init();
	}
	
	/**
	 * Creates an new IfBlockModel with given parent
	 * @param parent parent of the model.
	 */
	public IfBlockModel(BlockModel parent) {
		super(parent);
		init();
	}

	/**
	 * Init the body and the condition
	 */
	private void init() {
		_body = new ConnectedBlocks(this);
		_condition = null;
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
	 * @return model of the body of the if.
	 */
	public ConnectedBlocks getBody(){
		return _body;
	}
	
	/**
	 * returns the condition of the ifblock.
	 * @return model of the condition of the if.
	 */
	public BlockModel getCondition(){
		return _condition;
	}
	
	/**
	 * Sets the condition of the ifblock to a given block.
	 * @param c model being set as condition.
	 */
	public void setCondition(BlockModel c){
		_condition = c;
		if (c != null)
			c.changeParent(this);
		else
			tellParentChanged(null, null);
	}
	
	/**
	 * Makes the body of the ifblock empty and returns the new model.
	 * @return new model of the body.
	 */
	public ConnectedBlocks resetBody(){
		return _body = new ConnectedBlocks(this);
	}

	@Override
	public void changeParent(BlockModel p) {
		setParent(p);
		_body.changeParent(this);
		
		if (_condition != null)
			_condition.changeParent(this);
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
	 * Set the body for the if
	 * @param procesBlocks
	 */
	public void setBody(ConnectedBlocks procesBlocks) {
		_body = procesBlocks;
	}
	
	/**
	 * Clear all debug flags
	 */
	public void clearDebugFlags() {
		_debug = false;
		_body.clearDebugFlags();
		updateView();
	}
	
	/**
	 * Returns the state of the error of the condition.
	 * @return state of error
	 */
	public boolean getError(){
		return _error;
	}
	

	@Override
	public void tellParentChanged(BlockModel child,VariableType type) {
		if(type != null &&type != VariableType.BOOLEAN && type != VariableType.VALUE)
			_error = true;
		else
			_error = false;
		
		updateView();
	}

}
