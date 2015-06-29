package ide.frontend.classes.views;

import ide.backend.language.LanguageModule;
import ide.frontend.classes.IDEPanel;
import ide.frontend.mvcbase.Controller;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Observable;

/**
 * An AnchorBlock, it's purpose is to group together blocks
 * @author axel
 */
public class AnchorBlock extends BlockView {

	private static final long serialVersionUID = 1L;
	
	// The blocks connected in this view
	private ArrayList<BlockView> _body;
	
	/**
	 * Variables used to insert and remove components
	 */
	private int _currentInsertY;
	private int _startWidth;
	private final int _anchorHeight;
	
	private static final Color _color = new Color(255,188,121);

	public AnchorBlock(Observable model, Controller controller, IDEPanel p, LanguageModule lang) {
		super(model, controller, p,lang);
		_anchorHeight = 20;
		_startWidth = 50;
	}

	@Override
	public void set(int x, int y) {
		_body = new ArrayList<>();
		init(x,y,_startWidth,_anchorHeight);
		
		_currentInsertY = 0;
	}

	@Override
	public void addComponents() {
		// None to be added
	}

	@Override
	public void makeShapes() {
		// The anchor point
		Rectangle r = new Rectangle(0, _currentInsertY, _startWidth, _anchorHeight);
		addShape(r, _color, _color);
	}
	
	/**
	 * Check if this anchor block has anything to unsnap
	 * @return true if the anchor can unsnap something
	 */
	public boolean canUnsnap() {
		return _body.size() > 0;
	}
	
	
	protected int getBodySize() {
		return _body.size();
	}
	
	protected BlockView get(int i) {
		return _body.get(i);
	}

	protected void removeAll(AnchorBlock block) {
		_body.removeAll(block.getBody());
	}

	@Override
	protected void checkUnsnap(int x, int y, int screenX, int screenY) {
		BlockView unsnap = getBlockToUnsnap(x, y);
		
		if (unsnap == this) return;
		if (unsnap == null) return;
		
		// Set to correct position
		updateMouse(unsnap.fromOrigin().x, unsnap.fromOrigin().y);
		unsnap.setLocation(screenX + getMouseX(), screenY + getMouseY() );
					
		// Add to panel
		getPanel().add(unsnap);
		unsnap.setParentBlock(null);
		
		// Set to top
		unsnap.getParent().setComponentZOrder(unsnap, 0);
		
		// Resnapping components to correct panel
		for (Component cinner : unsnap.getInnerComponents()) {
			removeFromPanel(cinner);

			unsnap.addToPanel(unsnap, cinner);
		}
		
		resetPositions();
		repaint();
		
		setUnsnapped(unsnap);
	}
	
	@Override
	public BlockView getSelectedComponent(int x, int y) {
		for(BlockView child: _body) {
			BlockView rec = child.getSelectedComponent(x, y);
			if (rec != null)
				return rec;
		}
			
		return null;
	}

	@Override
	public BlockView getBlockToUnsnap(int x, int y) {
		// loop over the entire body
		for(BlockView child: _body) {
			BlockView rec = child.getBlockToUnsnap(x-child.getX(), y-child.getY());
			
			if (rec != null) {
				for (Component cinner : rec.getInnerComponents()) {
					removeFromPanel(cinner);
					
					rec.addToPanel(rec, cinner);
				}
				return rec;
			}
			if (child.containsBlock(x, y)) {
				if (_body.indexOf(child) == 0) return this;
				
				// unsnap the entire body from this to the end
				AnchorBlock block = new AnchorBlock(((AnchorBlockController)getController()).removeBlocks(child),null, getPanel(), getLang());
				block.set(child.getX(), child.getY());
				block.setParentBlock(this);
				
				for(int i=_body.indexOf(child); i<_body.size(); i++) {
					resetComponents(_body.get(i));
					block.add(_body.get(i));
				}
				
				_body.removeAll(block.getBody());
				return block;
			}
		}
			
		return null;
	}
	
	/**
	 * Get the body of the connected block
	 * @return
	 */
	private ArrayList<BlockView> getBody() { return _body; }

	@Override
	public BlockView getBlockToSnapScreen(int x, int y, Component comp) {
		if(!containsBlock(x, y)) return null;
		

		BlockView v = getBlockToSnap(x-getX(), y-getY(),comp);
		return v;
	}
	
	/**
	 * Check if the block in over the anchor
	 * @param x, the x position
	 * @param y, the y position
	 * @return
	 */
	private boolean onAnchor(int x, int y) {
		return getShape(0).contains(new Point(x,y));
	}

