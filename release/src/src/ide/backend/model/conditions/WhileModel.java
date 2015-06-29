package ide.backend.model.conditions;

import ide.backend.exceptions.CompileException;
import ide.backend.model.BlockModel;
import ide.backend.model.ConnectedBlocks;
import ide.backend.runtime.Compiler;
import ide.backend.save.DataSaver;
import ide.backend.variables.Variable.VariableType;
/**
 * Model reprensting a while block in the ide.
 * @author matthijs
 *
 */
public class WhileModel extends BlockModel{
	/**FIELDS**/
	//conditon of the if-block.
	private BlockModel _condition;
	//Body of the if-block.
	private ConnectedBlocks _body;
	// error state of the condition.
	private boolean _error;
	
	public WhileModel() {
		super(null);
		init();
	}
	
	/**
	 * Creates an new whileblock model
	 * @param parent
	 */
	public WhileModel(BlockModel parent) {
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
	 * @return
	 */
	public ConnectedBlocks getBody(){
		return _body;
	}
	
	/**
	 * returns the condition of the ifblock.
	 * @return
	 */
	public BlockModel getCondition(){
		return _condition;
	}
	
	/**
	 * Sets the condition of the ifblock to a given block.
	 * @param c
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
	 * @return 
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
	 * Sets the body of the while model to given block.
	 * @param procesBlocks blocks being set as model
	 */
	public void setBody(ConnectedBlocks procesBlocks) {
		_body = procesBlocks;	
	}
	
	/**
	 * Clear the debug flags of the model.
	 */
	public void clearDebugFlags() {
		_debug = false;
		_body.clearDebugFlags();
		updateView();
	}

	/**
	 * Returns the state of the error of the condition.
	 * @return
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
