package ide.backend.exceptions;

/**
 * Execption being throw if variable is not found on current stack or current private variables
 * @author matthijs
 *
 */
public class VariableNotFoundException extends Exception {
	
	/**
	 * To suppress a warning
	 */
	private static final long serialVersionUID = 4576338150862512610L;

	/**
	 * Creates a new VariableNotFoundException exception
	 * @param name name of the variable not found.
	 */
	public VariableNotFoundException(String name) {
		super("Variable " + name + " is not found.");
	}
}
