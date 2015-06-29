package ide.frontend.classes;

import ide.backend.language.LanguageModule;
import ide.backend.model.event.EventModel;
import ide.frontend.mvcbase.Controller;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * The class block in which input events can be added
 * @author Matthijs && axel
 *
 */
public class ClassInputEventsView extends JPanel implements Observer{
	private static final long serialVersionUID = 1L;
	
	/*FIELDS*/
	private Observable _model;
	private Controller _controller;
	private ClassModelController _classController;
	private JComboBox<String> _eventSelector;
	private HashMap<String, EventModel> _eventModels;
	private HashMap<EventModel, JButton> _eventButtons;
	private ClassCreationModel _creationModel;
	private LanguageModule _lang;
	private IDEPanel _panel;
	
	private JButton _addButton;
	private ClassInputEventsView h;
	private int oldMouseX;
	private int oldMouseY;

	public ClassInputEventsView(Observable c, ClassCreationModel m,  Controller controller, LanguageModule lang, IDEPanel panel) {
		initFields(c ,m, controller, lang, panel);
		initComponents();
		//this.setSize(new Dimension(100,200));dummy.add(out);

		this.setBounds(10, 10, 170, 100);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		this.setBorder(BorderFactory.createTitledBorder(_lang.getString("inpEvent")));
		h = this;
		makeDragable();
	}
	
	/**
	 * Make the view draggable
	 */
	private void makeDragable(){
		this.addMouseListener(new MouseAdapter(){
			// When we press the mouse we start updating the mouse coordinates
			public void mousePressed(MouseEvent e){
				oldMouseX = getX() - e.getXOnScreen();
				oldMouseY = getY() - e.getYOnScreen();
				h.getParent().setComponentZOrder(h, 0);
			}
		});
		this.addMouseMotionListener(new MouseAdapter(){
			public void mouseDragged(MouseEvent e){
				// If we drag the mouse we need to update our location
				setLocation(e.getXOnScreen() + oldMouseX, e.getYOnScreen() + oldMouseY);
				_panel.repaint();
			}
		});

	}
	
	/**
	 * Initiate the components
	 */
	private void initComponents() {
		initAddInputEvents();
	}

	/**
	 * Initialise the fields
	 * @param model, class model
	 * @param m, model of the class creator
	 * @param controller
	 * @param lang, the language module
	 * @param panel, the panel on which this view lays
	 */
	private void  initFields(Observable model, ClassCreationModel m, Controller controller, LanguageModule lang, IDEPanel panel){
		_model = model;
		_model.addObserver(this);
		_creationModel = m;
		_creationModel.addObserver(this);
		if(controller != null)
			_controller = controller;
		_classController = new ClassModelController(model);
		_lang = lang;
		_lang.addObserver(this );
		_panel = panel;
		_eventModels = new HashMap<String, EventModel>();
		_eventButtons = new HashMap<>();
	}
	
	/**
	 * Initialise the input events
	 */
	private void initAddInputEvents() {
		_eventSelector = new JComboBox<String>();
		this.add(_eventSelector);
		updateAvailableEvents();
		initAddButton();

	}
	
