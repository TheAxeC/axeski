package ide.frontend.classes.views.physicViews;

import ide.backend.language.LanguageModule;
import ide.frontend.classes.IDEPanel;
import ide.frontend.classes.views.BlockView;
import ide.frontend.mvcbase.Controller;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Observable;

import javax.swing.JLabel;
import javax.swing.JPanel;
/**
 * View for the moveblock.
 * @author matthijs
 *
 */
public class MoveView extends BlockView {
	
	private static final long serialVersionUID = 1L;
	private static final int representationWidth = 150;
	private static final int representationHeight = 50;

	private BlockView _bodyX;
	private BlockView _bodyY;
	
	private static final Color _color = new Color(95,158,209);
	private JLabel _title;
	private JLabel _x, _y;
	
	// Start variables
	private int _startWidth, _startHeight;
	private int _left, _mid, _right;
	
	// All variables for the left block
	private int _widthL, _heightL, _startxL, _startyL;
	// All variables for the right block
	private int _widthR, _heightR, _startxR, _startyR;
	
	boolean _Xadd;

	public MoveView(Observable model, Controller controller, IDEPanel p, LanguageModule lang) {
		super(model, controller, p, lang);
		_bodyX = null;
		_bodyY = null;
		
		initMarkers();
	}
	
	public void initMarkers() {
		_left = 50;
		_mid = 30;
		_right = 10;
		
		_widthL = 80;
		_startyL = 20;
		_heightL = 30;
		_startxL = _left;
		
		_widthR = 80;
		_startyR = 20;
		_heightR = 30;
		_startxR = _left + _widthL + _mid;
		
		_startWidth = _startxR + _widthR + _right;
		_startHeight = 2 * _startyL + _heightL;
	}

	public static JPanel getRepresentative(LanguageModule lang){
		return new JPanel() {
			private static final long serialVersionUID = 1L;

			{
				setOpaque(false);
				setLayout(null);
				JLabel title = new JLabel(lang.getString("Bmove"));
				title.setFont(new Font(title.getFont().getFontName(), Font.BOLD, title.getFont().getSize()));
				title.setBounds(5, 0, 100, 20);
				add(title);
			}
			
		    @Override
		    public void paintComponent(Graphics g) {
		    	super.paintComponent(g);
		    	Graphics2D g2d = (Graphics2D) g.create();

		    	Rectangle top = new Rectangle(0, 0, representationWidth-1, representationHeight-1);
		    	draw(g2d, top, Color.BLACK, _color);

				g2d.dispose();
		    }
		    
		    @Override
		    public Dimension getPreferredSize() {
		    	return new Dimension(representationWidth, representationHeight);
		    }

		    private void draw(Graphics2D g2d, Shape shape, Color foreground, Color background) {
		    	g2d.setColor(background);
		        g2d.fill(shape);
		        g2d.setColor(foreground);
		        g2d.draw(shape);
		    }
		};
	}

	@Override
	public void set(int x, int y) {
		init(x,y,_startWidth,_startHeight);
	}

	@Override
	public void makeTransparant(boolean trans) {
		setTransparency(trans);
		if (_bodyX != null) _bodyX.makeTransparant(trans);
		if (_bodyY != null) _bodyY.makeTransparant(trans);
	}

	@Override
	public void addComponents() {
		_title = new JLabel(_lang.getString("Bmove"));
		_title.setBounds(5, 0, 80, 20);
		_title.setFont(new Font(_title.getFont().getFontName(), Font.BOLD, _title.getFont().getSize()));
        addToPanel(this, _title);   
        _x = new JLabel("X:");
        _x.setBounds(_startxL - 20, _startyL + 5 , 15, 10);
        _x.setFont(new Font(_title.getFont().getFontName(), Font.BOLD, _title.getFont().getSize()));
        addToPanel( this, _x);
        _y = new JLabel("Y:");
        _y.setBounds(_startxR - 20, _startyR + 5 , 15, 10);
        _y.setFont(new Font(_title.getFont().getFontName(), Font.BOLD, _title.getFont().getSize()));
        addToPanel(this, _y);
	}

	@Override
	public void makeShapes() {
		Rectangle top = new Rectangle(0, 0, getWidthSize(), getHeightSize());
		if(((MoveController)getController()).getError())
			addShape(top, Color.red, _color);
		else
			addShape(top, _color, _color);
		
		Rectangle screen = new Rectangle(_startxL, _startyL, _widthL, _heightL);
		addShape(screen, Color.black, _color);
		
		Rectangle screen2 = new Rectangle(_startxR, _startyR, _widthR, _heightR);
		addShape(screen2, Color.black, _color);
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
		if (_bodyX != null) {
			BlockView view = _bodyX.getSelectedComponent(x - getX(), y - getY());
			if (view != null) return view;
		}
		
		if (_bodyY != null) {
			BlockView view = _bodyY.getSelectedComponent(x - getX(), y - getY());
			if (view != null) return view;
		}

		if (containsBlock(x, y)) return this;
		return null;
	}

