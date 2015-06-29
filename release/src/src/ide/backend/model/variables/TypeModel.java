package ide.backend.model.variables;
import ide.backend.exceptions.CompileException;
import ide.backend.model.BlockModel;
import ide.backend.runtime.Compiler;
import ide.backend.save.DataSaver;
import ide.backend.variables.Variable.VariableType;

/**
 * This class represents a Type field in the IDE.
 * @author matthijs
 *
 */
public class TypeModel  extends BlockModel{

	/* FIELDS */
	private VariableType _type;
	
	/**
	 * Creates a new typeModel
	 * @param parent parent of the model
	 * @param type type of the typemodel
	 */
	public TypeModel(BlockModel parent, VariableType type) {
		super(parent);
		_type = type;
	}

	/**
	 * Returns the type represented by the type model.
	 * @return
	 */
	public VariableType getType() {
		return _type;
	}

	/**
	 * Sets the type represented by the model to a given type [_type]
	 * @param _type
	 */
	public void setType(VariableType _type) {
		this._type = _type;
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
		// Do nothing
		
	}

}
