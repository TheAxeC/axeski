package ide.frontend.classes.views.functions;

import ide.backend.model.BlockModel;
import ide.backend.model.ConnectedBlocks;
import ide.backend.model.function.FunctionCallModel;
import ide.backend.model.function.FunctionModel;
import ide.backend.model.variables.AbstractRefVariabelModel;
import ide.backend.model.variables.TypeModel;
import ide.backend.model.variables.VariableModel;
import ide.backend.variables.Variable.VariableType;
import ide.frontend.classes.views.AbstractBlockController;
import ide.frontend.classes.views.BlockView;

import java.util.HashMap;
import java.util.Observable;

/**
 * Controller for the function View
 * @author axel
 *
 */
public class FunctionController extends AbstractBlockController {
	
	private static HashMap<String, VariableType> _toTypes;
	private static HashMap< VariableType, String> _fromTypes;
	
	private HashMap<String, VariableModel> params;
	
	public FunctionController(Observable model) {
		super(model);
		
		params = new HashMap<>();
		if(_toTypes == null){
			_toTypes = new HashMap<String, VariableType>();
			_toTypes.put("TypeNumber", VariableType.NUMBER);
			_toTypes.put("TypeBoolean", VariableType.BOOLEAN);
			_toTypes.put("TypeString", VariableType.STRING);
		}
		if(_fromTypes == null){
			_fromTypes = new HashMap<VariableType, String>();
			_fromTypes.put(null, "TypeNull");
			_fromTypes.put(VariableType.NUMBER, "TypeNumber");
			_fromTypes.put(VariableType.BOOLEAN, "TypeBoolean");
			_fromTypes.put(VariableType.STRING, "TypeString");
		}
	}
	
	/**
	 * Changes the name of the handler to the given name.
	 * @param name name of the function
	 */
	public void changeName(String name){
		((FunctionModel)getModel()).changeName(name);
	}
	
	/**
	 * Add a block to the body
	 * @param m block to add
	 */
	public void addBlock(BlockView m){
		((FunctionModel)getModel()).addBlock((ConnectedBlocks)m.getModel());
	}

	/**
	 * Reset the body of the function
	 * @return the resetted body
	 */
	public ConnectedBlocks resetBody(){
		return ((FunctionModel)getModel()).resetBody();
	}

	/**
	 * Get the body of the function
	 * @return the body
	 */
	public Observable getBodymodel() {
		return ((FunctionModel)getModel()).getConnectedBlocks();
	}

	/**
	 * Get the name of the function
	 * @return the name
	 */
	public String getName() {
		return ((FunctionModel)getModel()).getName();
	}
	
	/**
	 * Set the return type of the function
	 * @param type
	 */
	public void setReturn(String type) {
		if (!type.equals("TypeNull"))
			((FunctionModel)getModel()).addReturn(new TypeModel(null, _toTypes.get(type)));
	}
	
	/**
	 * Get the return type of the function
	 * @return type of the return
	 */
	public String getReturn() {
		if (((FunctionModel)getModel()).getAmountofReturns() > 0)
			return _fromTypes.get(((FunctionModel)getModel()).getReturnType(0));
		else
			return _fromTypes.get(null);
	}

	/**
	 * Make a reference
	 * @return the reference
	 */
	public FunctionCallModel makeRef() {
		return new FunctionCallModel(null, (FunctionModel) getModel());
	}
	
	/**
	 * Add a parameter to the function
	 * @param block, the parameter
	 */
	public void addParam(String name, VariableModel block) {
		block.setName(name);
		block.changeParent((BlockModel) getModel());
		((FunctionModel)getModel()).addParam(block);
		((FunctionModel)getModel()).addVariable(name);
		params.put(name, block);
	}
	
	/**
	 * Add a parameter that is loaded by a file
	 * @param name the name
	 * @param block the model to add
	 */
	public void addParamLoad(String name, VariableModel block) {
		params.put(name, block);
	}
 	
	/**
	 * make a reference to a parameter
	 * @return  a new reference to the parameter
	 */
	public AbstractRefVariabelModel makeRefParam(String name) {
		return params.get(name).makeReference((BlockModel) getModel());
	}
	
	/**
	 * Get the type in string form
	 * @param type type enum
	 * @return the string version of the type
	 */
	public String getType(VariableType type) {
		return _fromTypes.get(type);
	}
}

