package ide.backend.model.classes;

import ide.backend.exceptions.CompileException;
import ide.backend.model.BlockModel;
import ide.backend.model.event.EventModel;
import ide.backend.runtime.Compiler;
import ide.backend.save.DataSaver;
import ide.backend.variables.Variable.VariableType;

/**
 * This Class represents a wire in the program. Connection two instances. With a given Event.
 * @author matthijs
 *
 */
public class WireModel extends BlockModel{

	/*FIELDS*/
	private InstanceModel _from;
	private InstanceModel _to;
	private EventModel _event;
	
	/**
	 * Creates a new wire.
	 * @param parent parent of the model.
	 * @param from Instance sending the Event.
	 * @param to Instance recieving the Event.
	 * @param event the event being send.
	 */
	public WireModel(BlockModel parent, InstanceModel from, InstanceModel to, EventModel event) {
		super(parent);
		_from = from;
		_to = to;
		_event = event;
		
	}
	
	/**
	 * Returns the Instance sending the Event.
	 * @return sending instance
	 */
	public String getFrom(){
		return _from.getName();
	}
	
	/**
	 * Instance receiving the Event.
	 * @return receiving instance.
	 */
	public String getTo(){
		return _to.getName();
	}
	
	/**
	 * Event being send over wire
	 * @return Event being send.
	 */
	public String getEvent(){
		return _event.getType();
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
	public void tellParentChanged(BlockModel child,VariableType type) {
		//DO NOTHING
	}

	/**
	 * Check whether a given WireModel is equal to this model
	 * @param m other wireModel
	 * @return if equal true, else false.
	 */
	public boolean equal(WireModel m) {
		return _to == m._to && _from == m._from && _event == m._event;
	}
}
