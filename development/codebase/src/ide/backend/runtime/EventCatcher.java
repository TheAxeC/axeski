package ide.backend.runtime;

import ide.backend.model.classes.InstanceModel;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * Catches events thrown by the system
 * @author axel
 *
 */
public class EventCatcher implements Observer {
	
	/**
	 * Holds all systems event
	 * @author axel
	 *
	 */
	public enum SystemEvent {
		MOUSE_PRESSED,
		MOUSE_RELEASED,
		KEY_A,
		KEY_B,
		KEY_C,
		KEY_D,
		KEY_E,
		KEY_F,
		KEY_G,
		KEY_H,
		KEY_I,
		KEY_J,
		KEY_K,
		KEY_L,
		KEY_M,
		KEY_N,
		KEY_O,
		KEY_P,
		KEY_Q,
		KEY_R,
		KEY_S,
		KEY_T,
		KEY_U,
		KEY_V,
		KEY_W,
		KEY_X,
		KEY_Y,
		KEY_Z,
		DEFAULT
	}
	
	private static HashMap<SystemEvent, String> _converter = new HashMap<>();
	
	/**
	 * Mapping from system events to VM events
	 */
	static {
		_converter.put(SystemEvent.DEFAULT, "<default>");
		_converter.put(SystemEvent.MOUSE_PRESSED, "<mousePress>");
		_converter.put(SystemEvent.MOUSE_RELEASED, "<mouseRelease>");
		_converter.put(SystemEvent.KEY_A, "<keyA>");
		_converter.put(SystemEvent.KEY_B, "<keyB>");
		_converter.put(SystemEvent.KEY_C, "<keyC>");
		_converter.put(SystemEvent.KEY_D, "<keyD>");
		_converter.put(SystemEvent.KEY_E, "<keyE>");
		_converter.put(SystemEvent.KEY_F, "<keyF>");
		_converter.put(SystemEvent.KEY_G, "<keyG>");
		_converter.put(SystemEvent.KEY_H, "<keyH>");
		_converter.put(SystemEvent.KEY_I, "<keyI>");
		_converter.put(SystemEvent.KEY_J, "<keyJ>");
		_converter.put(SystemEvent.KEY_K, "<keyK>");
		_converter.put(SystemEvent.KEY_L, "<keyL>");
		_converter.put(SystemEvent.KEY_M, "<keyM>");
		_converter.put(SystemEvent.KEY_N, "<keyN>");
		_converter.put(SystemEvent.KEY_O, "<keyO>");
		_converter.put(SystemEvent.KEY_P, "<keyP>");
		_converter.put(SystemEvent.KEY_Q, "<keyQ>");
		_converter.put(SystemEvent.KEY_R, "<keyR>");
		_converter.put(SystemEvent.KEY_S, "<keyS>");
		_converter.put(SystemEvent.KEY_T, "<keyT>");
		_converter.put(SystemEvent.KEY_U, "<keyU>");
		_converter.put(SystemEvent.KEY_V, "<keyV>");
		_converter.put(SystemEvent.KEY_W, "<keyW>");
		_converter.put(SystemEvent.KEY_X, "<keyX>");
		_converter.put(SystemEvent.KEY_Y, "<keyY>");
		_converter.put(SystemEvent.KEY_Z, "<keyZ>");
		
	}
	
	private static HashMap<Integer,SystemEvent> _swingkeys = new HashMap<>();
	
	/**
	 * Mapping from Swing events to System events
	 */
	static {
		_swingkeys.put(KeyEvent.VK_A, SystemEvent.KEY_A);
		_swingkeys.put(KeyEvent.VK_B, SystemEvent.KEY_B);
		_swingkeys.put(KeyEvent.VK_C, SystemEvent.KEY_C);
		_swingkeys.put(KeyEvent.VK_D, SystemEvent.KEY_D);
		_swingkeys.put(KeyEvent.VK_E, SystemEvent.KEY_E);
		_swingkeys.put(KeyEvent.VK_F, SystemEvent.KEY_F);
		_swingkeys.put(KeyEvent.VK_G, SystemEvent.KEY_G);
		_swingkeys.put(KeyEvent.VK_H, SystemEvent.KEY_H);
		_swingkeys.put(KeyEvent.VK_I, SystemEvent.KEY_I);
		_swingkeys.put(KeyEvent.VK_J, SystemEvent.KEY_J);
		_swingkeys.put(KeyEvent.VK_K, SystemEvent.KEY_K);
		_swingkeys.put(KeyEvent.VK_L, SystemEvent.KEY_L);
		_swingkeys.put(KeyEvent.VK_M, SystemEvent.KEY_M);
		_swingkeys.put(KeyEvent.VK_N, SystemEvent.KEY_N);
		_swingkeys.put(KeyEvent.VK_O, SystemEvent.KEY_O);
		_swingkeys.put(KeyEvent.VK_P, SystemEvent.KEY_P);
		_swingkeys.put(KeyEvent.VK_Q, SystemEvent.KEY_Q);
		_swingkeys.put(KeyEvent.VK_R, SystemEvent.KEY_R);
		_swingkeys.put(KeyEvent.VK_S, SystemEvent.KEY_S);
		_swingkeys.put(KeyEvent.VK_T, SystemEvent.KEY_T);
		_swingkeys.put(KeyEvent.VK_U, SystemEvent.KEY_U);
		_swingkeys.put(KeyEvent.VK_V, SystemEvent.KEY_V);
		_swingkeys.put(KeyEvent.VK_W, SystemEvent.KEY_W);
		_swingkeys.put(KeyEvent.VK_X, SystemEvent.KEY_X);
		_swingkeys.put(KeyEvent.VK_Y, SystemEvent.KEY_Y);
		_swingkeys.put(KeyEvent.VK_Z, SystemEvent.KEY_Z);
	}
	
	
	// The runtime
	private InterpreterRuntime _runtime;
	
	public EventCatcher(InterpreterRuntime runtime) {
		_runtime = runtime;
	}

	@Override
	public void update(Observable o, Object arg) {
		if (_converter.get(arg) == null) return;

		if (o instanceof InstanceModel)
			_runtime.sendEvent(((InstanceModel) o).getName(), _converter.get(arg));
		else
			_runtime.sendEvent(null, _converter.get(arg));
	}
	
	/**
	 * Get the event that belongs to the given KeyEvent from Swing
	 */
	public static SystemEvent getEvent(Integer ev) {
		if (_swingkeys.get(ev) == null)
			return SystemEvent.DEFAULT;
		return _swingkeys.get(ev);
	}
	
	/**
	 * Get all default events
	 * @return
	 */
	public static String[] getDefaultEvents() {
		String[] list = {
				"<mousePress>",
				"<mouseRelease>",
				"<default>",
				"<keyA>",
				"<keyB>",
				"<keyC>",
				"<keyD>",
				"<keyE>",
				"<keyF>",
				"<keyG>",
				"<keyH>",
				"<keyI>",
				"<keyJ>",
				"<keyK>",
				"<keyL>",
				"<keyM>",
				"<keyN>",
				"<keyO>",
				"<keyP>",
				"<keyQ>",
				"<keyR>",
				"<keyS>",
				"<keyT>",
				"<keyU>",
				"<keyV>",
				"<keyW>",
				"<keyX>",
				"<keyY>",
				"<keyZ>"
			};
		return list;
	}
}
