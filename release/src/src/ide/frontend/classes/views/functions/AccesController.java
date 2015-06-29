package ide.frontend.classes.views.functions;

import java.util.Observable;

import ide.backend.model.event.EventModel;
import ide.backend.model.function.AccessModel;
import ide.frontend.classes.views.AbstractBlockController;


/**
 * Controller for an accesmodel to interact with its model.
 * @author Matthijs Kaminski
 *
 */
public class AccesController extends AbstractBlockController{

	public AccesController(Observable model) {
		super(model);
	}
	
	/**
	 * returns the eventmodel of the handler if set. (else null)
	 * @return the eventmodel
	 */
	public EventModel getEventModel(){
		return ((AccessModel)getModel()).getEvent();
	}
	
	/**
	 * returns the selected member.
	 * @return the selected member
	 */
	public String getSelectedMember(){
		return ((AccessModel)getModel()).getMember();
	}
	
	/**
	 * Sets the selected member.
	 * @param member the member to set
	 */
	public void setSelectedMember(String member){
		((AccessModel)getModel()).setMember(member);
	}
	
}
