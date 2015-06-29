package ide.backend.model.string;

import ide.backend.exceptions.CompileException;
import ide.backend.model.BlockModel;
import ide.backend.runtime.Compiler;
import ide.backend.save.DataSaver;
import ide.backend.variables.Variable.VariableType;

/**
 * Length model
 * Used to receive the length of a string
 * @author axel
 *
 */
public class LengthModel extends BlockModel {
	
	/** The contents which this model will take the length of */
	private BlockModel _content;
	private VariableType _contentType;
	private boolean _error;

	/**
	 * Creates a new lengthModel with no parent nor string.
	 */
	public LengthModel() {
		super(null);
	}
	/**
	 * Creates a new LengthModel with given parent.
	 * String content is not set.
	 * @param prnt parent of the model.
	 */
	public LengthModel(BlockModel prnt) {
		super(prnt);
		_error = false;
	}

	/** 
	 * Getter for the content of the lengthmodel
	 * @return
	 */
	public BlockModel getContent() {
		_error = false;
		return _content;
	}

	/**
	 * Setter for the content of the lengthmodel
	 * @param _content String to check length of.
	 */
	public void setContent(BlockModel content) {
		this._content = content;
		if(_content != null)
			_content.changeParent(this);
		else{
			_contentType = null;
			tellParentChanged(null, null);
		}
	}
	
	/**
	 * Returns the state of error of the model
	 * @return state of error.
	 */
	public boolean getError(){
		return _error;
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
		if(child == getContent()){
			_contentType = type;
		}
		if(_contentType != null){
			if(_contentType != VariableType.STRING && _contentType != VariableType.VALUE )
				_error = true;
			else 
				_error = false;

		}else{
			_error = false;
		}
		updateView();
		if(getParent() != null)
			getParent().tellParentChanged(this,VariableType.NUMBER);
	}

}