	@Override
	public BlockView getBlockToUnsnap(int x, int y) {
		if ((_bodyX == null && _bodyY == null)) return null;
		
		BlockView rec = null;
		if (_bodyX != null) {
			rec = _bodyX.getBlockToUnsnap(x-_bodyX.getX(), y-_bodyX.getY());
			
			if (rec == null && _bodyX.containsBlock(x, y)) rec = _bodyX;
			if (rec == _bodyX){
				((MoveController)getController()).removeBlockX();
				_bodyX = null;
				for (Component cinner : rec.getInnerComponents()) {
					removeFromPanel(cinner);

					rec.addToPanel(rec, cinner);
				}
			}
		}
		
		if (rec == null && _bodyY != null) {
			rec = _bodyY.getBlockToUnsnap(x-_bodyY.getX(), y-_bodyY.getY());
			
			if (rec == null && _bodyY.containsBlock(x, y)) rec = _bodyY;
			if (rec == _bodyY){
				((MoveController)getController()).removeBlockY();
				_bodyY = null;
				for (Component cinner : rec.getInnerComponents()) {
					removeFromPanel(cinner);

					rec.addToPanel(rec, cinner);
				}
			}
		}
		
		return rec;
	}

	@Override
	public BlockView getBlockToSnapScreen(int x, int y, Component comp) {
		return getBlockToSnap(x - getX(), y - getY(), comp);
	}

	@Override
	public BlockView getBlockToSnap(int x, int y, Component c) {
		if(!containsBlock(x + getX(), y + getY())) return null;
		
		if (_bodyX != null) {
			BlockView v = _bodyX.getBlockToSnap(x-_bodyX.getX(), y-_bodyX.getY(), c);
			if (v != null) return v;
		}
		
		if (_bodyY != null) {
			BlockView v = _bodyY.getBlockToSnap(x-_bodyY.getX(), y-_bodyY.getY(), c);
			if (v != null) return v;
		}
		
		if (_bodyX == null && acceptBlock(c) && containsBlockL(x, y)) {
			_Xadd = true;
			return this;
		}
		if (_bodyY == null && acceptBlock(c) && containsBlockR(x, y)) {
			_Xadd = false;
			return this;
		}
		return null;
	}
	
	private boolean containsBlockL(int x, int y) {
		if (x >= _startxL && x <= (_startxL + _widthL)) {
			if (y >= _startyL && y <= (_startyL + _heightL)) {
				return true;	
			}
		}
		return false;
	}

	private boolean containsBlockR(int x, int y) {
		if (x >= _startxR && x <= (_startxR + _widthR)) {
			if (y >= _startyR && y <= (_startyR + _heightR)) {
				return true;	
			}
		}
		return false;
	}
	
	@Override
	public Component add(Component comp) {
		if (_Xadd) {
			addView((BlockView) comp);
			((MoveController)getController()).addBlockX((BlockView) comp);
		} else {
			addViewRight((BlockView) comp);
			((MoveController)getController()).addBlockY((BlockView) comp);
		}
		return comp;
	}
	
	public BlockView addViewRight(BlockView comp) {
		_bodyY = (BlockView) comp;
		
		_bodyY.setParentBlock(this);
		
		// Set the position of the block relative to his parent
		setViewCorrectRight(_bodyY);
		
		Point p1 = _bodyY.getLocation();
		for (Component cinner : _bodyY.getInnerComponents()) {
			Point p = cinner.getLocation();
			p.setLocation(((int) p1.getX()) + p.getX(),( (int) p1.getY()) + p.getY());
			
			cinner.setLocation(p);

			addToPanel(_bodyY, cinner);
		}
		_bodyY.repaint();
		this.repaint();
		
		return comp;
	}
	
	@Override
	public BlockView addView(BlockView comp) {
		_bodyX = (BlockView) comp;
		
		_bodyX.setParentBlock(this);
		
		// Set the position of the block relative to his parent
		setViewCorrect(_bodyX);
		if (_bodyY != null) {
			setViewCorrectRight(_bodyY);
			
			Point p1 = _bodyY.getLocation();
			for (Component cinner : _bodyY.getInnerComponents()) {
				Point p = cinner.getLocation();
				p.setLocation(((int) p1.getX()) + p.getX(),( (int) p1.getY()) + p.getY());
				
				cinner.setLocation(p);

				addIfNotExisted(_bodyY, cinner);
			}
			_bodyY.repaint();
		}
		
		Point p1 = _bodyX.getLocation();
		for (Component cinner : _bodyX.getInnerComponents()) {
			Point p = cinner.getLocation();
			p.setLocation(((int) p1.getX()) + p.getX(),( (int) p1.getY()) + p.getY());
			
			cinner.setLocation(p);

			addToPanel(_bodyX, cinner);
		}
		_bodyX.repaint();
		this.repaint();
		
		return comp;
	}
	
