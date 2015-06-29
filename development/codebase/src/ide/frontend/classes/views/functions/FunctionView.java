package ide.frontend.classes.views.functions;

import ide.backend.language.LanguageModule;
import ide.backend.model.BlockModel;
import ide.backend.model.ConnectedBlocks;
import ide.backend.model.function.FunctionCallModel;
import ide.backend.model.variables.AbstractRefVariabelModel;
import ide.backend.model.variables.VariableModel;
import ide.frontend.classes.IDEPanel;
import ide.frontend.classes.views.AnchorBlock;
import ide.frontend.classes.views.BlockView;
import ide.frontend.classes.views.variables.VariableRefView;
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
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Observable;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class FunctionView extends BlockView {

	private static final long serialVersionUID = 1L;

	/**
	 * Body of the if-statement
	 */
	private AnchorBlock _body;
	
	private int _startWidth;
	private int _startHeight;
	private static Color _c   = new Color(255, 128, 14);
	
	private final static int _topHeight = 40;
	private final static int _leftWidth = 20;
	private final static int _bottomWidth = 145;
	private final static int _bottomHeight = 25;

	private int _currentInsertY;
	
	private JLabel _title;
	private JButton _makeCall;
	private JLabel _name;
	private JLabel _returnType;
	
	private HashMap<JButton, String> _spawns;

	public FunctionView(Observable model, Controller controller, IDEPanel p, LanguageModule lang) {
		super(model, controller, p, lang);
	
		_currentInsertY = 40;
		
		_startWidth = 170;
		_startHeight = _topHeight + _bottomHeight+20;
		_spawns = new HashMap<>();
	}
	
	public static JPanel getRepresentative(LanguageModule lang){
		return new JPanel() {
			private static final long serialVersionUID = 1L;

			{
				setOpaque(false);
				setLayout(null);
				JLabel title = new JLabel(lang.getString("Bfunction"));
				title.setFont(new Font(title.getFont().getFontName(), Font.BOLD, title.getFont().getSize()));
				title.setBounds(5, 0, 80, 20);
				add(title);
			}
			
		    @Override
		    public void paintComponent(Graphics g) {
		    	super.paintComponent(g);
		    	
		    	Graphics2D g2d = (Graphics2D) g.create();

		    	Rectangle top = new Rectangle(0, 0, _bottomWidth + 20, _topHeight);
		    	draw(g2d, top, _c,_c);
				
				Rectangle left = new Rectangle(0,  _topHeight, _leftWidth, _topHeight + _bottomHeight + 20);
				draw(g2d, left,  _c,_c);
				
				Rectangle bottom = new Rectangle(0, _topHeight +_topHeight - _bottomHeight +20 , _bottomWidth, _bottomHeight);
				draw(g2d, bottom,  _c,_c );
		        
				g2d.dispose();
		    }
		    
		    @Override
		    public Dimension getPreferredSize() {
		    	return new Dimension(_bottomWidth + 20, _topHeight + _bottomHeight + 20  );
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
		
		AnchorBlock b = new AnchorBlock(((FunctionController)getController()).getBodymodel(), null, getPanel(),getLang());
		b.set(0, 0);
		setBody(b);
		
		JDialog dialog = new FunctionDialog(this, _lang);
		Component root = SwingUtilities.getRoot(getPanel());
		dialog.setBounds(root.getWidth()/2 - root.getWidth()/8, root.getHeight()/2 - root.getHeight()/4 ,root.getWidth()/4, root.getHeight()/2);
		//dialog.setBounds(getPanel().getWidth()/2 - getPanel().getWidth()/4, getPanel().getHeight()/4 - getPanel().getHeight()/12, getPanel().getWidth()/6, getPanel().getHeight()/6);
		dialog.setTitle(_lang.getString("addFunctionDialog"));
		dialog.setAlwaysOnTop(true);
		dialog.setVisible(true);
	}
	
	/**
	 * Set all function attributes
	 */
	public void prepareFunction(HashMap<String, VariableModel> params) {
		_name.setText(((FunctionController) getController()).getName());
		_returnType.setText(_lang.getString("returnLabel") + " " + _lang.getString(((FunctionController) getController()).getReturn()));;
	
		int insert = 160;
		for(HashMap.Entry<String, VariableModel> entry: params.entrySet()) {
			((FunctionController) getController()).addParam(entry.getKey(), entry.getValue());
			// make the button to spawn the parameter
			JButton spawn = new JButton(entry.getKey());
			spawn.setBounds(insert, 5, 70, 30);
			insert += 80;
			addToPanel(this, spawn); 
			String type = ((FunctionController)getController()).getType(entry.getValue().getType());
			spawn.setToolTipText(_lang.getString(type));
			spawn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// Spawn the block
					AbstractRefVariabelModel m = (AbstractRefVariabelModel)((FunctionController)getController()).makeRefParam(spawn.getText());
					VariableRefView v = new VariableRefView(m, null, getPanel(), getLang());
	                v.set(getX() + getWidthSize() , getY());
					getPanel().add(v, m);
					
					
				}
			});
			_spawns.put(spawn, type);
		}
		_startWidth = insert + 40;
		resetPositions();
	}
	
	/**
	 * Set all function attributes
	 */
	public void prepareFunctionLoad(HashMap<String, VariableModel> params) {
		_name.setText(((FunctionController) getController()).getName());
		_returnType.setText(_lang.getString("returnLabel") + " " + _lang.getString(((FunctionController) getController()).getReturn()));;
	
		int insert = 160;
		for(HashMap.Entry<String, VariableModel> entry: params.entrySet()) {
			((FunctionController) getController()).addParamLoad(entry.getKey(), entry.getValue());
			// make the button to spawn the parameter
			JButton spawn = new JButton(entry.getKey());
			spawn.setBounds(insert, 5, 70, 30);
			insert += 80;
			addToPanel(this, spawn); 
			String type = ((FunctionController)getController()).getType(entry.getValue().getType());
			spawn.setToolTipText(_lang.getString(type));
			spawn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// Spawn the block
					AbstractRefVariabelModel m = (AbstractRefVariabelModel)((FunctionController)getController()).makeRefParam(spawn.getText());
					VariableRefView v = new VariableRefView(m, null, getPanel(), getLang());
	                v.set(getX() + getWidthSize() , getY());
					getPanel().add(v, m);
					
					
				}
			});
			_spawns.put(spawn, type);
		}
		_startWidth = insert + 40;
		resetPositions();
	}
	
	/**
	 * Create the function when loading it from a file
	 * @param x x-position of the function
	 * @param y y-position of the function
	 */
	public void setLoad(int x, int y) {
		init(x,y,_startWidth,_startHeight);
		
		AnchorBlock b = new AnchorBlock(((FunctionController)getController()).getBodymodel(), null, getPanel(),getLang());
		b.set(0, 0);
		setBody(b);
	}

	@Override
	public void addComponents() {
		_makeCall = new JButton("...");
		makeCallButton();
		_makeCall.setBounds(getWidthSize() - 40, 5, 40, 30);
		addToPanel(this, _makeCall); 
		
		_title = new JLabel(_lang.getString("Bfunction") + ": ");
		_title.setBounds(10, 5, 80, 20);
		_title.setFont(new Font(_title.getFont().getFontName(), Font.BOLD, _title.getFont().getSize()));
        addToPanel(this, _title);
        
        _name = new JLabel(((FunctionController) getController()).getName());
        _name.setBounds(80, 5, 80, 20);
        addToPanel(this, _name); 
        
        _returnType = new JLabel(_lang.getString("returnLabel") + " " + _lang.getString(((FunctionController) getController()).getReturn()));
        _returnType.setBounds(10, getHeightSize() - 5, 150, 20);
        addToPanel(this, _returnType); 
	}
	
	/**
	 * Make the functioncall action listener
	 */
	private void makeCallButton() {
		_makeCall.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// Create the function call
				FunctionCallModel m = (FunctionCallModel)((FunctionController)getController()).makeRef();
				FunctionCallView v = new FunctionCallView(m, null, getPanel(), getLang());
                v.set(getX() + getWidthSize() , getY());
               
                BlockModel model = new ConnectedBlocks(null);
				BlockView v3 = new AnchorBlock(model, null, getPanel(), _lang); 
				v3.set(getX() + getWidthSize() , getY());
				v3.add(v);
				getPanel().add(v3, model);
			}
		});
	}
	
	@Override
	public void makeShapes() {
		Rectangle top = new Rectangle(0, 0, getWidthSize()  , _topHeight -1);
		addShape(top, _c, _c);
		
		Rectangle left = new Rectangle(0, _topHeight , _leftWidth, getHeightSize()+20);
		addShape(left, _c, _c);
		
		Rectangle bottom = new Rectangle(0, getHeightSize() - _bottomHeight , _bottomWidth, _bottomHeight);
		addShape(bottom, _c, _c);
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
			AnchorBlock b = new AnchorBlock(((FunctionController)getController()).resetBody(), null, getPanel(),getLang());
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
		_body.setLocation(_leftWidth, _currentInsertY);
		
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
		_makeCall.setBounds(getWidthSize() - 40, 5, 40, 30);
		_returnType.setLocation(10, getHeightSize() - 25);
	 
		setBounds(getX(), getY(), getWidthSize(), getHeightSize());
		updateShapes();
		this.repaint();
	}

	@Override
	protected boolean acceptBlock(Component comp) {
		return ((FunctionController)getController()).acceptConnected((BlockView)comp);
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
		view.setLocation(_leftWidth, _currentInsertY);
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
		return new FunctionController(getModel());
	}

	@Override
	public void updateLanguage() {
		_title.setText(_lang.getString("Bfunction")  + ": ");
		_returnType.setText(_lang.getString("returnLabel") + " " + _lang.getString(((FunctionController) getController()).getReturn()));
	
		for(Entry<JButton, String> entry: _spawns.entrySet()) {
			entry.getKey().setToolTipText(_lang.getString(entry.getValue()));
		}
	}

	@Override
	public void removeBlock() {
		if(_body != null)
			_body.removeBlock();
		
	}
	
	@Override
	public String getHelpMenu() {
		return _lang.getString("BfunctionHelp");
	}
}
