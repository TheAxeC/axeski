package ide.backend.model;

import java.awt.Point;
import java.util.Observable;

import ide.backend.exceptions.CompileException;
import ide.backend.runtime.Compiler;
import ide.backend.save.DataSaver;
import ide.backend.variables.Variable.VariableType;

/**
 * This abstract Class is the base class for all usable blockmodels in the IDE. It contains all common properties.
 * The position of block in the IDE. DEFAULT: (0,0)
 * If the code of the block is run boolean [_debug] DEFAULT: false.
 * @author matthijs
 *
 */
public abstract class BlockModel extends Observable {
	
	/*FIELDS*/
	protected boolean _debug;
	protected Point _position;
	private BlockModel _parent;
	
	private boolean _break;
	
	/**
	 * Constructor for the blockmodel
	 * @param parent, the parent model or null if there is not parent
	 */
	public BlockModel(BlockModel parent) {
		_debug = false;
		_break = false;
		_position = new Point();
		_position.setLocation(0, 0);
		setParentModel(parent);
	}
	
	/** 
	 * Set the debug mode on or off
	 * @param cond, the condition to set
	 */
	public void setDebug(boolean cond) {
		_debug = cond;
		updateView();
	}
	
	/**
	 * [brk] decides whether this model contains a breakpoint
	 * @param brk, the beak status
	 */
	public void setBreak(boolean brk) {
		_break = brk;
		updateView();
	}
	
	/**
	 * Checks if this model contains a breakpoint
	 * @return check if this model contains a breakpoint
	 */
	public boolean isBreak() {
		return _break;
	}
	
	/**
	 * Checks if this model is in debug mode
	 * @return check if this model in in debug mode
	 */
	public boolean isDebug() {
		return _debug;
	}
	
	/**
	 * Returns the position of the block.
	 * @return the position of the block
	 */
	public Point getPos(){
		return _position;
	}
	
	/**
	 * Sets the position of the block to the given position [pos].
	 * @param pos, the new position, not null
	 */
	public void setPos(Point pos){
		_position = pos;
	}
	
	/**
	 * Updates the views of the model.
	 */
	protected void updateView() {
		this.setChanged();
		notifyObservers();
	}
	
	/**
	 * Sets the parent of the model to the given model [p].
	 * @param p, the parent model
	 */
	protected void setParent(BlockModel p){
		setParentModel(p);
	}
	
	/**
	 * Returns the parent of the model.
	 * @return the parent model or null if there is no parent
	 */
	protected BlockModel getParent(){
		return getParentModel();
	}
	
	
	/**
	 * This function makes sure the parent of a model is set right when the model is added to another model.
	 * @param p, the new parent
	 */
	public abstract void changeParent(BlockModel p);
	
	/**
	 * Cleares the debug flags.
	 */
	public void clearDebugFlags() {
		_debug = false;
		updateView();
	}
	
	/**
	 * Compile the model
	 * @param c, the compiler to use
	 * @return the compiled block
	 * @throws CompileException, thrown if the compiler has failed
	 */
	public abstract <T> T compile(Compiler<T> c) throws CompileException;
	
	/**
	 * Save the model
	 * @param s the data saver to use
	 * @return the saved data
	 */
	public abstract <T> T save(DataSaver<T> s);

	/**
	 * @return the parent
	 */
	public BlockModel getParentModel() {
		return _parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParentModel(BlockModel _parent) {
		this._parent = _parent;
	}
	
	/**
	 * If the parent of a block that its changed so that typechecking or 
	 * other error detection can be preformed.
	 * This is an upwards chain in the tree if models. If this function is not 
	 * relevant the block can stop the chain by not calling his parent.
	 * @param child child calling this function
	 * @param type type of variable of the child.
	 */
	public abstract void tellParentChanged(BlockModel child, VariableType type);
	
}
