package ide.backend.variables;

import ide.backend.exceptions.TypeException;

/**
 * A raw value variable
 * Input from the user is directly converted into a value variable
 * Casting to the correct type happens on runtime.
 * @author Axel
 */
public class ValueVariable extends Variable {

	/** the raw value */
	private String _content;
	
	public ValueVariable(String content) {
		super(VariableType.VALUE);
		_content = content;
	}
	
	/**
	 * Get the contained string from this value
	 * @return the contained string
	 */
	public String getContent() {
		return _content;
	}

	@Override
	public void setContent(Variable var) throws TypeException {
		throw new TypeException("Assignment on Value: ", getType(), var.getType());
	}

	@Override
	public Variable add(Variable var) throws TypeException {
		throw new TypeException("Addition on Value: ", getType(), var.getType());
	}

	@Override
	public Variable sub(Variable var) throws TypeException {
		throw new TypeException("Subtraction on Value: ", getType(), var.getType());
	}

	@Override
	public Variable mul(Variable var) throws TypeException {
		throw new TypeException("Multiply on Value: ", getType(), var.getType());
	}

	@Override
	public Variable div(Variable var) throws TypeException {
		throw new TypeException("Division on Value: ", getType(), var.getType());
	}

	@Override
	public Variable lessThan(Variable var) throws TypeException {
		throw new TypeException("Less than on Value: ", getType(), var.getType());
	}

	@Override
	public Variable greaterThan(Variable var) throws TypeException {
		throw new TypeException("Greater than on Value: ", getType(), var.getType());
	}

	@Override
	public Variable equal(Variable var) throws TypeException {
		throw new TypeException("Equal on Value: ", getType(), var.getType());
	}

	@Override
	public Variable pow(Variable var) throws TypeException {
		throw new TypeException("Power on Value: ", getType(), var.getType());
	}

	@Override
	public Variable sqrt(Variable var) throws TypeException {
		throw new TypeException("Sqrt on Value: ", getType());
	}

	@Override
	public Variable mod(Variable var) throws TypeException {
		throw new TypeException("Mod on Value: ", getType(), var.getType());
	}

	@Override
	public Variable toStringVariable() {
		return new StringVariable(_content);
	}

	@Override
	public Variable or(Variable var) throws TypeException {
		throw new TypeException("Or on Value: ", getType(), var.getType());
	}

	@Override
	public Variable not(Variable var) throws TypeException {
		throw new TypeException("Not on Value: ", getType());
	}

	@Override
	public Variable and(Variable var) throws TypeException {
		throw new TypeException("And on Value: ", getType(), var.getType());
	}
	
	@Override
	public String toString() {
		return "Value Variable: " + _content;
	}
	
	@Override
	public void reset() {
		_content = "";
	}
}
