package ide.frontend.classes;

import ide.backend.model.classes.ClassModel;
import ide.backend.model.event.EventModel;
import ide.backend.runtime.ModelCollection;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Model to create new classes
 * @author axelfaes
 */
public class ClassCreationModel extends Observable implements Observer   {
	/*FIELDS*/
	private ModelCollection _collection;
	
	/**
	 * @param collection the collection of a user made program
	 */
	public ClassCreationModel(ModelCollection collection) {
		_collection = collection;
		_collection.addObserver(this);
	}
	
	/**
	 * Create a new class with the given name
	 * @param name name of the class
	 * @return the classmodel
	 */
	public ClassModel addClass(String name){
		ClassModel out = new ClassModel(name);
		_collection.addClassModel(out);
		return out;
	}
	
	/**
	 * Remove a class from the program
	 * @param cls class to remove
	 */
	public void removeClass(ClassModel name) {
		_collection.removeClass(name);
	}
	
	/**
	 * Check if a class with the given nam already exists
	 * @param name name of a class
	 * @return whether the class already exists
	 */
	public boolean containsClass(String name){
		return _collection.containsClass( name);
	}

	/**
	 * Get all classes
	 * @return list of all classmodels
	 */
	public ArrayList<ClassModel> getClasses() {
		return _collection.getClassModels();
	}

	@Override
	public void update(Observable o, Object arg) {
		if(arg == null){
			this.setChanged();
			notifyObservers();
		}else{
			this.setChanged();
			notifyObservers(arg);
		}
	}

	/**
	 * Get all events
	 * @return list of all eventmodels
	 */
	public ArrayList<EventModel> getEvents() {
		return _collection.getEventModels();
	}
	
	
}

