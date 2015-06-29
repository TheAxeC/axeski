package ide.backend.model.event;


import java.util.ArrayList;

import ide.backend.exceptions.CompileException;
import ide.backend.model.BlockModel;
import ide.backend.model.variables.VariableModel;
import ide.backend.runtime.Compiler;
import ide.backend.save.DataSaver;
import ide.backend.variables.Variable.VariableType;


/**
 * This Class represents a Event in the eventCreation view. 
 * It's contents must be queryable by its accessblocks.
 * @author matthijs
 *
 */
public class EventModel extends BlockModel {

	/* FIELDS */
	private String _type;
	private ArrayList<VariableModel> _members;
	
	/**
	 * Creates a new EventModel with as parent the EventCreationView.
	 * The name of the event is its type.
	 * @param parent parent of the model
	 * @param type type of the event
	 */
	public EventModel(BlockModel parent, String type) {
		super(parent);
		_type = type;
		_members = new ArrayList<VariableModel>();
	}
	
	/**
	 * Adds a member [m] to the eventModel
	 * @param m, the member to add.
	 */
	public void addMember(VariableModel m){
		if (m != null) {
			_members.add(m);
			updateView();
		}
	}
	
	/**
	 * Removes a member [m] from the event.
	 * @param m, the member to remove.
	 */
	public void removeMember(VariableModel m){
		_members.remove(m);
		updateView();
	}
	
	/**
	 * Change the type of the model
	 * @param type the new type
	 */
	public void changeType(String type){
		_type = type;
		updateView();
	}
	
	/**
	 * Return the name of the members off the event.
	 * @post the names are returned if any members exists.
	 * @return list of all member names
	 */
	public ArrayList<String> getMemberNames(){
		ArrayList<String> out = new ArrayList<String>();
		for (VariableModel variableModel : _members) {
			out.add(variableModel.getName());
		}
		return out;
	}
	
	/**
	 * Return the types of the members off the event.
	 * @post the types are returned if any members exists.
	 * @return list of all member types
	 */
	public ArrayList<VariableType> getMemberTypes(){
		ArrayList<VariableType> out = new ArrayList<VariableType>();
		for (VariableModel variableModel : _members) {
			out.add(variableModel.getType());
		}
		return out;
	}
	
	/**
	 * Get the model from the member with the given name
	 * @param name name of the member
	 * @return the model or null if not found
	 */
	public VariableModel getMember(String name) {
		for (VariableModel v : _members) {
			if (v.getName().equals(name))
				return v;
		}
		return null;
	}
	
	/**
	 * Returns the type of a specific member of the event [name].
	 * @pre the name must me in the event members.
	 * @post the type of the variable will be returned.
	 * @param name name of the member
	 * @return the type
	 */
	public VariableType getVarType(String name){
		for (VariableModel variableModel : _members) {
			if(variableModel.getName().equals(name))
				return variableModel.getType();
		}
		return null;
	}
	
	/**
	 * Returns the type of the Event.
	 * @return the type
	 */
	public String getType(){
		return _type;
	}
	
	/**
	 * Creates a new reference to this EventModel.
	 * It's notified if changes are made to this model.
	 * @return the reference
	 */
	public RefEventModel makeReference(BlockModel prnt){
		RefEventModel out = new RefEventModel(prnt, this);
		this.addObserver(out);
		return out;
	}
	
	@Override
	public void changeParent(BlockModel p) {
		setParent(p);
	}

	@Override
	public <T> T compile(Compiler<T> c) throws CompileException {
		return c.compileBlock(this);
	}
	
	@Override
	public <T> T save(DataSaver<T> s) {
		return s.saveBlock(this);
	}

	/**
	 * Adds a new member (variable) to the event.
	 * @param name name of the new member.
	 * @return the new member model
	 */
	public VariableModel addMember(String name) {
		VariableModel m = new VariableModel(this);
		m.setName(name);
		this.addMember(m);
		return m;
	}
	

	@Override
	public void tellParentChanged(BlockModel child,VariableType type) {
		//DO NOTHING
	}

	/**
	 * returns all the members (variablemodels) of the event.
	 * @return list of all members models
	 */
	public ArrayList<VariableModel> getMembers() {
		return _members;
	}
}
