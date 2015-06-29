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

/**
 * View for the if else test. View for IfElseModel
 * @author axel
 *
 */
public class IfElseView extends BlockView {

	private static final long serialVersionUID = 1L;

	/**
	 * Body of the if-statement
	 */
	private AnchorBlock _body;
	private AnchorBlock _bodyElse;
	
	private BlockView _cond;
	private Rectangle _screen;
	
	private static final Color _color = new Color(143,135,130);
	
	private int _startWidth;
	private int _startHeight;
	
	private final static int _topHeight = 70;
	private final static int _leftWidth = 20;
	private final static int _bottomWidth = 50;
	private final static int _bottomHeight = 20;
	private final static int _middleHeight = 20;
	private final static int _middleWidth = 50;

	private int _currentInsertY;
	
	private int _standardHeight;
	private int _standardWidth;
	
	private int _condX;
	private int _condY;
	private int _rightWidth;
	
	private boolean _addCondition;
	private boolean _addElse;
	private JLabel _title;

	public IfElseView(Observable model, Controller controller, IDEPanel p, LanguageModule lang) {
		super(model, controller, p, lang);
		
		_condX = 50;
		_condY = 20;
		
		_standardWidth = 90;
		_standardHeight = 30;
		_rightWidth = 20;
		
		_currentInsertY = 30+_standardHeight ;
		_startWidth = _condX + _standardWidth + _rightWidth;
		_startHeight = _topHeight + _bottomHeight + _middleHeight;
		
		_addCondition = false;
	}
	
