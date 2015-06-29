package ide.frontend.classes.views;

import ide.backend.language.LanguageModule;
import ide.frontend.classes.IDEPanel;
import ide.frontend.mvcbase.Controller;
import ide.frontend.mvcbase.View;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JCheckBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 * @author Axel && Matthijs
 */
public abstract class BlockView extends JPanel implements View, Observer {
	private static final long serialVersionUID = 1L;
	
    private Observable _model;
    private Controller _controller;

	// Holds mouse coordinates
	private int oldMouseX;
	private int oldMouseY;
	private boolean mouseReleased;
	
	// Contains the unsnapped block
	private BlockView _unsnapped;
	
	// Main panel on which everything is drawn
	private IDEPanel _panel;
	
	// The parent view
	private BlockView _parent;
	
	// The language module
	protected LanguageModule _lang;
	
	// Components linked to their correct parents
	private HashMap< Component,BlockView> _bodycomps;
	private HashMap<Component, Point> _bodypos;
	
	// The shapes representing this block
	private ArrayList<Shape> _shapes;
	private ArrayList<Color> _colors;
	private ArrayList<Color> _outLineColors;
	
	private int _width;
	private int _height;

	/**
	 * Variables used to draw the view
	 */
	private boolean _transparant;
	private final float ALPHA = 0.7f;
	
	private static final Color BREAK_COLOR = Color.MAGENTA;
	private static final Color DEBUG_COLOR = Color.BLACK;
	
	private BlockView _self;
	private boolean _init;
	
	
	/**
	 * Main constructor
	 * @param model, model to observe
	 * @param controller, controller to use or null to use the default controller
	 * @param p, the IDEpanel this view will be placed onto
	 * @param lang, the languagemodule
	 */
	public BlockView(Observable model, Controller controller, IDEPanel p, LanguageModule lang) {
		// Set the model.
        setModel(model);
        // If a controller was supplied, use it. Otherwise let the first call to 
        // getController() create the default controller.
        if (controller != null) {
            setController(controller);
        }

		_panel = p;
		_lang = lang;
		_lang.addObserver(this);
		_self = this;
	}
	
	/**
	 * Used to set the position of this block
	 * @param x, the x position
	 * @param y, the y position
	 */
	public abstract void set(int x , int y);
	
	/**
	 * Update the language of this component
	 */
	public abstract void updateLanguage();
	
	/**
	 * Gets the most inner component at the given position [x, y]
	 * @param x, the x position
	 * @param y, the y position
	 * @return the most inner view
	 */
	 public abstract BlockView getSelectedComponent(int x, int y);
	
	/**
	 * Used to initialise the block
	 * @param startx, start position x
	 * @param starty start position y
	 * @param width the width of the view
	 * @param height the height of the view
	 */
	protected void init(int startx, int starty, int width, int height) {
		oldMouseX = 0;
		oldMouseY = 0;
		mouseReleased = false;
		
		_bodycomps = new HashMap<Component, BlockView>();
		_bodypos = new HashMap<Component, Point>();
		
		addingMouseListener();
		addMouseMotionListener();
		
		_unsnapped = null;
		_parent = null;
		
		_shapes  = new ArrayList<>();
		_colors = new ArrayList<>();
		_outLineColors = new ArrayList<>();
		
        setOpaque(false);
        setLayout(null);
        setBounds(startx, starty, width, height);
        setLocation(startx, starty);
        _width = width;
        _height = height;
        
        _transparant = false;
        
        initPanel();
        _init = true;
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if (_init) {
			if (o instanceof LanguageModule) {
				updateLanguage();
			} else {
				updateShapes();
				getPanel().repaint();
			}
		}
	}
	
	/**
	 * Check if this block is inited
	 * @return
	 */
	public boolean isInit() {
		return _init;
	}
	
	/**
	 * Makes the block transparent if [trans] is true.
	 * @param trans, true if the block needs to become transparant
	 */
	public abstract void makeTransparant(boolean trans);
	
	/**
	 * Set the transparancy of the view
	 * @param trans, true if transparancy needs to be set
	 */
	protected void setTransparency(boolean trans) {
		_transparant = trans;
	}
	
