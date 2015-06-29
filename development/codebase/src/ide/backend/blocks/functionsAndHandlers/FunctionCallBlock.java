package ide.backend.blocks.functionsAndHandlers;

import ide.backend.blocks.Block;
import ide.backend.blocks.variablesAndTypes.SetReturnBlock;
import ide.backend.blocks.variablesAndTypes.VariableBlock;
import ide.backend.core.Proces;
import ide.backend.exceptions.BreakException;
import ide.backend.exceptions.FunctionNotFoundException;
import ide.backend.exceptions.LockedException;
import ide.backend.exceptions.TypeException;
import ide.backend.exceptions.VariableNotFoundException;
import ide.backend.variables.Variable;

import java.util.ArrayList;
/**
 * This Class represents a Block to execute a functionCall.
 * It creates a new StackFrame on which it creates the variables for the parameters of the function being called.
 * It sets the values of these variables equal to values 
 * @author Matthijs Kaminski
 *
 */
public class FunctionCallBlock implements Block {

	private ArrayList<String> _params;
	private ArrayList<String> _returns;
	private String _func;
	
	public FunctionCallBlock(ArrayList<String> params, ArrayList<String> returns, String func) {
		_params = params;
		_returns = returns;
		_func = func;
	}
	
	@Override
	public Variable execute(Proces p) throws TypeException, BreakException, LockedException, VariableNotFoundException, FunctionNotFoundException {
		//container for fetching value parameters from currentStackframe.
		ArrayList<Variable> valueparams = new ArrayList<Variable>();
		//retrieving called function.
		FunctionBlock funcBlock = (FunctionBlock) p.getInstance().getFunction(_func);
		//retrieving parameters of function.
		ArrayList<VariableBlock> paramsFromFunc = funcBlock.getParams();
		//fetching  values for valueparameters.
		for (String param : _params) {
			valueparams.add(p.getVar(param));
		}
		//pushing a new stackframe.
		p.pushStackFrame();
		//creating the parameter variables on the new stackframe.
		for (VariableBlock variableBlock : paramsFromFunc) {
			variableBlock.execute(p);
		}
		//Setting the content of parameters of fucntion to valueparameters of the functioncall.
		//NOTE: filled in order as appearing in function definition.
		for(int i = 0 ; i < _params.size(); i++){
			p.getVar(paramsFromFunc.get(i).getName()).setContent(valueparams.get(i));
		}
		

		//Push a SetReturnsBlock onto the stack.
		p.pushBlock(new SetReturnBlock(_returns));
		//Push the functionBlock onto the stack.
		p.pushBlock(funcBlock);

		
		return null;
	}
	
	
	
	
}
