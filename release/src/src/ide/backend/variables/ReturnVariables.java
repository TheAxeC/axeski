package ide.backend.variables;

import java.util.Stack;


/**
 * This class is used to represent a stack of return variables.
 * The order is import because variables will be set to return values in order as they appear on the stack.
 * @author matthijs
 *
 */
public class ReturnVariables  {
	
	/*FIELDS*/
	private Stack<Variable> _returns;
	
	
	/**
	 * Constructs a new stack for the return variables.
	 */
	public ReturnVariables() {
		_returns = new Stack<Variable>();
	}
	
	/**
	 * Pushes a variable [var] on the stack.
	 * @param var variable being pushed onto the stack.
	 */
	public void push(Variable var){
		_returns.push(var);
	}
	
	/**
	 * pops the top variable from the stack.
	 * @return,  the top element.
	 */
	public Variable pop(){
		 return _returns.pop();
	}
	
	/**
	 * Checks whether the stack is empty.
	 * @return true if empty
	 */
	public boolean isEmpty(){
		return _returns.isEmpty();
	}
	
	/**
	 * Clears the total stack.
	 */
	public void clear(){
		_returns.clear();
	}

	/**
	 * Checks if return values are available
	 * @return amount of returns.
	 */
	public int hasReturns() {
		return _returns.size();
	}

}
