package ide.frontend.classes.views.functions;

import ide.backend.model.classes.ClassModel;
import ide.backend.model.event.EventModel;
import ide.backend.model.event.RefEventModel;
import ide.backend.model.function.EmitModel;
import ide.backend.model.variables.AbstractRefVariabelModel;
import ide.backend.variables.Variable.VariableType;
import ide.frontend.classes.views.AbstractBlockController;

import java.util.ArrayList;
import java.util.Observable;

/**
 * Controller for the emit block
 * @author axel
 *
 */
public class EmitController extends AbstractBlockController {
	
	/**
	 * Names and types of the event that is selected
	 */
	private ArrayList<String> _memberNames;
	private ArrayList<VariableType> _memberTypes;

	public EmitController(Observable model) {
		super(model);
		_memberNames = new ArrayList<>();
		_memberTypes = new ArrayList<>();
	}
	
	/**
	 * Set the event correctly from the model
	 * @param cls, the classmodel we're working in
	 * @param events, all possible events
	 * @param evt, currently selected event
	 * @return the member names
	 * 			null indicates no change
	 */
	public void setEvent(ClassModel cls, ArrayList<EventModel> events, String evt) {
		EmitModel model = ((EmitModel)getModel());
		
		RefEventModel modelevt = model.getEvent();

		if (modelevt == null || !modelevt.getEvent().equals(evt)) {
			for(EventModel m: events) {
				if (m.getType().equals(evt)) {
					RefEventModel newRef = m.makeReference(model);
					model.setEvent(newRef);
					if (modelevt != null)
						cls.removeOutputEvent(modelevt.getEventModel());
					cls.addOutputEvent(m);
				}
			}
		}
		_memberNames = model.getEvent().getEventModel().getMemberNames();
		_memberTypes = model.getEvent().getEventModel().getMemberTypes();
	}
	
	/**
	 * Generate the data from an event that is being emitted
	 */
	public void generateData() {
		EmitModel model = ((EmitModel)getModel());
		
		if (model.getEvent() != null) {
			_memberNames = model.getEvent().getEventModel().getMemberNames();
			_memberTypes = model.getEvent().getEventModel().getMemberTypes();
		}
	}
	
	/**
	 * Deselect the event from the emit
	 * @param cls, the classmodel we're working in
	 */
	public void deselectEvent(ClassModel cls) {
		EmitModel model = ((EmitModel)getModel());
		
		RefEventModel modelevt = model.getEvent();
		if (modelevt == null) return;
		
		cls.removeOutputEvent(modelevt.getEventModel());
		model.setEvent(null);
		_memberNames.clear();
		_memberTypes.clear();
	}
	
	/**
	 * Get all the member names.
	 * @return list of all members
	 */
	public ArrayList<String> getMembers() {
		return _memberNames;
	}

	/**
	 * Get all the member types.
	 * @return list of all types
	 */
	public ArrayList<VariableType> getTypes() {
		return _memberTypes;
	}
	
	/**
	 * Set the member.
	 * @param member member to set
	 * @param model model to set the member to
	 */
	public void setMember(String member, AbstractRefVariabelModel model) {
		((EmitModel)getModel()).setMember(member, model);
	}
	
	/**
	 * Returns the error state of the model.
	 * @return state of error.
	 */
	public boolean getError(){
		return ((EmitModel)getModel()).getError();
	}

}