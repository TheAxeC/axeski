package ide.backend.blocks.other;

import ide.backend.blocks.Block;
import ide.backend.blocks.variablesAndTypes.ValueBlock;
import ide.backend.core.Proces;
import ide.backend.exceptions.BreakException;
import ide.backend.exceptions.FunctionNotFoundException;
import ide.backend.exceptions.LockedException;
import ide.backend.exceptions.TypeException;
import ide.backend.exceptions.VariableNotFoundException;
import ide.backend.variables.NumberVariable;
import ide.backend.variables.Variable;

/**
 * The sleep block
 * Lets this proces sleep for the given amount of ms given by its inner block
 * @author axel
 *
 */
public class SleepBlock implements Block {

	/** name of the variable */
	private Block _toSleep;
	
	/***
	 * Creates a new sleepblock. 
	 * Which execution will sleep the amout of ms given by its inner block.
	 * @param var the inner variable.
	 */
	public SleepBlock(Block var) {
		_toSleep = var;
	}
	
	@Override
	public Variable execute(Proces p) throws TypeException, BreakException, LockedException,VariableNotFoundException, FunctionNotFoundException {
		Variable var = _toSleep.execute(p);
		double sleep = NumberVariable.parseVariable(var);
		
		// Sleep for the indicate number
		if (sleep > 0)
			p.pushBlock(new SleepBlock(new ValueBlock(Double.toString(sleep-1))));
		
		return null;
	}
}
