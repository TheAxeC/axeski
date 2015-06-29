package ide.backend.model.physicModels;

import ide.backend.exceptions.CompileException;
import ide.backend.model.BlockModel;
import ide.backend.runtime.Compiler;
import ide.backend.save.DataSaver;
import ide.backend.variables.Variable.VariableType;

/**
 * Model for the change appearance block.
 * The appearance of the instance will be set to content 
 * of its content converted to a string
 * @author matthijs
 *
 */
public class ChangeAppearanceModel extends BlockModel {
	
	/** The appearance of the instance will be set to content 
	 * of its content converted to a string 
	 **/
	private BlockModel _content;

	public ChangeAppearanceModel() {
		super(null);
	}
	public ChangeAppearanceModel(BlockModel prnt) {
		super(prnt);
	}

	/** 
	 * Getter for the content of the print model
	 * @return the content
	 */
	public BlockModel getContent() {
		return _content;
	}

	/**
	 * Setter for the content of the printModel
	 * @param content the new content
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
		//DO NOTHING
	}

}
