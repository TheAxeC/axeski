package ide.frontend.classes.views.conditions;

import ide.backend.language.LanguageModule;
import ide.frontend.classes.IDEPanel;
import ide.frontend.classes.views.AnchorBlock;
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

public class ForeverView extends BlockView {

	private static final long serialVersionUID = 1L;

	/**
	 * Body of the if-statement
	 */
	private AnchorBlock _body;

	private static final Color _color = new Color(143,135,130);
	
	private int _startWidth;
	private int _startHeight;
	
	private final static int _topHeight = 50;
	private final static int _leftWidth = 20;
	private final static int _bottomWidth = 50;
	private final static int _bottomHeight = 20;

	private int _currentInsertY;
	
	private int _standardHeight;
	private int _standardWidth;
	
	
	private int _rightWidth;
	
	
	// Components
	private JLabel _title;

	public ForeverView(Observable model, Controller controller, IDEPanel p, LanguageModule lang) {
		super(model, controller, p, lang);
		
		_standardWidth = 80;
		_standardHeight = 20;
		_rightWidth = 20;
		
		_currentInsertY = _standardHeight + 3;
		_startWidth =  _standardWidth + _rightWidth;
		_startHeight = _topHeight + _bottomHeight;
	}
	
	public static JPanel getRepresentative(LanguageModule lang){
		return new JPanel() {

			private static final long serialVersionUID = 1L;

			{
				setOpaque(false);
				setLayout(null);
				JLabel title = new JLabel(lang.getString("Bforever"));
				title.setFont(new Font(title.getFont().getFontName(), Font.BOLD, title.getFont().getSize()));
				title.setBounds(5, 0, 80, 20);
				add(title);
			}
			
		    @Override
		    public void paintComponent(Graphics g) {
		    	super.paintComponent(g);
		    	Graphics2D g2d = (Graphics2D) g.create();

		    	Rectangle top = new Rectangle(0, 0, _bottomWidth + 40, _topHeight);
		    	draw(g2d, top, _color, _color);
				
				Rectangle left = new Rectangle(0, 0, _leftWidth, _topHeight + _bottomHeight + 20);
				draw(g2d, left, _color, _color);
				
				Rectangle bottom = new Rectangle(0, _topHeight + 20, _bottomWidth, _bottomHeight);
				draw(g2d, bottom, _color, _color);
		        
				g2d.dispose();
		    }
		    
		    @Override
		    public Dimension getPreferredSize() {
		    	return new Dimension(_bottomWidth + 20, _topHeight + _bottomHeight + 20);
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
		
		AnchorBlock b = new AnchorBlock(((ForeverViewController)getController()).getBodymodel(), null, getPanel(), getLang());
		b.set(0, 0);
		setBody(b);
	}

	@Override
	public void addComponents() {
		_title = new JLabel(_lang.getString("Bforever"));
		_title.setFont(new Font(_title.getFont().getFontName(), Font.BOLD, _title.getFont().getSize()));
		_title.setBounds(5, 0, 80, 20);
        addToPanel(this, _title);  
	}

	@Override
	public void makeShapes() {
		Rectangle top = new Rectangle(0, 0, getWidthSize(), _rightWidth );
		addShape(top, _color, _color);
		
		Rectangle left = new Rectangle(0, 0, _leftWidth, getHeightSize());
		addShape(left, _color, _color);
		
		Rectangle bottom = new Rectangle(0, getHeightSize() - _bottomHeight, _bottomWidth, _bottomHeight);
		addShape(bottom, _color, _color);
		
	}

	@Override
	protected void checkUnsnap(int x, int y, int screenX, int screenY) {
		// Should never be used
		// The if block can never appear without a containing connected block
	}
	
	@Override
	public BlockView getSelectedComponent(int x, int y) {		
		BlockView view = _body.getSelectedComponent(x - _body.getX(), y - _body.getY());
		if (view != null) return view;
		
		if (containsBlock(x, y)) return this;
		return null;
	}

	@Override
	public BlockView getBlockToUnsnap(int x, int y) {
		BlockView rec = null;
		if (_body.canUnsnap()) {
			rec = _body.getBlockToUnsnap(x-_body.getX(), y-_body.getY());
			
			if (rec == _body) {
				// Unsnap entire body
				AnchorBlock block = _body;
				
				AnchorBlock b = new AnchorBlock(((ForeverViewController)getController()).resetBody(), null, getPanel(), getLang());
				b.set(0, 0);
				setBody(b);
				
				rec = block;
			}
			
			if (rec != null) {
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
		// Should never be used
		// The if block can never appear without a containing connected block
		return null;
	}

	@Override
	public BlockView getBlockToSnap(int x, int y, Component c) {
		if(!containsBlock(x + getX(), y + getY())) return null;
		
		BlockView v = _body.getBlockToSnap(x-_body.getX(), y-_body.getY(),c);
		
		if (v == _body)
			return this;
		return v;
	}

	@Override
	public Component add(Component comp) {
		addView((BlockView) comp);
		return comp;
	}
	
	@Override
	public BlockView addView(BlockView comp) {
		_body.addView(comp);
		captureComponents();
		
		return comp;
	}
	
	
	
	/**
	 * Takes all components from the body
	 * and add them to the if block
	 */
	private void captureComponents() {
		Point p1 = _body.getLocation();
		for (Component cinner : _body.getInnerComponents()) {
			Point p = cinner.getLocation();
			
			p.setLocation(((int) p1.getX()) + p.getX(),( (int) p1.getY()) + p.getY());
			
			cinner.setLocation(p);
			addToPanel(_body, cinner);
		}
	}

	@Override
	public boolean notContainBlock(BlockView comp) {	
		if (!_body.notContainBlock(comp))
			return false;
		return true;
	}

	@Override
	public void resetPositions() {
		// Resetting the bounds
		setHeightSize( _startHeight);
		setWidthSize( _startWidth);
		_currentInsertY = _standardHeight + 3;
		
		for (Component cinner : getInnerComponents()) {
			addToPanel(this, cinner);
		}
		
		
		
		_body.resetPositions();
		
		// Resetting position
		_body.setLocation(_leftWidth + 3, _currentInsertY);
		
		setHeightSize(getHeightSize() + _body.getHeightSize());
		setWidthSize( Math.max(getWidthSize(), _body.getWidthSize()+_leftWidth));
		
		Point p1 = _body.getLocation();
		for (Component cinner : _body.getInnerComponents()) {
			Point p = cinner.getLocation();
			
			p.setLocation(((int) p1.getX()) + p.getX(),( (int) p1.getY()) + p.getY());
			
			cinner.setLocation(p);
			addIfNotExisted(_body, cinner);
		}

		_body.repaint();
		
		setBounds(getX(), getY(), getWidthSize(), getHeightSize());
		updateShapes();
		this.repaint();
	}

	@Override
	protected boolean acceptBlock(Component comp) {
		return true;
	}

	protected boolean acceptBlock(Component comp, boolean cond) {
		if (cond)
			return ((WhileViewController)getController()).acceptSingle((BlockView) comp);
		else
			return ((WhileViewController)getController()).acceptConnected((BlockView) comp);
	}
	
	/**
	 * Set the body of this block to the given anchorblock
	 * @param view
	 */
	public void setBody(AnchorBlock view) {
		_body = view;		
		view.setParentBlock(this);
		
		// Set the position of the block relative to his parent
		setViewCorrect(view);
		
		view.repaint();
		this.repaint();
	}
	
	/**
	 * Set the view in the correct position in the view
	 * @param view
	 */
	private void setViewCorrect(BlockView view) {
		view.setLocation(_leftWidth + 3, _currentInsertY );
		setHeightSize(getHeightSize() + view.getHeightSize());

		setWidthSize(Math.max(getWidthSize(), view.getWidthSize()));
		setBounds(getX(), getY(), getWidthSize(), getHeightSize());
		
		updateShapes();
	}
	
    @Override
    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	Graphics2D g2d = (Graphics2D) g.create();

    	Point p = getLocation();
    	if (getParentBlock() != null) g2d.translate(p.getX(), p.getY());
   
    	// Draw the body
        _body.paintComponent(g2d);
        g2d.dispose();
    }

	@Override
	public void makeTransparant(boolean trans) {
		setTransparency(trans);
		_body.makeTransparant(trans);
	}

	@Override
	public Controller defaultController(Observable model) {
		return new ForeverViewController(getModel());
	}

	@Override
	public void updateLanguage() {
		_title.setText(_lang.getString("Bforever"));
	}

	@Override
	public void removeBlock() {
		if(_body != null){
			_body.removeBlock();
		}
		
	}
	
	@Override
	public String getHelpMenu() {
		return _lang.getString("BforeverHelp");
	}

}

