package ide.frontend.classes.views.functions;

import ide.backend.language.LanguageModule;
import ide.backend.model.BlockModel;
import ide.frontend.classes.IDEPanel;
import ide.frontend.classes.views.AnchorBlock;
import ide.frontend.classes.views.BlockView;
//import ide.frontend.classes.views.ColorFactory;
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

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * View for a handlerModel.
 * @author matthijs
 *
 */
public class HandlerView extends BlockView {

	private static final long serialVersionUID = 1L;

	/**
	 * Body of the if-statement
	 */
	private AnchorBlock _body;
	
	private int _startWidth;
	private int _startHeight;
	private static Color _c   = new Color(255, 128, 14);
	//private static Color _compC = ColorFactory.calculateComplementary(255, 128, 14);
	
	private final static int _topHeight = 30;
	private final static int _leftWidth = 20;
	private final static int _bottomWidth = 50;
	private final static int _bottomHeight = 20;

	private int _currentInsertY;
	
	private JButton _b;
	private JLabel _title;
	private JTextField _name;

	
	public HandlerView(Observable model, Controller controller, IDEPanel p, LanguageModule lang) {
		super(model, controller, p, lang);
	
		_currentInsertY = 30;
		
		_startWidth = 200;
		_startHeight = _topHeight + _bottomHeight+20;
		
	
	}	
	
	public static JPanel getRepresentative(LanguageModule lang){
		return new JPanel() {
			private static final long serialVersionUID = 1L;

			{
				setOpaque(false);
				setLayout(null);
				JLabel title = new JLabel(lang.getString("Bhandler"));
				title.setFont(new Font(title.getFont().getFontName(), Font.BOLD, title.getFont().getSize()));
				title.setBounds(5, 0, 100, 20);
				add(title);
			}
			
		    @Override
		    public void paintComponent(Graphics g) {
		    	super.paintComponent(g);
		    	
		    	Graphics2D g2d = (Graphics2D) g.create();

		    	Rectangle top = new Rectangle(0, 0, _bottomWidth + 60, _topHeight);
		    	draw(g2d, top, _c,_c);
				
				Rectangle left = new Rectangle(0,  _topHeight, _leftWidth, _topHeight + _bottomHeight + 20);
				draw(g2d, left,  _c,_c);
				
				Rectangle bottom = new Rectangle(0, _topHeight +_topHeight - _bottomHeight +20 , _bottomWidth, _bottomHeight);
				draw(g2d, bottom,  _c,_c );
		        
				g2d.dispose();
		    }
		    
		    @Override
		    public Dimension getPreferredSize() {
		    	return new Dimension(_bottomWidth + 60, _topHeight + _bottomHeight + 20  );
		    }

		    private void draw(Graphics2D g2d, Shape shape, Color foreground, Color background) {
		    	g2d.setColor(background);
		        g2d.fill(shape);
		        g2d.setColor(foreground);
		        g2d.draw(shape);
		    }
		};
	}

	/**
	 * Returns the eventButton for the given handler.
	 * @return
	 */
	public JButton getEventButton(){
		return _b;
	}

	@Override
	public void set(int x, int y) {
		init(x,y,_startWidth,_startHeight);
		
		AnchorBlock b = new AnchorBlock(((HandlerController)getController()).getBodymodel(), null, getPanel(),getLang());
		b.set(0, 0);
		setBody(b);
	}

	@Override
	public void addComponents() {
		_b = new JButton();
		_b.setBounds(2, 0, 20, 20);
		addConnectAction();
		addToPanel(this, _b);  
		_title = new JLabel(_lang.getString("Bhandler"));
		_title.setBounds(25, 5, 80, 20);
		//_title.setForeground(_compC);
		_title.setFont(new Font(_title.getFont().getFontName(), Font.BOLD, _title.getFont().getSize()));
        addToPanel(this, _title);  
        _name = new JTextField(((HandlerController)getController()).getName());
        handlerNameChanged();
        _name.setBounds(110, 0, 80, 30);
        addToPanel(this, _name); 
	}

