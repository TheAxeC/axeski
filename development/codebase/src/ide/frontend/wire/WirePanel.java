package ide.frontend.wire;

import ide.backend.language.LanguageModule;
import ide.backend.model.classes.InstanceModel;
import ide.backend.model.event.EventModel;
import ide.frontend.wire.InstanceView.ConnectionButton;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 * The panel on which instanceViews are placed and connection between intstances
 * are created.
 * @author Matthijs Kaminski
 *
 */
public class WirePanel extends Observable implements Observer {
	private static final int AXIS_LENGTH = Integer.MAX_VALUE;
	
	/**
	 * FIELDS
	 */
	
	//fields for creating connections
	private InstanceModel _pressedInstanceOut ;
	private InstanceModel _pressedInstanceIn ;
	private EventModel _pressedEventModel;
	private JButton _pressedButtonOut;
	private JButton _pressedButtonIn;
	
	//layer panel for difference lines and views.
	private JLayeredPane _layers;
	//main panel of the wirepanel.
	private JPanel _main;
	//panel on which lines are drawn.
	private JPanel _linePanel;
	//wirecreation view in which this view is placed.
	private WireCreationView _v;
	//default linecolor.
	private Color _lineColor;
	//current selected wire
	private JButton[] _selected;

	// map output on inputs.
	HashMap<JButton, ArrayList<JButton>> _buttonmap;
	// lists of all instances.
	ArrayList<InstanceView> _instances;
	
	// Holds mouse coordinates for dragging
	private int oldMouseX;
	private int oldMouseY;
	private boolean mouseReleased;

	private Point _origin;
	//language module of the program.
	private LanguageModule _lang;
	
	/**
	 * Create a new WirePanel.
	 * @param v WireCreationView on which this panel is added.
	 * @param lang language module of the program.
	 */
	public WirePanel(WireCreationView v, LanguageModule lang) {
		_lang = lang;
		_lang.addObserver(this);
		_lineColor = new Color(89, 89, 89);
		_v=v;
		
		_instances = new ArrayList<>();
	
		_buttonmap = new HashMap<JButton, ArrayList<JButton>>();
		
		_layers = new JLayeredPane();
		
		initMainPanel();
		
		_layers.add(_main, new Integer(1));
		_linePanel = initLinePanel();
		
		_linePanel.setOpaque(false);
		_linePanel.setBounds(0, 0, 2000, 2000);
		_layers.add(_linePanel, new Integer(2));
		
		_origin = new Point(0,0);
		addingMouseListener();
		addMouseMotionListener();
	}


