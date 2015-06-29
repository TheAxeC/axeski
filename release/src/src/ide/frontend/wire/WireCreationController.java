package ide.frontend.wire;

import java.util.ArrayList;
import java.util.Observable;

import ide.backend.model.classes.ClassModel;
import ide.backend.model.classes.InstanceModel;
import ide.backend.model.classes.WireModel;
import ide.backend.model.event.EventModel;
import ide.frontend.mvcbase.AbstractController;

/**
 * Controller for the wireframe
 * @author axel
 *
 */
public class WireCreationController extends AbstractController{

	/**
	 * Creates a new controller for a view to interact with its model
	 * @param model model being interacted with
	 */
	public WireCreationController(Observable model) {
		super(model);
		
	}

	/**
	 * Get all defined classes
	 * @return
	 */
	public ArrayList<ClassModel> getClasses() {
		return ((WireCreationModel)getModel()).getClasses();
	}
	
	/**
	 * Add a new instance [i]
	 * @param i instance being added
	 */
	public void addInstance(InstanceModel i){
		((WireCreationModel)getModel()).addInstance(i);
	}
	
	/**
	 * Add a new instance [i]
	 * @param i instance being removed
	 */
	public void removeInstance(InstanceModel i){
		((WireCreationModel)getModel()).removeInstance(i);
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
		((WireCreationModel)getModel()).addWire(instanceOut, eventModel, instanceIn);
	}
	
	/**
	 * Get all defined instances
	 * @return
	 */
	public ArrayList<InstanceModel> getInstanceModels() {
		return ((WireCreationModel)getModel()).getInstanceModels();
	}
	
	/**
	 * Get all wires
	 * @return
	 */
	public ArrayList<WireModel> getWireModels() {
		return ((WireCreationModel)getModel()).getWireModels();
	}

	/**
	 * Remove wire from wireframe.
	 * @param instanceOut instance sending event
	 * @param eventModel event being send
	 * @param instanceIn instance  receiving event.
	 */
	public void removeWire(InstanceModel instanceModelOut,
			EventModel eventModel, InstanceModel instanceModelIn) {
		((WireCreationModel)getModel()).removeWire(instanceModelOut, eventModel, instanceModelIn);
		
	}

	/**
	 * Returns the currently existing events in the program.
	 * @return
	 */
	public ArrayList<EventModel> getExistingEvents() {
		return ((WireCreationModel)getModel()).getExistingEvents();
	}

}
