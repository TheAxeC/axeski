package ide.backend.model.classes;

import java.util.ArrayList;
import java.util.HashMap;

import ide.backend.exceptions.CompileException;
import ide.backend.model.BlockModel;
import ide.backend.model.event.EventModel;
import ide.backend.model.function.FunctionModel;
import ide.backend.model.function.HandlerModel;
import ide.backend.model.variables.MemberModel;
import ide.backend.runtime.Compiler;
import ide.backend.save.DataSaver;
import ide.backend.variables.Variable;
import ide.backend.variables.Variable.VariableType;


/**
 * This Model represent a Class in the IDE. 
 * It contains a lot of containers to store the different objects a Class holds.
 * It must be created with a name. 
 * @author Matthijs Kaminski
 *
 */
public class ClassModel extends BlockModel{

	/*FIELDS*/
	private String _name;
	private ArrayList<FunctionModel> _functions;
	private HashMap<EventModel, ArrayList<HandlerModel> > _handelers;
	private ArrayList<EventModel> _inputEvents;
	private HashMap<String, MemberModel> _members;
	private ArrayList<BlockModel> _floatingBlocks;
	
	private HashMap<EventModel, Integer> _outputEvents;
	//costumes of the class
	private HashMap<String, Costume> _costumes;
	//primary Costume of the class;
	String _primaryCostume;
	
	/**
	 * Creates a new ClassModel with a given name.
	 * And Creates all necessary containers.
	 * @param name name of the class
	 */
	public ClassModel(String name) {
		super(null);
		initClassModel(name);
	}
	
	private void initClassModel(String name){
		_name = name;
		_functions = new ArrayList<FunctionModel>();
		_handelers = new HashMap<EventModel, ArrayList<HandlerModel>>();
		_inputEvents = new ArrayList<EventModel>();
		_outputEvents = new HashMap<>(); //new ArrayList<EventModel>();
		_members = new HashMap<String, MemberModel>();
		_floatingBlocks = new ArrayList<BlockModel>();
		_handelers.put(null, new ArrayList<HandlerModel>());
		_costumes = new HashMap<String, Costume>();
		_primaryCostume = null;
		
	}
	
	/**
	 * Changes the name of the Class
	 * @param name the new name of the class
	 */
	public void changeName(String name){
		_name = name;
		this.setChanged();
		notifyObservers("changeName");
	}
	
	/**
	 * Returns the name of the Class.
	 * @return name of the class
	 */
	public String getName(){
		return _name;
	}
	
	/**
	 * Adds a function [func] to the collection of functions of the Class.
	 * @param func the function to add
	 */
	public void addFunction(FunctionModel func){
		_functions.add(func);
		
	}
	
	/**
	 * Removes a given function [func] from the collection of functions.
	 * @param func the function to remove
	 */
	public void removeFunction(FunctionModel func){
		_functions.remove(func);
		
	}
	
	/**
	 * Returns the collection of functionModels.
	 * @return list of all function
	 */
	public ArrayList<FunctionModel> getFunctions(){
		return _functions;
	}
	
	/**
	 * Adds a handler [hand] to the collection of handlers of the Class.
	 * A handler is stored by the event [event] it handles.
	 * @param event the event that the handler responds to
	 * @param hand the handler model
	 */
	public void addHandler(EventModel event, HandlerModel hand){
		_handelers.get(event).add(hand);
		
	}
	
	/**
	 * Removes a handler from the collection of handlers of the class.
	 * @param event the eventmodel the handler responds to
	 * @param hand the handler model to remove
	 */
	public void removeHandler(EventModel event, HandlerModel hand){
		_handelers.get(event).remove(hand);
		
	}
	
	/**
	 * Returns the Handlers and their events of the Class.
	 * @return the Handlers and their events of the Class.
	 */
	public HashMap<EventModel, ArrayList<HandlerModel>>  getHandlers(){
		return _handelers;
	}
	
	/**
	 * Change the event a handler belongs to
	 * @param newEvent the new event 
	 * @param hand the handler model
	 */
	public void changeHandlerEvent(EventModel newEvent, HandlerModel hand){
		if(hand.getEventRef() == null){
			hand.setEvent(newEvent.makeReference(hand));
			_handelers.get(null).remove(hand);
			addHandler(newEvent, hand);
		}
		else{
			_handelers.get(hand.getEventRef().getEventModel()).remove(hand);
			addHandler(newEvent, hand);
			if(newEvent == null)
				hand.setEvent(null);
			else
				hand.setEvent(newEvent.makeReference(hand));
		}
	}
	
	/**
	 * Adds an input event [event] to the collection of input events.
	 * @param event the eventmodel
	 */
	public void addInputEvent(EventModel event){
		_inputEvents.add(event);
		_handelers.put(event, new ArrayList<HandlerModel>());
		Object[] arg = new Object[2];
		arg[0] = new String("addInput");
		arg[1] = event;
		this.setChanged();
		notifyObservers(arg);
	}
	
	/**
	 * removes an input event [event] from the collection of input events.
	 * @param event the eventmodel to remove
	 */
	public void removeInputEvent(EventModel event){
		_inputEvents.remove(event);
		Object[] arg = new Object[2];
		arg[0] = new String("deleteInput");
		arg[1] = event;
		this.setChanged();
		notifyObservers(arg);
	}
	
