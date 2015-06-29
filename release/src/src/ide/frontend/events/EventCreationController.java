package ide.frontend.events;

import java.util.Observable;

import ide.backend.model.event.EventModel;
import ide.frontend.mvcbase.AbstractController;

/**
 * Controller for the eventCreationView to interact with its model
 * @author Matthijs Kaminski
 *
 */
public class EventCreationController extends AbstractController{

	/**
	 * Creates a new EventCreationController.
	 * @param model model to interact with.
	 */
	public EventCreationController(Observable model) {
		super(model);
	}

	/**
	 * add a event with given name
	 * @param text name of the added event
	 * @return created model of the event.
	 */
	public EventModel addEvent(String text) {
		return ((EventCreationModel)getModel()).addEvent(text);
	}

	/**
	 * removes a given event model from its model
	 * @param m model of the event.
	 */
	public void removeEvent(EventModel m) {
		((EventCreationModel)getModel()).removeEvent(m);
	}
	
	/**
	 * Check if the name is the name of a default event
	 * @param name, name of the event
	 * @return true if name is the name of a default event
	 */
	public boolean isDefaultEvent(String name) {
		return !((EventCreationModel)getModel()).existEvent(name);
	}
}
