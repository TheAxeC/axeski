package ide.frontend.events;

import ide.backend.model.event.EventModel;
import ide.backend.runtime.ModelCollection;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Model for the eventCreationview. 
 * This model interacts with the global modelcollection of the program.
 * @author Matthijs Kaminski
 */
public class EventCreationModel extends Observable implements Observer {
	/*FIELDS*/
	//model collection of the progam.
	ModelCollection _collection;
	
	/**
	 * Creates a new EventCreationModel
	 * @param collection ModelCollection of the program to which the one interacts.
	 * and observes for changes.
	 */
	public EventCreationModel(ModelCollection collection) {
		_collection = collection;
		collection.addObserver(this);
	}
	
	/**
	 * Add an event to the program.
	 * @param name name of the new event.
	 * @return returns a newly created eventmodel of the given name
	 */
	public EventModel addEvent(String name){
		EventModel out = new EventModel(null, name);
		_collection.addEventModel(out);
		return out;
	}
	
	/**
	 * Checks whether an event with given name already exists.
	 * @param name name to be cheked.
	 * @return if such an event exists.
	 */
	public boolean existEvent(String name){
		return _collection.getEventModel(name) != null;

	}
	
	/**
	 * remove a given event
	 * @param m model of the event to be removed.
	 */
	public void removeEvent(EventModel m){
		_collection.removeEventModel(m);
	}
	
	/**
	 * Returns all the existing event models in the program.
	 * @return
	 */
	public ArrayList<EventModel> getModels(){
		return _collection.getEventModels();
	}


	@Override
	public void update(Observable o, Object arg) {
		if(arg == null){
			this.setChanged();
			notifyObservers( _collection.getNonDefaultEventModels());
		}
		
	}
}
