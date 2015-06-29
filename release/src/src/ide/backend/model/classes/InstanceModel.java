package ide.backend.model.classes;

import java.awt.Point;
import java.util.Observable;
import java.util.Observer;

import ide.backend.exceptions.CompileException;
import ide.backend.model.BlockModel;
import ide.backend.runtime.Compiler;
import ide.backend.runtime.EventCatcher.SystemEvent;
import ide.backend.save.DataSaver;
import ide.backend.variables.Variable.VariableType;

/**
 * This Class represents the model of an instance used in the wireFrame part of the IDE.
 * An instance observers its Class for changes.
 * An Instance has an name for addressing it.
 * @author matthijs
 *
 */
public class InstanceModel extends BlockModel implements Observer{
	
	/*FIELDS*/
	private ClassModel _class;
	private String _name;
	
	private Costume _primaryCostume;

	/**
	 * Creates an new instance of a given Class [classmodel] with a given name [name].
	 * @param parent parent of the model
	 * @param classModel classmodel of which it is an instance
	 * @param name name of the instance.
	 */
	public InstanceModel(BlockModel parent, ClassModel classModel, String name) {
		super(parent);
		_class = classModel;
		_class.addObserver(this);
		_name = name;
		
		_primaryCostume = classModel.getCostume(classModel.getSelectedCostume());
	}
	
	/**
	 * Returns the name of the instance.
	 * @return name of instance
	 */
	public String getName(){
		return _name;
	}
	
	/**
	 * Returns the name of the class of which the instance is an instance.
	 * @return name of the class of the instance.
	 */
	public String getClassName(){
		return _class.getName();
	}
	
	/**
	 * The classModel of the instance
	 * @return classmodel
	 */
	public ClassModel getClassModel(){
		return _class;
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
	public void update(Observable o, Object arg) {
		if (o instanceof ClassModel) {
			ClassModel classModel = (ClassModel) o;
			_primaryCostume = classModel.getCostume(classModel.getSelectedCostume());
		}
		if (arg instanceof Costume) {
			_primaryCostume = (Costume) arg;
			this.setChanged();
			this.notifyObservers();
		} else {
			this.setChanged();
			this.notifyObservers(arg);
		}
	}
	
	/**
	 * If View of model is pressed
	 * @param mousePressed mousepressed event
	 */
	public void pushEvent(SystemEvent mousePressed) {
		this.setChanged();
		this.notifyObservers(mousePressed);
	}
	
	/**
	 * Costume of the instance.
	 * @return Costume.
	 */
	public Costume getCostume() {
		return _primaryCostume;
	}
	
	@Override
	public void tellParentChanged(BlockModel child,VariableType type) {
		//DO NOTHING
	}
	
	/**
	 * Sets the position of the block to the given position [pos].
	 * @param pos positon being set.
	 */
	@Override
	public void setPos(Point pos){
		double[] point = new double[2];
		point[0] = pos.x - _position.x;
		point[1] = pos.y - _position.y;
		this.setChanged();
		this.notifyObservers(point);
		super.setPos(pos);
	}
}
