package ide.backend.blocks.operator;

import ide.backend.variables.Variable.VariableType;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is used for typechecking operators.
 * @author matthijs
 *
 */
public class TypeChecking {
	/**
	 * FIELDS
	 */
	private static HashMap<String, ArrayList<VariableType>> _checklist;
	private static HashMap<String, VariableType> _returntypes;
	static {
		_checklist = new HashMap<String, ArrayList<VariableType>>();
		_returntypes = new HashMap<>();
		//equals
		ArrayList<VariableType> equals = new ArrayList<>();
		equals.add(VariableType.BOOLEAN);
		equals.add(VariableType.NUMBER);
		equals.add(VariableType.STRING);
		_checklist.put("==", equals);
		_returntypes.put("==", VariableType.BOOLEAN);

		//OR
		ArrayList<VariableType> or = new ArrayList<>();
		or.add(VariableType.BOOLEAN);
		or.add(VariableType.BOOLEAN);
		_checklist.put("||", or);
		_returntypes.put("||", VariableType.BOOLEAN);

		//&&
		ArrayList<VariableType> and = new ArrayList<>();
		and.add(VariableType.BOOLEAN);
		and.add(VariableType.VALUE);
		_checklist.put("&&", and);
		_returntypes.put("&&", VariableType.BOOLEAN);

		//!
		ArrayList<VariableType> not = new ArrayList<>();
		not.add(VariableType.BOOLEAN);
		not.add(VariableType.VALUE);
		_checklist.put("!", not);
		_returntypes.put("!", VariableType.BOOLEAN);
		
		
		//<
		ArrayList<VariableType> less = new ArrayList<>();
		less.add(VariableType.NUMBER);
		less.add(VariableType.STRING);
		less.add(VariableType.VALUE);
		_checklist.put("<", less);
		_returntypes.put("<", VariableType.BOOLEAN);
		
		//>
		_checklist.put(">", less);
		_returntypes.put(">", VariableType.BOOLEAN);
		
		//%
		ArrayList<VariableType> arith = new ArrayList<>();
		arith.add(VariableType.NUMBER);
		
		_checklist.put("%", arith);
		
		
		//sqrt
		_checklist.put("sqrt", arith);
		
		
		//*
		_checklist.put("*", arith);
	
		
		//-
		_checklist.put("-", arith);
		
		
		//^
		_checklist.put("^", arith);
		// /
		_checklist.put("/", arith);
		//+ 
		ArrayList<VariableType> plus = new ArrayList<>();
		plus.add(VariableType.NUMBER);
		plus.add(VariableType.STRING);
		plus.add(VariableType.VALUE);
		
		_checklist.put("+", plus);
		
	}
	
	/**
	 * Checks if the operation is allowed on given operands and what the return value is.
	 * @param op operator
	 * @param left type of left operand
	 * @param right type of right operand
	 * @return null if not possible, type of return if possible.
	 */
	public static VariableType typeCheck(String op, VariableType left, VariableType right){
		if(!_checklist.get(op).contains(left))
			return null;
		
		if(left != right && right != VariableType.VALUE && _returntypes.get(op) != right)
			return null;
			
			
		if(_returntypes.containsKey(op))
			return _returntypes.get(op);
		else{
			if(left == VariableType.VALUE)
				return VariableType.STRING;
			else
				return left;
		}
			
	}

}
