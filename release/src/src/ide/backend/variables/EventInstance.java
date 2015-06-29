package ide.backend.variables;

import ide.backend.core.Event;
import ide.backend.exceptions.TypeException;
import ide.backend.exceptions.VariableNotFoundException;

import java.util.HashMap;

/**
 * Instance of an event
 * @author Axel
 */
public class EventInstance extends Variable {
	
	/** Reference to the class from which this object is an instance */
	private Event _event;
	
	/** Holds all variables contained within the event */
	private HashMap<String, Variable> _members;

	public EventInstance(Event event) {
		super(VariableType.EVENT);
		_event = event;
		_members = new HashMap<String, Variable>();
	}
	
	/**
	 * Getter for the class from which this object is an instance
	 * @return class from which this object is an instance
	 */
	public Event getEvent() {
		return _event;
	}
	
	/**
	 * Get a certain variable from the instance
	 * @param name, the name of the variable
	 * @return the variable with the given name
	 * @throws VariableNotFoundException, thrown if the variable doesnt exist
	 */
	public Variable getVar(String name) throws VariableNotFoundException {
		Variable out = _members.get(name);
		if(out == null)
			throw new VariableNotFoundException(name);
		return out;
	}
	
	/**
	 * Set a variable with a certain name
	 * @param key, the name of the variable
	 * @param val, the variable
	 */
	public void setVar(String key, Variable val) {
		_members.put(key, val);
	}

	@Override
	public void setContent(Variable var) throws TypeException {
		if (getType() != var.getType())
			throw new TypeException("Type error: ", getType(), var.getType());
		
		_event = ((EventInstance) var)._event;
		_members = ((EventInstance) var)._members;
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
		return new StringVariable("Event");
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
		return "EventInstance Variable";
	}

	@Override
	public void reset() {
	}
}
