package ide.backend.model.variables;

import ide.backend.exceptions.CompileException;
import ide.backend.model.BlockModel;
import ide.backend.runtime.Compiler;
import ide.backend.save.DataSaver;
import ide.backend.variables.Variable.VariableType;

/**
 * Represents an input value from the user
 * This input could be a number, string etc
 * @author Axel
 */
public class ValueModel extends BlockModel {
	
	//content of the value
	private String _content;
	
	/**
	 * Creates a new valuemodel with no parent.
	 */
	public ValueModel() {
		super(null);
		_content = "";
	}

	/**
	 * Creates a new ValueModel
	 * @param parent parent of the model
	 */
	public ValueModel(BlockModel parent) {
		super(parent);
		_content = "";
	}

	/**
	 * Get the content of the value block
	 * @return
	 */
	public String getContent() {
		return _content;
	}

	/**
	 * Set the content of the value block
	 * @param content
	 */
	public void setContent(String content) {
		this._content = content;
	}
	
	@Override
	public void changeParent(BlockModel p) {
		setParent(p);
		tellParentChanged(null, null);
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
		if(getParent() != null)
			getParent().tellParentChanged(this, VariableType.VALUE);
		
	}
	
}
