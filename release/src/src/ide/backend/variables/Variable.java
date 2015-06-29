package ide.backend.variables;

import ide.backend.exceptions.TypeException;

/**
 * The basic variable class.
 * This class is the base class for all different variables used in the IDE
 * Each variable is expressed using an enum.
 * @author Axel
 */
public abstract class Variable {
	
	/**
	 * An enumeration used to indentify the type of the Variable
	 */
	public enum VariableType {
		/** Indicates a boolean type */
		BOOLEAN,
		/** Indicates a numeric value */
		NUMBER,
		/** Indicates a string value */
		STRING,
		/** Indicates a event value */
		EVENT,
		/** Indicates a raw value that still needs to be processed by the VM */
		VALUE
	}
	
	/** Indicates the type of the Variable */
	private VariableType _type; 

	public Variable(VariableType type) {
		_type = type;
	}
	
	/**
	 * Returns the type of this variable.
	 * @return the type of this Variable
	 */
	public VariableType getType() {
		return _type;
	}

	/**
	 * This function simulates the assignment operator
	 * The executed operation is: this = [var]
	 * @param var, the right hand side value
	 * @throws TypeException, thrown when the assignment doesnt exist on the variable
	 */
	public abstract void setContent(Variable var) throws TypeException;
	
	/**
	 * This function simulates the addition operator
	 * The executed operation is: return = this + [var]
	 * @param var, the right hand side value
	 * @throws TypeException, thrown when the addition doesnt exist on the variable
	 */
	public abstract Variable add(Variable var) throws TypeException;
	
	/**
	 * This function simulates the subtraction operator
	 * The executed operation is: return = this - [var]
	 * @param var, the right hand side value
	 * @throws TypeException, thrown when the subtraction doesnt exist on the variable
	 */
	public abstract Variable sub(Variable var) throws TypeException;
	
	/**
	 * This function simulates the multiply operator
	 * The executed operation is: return = this * [var]
	 * @param var, the right hand side value
	 * @throws TypeException, thrown when the multiply doesnt exist on the variable
	 */
	public abstract Variable mul(Variable var) throws TypeException;
	
	/**
	 * This function simulates the multiply operator
	 * The executed operation is: return = this / [var]
	 * @param var, the right hand side value
	 * @throws TypeException, thrown when the division doesnt exist on the variable
	 * 				or when you divide by zero
	 */
	public abstract Variable div(Variable var) throws TypeException;
	
	/**
	 * This function simulates the lessThan operator
	 * The executed operation is: return = this < [var]
	 * @param var, the right hand side value
	 * @throws TypeException, thrown when the lessThan doesnt exist on the variable
	 */
	public abstract Variable lessThan(Variable var) throws TypeException;
	
	/**
	 * This function simulates the greaterThan operator
	 * The executed operation is: return = this > [var]
	 * @param var, the right hand side value
	 * @throws TypeException, thrown when the greaterThan doesnt exist on the variable
	 */
	public abstract Variable greaterThan(Variable var) throws TypeException;
	
	/**
	 * This function simulates the equals operator
	 * The executed operation is: return = this == [var]
	 * @param var, the right hand side value
	 * @throws TypeException, thrown when the equals doesnt exist on the variable
	 */
	public abstract Variable equal(Variable var) throws TypeException;
	
	/**
	 * This function simulates the power operator
	 * The executed operation is: return = this ^ [var]
	 * @param var, the right hand side value
	 * @throws TypeException, thrown when the power doesnt exist on the variable
	 */
	public abstract Variable pow(Variable var) throws TypeException;
	
	/**
	 * This function simulates the multiply operator
	 * The executed operation is: return = sqrt(this)
	 * @param var, a dummy variable to keep the function binair
	 * @throws TypeException, thrown when the sqrt doesnt exist on the variable
	 */
	public abstract Variable sqrt(Variable var) throws TypeException;
	
	/**
	 * This function simulates the moduleo operator
	 * The executed operation is: return = this % [var]
	 * @param var, the right hand side value
	 * @throws TypeException, thrown when the modulo doesnt exist on the variable
	 */
	public abstract Variable mod(Variable var) throws TypeException;
	
	/**
	 * This function simulates the logic OR operator
	 * The executed operation is: return = this || [var]
	 * @param var, the right hand side value
	 * @throws TypeException, thrown when the OR doesnt exist on the variable
	 */
	public abstract Variable or(Variable var) throws TypeException;
	

	/**
	 * This function simulates the logic NOT operator
	 * The executed operation is: return = this ! [var]
	 * @param var, a dummy variable to keep the function binair
	 * @throws TypeException, thrown when the NOT doesnt exist on the variable
	 */
	public abstract Variable not(Variable var) throws TypeException;

	/**
	 * This function simulates the logic AND operator
	 * The executed operation is: return = this && [var]
	 * @param var, the right hand side value
	 * @throws TypeException, thrown when the AND doesnt exist on the variable
	 */
	public abstract Variable and(Variable var) throws TypeException;
	
	/**
	 * Converts the current variable into a string variable
	 * @return the string version of this variable
	 */
	public abstract Variable toStringVariable();
	
	
	/**
	 * This function checks whether the type of a given variables equals the variables type or is a value.
	 * If not equal or not a value operator throws TypesException
	 * @param var, the right hand side value
	 * @throws TypeException thrown when the type isn't equal or not a value
	 */
	protected void equalType(Variable var) throws TypeException {

		if (_type != var.getType() && var.getType() != VariableType.VALUE)

			throw new TypeException("Type error: ", _type, var.getType());
	}

	/**
	 * Reset the content of the variable
	 */
	public abstract void reset();
}
