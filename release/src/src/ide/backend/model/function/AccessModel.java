package ide.backend.model.function;

import java.util.Observable;
import java.util.Observer;

import ide.backend.exceptions.CompileException;
import ide.backend.model.BlockModel;
import ide.backend.model.event.EventModel;
import ide.backend.model.event.RefEventModel;
import ide.backend.model.variables.VariableModel;
import ide.backend.runtime.Compiler;
import ide.backend.save.DataSaver;
import ide.backend.variables.Variable.VariableType;

/**
 * This class represents a AccesBlock in the IDE.
 * @author Matthijs Kaminski
 *
 */
public class AccessModel extends BlockModel implements Observer{

	/*FIELDS*/
	private RefEventModel _event;
	private HandlerModel _handler;
	private String _member;
	
	private VariableModel _memberModel;
	
	
	public AccessModel() {
		super(null);
		_member = "";
		searchHandler();
		updateView();
	}
	
	/**
	 * Creates a new accessModel. 
	 * Searches if it's added to a handler, if so it already sets its event it accesses.
	 * @param parent the parentblock
	 */
	public AccessModel(BlockModel parent) {
		super(parent);
		_member = "";
		searchHandler();
		updateView();
	}
	
	/**
	 * Sets the chosen member to a given String [member].
	 * @param m, the name of the member to set or null to unset
	 */
	public void setMember(String m){
		if (_memberModel != null) {
			_memberModel.deleteObserver(this);
		}
		_member = m;
		if (_event != null) {
			 VariableModel model = _event.getEventModel().getMember(_member);
			 if (model != null)
				 model.addObserver(this);
			 _memberModel = model;
		}
		tellParentChanged(null, null);
	}
	
	/**
	 * Returns the currentSet member it accesses.
	 * @return get the member
	 */
	public String getMember(){
		return _member;
	}
	
	/**
	 * Returns the name of the event from which a member is acces through this blockModel.
	 * @return return the eventname
	 */
	public String getEventName(){
		return _event.getEvent();
	}
	
	
	/**
	 * Get the event model from this access model
	 * @return the event mdel
	 */
	public EventModel getEvent(){
		if(_event != null)
			return _event.getEventModel();
		else
			return null;
	}
	
	/**
	 * Search for the handler of the access model
	 */
	private void searchHandler(){
		if(getParentModel() != null){
			BlockModel current = getParentModel();
			while(current != null  &&  current.getClass() != HandlerModel.class){
				current = current.getParentModel();
			}
			_handler = (HandlerModel) current;
			if(_handler!=null){
				if(_event != null)
					_event.getEventModel().deleteObserver(this);
				_event = _handler.getEventRef();
				if(_event != null)
					_event.getEventModel().addObserver(this);
			}
			else
				_event = null;
		}else
			_event = null;
		
	}
	
	@Override
	public void changeParent(BlockModel p) {
		setParent(p);
		searchHandler();
		tellParentChanged(null, null);
		updateView();
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
	public void update(Observable o, Object arg) {
		tellParentChanged(null, null);
		updateView();
	}
	

	@Override
	public void tellParentChanged(BlockModel child,VariableType type) {
		//DO NOTHING
		if (getParentModel() != null && _event != null) {
			getParentModel().tellParentChanged(this, _event.getVarType(_member));
		}
	}


}