	/**
	 * Returns all the inputEvents of the Class.
	 * @return list of all input events
	 */
	public ArrayList<EventModel> getInputEvents(){
		return _inputEvents;
	}
	
	/**
	 * Adds an output event [event] to the collection of output events.
	 * @param event eventmodel to add as output
	 */
	public void addOutputEvent(EventModel event) {
		if (_outputEvents.containsKey(event)) {
			_outputEvents.put(event, _outputEvents.get(event)+1);
			return;
		}
		_outputEvents.put(event, 1);
		Object[] arg = new Object[2];
		arg[0] = new String("addOutput");
		arg[1] = event;
		this.setChanged();
		notifyObservers(arg);
		
	}
	
	/**
	 * removes an output event [event] from the collection of output events.
	 * @param event model to remove
	 */
	public void removeOutputEvent(EventModel event) {
		Integer nb = _outputEvents.get(event);
		if (nb == null) return;
		
		if (nb <= 1) {
			_outputEvents.remove(event);
			Object[] arg = new Object[2];
			arg[0] = new String("deleteOutput");
			arg[1] = event;
			this.setChanged();
			notifyObservers(arg);
		}
		else
			_outputEvents.put(event, _outputEvents.get(event)-1);
	}
	
	/**
	 * Returns all the outputEvents of the Class.
	 * @return list of all output events
	 */
	public ArrayList<EventModel> getOutputEvents(){
		return new ArrayList<EventModel>(_outputEvents.keySet());
	}
	
	/**
	 * Adds a new member to the collection of members of the Class.
	 * @param name name of the member variable (needs to be unique)
	 * @param type the type of the variable
	 */
	public MemberModel addMember(String name, Variable.VariableType type){
		MemberModel m = new MemberModel(null, name, type);
		_members.put(name, m);
		return m;
		
	}
	
	/**
	 * Removes a member from the collection of members of the Class.
	 * @param name name of the member to remove
	 */
	public void removeMember(String name){
		_members.remove(name);
		
	}
	
	/**
	 * Returns a hashmap with of member variables mapt on their type.
	 * @return list of all members
	 */
	public HashMap<String, MemberModel> getMembers(){
		return _members;
	}
	
	/**
	 * Adds a new Floating block to the Class.
	 * @param block the floating block to add
	 */
	public void addFloatBlock(BlockModel block){
		_floatingBlocks.add(block);
		
	}
	
	/**
	 * Removes a floatingBlock from the Class.
	 * @param block the floating blokc to remove
	 */
	public void removeFloatBlock(BlockModel block){
		_floatingBlocks.remove(block);
		
	}
	
	/**
	 * return all floating blocks
	 * @return list of all floaters
	 */
	public ArrayList<BlockModel> getFloaters() {
		return _floatingBlocks;
	}
	
	/** Returns the current costumes of the class.
	 * @return list of all costumes
	 */
	public HashMap<String,Costume> getCostumes(){
		return _costumes;
	}
	
	/**
	 * Adds a costume to the the class.
	 * @param name name of the costume.
	 * @param costume costume to be added.
	 */
	public void addCostume(String name, Costume costume){
		_costumes.put(name, costume);
	}
	
	/**
	 * Removes costume with given name from the class.
	 * @param name Name of the costume to be removed.
	 */
	public void removeCostume(String name) {
		_costumes.remove(name);
		
	}
	
	/**
	 * Set the primary costume of the class to the costume with the given name.
	 * @param name Name of costume to be set primary.
	 */
	public void setSelectedCostume(String name){
		_primaryCostume = name;
		this.setChanged();
		notifyObservers(_primaryCostume);
	}
	
	/**
	 * Returns the primary costume of class.
	 * @return the selected costume
	 */
	public String getSelectedCostume(){
		return _primaryCostume;
	}
	
	/**
	 * Return the costume of the given name
	 * @param name name of the costume
	 * @return the costume
	 */
	public Costume getCostume(String name) {
		return _costumes.get(name);
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
	 * Clear all debug flags
	 * This function also clears the debug flags of the entire contents of the class
	 */
	public void clearDebugFlags() {
		for(FunctionModel m: _functions) {
			m.clearDebugFlags();
		}
		
		for (HashMap.Entry<EventModel, ArrayList<HandlerModel>> entry : _handelers.entrySet()) {
			if (entry.getKey() == null) continue;

			for (HandlerModel hand : entry.getValue()) {
				hand.clearDebugFlags();
			}
		}
	}

	/**
	 * Removes an output event defined by the given string
	 * @param event name of event to be removed.
	 */
	public void removeOutputEvent(String event) {
		EventModel o = null;
		for (EventModel e : _outputEvents.keySet()) {
			if(e.getType().equals(event)){
				o = e;
				break;
			}
		}
		if(o != null){
			removeOutputEvent(o);
		}
		
	}
	
	@Override
	public void tellParentChanged(BlockModel child,VariableType type) {
		//DO NOTHING
	}

	/**
	 * Check if a function with name [name] doesnt exist yet
	 * @param name
	 * @return
	 */
	public boolean functionNotExist(String name) {
		for(FunctionModel m: _functions) {
			if (m.getName().equals(name)) return false;
		}
		return true;
	}

	/**
	 * Get the member with the given name
	 * @param name member to look for
	 * @return the member or null if not found
	 */
	public MemberModel getMember(String name) {
		return _members.get(name);
	}



}
