package ide.backend.model.string;

import ide.backend.exceptions.CompileException;
import ide.backend.model.BlockModel;
import ide.backend.runtime.Compiler;
import ide.backend.save.DataSaver;
import ide.backend.variables.Variable.VariableType;

/**
 * CharAt model, used to retreive character from a model
 * @author axel
 *
 */
public class CharAtModel extends BlockModel {
	
	/** The contents which this model will concatonate */
	// string and index of the chatAr model
	private BlockModel _str, _ind;
	// The variable types of the index block and the string block
	private VariableType _indexType, _strType;
	// The error message
	private boolean _error;

	// Empty constructor for the block creator
	public CharAtModel() {
		super(null);
		_error = false;
	}
	public CharAtModel(BlockModel prnt) {
		super(prnt);
		_error = false;
	}

	/** 
	 * Getter for the content of the charat model
	 * @return the content
	 */
	public BlockModel getContent() {
		return _str;
	}

	/**
	 * Setter for the content of the charat Model
	 * @param str the block that represents the string
	 */
	public void setContent(BlockModel str) {
		this._str = str;
		if(str != null)
			str.changeParent(this);
		else{
			_strType = null;
			tellParentChanged(null, null);
		}
	}
	
	/** 
	 * Getter for the content of the charat model
	 * @return the block that represents the index
	 */
	public BlockModel getIndex() {
		return _ind;
	}

	/**
	 * Setter for the content of the charat model.
	 * @param ind, the index
	 */
	public void setIndex(BlockModel ind) {
		this._ind = ind;
		if(ind != null)
			ind.changeParent(this);
		else{
			_indexType = null;
			tellParentChanged(null, null);
		}
			
	}
	
	/**
	 * Returns the state of error of the model
	 * @return the error
	 */
	public boolean getError(){
		return _error;
	}
	

	@Override
	public void changeParent(BlockModel p) {
		setParent(p);
		
		if (_ind != null) _ind.changeParent(this);
		if (_str != null) _str.changeParent(this);
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
		if(child == getIndex()){
			_indexType = type;
		}
		if(child == getContent())
			_strType = type;
		
		if(_indexType != null && _strType != null){
			if(_indexType != VariableType.NUMBER && _indexType != VariableType.VALUE )
				_error = true;
			else if(_strType != VariableType.STRING && _strType != VariableType.VALUE )
				_error = true;
			else 
				_error = false;

		}else{
			_error = false;
		}
		updateView();
		if(getParent() != null)
			getParent().tellParentChanged(this,VariableType.STRING);
	}

}