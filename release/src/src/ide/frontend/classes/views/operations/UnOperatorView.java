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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
/**
 * View for unary operator.
 * @author matthijs
 *
 */
public class UnOperatorView extends BlockView {
	
	private static final long serialVersionUID = 1L;
	private static final int representationWidth = 100;
	private static final int representationHeight = 50;

	private BlockView _bodyL;
	
	
	private static final Color _color = new Color(95,158,209);
	private JLabel _title;
	
	// Start variables
	private int _startWidth, _startHeight;
	private int _left, _mid, _heightExtra; //, _right;
	
	// All variables for the left block
	private int _widthL, _heightL, _startxL, _startyL;
	
	
	private int _widthO, _heightO , _startxO, _startyO;
	
	boolean _leftAdd;
	
	private JComboBox<String> _selector;

	public UnOperatorView(Observable model, Controller controller, IDEPanel p, LanguageModule lang) {
		super(model, controller, p, lang);
		_bodyL = null;
		initMarkers();
	}
	
	public void initMarkers() {
		_heightExtra = 10;
		_left = 10;
		_mid = 20;
		//_right = 10;
		
		
		
		_widthO = 60;
		_startyO = 15;
		_heightO = 30;
		_startxO = _left;
		
		
		_widthL = 80;
		_startyL = 10;
		_heightL = 30;
		_startxL = _left + _widthO ;
		
		
		_startWidth = _startxL  + _widthL + _mid;
		_startHeight = 2 * _startyL + _heightL;
	}
	
	/**
	 * Init the operator selector
	 */
	private void initSelectOperator() {
		//load all the possible operators
		_selector = new JComboBox<String>(((UnOperatorController)getController()).getPossibleOperators());
		_selector.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//set operator to selected operator.
				((UnOperatorController)getController()).setOperator((String)_selector.getSelectedItem());
				
			}
		});
		//load selected operator
		_selector.setSelectedItem(((UnOperatorController)getController()).getOperator());
	}

	public static JPanel getRepresentative(LanguageModule lang){
		return new JPanel() {
			private static final long serialVersionUID = 1L;

			{
				setOpaque(false);
				setLayout(null);
				JLabel title = new JLabel(lang.getString("Bbinop"));
				title.setFont(new Font(title.getFont().getFontName(), Font.BOLD, title.getFont().getSize()));
				title.setBounds(5, 0, 80, 20);
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
		if (_bodyL != null) _bodyL.makeTransparant(trans);
	}

	@Override
	public void addComponents() {
		_title = new JLabel(_lang.getString("Bbinop"));
		_title.setBounds(5, 0, 80, 20);
		_title.setFont(new Font(_title.getFont().getFontName(), Font.BOLD, _title.getFont().getSize()));
        addToPanel(this, _title); 
        initSelectOperator();
        _selector.setBounds(_startxO, _startyO, _widthO, _heightO);
        addToPanel(this, _selector); 
	}

	@Override
	public void makeShapes() {
		Rectangle top = new Rectangle(0, 0, getWidthSize(), getHeightSize());
		if(((UnOperatorController)getController()).getError())
			addShape(top, Color.RED, _color);
		else
			addShape(top, _color, _color);
		
		Rectangle screen = new Rectangle(_startxL, _startyL, _widthL, _heightL);
		addShape(screen, Color.black, _color);
		
		
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
		if (_bodyL != null) {
			BlockView view = _bodyL.getSelectedComponent(x - getX(), y - getY());
			if (view != null) return view;
		}
		
		if (containsBlock(x, y)) return this;
		return null;
	}

	@Override
	public BlockView getBlockToUnsnap(int x, int y) {
		if (_bodyL == null) return null;
		
		BlockView rec = null;
		if (_bodyL != null) {
			rec = _bodyL.getBlockToUnsnap(x-_bodyL.getX(), y-_bodyL.getY());
			
			if (rec == null && _bodyL.containsBlock(x, y)) rec = _bodyL;
			if (rec == _bodyL){
				((UnOperatorController)getController()).removeLeft();
				_bodyL = null;
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
		
		if (_bodyL != null) {
			BlockView v = _bodyL.getBlockToSnap(x-_bodyL.getX(), y-_bodyL.getY(), c);
			if (v != null) return v;
		}
	
		if (_bodyL == null && acceptBlock(c) && containsBlockL(x, y)) {
			_leftAdd = true;
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

	
	
	@Override
	public Component add(Component comp) {
		if (_leftAdd) {
			addView((BlockView) comp);
			((UnOperatorController)getController()).addLeft((BlockView) comp);
		}
		return comp;
	}
	
	
	
	@Override
	public BlockView addView(BlockView comp) {
		_bodyL = (BlockView) comp;
		
		_bodyL.setParentBlock(this);
		
		// Set the position of the block relative to his parent
		setViewCorrect(_bodyL);
		
		Point p1 = _bodyL.getLocation();
		for (Component cinner : _bodyL.getInnerComponents()) {
			Point p = cinner.getLocation();
			p.setLocation(((int) p1.getX()) + p.getX(),( (int) p1.getY()) + p.getY());
			
			cinner.setLocation(p);

			addToPanel(_bodyL, cinner);
		}
		_bodyL.repaint();
		this.repaint();
		
		return comp;
	}
	
	/**
	 * Set the view in the correct position in the view
	 * @param view
	 */
	private void setViewCorrect(BlockView view) {
		
		view.setLocation(_startxL, _startyL);
		
		_heightL = Math.max(view.getHeightSize(), _heightL);
		setHeightSize(_heightExtra * 2  + _heightL);

		_widthL = view.getWidthSize();
		setWidthSize(_startxL + _widthL + _mid  );
		setBounds(getX(), getY(), getWidthSize(), getHeightSize());
		
		
		
		updateShapes();
	}
	
	
	
	private void setViewCorrectOp(){
		_selector.setBounds(_startxO, _startyO, _widthO, _heightO);
	}

	@Override
	public boolean notContainBlock(BlockView comp) {
		if (_bodyL != null && !_bodyL.notContainBlock(comp))
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
		
		if (_bodyL != null) {
			_bodyL.resetPositions();
			
			// Resetting position
			setViewCorrect(_bodyL);
			
			Point p1 = _bodyL.getLocation();
			for (Component cinner : _bodyL.getInnerComponents()) {
				Point p = cinner.getLocation();
				
				p.setLocation(((int) p1.getX()) + p.getX(),( (int) p1.getY()) + p.getY());
				
				cinner.setLocation(p);
				addIfNotExisted(_bodyL, cinner);
			}
	
			_bodyL.repaint();
		}
		
		setViewCorrectOp();

		
		
		setBounds(getX(), getY(), getWidthSize(), getHeightSize());
		updateShapes();
		this.repaint();
	}

	@Override
	protected boolean acceptBlock(Component comp) {
		return ((UnOperatorController)getController()).acceptSingle((BlockView) comp);
	}

	@Override
	public Controller defaultController(Observable model) {
		return new UnOperatorController(getModel());
	}
	
    @Override
    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	Graphics2D g2d = (Graphics2D) g.create();

    	Point p = getLocation();
    	if (getParentBlock() != null) g2d.translate(p.getX(), p.getY());
   
    	// Draw the body
    	if (_bodyL != null)
    		_bodyL.paintComponent(g2d);
    	
    	
        g2d.dispose();
    }

	@Override
	public void updateLanguage() {
		_title.setText(_lang.getString("Bbinop"));
	}

	@Override
	public void removeBlock() {
		
		
	}
	
	@Override
	public String getHelpMenu() {
		return _lang.getString("BbinopHelp");
	}
}