	/**
	 * Initiates the panel
	 */
	private void initPanel() {
		addComponents();	
		makeShapes();
		_panel.repaint();
	}
	
	/**
	 * Get the representation of this blockview
	 * To be reimplemented by each subclass
	 * @return the representative
	 */
	public static JPanel getRepresentative(LanguageModule lang){
		return new JPanel();
	}

	/**
	 * Set the unsnapped block
	 * @param view
	 */
	protected void setUnsnapped(BlockView view) {
		_unsnapped = view;
	}

	/**
	 * Add the components specific to this BlockView
	 * To be overriden by children, abstract function
	 */
	public abstract void addComponents();
	
	/**
	 * Add the components specific to this BlockView
	 * To be overriden by children, abstract function
	 */
	public abstract void makeShapes();
	
	/**
	 * Update the mouse coordinates
	 * @param x the x position
	 * @param y the y position
	 */
	protected void updateMouse(int x, int y) {
		oldMouseX += x;
		oldMouseY += y;
	}
	
	/**
	 * Get the old mouse positon
	 * @return x-position
	 */
	public int getMouseX() {
		return oldMouseX;
	}
	
	/**
	 * Get the old mouse positon
	 * @return y-position
	 */
	public int getMouseY() {
		return oldMouseY;
	}
	
	/**
	 * Get the IDE panel
	 * @return the IDEPanel this view is placed on
	 */
	public IDEPanel getPanel() {
		return _panel;
	}
	
	/**
	 * Add a new shape
	 * @param shape the shape to add
	 * @param foreground the border color
	 * @param background the background color or null to have transparant background
	 */
	public void addShape(Shape shape, Color foreground, Color background) {
		_shapes.add(shape);
		_colors.add(foreground);
		_outLineColors.add(background);
	}
	
	/**
	 * Get a shape at index [i]
	 * @param i index to access
	 * @return the correct shape
	 */
	protected Shape getShape(int i) {
		return _shapes.get(i);
	}
	
	/**
	 * Add the component to the blockview's panel
	 * @param t, the blockview
	 * @param comp, the component
	 */
	public void addToPanel(BlockView t, Component comp) {
		super.add(comp);
		
		if(!_bodycomps.containsKey(comp)){
			_bodycomps.put(comp ,t);
			_bodypos.put(comp, comp.getLocation());
		}
		else {
			comp.setLocation(_bodypos.get(comp));
		}	
	}
	
	
	/**
	 * Add the component if it wasnt already added
	 * @param t, the belonging view
	 * @param comp the component to add
	 */
	protected void addIfNotExisted(BlockView t, Component comp) {
		super.add(comp);
		
		if(!_bodycomps.containsKey(comp)){
			_bodycomps.put(comp ,t);
			_bodypos.put(comp, comp.getLocation());
		}
	}
	
	/**
	 * Get the width
	 * @return the width
	 */
	public int getWidthSize() {
		return _width;
	}
	
	/**
	 * Get the height
	 * @return the height
	 */
	public int getHeightSize() {
		return _height;
	}
	
	/**
	 * Set the width
	 * @param width the new widht
	 */
	public void setWidthSize(int width) {
		_width = width;
	}
	
	/**
	 * Set the height
	 * @param height the new height
	 */
	public void setHeightSize(int height) {
		_height = height;
	}
	
	/**
	 * Update the shapes that represent this block
	 */
	synchronized protected void updateShapes() {
		_shapes.clear();
		_colors.clear();
		_outLineColors.clear();
		makeShapes();
		
		setBreakShape();
		setDebugShape();
	}
	
	/**
	 * Set the debug shape
	 */
	private void setDebugShape() {
		boolean brk = ((AbstractBlockController) getController()).getDebugStatus();
		if (brk) {
			Rectangle rect = new Rectangle(0, 0, getWidthSize()-1, getHeightSize()-1);
			addShape(rect, DEBUG_COLOR, null);
		}
	}
	
	/**
	 * Set the break shape
	 */
	private void setBreakShape() {
		boolean brk = ((AbstractBlockController) getController()).getBreakStatus();
		if (brk) {
			Rectangle rect = new Rectangle(0, 0, getWidthSize()-1, getHeightSize()-1);
			addShape(rect, BREAK_COLOR, null);
		}
	}
	
