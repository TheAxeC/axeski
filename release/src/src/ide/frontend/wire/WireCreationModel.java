package ide.frontend.wire;

import ide.backend.model.classes.ClassModel;
import ide.backend.model.classes.InstanceModel;
import ide.backend.model.classes.WireModel;
import ide.backend.model.event.EventModel;
import ide.backend.runtime.ModelCollection;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Wireframe model
 * @author axel
 *
 */
public class WireCreationModel extends Observable implements Observer  {
	
	/**
	 * The model collection
	 */
	ModelCollection _collection;
	
	/**
	 * Creates a new wireCreationModel
	 * @param collection Modelcollection to which it responds.
	 */
	public WireCreationModel(ModelCollection collection) {
		_collection = collection;
		_collection.addObserver(this);
	}

	@Override
	public void update(Observable o, Object arg) {
		this.setChanged();
		notifyObservers(arg);
		
	}

	/**
	 * Get all defined classes
	 * @return all classes
	 */
	public ArrayList<ClassModel> getClasses() {
		return _collection.getClassModels();
	}
	
	/**
	 * Add a new instance [i]
	 * @param i instance being added
	 */
	public void addInstance(InstanceModel i){
		_collection.addInstanceModel(i);
	}
	
	/**
	 * Remove an instance [i]
	 * @param i instance being removed
	 */
	public void removeInstance(InstanceModel i){
		_collection.removeInstanceModel(i);
	}

	/**
	 * Add a new wire from [instanceOut] to [instanceIn] with event
	 * [eventModel]
	 * @param instanceOut instance sending event
	 * @param eventModel event being send
	 * @param instanceIn instance  receiving event.
	 */
	public void addWire(InstanceModel instanceOut, EventModel eventModel,
			InstanceModel instanceIn) {
		_collection.addWireModel(new WireModel(null,instanceOut, instanceIn, eventModel));
	}
	
	/**
	 * Get all defined instances
	 * @return all instances
	 */
	public ArrayList<InstanceModel> getInstanceModels() {
		return _collection.getInstanceModels();
	}

	/**
	 * Get all wires
	 * @return all wires
	 */
	public ArrayList<WireModel> getWireModels() {
		return _collection.getWireModels();
	}

	/**
	 * Removes a wire form the wireframe.
	 * @param instanceOut instance sending event
	 * @param eventModel event being send
	 * @param instanceIn instance  receiving event.
	 */
	public void removeWire(InstanceModel instanceModelOut,
			EventModel eventModel, InstanceModel instanceModelIn) {
		_collection.removeWire(instanceModelOut,eventModel, instanceModelIn);
		
	}

	/**
	 * returns the currently existing events in the program.
	 * @return all eventmodels
	 */
	public ArrayList<EventModel> getExistingEvents() {
		return _collection.getEventModels();
		
	}

}
