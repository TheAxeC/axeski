package ide.backend.exceptions;

/**
 * Exception thrown if attempting to lock a variable that is already locked.
 * @author axel
 *
 */
public class LockedException extends Exception {
	
	/**
	 * To suppress a warning
	 */
	private static final long serialVersionUID = 4576338150862512610L;
	
	public LockedException(String name) {
		super("Variable " + name + " is locked.");
	}
}
