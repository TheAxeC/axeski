package ide.frontend.wire;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import ide.backend.language.LanguageModule;
import ide.backend.model.classes.InstanceModel;
import ide.backend.model.event.EventModel;
import ide.frontend.mvcbase.Controller;

/**
 * This view represent the view of an instance in the wirepanel
 * it contains all the input and output events as the class of the instance describe.
 * A change in these events in the classmodel will invoke the same result in this view.
 * Connections are saved by the panel not the instance.
 * @author Matthijs Kaminski
 *
 */
public class InstanceView extends JPanel implements Observer {
	private static final long serialVersionUID = 1L;
	
	/**FIELDS**/
	//for dragging.
	private int oldMouseX;
	private int oldMouseY;
	//model of the view.
	private InstanceModel _model;
	//wire panel where the view is placed on.
	private WirePanel _panel;
	//pointer to itself.
	private InstanceView _t;
	//panel for input events.
	private JPanel _inputs;
	//panel for output events.
	private JPanel _outputs;
	
	//storing event name to the panel of the button representing it.
	private HashMap<String, JPanel> _inputEvents;
	private HashMap<String, JPanel> _outputEvents;

	//language module of the program.
	private LanguageModule _lang;
	

	/**
	 * Create a new instance view for an instance.
	 * @param model model of the instance.
	 * @param controller controller to the instance.
	 * @param lang language module of the program
	 * @param panel panel on which this instaceview is placed.
	 */
	public InstanceView(Observable model, Controller controller, LanguageModule lang, WirePanel panel) {
		initFields(model, panel, lang);
		this.setBounds(_model.getPos().x, _model.getPos().y, 190, 140);
		this.setBorder(BorderFactory.createTitledBorder(_model.getName()));
		((TitledBorder)this.getBorder()).setTitleJustification(TitledBorder.CENTER);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		initComponents();
		makeDragable();
	}

	/**
	 * init the fiels of the instance view
	 * @param model instace model
	 * @param panel where view is added on
	 * @param lang languageModule of the program.
	 */
	private void initFields(Observable model, WirePanel panel, LanguageModule lang) {
		_model = (InstanceModel)model;
		_model.addObserver(this);
		_lang = lang;
		_lang.addObserver(this);
		_panel = panel;
		_inputEvents = new HashMap<>();
		_outputEvents = new HashMap<>();
		_t = this;
		_inputs = new JPanel();
		_inputs.setBorder(BorderFactory.createTitledBorder(_lang.getString("inputEvents")));
		_inputs.setLayout(new GridLayout(0,1));
		_inputs.setBounds(0,0,180,50);
		this.add(_inputs);
		_outputs = new JPanel();
		_outputs.setBorder(BorderFactory.createTitledBorder(_lang.getString("OutputEvents")));
		_outputs.setLayout(new GridLayout(0,1));
		_outputs.setBounds(0,55,180,50);
		this.add(_outputs);
		setName(_model.getName());
	}

	/**
	 * Initiate the components
	 */
	private void initComponents(){
		initInputEvents();
		initOutputEvents();
	}
	
	/**
	 * Add all output events
	 */
	private void initOutputEvents() {
		for (EventModel ev : _model.getClassModel().getOutputEvents()) {
			addOutputEvent(ev);
		}
	}

	/**
	 * Add all input events
	 */
	private void initInputEvents() {
		for (EventModel ev : _model.getClassModel().getInputEvents()) {
			addInputEvent(ev);
		}
	}

	/**
	 * Create an output event panel (button and switcher) for the given event on this instanceview
	 * @param ev
	 */
	private void addInputEvent(EventModel ev) {
		JPanel dummy = new JPanel();
		dummy.setLayout(new FlowLayout());
		dummy.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		JButton b = createConnectionButton(ev, true);
		b.setName("con");
		JButton switcher = addSwitchEventButton(b, ev);
		switcher.setPreferredSize(new Dimension(80,30));
		switcher.setSize(new Dimension(80,30));
		dummy.add(switcher);
		dummy.add(b);
		dummy.setPreferredSize(new Dimension(110,35));
		dummy.setSize(new Dimension(110,35));
		
		_inputs.add(dummy);
		_inputEvents.put(ev.getType(), dummy);
		_inputs.setBounds(_inputs.getBounds().x, _inputs.getBounds().y, _inputs.getBounds().width, _inputs.getBounds().height + dummy.getHeight());
		this.setBounds(this.getBounds().x, this.getBounds().y, this.getBounds().width, this.getBounds().height + dummy.getHeight());
		this.revalidate();
		this.repaint();
	}
	
