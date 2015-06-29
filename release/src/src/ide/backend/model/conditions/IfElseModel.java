package ide.backend.model.conditions;

import ide.backend.exceptions.CompileException;
import ide.backend.model.BlockModel;
import ide.backend.model.ConnectedBlocks;
import ide.backend.runtime.Compiler;
import ide.backend.save.DataSaver;
import ide.backend.variables.Variable.VariableType;

/**
 * The model represents an if-else in the IDE
 * @author axel
 *
 */
public class IfElseModel extends BlockModel{
	/**FIELDS**/
	//conditon of the if-block.
	private BlockModel _condition;
	//Body of the if-block.
	private ConnectedBlocks _bodyIf;
	//Body of the else-block.
	private ConnectedBlocks _bodyElse;
	// error state of the condition.
	private boolean _error;
	
	/**
	 * Creates an IfElseModel with no parent.
	 */
	public IfElseModel() {
		super(null);
		init();
	}
	
	/**
	 * Creates an new ifblock model with given parent.
	 * @param parent parent of the model.
	 */
	public IfElseModel(BlockModel parent) {
		super(parent);
		init();
	}

	/**
	 * Init the body and the condition
	 */
	private void init() {
		_bodyIf = new ConnectedBlocks(this);
		_bodyElse = new ConnectedBlocks(this);
		_condition = null;
	}

	
	/**
	 * Adds a block to the body of the if.
	 * @param m, block to be added.
	 */
	public void addBlockIf(BlockModel m){
		_bodyIf.addBlock(m);
	}
	
	/**
	 * Adds a block to the body of the else.
	 * @param m, block to be added.
	 */
	public void addBlockElse(BlockModel m){
		_bodyElse.addBlock(m);
	}
	
	/**
	 * Returns the body of the ifblock.
	 * @return model of the body of if.
	 */
	public ConnectedBlocks getBodyIf(){
		return _bodyIf;
	}
	
	/**
	 * Returns the body of the elseblock.
	 * @return model of body of else.
	 */
	public ConnectedBlocks getBodyElse(){
		return _bodyElse;
	}
	
	/**
	 * returns the condition of the ifblock.
	 * @return model of condition.
	 */
	public BlockModel getCondition(){
		return _condition;
	}
	
	/**
	 * Sets the condition of the ifblock to a given block.
	 * @param c BlockModel being set as Condition.
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
	 * @return the new body of the if.
	 */
	public ConnectedBlocks resetBodyIf(){
		return _bodyIf = new ConnectedBlocks(this);
	}

	/**
	 * Makes the body of the elseblock empty and returns the new model.
	 * @return the new body of the else.
	 */
	public ConnectedBlocks resetBodyElse(){
		return _bodyElse = new ConnectedBlocks(this);
	}
	
	@Override
	public void changeParent(BlockModel p) {
		setParent(p);
		_bodyIf.changeParent(this);
		_bodyElse.changeParent(this);
		
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
	 * @param procesBlocks Model to be set as body of if.
	 */
	public void setBodyIf(ConnectedBlocks procesBlocks) {
		_bodyIf = procesBlocks;
	}
	
	/**
	 * Set the body for the else
	 * @param procesBlocks model to be set as body of else.
	 */
	public void setBodyElse(ConnectedBlocks procesBlocks) {
		_bodyElse = procesBlocks;
	}
	
	/**
	 * Clear the debug flags
	 */
	public void clearDebugFlags() {
		_debug = false;
		_bodyIf.clearDebugFlags();
		_bodyElse.clearDebugFlags();
		updateView();
	}
	
	/**
	 * Returns the state of the error of the condition.
	 * @return state of error.
	 */
	public boolean getError(){
		return _error;
	}
	

	@Override
	public void tellParentChanged(BlockModel child,VariableType type) {
		if(type != null && type != VariableType.BOOLEAN && type != VariableType.VALUE)
			_error = true;
		else
			_error = false;
		
		updateView();
	}

}