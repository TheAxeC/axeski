package ide.backend.exceptions;

/**
 * Thrown if the compiler cant compile a given program
 * @author axelfaes
 *
 */
public class CompileException extends Exception{
		
	private static final long serialVersionUID = 1L;

	public CompileException() {
		super("Block not found during compilation");
	}
}
