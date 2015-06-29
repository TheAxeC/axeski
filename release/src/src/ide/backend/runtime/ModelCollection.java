package ide.backend.runtime;

import ide.backend.model.classes.ClassModel;
import ide.backend.model.classes.InstanceModel;
import ide.backend.model.classes.WireFrameModel;
import ide.backend.model.classes.WireModel;
import ide.backend.model.event.EventModel;

import java.util.ArrayList;
import java.util.Observable;

/**
 * Collection of all models
 * @author axel en Matthijs
 */
public class ModelCollection extends Observable {
	/**FIELDS**/
	//classmodels
	protected ArrayList<ClassModel> _models;
	//events
	protected ArrayList<EventModel> _events;
	//default events
	protected ArrayList<EventModel> _defaultEvents;
	//wireframeModel
	protected WireFrameModel _wireFrameModel;

	/**
	 * Creates a new ModelCollection
	 */
	public ModelCollection() {
		reset();
	}
	
	/**
	 * Alert all the observers for changes to the modelcollection
	 */
	public void alertObservers(){
		this.setChanged();
		this.notifyObservers();
	}
	
	/**
	 * resets the modelCollection
	 */
	public void reset() {
		_models = new ArrayList<ClassModel>();
		_events = new ArrayList<EventModel>();
		_defaultEvents = new ArrayList<EventModel>();
		_wireFrameModel = new WireFrameModel(null);
		alertObservers();
		createDefaultEvents();
	}
	
	/**
	 * Getter for the classModels
	 * @return the classModels
	 */
	public ArrayList<ClassModel> getClassModels() {
		return _models;
	}

	/**
	 * Getter for the eventModels
	 * @return the eventModels
	 */
	public ArrayList<EventModel> getEventModels() {
		ArrayList<EventModel> out = new ArrayList<EventModel>();
		out.addAll(_events);
		out.addAll(_defaultEvents);
		return out;
	}
	
	/**
	 * Get a eventModel [evt] to the runtime
	 * If the model with name [name] doesnt exist, return null
	 * @param evt name of the model
	 * @return the model if found else null.
	 */
	public EventModel getEventModel(String name) {
		for(EventModel evt: _events) {
			if (evt.getType().equals(name))
				return evt;
		}
		for(EventModel evt: _defaultEvents) {
			if (evt.getType().equals(name))
				return evt;
		}
		return null;
	}
	
	/**
	 * Add a eventModel [evt] to the runtime
	 * @param evt EventModel being added.
	 */
	public void addEventModel(EventModel evt) {
		_events.add(evt);
		Object[] arg = new Object[2];
		arg[0] = new String("addEvent");
		arg[1] = evt;
		this.setChanged();
		notifyObservers(arg);
	}
	
	/**
	 * Removes an eventModel [evt] from the runtime.
	 * @param evt eventmodel being removed.
	 */
	public void removeEventModel(EventModel evt){
		_events.remove(evt);
		Object[] arg = new Object[2];
		arg[0] = new String("deleteEvent");
		arg[1] = evt;
		this.setChanged();
		notifyObservers(arg);
	}
	
	/**
	 * Removes a class form the collection
	 * @param cls ClassModel being removed.
	 */
	public void removeClass(ClassModel cls) {
		_models.remove(cls);
		Object[] arg = new Object[2];
		arg[0] = new String("deleteClass");
		arg[1] = cls;
		this.setChanged();
		notifyObservers(arg);
	}
	
	/**
	 * Add a classModel [cls] to the runtime
	 * @param cls classmodel being added
	 */
	public void addClassModel(ClassModel cls) {
		_models.add(cls);
		Object[] arg = new Object[2];
		arg[0] = new String("addClass");
		arg[1] = cls;
		this.setChanged();
		notifyObservers(arg);
	}
	
	/**
	 * Adds an instanceModel to the wireFrameModel of the runtime.
	 * @param m InstanceModel being added.
	 */
	public void addInstanceModel(InstanceModel m){
		_wireFrameModel.addInstance(m);
	}
	
	/**
	 * removes an instanceModel to the wireFrameModel of the runtime.
	 * @param m instanceModel being removed
	 */
	public void removeInstanceModel(InstanceModel m){
		_wireFrameModel.removeInstance(m);
	}
	
	/**
	 * Returns the classModel with the given name [cl] if found in the models. Else it returns null.
	 * @param cl name of the class model
	 * @return the classmodel if exists else null.
	 */
	public ClassModel getClassModel(String cl){
		for (ClassModel model : _models) {
			if(model.getName().equals(cl))
				return model;
		}
		return null;
	}
	
	/**
	 * Get the instanceModel with the given name
	 * @param name name of the instance model
	 * @return IstanceModel if exists else null.
	 */
	public InstanceModel getInstanceModel(String name){
		return _wireFrameModel.getInstance(name);
	}
	
	/**
	 * Getter for the WireFrameModel
	 * @return the WireFrameModel
	 */
	public WireFrameModel getWireFrameModel() {
		return _wireFrameModel;
	}
	
	/**
	 * Adds a wireModel to the WireFrameModel of the runtime.
	 * @param wire wire being added
	 */
	public void addWireModel(WireModel wire) {
		_wireFrameModel.addWire(wire);
		
	}
	/**
	 * Create all default events.
	 */
	private void createDefaultEvents() {
		_defaultEvents.add(new EventModel(null, "<Start>"));
		
		for(String name: EventCatcher.getDefaultEvents()) {
			_defaultEvents.add(new EventModel(null, name));
		}
	}

	/**
	 * returns the non defaultEventModels.
	 * @return array containing all non defaultEvents.
	 */
	public  ArrayList<EventModel> getNonDefaultEventModels() {
		return _events;
	}

	/**
	 * Returns if a class exists with a given name.
	 * @param name name being checked for existence
	 * @return true if exists else false.
	 */
	public boolean containsClass(String name) {
		for (ClassModel c : _models) {
			if(c.getName().equals(name))
				return true;
		}
		return false;
	}

	/**
	 * Get all instance models
	 * @return alle the instanceModels in the collection
	 */
	public ArrayList<InstanceModel> getInstanceModels() {
		return _wireFrameModel.getInstances();
	}
	
	
	/**
	 * Get all wires
	 * @return all the wires in the collection
	 */
	public ArrayList<WireModel> getWireModels() {
		return _wireFrameModel.getWires();
	}

	/**
	 * Clear debug flags in all block in all classes.
	 */
	public void clearDebugFlags() {
		for(ClassModel m: _models) {
			m.clearDebugFlags();
		}
	}	

	/**
	 * Removes a wire define by the given parameters.
	 * @param instanceModelOut From side of the wire
	 * @param eventModel	Event from the wire
	 * @param instanceModelIn To side of the wire.
	 */
	public void removeWire(InstanceModel instanceModelOut,
			EventModel eventModel, InstanceModel instanceModelIn) {
		for(WireModel m :_wireFrameModel.getWires()){
			if(m.getEvent().equals(eventModel.getType()) &&
					m.getFrom().equals(instanceModelOut.getName()) &&
					m.getTo().equals(instanceModelIn.getName())){
				_wireFrameModel.removeWire(m);
				return;
			}
		}
	}

	
}
