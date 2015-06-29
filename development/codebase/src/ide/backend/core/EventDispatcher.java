package ide.backend.core;

import java.util.ArrayList;

import ide.backend.blocks.Block;
import ide.backend.blocks.functionsAndHandlers.HandlerBlock;
import ide.backend.collections.EventPool;
import ide.backend.collections.WireFrame;
import ide.backend.exceptions.FunctionNotFoundException;
import ide.backend.variables.EventInstance;

/**
 * Responsible for dispatching all transmitted events
 * @author Axel
 */
public class EventDispatcher {
	
	private WireFrame _frame;
	
	private EventPool _pool;

	public EventDispatcher(WireFrame frame, EventPool pool) {
		_pool = pool;
		_frame = frame;
	}
	
	/**
	 * dispatch an event [ev], from an Instance [transmitter]
	 * @param ev, the event instance
	 * @param transmitter, the transmitting instance
	 * @return a list of all processes created by the dispatched event
	 * @throws FunctionNotFoundException
	 */
	public ArrayList<Proces> dispatchEvent(EventInstance ev, Instance transmitter) throws FunctionNotFoundException {
		ArrayList<Instance> instances = _frame.getConnected(ev.getEvent(), transmitter);
		return dispatchEventFromInstance(ev, instances, false);
	}
	
	/**
	 * dispatch an event [ev], to an Instance [receiver]
	 * If receiver is null, transmit the event to all instances
	 * @param ev, the event instance
	 * @param receiver, the receiving instance
	 * @return a list of all processes created by the dispatched event
	 * @throws FunctionNotFoundException
	 */
	public ArrayList<Proces> dispatchGlobalEvent(EventInstance ev, Instance receiver) throws FunctionNotFoundException {
		ArrayList<Instance> instances = new ArrayList<Instance>();
		if (receiver != null)
			instances.add(receiver);
		else
			instances = _frame.getAllInstances();
		
		return dispatchEventFromInstance(ev, instances, receiver == null);
	}
	
	/**
	 * dispatch an event [ev], to a list of instances [instances]
	 * @param ev, the event instance
	 * @param instances, the receiving instances
	 * @return a list of all processes created by the dispatched event
	 * @throws FunctionNotFoundException
	 */
	private ArrayList<Proces> dispatchEventFromInstance(EventInstance ev, ArrayList<Instance> instances, boolean global) throws FunctionNotFoundException {
		ArrayList<Proces> processes = new ArrayList<Proces>();
		
		for(Instance inst: instances) {
			try {
				ArrayList<Block> handlers = inst.getHandlers(ev.getEvent());
				for(Block b: handlers) {
					((HandlerBlock) b).setEvent(ev);
					processes.add(new Proces(b, inst, _pool));
				}
			} catch (FunctionNotFoundException e) {
				// We are not launching a global event
				if (!global)
					throw e;
			}
		}
		return processes;	
	}
}
