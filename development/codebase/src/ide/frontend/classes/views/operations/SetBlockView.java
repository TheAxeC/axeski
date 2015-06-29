package ide.frontend.classes.views.operations;

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
 * Represents a set block
 * This can set variables
 * @author Matthijs && Axel
 *
 */
public class SetBlockView extends BlockView{
	private static final long serialVersionUID = 1L;

	private BlockView _body;
	private BlockView _ref;
	
	private static final int representationWidth = 150;
	private static final int representationHeight = 50;
	
	private static final int _startHeight = 50;
	private static final int _startWidth = 230;
	
	private static final Color _color = new Color(95,158,209);
	
	private boolean _snaptoRef;
	
	/*BODY FIELDS*/
	private int _currentInsertYBody;
	private int _leftWidthBody;
	private int _rightWidthBody;
	
	private int _standardHeightBody;
	private int _standardWidthBody;
	private int _standardLeftWidthBody;

	/*REF FIELDS*/
	
	private int _currentInsertYRef;
	private int _leftWidthRef;
	
	private int _standardHeightRef;
	private int _standardWidthRef;
	
	private int _space;
	
	/*Components */
	private JLabel _title;
	
	public SetBlockView(Observable model, Controller controller, IDEPanel p, LanguageModule lang) {
		super(model, controller, p, lang);
		_body = null;
		_ref = null;
		//ref
		_currentInsertYRef = 10;
		_leftWidthRef = 50;
		_standardHeightRef = 30;
		_standardWidthRef = 70;
		//body
		_leftWidthBody = 130;
		_standardWidthBody = 80;
		_rightWidthBody = 20;
		_currentInsertYBody = 10;
		_standardHeightBody = 30;
		_standardLeftWidthBody = 140;
		
		_space = 10;
	}

