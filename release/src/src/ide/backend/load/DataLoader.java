package ide.backend.load;

import ide.backend.exceptions.FileException;
import ide.backend.exceptions.LoadException;
import ide.backend.runtime.ModelCollection;

/**
 * Inteface used to load a program into the IDE
 * The Data used is up to the implementing class
 * @author Axel Faes
 *
 */
public abstract class DataLoader {
	
	/**
	 * Loads a program from a given file.
	 * @param collection ModelCollection being loaded to.
	 * @param fileName File being loaded from
	 * @throws LoadException exception being throw if error occurs.
	 * @throws FileException exeception being throw if error occrrs.
	 */
	public abstract void load(ModelCollection collection, String fileName) throws LoadException, FileException;

	/**
	 * Load the program from a string
	 * @param collection the program that will become the loaded program
	 * @param content the saved data that represents a program
	 * @throws LoadException error with the loading of the program
	 * @throws FileException error with the file
	 */
	public abstract void loadString(ModelCollection collection, String content) throws LoadException, FileException;	
	
}
