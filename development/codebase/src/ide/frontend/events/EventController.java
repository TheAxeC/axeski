package ide.frontend.events;

import java.util.ArrayList;
import java.util.Observable;

import ide.backend.model.event.EventModel;
import ide.backend.model.variables.VariableModel;
import ide.frontend.mvcbase.AbstractController;

/**
 * Controller for an eventView to interact with its model
 * @author Matthijs Kaminski
 *
 */
public class EventController extends AbstractController {

	/**
	 * Model to interact with.
	 * @param model eventmodel.
	 */
	public EventController(Observable model) {
		super(model);
	}
	
	/**
	 * add new member to the event with given name
	 * @param name name of the to be added member.
	 * @return created variable model for the new member.
	 */
	public VariableModel addMember(String name) {
		return ((EventModel)getModel()).addMember(name);
	}

	/**
	 * Removes a given member (variable) for its model
	 * @param variableModel model to be removed.
	 */
	public void removeMember(VariableModel variableModel) {
		((EventModel)getModel()).removeMember(variableModel);
		
	}
	
	/**
	 * returns the name of the eventmodel.
	 * @return name of the eventmodel.
	 */
	public String getName() {
		return ((EventModel)getModel()).getType();
	}

	/**
	 * returns all the existing models of the event.
	 * @return all variable models representing the members of the event.s
	 */
	public ArrayList<VariableModel> getMembers() {
		return ((EventModel)getModel()).getMembers();
	}


}
