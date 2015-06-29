package ide.backend.runtime;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ide.backend.collections.ClassPool;
import ide.backend.collections.EventPool;
import ide.backend.collections.WireFrame;
import ide.backend.core.ClassContainer;
import ide.backend.core.Event;
import ide.backend.core.EventDispatcher;
import ide.backend.core.Instance;
import ide.backend.core.VirtualMachine;
import ide.backend.exceptions.BreakException;
import ide.backend.exceptions.FunctionNotFoundException;
import ide.backend.model.classes.InstanceModel;

/**
 * @author Axel Faes && Matthijs Kaminski
 * The implemented runtime class that uses the custom interpreter
 */
public class InterpreterRuntime extends Runtime {

	/**
	 * Copy the compiler user
	 */
	private RuntimeData _compileCopy;
	
	/**
	 * Data for the runtime
	 */
	private RuntimeData _runtimeData;
	
	private ModelCollection _models;
	
	/** Lock to synchronize event catching and running */
	private Lock _lock;
	
	/**
	 * Contains all data required by the runtime
	 * @author axel
	 *
	 */
	private class RuntimeData {
		/**
		 * The Virtual Machine
		 */
		private VirtualMachine _vm;
		
		/**
		 * The classPool
		 */
		private ClassPool _classPool;
		
		/**
		 * The eventpool
		 */
		private EventPool _eventPool;
		
		/**
		 * The wireframe
		 */
		private WireFrame _wireFrame;
		
		/**
		 * Initialise the runtime data
		 */
		public void init() {
			_classPool = new ClassPool();
			_eventPool = new EventPool();
			_wireFrame = new WireFrame(_eventPool);
			_vm = new VirtualMachine(new EventDispatcher(_wireFrame, _eventPool));
		}
		
		/**
		 * Reset the virtual machine
		 */
		public void reset() {
			_vm = new VirtualMachine(new EventDispatcher(_wireFrame, _eventPool));
		}
		
		/**
		 * Getter for the VM
		 * @return the vm
		 */
		public VirtualMachine getVM() {
			return _vm;
		}
		
		/**
		 * Getter for the classpool
		 * @return the classpool
		 */
		public ClassPool getClassPool() {
			return _classPool;
		}
		
		/**
		 * Getter for the eventpool
		 * @return the event pool
		 */
		public EventPool getEventPool() {
			return _eventPool;
		}
		
		/**
		 * Getter for the wireframe
		 * @return the wireframe
		 */
		public WireFrame getFrame() {
			return _wireFrame;
		}
	}

	/**
	 * Creates a new runtime.
	 */
	public InterpreterRuntime() {
		_runtimeData = new RuntimeData();
		clean();
		setCompiler();
		
		_isCompiled = false;
		_running = false;
		_isDebug = false;
		_catcher = new EventCatcher(this);
		_lock = new ReentrantLock();
	}
	
	/**
	 * resets the data of the runtime.
	 */
	private void clean() {
		_runtimeData.init();
	}

	@Override
	public boolean compile(ModelCollection collection) {
		_compileCopy = _runtimeData;
		clean();
		boolean succes = _compiler.compile(this, collection);

		if (succes) {
			_isCompiled = true;
			_isDebug = false;
			prepareRun();
			_models = collection;
		} else {
			_runtimeData = _compileCopy;
		}
		return succes;
	}
	
	@Override
	public boolean compileDebug(ModelCollection collection) {
		_compileCopy = _runtimeData;
		clean();
		boolean succes = _compiler.compileDebug(this, collection);

		if (succes) {
			_isCompiled = true;
			_isDebug = true;
			prepareRun();
			_models = collection;
		} else {
			_runtimeData = _compileCopy;
		}
		return succes;
	}

