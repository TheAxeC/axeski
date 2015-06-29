package ide.frontend.classes;

import ide.backend.model.BlockModel;
import ide.backend.model.event.EventModel;
import ide.backend.model.function.HandlerModel;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Data Class being used by the IDEPanel class to store information
 * concering wires between inputEvent and handlers.
 * This class was created to clean the ide panel Class from this polution.
 * This class also checks if a wire is being clicked.
 * @author Matthijs Kaminski
 *
 */
public class IDEPanelData {
	
	/**
	 * FIELDS
	 */
	//handler being pressed
	private JButton _pressedButtonHandler;
	//input event being pressed
	private JButton _pressedButtonEvent;
	//input events are mapped on the connected handlers (one-many relation)
	private HashMap<JButton, ArrayList<JButton>> _buttonmap;
	//button is mapped on the handler to which it belongs for better access.
	private HashMap<JButton, HandlerModel> _handlermap;
	//handler model being pressed.
	private HandlerModel _pressedHandler;
	//event being pressed.
	private EventModel _pressedEvent;
	//selected wire.
	private JButton[] _selectedWire;

	
	/**
	 * Constructor:
	 * init selected wire to null
	 */
	public IDEPanelData() {
		_selectedWire = null;
		_buttonmap = new HashMap<JButton, ArrayList<JButton>>();
		_handlermap = new HashMap<JButton, HandlerModel>();
	}


	/**
	 * Returns the buttonmap so wires can be drawn.
	 * @return buttonmap storing connections.
	 */
	public HashMap<JButton, ArrayList<JButton>> get_buttonmap() {
		return _buttonmap;
	}


	/**
	 * Adds an connection between an inputEvent and a handler in the class.
	 * The pressed handler and event are reset after the connection is added.
	 * Add the handler and its button to the handler map.
	 * @param b, secondly pressed button
	 */
	public void addHandler() {
		if(_buttonmap.containsKey(_pressedButtonEvent)){
			_buttonmap.get(_pressedButtonEvent).add(_pressedButtonHandler);
		}else{
			ArrayList<JButton> in = new ArrayList<JButton>();
			in.add(_pressedButtonHandler);
			_buttonmap.put(_pressedButtonEvent,in);
		}
		_handlermap.put(_pressedButtonHandler, _pressedHandler);

		_pressedButtonEvent.setBackground(new JButton().getBackground());
		_pressedButtonHandler.setBackground(new JButton().getBackground());
		_pressedButtonEvent = null;
		_pressedButtonHandler = null;
		_pressedEvent = null;
		_pressedHandler = null;
	}

	/**
	 * If an inputEvent is pressed or the connection of a handler is pressed. This function will be invoked.
	 * The selected event/handlers is saved/changed.
	 * If an Event and handler is selected a connection is made and saved.
	 * @param b button that has been pressed.
	 * @param m blockmodel whose view contains the button that was pressed.
	 */
	public void pressedButton(JButton b, BlockModel m, ClassModelController controller,  JPanel linePanel){

		if(m.getClass() == HandlerModel.class){
			if(_pressedHandler != null)
				_pressedButtonHandler.setBackground(new JButton().getBackground());
			_pressedButtonHandler = b;	
			_pressedHandler = (HandlerModel)m;
		}

		else{
			if(_pressedEvent != null)
				_pressedButtonEvent.setBackground(new JButton().getBackground());
			_pressedButtonEvent = b;
			_pressedEvent = (EventModel)m;
		}

		if(_pressedHandler != null && _pressedEvent != null){
			controller.changeHandlerEvent(_pressedEvent, _pressedHandler);
			addHandler();
			linePanel.repaint();
		}
	}
	
	/**
	 * Draws a connection between a given handlers an input event. (Without adding it in the models)
	 * This functions is used to load view from the models.
	 * @param e EventModel of the inputEvent
	 * @param b Button of the handler
	 * @param m model of the pressed handler
	 * @param linePanel linepanel were the wires are being drawn on.
	 * @param inputView inputeventview storing the inputevent buttons of this class.
	 */
	public void addConnectionHandler(EventModel e, JButton b, HandlerModel m, JPanel linePanel, ClassInputEventsView inputView){
		_pressedButtonEvent = inputView.getEventButton(e);
		_pressedEvent = e;
		_pressedButtonHandler = b;
		_pressedHandler = m;
		addHandler();
		linePanel.repaint();
	}
	