	@Override
	public BlockView getBlockToSnap(int x, int y, Component c) {
		if (_currentInsertY > y) {
			for(BlockView child: _body) {
				BlockView out;
				out = child.getBlockToSnap(x - child.getX(), y - child.getY(), c);
				if (out != null) return out;
			}
		}
		
		if (onAnchor(x, y) && acceptBlock(c)) return this;
		else return null;
	}

	@Override
	public Component add(Component comp) {
		BlockView view = addView((BlockView) comp);
		if (view != null){
			((AnchorBlockController)getController()).addBlock(view);
		}
		
		getPanel().repaint();
		return comp;
	}
	
	@Override
	public BlockView addView(BlockView comp) {
		BlockView view = (BlockView) comp;
		
		if (view instanceof AnchorBlock) {
			ArrayList<BlockView> body = ((AnchorBlock) view).getBody();
			// Doesnt go correctly with components
			for(BlockView b: body) {
				resetComponents(b);
				add(b);
			}
			return null;
		}
		
		view.setParentBlock(this);
		
		// Set the position of the block relative to his parent
		setViewCorrect(view);
		
		Point p1 = view.getLocation();
		for (Component cinner : view.getInnerComponents()) {
			
			Point p = cinner.getLocation();
			p.setLocation(((int) p1.getX()) + p.getX(),( (int) p1.getY()) + p.getY());
			
			cinner.setLocation(p);

			addToPanel(view, cinner);
		}
		view.repaint();
		this.repaint();
		
		_body.add(view);
		
		return view;
	}
	
	/**
	 * Set the view in the correct position in the view
	 * @param view
	 */
	private void setViewCorrect(BlockView view) {
		view.setLocation(0, _currentInsertY);
		setHeightSize(getHeightSize() + view.getHeightSize());

		setWidthSize(Math.max(getWidthSize(), view.getWidthSize()));
		_currentInsertY += view.getHeightSize();
		setBounds(getX(), getY(), getWidthSize(), getHeightSize());
		
		updateShapes();
	}

	@Override
	public boolean notContainBlock(BlockView comp) {
		for(BlockView view: _body) {
			if (view == comp)
				return false;
			
			if (!view.notContainBlock(comp))
				return false;
		}
		return true;
	}

	@Override
	public void resetPositions() {
		// Resetting insert parameter
		_currentInsertY = 0;
		
		// Resetting the bounds
		setHeightSize( _anchorHeight);
		setWidthSize( _startWidth);
		
		// Set all components to the original positions
		for (Component cinner : getInnerComponents()) {
			addToPanel(this, cinner);
		}
		
		for(BlockView view: _body) {
			view.resetPositions();
			
			// Resetting position
			view.setLocation(0, _currentInsertY);
			setHeightSize(getHeightSize() + view.getHeightSize() + 3);
			
			setWidthSize( Math.max(getWidthSize(), view.getWidthSize()));
			
			_currentInsertY += view.getHeightSize() +3;
			setBounds(getX(), getY(), getWidthSize(), getHeightSize());
			
			Point p1 = view.getLocation();
			for (Component cinner : view.getInnerComponents()) {
				Point p = cinner.getLocation();
				
				p.setLocation(((int) p1.getX()) + p.getX(),( (int) p1.getY()) + p.getY());

				cinner.setLocation(p);
				
				addIfNotExisted(view, cinner);
			}

			view.repaint();
		}
		
		setBounds(getX(), getY(), getWidthSize(), getHeightSize());
		updateShapes();
		this.revalidate();
		this.repaint();
	}

	@Override
	protected boolean acceptBlock(Component comp) {
		return ((AnchorBlockController)getController()).acceptConnected((BlockView)comp);
	}

	/**
	 * Remove a component from the panel
	 * @param c
	 */
	@Override
	public void removeFromPanel(Component c) {
		super.removeFromPanel(c);
		
		for(BlockView v: _body) {
			v.removeFromPanel(c);
		}
	}
	
    @Override
	public void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	Graphics2D g2d = (Graphics2D) g.create();

    	Point p = getLocation();
    	if (getParentBlock() != null) g2d.translate(p.getX(), p.getY());
   
    	// Draw the children
        for(BlockView v: _body) v.paintComponent(g2d);
        g2d.dispose();
    }

	@Override
	public void makeTransparant(boolean trans) {
		setTransparency(trans);
		for(BlockView v: _body) v.makeTransparant(trans);
	}

	@Override
	public Controller defaultController(Observable model) {
		return new AnchorBlockController(getModel());
	}

	@Override
	public void updateLanguage() {
		
	}

	@Override
	public void removeBlock() {
		for (BlockView blockView : _body) {
			blockView.removeBlock();;
		}
		
	}

	@Override
	public String getHelpMenu() {
		return "";
	}
}
