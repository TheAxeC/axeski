package ide.frontend.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

import javax.swing.ImageIcon;

import ide.backend.model.BlockModel;
import ide.backend.model.classes.ClassModel;
import ide.backend.model.classes.Costume;
import ide.backend.model.event.EventModel;
import ide.backend.model.function.FunctionModel;
import ide.backend.model.function.HandlerModel;
import ide.backend.model.variables.MemberModel;
import ide.backend.variables.Variable.VariableType;
import ide.frontend.mvcbase.AbstractController;

/**
 * Controller for the class
 * @author axelfaes
 *
 */
public class ClassModelController extends AbstractController{

	public ClassModelController(Observable model) {
		super(model);
	}

	/**
	 * Add a block to the class (IDEPANEL).
	 * @param m block to add
	 */
	public void addBlock(BlockModel m) {
		m.changeParent(null);
		if(m.getClass() == HandlerModel.class)
			((ClassModel)getModel()).addHandler(null, (HandlerModel)m);
		else if(m.getClass() == FunctionModel.class){
			((ClassModel)getModel()).addFunction((FunctionModel)m);;
		}else{
			((ClassModel)getModel()).addFloatBlock(m);
		}	
		
	}
	
	/**
	 * Removes a block form the class (IDEPANEL).
	 * @param m block to remove
	 */
	public void removeBlock(BlockModel m) {
		if(m.getClass() == HandlerModel.class) {
			if (((HandlerModel)m).getEventRef() != null)
				((ClassModel)getModel()).removeHandler(((HandlerModel)m).getEventRef().getEventModel(), (HandlerModel)m);
			else
				((ClassModel)getModel()).removeHandler(null, (HandlerModel)m);
		}
		else if(m.getClass() == FunctionModel.class){
			((ClassModel)getModel()).removeFunction((FunctionModel)m);
		}else{
			((ClassModel)getModel()).removeFloatBlock(m);
		}		
	}

	/**
	 * Add an Input Event to the Class.
	 * @param eventModel event to add as input
	 */
	public void addInputEvent(EventModel eventModel) {
		((ClassModel)getModel()).addInputEvent(eventModel);
	}
	
	/**
	 * Add an Input Event to the Class.
	 * @param eventModel event to remove
	 */
	public void removeInputEvent(EventModel eventModel) {
		((ClassModel)getModel()).removeInputEvent(eventModel);
	}
	
	
	/**
	 * Change the event Handled by a handler.
	 * @param e event from the handler
	 * @param h the handler
	 */
	public void changeHandlerEvent(EventModel e, HandlerModel h){
		((ClassModel)getModel()).changeHandlerEvent(e, h);
	}

	/**
	 * Get a list of all input events
	 * @return list of all input events
	 */
	public ArrayList<EventModel> getInputEvents() {
		return ((ClassModel)getModel()).getInputEvents();
	}

	/**
	 * Remove an memberVariable from the Class.
	 * @param name item to remove
	 */
	public void removeMemberVar(String name) {
		((ClassModel)getModel()).removeMember(name);
		
	}

	/**
	 * Returns whether a member with a given name exists or not.
	 * @param name name of the member
	 * @return whether it exists or not
	 */
	public boolean containsMemberVar(String name) {
		return ((ClassModel)getModel()).getMembers().containsKey(name);
	}

	/**
	 * Returns an arrayList containing all the names of the members.
	 * @return list of all names
	 */
	public ArrayList<String> getMemberNames() {
		ArrayList<String> out = new ArrayList<String>();
		for (String string : ((ClassModel)getModel()).getMembers().keySet()) {
			out.add(string);
		}
		
		return out;
	}
	
	/**
	 * Returns a copy of the current members collection.
	 * @return list of all members linked to their names
	 */
	public HashMap<String, MemberModel> getMembers(){
		HashMap<String, MemberModel> out = new HashMap<String, MemberModel>();
		for (HashMap.Entry<String, MemberModel> entry : ((ClassModel)getModel()).getMembers().entrySet()) {
		  out.put(entry.getKey(), entry.getValue());
		}
		return  out;
	}
	
	/**
	 * Adds a new member variable to the classmodel.
	 * @param name, name of the new variable
	 * @param type, type of the new variable
	 * @return the member model
	 */
	public MemberModel addMemberVar(String name, VariableType type){
		return ((ClassModel)getModel()).addMember(name, type);
	}
	
	/**
	 * Returns the current Costumes of the class.
	 * @return list of all costumes
	 */
	public ArrayList<Costume> getCostumes(){
		ArrayList<Costume> out = new ArrayList<Costume>();
		for (HashMap.Entry<String, Costume> entry : ((ClassModel)getModel()).getCostumes().entrySet()) {
			  out.add(entry.getValue());
			}
		return  out;
	}

	/**
	 * Adds a new Costume to the class.
	 * @param name Name of the costume.
	 * @param path Path of the image of the costume.
	 * @param img Loaded Image of the costume.
	 */
	public void addCostume(String name, String path, ImageIcon img) {
		Costume c = new Costume(name, path);
		c.setImage(img);
		((ClassModel)getModel()).addCostume(name, c);
		
	}
	/**
	 * Removes costume of given name from the class.
	 * @param name name of the costume to remove
	 */
	public void removeCostume(String name){
		((ClassModel)getModel()).removeCostume(name);
	}
	
	/**
	 * Sets the primary costume of the class to the costume with the given name.
	 * @param name Name of the costume to be set primary.
	 * @pre Class should contain costume with given name.
	 */
	public void setPrimaryCostume(String name){
		((ClassModel)getModel()).setSelectedCostume(name);
	}

	/**
	 * Returns the current primary costume name of the class.
	 * @return name of the costume.
	 */
	public String getPrimaryCostume() {
		return ((ClassModel)getModel()).getSelectedCostume();
	}

	/**
	 * Returns if the class already contains a costume with given name.
	 * @param text Name to be checked
	 * @return if the class already contains a costume with given name.
	 */
	public boolean containsCostume(String name) {
		return ((ClassModel)getModel()).getCostumes().containsKey(name);
	}

	/**
	 * Removes the output event with the given name
	 * @param event name of the output event
	 */
	public void removeOutputEvent(String event) {
		((ClassModel)getModel()).removeOutputEvent(event);
		
	}

}
