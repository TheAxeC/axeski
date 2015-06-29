package ide.backend.model.classes;

import java.util.ArrayList;

import ide.backend.exceptions.CompileException;
import ide.backend.model.BlockModel;
import ide.backend.runtime.Compiler;
import ide.backend.save.DataSaver;
import ide.backend.variables.Variable.VariableType;

/**
 * Model representing the wireframe. Containing all the instances and the wires between those instances.
 * @author matthijs
 *
 */
public class WireFrameModel extends BlockModel{

	/*FIELDS*/
	private ArrayList<InstanceModel> _instances;
	private ArrayList<WireModel> _wires;
	
	/**
	 * Creates a new wireFrameModel.
	 * @param parent parent of the model.
	 */
	public WireFrameModel(BlockModel parent) {
		super(parent);
		_instances = new ArrayList<InstanceModel>();
		_wires = new ArrayList<WireModel>();
		updateView();
	}
	
	/**
	 * Adds a instance to the model.
	 * @param instance  instance being added.
	 */
	public void addInstance(InstanceModel instance){
		_instances.add(instance);
		updateView();
	}
	
	/**
	 * Removes an instance from the model.
	 * @param instance instance being removed.
	 */
	public void removeInstance(InstanceModel instance){
		_instances.remove(instance);
		updateView();
	}
	
	/**
	 * Returns all the instances the model contains.
	 * @return all the instances.
	 */
	public ArrayList<InstanceModel> getInstances(){
		return _instances;
	}
	
	/**
	 * Adds a wire to the model
	 * @param wire wire being added.
	 */
	public void addWire(WireModel wire){
		for(WireModel m: _wires)
			if (m.equal(wire))
				return;
		_wires.add(wire);
	}
	
	/**
	 * Removes a wire from the model
	 * @param wire wire being removed.
	 */
	public void removeWire(WireModel wire){
		_wires.remove(wire);
	}
	
	/**
	 * Returns all the wires in the model
	 * @return all the wires.
	 */
	public ArrayList<WireModel> getWires(){
		return _wires;
	}
	
	/**
	 * Returns Instane model with given name [name] if exists else returns null.
	 * @param name
	 * @return
	 */
	public InstanceModel getInstance(String name) {
		for (InstanceModel model : _instances) {
			if(model.getName().equals(name))
				return model;
		}
		return null;
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
