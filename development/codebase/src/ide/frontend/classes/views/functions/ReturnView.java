package ide.frontend.classes.views.functions;

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
 * BlockView for the returnblock.
 * @author matthijs
 *
 */
public class ReturnView extends BlockView {
	
	private static final long serialVersionUID = 1L;

	private BlockView _body;
	
	private static final int _startHeight = 50;
	private static final int _startWidth = 150;
	
	private static final Color _color = new Color(95,158,209);
	
	private int _currentInsertY;
	private int _leftWidth;
	private int _rightWidth;
	
	private int _standardHeight;
	private int _standardWidth;
	
	private JLabel _title;

	public ReturnView(Observable model, Controller controller, IDEPanel p, LanguageModule lang) {
		super(model, controller, p, lang);
		_body = null;
		
		_leftWidth = 60;
		_standardWidth = 80;
		_rightWidth = 20;
		_currentInsertY = 10;
		_standardHeight = 30;
	}

	public static JPanel getRepresentative(LanguageModule lang){
		return new JPanel() {
			private static final long serialVersionUID = 1L;

			{
				setOpaque(false);
				setLayout(null);
				JLabel title = new JLabel(lang.getString("Breturn"));
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
		if (_body != null) _body.makeTransparant(trans);
	}

	@Override
	public void addComponents() {
		_title = new JLabel(_lang.getString("Breturn"));
		_title.setBounds(5, 0, 60, 20);
		_title.setFont(new Font(_title.getFont().getFontName(), Font.BOLD, _title.getFont().getSize()));
        addToPanel(this, _title);   
	}

	@Override
	public void makeShapes() {
		Rectangle top = new Rectangle(0, 0, getWidthSize(), getHeightSize());
		if(	!((ReturnViewController)getController()).getError())
			addShape(top, _color, _color);
		else
			addShape(top, Color.red, _color);
		
		int width = (_body == null) ? _standardWidth : _body.getWidthSize();
		int height = (_body == null) ? _standardHeight : _body.getHeightSize();
		Rectangle screen = new Rectangle(_leftWidth, _currentInsertY, width, height);
		addShape(screen, Color.black, _color);
	}

	@Override
	protected void checkUnsnap(int x, int y, int screenX, int screenY) {
		// Should never be used
		// The print block can never appear without a containing connected block
	}
	
	@Override
	public BlockView getSelectedComponent(int x, int y) {
		if (_body != null) {
			BlockView view = _body.getSelectedComponent(x - getX(), y - getY());
			if (view != null) return view;
		}

		if (containsBlock(x, y)) return this;
		return null;
	}

	@Override
	public BlockView getBlockToUnsnap(int x, int y) {
		if ((_body == null)) return null;
		
		BlockView rec = _body.getBlockToUnsnap(x-_body.getX(), y-_body.getY());
		
		if (rec == null && _body.containsBlock(x, y)) rec = _body;
		if (rec == _body){
			((ReturnViewController)getController()).removeVar(_body);
			_body = null;
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
		if (_body == null && acceptBlock(c)) return this;
		
		if (_body != null) {
			BlockView v = _body.getBlockToSnap(x-_body.getX(), y-_body.getY(), c);
			return v;
		}
		return null;
	}

	@Override
	public Component add(Component comp) {
		addView((BlockView) comp);
		((ReturnViewController)getController()).addVar((BlockView) comp);
		return comp;
	}
	
	@Override
	public BlockView addView(BlockView comp) {
		_body = (BlockView) comp;
		
		_body.setParentBlock(this);
		
		// Set the position of the block relative to his parent
		setViewCorrect(_body);
		
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
	
	/**
	 * Set the view in the correct position in the view
	 * @param view
	 */
	private void setViewCorrect(BlockView view) {
		view.setLocation(_leftWidth, _currentInsertY);
		
		setHeightSize(_startHeight + view.getHeightSize() - _standardHeight);

		setWidthSize(Math.max(getWidthSize()-_standardWidth, view.getWidthSize()+_leftWidth));
		setBounds(getX(), getY(), getWidthSize(), getHeightSize());
		
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
		setHeightSize( _startHeight - _standardHeight);
		setWidthSize( _startWidth - _standardWidth);
		
		for (Component cinner : getInnerComponents()) {
			addToPanel(this, cinner);
		}
		
		if (_body != null) {
			_body.resetPositions();
			
			// Resetting position
			_body.setLocation(_leftWidth, _currentInsertY);
			
			setHeightSize(getHeightSize() + _body.getHeightSize());
			setWidthSize( Math.max(getWidthSize(), _body.getWidthSize()+_leftWidth+_rightWidth));
			
			Point p1 = _body.getLocation();
			for (Component cinner : _body.getInnerComponents()) {
				Point p = cinner.getLocation();
				
				p.setLocation(((int) p1.getX()) + p.getX(),( (int) p1.getY()) + p.getY());
				
				cinner.setLocation(p);
				addIfNotExisted(_body, cinner);
			}
	
			_body.repaint();
		} else {
			setHeightSize( _startHeight);
			setWidthSize( _startWidth);
		}
		
		setBounds(getX(), getY(), getWidthSize(), getHeightSize());
		updateShapes();
		this.repaint();
	}

	@Override
	protected boolean acceptBlock(Component comp) {
		return ((ReturnViewController)getController()).acceptVar((BlockView) comp);
	}

	@Override
	public Controller defaultController(Observable model) {
		return new ReturnViewController(getModel());
	}
	
    @Override
    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	Graphics2D g2d = (Graphics2D) g.create();

    	Point p = getLocation();
    	if (getParentBlock() != null) g2d.translate(p.getX(), p.getY());
   
    	// Draw the body
    	if (_body != null)
    		_body.paintComponent(g2d);
        g2d.dispose();
    }

	@Override
	public void updateLanguage() {
		_title.setText(_lang.getString("Breturn"));
	}

	@Override
	public void removeBlock() {
		
		
	}
	
	@Override
	public String getHelpMenu() {
		return _lang.getString("BreturnHelp");
	}
}
