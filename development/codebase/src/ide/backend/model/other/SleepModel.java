package ide.backend.model.other;

import ide.backend.exceptions.CompileException;
import ide.backend.model.BlockModel;
import ide.backend.runtime.Compiler;
import ide.backend.save.DataSaver;
import ide.backend.variables.Variable.VariableType;

/***
 * This block represents a sleepblock in the IDE.
 * @author Axel
 *
 */
public class SleepModel extends BlockModel {
	/** FIELDS ***/
	private BlockModel _content;

	/**
	 * Creates a new sleepmodel with no parent or content.
	 */
	public SleepModel() {
		super(null);
		_content = null;
	}
	
	/**
	 * Creates a new sleepmodel with given parent and no content.
	 * @param prnt parent of the model.
	 */
	public SleepModel(BlockModel prnt) {
		super(prnt);
		_content = null;
	}
	
	/**
	 * Get the variable model
	 * @return
	 */
	public BlockModel getContent(){
		return _content;	
	}
	
	/**
	 * Set the variableModel [name]
	 * @param name, the variableModel
	 */
	public void setContent(BlockModel model) {
		this._content = model;
		if(_content != null){
			_content.changeParent(this);
		}
	}
	
	@Override
	public void changeParent(BlockModel p) {
		setParent(p);
		if(_content != null)
		_content.changeParent(this);
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
