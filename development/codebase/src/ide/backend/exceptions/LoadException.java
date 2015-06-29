package ide.backend.exceptions;

/**
 * Throw if a file cannot be loaded. 
 * @author axel
 *
 */
public class LoadException extends Exception{
	
	private static final long serialVersionUID = 1L;

	public LoadException(String name) {
		super("File: "+ name + " could not be loaded.");
	}
}
