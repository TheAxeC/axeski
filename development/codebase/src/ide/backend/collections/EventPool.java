package ide.backend.collections;

import java.util.HashMap;
import ide.backend.core.Event;

/**
 * This class contains all Events that are created in the program in the IDE. The class stores them in a hashmap so they can be accessed
 * via there TYPE [name].
 * If an event is added with a name (type) that already exists, it will be overwritten.
 * @author matthijs
 *
 */
public class EventPool {
	/*FIELDS*/
	private HashMap<String, Event> _events;
	
	/**
	 * Creates a new empty eventpool.
	 */
	public EventPool() {
		_events = new HashMap<String, Event>();
	}
	
	/**
	 * Adds an event to the eventpool.
	 * @param name name of the event
	 */
	public void addEvent(String name){
		_events.put(name, new Event(name));
	}
	
	/**
	 * Adds a given event with a given name to the eventpool.
	 * @param name name of the event
	 * @param e the event
	 */
	public void addEvent(String name, Event e){
		_events.put(name, e);
	}
	
	/**
	 * remove an event from the pool with the given name [name].
	 * @param name name of the event
	 */
	public void removeEvent(String name){
		_events.remove(name);
	}
	
	/**
	 * Return the event with the given name [name].
	 * @param name name of the event
	 * @return the event or null if not found
	 */
	public Event getEvent(String name){
		return _events.get(name);
	}
	
}
