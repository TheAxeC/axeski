package ide.backend.variables;


import ide.backend.exceptions.TypeException;

/**
 * This class represents a String 
 * @author matthijs
 *
 */
public class StringVariable extends Variable {
	
	/* FIELDS */
	private String _content;
	
	/**
	 * Creates a new StringVariable representing the value of the given String [in].
	 * @param in, the given string.
	 */
	public StringVariable(String in) {
		
		super(VariableType.STRING);
		_content = new String(in);	
		
	}
	
	/**
	 * Creates a new StringVariable representing the value of the given StringVariable [var] or ValueVariable [var].
	 * @param var
	 * @throws TypeException
	 */
	public StringVariable(Variable var) throws TypeException {
		super(VariableType.STRING);
		_content = parseVariable(var);
	}

	@Override
	public void setContent(Variable var) throws TypeException {
		equalType(var);
		_content = ((StringVariable)var.toStringVariable())._content;
		
	}

	@Override
	public Variable add(Variable var) throws TypeException {
		equalType(var);
		StringVariable out = new StringVariable(_content + ((StringVariable)var.toStringVariable())._content);
		return out;
	}

	@Override
	public Variable sub(Variable var) throws TypeException {
		throw new TypeException("Sub on String", this.getType(), var.getType());
	}

	@Override
	public Variable mul(Variable var) throws TypeException {
		throw new TypeException("mul on String", this.getType(), var.getType());
	}

	@Override
	public Variable div(Variable var) throws TypeException {
		throw new TypeException("div on String", this.getType(), var.getType());
	}

	@Override
	public Variable lessThan(Variable var) throws TypeException {
		equalType(var);
		return new BooleanVariable(_content.compareTo(((StringVariable)var.toStringVariable())._content) < 0);
	}

	@Override
	public Variable greaterThan(Variable var) throws TypeException {
		equalType(var);
		return new BooleanVariable(_content.compareTo(((StringVariable)var.toStringVariable())._content) > 0);
	}

	@Override
	public Variable equal(Variable var) throws TypeException {
		equalType(var);
		return new BooleanVariable(_content.compareTo(((StringVariable)var.toStringVariable())._content) == 0);
	}

	@Override
	public Variable pow(Variable var) throws TypeException {
		throw new TypeException("Power on String", this.getType(), var.getType());
	}

	@Override
	public Variable sqrt(Variable var) throws TypeException {
		throw new TypeException("Sqrt on String", this.getType());
	}

	@Override
	public Variable mod(Variable var) throws TypeException {
		throw new TypeException("mod on String", this.getType(), var.getType());
		
	}
	
	@Override
	public Variable or(Variable var) throws TypeException {
		throw new TypeException("OR on String", this.getType(), var.getType());
	}

	@Override
	public Variable not(Variable var) throws TypeException {
		throw new TypeException("NOT on String", this.getType());
	}

	@Override
	public Variable and(Variable var) throws TypeException {
		throw new TypeException("AND on String", this.getType(), var.getType());
	}

	@Override
	public Variable toStringVariable() {
		return this;
	}
	
	/**
	 * This function returns  a String representing the value of the given StringVariable [var] or ValueVariable [var].
	 * @param var
	 * @return
	 * @throws TypeException
	 */
	private String parseVariable(Variable var) throws TypeException {
		equalType(var);
		// Check if we are dealing with a number or with a value
		if (var.getType() == VariableType.STRING)
			return ((StringVariable) var)._content;
		else {
			return ((ValueVariable) var).getContent();
			
		}
	}
	
	/**
	 * Returns the content of the variable so it can be printed.
	 * @return
	 */
	public String toString(){
		return _content;
	}

	@Override
	public void reset() {
		_content = "";
	}
	
	
}