	/**
	 * Create an output event panel (button and switcher) for the given event on this instanceview
	 * @param ev
	 */
	private void addOutputEvent(EventModel ev) {
		JPanel dummy = new JPanel();
		dummy.setLayout(new FlowLayout());
		dummy.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		JButton b = createConnectionButton(ev, false);
		b.setName("con");
		JButton switcher = addSwitchEventButton(b, ev);
		switcher.setPreferredSize(new Dimension(80,30));
		switcher.setSize(new Dimension(80,30));
		dummy.add(switcher);
		dummy.add(b);
		dummy.setPreferredSize(new Dimension(110,35));
		dummy.setSize(new Dimension(110,35));
		
		_outputs.add(dummy);
		_outputEvents.put(ev.getType(), dummy);
		_outputs.setBounds(_outputs.getBounds().x, _outputs.getBounds().y, _outputs.getBounds().width, _outputs.getBounds().height + dummy.getHeight());
		this.setBounds(this.getBounds().x, this.getBounds().y, this.getBounds().width, this.getBounds().height + dummy.getHeight());
		this.revalidate();
		this.repaint();
	}
 
	/**
	 * Create a new connection button which can be queried for information.
	 * The instance it represents and the eventmodel it represents.
	 * @param ev eventmodel it represents.
	 * @param type type of event. true for input events, false for output events.
	 * @return the created connectionbutton.
	 */
	private JButton createConnectionButton(EventModel ev, boolean type) {
		JButton b = new ConnectionButton(ev, type, _model);
		return b;
	}
	
	/**
	 * Create a connection button to connect wires.
	 * Create a new connection button which can be queried for information.
	 * The instance it represents and the eventmodel it represents.
	 * @author matthijs
	 *
	 */
	public class ConnectionButton extends JButton{
		
