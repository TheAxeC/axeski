package ide.backend.model.math;

import ide.backend.exceptions.CompileException;
import ide.backend.model.BlockModel;
import ide.backend.runtime.Compiler;
import ide.backend.save.DataSaver;
import ide.backend.variables.Variable.VariableType;

/**
 * This model represents a model block, which will create a random number variable
 * @author matthijs Kaminski
 *
 */
public class RandomModel extends BlockModel{
	
	/**
	 * Create a new randomModel
	 */
	public RandomModel() {
		super(null);
	}
	
	public RandomModel(BlockModel parent){
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
		if(getParent() != null){
			//tell the parent its a numberavariable.
			getParent().tellParentChanged(this,VariableType.NUMBER);
		}
	}

}
