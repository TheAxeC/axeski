package ide.backend.model.physicModels;

import ide.backend.exceptions.CompileException;
import ide.backend.model.BlockModel;
import ide.backend.runtime.Compiler;
import ide.backend.save.DataSaver;
import ide.backend.variables.Variable.VariableType;
/**
 * Sets the visibility of the sprite of an instance to true.
 * @author Matthijs Kaminski
 *
 */
public class ShowModel extends BlockModel{
	
	/**
	 * Creates a new showModel with no parent.
	 */
	public ShowModel() {
		super(null);
	}
	
	/**
	 * Creates a new show model with given parent.
	 * @param parent parent of the model.
	 */
	public ShowModel(BlockModel parent){
		super(parent);
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

}
