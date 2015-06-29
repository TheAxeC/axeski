package ide.backend.collections;

import ide.backend.core.ClassContainer;
import ide.backend.core.Event;
import ide.backend.core.Instance;
import ide.backend.model.classes.InstanceModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Contains the entire wireframe from the current program
 * Every instance placed in this wireframe and all his wires 
 * (to other instances, labeled with an event), are stored in the
 * WiredInstances
 * @author Axel
 */
public class WireFrame {
	
	private HashMap<Instance, WiredInstance> _instances;
	
	private HashMap<String, Instance> _nameOfInstances;
	
	private EventPool _pool;

	/**
	 * Creates a new wirefrome
	 * @param pool Eventpool
	 */
	public WireFrame(EventPool pool) {
		_instances = new HashMap<Instance, WiredInstance>();
		_nameOfInstances = new HashMap<>();
		_pool = pool;
	}

	
	/**
	 * Get all connected instance labeled by event [event] from Instance [from]
	 * @param event
	 * @param from
	 * @return Get all connected instance from event [event]
	 */
	public ArrayList<Instance> getConnected(Event event, Instance from) {
		WiredInstance inst = _instances.get(from);
		if (inst == null)
			return null;
		return inst.getConnected(event);
	}
	
	/**
	 * Adds a new instance to the wireframe
	 * @param c the metatable from the instance to add
	 */
	public void addInstance(ClassContainer c, String name, InstanceModel model) {
		Instance tmp = c.makeInstance(name, model);
		_instances.put(tmp, new WiredInstance());
		_nameOfInstances.put(name, tmp);
	}
	
	/**
	 * Remove Instance [i] from the wireFrame
	 * @param i the instance to remove
	 */
	public void removeInstance(Instance i) {
		_instances.remove(i);
	}
	
	/**
	 * Get all registered instances
	 * @return all registered instances
	 */
	public ArrayList<Instance> getAllInstances() {
		ArrayList<Instance> instances = new ArrayList<Instance>();
		for(Entry<Instance, WiredInstance> entry : _instances.entrySet()) {
			instances.add(entry.getKey());
		}
		return instances;
	}
	
	/**
	 * Add a new connection from Instance [from] to Instance [to]
	 * with label [ev]
	 * @param from instance sending event
	 * @param to instance recieving event
	 * @param ev event being send
	 */
	public void addConnection(String from, String to, Event ev) {
		WiredInstance inst = _instances.get(_nameOfInstances.get(from));
		if (inst == null)
			return;
		inst.addConnection(ev, _nameOfInstances.get(to));
	}
	
	/**
	 * Removes all events labeled by name [eventName]
	 * @param eventName name of event.
	 */
	public void removeConnection(String eventName) {
		Event a = _pool.getEvent(eventName);
		
		for(Entry<Instance, WiredInstance> entry : _instances.entrySet()) {
			WiredInstance val = entry.getValue();
		    val.removeEvent(a);
		}
	}

	/**
	 * Get the instance with the given name
	 * @param instance instance name being searched.
	 * @return
	 */
	public Instance getInstance(String instance) {
		for(Entry<Instance, WiredInstance> entry : _instances.entrySet()) {
			if (entry.getKey().getName().equals(instance))
				return entry.getKey();
		}
		return null;
	}
}
