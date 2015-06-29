package ide.backend.variables;

import ide.backend.exceptions.TypeException;

import java.util.HashMap;
import java.util.Map;

/**
 * Used for the implementation of arithmetic operators
 * @author Axel
 */
public interface ArithOperators {
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
	public static Map<String, ArithOperators> operators = new HashMap<String, ArithOperators>() {
		private static final long serialVersionUID = 1L;
		
		/**
		 * All the lambda function defined for the arithmetic operators
		 */
		private ArithOperators add = (a, b) -> a.add(b);
		private ArithOperators sub = (a, b) -> a.sub(b);
		private ArithOperators mul = (a, b) -> a.mul(b);
		private ArithOperators div = (a, b) -> a.div(b);
		private ArithOperators pow = (a, b) -> a.pow(b);
		private ArithOperators sqrt = (a, b) -> a.sqrt(b);
		private ArithOperators mod = (a, b) -> a.mod(b);
		
		/**
		 * Adding the operators to the hashmap
		 */
		{
            put("+", add);
            put("-", sub);
            put("*", mul);
            put("/", div);
            put("%", mod);
            put("^", pow);
            put("sqrt", sqrt);
        }
    };
}