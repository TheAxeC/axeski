package ide.backend.core;

import ide.backend.blocks.Block;
import ide.backend.blocks.debug.DebugBlock;
import ide.backend.collections.EventPool;
import ide.backend.exceptions.*;
import ide.backend.variables.*;
import ide.backend.variables.Variable.VariableType;

import java.util.Stack;

/**
 * Represents a proces for the VM to run. 
 * The VM will create a proces and run it for one basic step if the run function on the proces is called. 
 * By default the proces will have no lock on variables.
 * @author matthijs
 *
 */
public class Proces {
	/*FIELDS*/
	private Stack<Block> _code;
	private Stack<FunctionFrame> _stack;
	private Instance _instance;
	private ReturnVariables _returnValues;
	private EventPool _pool;
	private boolean _haslock;
	private boolean _break;
	
	/**
	 * Creates a new process for the given instance [instance] and executes the handler [Block] function to push all it's blocks on the stack.
	 * @param handler handler starting the process
	 * @param instance instance on which the process is executed
	 */
	public Proces(Block handler, Instance instance, EventPool pool) {
		//init stacks and return values
		initProces(instance, pool);
		try {
			handler.execute(this);
		} catch (TypeException | LockedException | VariableNotFoundException | FunctionNotFoundException e) {
			System.err.println("Unreachable code in Constructor Proces");
		} catch (BreakException e) {}
	}
	
	/**
	 * Get an event with name [name]
	 * @param name, name of the event
	 * @return  the event
	 */
	public Event getEvent(String name) {
		return _pool.getEvent(name);
	}
	
	/**
	 * Get an instance with name [name]
	 * @param name, name of the instance
	 * @return  the instance
	 */
	public Instance getInstance() {
		return _instance;
	}
	
	/**
	 * Check if this proces has a lock
	 * @return if this proces has a lock
	 */
	public boolean hasLock() {
		return _haslock;
	}
	
	/**
	 * Set/Unset a lock on this proces
	 * @param set value of the lock to be set.
	 */
	public void setLock(boolean set) {
		_haslock = set;
	}
	
	/**
	 * Initialize all the stacks and container datastructures for a process.
	 * @param instance
	 */
	private void initProces(Instance instance, EventPool pool){
		_pool = pool;
		_code = new Stack<Block>();
		_stack = new Stack<FunctionFrame>();
		_instance = instance;
		_returnValues = new ReturnVariables();
		_haslock = false;
		_break = false;
	}
	
	/**
	 * Pushes a new FunctionFrame on the stack.
	 */
	public void pushStackFrame(){
		_stack.push(new FunctionFrame());
	}
	
	/**
	 * Removes the top functionframe from the stack.
	 */
	public void popStackFrame(){
		_stack.pop();
	}
	
	/**
	 * This functions pushes a new variable from the given type with the given name on the stack.
	 * Note: if a variable of the given name already exists it's overwritten by the functionframe.
	 * @param name
	 * @param type
	 */
	public void pushVar(String name, VariableType type){
		_stack.peek().pushVar(name, type);
	}
	
	/**
	 * Gets a variable of the given name [name] form the top fucntionframe, if the variable is not found there, it will be searched in the member variables of the 
	 * instance. If so the variable can be locked and an exception will be thrown. If the variable is not found in the members of the instance
	 * a vaiablenotfound exception will be thrown.
	 * @param name
	 * @return
	 * @throws VariableNotFoundException
	 * @throws LockedException
	 */
	public Variable getVar(String name) throws VariableNotFoundException, LockedException{
		
		try {
			return _stack.peek().getVar(name);
		} catch (VariableNotFoundException e) {
			return _instance.getVar(name);
		}		
		
	}
	
	/**
	 * This function pushes a given Block [block] on the stack of blocks.
	 * @param block block being pushed on to the stack.
	 */
	public void pushBlock(Block block){
		_code.push(block);
	}
	
	/**
	 * This function pops the top block of the stack of blocks.
	 * @return block being popped
	 */
	public Block popBlock(){
		return _code.pop();
	}
	
	/**
	 * This function pushes a variable on the return variables stack.
	 * @param var variable being pushed.
	 */
	public void pushReturn(Variable var){
		_returnValues.push(var);
	}
	
	/**
	 * This function pops and returns the top variables on the returnvariabales stack.
	 * @return returnvariabel being popped.
	 */
	public Variable popReturn(){
		return _returnValues.pop();
	}
	
	/**
	 * This function runs one primitive step of the process. Return a eventInstance if created by the execution of a Block.
	 * If the process is finished it will throw a ProcesFinishedException.
	 * If a block fails to lock or access a variable because of a lock. The block is pushed back on the stack as if nothing happened.
	 * 
	 * @return Null if no EventInstance is created. An EventInstance if created.
	 * @throws ProcesFinshedException If the proces is finished.
	 */
	public EventInstance run(int steps) throws ProcesFinshedException, BreakException {
		if( _code.isEmpty())
			throw new ProcesFinshedException();
		
		EventInstance ret = null;
		for(int i=0;i < steps; i++) {
			if (_code.isEmpty()) break;
			Block b = popBlock();
			if (_break) {
				if (b instanceof DebugBlock)
					((DebugBlock) b).setBreak(false);
			}
			try {
				EventInstance ev = (EventInstance) b.execute(this);
				if (ev != null)
					ret = ev;
			} catch (TypeException e) {
				System.err.println(e.getMessage());
				throw new ProcesFinshedException();
			} catch (LockedException e) {
				pushBlock(b);
			} catch (VariableNotFoundException e) {
				System.err.println(e.getMessage());
				throw new ProcesFinshedException();
			} catch (FunctionNotFoundException e) {
				System.err.println(e.getMessage());
				throw new ProcesFinshedException();
			} catch (BreakException e) {
				_break = true;
				pushBlock(b);
				throw e;
			}
			
			if (_break) {
				if (b instanceof DebugBlock)
					((DebugBlock) b).setBreak(true);
				_break = false;
			}
		}
		return ret;
	}
	
	/**
	 * Changes the appearance of the instance to the given costume.
	 * @param name name of the costume.
	 */
	public void changeInstanceAppearance(String name){
		_instance.changeAppearance(name);
	}

	/**

	 * Increments the x and y position of the instance of this proces with given x and y.
	 * @param x increment value for the x position
	 * @param y increment value for the y position
	 */
	public void moveInstance(double x, double y) {
		_instance.move(x, y);
		
	}

	/**
	 * Changes the visibility of the instance to the given value.
	 * @param visibility value of visibility to be set.
	 */
	public void setVisibilityInstance(boolean visibility) {
		_instance.setVisibility(visibility);
	}

	/**
	 * Lock the variable with the given name
	 * @param var variable being locked.
	 * @throws LockedException 
	 */
	public void lock(String var) throws LockedException {
		if (!_haslock && _instance.isBeingLocked()) {
			throw new LockedException(var);
		}
		else {
			_haslock = true;
			_instance.lockMember(var);
		}
			
	}
	
	/**
	 * Unlock the variable with the given name
	 * @param var variable being unlocked.
	 */
	public void unlock(String var) throws LockedException {
		if (_instance.unLockMember(var)) {
			_haslock = false;
		}
	}

	/**
	 * Clears the total stack of returns
	 */
	public void clearReturns() {
		_returnValues.clear();
		
	}

	/**
	 * Checks if return values are available
	 * @return amount of returns.
	 */
	public int hasReturns() {
		return _returnValues.hasReturns();
	}

}
