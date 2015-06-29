package ide.backend.model.function;

import ide.backend.exceptions.CompileException;
import ide.backend.model.BlockModel;
import ide.backend.model.ConnectedBlocks;
import ide.backend.model.variables.TypeModel;
import ide.backend.model.variables.VariableModel;
import ide.backend.runtime.Compiler;
import ide.backend.save.DataSaver;
import ide.backend.variables.Variable.VariableType;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class represents the model for the Function in the IDE.
 * A function has a body of Blocks, a list of parameters (VariableBlocks) and a list of return Types (TypeModels).
 * Note that the order of both parameters and Types is of importance for functionCalls.
 * @author matthijs
 *
 */
public class FunctionModel extends TopLevelModel{
	
	/*FIELDS*/
	private String _name;
	private ConnectedBlocks _body;
	private ArrayList<BlockModel> _params;
	private ArrayList<TypeModel> _returns;
	
	private HashMap<String, VariableModel> _paramModels;
	
	public FunctionModel() {
		super(null);
		initFunctieModel("");
	}
	
	/**
	 * Creates a new functionModel and the containers for the content of the function.
	 * By Default: name is the empty String.
	 */
	public FunctionModel(String name) {
		super(null);
		initFunctieModel(name);
	}

	private void initFunctieModel(String name) {
		_name = name;
		_body = new ConnectedBlocks(this);
		_params = new ArrayList<BlockModel>();
		_returns = new ArrayList<TypeModel>();
		_paramModels = new HashMap<>();
	}
	
	/**
	 * Changes the name of the function to given String [input].
	 * @param input new name of the function
	 */
	public void changeName(String input){
		_name = input;
		updateView();
	}

	/**
	 * Returns the name of the function.
	 * @return name of the function
	 */
	public String getName(){
		return _name;
	}

	/**
	 * This function adds the given Block [block] to the body of the function.
	 * @param block block to add
	 */
	public void addBlock(ConnectedBlocks block){
		_body.addConnectedBlock(block);
		updateView();
	}
	
	/**
	 * Removes a given connectedBlock [block] from the function body.
	 * 
	 * @param block the block to remove, also removes the blocks below this block
	 * @return the released connected block formed by the removed blocks
	 */
	public ConnectedBlocks removeBlock(BlockModel block){
		return _body.removeBlocks(block);
		
	}
	
	/**
	 * Returns the body of the function in form of a connectedBlock.
	 * Used to save this blockModel.
	 * @return the body
	 */
	public ConnectedBlocks getConnectedBlocks(){
		return _body;
	}
	
	/**
	 * Returns alls the BlockModels contained by the body of the function.
	 * @return list of the blocks in the body
	 */
	public ArrayList<BlockModel> getBody(){
		return _body.getBlocks();
	}
	
	/**
	 * Adds a given parameter [block] to the function.
	 * @param block model to add a parameter
	 */
	public void addParam(VariableModel block){
		addVariable(block.getName());
		_params.add(block);
		_paramModels.put(block.getName(), block);
		updateView();
	}
	
	/**
	 * Removes a given parameter [block] form the function.
	 * @param block block to remove as parameter
	 */
	public void RemoveParam(VariableModel block){
		_paramModels.remove(block.getName());
		removeVariable(block.getName());
		_params.remove(block);
		updateView();
	}
	
	/**
	 * Returns the type of the parameter on the given position [index] in the function definition.
	 * @param index index to access
	 * @return type
	 */
	public VariableType getParamType(int index){
		return ((VariableModel)_params.get(index)).getType();
	}
	
	/**
	 * Return the type of the return on the given position [index] in the function definition.
	 * @param index index to access
	 * @return type
	 */
	public VariableType getReturnType(int index){
		return _returns.get(index).getType();
	}
	
	/**
	 * Get list of the parameters
	 * @return list of parameters
	 */
	public ArrayList<BlockModel> getParams(){
		return _params;
	}
	
	/**
	 * Adds a return type [type] to the function
	 * @param type
	 */
	public void addReturn(TypeModel type){
		_returns.add(type);
		updateView();
	}
	
	/**
	 * removes a return type at a given position [i] in the list of return types from the function.
	 * @param i
	 */
	public void removeReturn(int i){
		_returns.remove(i);
		updateView();
		
	}
	
	@Override
	public void changeParent(BlockModel p) {
		setParent(p);
	}
	
	@Override
	public <T> T compile(Compiler<T> c) throws CompileException {
		return c.compileBlock(this);
	}
	
	@Override
	public <T> T save(DataSaver<T> s) {
		return s.saveBlock(this);
	}
	
	@Override
	public void clearDebugFlags() {
		_debug = false;
		_body.clearDebugFlags();
		updateView();
	}
	
	/**
	 * Reset the body of the function
	 * @return the resetted body
	 */
	public ConnectedBlocks resetBody() {
		return _body = new ConnectedBlocks(this);
	}
	

	@Override
	public void tellParentChanged(BlockModel child,VariableType type) {
		//DO NOTHING
	}

	/**
	 * Returns the amount of parameters of the function.
	 * @return amount of parameters
	 */
	public int getAmountofParams() {
		return _params.size();
	}

	/**
	 * returns the amount of returns
	 * @return amount 
	 */
	public int getAmountofReturns() {
		return _returns.size();
	}
	
	/**
	 * Get the parameter models
	 * @return the parameter models linked to their names
	 */
	public HashMap<String, VariableModel> getParamModels() {
		return _paramModels;
	}
}
