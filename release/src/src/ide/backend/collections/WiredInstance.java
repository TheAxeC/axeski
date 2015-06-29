package ide.backend.collections;

import ide.backend.core.Event;
import ide.backend.core.Instance;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is used in the WireFrame
 * It shows all connections with certain events, to some instances
 * @author Axel
 *
 */
public class WiredInstance {
	
	private HashMap<Event, ArrayList<Instance>> _events;

	/**
	 * This class is used in the WireFrame
	 * It shows all connections with certain events, to some instances
	 */
	public WiredInstance() {
		_events = new HashMap<Event, ArrayList<Instance>>();
	}
	
	/**
	 * Get all connected instance from event [event]
	 * @param event
	 * @return Get all connected instance from event [event]
	 */
	public ArrayList<Instance> getConnected(Event event) {
		ArrayList<Instance> inst = _events.get(event);
		if (inst == null)
			return new ArrayList<Instance>();
		return inst;
	}
	
	/**
	 * Add a new connection to these wires
	 * @param event, the type of the connection
	 * @param instance, to which the wire connects
	 */
	public void addConnection(Event event, Instance instance) {
		ArrayList<Instance> list = getConnected(event);
		if (list.isEmpty()) {
			list = new ArrayList<Instance>();
			list.add(instance);
			_events.put(event, list);
		}
		else
			list.add(instance);
	}
	
	/**
	 * Remove an event
	 * @param event, event to remove
	 */
	public void removeEvent(Event event) {
		_events.remove(event);
	}

}
