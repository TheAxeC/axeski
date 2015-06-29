package ide.backend.exceptions;

/**
 * Thrown if a function can not be found on runtime
 * @author axelfaes
 *
 */
public class FunctionNotFoundException extends Exception{
	
	private static final long serialVersionUID = 1L;

	public FunctionNotFoundException(String name) {
		super("Fucntion: "+ name + " not found.");
	}
}
