package ide.backend.model.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;

import ide.backend.exceptions.CompileException;
import ide.backend.model.BlockModel;
import ide.backend.model.event.RefEventModel;
import ide.backend.model.variables.AbstractRefVariabelModel;
import ide.backend.model.variables.VariableModel;
import ide.backend.runtime.Compiler;
import ide.backend.save.DataSaver;
import ide.backend.variables.Variable.VariableType;

/**
 * The emit model emits an event [event] with members [members]
 * @author axel
 */
public class EmitModel extends BlockModel implements Observer {
	
	// Event which the emit broadcasts
	private RefEventModel _event;
	
	private HashMap<String, AbstractRefVariabelModel> _members;
	//state of error of the emit model
	private boolean _error;
	//
	private ArrayList<VariableModel> _memberstypes;
	
	public EmitModel() {
		super(null);
		init();
	}

	private void init() {
		_memberstypes = new ArrayList<>();
		_members = new HashMap<>();
	}

	public EmitModel(BlockModel parent) {
		super(parent);
		init();
	}
	
	/**
	 * Set the event [ev]
	 * @param ev, the event to set
	 */
	public void setEvent(RefEventModel ev) {
		if (_event != null){
			_event.getEventModel().deleteObserver(this);
			for (VariableModel mem : _memberstypes) {
				mem.deleteObserver(this);
			}
		}
		_members.clear();
		_event = ev;
		_memberstypes.clear();
		if (_event != null){
			_event.getEventModel().addObserver(this);
			for(VariableModel m :_event.getEventModel().getMembers()){
				m.addObserver(this);
				_memberstypes.add(m);
			}
		}
	}
	
	/**
	 * Get the event this emit will send
	 * @return the event this emit will send
	 */
	public RefEventModel getEvent() {
		return _event;
	}
	
	/**
	 * Get the name of the event
	 * @return the name
	 */
	public String getEventName() {
		if (_event != null)
			return _event.getEvent();
		return "";
	}
	
	/**
	 * Returns the state of error of the model.
	 * @return error state.
	 */
	public boolean getError(){
		return _error;
	}
	
	/**
	 * Get a list of names of all the members
	 * @return list of the member names
	 */
	public ArrayList<String> getMemberNames() {
		ArrayList<String> names = new ArrayList<String>();
		for(Entry<String, AbstractRefVariabelModel> ent: _members.entrySet()) {
			names.add(ent.getKey());
		}
		return names;
	}
	
	@Override
	public void changeParent(BlockModel p) {
		setParent(p);
		for (Entry<String, AbstractRefVariabelModel> mem : _members.entrySet()) {
			mem.getValue().changeParent(this);
		}
		
	}
	
	/**
	 * Set a member in the event to send
	 * @param name, name of the member
	 * @param mdl, value of the member
	 */
	public void setMember(String name, AbstractRefVariabelModel abstractRefVariabelModel) {
		_members.put(name,  abstractRefVariabelModel);
		if (abstractRefVariabelModel != null)
			abstractRefVariabelModel.changeParent(this);
		if (abstractRefVariabelModel == null)
			tellParentChanged(null, null);
	}
	
	/**
	 * Returns all the member names of the emit mapped on their variable.
	 * @return list of all members linked to their names
	 */
	public HashMap<String, AbstractRefVariabelModel> getMembers() {
		return _members;
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
		_error = false;
		for (Entry<String, AbstractRefVariabelModel> mem : _members.entrySet()) {
			//check if types are okay
			if (_event != null  && mem.getValue() !=  null && _event.getVarType(mem.getKey()) != mem.getValue().getType()) {
				_error = true;
				break;
			}
			
		}
		//tell to change update on error.
		this.setChanged();
		this.notifyObservers("error");
	}

	@Override
	public void update(Observable o, Object arg) {
		//type of member is changed
		if(arg != null && ((String)arg).equals("typechanged"))
			tellParentChanged(null, null);
		//event is totally changed
		else {
			setChanged();
			notifyObservers("updateEmit");
		}
	}



}
