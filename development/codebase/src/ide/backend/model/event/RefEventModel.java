package ide.backend.model.event;

import ide.backend.exceptions.CompileException;
import ide.backend.model.BlockModel;
import ide.backend.runtime.Compiler;
import ide.backend.save.DataSaver;
import ide.backend.variables.Variable.VariableType;

import java.util.Observable;
import java.util.Observer;

/**
 * This Class is used to represent using a Event in a Class.
 * @author Matthijs Kaminski
 *
 */
public class RefEventModel  extends BlockModel implements Observer {

	/*FIELDS*/
	private EventModel _event;
	
	/**
	 * Creates a new Model which observes the model to which it refers.
	 * @param parent parent model.
	 * @param event eventModel to which it refers.
	 */
	public RefEventModel(BlockModel parent, EventModel event) {
		super(parent);
		_event = event;
	}
	
	/**
	 * Returns the name of the event to which it refers.
	 * @return the name
	 */
	public String getEvent(){
		return _event.getType();
	}
	
	/**
	 * Returns the event to which it refers.
	 * @return the model of the event
	 */
	public EventModel getEventModel(){
		return _event;
	}
	
	/**
	 * Returns the TYPE of the given member [name] in the event.
	 * @param name of the member
	 * @return type of the member
	 */
	public VariableType getVarType(String name){
		return _event.getVarType(name);
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
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public <T> T save(DataSaver<T> s) {
		return s.saveBlock(this);
	}

	@Override
	public void tellParentChanged(BlockModel child,VariableType type) {
		//DO NOTHING
	}

}
