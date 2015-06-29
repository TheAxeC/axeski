package ide.frontend.classes.views.functions;

import ide.backend.model.variables.AbstractRefVariabelModel;
import ide.frontend.classes.views.BlockView;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JPanel;

/**
 * Emitbox
 * The user can set variables in here to emit
 * @author axel
 *
 */
public class EmitBox extends JPanel {

	private static final long serialVersionUID = 1L;
	
	// Color of the emitBox
	private static final Color _color = new Color(200,82,0);
	
	private ArrayList<EmitField> _fields;
	
	private ArrayList< Component> _comps;
	
	/**
	 * Controller for the emit block
	 */
	private EmitController _controller;
	private EmitView _parent;
	
	private int _width, _height;
	
	private int _insertIndex;

	public EmitBox(EmitView v, EmitController controller, int x, int y) {
		_fields = new ArrayList<>();
		_comps = new ArrayList<>();
		_controller = controller;
		_parent = v;
		setLocation(x, y);
		_width = 0;
		_height = 0;
	}
	
	/**
	 * Draw the box
	 */
    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	Graphics2D g2d = (Graphics2D) g.create();

		for(EmitField f: _fields) {
			BlockView v = f.getContent();
			if (v != null) v.paintComponent(g2d);
		}
   
        g2d.dispose();
    }

	/**
	 * Insert a reference at the correct position
	 * @param comp view to add
	 */
	public void addView(BlockView comp) {
		EmitField f = _fields.get(_insertIndex);
		f.setContent(comp);
		
		comp.setParentBlock(_parent);

		Point p1 = comp.getLocation();
		for (Component cinner : comp.getInnerComponents()) {
			Point p = cinner.getLocation();
			p.setLocation(((int) p1.getX()) + p.getX(),( (int) p1.getY()) + p.getY());
			
			cinner.setLocation(p);

			_parent.addToPanel(comp, cinner);
		}
		comp.repaint();
		this.repaint();
	}
	
	/**
	 * Insert a reference at the correct position
	 * @param member member to which the view needs to be added
	 * @param comp view to add
	 */
	public void addView(String member, BlockView comp) {
		for(EmitField f: _fields) {
			if (f.getMember().equals(member)) {
				_insertIndex = _fields.indexOf(f);
			}
		}
		addView(comp);
	}
	
	/**
	 * Make the shapes to show on the emit box
	 */
	public void makeShapes() {
		Rectangle top = new Rectangle((int) getLocation().getX(), (int) getLocation().getY(), _width, _height);
		if(!((EmitController)_parent.getController()).getError())
			_parent.addShape(top, _color, _color);
		else
			_parent.addShape(top, Color.red, _color);
		
		
		for(EmitField f: _fields) {
			f.makeShapes(_parent);
		}
	}

	/**
	 * Get the block to unsnap
	 * @param x x-position
	 * @param y y-position
	 * @param c view to snap
	 * @return the component to snap to
	 */
	public BlockView getBlockToSnap(int x, int y, Component c) {
		for(EmitField f: _fields) {
			if (f.containsBlock(x, y)) {
				_insertIndex = _fields.indexOf(f);
				return _parent;
			}
		}
		return null;
	}

	/**
	 * Add a member
	 * @param comp view to add
	 */
	public void addMember(BlockView comp) {
		String member = _fields.get(_insertIndex).getMember();
		_controller.setMember(member, (AbstractRefVariabelModel) comp.getModel());
	}

	/**
	 * Get the block to unsnap
	 * @param i x-position to unsnap
	 * @param j y-position to unsnap
	 * @return the view to unsnap
	 */
	public BlockView getBlockToUnsnap(int x, int y) {
		
		for(EmitField f: _fields) {
			if (f.containsBlock(x + getX(), y + getY()) && f.getContent() != null) {
				_insertIndex = _fields.indexOf(f);
				
				BlockView ret = f.getContent();
		
				_controller.setMember(f.getMember(), null);
				f.setContent(null);
				Component[] comps = ret.getInnerComponents();
				ArrayList<Component> c = new ArrayList<>();
				for(Component comp: comps)
					c.add(comp);
				_parent.removeComponents(c);
				
				return ret;
			}
		}
		return null;
	}

	/**
	 * Look up all the members that we can create an emit window
	 */
	public void makeMembers() {
		for(String str: _controller.getMembers()) {
			EmitField add = new EmitField(str, (int) getLocation().getX(), (int) getLocation().getY() + _height);
			_fields.add(add);
			_height += add.getHeight();
			_width = Math.max(add.getWidth(), _width);
			_comps.addAll(add.getInnerComponents());
		}
		
		for (Component cinner : _comps) {
			_parent.addToPanel(_parent, cinner);
		}
	}
	
	/**
	 * Reset the positions
	 */
	public void resetPositions() {
		_parent.setWidthSize(Math.max(_parent.getWidthSize(), _width));
		_parent.setHeightSize(_parent.getHeightSize() + _height);
	}

	/**
	 * Remove all member variables
	 */
	public void removeMembers() {
		for(EmitField f: _fields) {
			_controller.setMember(f.getMember(), null);
			
			if (f.getContent() != null) {
				Component[] comps = f.getContent().getInnerComponents();
				ArrayList<Component> c = new ArrayList<>();
				for(Component comp: comps)
					c.add(comp);
				_parent.removeComponents(c);
			}				
		}
		
		// loop over all member to remove the components
		_parent.removeComponents(_comps);
		
		_fields.clear();
		_comps.clear();
		
		_width = 0;
		_height = 0;
	}

	/**
	 * Get the selected component located at [x,y]
	 * @param x, the x position
	 * @param y, the y position
	 * @return, the component or null if none was found
	 */
	public BlockView getSelectedComponent(int x, int y) {
		for(EmitField f: _fields) {
			if (f.containsBlock(x, y) && f.getContent() != null) {
				BlockView ret = f.getContent();				
				return ret;
			}
		}
		return null;
	}
}

