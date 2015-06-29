package ide.backend.model.variables;


import ide.backend.exceptions.CompileException;
import ide.backend.model.BlockModel;
import ide.backend.runtime.Compiler;
import ide.backend.save.DataSaver;

import java.util.Observable;


/**
 * This class represents a reference to a Member in the IDE.
 * Since the Member might be changed. The reference observes the Member to which it refers.
 * For easy querying the type and name of the reference is stored in this model.
 * @author matthijs
 *
 */
public class RefMemberModel extends AbstractRefVariabelModel{

	/**
	 * Constructor for a ReferenceVarModel. 
	 * A variable to which it refers is given as a parameter [m].
	 * @param m VariableModel to which it refers.
	 */
	public RefMemberModel(BlockModel parent,MemberModel m) {
		super(parent);
		_name = m.getName();
		_type = m.getType();
		updateView();
	}
	
	
	/**
	 * Update if the variable changes.
	 */
	@Override
	public void update(Observable o, Object arg) {
		_name = ((MemberModel) o).getName();
		_type = ((MemberModel) o).getType();
		tellParentChanged(null, null);
		updateView();	
	}

	
	@Override
	public void changeParent(BlockModel p) {
		setParent(p);
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
}



