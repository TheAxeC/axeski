package ide.backend.variables;

import ide.backend.variables.Variable.VariableType;

/**
 * Factory for creating new variables of given type.
 * @author matthijs
 *
 */
public final class VariableFactory {

	/**
	 * Creates a new variable of given type.
	 * @param type type of variable being created
	 * @return created variable
	 */
	public static Variable create(VariableType type) {
		switch (type) {
			case BOOLEAN:
				return new BooleanVariable(true);
			case NUMBER:
				return new NumberVariable(0);
			case STRING:
				return new StringVariable("");
			case EVENT:
				return new EventInstance(null);
			case VALUE:
			default:
				return new ValueVariable("");
		}
	}
	
	/**
	 * Returns the type being represented by a given string.
	 * @param type string representing type
	 * @return type if found.
	 */
	public static VariableType getType(String type) {
		type = type.toUpperCase();
		switch (type) {
			case "BOOLEAN":
				return VariableType.BOOLEAN;
			case "NUMBER":
				return VariableType.NUMBER;
			case "STRING":
				return VariableType.STRING;
			case "EVENT":
				return VariableType.EVENT;
			case "VALUE":
			default:
				return VariableType.VALUE;
		}
	}

}
