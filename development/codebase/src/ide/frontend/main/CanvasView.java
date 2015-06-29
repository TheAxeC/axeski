package ide.frontend.main;

import ide.backend.language.LanguageModule;
import ide.backend.runtime.EventCatcher;
import ide.frontend.canvas.SpriteView;
import ide.frontend.mvcbase.AbstractView;
import ide.frontend.mvcbase.Controller;
import ide.frontend.wire.InstanceView;
import ide.frontend.wire.WireCreationView;
import ide.frontend.wire.WirePanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Observable;

import javax.swing.JPanel;

/**
 * Shows the canvas where the user can click on instances
 * @author axel
 *
 */
public class CanvasView extends AbstractView {
	private static final int AXIS_LENGTH = Integer.MAX_VALUE;
	
	private JPanel _main;

	// Holds mouse coordinates
	private int oldMouseX;
	private int oldMouseY;
	private boolean mouseReleased;
	private Point _origin;
	
	private ArrayList<SpriteView> _instances;
	
	/**
	 * Shows the canvas where the user can click on instances
	 * @param model the model to observe
	 * @param controller the controller to use (null for default)
	 * @param lang, the language module
	 * @param wireFrame, the wirecreationview.
	 * 					The canvas needs to update when the wireframe updates
	 */
	public CanvasView(Observable model, Controller controller, LanguageModule lang, WireCreationView wireFrame) {
		super(model, controller);
		wireFrame.registerObserver(this);

		lang.addObserver(this);
		
		_origin = new Point(0,0);
		_instances = new ArrayList<>();
		initPanels();
		addMouseMotionListener();
		addingMouseListener();
		addingKeyListener();
	}
	
	/**
	 * Initialise the panels
	 */
	private void initPanels(){
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
		_main.setFocusable( true );
		_main.setLayout(null);
	}
	
	/**
	 * Get the ui from this element
	 * @return the ui
	 */
	public JPanel getUI(){
		return _main;
	}
	
	@Override
    public Controller defaultController(Observable model) {
        return null;
    }

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof WirePanel)
			makeSprites(((WirePanel) o));
	}
	
	/**
	 * Creates all sprites on the canvas
	 * @param wireframe
	 */
	private void makeSprites(WirePanel wireframe) {
		_main.removeAll();
		_instances.clear();
		for(InstanceView view: wireframe.getInstances()) {
			SpriteView sprite = new SpriteView(view.getModel());
			_instances.add(sprite);
			_main.add(sprite);
		}
		_main.revalidate();
		_main.repaint();
	}
	
	/**
	 * Add a mouse motion listener
	 */
	private void addMouseMotionListener() {
		_main.addMouseMotionListener(new MouseAdapter(){
            public void mouseDragged(MouseEvent e){
            	// Scroll the pane
        		for(SpriteView v: _instances) {
        			v.setLocation(v.getX() + e.getXOnScreen() - oldMouseX , v.getY() + e.getYOnScreen() - oldMouseY);
        		}

        		_origin.setLocation(_origin.getX() + e.getXOnScreen() - oldMouseX , _origin.getY() + e.getYOnScreen() - oldMouseY);
        		oldMouseX = e.getXOnScreen();
                oldMouseY = e.getYOnScreen();
                _main.repaint();
            }
        });
	}
	
	/**
	 * Move the coordinate system of the canvas view back to the origin
	 */
	public void moveToOrigin() {
		for(SpriteView v: _instances) {
			v.setLocation((int) (v.getX() - _origin.getX()) , (int) (v.getY() - _origin.getY()));
		}

		_origin.setLocation(0, 0);
        _main.repaint();
	}
	
	/**
	 * Add a mouse listener
	 */
	private void addingMouseListener() {
		_main.addMouseListener(new MouseAdapter(){
			// If we release the mouse
            public void mouseReleased(MouseEvent e){
            	mouseReleased = false;
            	_main.repaint();
            }
            
            // When we press the mouse we start updating the mouse coordinates
            public void mousePressed(MouseEvent e){
            	if (!mouseReleased) {
	                oldMouseX = e.getXOnScreen();
	                oldMouseY = e.getYOnScreen();
	                mouseReleased = true;
            	}
            }

        });
	}
	
	/**
	 * Adds a key listener
	 */
	private void addingKeyListener() {
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new MyDispatcher());
	}
	
	/**
	 * Catches all keyevents
	 * @author axel
	 *
	 */
	public class MyDispatcher implements KeyEventDispatcher {
	    @Override
	    public boolean dispatchKeyEvent(KeyEvent e) {
	        if (!e.isConsumed()) {
	        	int key = e.getKeyCode();
	            processMyStuff(e, key);
	        }
	        return false;
	    }

	    protected void processMyStuff(KeyEvent e, int key) {
	    	if (e.getID() == KeyEvent.KEY_RELEASED) {
	    		setChanged();
	    		notifyObservers(EventCatcher.getEvent(key));
	    	}
	    }
	}
	
}













