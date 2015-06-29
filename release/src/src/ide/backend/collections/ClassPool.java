package ide.backend.collections;

import java.util.HashMap;

import ide.backend.core.ClassContainer;


/**
 * This class contains all Classes that are created in the program in the IDE. The class stores them in a hashmap so they can be accessed
 * via there TYPE [name].
 * If a class is added with a name (type) that already exists, it will be overwritten.
 * @author matthijs
 *
 */
public class ClassPool {
	/*FIELDS*/
	private HashMap<String, ClassContainer> _classes;
	
	/**
	 * Creates a new empty classpool.
	 */
	public ClassPool() {
		_classes = new HashMap<String, ClassContainer>();
		
	}
	
	/**
	 * Add a class to the pool with the given name
	 * @param name name of the class
	 * @param c container of the class
	 */
	public void addClass(String name, ClassContainer c){
		_classes.put(name, c);
	}
	
	/**
	 * Add a class to the pool with the given name
	 * @param name name of the class
	 * @param costume costume of the class
	 */
	public void addClass(String name, String costume){
		_classes.put(name, new ClassContainer(name, costume));
	}
	
	/**
	 * remove an classcontainer from the pool with the given name [name].
	 * @param name name of the class
	 */
	public void removeClass(String name){
		_classes.remove(name);
	}
	
	
	/**
	 * Return the classContainer with the given name [name].
	 * @param name name of the class
	 * @return the class container or null if not found
	 */
	public ClassContainer getClass(String name){
		return _classes.get(name);
	}
}