	/**
	 * Set the view in the correct position in the view
	 * @param view
	 */
	private void setViewCorrect(BlockView view) {
		view.setLocation(_startxL, _startyL);
		int t = _startHeight - _heightL;
		_heightL = Math.max(view.getHeightSize(), _heightL);
		setHeightSize(t + _heightL);

		_widthL = view.getWidthSize();
		setWidthSize(_startxL + _widthL + _mid + _widthR + _right);
		setBounds(getX(), getY(), getWidthSize(), getHeightSize());
		
		_startxR = _left + _mid + _widthL;
		
		
		updateShapes();
	}
	
	/**
	 * Set the view in the correct position in the view
	 * @param view
	 */
	private void setViewCorrectRight(BlockView view) {
		view.setLocation(_startxR, _startyR);
		int t = _startHeight - _heightR;
		_heightR = Math.max(view.getHeightSize(), _heightR);
		setHeightSize(t  + _heightR);

		_widthR = view.getWidthSize();
		setWidthSize(_startxR + _widthR + _right);
		setBounds(getX(), getY(), getWidthSize(), getHeightSize());
		
		updateShapes();
	}

	@Override
	public boolean notContainBlock(BlockView comp) {
		if (_bodyX != null && !_bodyX.notContainBlock(comp))
			return false;
		if (_bodyY != null && !_bodyY.notContainBlock(comp))
			return false;
		return true;
	}

	@Override
	public void resetPositions() {
		// Resetting the bounds
		initMarkers();
		setHeightSize( _startHeight );
		setWidthSize( _startWidth);
		
		for (Component cinner : getInnerComponents()) {
			addToPanel(this, cinner);
		}
		
		if (_bodyX != null) {
			_bodyX.resetPositions();
			
			// Resetting position
			setViewCorrect(_bodyX);
			
			Point p1 = _bodyX.getLocation();
			for (Component cinner : _bodyX.getInnerComponents()) {
				Point p = cinner.getLocation();
				
				p.setLocation(((int) p1.getX()) + p.getX(),( (int) p1.getY()) + p.getY());
				
				cinner.setLocation(p);
				addIfNotExisted(_bodyX, cinner);
			}
	
			_bodyX.repaint();
		}

		if (_bodyY != null) {
			_bodyY.resetPositions();
			
			// Resetting position
			setViewCorrectRight(_bodyY);
			
			Point p1 = _bodyY.getLocation();
			for (Component cinner : _bodyY.getInnerComponents()) {
				Point p = cinner.getLocation();
				
				p.setLocation(((int) p1.getX()) + p.getX(),( (int) p1.getY()) + p.getY());
				
				cinner.setLocation(p);
				addIfNotExisted(_bodyY, cinner);
			}
	
			_bodyY.repaint();
		}
		removeFromPanel(_x);
		 _x.setLocation(_startxL - 20, _startyL + 5);
		 addToPanel(this, _x);
		removeFromPanel(_y);
		 _y.setLocation(_startxR - 20, _startyR + 5 );
		 addToPanel(this, _y);
		
		setBounds(getX(), getY(), getWidthSize(), getHeightSize());
		updateShapes();
		this.repaint();
	}

	@Override
	protected boolean acceptBlock(Component comp) {
		return ((MoveController)getController()).acceptSingle((BlockView) comp);
	}

	@Override
	public Controller defaultController(Observable model) {
		return new MoveController(getModel());
	}
	
    @Override
    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	Graphics2D g2d = (Graphics2D) g.create();

    	Point p = getLocation();
    	if (getParentBlock() != null) g2d.translate(p.getX(), p.getY());
   
    	// Draw the body
    	if (_bodyX != null)
    		_bodyX.paintComponent(g2d);
    	if (_bodyY != null)
    		_bodyY.paintComponent(g2d);
    	
        g2d.dispose();
    }

	@Override
	public void updateLanguage() {
		_title.setText(_lang.getString("Bmove"));
	}

	@Override
	public void removeBlock() {
		
		
	}
	
	@Override
	public String getHelpMenu() {
		return _lang.getString("BmoveHelp");
	}

}
