package ide.backend.variables;

import ide.backend.exceptions.TypeException;

/**
 * A number variable
 * @author Axel
 */
public class NumberVariable extends Variable {

	/** The number used to represent the variable */
	private double _content;
	
	/**
	 * Creates a new NumberVariable with given content parsed as a double
	 * @param content content
	 * @throws TypeException if parsing fails exception is thrown
	 */
	public NumberVariable(Variable content) throws TypeException {
		super(VariableType.NUMBER);
		_content = parseVariable(content);
	}
	
	/**
	 * Creates a new variable with given content
	 * @param content content of the variable
	 */
	public NumberVariable(double content) {
		super(VariableType.NUMBER);
		_content = content;
	}

	@Override
	public void setContent(Variable var) throws TypeException {
		_content = parseVariable(var);
	}

	@Override
	public Variable add(Variable var) throws TypeException {
		double other = parseVariable(var);
		return new NumberVariable(_content + other);
	}

	@Override
	public Variable sub(Variable var) throws TypeException {
		double other = parseVariable(var);
		return new NumberVariable(_content - other);
	}

	@Override
	public Variable mul(Variable var) throws TypeException {
		double other = parseVariable(var);
		return new NumberVariable(_content * other);
	}

	@Override
	public Variable div(Variable var) throws TypeException {
		double other = parseVariable(var);
		if (other == 0)
			throw new TypeException("Divide by zero: ", getType());
		return new NumberVariable(_content / other);
	}

	@Override
	public Variable lessThan(Variable var) throws TypeException {
		double other = parseVariable(var);
		return new BooleanVariable(_content < other);
	}

	@Override
	public Variable greaterThan(Variable var) throws TypeException {
		double other = parseVariable(var);
		return new BooleanVariable(_content > other);
	}

	@Override
	public Variable equal(Variable var) throws TypeException {
		double other = parseVariable(var);
		return new BooleanVariable(_content == other);
	}

	@Override
	public Variable pow(Variable var) throws TypeException {
		double other = parseVariable(var);
		return new NumberVariable(Math.pow(_content, other));
	}

	@Override
	public Variable sqrt(Variable var) throws TypeException {
		return new NumberVariable(Math.sqrt(_content));
	}

	@Override
	public Variable mod(Variable var) throws TypeException {
		double other = parseVariable(var);
		return new NumberVariable(_content % other);
	}

	@Override
	public Variable toStringVariable() {
		return new StringVariable(Double.toString(_content));
	}

	@Override
	public Variable or(Variable var) throws TypeException {
		throw new TypeException("Or on Number: ", getType(), var.getType());
	}

	@Override
	public Variable not(Variable var) throws TypeException {
		throw new TypeException("Not on Number: ", getType());
	}

	@Override
	public Variable and(Variable var) throws TypeException {
		throw new TypeException("And on Number: ", getType(), var.getType());
	}

	/**
	 * Parses a variable. It will convert a Variable into a number
	 * @param var the variable to parsed
	 * @return the parsed number
	 * @throws TypeException will be thrown if the parsing cant happen because of an incorrect type
	 */

	
	public static double parseVariable(Variable var) throws TypeException {
		// Check if we are dealing with a number or with a value
		if (var.getType() == VariableType.NUMBER)
			return ((NumberVariable) var)._content;
		else {
			try {
				return Double.parseDouble(((ValueVariable) var).getContent());
			} catch (NumberFormatException e) {
				throw new TypeException("Value type is nan: ", var.getType());
			}
		}
	}

	@Override
	public String toString() {
		return "Number Variable: " + _content;
	}
	
	/**
	 * Returns the number value that this variable represents. 
	 * @return
	 */
	public double getContent(){
		return _content;
	}
	
	@Override
	public void reset() {
		_content = 0;
	}
}