	/**
	 * Get the position of the component
	 * @param comp the component
	 * @return the position
	 */
	protected Point getPosition(Component comp) {
		return _bodypos.get(comp);
	}
	
	/**
	 * Reset the positions of the components of the view
	 * @param view the view to reset
	 */
	protected void resetComponents(BlockView view) {
		for (Component cinner : view.getInnerComponents()) {
			Point p = view.getPosition(cinner);
			removeFromPanel(cinner);
			
			cinner.setLocation(p);
			view.addToPanel(view, cinner);
		}
	}

	/**
	 * Add a mouse motion listener
	 */
	private void addMouseMotionListener() {
		this.addMouseMotionListener(new MouseAdapter(){
            public void mouseDragged(MouseEvent e){
            	// If we drag the mouse
            	// We need to move the panel aswell as the (possible) unsnapped block
            	
            	if (_unsnapped != null) {
            		_unsnapped.setLocation(e.getXOnScreen() + oldMouseX, e.getYOnScreen() + oldMouseY);
            		if (_unsnapped.getParent() != null)
            			_unsnapped.getParent().setComponentZOrder(_unsnapped, 0);
            		else
            			_unsnapped.setComponentZOrder(_unsnapped, 0);
                	_panel.repaint();
            		return;
            	}
            	setLocation(e.getXOnScreen() + oldMouseX, e.getYOnScreen() + oldMouseY);
            	_panel.repaint();
            }
        });
	}
	
	/**
	 * The String needed to be displayed in the help menu
	 * @return the help menu
	 */
	public abstract String getHelpMenu();
	
	/**
	 * Used as popup on blocks
	 * @author axel
	 *
	 */
	class PopUpClick extends JPopupMenu implements Observer {
		private static final long serialVersionUID = 1L;
		private JMenuItem _remove;
		private JCheckBox _break;
		private JMenuItem _help;
		
		private Point _point;
	    
		/**
		 * The position where the user clicked
		 * @param x
		 * @param y
		 */
	    public PopUpClick(int x, int y){
	    	_point = new Point(x, y);
	    	addRemoveItem();
	    	addSetBreak();
	    	addHelpMenu();
	    }
	    
