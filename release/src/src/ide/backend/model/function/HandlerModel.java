package ide.backend.model.function;

import java.util.ArrayList;

import ide.backend.exceptions.CompileException;
import ide.backend.model.BlockModel;
import ide.backend.model.ConnectedBlocks;
import ide.backend.model.event.RefEventModel;
import ide.backend.runtime.Compiler;
import ide.backend.save.DataSaver;
import ide.backend.variables.Variable.VariableType;


/**
 * HandlerModel, is the model for a handler function that can be
 * started by an event.
 * @author Axel
 */
public class HandlerModel extends TopLevelModel{

	/** Body of the handler */
	private ConnectedBlocks _body;
	
	/** Name of the handler */
	private String _name;

	/** Event from this handler */
	private RefEventModel event;
	
	/**
	 * Creates a newhandlerModel with no name.
	 */
	public HandlerModel() {
		super(null);
		_body = new ConnectedBlocks(this);
		_name = "";
	}	
	
	/**
	 * Creates a new handler with given name.
	 * @param name name of the handler.
	 */
	public HandlerModel(String name) {
		super(null);
		_body = new ConnectedBlocks(this);
		_name = name;
	}	
	
	/**
	 * Returns all the blocks contained by the body of the handler.
	 * @return the body of the handler
	 */
	public ArrayList<BlockModel> getBody(){
		return _body.getBlocks();
	}
	
	/**
	 * Add a block into the handler
	 * @param block block to add
	 */
	public void addBlock(ConnectedBlocks block){
		_body.addConnectedBlock(block);
		updateView();
	}
	
	/**
	 * Remove blocks from the handler
	 * @param block, top block to remove
	 * @return the connected Blocks
	 */
	public ConnectedBlocks removeBlock(ConnectedBlocks block){
		return _body.removeBlocks(block);
		
	}
	
	/**
	 * Returns the body of the handler in the form of connectedBlocks.
	 * Used to save this model.
	 * @return the body of the handler
	 */
	public ConnectedBlocks getConnectedBlocks(){
		return _body;
	}

	/**
	 * Getter for the name
	 * @return name of the handler
	 */
	public String getName() {
		return _name;
	}
	
	/**
	 * Set the name of the handler.
	 * @param name new name
	 */
	public void setName(String name){
		_name = name;
	}

	/**
	 * Getter for the event as a String
	 * @return EventName of the handler
	 */
	public String getEvent() {
		if(event != null)
			return event.getEvent();
		return "";
	}
	
	/**
	 * Returns the eventreference of the handler.
	 * @return refernce to the event.
	 */
	public RefEventModel getEventRef(){
		return event;
	}
	
	/*
	 * Returns whether the handler has an event.
	 */
	public boolean hasEventRef(){
		if(event != null)
			return true;
		return false;
	}
	
	
	/**
	 * Setter for an event
	 * @param event refrence to the event.
	 */
	public void setEvent(RefEventModel event) {
		this.event = event;
		_body.changeParent(this);
	}
	
	@Override
	public void changeParent(BlockModel p) {
		setParent(p);
		_body.changeParent(this);
	}

	@Override
	public <T> T compile(Compiler<T> c) throws CompileException {
		return c.compileBlock(this);
	}
	
	@Override
	public <T> T save(DataSaver<T> s) {
		return s.saveBlock(this);
	}

	public ConnectedBlocks resetBody() {
		return _body = new ConnectedBlocks(this);
	}

	/**
	 * Clears the debug flags for the handler and its innercomponents.
	 */
	public void clearDebugFlags() {
		_debug = false;
		_body.clearDebugFlags();
		updateView();
	}
	

	@Override
	public void tellParentChanged(BlockModel child,VariableType type) {
		//DO NOTHING
	}
}
