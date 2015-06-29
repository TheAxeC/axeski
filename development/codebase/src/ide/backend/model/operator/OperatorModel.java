package ide.backend.model.operator;

import java.util.ArrayList;

import ide.backend.blocks.operator.TypeChecking;
import ide.backend.exceptions.CompileException;
import ide.backend.model.BlockModel;
import ide.backend.runtime.Compiler;
import ide.backend.save.DataSaver;
import ide.backend.variables.ArithOperators;
import ide.backend.variables.LogicOperators;
import ide.backend.variables.Variable.VariableType;

/**
 * This Class represents a binary operator in the iDE.
 * @author matthijs
 *
 */
public class OperatorModel extends BlockModel {

	/** The contents which this model will print to the screen */
	private BlockModel _left, _right;
	
	/** the operator by this model */
	private String _operator;
	//type of left block
	private VariableType _leftType;
	//type of right block
	private VariableType _rightType;
	//state of error;
	private boolean _error;

	/**
	 * Creates a new operator with no parent.
	 * default operator +.
	 */
	public OperatorModel(){
		super(null);
		_operator = "+";
	}
	
	/**
	 * Creates a new operatorModel with given parent
	 * @param prnt parent of the model.
	 * default operator +.
	 */
	public OperatorModel(BlockModel prnt) {
		super(prnt);
		_operator = "+";
	}
	
	/**
	 * Creates a new OperatorModel with given parent, left and right operand and operator
	 * @param prnt parent of the model
	 * @param left left operand
	 * @param operator operator
	 * @param right right operand
	 */
	public OperatorModel(BlockModel prnt, BlockModel left, String operator, BlockModel right) {
		super(prnt);
		_left = left;
		_right = right;
		_operator = operator;
	}

	/** 
	 * Getter for the content of the operator model
	 * @return
	 */
	public BlockModel getLeft() {
		return _left;
	}

	/**
	 * Setter for the content of the operatorModel
	 * @param _content
	 */
	public void setLeft(BlockModel left) {
		this._left = left;
		if(left != null)
			left.changeParent(this);
		else{
			_leftType = null;
			tellParentChanged(null, null);
		}
	}
	
	/** 
	 * Getter for the content of the operator model
	 * @return
	 */
	public BlockModel getRight() {
		return _right;
	}

	/**
	 * Setter for the content of the operatorModel
	 * @param _content
	 */
	public void setRight(BlockModel right) {
		this._right = right;
		if(right != null)
			right.changeParent(this);
		else{
			_rightType = null;
			tellParentChanged(null, null);
		}
	}
	
	/** 
	 * Getter for the operator of the operator model
	 * @return
	 */
	public String getOperator() {
		return _operator;
	}

	/**
	 * Setter for the operator of the operatorModel.
	 * Tell parent if exists that operator is changed.
	 * @param _content
	 */
	public void setOperator(String operator) {
		this._operator = operator;
		tellParentChanged(null, null);
	}
	
	public ArrayList<String> getPossibleOperators(){
		ArrayList<String> out = new ArrayList<String>();
		for(String type :LogicOperators.operators.keySet()){
			out.add(type);
		}
		for(String type : ArithOperators.operators.keySet()){
			out.add(type);
		}
		return out;
	}
	
	/**
	 * Returns the state of the error of the model
	 * @return
	 */
	public boolean getError(){
		return _error;
	}
	
	@Override
	public void changeParent(BlockModel p) {
		setParent(p);
		if(_left != null)
			_left.changeParent(this);
		if(_right != null)
			_right.changeParent(this);
		
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
		if(child == getLeft()){
			_leftType = type;
		}
		if(child == getRight())
			_rightType = type;
		
		if(_leftType != null && _rightType != null){
			VariableType out = TypeChecking.typeCheck(getOperator(), _leftType, _rightType);
			if(out != null){
				if(getParent() != null)
					getParent().tellParentChanged(this,out);
				_error = false;
			}else
				_error = true;

		}else{
			_error = false;
		}
		updateView();
	}
	

}