	    /**
	     * Add the help menu
	     */
	    private void addHelpMenu() {
	    	_help = new JMenuItem(_lang.getString("help"));
	    	_help.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					//default title and icon
					if (((AbstractBlockController)getController()).acceptSingle(_self)) {
						BlockView view = getSelectedComponent((int)_point.getX() + _self.getX(), (int)_point.getY() + _self.getY());
				    	if (view != null) {
				    		String str = view.getHelpMenu();
							JOptionPane.showMessageDialog(_self, str);
				    	} 
					}
					else {
						BlockView view = getSelectedComponent((int)_point.getX(), (int)_point.getY());
				    	if (view != null) {
				    		String str = view.getHelpMenu();
							JOptionPane.showMessageDialog(_self, str);
				    	}
			    	}
			    	
				}
	        });
	        add(_help);
	    }
	    
	    /**
	     * Add the break item
	     */
	    private void addSetBreak() {
	    	_break = new JCheckBox(_lang.getString("breakBlock"));
	    	
	    	BlockView view = getSelectedComponent((int)_point.getX(), (int)_point.getY());
	    	if (view != null && !((AbstractBlockController)getController()).acceptSingle(view)) {
		    	_break.setSelected(((AbstractBlockController) view.getController()).getBreakStatus());
		    	_break.addActionListener(new ActionListener() {
	
					@Override
					public void actionPerformed(ActionEvent e) {
						BlockView view = getSelectedComponent((int)_point.getX(), (int)_point.getY());
						if (view != null)
							((AbstractBlockController) view.getController()).setBreakStatus(_break.isSelected());
					}
		        });
	    	   	add(_break);
	    	}
	    }
	    
	    /**
	     * Add the removeitem
	     */
	    private void addRemoveItem() {
	    	_remove = new JMenuItem(_lang.getString("removeBlock"));
	    	_remove.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					_panel.removeFromPanelAndClass(_self);
				}
	        });
	        add(_remove);
	    }

		@Override
		public void update(Observable arg0, Object arg1) {
			_remove.setText(_lang.getString("removeBlock"));
			_break.setText(_lang.getString("breakBlock"));
		}
	}
	
	@Override
	public void setLocation(int x, int y) {
		super.setLocation(x, y);
		((AbstractBlockController)getController()).updateLocation(x,y);
	}
	
	/**
	 * Remove a component from the panel
	 * @param c component to remove
	 */
	public void removeFromPanel(Component c) {
		remove(c);
		_bodycomps.remove(c);
		_bodypos.remove(c);
	}
	
	/**
	 * Get the inner components
	 * @return all inner components
	 */
	public Component[] getInnerComponents(){
		return _bodycomps.keySet().toArray(new Component[_bodycomps.size()]);
	}
	
	/**
	 * Add a mouse listener
	 */
	private void addingMouseListener() {
		this.addMouseListener(new MouseAdapter(){
			/**
	         * If we release the mouse
		 	 * We either release it when we hold an unsnapped block
			 * or when we where moving ourselves
			 */
            public void mouseReleased(MouseEvent e){
            	if (_unsnapped == null) _panel.setReleased(e.getComponent(), getX()+e.getX(), getY()+e.getY());
            	else {
            		_panel.setReleased(_unsnapped, getX()+e.getX(), getY()+e.getY());
            		_unsnapped.makeTransparant(false);
            		_unsnapped.repaint();
            	}
            		
            	mouseReleased = false;
            	_unsnapped = null;
            	makeTransparant(false);
            	repaint();
            }
            
            // When we press the mouse we start updating the mouse coordinates
            // Possible we also want to unsnap a block, that may be nested
            public void mousePressed(MouseEvent e){
            	
            	if (!mouseReleased && SwingUtilities.isLeftMouseButton(e)) {
	                Component comp = e.getComponent();
	                comp.getParent().setComponentZOrder(comp, 0);
	                
	                oldMouseX = getX() - e.getXOnScreen();
	                oldMouseY = getY() - e.getYOnScreen();
	                mouseReleased = true;
	                
	                checkUnsnap(e.getX(), e.getY(), e.getXOnScreen(), e.getYOnScreen());  
	                
	                if (_unsnapped == null) makeTransparant(true);
	                else {
	                	_unsnapped.makeTransparant(true);
	                	_unsnapped.resetPositions();
	                }
            	}
            }
            
            /**
             * On mouse clicked
             */
            public void mouseClicked(MouseEvent e) {
            	if (SwingUtilities.isRightMouseButton(e)) {
            		PopUpClick menu = new PopUpClick(e.getX(), e.getY());
            		menu.show(e.getComponent(), e.getX(), e.getY());
            	}
            }
        });
	}
	
	/**
	 * Get the distance from the current view, to the outer views origin
	 * @return the position from the origin
	 */
	public Point fromOrigin() {
		Point p = getLocation();
		
		BlockView v = _parent;
		while (v._parent != null) {
			Point p1 = v.getLocation();
			p.translate(p1.x, p1.y);
			v = v._parent;
		}
		return p;
	}
	
	/**
	 * Check if a component has been unsnapped
	 */
	protected abstract void checkUnsnap(int x, int y, int screenX, int screenY);
	
	/**
	 * Get the block to unsnap
	 * If there is no block to unsnap, returns null
	 * @param x the x location of the block, relative
	 * @param y the y location of the block, relative
	 * @return the block to unsnap
	 */
	public abstract BlockView getBlockToUnsnap(int x, int y);
	
	/**
	 * Get the deepest block that contains (x,y)
	 * @param x, the x-position
	 * @param y, the y-position
	 * @return the block to unsnap
	 */
	public abstract BlockView getBlockToSnapScreen(int x, int y, Component comp);
	
	/**
	 * Get the deepest block that contains (x,y)
	 * @param x, the x-position
	 * @param y, the y-position
	 * @return the block to snap
	 */
	public abstract BlockView getBlockToSnap(int x, int y, Component comp);
	
	/**
	 * Get the parent block
	 * @return the parent block
	 */
	public BlockView getParentBlock() {
		return _parent;
	}
	
	/**
	 * Set the parent of this view
	 * @param view the new parent
	 */
	public void setParentBlock(BlockView view) {
		_parent = view;
	}
	
	/**
	 * Check if this block contains the (x,y) point
	 * @param x, the x-position
	 * @param y, the y-position
	 * @return wether the block contains the params
	 */
	public boolean containsBlock(int x,int y) {
		if (x >= getX() && x <= (getX() + getWidth())) {
			if (y >= getY() && y <= (getY() + getHeight())) {
				return true;	
			}
		}
		return false;
	}
	
	/**
	 * Add a block to this view
	 */
	public abstract Component add(Component comp);
	
	/**
	 * Add a block to this view
	 */
	public abstract BlockView addView(BlockView comp);
	
	/**
	 * Check if this block does not contain [comp]
	 * @param comp
	 * @return
	 */
	public abstract boolean notContainBlock(BlockView comp);
	
	/**
	 * Redraw the block in the correct form
	 */
	public abstract void resetPositions();
	
	/**
	 * Check if this block accepts [comp]
	 * @param comp the component to add
 	 * @return Check if this block accepts [comp]
	 */
	protected abstract boolean acceptBlock(Component comp);
	
	
	/**
	 * Getter for the language module
	 * @return the language module
	 */
	protected LanguageModule getLang(){
		return _lang;
	}
	
	/**
	 * Remove a block
	 */
	public abstract void removeBlock();
	
	/**
	 * Remove components recursive
	 */
	public void removeComponents(ArrayList<Component> comps) {
		for (Component cinner : comps) {
			removeFromPanel(cinner);
		}
		
		if (_parent != null)
			_parent.removeComponents(comps);
	}
	