	private void initMainPanel() {
		_main = new JPanel(){
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g.create();
				
				g2d.setColor(Color.RED);
				g2d.drawLine((int)_origin.getX(), -AXIS_LENGTH, (int) _origin.getX(), AXIS_LENGTH);
				g2d.setColor(Color.BLUE);
				g2d.drawLine(-AXIS_LENGTH, (int)_origin.getY(), AXIS_LENGTH, (int)_origin.getY());
				g2d.setColor(Color.BLACK);
				
				g2d.dispose();
			}
		};
		_main.setBounds(0, 0, 2000, 2000);
		_main.setLayout(null);
	}


	/**
	 * Create a panel the draw the connections on.
	 * @return created panel
	 */
	private JPanel initLinePanel() {
		return new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				//all events that shouldn't be drawn.
				HashSet<EventModel> unchecked = _v.getUnchecked();
				Graphics2D g2d = (Graphics2D) g.create();
				// create new QuadCurve2D.Float
				g2d.setRenderingHint(
						RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setStroke(new BasicStroke(5));
				g2d.setPaint(_lineColor);
				JButton a;
				ArrayList<JButton> b;
				for (HashMap.Entry<JButton, ArrayList<JButton>> entry : _buttonmap.entrySet()) {
					a = entry.getKey();
					b = entry.getValue();
					//check is should be drawn.
					if(!unchecked.contains(((ConnectionButton) a).getEventModel())){
						for (JButton j : b) {
							QuadCurve2D q = calcCurve(a, j);
							g2d.draw(q);
						}
					}
				}
				
				if(_selected != null){
					g2d.setPaint(Color.RED);
					QuadCurve2D q = calcCurve(_selected[0], _selected[1]);
					g2d.draw(q);
				}
			}
		};
	}
	
	/**
	 * Calculates the wire connecting an input and output event.
	 * @param a the output event button.
	 * @param j the input event button.
	 * @return calculated wire.
	 */
	private QuadCurve2D calcCurve(JButton a, JButton j){
		QuadCurve2D q = new QuadCurve2D.Float();
			Point p = a.getParent().getParent().getParent().getLocation();
			p.translate(a.getParent().getParent().getX(), a.getParent().getParent().getY());
			p.translate(a.getParent().getX(), a.getParent().getY());
			p.translate(a.getX(), a.getY());
			p.translate(a.getWidth()/2, a.getHeight()/2);
			Point p2 = j.getParent().getParent().getParent().getLocation();
			p2.translate(j.getParent().getParent().getX(), j.getParent().getParent().getY());
			p2.translate(j.getParent().getX(), j.getParent().getY());
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
	 * Return the ui of the wire panel.
	 * @return
	 */
	public JLayeredPane getUI(){
		return _layers;
	}
	
	/**
	 * Add a component to the view
	 * @param c the component being added.
	 */
	public void add(JComponent c){
		_main.add(c);
	}

	/**
	 * 
	 * @param model, the model that is pressed
	 * @param e, the event model of the connection
	 * @param b, the button that is pressed
	 * @param type, the type of the connection
	 */
	public void pressed(InstanceModel model, EventModel e, JButton b, boolean type) {
		//input
		if(type){
			if(_pressedInstanceIn != null){
				_pressedButtonIn.setBackground(new JButton().getBackground());
			}
			if(_pressedEventModel == null || _pressedInstanceIn != null){
				_pressedEventModel = e;
				_pressedButtonIn = b;
				_pressedInstanceIn = model;
			}else if(_pressedEventModel == e){
				_pressedInstanceIn = model;
				_pressedButtonIn =b;
			}else{
				b.setBackground(new JButton().getBackground());
				_pressedButtonOut.setBackground(new JButton().getBackground());
				_pressedEventModel = null;
				_pressedInstanceIn = null;
				_pressedInstanceOut = null;
				
			}
		}else{
			if(_pressedInstanceOut != null){
				_pressedButtonOut.setBackground(new JButton().getBackground());
			}
			if(_pressedEventModel == null || _pressedInstanceOut != null){
				_pressedEventModel = e;
				_pressedButtonOut = b;
				_pressedInstanceOut = model;
			}else if(_pressedEventModel == e){
				_pressedInstanceOut = model;
				_pressedButtonOut =b;
			}else{
				_pressedButtonIn.setBackground(new JButton().getBackground());
				b.setBackground(new JButton().getBackground());
				_pressedEventModel = null;
				_pressedInstanceIn = null;
				_pressedInstanceOut = null;
			}
		}
		
		if(_pressedInstanceIn != null && _pressedInstanceOut != null)
			addWire();		
	}
	
	/**
	 * Add a connection is made correctly.
	 * Connections are always saved from output event to input event. (one-many)
	 */
	private void addWire() {
		//check if output event already has a connection.
		if(_buttonmap.containsKey(_pressedButtonOut)){
			//add input event to outputs connections
			_buttonmap.get(_pressedButtonOut).add(_pressedButtonIn);
		}else{
			//create a connection.
			ArrayList<JButton> in = new ArrayList<JButton>();
			//add input events to the new connections of the output event.
			in.add(_pressedButtonIn);
			_buttonmap.put(_pressedButtonOut,in);
		}
		//add the connection in the models.
		_v.addWire(_pressedInstanceOut,_pressedEventModel,_pressedInstanceIn);
		//reset the buttons.
		_pressedButtonIn.setBackground(new JButton().getBackground());
		_pressedButtonOut.setBackground(new JButton().getBackground());
		_pressedEventModel = null;
		_pressedInstanceIn = null;
		_pressedInstanceOut = null;
		repaint();
	}
	
	/**
	 * Get all instances
	 * @return
	 */
	public ArrayList<InstanceView> getInstances() {
		return _instances;
	}
	
	/**
	 * Adds a wire that already exists
	 * @param from
	 * @param to
	 */
	public void addExistingWire(String from, String ev, String to) {
		JButton out = getButton(from, ev, false);
		JButton in = getButton(to, ev, true);
		if(_buttonmap.containsKey(out)){
			_buttonmap.get(out).add(in);
		}else{
			ArrayList<JButton> ins = new ArrayList<JButton>();
			ins.add(in);
			_buttonmap.put(out, ins);
		}
		repaint();
	}


	/**
	 * Repaints the total view (instances + wires)
	 */
	public void repaint() {
		_main.repaint();
		_linePanel.repaint();
	}


	/**
	 * adds a new instance to the panel and its model to the collection
	 * @param v instance view being added to the panel.
	 * @param i instance model being added to the collection.
	 */
	public void add(InstanceView v, InstanceModel i) {
		_instances.add(v);
		add(v);
		if (i != null)
			_v.addInstance(i);
		this.setChanged();
		this.notifyObservers();
	}


	/**
	 * Clear the total panel.
	 */
	public void clear() {
		_instances.clear();
		_buttonmap.clear();
		_linePanel.repaint();
		_main.removeAll();
		this.setChanged();
		this.notifyObservers();
	}
	
	/**
	 * Get the button representing the event on the instance.
	 * @param name name of the instance
	 * @param ev name of the event
	 * @param input whether the event is an input event or an output event.
	 * @return the button
	 */
	private JButton getButton(String name, String ev, boolean input) {
		InstanceView view = getInstance(name);
		return view.getButton(ev, input);
	}
	
	/**
	 * Find the instance with name [name]
	 * @param name
	 * @return
	 */
	private InstanceView getInstance(String name) {	
		for(InstanceView v: _instances)
			if (v.getName().equals(name))
				return v;
		
		return null;
	}
	
	/**
	 * Get a list of all instance names
	 * @return
	 */
	public ArrayList<String> getInstanceNames() {
		ArrayList<String> ret = new ArrayList<>();
		
		for(InstanceView v: _instances)
			ret.add(v.getName());
		
		return ret;
	}
	
	/**
	 * Remove all instances from the given class
	 * @param mdl
	 */
	public void removeInstances(String cls) {
		ArrayList<InstanceView> toRem = new ArrayList<>();
		for(InstanceView view: _instances) {
			if (view.fromClass(cls)) {
				toRem.add(view);
				_v.removeInstance(view.getModel());
				_main.remove(view);
				
				if (view.getModel() == _pressedInstanceOut) {
					_pressedInstanceOut = null;
					_pressedButtonIn = null;
				} else if (view.getModel() == _pressedInstanceIn) {
					_pressedInstanceOut = null;
					_pressedButtonOut = null;
				}				
			}
		}
		_instances.removeAll(toRem);
		repaint();
		this.setChanged();
		this.notifyObservers();
	}

	/**
	 * Add a mouse motion listener
	 * @param p
	 */
	private void addMouseMotionListener() {
		_main.addMouseMotionListener(new MouseAdapter(){
            public void mouseDragged(MouseEvent e){
            	// Scroll the pane
        		for(InstanceView v: _instances) {
        			v.setLocation(v.getX() + e.getXOnScreen() - oldMouseX , v.getY() + e.getYOnScreen() - oldMouseY);
        		}

        		_origin.setLocation(_origin.getX() + e.getXOnScreen() - oldMouseX , _origin.getY() + e.getYOnScreen() - oldMouseY);
        		oldMouseX = e.getXOnScreen();
                oldMouseY = e.getYOnScreen();
        		repaint();
            }
        });
	}
	
	/**
	 * Add a mouse listener
	 * @param p
	 */
	private void addingMouseListener() {
		_main.addMouseListener(new MouseAdapter(){
			// If we release the mouse
			// We either release it when we hold an unsnapped block
			// or when we where moving ourselves
            public void mouseReleased(MouseEvent e){
            	mouseReleased = false;
            	repaint();
            }
            
            // When we press the mouse we start updating the mouse coordinates
            // Possible we also want to unsnap a block, that may be nested
            public void mousePressed(MouseEvent e){
            	if (!mouseReleased) {
	                oldMouseX = e.getXOnScreen();
	                oldMouseY = e.getYOnScreen();
	                mouseReleased = true;
            	}
            }
            
            public void mouseClicked(MouseEvent e) {
            	if (SwingUtilities.isRightMouseButton(e)) {
					PopUpClick menu = new PopUpClick();
					menu.show(e.getComponent(), e.getX(), e.getY());
				} else{
	            	if (_v.getLoaded() != null) {
						_v.getLoaded().createInstance(e.getX(), e.getY());
	            	}else{
	            		checkWireClicked(e.getX(), e.getY());
	            	}
				}
            }

        });
	}
	
	/**
	 * Check whether a wire was click when clicking on the wire panel. 
	 * @param x x coordinaat of the mouse.
	 * @param y y coordinaat of the mouse.
	 */
	private void checkWireClicked(int x, int y) {
		//get which event shuld not be drawn
		HashSet<EventModel> unchecked = _v.getUnchecked();
		_selected = null;
		for (HashMap.Entry<JButton, ArrayList<JButton>> entry : _buttonmap.entrySet()) {
			JButton a = entry.getKey();
			ArrayList<JButton> b = entry.getValue();
			if(!unchecked.contains(((ConnectionButton) a).getEventModel())){
				for (JButton j : b) {
					//calculate the wire
					QuadCurve2D q = calcCurve(a, j);
					// if wire contains click
					if(q.contains(x, y-20)){
						//set wire selected.
						_selected = new JButton[2];
						_selected[0] = a;
						_selected[1] = j;
						//repaint the wires.
						_linePanel.repaint();
						//only one wire can be clicked.
						return;
					}
				}
			}
		}
		
	}
	
	/**
	 * Remove a wire between to given buttons.
	 * @param output button 
	 * @param input button
	 */
	private void removeWire(JButton output, JButton input){
		_buttonmap.get(output).remove(input);
		_v.removeWire(((ConnectionButton)output).getInstanceModel(), ((ConnectionButton)output).getEventModel(), ((ConnectionButton)input).getInstanceModel());
		_linePanel.repaint();
	}
	

	/**
	 * Remove the selected wire if one is selected.
	 */
	private void removeSelectedWire() {
		if(_selected != null){
			removeWire(_selected[0], _selected[1]);
			_selected = null;
		}
	}


	/**
	 * unload the instance loader;
	 */
	public void setUnselected() {
		_v.unload();
		
	}
	
	public void removeInstance(InstanceView in){
		_main.remove(in);
		_v.removeInstance(in.getModel());
		_instances.remove(in);
		_selected = null;
		repaint();
		this.setChanged();
		this.notifyObservers();
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
			addDeselectItem();
		}
		
		private void addDeselectItem() {
			_deselect = new JMenuItem(_lang.getString("IDEdeselectBlock"));
			_deselect.addActionListener(new ActionListener() {
	
				@Override
				public void actionPerformed(ActionEvent e) {
					setUnselected();
				}
			});
			add(_deselect);
		}

		private void addRemoveItem() {
			_remove = new JMenuItem(_lang.getString("IDEremovewire"));
			_remove.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					removeSelectedWire();
				}

			});
			add(_remove);
		}
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		
	}

	/**
	 * remove an input event from the wires.
	 * @param input
	 */
	public void removeInputEvent(JButton input) {
		for (HashMap.Entry<JButton, ArrayList<JButton>> entry : _buttonmap.entrySet()) {
			JButton a = entry.getKey();
			ArrayList<JButton> b = entry.getValue();
			int i = 0;
			while(i < b.size()){
				if(b.get(i) == input){
					removeWire(a, b.get(i));
				}else{
					i++;
				}
			}
		}
	}
	/**
	 * remove an output event and it's wires.
	 * @param output
	 */
	public void removeOutputEvent(JButton output) {
		int i = 0;
		ArrayList<JButton> b  = _buttonmap.get(output);
		if(b != null){
			while (i < b.size()){
				removeWire(output, b.get(i));
			}
		}
		_buttonmap.remove(output);
	}


	/**
	 * Sets all instances to its origin
	 */
	public void moveToOrigin() {
		// Scroll the pane
		for(InstanceView v: _instances) {
			v.setLocation((int) (v.getX() - _origin.getX() ), (int) (v.getY() - _origin.getY()));
		}

		_origin.setLocation(0, 0);
		repaint();
	}
}