	private void handlerNameChanged() {
		_name.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				((HandlerController)getController()).changeName(_name.getText());
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				((HandlerController)getController()).changeName(_name.getText());
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				((HandlerController)getController()).changeName(_name.getText());
			}
		});
	}

	private void addConnectAction() {
		_b.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!((HandlerController)getController()).hasEvent()){
					_b.setBackground(Color.GREEN);
					getPanel().pressedButton(_b, (BlockModel)getModel());
				}
				
			}
		});
		
	}

	@Override
	public void makeShapes() {
		Rectangle top = new Rectangle(0, 0, getWidthSize()  , _topHeight -1);
		addShape(top, _c, _c);
		
		Rectangle left = new Rectangle(0, _topHeight , _leftWidth, getHeightSize()+20);
		addShape(left,  _c, _c);
		
		Rectangle bottom = new Rectangle(0, getHeightSize() - _bottomHeight , _bottomWidth, _bottomHeight);
		addShape(bottom,  _c, _c);
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
		BlockView view = _body.getSelectedComponent(x-_body.getX(), y-_body.getY());
		if (view != null) return view;
		return this;
	}

	@Override
	public BlockView getBlockToUnsnap(int x, int y) {
		if (!_body.canUnsnap()) return null;
		
		BlockView rec = _body.getBlockToUnsnap(x-_body.getX(), y-_body.getY());
		
		if (rec == _body) {
			// Unsnap entire body
			AnchorBlock block = _body;
			AnchorBlock b = new AnchorBlock(((HandlerController)getController()).resetBody(), null, getPanel(),getLang());
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
		
		return rec;
	}

	@Override
	public BlockView getBlockToSnapScreen(int x, int y, Component comp) {
		if(!containsBlock(x, y)) return null;

		BlockView v = getBlockToSnap(x-getX(), y-getY(),comp);
		return v;
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
		_body.add(comp);
		captureComponents();
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
		
		for (Component cinner : getInnerComponents()) {
			addToPanel(this, cinner);
		}
		
		_body.resetPositions();
		
		// Resetting position
		_body.setLocation(_leftWidth + 3, _currentInsertY + 2);
		
		setHeightSize(getHeightSize() + _body.getHeightSize());
		setWidthSize( Math.max(getWidthSize(), _body.getWidthSize()+_leftWidth+5));
		
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
		return ((HandlerController)getController()).acceptConnected((BlockView)comp);
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
		view.setLocation(_leftWidth + 3, _currentInsertY + 2);
		setHeightSize(getHeightSize() + view.getHeightSize());

		setWidthSize(Math.max(getWidthSize(), view.getWidthSize()));
		//_currentInsertY += view.getHeightSize();
		setBounds(getX(), getY(), getWidthSize(), getHeightSize());
		
		updateShapes();
	}
	
    @Override
    public void paintComponent(Graphics g) {
    	Graphics2D g2d = (Graphics2D) g.create();

    	Point p = getLocation();
    	if (getParentBlock() != null) g2d.translate(p.getX(), p.getY());
   
    	// Draw the body
        _body.paintComponent(g2d);
        g2d.dispose();
        
        super.paintComponent(g);
    }

	@Override
	public void makeTransparant(boolean trans) {
		setTransparency(trans);
		_body.makeTransparant(trans);
	}

	@Override
	public Controller defaultController(Observable model) {
		return new HandlerController(getModel());
	}

	@Override
	public void updateLanguage() {
		_title.setText(_lang.getString("Bhandler"));
	}

	@Override
	public void removeBlock() {
		if(_body != null)
			_body.removeBlock();
		
	}
	
	@Override
	public String getHelpMenu() {
		return _lang.getString("BhandlerHelp");
	}
}
