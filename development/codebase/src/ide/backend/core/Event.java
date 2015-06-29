package ide.backend.core;

import ide.backend.variables.EventInstance;
import ide.backend.variables.VariableFactory;
import ide.backend.variables.Variable.VariableType;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Represents a template for new events
 * @author Axel
 */
public class Event {
	
	/** The mapping of the types (of the variables) and their respective names */
	private HashMap<String, VariableType> _members;
	
	/** A unique identifier for this event */
	private String _type;
	
	public Event(String type) {
		_members = new HashMap<>();
		_type = type;
	}
	
	/**
	 * Creates a new event instance
	 * @return a new event instance
	 */
	public EventInstance makeEventInstance() {
		EventInstance inst = new EventInstance(this);
		
		for(Entry<String, VariableType> entry : _members.entrySet()) {
		    String key = entry.getKey();
		    VariableType type = entry.getValue();
		    
		    inst.setVar(key,  VariableFactory.create(type));
		}
		
		return inst;
	}
	
	/**
	 * Getter for the type of the event
	 * @return type to set
	 */
	public String getType() {
		return _type;
	}
	
	/**
	 * Adds a new type to this event
	 * @param name, name of the type
	 * @param type, the type to add
	 */
	public void addType(String name, VariableType type) {
		_members.put(name, type);
	}
	
	/**
	 * Remove a type identified with name [name]
	 * @param name, name of the type
	 */
	public void removeType(String name) {
		_members.remove(name);
	}
}
