package ide.backend.core;

import ide.backend.exceptions.BreakException;
import ide.backend.exceptions.FunctionNotFoundException;
import ide.backend.exceptions.ProcesFinshedException;
import ide.backend.variables.EventInstance;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * The Virtual Machine of the language
 * It will run one step from the Processes
 * @author Axel
 */
public class VirtualMachine {
	
	private EventDispatcher _dispatcher;
	
	private LinkedList<Proces> _processes;
	
	private LinkedList<Proces> _eventProcesses;
	private LinkedList<Proces> _doneProcesses;

	/**
	 * Creates a new VM with given dispatcher
	 * @param dispatcher dispatcher for the vm.
	 */
	public VirtualMachine(EventDispatcher dispatcher) {
		_dispatcher = dispatcher;
		_processes = new LinkedList<Proces>();
		_eventProcesses = new LinkedList<Proces>();
		_doneProcesses = new LinkedList<Proces>();
	}
	
	/**
	 * Runs one step from the VM
	 * @return whether or not the VM is done running
	 * @throws FunctionNotFoundException
	 */
	public boolean runStep(int steps) throws FunctionNotFoundException, BreakException {
		if (_processes.isEmpty()) {
			_processes.addAll(_eventProcesses);
			_eventProcesses.clear();
		}
		
		boolean status = true;
		BreakException exep = null;
		while (!_processes.isEmpty()) {
			Proces proces = _processes.removeLast();
			try {
				status = status && runSingleProces(proces,steps);
			} catch (BreakException e) {
				exep = e;
			}
		}
		_processes.addAll(_eventProcesses);
		_processes.addAll(_doneProcesses);
		_eventProcesses.clear();
		_doneProcesses.clear();
		
		if (exep != null) throw exep;
		
		return status;
	}
	
	private boolean runSingleProces(Proces proces, int steps) throws FunctionNotFoundException, BreakException {
		try {
			EventInstance inst = (EventInstance) proces.run(steps);
			if (inst != null) {
				ArrayList<Proces> processes = _dispatcher.dispatchEvent(inst, proces.getInstance());
				for(Proces p: processes) {
					_eventProcesses.push(p);
				}
			}
			_doneProcesses.push(proces);
		} catch (ProcesFinshedException e) {
			if (_processes.isEmpty() && _doneProcesses.isEmpty())
				return false;
		} catch (BreakException e) {
			_doneProcesses.push(proces);
			throw e;
		}
		return true;		
	}
	
	/**
	 * Adds a global event (such as a keypress
	 * @param ev, the event thats transmitted
	 * @param receiver, the receiving instance,
	 * 				if null, all instances will be seen as the receiver
	 * @throws FunctionNotFoundException
	 */
	public void addEvent(EventInstance ev, Instance receiver) throws FunctionNotFoundException {
		ArrayList<Proces> processes = _dispatcher.dispatchGlobalEvent(ev, receiver);
		for(Proces p: processes) {
			_eventProcesses.push(p);
		}
	}
	
	/**
	 * Resets the VM
	 */
	public void reset() {
		_processes = new LinkedList<Proces>();
	}

}