		private static final long serialVersionUID = 1L;
		//fields of the button.
		JButton _self = this;
		InstanceModel _m;
		EventModel _e;
		/**
		 * Constructor
		 * @param ev eventmodel it represents.
		 * @param type type of event. true for input events, false for output events.
		 * @param m intance model of the view where the button is placed on.
		 */
		public ConnectionButton(EventModel ev, boolean type, InstanceModel m){
			super();
			_m = m;
			_e = ev;
			this.setName("connection");
			this.setPreferredSize(new Dimension(20,20));
			this.setSize(new Dimension(20,20));
			this.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					_self.setBackground(Color.GREEN);
					_panel.pressed(_model, ev, _self, type);

				}
			});
		}
		/**
		 * Returns the Instancemodel of the instance view 
		 * where this button is placed on.
		 * @return the instancemodel
		 */
		public InstanceModel getInstanceModel(){
			return _m;
		}
		
		/**
		 * Returns the event of the button.
		 * @return
		 */
		public EventModel getEventModel(){
			return _e;
		}
		
	}
	
	@Override
	public void setLocation(int x, int y) {
		super.setLocation(x, y);
		_model.setPos(new Point(x,y));
	}
	
	/**
	 * if pushed on the name of the input event the connection button for the handlers
	 *  is switch form left to right and vica versa.
	 * @param in, button being switched.
	 * @return
	 */
	private JButton addSwitchEventButton(JButton in, EventModel ev){
		JButton out = new JButton(ev.getType());
		out.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(out.getParent().getComponentOrientation() ==  ComponentOrientation.LEFT_TO_RIGHT){
					out.getParent().setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);				
				}else{
					out.getParent().setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);					
				}
				out.getParent().revalidate();
				out.getParent().repaint();
				_panel.repaint();				
			}
		});
		return out;
	}
	
	/**
	 * Removes given input Event 
	 * @param ev Input event to be removed.
	 */
	private void removeInputEvent(EventModel ev) {
		String rem = ev.getType();
		removeInputEvent( rem) ;
	}
	/**
	 * Removes an inputEvent with given name.
	 * @param rem name of the inputEvent to be removed.
	 */
	private void removeInputEvent(String rem) {
		
		JPanel b = _inputEvents.get(rem);
		_inputEvents.remove(rem);
		_panel.removeInputEvent(searchButton(b.getComponents()));
		_inputs.remove(b);
		_inputs.setBounds(_inputs.getBounds().x, _inputs.getBounds().y, _inputs.getBounds().width, _inputs.getBounds().height - b.getHeight());
		this.setBounds(this.getBounds().x, this.getBounds().y, this.getBounds().width, this.getBounds().height - b.getHeight());
		this.revalidate();
		this.repaint();
	}
	/**
	 * Searches for the connection button in a given list of components. 
	 * @param comps components to be searched.
	 * @return null if not found else the button
	 */
	private JButton searchButton(Component[] comps){
		for (Component component : comps) {
			if(component.getName() != null && component.getName().equals("con"))
				return (JButton) component;
		}
		return null;
	}
	
	/**
	 * Removes an output event ev
	 * @param ev the output event to be removed.
	 */
	private void removeOutputEvent(EventModel ev) {
		String rem = ev.getType();
		removeOutputEvent(rem);
	}
	
	/**
	 * Removes an output event with the given name [rem].
	 * @param rem name of the output event to be removed.
	 */
	private void removeOutputEvent(String rem) {
		JPanel b = _outputEvents.get(rem);
		if (b != null) {
			_outputEvents.remove(rem);
			_panel.removeOutputEvent(searchButton(b.getComponents()));
			_outputs.remove(b);
			_outputs.setBounds(_outputs.getBounds().x, _outputs.getBounds().y, _outputs.getBounds().width, _outputs.getBounds().height - b.getHeight());
			this.setBounds(this.getBounds().x, this.getBounds().y, this.getBounds().width, this.getBounds().height - b.getHeight());
		}
		this.revalidate();
		this.repaint();
	}

	/**
	 * Makes the instance view dragable in the wirepanel.
	 */
	private void makeDragable(){
		this.addMouseListener(new MouseAdapter(){
			// When we press the mouse we start updating the mouse coordinates
			public void mousePressed(MouseEvent e){
				_t.getParent().setComponentZOrder(_t, 0);

				oldMouseX = getX() - e.getXOnScreen();
				oldMouseY = getY() - e.getYOnScreen();
			}
			
			public void mouseClicked(MouseEvent e) {
            	if (SwingUtilities.isRightMouseButton(e)) {
					PopUpClick menu = new PopUpClick();
					menu.show(e.getComponent(), e.getX(), e.getY());
            	}
			}
		});

		this.addMouseMotionListener(new MouseAdapter(){
			public void mouseDragged(MouseEvent e){
				// If we drag the mouse we need to update our location
				_panel.setUnselected();
				setLocation(e.getXOnScreen() + oldMouseX, e.getYOnScreen() + oldMouseY);
				_panel.repaint();
				
			}
		});
	}

	@Override
	public void update(Observable o, Object arg) {
		if(o.getClass() == LanguageModule.class)
			updateLang();
		else if(arg != null){
			if (!(arg instanceof Object[])) return;
			if(((String)((Object[]) arg)[0]).equals("addInput")){
				addInputEvent(((EventModel)((Object[]) arg)[1]));
			}
			else if(((String)((Object[]) arg)[0]).equals("deleteInput")){
				removeInputEvent(((EventModel)((Object[]) arg)[1]));
			}
			else if(((String)((Object[]) arg)[0]).equals("addOutput")){
				addOutputEvent(((EventModel)((Object[]) arg)[1]));
			}
			else if(((String)((Object[]) arg)[0]).equals("deleteOutput")){
				removeOutputEvent(((EventModel)((Object[]) arg)[1]));
			}
		}
	}
	
	/**
	 * Updates components text to the set language.
	 */
	private void updateLang() {
		_inputs.setBorder(BorderFactory.createTitledBorder(_lang.getString("inputEvents")));
		_outputs.setBorder(BorderFactory.createTitledBorder(_lang.getString("OutputEvents")));
	}

	/**
	 * Get the button on the correct panel with the correct event.
	 * @param ev name of event.
	 * @param input if input or output event.
	 * @return the button the button if found else null.
	 */
	public JButton getButton(String ev, boolean input) {
		JPanel p;
		if (input) {
			p = _inputEvents.get(ev);
		} else {
			p = _outputEvents.get(ev);
		}
		if (p == null) return null;

		return searchButton(p.getComponents());
	}
	
	/**
	 * Check if this instance is an element from the given class
	 * @param cls classname
	 * @return true if instance of given class, else false.
	 */
	public boolean fromClass(String cls) {
		return _model.getClassName().equals(cls);
	}
	
	/**
	 * Remove this instance
	 */
	public InstanceModel getModel() {
		return _model;		
	}
	
	/**
	 * removes all the wires from this instance and then removes this instance itself from the wirepanel.
	 */
	private void removeThisInstance() {
		while(_inputEvents.keySet().size() != 0){
			removeInputEvent((String)_inputEvents.keySet().toArray()[0]);
		}
		while(_outputEvents.keySet().size() != 0){
			removeOutputEvent((String)_outputEvents.keySet().toArray()[0]);
		}
		
		_panel.removeInstance(this);
		
	}
	
	/**
	 * Used as popup on blocks
	 * @author axel
	 *
	 */
	class PopUpClick extends JPopupMenu {
		private static final long serialVersionUID = 1L;
		JMenuItem _remove;
		JMenuItem _deselect;

		public PopUpClick(){
			addRemoveItem();
			
		}
		private void addRemoveItem() {
			_remove = new JMenuItem(_lang.getString("removeBlock"));
			_remove.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					removeThisInstance();
				}

			});
			add(_remove);
		}
	}
}