	/**
	 * Check whether a click input event selected a wire.
	 * @param e mouve event of the click.
	 * @param linePanel linepanel on which the wires are drawn.
	 */
	public void checkClickedWire(MouseEvent e, JPanel linePanel) {
		JButton a;
		ArrayList<JButton> b;
		_selectedWire = null;
		//loop over all the wires.
		for (HashMap.Entry<JButton, ArrayList<JButton>> entry : _buttonmap.entrySet()) {
			a = entry.getKey();
			b = entry.getValue();
			for (JButton j : b) {
				//calculate the wire
				QuadCurve2D q = calculateWire(a, j);
				//if wire contains the click repaint the the wire selected.
				if(q.contains(e.getX(), e.getY()-20)){
					_selectedWire = new JButton[2];
					_selectedWire[0] = a;
					_selectedWire[1] = j;
					linePanel.repaint();
					return;
				}
			}

		}
		
	}

	/**
	 * Calcutate the wire between an input event button and a handler event button.
	 * @param a input event button.
	 * @param j handler event button.
	 * @return the calculated wire.
	 */
	public QuadCurve2D calculateWire(JButton a, JButton j) {
		QuadCurve2D q = new QuadCurve2D.Float();
		Point p = a.getParent().getParent().getLocation();
		p.translate(a.getParent().getX(), a.getParent().getY());
		p.translate(a.getX(), a.getY());
		p.translate(a.getWidth()/2, a.getHeight()/2);
		Point p2 = j.getParent().getLocation();
		p2.translate(j.getX(), j.getY());
		p2.translate(j.getWidth()/2, j.getHeight()/2);
		if( p.getY() <  p2.getY() )
			q.setCurve(p2.getX(), p2.getY(),(p2.getX() + p.getX())/2 ,((p2.getY() - p.getY())/2) + p.getY() + 80,p.getX(), p.getY());
		else{
			q.setCurve(p.getX(), p.getY(),(p.getX() + p2.getX())/2 ,((p.getY() - p2.getY())/2) + p2.getY() + 80,p2.getX(), p2.getY());
		}
		return q;
	}

	/**
	 * Returns the current selected wire.
	 * @return buttons defining the selected wire.
	 */
	public JButton[] getSelectedWire() {
		return _selectedWire;
	}

	/**
	 * remove the selected wire from the connections
	 * @param controller controller to the classmodel which stores the connections.
	 * @param linePanel line panel on which the wires are drawn.
	 */
	public void removeSelected(ClassModelController controller, JPanel linePanel) {
		if(_selectedWire != null){
			_buttonmap.get(_selectedWire[0]).remove(_selectedWire[1]);
			controller.changeHandlerEvent(null, _handlermap.get(_selectedWire[1]));
			_selectedWire = null;
			linePanel.repaint();
		}
		
	}

	/**
	 * Remove the wire to which a given handler is connected.
	 * @param model handler possibly connected to a wire.
	 */
	public void removeHandlerWire(HandlerModel model) {
		JButton b = null;
		for (HashMap.Entry<JButton,HandlerModel> entry : _handlermap.entrySet()){
			if(entry.getValue() == model){
				b = entry.getKey();
				break;
			}
		}
		
		for (HashMap.Entry<JButton, ArrayList<JButton>> entry : _buttonmap.entrySet()) {
			ArrayList<JButton> arr = entry.getValue();
			arr.remove(b);
		}
		
		
	}

	/**
	 * Remove alll wires connect to the removedInputEvent.
	 * @param event event being removed.
	 * @param controller controller to the class storing the information.
	 * @param linepanel panel on which the lines are drawn.
	 */
	public void removeInputEvent(JButton event, ClassModelController controller, JPanel linepanel) {
		ArrayList<JButton> rem = _buttonmap.remove(event);
		if(rem != null){
			for(JButton b : rem){
				HandlerModel m = _handlermap.remove(b);
				if(m != null)
					controller.changeHandlerEvent(null, m); //_handlermap.remove(b));
			}
			linepanel.repaint();
		}
	
	}

	/**
	 * unselects the current selected wire.
	 */
	public void unselect() {
		_selectedWire = null;
		
	}


}