//////////////////////////////////////////////////////////
//// Draw different shapes
//////////////////////////////////////////////////////////
	

    @Override
    /**
     * Repaint the graphics of the block
     */
    synchronized public void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	Graphics2D g2d = (Graphics2D) g.create();

    	Point p = getLocation();
    	if (_parent != null) g2d.translate(p.getX(), p.getY());
    	
    	// Draw the shapes
    	for(int i=0; i<_shapes.size() && i<_colors.size() && i<_outLineColors.size(); i++) {
    		draw(g2d, _shapes.get(i), _colors.get(i), _outLineColors.get(i));        	
        }   
   
        g2d.dispose();
    }
    
    /**
     * Draw a shape
     * @param g2d
     * @param shape the shape to draw
     * @param foreground, the border color 
     * @param background, the background color
     */
    private void draw(Graphics2D g2d, Shape shape, Color foreground, Color background) {
    	if (background != null) {
	    	if (_transparant) {
	    		g2d.setColor(new Color(background.getRed()/255f,background.getGreen()/255f,background.getBlue()/255f, ALPHA));
	    	}
	    	else g2d.setColor(background);
    		g2d.fill(shape);
    	}
    	g2d.setColor(foreground);
    	
		g2d.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
    	double thickness = 3;
    	Stroke oldStroke = g2d.getStroke();
    	g2d.setStroke(new BasicStroke((float) thickness));
        
        g2d.draw(shape);
 
        g2d.setStroke(oldStroke);
        
        g2d.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
    }

//////////////////////////////////////////////////////////
//// MVC
//////////////////////////////////////////////////////////
  
    /**
     * Set the controller
     */
    public void setController(Controller controller) {
        _controller = controller;
        // Tell the controller this object is its view.
        getController().setView(this);
    }

    /**
     * Get the controller
     */
    public Controller getController() {
        // If a controller hasn't been defined yet...
        if (_controller == null) {
            // ...make one. Note that defaultController is normally overriden by 
            // the AbstractView subclass so that it returns the appropriate 
            // controller for the view.
            setController(defaultController(getModel()));
        }
        
        return _controller;
    }
	
    /**
     * Set the model
     */
    public void setModel(Observable model) {
    	_model = model;
    	_model.addObserver(this);
    }

    /**
     * Get the model
     */
    public Observable getModel() {
        return _model;
    }

    //should be implemented in the sub-class that extends this class
    //creates a new controller of the desired type (implement) if the contoller is not given at use of the constructor,
    //this will create one.
    public abstract Controller defaultController(Observable model);
}