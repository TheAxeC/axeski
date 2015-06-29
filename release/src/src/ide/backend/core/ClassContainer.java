package ide.backend.core;

import ide.backend.blocks.Block;
import ide.backend.exceptions.FunctionNotFoundException;
import ide.backend.model.classes.Costume;
import ide.backend.model.classes.InstanceModel;
import ide.backend.variables.VariableFactory;
import ide.backend.variables.Variable.VariableType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Represents a class made in the IDE programming language
 * @author Axel
 */
public class ClassContainer {
	
	/** Holds all variabletypes contained as members within the class */
	private HashMap<String, VariableType> _members;
	
	/** Holds all functions contained within the class */
	private HashMap<String, Block> _functions;
	
	/** Holds all variables contained within the class */
	private HashMap<Event, ArrayList<Block>> _handlers;
	
	/** Holds all variables contained within the class */
	private HashMap<String, Costume> _costumes;
	
	private String _mainCostume;
	
	/** Unique name of this class */
	private String _name;

	/**
	 * 
	 * @param name name of the class
	 * @param mainCostume, name of the primary costume of the class
	 */
	public ClassContainer(String name, String mainCostume) {
		_name = name;
		_handlers = new HashMap<Event, ArrayList<Block>>();
		_functions = new HashMap<String, Block>();
		_members = new HashMap<String, VariableType>();
		_costumes = new HashMap<String, Costume>();
		
		_mainCostume = mainCostume;
	}
	
	/**
	 * Getter for the name of the classcontainer
	 * @return the name
	 */
	public String getName() {
		return _name;
	}
	
	/**
	 * Get the function from this class with name [name]
	 * @param name, name of the class
	 * @return the function block
	 * @throws FunctionNotFoundException thrown if the function doesnt exist
	 */
	public Block getFunction(String name) throws FunctionNotFoundException {
		Block b = (Block) _functions.get(name);
		if (b == null)
			throw new FunctionNotFoundException(name);
		return b;
	}
	
	/**
	 * Get the handlers from this class bound to event [ev]
	 * @param ev, the event
	 * @return the list of handlers bound to [ev]
	 * @throws FunctionNotFoundException, thrown if no handlers could be found
	 */
	public ArrayList<Block> getHandlers(Event ev) throws FunctionNotFoundException {
		ArrayList<Block> b = (ArrayList<Block>) _handlers.get(ev);
		if (b == null)
			throw new FunctionNotFoundException(ev.getType());
		return b;
	}
	
	/**
	 * Creates a new instance
	 * @return the newly created instance
	 */
	public Instance makeInstance(String name, InstanceModel model) {
		Instance inst = new Instance(this, name, model);
		
		for(Entry<String, VariableType> entry : _members.entrySet()) {
		    String key = entry.getKey();
		    VariableType type = entry.getValue();

		    inst.setVar(key,  VariableFactory.create(type));
		}
		
		return inst;
	}
	
	/**
	 * Add a member to the class
	 * @param name name of the variable to add
	 * @param type type of the variable to add
	 */
	public void addMember(String name, VariableType type) {
		_members.put(name, type);
	}
	
	/**
	 * Add a handler to the class
	 * @param ev event of the handler
	 * @param b block that represents the handler
	 */
	public void addHandler(Event ev, Block b) {
		ArrayList<Block> blocks = (ArrayList<Block>) _handlers.get(ev);
		if (blocks == null) {
			ArrayList<Block> list = new ArrayList<>();
			list.add(b);
			_handlers.put(ev, list);
		}
		else {
			blocks.add(b);
		}
	}
	
	/**
	 * Add a function to the class
	 * @param name name of the function
	 * @param b the function block
	 */
	public void addFunction(String name, Block b) {
		_functions.put(name, b);
	}

	/**
	 * Adds a costume to the class container
	 * @param name name of the costume
	 * @param costume costume
	 */
	public void addCostume(String name, Costume costume) {
		_costumes.put(name, costume);
		
	}
	
	/**
	 * Returns the costume to which the given name refers.
	 * @param name Name of the costume to be returned.
	 * @return Costume or null if not found.
	 */
	public Costume getCostume(String name){
		return _costumes.get(name);
	}
	
	/**
	 * Get the main costume name of this class
	 * @return the main costume
	 */
	public String getMainCostume() {
		return _mainCostume;
	}

}
