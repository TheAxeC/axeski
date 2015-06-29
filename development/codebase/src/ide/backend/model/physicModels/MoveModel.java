package ide.backend.model.physicModels;

import ide.backend.exceptions.CompileException;
import ide.backend.model.BlockModel;
import ide.backend.runtime.Compiler;
import ide.backend.save.DataSaver;
import ide.backend.variables.Variable.VariableType;

/**
 * This block allow an instance to increment its x and y position with the defined
 * x and y in this block.
 * @author Matthijs Kaminski
 */
public class MoveModel extends BlockModel{
	
	/**FIELDS**/
	private BlockModel _x;
	private BlockModel _y;
	private VariableType _XType;
	private VariableType _YType;
	private boolean _error;
	
	/**
	 * Creates a new moveModel with no parent.
	 */
	public MoveModel() {
		super(null);
		_x = null;
		_y = null;
	}
	
	/**
	 * Creates a new MoveModel with given parent
	 * @param parent parent of the model.
	 */
	public MoveModel(BlockModel parent){
		super(parent);
	}
	
	/**
	 * Returns the X set in this block.
	 * @return the model of the x 
	 */
	public BlockModel getX(){
		return _x;
	}
	
	/**
	 * Sets the x to the given x.
	 * @param x the model being set.
	 */
	public void setX(BlockModel x){
		_x = x;
		if (x != null)
			_x.changeParent(this);
		else{
			_XType = null;
			tellParentChanged(null, null);
		}
	}
	
	/**
	 * Returns the Y set in this block.
	 * @return the model representing y
	 */
	public BlockModel getY(){
		return _y;
	}
	
	/**
	 * Sets the y to the given y.
	 * @param y the model being set.
	 */
	public void setY(BlockModel y){
		_y = y;
		if (y != null)
			_y.changeParent(this);
		else{
			_YType = null;
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
		if (_x != null)
			_x.changeParent(this);
		if (_y != null)
			_y.changeParent(this);
	}

	@Override
	public <T> T compile(Compiler<T> c) throws CompileException {
		return  c.compileBlock(this);
	}

	@Override
	public <T> T save(DataSaver<T> s) {
		return s.saveBlock(this);
	}
	

	@Override
	public void tellParentChanged(BlockModel child,VariableType type) {
		if(child == getX()){
			_XType = type;
		}
		if(child == getY())
			_YType = type;
		
		if(_XType != null && _YType != null){
			if(_XType != VariableType.NUMBER && _XType != VariableType.VALUE )
				_error = true;
			else if(_YType != VariableType.NUMBER && _YType != VariableType.VALUE )
				_error = true;
			else 
				_error = false;

		}else{
			_error = false;
		}
		updateView();
		
	}

}