	/**
	 * Add a button to add input events
	 */
	private void initAddButton() {
		_addButton = new JButton(_lang.getString("addEventToClass"));
		_addButton.setAlignmentX(CENTER_ALIGNMENT);
		this.add(_addButton);
		_addButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				EventModel ev = _eventModels.get((String)_eventSelector.getSelectedItem());
				if (!_classController.getInputEvents().contains(ev)) {
					addInputEventPanel(ev);
					_classController.addInputEvent(ev);
				}
			}

		});
	}
	
	private void addInputEventPanel(EventModel ev) {
		JPanel dummy = new JPanel();
		dummy.setLayout(new FlowLayout());
		dummy.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		
		JButton out = addEventButton(ev);
		out.setPreferredSize(new Dimension(20,20));
		out.setSize(new Dimension(20,20));
		_eventButtons.put(ev, out);
		
		JButton switcher = addSwitchEventButton(out, ev.getType());
		switcher.setPreferredSize(new Dimension(80,30));
		switcher.setSize(new Dimension(80,30));
		dummy.add(switcher);

		dummy.add(new RemButton(dummy, out, ev.getType(), 22));
		dummy.add(out);
		dummy.setPreferredSize(new Dimension(120,35));
		dummy.setSize(new Dimension(120,35));

		h.add(dummy);
		h.setBounds(h.getBounds().x, h.getBounds().y, h.getBounds().width, h.getBounds().height + dummy.getHeight());
		h.revalidate();
		h.repaint();	
	}
	
	private class RemButton extends JButton implements ActionListener {
		private static final long serialVersionUID = 1L;
		private JPanel _panel;
		private String _event;
		private JButton _b;
		public RemButton(JPanel panel, JButton b, String event, int size) {
			b = _b;
			_panel = panel;
			_event = event;
			///ide remove button from lines with b;
		
            setPreferredSize(new Dimension(size, size));
            //Make the button looks the same for all Laf's
            setUI(new BasicButtonUI());
            //Make it transparent
            setContentAreaFilled(false);
            //No need to be focusable
            setFocusable(false);
            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
            //Making nice rollover effect
            //we use the same listener for all buttons
            addMouseListener(buttonMouseListener);
            setRolloverEnabled(true);
            //Close the proper tab by clicking the button
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            h.remove(_panel);
            h.setBounds(h.getBounds().x, h.getBounds().y, h.getBounds().width, h.getBounds().height - _panel.getHeight());
            h.revalidate();
            h.repaint();
            ClassInputEventsView.this._panel.removeInputEvent(_eventButtons.get(_eventModels.get(_event)));
            _classController.removeInputEvent(_eventModels.get(_event));
        }

        //we don't want to update UI for this button
        public void updateUI() {
        }

        //paint the cross
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            //shift the image for pressed buttons
            if (getModel().isPressed()) {
                g2.translate(1, 1);
            }
            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.BLACK);
            if (getModel().isRollover()) {
                g2.setColor(Color.MAGENTA);
            }
            int delta = 6;
            g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
            g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
            g2.dispose();
        }
    }
	
	private final static MouseListener buttonMouseListener = new MouseAdapter() {
        public void mouseEntered(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(true);
            }
        }

        public void mouseExited(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(false);
            }
        }
    };
    
    /**
     * Returns the button associated with the given EventModel.
     * @param e the event model
     * @return the corresponding button
     */
    public JButton getEventButton(EventModel e){
    	return _eventButtons.get(e);
    }
	
	/**
	 * Create a new event button
	 * @return 
	 */
	private JButton addEventButton(EventModel ev) {
		JButton out = new JButton();
		out.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				out.setBackground(Color.GREEN);
				_panel.pressedButton(out, ev);
			}
		});
		return out;
	}
	
	/**
	 * if pushed on the name of the input event the connection button for the handlers
	 *  is switch form left to right and vica versa.
	 * @param in, button being switched.
	 * @return
	 */
	private JButton addSwitchEventButton(JButton in, String eventName){
		JButton out = new JButton(eventName);
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
	 * Add an event
	 * @param event
	 */
	private void addEvent(EventModel event){
		_eventSelector.addItem(event.getType());
		_eventModels.put(event.getType(), event);
	}
	
	/**
	 * Removes an event
	 * @param event
	 */
	private void removeEvent(EventModel event){
		
		if(_eventButtons.get(event) != null){
			ClassInputEventsView.this._panel.removeInputEvent(_eventButtons.get(event));
	        _classController.removeInputEvent(event);
			JButton b = _eventButtons.get(event);
			_eventButtons.remove(event);
			JPanel dummy = (JPanel) b.getParent();
			this.remove(dummy);
			h.setBounds(h.getBounds().x, h.getBounds().y, h.getBounds().width, h.getBounds().height - dummy.getHeight());
			
		}
		
		_eventSelector.removeItem(event.getType());
		_eventModels.remove(event.getType());
	}
	
	/**
	 * Update all events that are shown
	 */
	private void updateAvailableEvents(){
		ArrayList<EventModel> events = ((ClassCreationController)getController()).getEvents();
		for (EventModel event : events) {
			_eventSelector.addItem(event.getType());
			_eventModels.put(event.getType(), event);
		}
	}
	
	private Controller getController(){
		if(_controller != null)
			return _controller;
		else
			return _controller = new ClassCreationController(_creationModel);
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o == null) {
			updateTotalView();
		} else if(o.getClass() == LanguageModule.class)
			updateLang();
		else if(arg != null){
			if (! (arg instanceof Object[])) return;
			
			if(((String)((Object[]) arg)[0]).equals("addEvent")){
				addEvent(((EventModel)((Object[]) arg)[1]));
				_panel.addEvent();
			}
			else if(((String)((Object[]) arg)[0]).equals("deleteEvent")){
				removeEvent(((EventModel)((Object[]) arg)[1]));
				_panel.deleteEvent(((EventModel)((Object[]) arg)[1]).getType());
			}
		} else{
			updateTotalView();
		}
		
	}

	/**
	 * Update everything
	 */
	private void updateTotalView() {
		_eventModels.clear();
		_eventSelector.removeAllItems();
		updateAvailableEvents();
		
		for(EventModel ev: _classController.getInputEvents()) {
			addInputEventPanel(ev);
		}
	}

	/**
	 * Update the language module
	 */
	private void updateLang() {
		_addButton.setText(_lang.getString("addEventToClass"));
		
	}
}
