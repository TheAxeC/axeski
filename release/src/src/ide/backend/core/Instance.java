package ide.backend.core;

import ide.backend.blocks.Block;
import ide.backend.exceptions.*;
import ide.backend.model.classes.Costume;
import ide.backend.model.classes.InstanceModel;
import ide.backend.variables.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Observable;


/**
 * An instance of a classcontainer
 * @author Matthijs && Axel
 */
public class Instance extends Observable {
	
	/*Fields*/
	private HashMap<String, Variable> _members;
	private HashSet<String> _lockedMembers;
	private boolean _visible;
	private ClassContainer _metatable;
	private String _name;
	
	private int _x, _y;
	
	/**
	 * Creates a new instance of a classContainer
	 * @param container container of which the created instance is an instance.
	 * @param name name of the instance
	 * @param model model of the instance to interact with.
	 */
	public Instance(ClassContainer container, String name, InstanceModel model) {
		InitInstance(container, name, model);
	}
	
	/**
	 * Get the metatable of this instance
	 * @return the metatable
	 */
	public ClassContainer getMetatable() {
		return _metatable;
	}
	
	/**
	 * Check if this instance is visible
	 * @return the visibility
	 */
	public boolean isVisible() {
		return _visible;
	}
	
	/**
	 * Set the visibility of this instance
	 * @param visible state of visibility being set.
	 */
	public void setVisibility(boolean visible) {
		_visible = visible;
		this.setChanged();
		this.notifyObservers(new Boolean(_visible));
	}
	
	/**
	 * Set a variable with a certain name
	 * @param key, the name of the variable
	 * @param val, the variable
	 */
	public void setVar(String key, Variable val) {
		_members.put(key, val);
	}
	
	/**
	 * Get a certain variable from the instance
	 * @param name, the name of the variable
	 * @return the variable with the given name
	 * @throws VariableNotFoundException, thrown if the variable doesnt exist
	 */
	public Variable getVar(String name) throws LockedException, VariableNotFoundException{
		if(_lockedMembers.contains(name))
			throw new LockedException(name);
		
		Variable out = _members.get(name);
		if(out == null)
			throw new VariableNotFoundException(name);
		return out;
	}
	
	/**
	 * Get the function from this class with name [name]
	 * @param name, name of the class
	 * @return the function block
	 * @throws FunctionNotFoundException thrown if the function doesnt exist
	 */
	public Block getFunction(String name) throws FunctionNotFoundException{
		return _metatable.getFunction(name);
	}
	
	/**
	 * Get the handlers from this class bound to event [ev]
	 * @param ev, the event
	 * @return the list of handlers bound to [ev]
	 * @throws FunctionNotFoundException, thrown if no handlers could be found
	 */
	public ArrayList<Block> getHandlers(Event ev) throws FunctionNotFoundException {
		return _metatable.getHandlers(ev);
	}	
	
	/**
	 * Initialises the instance
	 * @param metatable
	 */
	private void InitInstance(ClassContainer metatable, String name, InstanceModel model){
		_name = name;
		_members = new HashMap<String, Variable>();
		_lockedMembers = new HashSet<String>();
		_visible = true;
		_metatable = metatable;
		this.addObserver(model);
		_x = 0;
		_y = 0;
	}

	/**
	 * Returns the name of the instance.
	 * @return name of the instance.
	 */
	public String getName() {
		return _name;
	}
	
	/**
	 * Changes the appearance of an instance to the given instance if the costume exists.
	 * @param name name of appearance to be set. 
	 */
	public void changeAppearance(String name){
		Costume c;
		if(( c = _metatable.getCostume(name)) != null){
			this.setChanged();
			this.notifyObservers(c);
		}
	}
	
	/**
	 * Checks if this instance is being locked
	 * @return
	 */
	public boolean isBeingLocked() {
		return _lockedMembers.size() > 0;
	}

	/**
	 * Increments the x and y position of the instance view with given x and y.
	 * @param x x incrementation.
	 * @param y y incrementation.
	 */
	public void move(double x, double y) {
		double[] point = new double[2];
		point[0] = x;
		point[1] = y;
		_x -= x;
		_y -= y;
		this.setChanged();
		this.notifyObservers(point);
		
	}


	/**
	 * Lock the member variable with name [name]
	 * @param name name of member to be locked.
	 */
	public void lockMember(String name) {
		_lockedMembers.add(name);
	}
	
	/**
	 * UnLock the member variable with name [name]
	 * @param name name the member to be unlocked.
	 * @return if there are still locked members.
	 */
	public boolean unLockMember(String name) {
		_lockedMembers.remove(name);
		return _lockedMembers.size() == 0;
	}

	/**
	 * Reset the position of all instances 
	 */
	public void resetPositions() {
		move(_x, _y);
		changeAppearance(_metatable.getMainCostume());
		setVisibility(true);
		
		for(HashMap.Entry<String, Variable> entry: _members.entrySet()) {
			entry.getValue().reset();
		}
	}

}
