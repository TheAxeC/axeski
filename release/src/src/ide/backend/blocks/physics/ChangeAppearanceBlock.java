package ide.backend.blocks.physics;

import ide.backend.blocks.Block;
import ide.backend.core.Proces;
import ide.backend.exceptions.BreakException;
import ide.backend.exceptions.FunctionNotFoundException;
import ide.backend.exceptions.LockedException;
import ide.backend.exceptions.TypeException;
import ide.backend.exceptions.VariableNotFoundException;
import ide.backend.variables.Variable;


/**
 * This Block changes the appearance of the instance to the value of a variable produced by its innerblock converted to a string.
 * This is done by converting the variable returned by the innerblock to a StringVariable which has an implemented toString function.
 * @author matthijs
 *
 */
public class ChangeAppearanceBlock implements Block {

	/*FIELDS*/
	private Block _innerBlock;
	
	public ChangeAppearanceBlock(Block innerBlock) {
		_innerBlock = innerBlock;
	}
	
	@Override
	public Variable execute(Proces p) throws TypeException, BreakException, LockedException,VariableNotFoundException, FunctionNotFoundException {
		p.changeInstanceAppearance(_innerBlock.execute(p).toStringVariable().toString());
		return null;
	}
}