	public static JPanel getRepresentative(LanguageModule lang){
		return new JPanel() {

			private static final long serialVersionUID = 1L;

			{
				setOpaque(false);
				setLayout(null);
				JLabel title = new JLabel(lang.getString("Bifelse"));
				title.setFont(new Font(title.getFont().getFontName(), Font.BOLD, title.getFont().getSize()));
				title.setBounds(5, 0, 50, 20);
				add(title);
			}
			
		    @Override
		    public void paintComponent(Graphics g) {
		    	super.paintComponent(g);
		    	Graphics2D g2d = (Graphics2D) g.create();

		    	Rectangle top = new Rectangle(0, 0, _bottomWidth + 20, _topHeight );
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
		
		AnchorBlock b = new AnchorBlock(((IfElseViewController)getController()).getBodymodelIf(), null, getPanel(), getLang());
		b.set(0, 0);
		setBody(b);
		
		AnchorBlock b2 = new AnchorBlock(((IfElseViewController)getController()).getBodymodelElse(), null, getPanel(), getLang());
		b2.set(0, 0);
		setBodyElse(b2);
	}

	@Override
	public void addComponents() {
		_title = new JLabel(_lang.getString("Bifelse"));
		_title.setFont(new Font(_title.getFont().getFontName(), Font.BOLD, _title.getFont().getSize()));
		_title.setBounds(5, 0, 100, 20);
        addToPanel(this, _title);  
	}

	@Override
	public void makeShapes() {
		Rectangle top = new Rectangle(0, 0, getWidthSize(), _condY + 10 + ((_cond == null) ? _standardHeight : _cond.getHeight()));
		if(((IfElseViewController)getController()).getError())
			addShape(top, Color.RED, _color);
		else
			addShape(top, _color, _color);
		
		Rectangle left = new Rectangle(0, 0, _leftWidth, getHeightSize());
		addShape(left, _color, _color);
		
		Rectangle middle = null;
		if (_body != null)
			middle = new Rectangle(0, _body.getY() + _body.getHeightSize(), _middleWidth, _middleHeight);
		else 
			middle = new Rectangle(0, _topHeight, _middleWidth, _middleHeight);
		addShape(middle, _color, _color);
		
		Rectangle bottom = new Rectangle(0, getHeightSize() - _bottomHeight, _bottomWidth, _bottomHeight);
		addShape(bottom, _color, _color);
		
		int width = (_cond == null) ? _standardWidth : _cond.getWidthSize();
		int height = (_cond == null) ? _standardHeight : _cond.getHeightSize();
		_screen = new Rectangle(_condX, _condY, width, height);
		addShape(_screen, Color.black, _color);
	}

	@Override
	protected void checkUnsnap(int x, int y, int screenX, int screenY) {
		// Should never be used
		// The if block can never appear without a containing connected block
	}
	
	@Override
	public BlockView getSelectedComponent(int x, int y) {
		if (_cond != null) {
			BlockView view = _cond.getSelectedComponent(x - getX(), y - getY());
			if (view != null) return view;
		}
		
		BlockView view = _body.getSelectedComponent(x - _body.getX(), y - _body.getY());
		if (view != null) return view;
		
		view = _bodyElse.getSelectedComponent(x - _bodyElse.getX(), y - _bodyElse.getY());
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
				
				AnchorBlock b = new AnchorBlock(((IfElseViewController)getController()).resetBodyIf(), null, getPanel(), getLang());
				b.set(0, 0);
				setBody(b);
				
				rec = block;
			}
		}
		
		if (_bodyElse.canUnsnap()) {
			rec = _bodyElse.getBlockToUnsnap(x-_bodyElse.getX(), y-_bodyElse.getY());
			
			if (rec == _bodyElse) {
				// Unsnap entire body
				AnchorBlock block = _bodyElse;
				
				AnchorBlock b = new AnchorBlock(((IfElseViewController)getController()).resetBodyElse(), null, getPanel(), getLang());
				b.set(0, 0);
				setBodyElse(b);
				
				rec = block;
			}
		}
		
		if (rec != null) {
			for (Component cinner : rec.getInnerComponents()) {
				removeFromPanel(cinner);
				rec.addToPanel(rec, cinner);
			}
		}
		
		if (rec == null && _cond != null) {
			rec = _cond.getBlockToUnsnap(x-_cond.getX(), y-_cond.getY());
			if (rec == null && _cond.containsBlock(x, y)) rec = _cond;
			if (rec == _cond){
				((IfElseViewController)getController()).removeCondition();
				_cond = null;
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
		_addCondition = false;
		if(!containsBlock(x + getX(), y + getY())) return null;
		
		BlockView v = _body.getBlockToSnap(x-_body.getX(), y-_body.getY(),c);
		_addElse = false;
		if (v == _body)
			return this;
		if (v == null) {
			v = _bodyElse.getBlockToSnap(x-_bodyElse.getX(), y-_bodyElse.getY(),c);
			_addElse = true;
			if (v == _bodyElse)
				return this;
			if (v == null) {
				// Maybe we need to insert in the condition:
				if(!_screen.contains(x, y)) return null;
				_addCondition = true;
				if (_cond == null) return this;
				_addCondition = false;
				
				v = _cond.getBlockToSnap(x-_cond.getX(), y-_cond.getY(), c);
			}
		}
		return v;
	}

	@Override
	public Component add(Component comp) {
		if (_addElse)
			addViewElse((BlockView) comp);
		else
			addView((BlockView) comp);
		if (_addCondition) {
			((IfElseViewController)getController()).setCondition(_cond);
		}
		return comp;
	}
	
	@Override
	public BlockView addView(BlockView comp) {
		if (_addCondition) {
			addConditionView(comp);
		} else {
			_body.addView(comp);
		 	captureComponents(_body);
		}
		return comp;
	}
	
	public BlockView addViewElse(BlockView comp) {
		if (_addCondition) {
			addConditionView(comp);
		} else {
			_bodyElse.addView(comp);
		 	captureComponents(_bodyElse);
		}
		return comp;
	}
	
	public BlockView addConditionView(BlockView comp){
		_cond = (BlockView) comp;
		_cond.setParentBlock(this);

		// Set the position of the block relative to his parent
		_cond.setLocation(_condX, _condY);
		setHeightSize(_startHeight + _cond.getHeightSize() - _standardHeight);
		int add = - _standardWidth + _cond.getWidthSize();
		if (add > 0)
			setWidthSize(getWidthSize() + add);
		setBounds(getX(), getY(), getWidthSize(), getHeightSize());
		updateShapes();
		
		Point p1 = _cond.getLocation();
		for (Component cinner : _cond.getInnerComponents()) {
			Point p = cinner.getLocation();
			p.setLocation(((int) p1.getX()) + p.getX(),( (int) p1.getY()) + p.getY());
			
			cinner.setLocation(p);

			addToPanel(_cond, cinner);
		}
		_cond.repaint();
		this.repaint();
		return comp;
	}
	
	/**
	 * Takes all components from the body
	 * and add them to the if block
	 */
	private void captureComponents(AnchorBlock b) {
		Point p1 = b.getLocation();
		for (Component cinner : b.getInnerComponents()) {
			Point p = cinner.getLocation();
			
			p.setLocation(((int) p1.getX()) + p.getX(),( (int) p1.getY()) + p.getY());
			
			cinner.setLocation(p);
			addToPanel(b, cinner);
		}
	}

	@Override
	public boolean notContainBlock(BlockView comp) {	
		if (!_body.notContainBlock(comp)) return false;
		if (!_bodyElse.notContainBlock(comp)) return false;
		if (_cond != null && !_cond.notContainBlock(comp))
			return false;
		return true;
	}

	@Override
	public void resetPositions() {
		// Resetting the bounds
		setHeightSize( _startHeight);
		setWidthSize( _startWidth);
		_currentInsertY = _condY + 10  + 3 +_standardHeight;
		
		for (Component cinner : getInnerComponents()) {
			addToPanel(this, cinner);
		}
		
		if (_cond != null) {
			_cond.resetPositions();
			_cond.setLocation(_condX, _condY);
			_currentInsertY += _cond.getHeightSize() - _standardHeight;
			setHeightSize(getHeightSize() + _cond.getHeightSize()-_standardHeight);
			setWidthSize( Math.max(getWidthSize(), _cond.getWidthSize()+_condX+_rightWidth));
			
			Point p1 = _cond.getLocation();
			for (Component cinner : _cond.getInnerComponents()) {
				Point p = cinner.getLocation();
				
				p.setLocation(((int) p1.getX()) + p.getX(),( (int) p1.getY()) + p.getY());
				
				cinner.setLocation(p);
				addIfNotExisted(_cond, cinner);
			}
		}
		
		_body.resetPositions();
		_bodyElse.resetPositions();
		
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
		
		_bodyElse.setLocation(_leftWidth + 3, _currentInsertY+_middleHeight+_body.getHeightSize()+3);
		setHeightSize(getHeightSize() + _bodyElse.getHeightSize());
		setWidthSize( Math.max(getWidthSize(), _bodyElse.getWidthSize()+_leftWidth));
		
		p1 = _bodyElse.getLocation();
		for (Component cinner : _bodyElse.getInnerComponents()) {
			Point p = cinner.getLocation();
			
			p.setLocation(((int) p1.getX()) + p.getX(),( (int) p1.getY()) + p.getY());
			
			cinner.setLocation(p);
			addIfNotExisted(_bodyElse, cinner);
		}

		_body.repaint();
		_bodyElse.repaint();
		
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
			return ((IfElseViewController)getController()).acceptSingle((BlockView) comp);
		else
			return ((IfElseViewController)getController()).acceptConnected((BlockView) comp);
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
	 * Set the body of this block to the given anchorblock
	 * @param view
	 */
	public void setBodyElse(AnchorBlock view) {
		_bodyElse = view;		
		view.setParentBlock(this);
		
		// Set the position of the block relative to his parent
		setViewCorrectElse(view);
		
		view.repaint();
		this.repaint();
	}
	
	/**
	 * Set the view in the correct position in the view
	 * @param view
	 */
	private void setViewCorrect(BlockView view) {
		view.setLocation(_leftWidth + 3, _currentInsertY+3);
		setHeightSize(getHeightSize() + view.getHeightSize());

		setWidthSize(Math.max(getWidthSize(), view.getWidthSize()));
		setBounds(getX(), getY(), getWidthSize(), getHeightSize());
		
		updateShapes();
	}
	
	/**
	 * Set the view in the correct position in the view
	 * @param view
	 */
	private void setViewCorrectElse(BlockView view) {
		view.setLocation(_leftWidth + 3, _currentInsertY+_middleHeight+_body.getHeightSize()+3);
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
   
    	if (_cond != null)
    		_cond.paintComponent(g2d);
    	// Draw the body
        _body.paintComponent(g2d);
        
        // Draw the else body
        _bodyElse.paintComponent(g2d);
        
        g2d.dispose();
    }

	@Override
	public void makeTransparant(boolean trans) {
		setTransparency(trans);
		_body.makeTransparant(trans);
		_bodyElse.makeTransparant(trans);
	}

	@Override
	public Controller defaultController(Observable model) {
		return new IfElseViewController(getModel());
	}
	
	@Override
	public void updateLanguage() {
		_title.setText(_lang.getString("Bifelse"));
	}

	@Override
	public void removeBlock() {
		if(_body != null)
			_body.removeBlock();
		if (_bodyElse != null)
			_bodyElse.removeBlock();
	}
	
	@Override
	public String getHelpMenu() {
		return _lang.getString("BifelseHelp");
	}
}
