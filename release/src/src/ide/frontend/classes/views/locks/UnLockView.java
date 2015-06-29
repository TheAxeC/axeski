package ide.frontend.classes.views.locks;

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
 * View for unlocking a variable. View for UnlockModel
 * @author axel
 *
 */
public class UnLockView extends BlockView{
	private static final long serialVersionUID = 1L;

	private BlockView _ref;
	
	private static final int _startHeight = 50;
	private static final int _startWidth = 150;
	
	private static final Color _color = new Color(95,158,209);
	
	/*REF FIELDS*/
	private int _currentInsertYRef;
	private int _leftWidthRef;
	private int _standardHeightRef;
	private int _standardWidthRef;
	
	/*Components */
	private JLabel _title;
	
	public UnLockView(Observable model, Controller controller, IDEPanel p, LanguageModule lang) {
		super(model, controller, p, lang);
		_ref = null;
		//ref
		_currentInsertYRef = 10;
		_leftWidthRef = 60;
		_standardHeightRef = 30;
		_standardWidthRef = 80;
	}

	public static JPanel getRepresentative(LanguageModule lang){
		return new JPanel() {
			private static final long serialVersionUID = 1L;

			{
				setOpaque(false);
				setLayout(null);
				JLabel title = new JLabel(lang.getString("Bunlock"));
				title.setFont(new Font(title.getFont().getFontName(), Font.BOLD, title.getFont().getSize()));
				title.setBounds(5, 0, 50, 20);
				add(title);
			}
			
		    @Override
		    public void paintComponent(Graphics g) {
		    	super.paintComponent(g);
		    	Graphics2D g2d = (Graphics2D) g.create();

		    	Rectangle top = new Rectangle(0, 0, _startWidth-1, _startHeight-1);
		    	draw(g2d, top, Color.BLACK, _color);

				g2d.dispose();
		    }
		    
		    @Override
		    public Dimension getPreferredSize() {
		    	return new Dimension(_startWidth, _startHeight);
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
	}

	@Override
	public void addComponents() {
		_title = new JLabel(_lang.getString("Bunlock"));
		_title.setBounds(5, 0, 50, 20);
		_title.setFont(new Font(_title.getFont().getFontName(), Font.BOLD, _title.getFont().getSize()));
        addToPanel(this, _title);   
	}

	@Override
	public void makeShapes() {
		Rectangle top = new Rectangle(0, 0, getWidthSize(), getHeightSize());
		addShape(top, _color, _color);
		int width = (_ref == null) ? _standardWidthRef : _ref.getWidthSize();
		int height = (_ref == null) ? _standardHeightRef : _ref.getHeightSize();
		Rectangle screen = new Rectangle(_leftWidthRef, _currentInsertYRef, width, height);
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
		
		if (containsBlock(x, y)) return this;
		return null;
	}

	@Override
	public BlockView getBlockToUnsnap(int x, int y) {
		BlockView rec = null;
		if(_ref != null && _ref.containsBlock(x, y)){
			rec = _ref;
			((UnLockController)getController()).removeReference();
			_ref = null;
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
		if(_ref == null && acceptBlock(c)){
			if (x >= _leftWidthRef && x <= (_leftWidthRef + _standardWidthRef)) {
				if (y >= _currentInsertYRef && y <= (_currentInsertYRef + _standardHeightRef)) {
					return this;	
				}
			}			
		}
		return null;
	}

	@Override
	public Component add(Component comp) {
		addView((BlockView) comp);
		((UnLockController)getController()).setReference((BlockView) comp);
		return comp;
		
	}

	public BlockView addView(BlockView comp) {
		_ref = (BlockView) comp;
		
		_ref.setParentBlock(this);
		
		// Set the position of the block relative to his parent
		setViewCorrectRef(_ref);
		
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
	private void setViewCorrectRef(BlockView view) {
		view.setLocation(_leftWidthRef, _currentInsertYRef);
		
		setHeightSize(_startHeight + view.getHeightSize() - _standardHeightRef);
		setWidthSize(Math.max(getWidthSize()-_standardWidthRef, view.getWidthSize()+_leftWidthRef));
		setBounds(getX(), getY(), getWidthSize(), getHeightSize());
		
		updateShapes();
	}

	@Override
	public boolean notContainBlock(BlockView comp) {
		return true;
	}

	@Override
	public void resetPositions() {
		// Resetting the bounds
		setHeightSize( _startHeight );
		setWidthSize( _startWidth );
		
		for (Component cinner : getInnerComponents()) {
			addToPanel(this, cinner);
		}
		if(_ref != null){
			_ref.resetPositions();
			
			// Resetting position
			_ref.setLocation(_leftWidthRef, _currentInsertYRef);
			setHeightSize( Math.max(getHeightSize(), _startHeight - _standardHeightRef+ _ref.getHeightSize()));
			setWidthSize(getWidthSize() - _standardWidthRef + _ref.getWidthSize());
			
			Point p1 = _ref.getLocation();
			for (Component cinner : _ref.getInnerComponents()) {
				Point p = cinner.getLocation();
				
				p.setLocation(((int) p1.getX()) + p.getX(),( (int) p1.getY()) + p.getY());
				
				cinner.setLocation(p);
				addIfNotExisted(_ref, cinner);
			}
	
			_ref.repaint();
		}
		
		
		setBounds(getX(), getY(), getWidthSize(), getHeightSize());
		updateShapes();
		this.repaint();
	}

	@Override
	protected boolean acceptBlock(Component comp) {
		return ((UnLockController)getController()).acceptVar((BlockView) comp);
	}

	@Override
	public Controller defaultController(Observable model) {
		return new UnLockController(getModel());
	}
	
    @Override
    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	Graphics2D g2d = (Graphics2D) g.create();

    	Point p = getLocation();
    	if (getParentBlock() != null) g2d.translate(p.getX(), p.getY());
    	
    	if (_ref != null)
    		_ref.paintComponent(g2d);

        g2d.dispose();
    }
    
	@Override
	public void updateLanguage() {
		_title.setText(_lang.getString("Bunlock"));
	}

	@Override
	public void removeBlock() {
		
		
	}
	
	@Override
	public String getHelpMenu() {
		return _lang.getString("BunlockHelp");
	}

}