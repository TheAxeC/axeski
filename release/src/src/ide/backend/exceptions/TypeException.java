package ide.backend.exceptions;
import ide.backend.variables.Variable.VariableType;
/**
 * This exception is thrown when using an operation on the wrong type of variable.
 * @author Matthijs Kaminski
 *
 */
public class TypeException extends Exception {
	
	/**
	 * To suppress a warning
	 */
	private static final long serialVersionUID = 4576338150862512610L;

	/**
	 * Contructor for the exception.
	 * This constructor takes a string [m] which holds a message, a VariableType [expected] which is the expected type and a 
	 * a VariableType [recieved] is the variable type recieved by the function.
	 * @param m message 
	 * @param expected expected type
	 * @param recieved received type
	 */
	public TypeException(String m, VariableType expected, VariableType recieved) {
		super(m + "Expected type:" + expected + " Recieved Type: " + recieved);
	}
	
	public TypeException(String m, VariableType expected) {
		super(m + "Expected type:" + expected );
	}
}