	public static JPanel getRepresentative(LanguageModule lang){
		return new JPanel() {
			private static final long serialVersionUID = 1L;

			{
				setOpaque(false);
				setLayout(null);
				JLabel title = new JLabel(lang.getString("Bset"));
				title.setFont(new Font(title.getFont().getFontName(), Font.BOLD, title.getFont().getSize()));
				title.setBounds(5, 0, 50, 20);
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
		if (_body != null) _body.makeTransparant(trans);
	}

	@Override
	public void addComponents() {
		_title = new JLabel(_lang.getString("Bset"));
		_title.setFont(new Font(_title.getFont().getFontName(), Font.BOLD, _title.getFont().getSize()));
		_title.setBounds(5, 0, 50, 20);
        addToPanel(this, _title);   
	}

	@Override
	public void makeShapes() {
		Rectangle top = new Rectangle(0, 0, getWidthSize(), getHeightSize());
		if (((SetBlockViewController)getController()).getError()) 
			addShape(top, Color.RED, _color);
		else
			addShape(top, _color, _color);
		int width = (_ref == null) ? _standardWidthRef : _ref.getWidthSize();
		int height = (_ref == null) ? _standardHeightRef : _ref.getHeightSize();
		Rectangle screen = new Rectangle(_leftWidthRef, _currentInsertYRef, width, height);
		addShape(screen, Color.black, _color);
		
		width = (_body == null) ? _standardWidthBody : _body.getWidthSize();
		height = (_body == null) ? _standardHeightBody : _body.getHeightSize();
		screen = new Rectangle(_leftWidthBody, _currentInsertYBody, width, height);
		addShape(screen, Color.black, _color);
	}

	@Override
	protected void checkUnsnap(int x, int y, int screenX, int screenY) {
		// Should never be used
		// The print block can never appear without a containing connected block
	}
	
	@Override
	public BlockView getSelectedComponent(int x, int y) {
		if (_ref != null) {
			BlockView view = _ref.getSelectedComponent(x - getX(), y - getY());
			if (view != null) return view;
		}
		
		if (_body != null) {
			BlockView view = _body.getSelectedComponent(x - getX(), y - getY());
			if (view != null) return view;
		}
		
		if (containsBlock(x, y)) return this;
		return null;
	}

	@Override
	public BlockView getBlockToUnsnap(int x, int y) {
		BlockView rec = null;
		if ((_body == null) && (_ref == null)) return null;
		
		if(_body != null){
			rec = _body.getBlockToUnsnap(x-_body.getX(), y-_body.getY());
			
			if (rec == null && _body.containsBlock(x, y)) rec = _body;
			if (rec == _body){
				((SetBlockViewController)getController()).removeContent();
				_body = null;
			}
		}
		
		if(rec == null && _ref != null && _ref.containsBlock(x, y)){
			rec = _ref;
			((SetBlockViewController)getController()).removeReference();
			_ref = null;
		}
		
		if (rec != null) {
			for (Component cinner : rec.getInnerComponents()) {
				removeFromPanel(cinner);

				rec.addToPanel(rec, cinner);
			}
		}
		
		return rec;
	}

	@Override
	public BlockView getBlockToSnapScreen(int x, int y, Component comp) {
		// Should never be used
		// The print block can never appear without a containing connected block
		return null;
	}

	@Override
	public BlockView getBlockToSnap(int x, int y, Component c) {
		if(!containsBlock(x + getX(), y + getY())) return null;
		if(_ref == null && acceptBlockRef(c)){
			if (x >= _leftWidthRef && x <= (_leftWidthRef + _standardWidthRef)) {
				if (y >= _currentInsertYRef && y <= (_currentInsertYRef + _standardHeightRef)) {
					_snaptoRef = true;
					return this;	
				}
			}
			
		}
		if (_body == null && acceptBlock(c)) {
			if (x >= _leftWidthBody && x <= (_leftWidthBody + _standardWidthBody)) {
				if (y >= _currentInsertYBody && y <= (_currentInsertYBody + _standardHeightBody)) {
					_snaptoRef = false;
					return this;	
				}
			}
		}
		if( _body != null){
			BlockView v = _body.getBlockToSnap(x-_body.getX(), y-_body.getY(), c);
			return v;
		}
		return null;
	}

	@Override
	public Component add(Component comp) {
		if(_snaptoRef){
			addViewRef((BlockView) comp);
			((SetBlockViewController)getController()).setReference((BlockView) comp);
		}
		else if( _body == null){
			addView((BlockView) comp);
			((SetBlockViewController)getController()).setContent((BlockView) comp);
		}
		return comp;
	}
	
	@Override
	public BlockView addView(BlockView comp) {
		_body = (BlockView) comp;
		
		_body.setParentBlock(this);
		
		// Set the position of the block relative to his parent
		setViewCorrectBody(_body);
		
		Point p1 = _body.getLocation();
		for (Component cinner : _body.getInnerComponents()) {
			Point p = cinner.getLocation();
			p.setLocation(((int) p1.getX()) + p.getX(),( (int) p1.getY()) + p.getY());
			
			cinner.setLocation(p);
			addToPanel(_body, cinner);
		}
		_body.repaint();
		this.repaint();
		
		return comp;
	}
	
	public BlockView addViewRef(BlockView comp) {
		_ref = (BlockView) comp;
		
		_ref.setParentBlock(this);
		
		// Set the position of the block relative to his parent
		setViewCorrectRef(_ref);
		if(_body != null) {
			setViewCorrectBody(_body);
			
			Point p1 = _body.getLocation();
			for (Component cinner : _body.getInnerComponents()) {
				Point p = cinner.getLocation();
				p.setLocation(((int) p1.getX()) + p.getX(),( (int) p1.getY()) + p.getY());
				
				cinner.setLocation(p);

				addToPanel(_body, cinner);
			}
			_body.repaint();
			this.repaint();
		}
		
		Point p1 = _ref.getLocation();
		for (Component cinner : _ref.getInnerComponents()) {
			Point p = cinner.getLocation();
			p.setLocation(((int) p1.getX()) + p.getX(),( (int) p1.getY()) + p.getY());
			
			cinner.setLocation(p);

			addToPanel(_ref, cinner);
		}
		_ref.repaint();
		this.repaint();
		
		return comp;
	}
	
	/**
	 * Set the view in the correct position in the view
	 * @param view
	 */
	private void setViewCorrectBody(BlockView view) {
		view.setLocation(_leftWidthBody, _currentInsertYBody);
		
		setHeightSize(_startHeight + view.getHeightSize() - _standardHeightBody);

		setWidthSize(Math.max(getWidthSize()-_standardWidthBody, view.getWidthSize()+_leftWidthBody));
		setBounds(getX(), getY(), getWidthSize(), getHeightSize());
		
		updateShapes();
	}
	
	/**
	 * Set the view in the correct position in the view
	 * @param view
	 */
	private void setViewCorrectRef(BlockView view) {
		view.setLocation(_leftWidthRef, _currentInsertYRef);
		
		setHeightSize(_startHeight + view.getHeightSize() - _standardHeightRef);
		setWidthSize(getWidthSize()-_standardWidthRef + view.getWidthSize());
		setBounds(getX(), getY(), getWidthSize(), getHeightSize());
		_leftWidthBody += _space +Math.abs(- _standardWidthRef + view.getWidthSize());
		
		updateShapes();
	}

	@Override
	public boolean notContainBlock(BlockView comp) {
		if (_body != null && !_body.notContainBlock(comp))
			return false;
		return true;
	}

	@Override
	public void resetPositions() {
		// Resetting the bounds
		setHeightSize( _startHeight );
		setWidthSize( _startWidth );
		_leftWidthBody = _standardLeftWidthBody;
		
		for (Component cinner : getInnerComponents()) {
			addToPanel(this, cinner);
		}
		if(_ref != null){
			_ref.resetPositions();
			
			// Resetting position
			_ref.setLocation(_leftWidthRef, _currentInsertYRef);
			//int oldWidth = getWidthSize();
			setHeightSize( Math.max(getHeightSize(), _startHeight - _standardHeightRef+ _ref.getHeightSize()));
			setWidthSize(getWidthSize() - _standardWidthRef + _ref.getWidthSize());
			//_leftWidthBody += Math.abs(- _standardWidthRef + _ref.getWidthSize());
			
			Point p1 = _ref.getLocation();
			for (Component cinner : _ref.getInnerComponents()) {
				Point p = cinner.getLocation();
				
				p.setLocation(((int) p1.getX()) + p.getX(),( (int) p1.getY()) + p.getY());
				
				cinner.setLocation(p);
				addIfNotExisted(_ref, cinner);
			}
	
			_ref.repaint();
		}
		
		if (_body != null) {
			_body.resetPositions();
			
			// Resetting position
			_body.setLocation(_leftWidthBody, _currentInsertYBody);
			
			setHeightSize( Math.max(getHeightSize(), _startHeight - _standardHeightBody+ _body.getHeightSize()));
			setWidthSize( getWidthSize() - _standardWidthBody + _body.getWidthSize() + _rightWidthBody);
			
			Point p1 = _body.getLocation();
			for (Component cinner : _body.getInnerComponents()) {
				Point p = cinner.getLocation();
				
				p.setLocation(((int) p1.getX()) + p.getX(),( (int) p1.getY()) + p.getY());
				
				cinner.setLocation(p);
				addIfNotExisted(_body, cinner);
			}
	
			_body.repaint();
		} 
		
		setBounds(getX(), getY(), getWidthSize(), getHeightSize());
		updateShapes();
		this.repaint();
	}

	@Override
	protected boolean acceptBlock(Component comp) {
		return ((SetBlockViewController)getController()).acceptSingle((BlockView) comp);
	}
	
	protected boolean acceptBlockRef(Component comp) {
		return ((SetBlockViewController)getController()).acceptVar((BlockView) comp);
	}

	@Override
	public Controller defaultController(Observable model) {
		return new SetBlockViewController(getModel());
	}
	
    @Override
    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	Graphics2D g2d = (Graphics2D) g.create();

    	Point p = getLocation();
    	if (getParentBlock() != null) g2d.translate(p.getX(), p.getY());
    	
    	if (_ref != null)
    		_ref.paintComponent(g2d);
    	// Draw the body
    	if (_body != null)
    		_body.paintComponent(g2d);
        g2d.dispose();
    }
    
	@Override
	public void updateLanguage() {
		_title.setText(_lang.getString("Bset"));
	}

	@Override
	public void removeBlock() {
		
		
	}
	
	@Override
	public String getHelpMenu() {
		return _lang.getString("BsetHelp");
	}
}
