package ide.backend.model.variables;



import ide.backend.exceptions.CompileException;
import ide.backend.model.BlockModel;
import ide.backend.runtime.Compiler;
import ide.backend.save.DataSaver;
import ide.backend.variables.Variable.VariableType;

/**
* This class represents the model for a block creating a new Member variable.
* @author matthijs
*
*/
public class MemberModel extends AbstractVariableModel{

	/**
	 * Creates a new MemberModel. with no parent
	 * By Default: Variable name is empty and the type is NUMBER.
	 */
	public MemberModel(){
		super(null);
		_name = new String("");
		_type = new TypeModel(this, VariableType.NUMBER);
	}
	
	/**
	 * Creates a new MemberModel. with given parent.
	 * By Default: Variable name is empty and the type is NUMBER.
	 * @param parent parent of the model.
	 */
	public MemberModel(BlockModel parent) {
		super(parent);
		_name = new String("");
		_type = new TypeModel(this, VariableType.NUMBER);
	}
	
	/**
	 * Creates a new MemberModel with given paren, name and type.
	 * @param parent parent of the model.
	 * @param name name of the member.
	 * @param type type of the member.
	 */
	public MemberModel(BlockModel parent,String name, VariableType type){
		super(parent);
		_name = name;
		_type = new TypeModel(this, type);
		
	}
	
	
	/**
	 * Creates a new reference to this MemberModel.
	 * It's notified if changes are made to this model.
	 * @return
	 */
	public AbstractRefVariabelModel makeReference(BlockModel prnt){
		AbstractRefVariabelModel out = new RefMemberModel(null, this);
		this.addObserver(out);
		return out;
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
	public void changeParent(BlockModel p) {
		setParent(p);
		
	}


}
