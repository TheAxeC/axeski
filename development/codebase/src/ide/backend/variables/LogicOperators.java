package ide.backend.variables;

import ide.backend.exceptions.TypeException;

import java.util.HashMap;
import java.util.Map;

/**
 * Used for the implementation of logic operators
 * @author Axel
 */
public interface LogicOperators {
	/**
	 * The function used by the lambda functions
	 * @param left, the left parameter
	 * @param right, the right parameter
	 * @return the result
	 * @throws TypeException, exception on wrong type
	 */
	Variable operation(Variable left, Variable right) throws TypeException;
	
	/**
	 * Hashmap linking all operators to identifying strings
	 */
	public Map<String, LogicOperators> operators = new HashMap<String, LogicOperators>() {
		private static final long serialVersionUID = 1L;

		/**
		 * All the lambda function defined for the logic operators
		 */
		private LogicOperators lessThan = (a, b) -> a.lessThan(b);
		private LogicOperators greaterThan = (a, b) -> a.greaterThan(b);
		private LogicOperators equals = (a, b) -> a.equal(b);
		private LogicOperators or = (a, b) -> a.or(b);
		private LogicOperators not = (a, b) -> a.not(b);
		private LogicOperators and = (a, b) -> a.and(b);
		
		/**
		 * Adding the operators to the hashmap
		 */
		{
            put(">", greaterThan);
            put("<", lessThan);
            put("==", equals);
            put("!", not);
            put("||", or);
            put("&&", and);
        }
    };
}
