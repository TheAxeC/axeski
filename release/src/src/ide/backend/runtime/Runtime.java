package ide.backend.runtime;

import java.util.Observable;

import ide.backend.exceptions.FunctionNotFoundException;

/**
 * The runtime class used by the IDE to run and compile programs
 * This is an abstract class. This makes it possible to switch the entire 
 * runtime/programming language without changing the IDE itself.
 * @author Axel
 */
public abstract class Runtime {
	
	protected Compiler<?> _compiler;
	protected boolean _running;
	protected boolean _isCompiled;
	protected boolean _isDebug;
	
	protected EventCatcher _catcher;
	
	/**
	 * Reset the runtime
	 */
	public abstract void reset();
	
	/**
	 * Compiles the current program
	 */
	public abstract boolean compile(ModelCollection collection);
	
	/**
	 * Compiles the current program in debug mode
	 */
	public abstract boolean compileDebug(ModelCollection collection);
	
	/**
	 * Runs the current program
	 * @throws FunctionNotFoundException 
	 */
	public abstract boolean run() throws FunctionNotFoundException;
	
	/**
	 * Takes one step through the program.
	 */
	public abstract boolean step() throws FunctionNotFoundException;
	
	/**
	 * Stops the executing.
	 */
	public abstract void stopRunning();
	
	/**
	 * Check if the VM is running.
	 * @return true if running else false.
	 */
	public abstract boolean isRunning();
	
	/**
	 * Check if the VM is in Debug Mode.
	 * @return true if in debug else false.
	 */
	public abstract boolean isDebugMode();
	
	/**
	 * Check if the VM is compiled.
	 * @return true if compiled else false.
	 */
	public abstract boolean isCompiled();
	
	/**
	 * Used to create the compiler for this runtime.
	 */
	public void setCompiler() { _compiler = null; }
	
	/**
	 * Prepare the VM to run.
	 */
	protected abstract void prepareRun();
	
	public void catchEvent(Observable ob) {
		ob.addObserver(_catcher);
	}
}
