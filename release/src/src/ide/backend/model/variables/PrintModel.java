package ide.backend.model.variables;

import ide.backend.exceptions.CompileException;
import ide.backend.model.BlockModel;
import ide.backend.runtime.Compiler;
import ide.backend.save.DataSaver;
import ide.backend.variables.Variable.VariableType;

/**
 * The model for the print function
 * @author axel
 */
public class PrintModel extends BlockModel {
	
	/** The contents which this model will print to the screen */
	private BlockModel _content;

	/**
	 * Creates a new printModel with no parent nor content.
	 */
	public PrintModel() {
		super(null);
	}
	
	/**
	 * Creates a new printModel with given parent.
	 * No content is set.
	 * @param prnt parent of the model.
	 */
	public PrintModel(BlockModel prnt) {
		super(prnt);
	}

	/** 
	 * Getter for the content of the print model
	 * @return
	 */
	public BlockModel getContent() {
		return _content;
	}

	/**
	 * Setter for the content of the printModel
	 * @param _content
	 */
	public void setContent(BlockModel content) {
		this._content = content;
		if(_content != null)
			_content.changeParent(this);
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
		// Do nothing	
	}

}
