package ide.backend.variables;


import ide.backend.exceptions.TypeException;

/**
 * This class represents a Boolean
 * @author matthijs
 *
 */
public class BooleanVariable extends Variable {
	
	/* FIELDS */
	private boolean _content;
	
	/**
	 * Creates a new BooleanVariable with the value of the given boolean [in]
	 * @param in the content
	 */
	public BooleanVariable(boolean in) {
		super(VariableType.BOOLEAN);
		_content = in;
	}
	
	/**
	 *  Creates a new BooleanVariable with the value of the given booleanVariable [var] or a given valueVariable [var] 
	 * @param var
	 * @throws TypeException
	 */
	public BooleanVariable(Variable var) throws TypeException {
		super(VariableType.BOOLEAN);
		_content = parseVariable(var);
	}

	@Override
	public void setContent(Variable var) throws TypeException {
		_content = parseVariable(var);

	}

	@Override
	public Variable add(Variable var) throws TypeException {
		throw new TypeException("add on Boolean", this.getType(), var.getType());
	}

	@Override
	public Variable sub(Variable var) throws TypeException {
		throw new TypeException("sub on Boolean", this.getType(), var.getType());
	}

	@Override
	public Variable mul(Variable var) throws TypeException {
		throw new TypeException("mul on Boolean", this.getType(), var.getType());
	}

	@Override
	public Variable div(Variable var) throws TypeException {
		throw new TypeException("div on Boolean", this.getType(), var.getType());
	}

	@Override
	public Variable lessThan(Variable var) throws TypeException {
		throw new TypeException("lessThan on Boolean", this.getType(), var.getType());
	}

	@Override
	public Variable greaterThan(Variable var) throws TypeException {
		throw new TypeException("greaterThan on Boolean", this.getType(), var.getType());
	}

	@Override
	public Variable equal(Variable var) throws TypeException {
		equalType(var);
		return new BooleanVariable(_content == ((BooleanVariable) var)._content);
	}

	@Override
	public Variable pow(Variable var) throws TypeException {
		throw new TypeException("pow on Boolean", this.getType(), var.getType());
	}

	@Override
	public Variable sqrt(Variable var) throws TypeException {
		throw new TypeException("sqrt on Boolean", this.getType());
	}

	@Override
	public Variable mod(Variable var) throws TypeException {
		throw new TypeException("mod on Boolean", this.getType(), var.getType());
	}

	@Override
	public Variable toStringVariable() {
		return new StringVariable(Boolean.toString(_content));
	}

	@Override
	public Variable or(Variable var) throws TypeException {
		equalType(var);
		return new BooleanVariable(_content || parseVariable(var));
	}

	@Override
	public Variable not(Variable var) throws TypeException {
		//left
		return new BooleanVariable(!_content );
	}

	@Override
	public Variable and(Variable var) throws TypeException {
		equalType(var);
		return new BooleanVariable(_content && parseVariable(var));
	}
	
	/**
	 * This Function return a boolean equal to the value of a given BooleanVariable [var] or ValueVariable [var]
	 * @param var the variable to parse
	 * @return the boolean content from the variable
	 * @throws TypeException if the types dont match
	 */
	public static Boolean parseVariable(Variable var) throws TypeException {
		// Check if we are dealing with a boolean or with a value
		if (var.getType() == VariableType.BOOLEAN)
			return ((BooleanVariable) var)._content;
		else if (var.getType() == VariableType.VALUE)
			return ((ValueVariable) var).getContent().equals("true") ;
		else
			throw new TypeException("Type error: ", var.getType());	
	}

	@Override
	public void reset() {
		_content = true;
	}



}
