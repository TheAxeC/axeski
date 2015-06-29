package ide.backend.model.string;

import ide.backend.exceptions.CompileException;
import ide.backend.model.BlockModel;
import ide.backend.runtime.Compiler;
import ide.backend.save.DataSaver;
import ide.backend.variables.Variable.VariableType;

/**
 * Concat Model, used to concatonate two strings
 * @author axel
 *
 */
public class ConcatModel extends BlockModel {
	
	/** The contents which this model will concatonate */
	private BlockModel _left, _right;

	private boolean _error;

	public ConcatModel() {
		super(null);
	}
	public ConcatModel(BlockModel prnt) {
		super(prnt);
	}

	/** 
	 * Getter for the content of the concat model
	 * @return the left string block
	 */
	public BlockModel getLeft() {
		return _left;
	}

	/**
	 * Setter for the content of the concat
	 * @param content the left content
	 */
	public void setLeft(BlockModel left) {
		this._left = left;
		if(left != null)
			left.changeParent(this);
		else{
			tellParentChanged(null, null);				
		}
	}
	
	/** 
	 * Getter for the content of the concat model
	 * @return the right string block
	 */
	public BlockModel getRight() {
		return _right;
	}

	/**
	 * Setter for the content of the concat
	 * @param content the right content
	 */
	public void setRight(BlockModel right) {
		this._right = right;
		if(right != null)
			right.changeParent(this);
		else{
			tellParentChanged(null, null);				
		}
		
	}
	
	/**
	 * Returns the state of the error of the model
	 * @return the error
	 */
	public boolean getError(){
		return _error;
	}

	@Override
	public void changeParent(BlockModel p) {
		setParent(p);
		
		if (_right != null) _right.changeParent(this);
		if (_left != null) _left.changeParent(this);
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
			getParent().tellParentChanged(this,VariableType.STRING);
	}

}
