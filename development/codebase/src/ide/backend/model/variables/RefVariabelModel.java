package ide.backend.model.variables;

import ide.backend.exceptions.CompileException;
import ide.backend.model.BlockModel;
import ide.backend.model.function.TopLevelModel;
import ide.backend.runtime.Compiler;
import ide.backend.save.DataSaver;
import java.util.Observable;


/**
 * This class represents a reference to a variable in the IDE.
 * Since the variable might be changed. The reference observes the variable to which it refers.
 * For easy querying the type and name of the reference is stored in this model.
 * @author matthijs
 *
 */
public class RefVariabelModel extends AbstractRefVariabelModel{
	
	
	private VariableModel _m;
	private boolean _error;
	
	/**
	 * Constructor for a ReferenceVarModel. 
	 * A variable to which it refers is given as a parameter [m].
	 * @param m VariableModel to which it refers,
	 */
	public RefVariabelModel(BlockModel parent,VariableModel m) {
		super(parent);
		_name = m.getName();
		_type = m.getType();
		_m = m;
		checkForError();
		updateView();
	}
	
	/**
	 * Searches the toplevelModel of this model.
	 * @return
	 */
	private TopLevelModel searchTopLevel(){
		TopLevelModel out = null;
		if(getParentModel() != null){
			BlockModel current = getParentModel();
			while(current != null && !(current instanceof TopLevelModel)){
				current = current.getParentModel();
			}
			out = (TopLevelModel) current;
			return out;
		}
		return out;
		
	}
	/**
	 * Update if the variable changes.
	 */
	@Override
	public void update(Observable o, Object arg) {
		_name = ((VariableModel) o).getName();
		_type = ((VariableModel) o).getType();
		checkForError();
		tellParentChanged(null, null);
		updateView();	
	}

	
	
	/**
	 * Returns if there's an error.
	 * @return
	 */
	public boolean getError(){
		return _error;
	}
	
	@Override
	public void changeParent(BlockModel p) {
		setParent(p);
		//check if still in same toplevel as the variableModel to which this block refers.
		checkForError();
		tellParentChanged(null, null);
		updateView();
	}

	private void checkForError() {
		if(_m.searchTopLevel() != searchTopLevel() || _m.getError()){
			_error = true;
		}else{
			_error = false;
		}
	}

	@Override
	public <T> T compile(Compiler<T> c) throws CompileException {
		return c.compileBlock(this);
	}
	
	@Override
	public <T> T save(DataSaver<T> s) {
		return s.saveBlock(this);
	}


	
	
}
