package ide.backend.exceptions;

/**
 * Thrown if a file can not be loaded into Java
 * @author axelfaes
 *
 */
public class FileException extends Exception{
	
	private static final long serialVersionUID = 1L;

	public FileException(String name) {
		super(name);
	}
}
