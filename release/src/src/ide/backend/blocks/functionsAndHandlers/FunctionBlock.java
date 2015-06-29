package ide.backend.blocks.functionsAndHandlers;



import java.util.ArrayList;

import ide.backend.blocks.Block;
import ide.backend.blocks.variablesAndTypes.VariableBlock;
import ide.backend.core.Proces;
import ide.backend.exceptions.BreakException;
import ide.backend.exceptions.FunctionNotFoundException;
import ide.backend.exceptions.LockedException;
import ide.backend.exceptions.TypeException;
import ide.backend.exceptions.VariableNotFoundException;
import ide.backend.variables.Variable;


/**
 * The Function block pushes its entire body onto the stack.
 * It will not push a new StackFrame since this has been done by the functionCall.
 * It doesn't need to do anything special for the return variables since this is done by the 
 * setReturnBlock in the functionCall and the returnBlock in its body.
 * It will push a PopBlock when the function is finished.
 * @author Matthijs
 */
public class FunctionBlock implements Block {

	/*FIELDS*/
	/**Parameters of the Function **/
	private ArrayList<VariableBlock> _params;
	/** Body of the Function */
	private ArrayList<Block> _body;

	public FunctionBlock(ArrayList<VariableBlock> params, ArrayList<Block> body) {
		_body = body;
		_params = params;
		
	}
	
	/**
	 * Return the parameterblocks. This function is used by the FunctionCallBlock.
	 * @return, parameterblock which are variableblocks to define new variables.
	 */
	public ArrayList<VariableBlock> getParams(){
		return _params;
	}
	

	@Override
	public Variable execute(Proces p) throws TypeException, BreakException, LockedException, VariableNotFoundException, FunctionNotFoundException {
		p.pushBlock(new PopBlock());
		for(int i=_body.size()-1; i >= 0; i--) {
			Block b = _body.get(i);
			p.pushBlock(b);
		}
		return null;
	}
		
}