	@Override
	public boolean run() throws FunctionNotFoundException {
		if (!_running && _isCompiled) {
			_running = true;
	
			long time = 0;
			long timer = 0;
			timer = System.nanoTime();
			while(_running) {
				// Let each step take one ms
				if (System.nanoTime() - timer > 33000000) {
					try {
						Thread.sleep(1);
						time = 1000000;
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
				if (time < 1000000) {
					try {
						Thread.sleep(0, (int) (999999 - time));
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
				
				// Run the vm
				long timeStart = System.nanoTime();
				try {
					_lock.lock();
					if (_running)
						_runtimeData.getVM().runStep(1);
				} catch (BreakException e) {
					_running = false;
					return false;
				} finally {
					_lock.unlock();
				}
				long timeEnd = System.nanoTime();
				
				time = timeEnd - timeStart;
			}
		}
		return true;
	}
	
	@Override
	public boolean step() throws FunctionNotFoundException {
		if (!_running && _isCompiled) {
			try {
				_lock.lock();
				int total = (_isDebug ? 3 : 1);
				return _runtimeData.getVM().runStep(total);
			} catch (BreakException e) {
				return true;
			} finally {
				_lock.unlock();
			}
		}
		return false;
	}

	@Override
	public void setCompiler() {
		_compiler = new InterpreterCompiler();
	}

	/**
	 * Adds a new ClassContainer to the classPool.
	 * @param c classContainer being added
	 */
	public void addClassContainer(ClassContainer c){
		_runtimeData.getClassPool().addClass(c.getName(),c);
	}
	
	/**
	 * Returns the classcontainer to which the given name [name] refers.
	 * @param name name of classcontainer.
	 * @return the classcontainer if found else null.
	 */
	public ClassContainer getClassContainer(String name){
		return _runtimeData.getClassPool().getClass(name);
	}

	/**
	 * Get an event from the runtime
	 * @param name the name of the event
	 * @return the found event or null
	 */
	public Event getEvent(String name) {
		return _runtimeData.getEventPool().getEvent(name);
	}

	/**
	 * Add an event to the runtime.
	 * @param e Event being added.
	 */
	public void AddEvent(Event e) {
		_runtimeData.getEventPool().addEvent(e.getType(), e);
	}
	
	/**
	 * Add an instance to the runtime
	 * @param classname the name of the class
	 * @param name the name of the instance
	 * @param model the model of the instance
	 */
	public void addInstance(String classname, String name, InstanceModel model) {
		_runtimeData.getFrame().addInstance(_runtimeData.getClassPool().getClass(classname), name, model);
	}
	
	/**
	 * Add a connection to the runtime
	 * @param from, from where the wire starts
	 * @param to, where the wire goes to
	 * @param event, the event on the wire
	 */
	public void addConnection(String from, String to, String event){
		_runtimeData.getFrame().addConnection(from, to, _runtimeData.getEventPool().getEvent(event));
	}

	@Override
	public void reset() {
		_runtimeData.reset();
	}

	@Override
	public void stopRunning() {
		if (_isCompiled) {
			try {
				_lock.lock();
				_running = false;
			} finally {
				_lock.unlock();
			}
			prepareRun();
			_models.clearDebugFlags();
		}
	}

	@Override
	public boolean isRunning() {
		return _running;
	}
	
	@Override
	public boolean isCompiled() {
		return _isCompiled;
	}
	
	@Override
	public boolean isDebugMode() {
		return _isDebug;
	}

	@Override
	protected void prepareRun() {
		try {
			for(Instance inst: _runtimeData.getFrame().getAllInstances())
				inst.resetPositions();
			// Reset the VM
			_runtimeData.getVM().reset();
			// Add the start event
			_runtimeData.getVM().addEvent(getEvent("<Start>").makeEventInstance(), null);
		} catch (FunctionNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Send an event [event] to [instance]
	 * @param instance
	 * @param event
	 */
	public void sendEvent(String instance, String event) {
		if (_running) {
			try {
				_lock.lock();
				if (instance == null)
					_runtimeData.getVM().addEvent(getEvent(event).makeEventInstance(), null);
				else {
					Instance inst = _runtimeData.getFrame().getInstance(instance);
					_runtimeData.getVM().addEvent(getEvent(event).makeEventInstance(), inst);
				}
			} catch (FunctionNotFoundException e) {
				// These events do not necessarily need to be executed
			} finally {
				_lock.unlock();
			}
		}
	}
	
	/**
	 * Getter for the event catcher
	 * @return the event catcher
	 */
	public EventCatcher getCatcher() {
		return _catcher;
	}
	
}
