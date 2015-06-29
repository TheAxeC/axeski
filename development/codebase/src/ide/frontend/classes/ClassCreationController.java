package ide.frontend.classes;

import java.util.ArrayList;
import java.util.Observable;

import ide.backend.model.classes.ClassModel;
import ide.backend.model.event.EventModel;
import ide.frontend.mvcbase.AbstractController;

/**
 * Controller for the creation of a class
 * @author Matthijs
 */
public class ClassCreationController extends AbstractController {

	public ClassCreationController(Observable model) {
		super(model);	
	}
	
	/**
	 * Check if a class with the given nam already exists
	 * @param name name of a class
	 * @return whether the class already exists
	 */
	public boolean containsClass(String name){
		return ((ClassCreationModel)getModel()).containsClass(name);
	}

	/**
	 * Create a new class with the given name
	 * @param name name of the class
	 * @return the classmodel
	 */
	public ClassModel createClass(String name){
		return ((ClassCreationModel)getModel()).addClass(name);
	}
	
	/**
	 * Remove a class from the program
	 * @param cls class to remove
	 */
	public void removeClass(ClassModel cls) {
		((ClassCreationModel)getModel()).removeClass(cls);
	}

	/**
	 * Get all classes
	 * @return list of all classmodels
	 */
	public ArrayList<ClassModel> getClasses() {
		
		return ((ClassCreationModel)getModel()).getClasses();
	}

	/**
	 * Get all events
	 * @return list of all eventmodels
	 */
	public ArrayList<EventModel> getEvents() {
		return ((ClassCreationModel)getModel()).getEvents();
	}
}
