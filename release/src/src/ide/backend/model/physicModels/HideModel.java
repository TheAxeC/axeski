package ide.backend.model.physicModels;

import ide.backend.exceptions.CompileException;
import ide.backend.model.BlockModel;
import ide.backend.runtime.Compiler;
import ide.backend.save.DataSaver;
import ide.backend.variables.Variable.VariableType;

/**
 * Sets the visibility of the sprite of an instance to false.
 * @author Matthijs Kaminski
 *
 */
public class HideModel extends BlockModel{
	
	/**
	 * Creates a new HideModel with no parent.
	 */
	public HideModel() {
		super(null);
	}
	
	/**
	 * Creates a new HideModel with given parent.
	 * @param parent parent of the model.
	 */
	public HideModel(BlockModel parent){
		super(parent);
	}

	@Override
	public void changeParent(BlockModel p) {
		// TODO Auto-generated method stub
		
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
