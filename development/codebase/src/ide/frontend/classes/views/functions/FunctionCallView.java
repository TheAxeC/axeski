package ide.frontend.classes.views.functions;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Observable;

import javax.swing.JLabel;

import ide.backend.language.LanguageModule;
import ide.frontend.classes.IDEPanel;
import ide.frontend.classes.views.BlockView;
import ide.frontend.mvcbase.Controller;

public class FunctionCallView extends BlockView{
	private static final long serialVersionUID = 1L;

	private ArrayList<ParameterView> _body;
	private ParameterView _return ;
	
	private  int _startHeight = 50;
	private  int _startWidth = 100;
	
	private static final Color _color = new Color(95,158,209);
	

	private int _leftWidth;

	private JLabel _title;

	private int _space;
	private int _insert;

	private int _varWidth;

	public FunctionCallView(Observable model, Controller controller, IDEPanel p, LanguageModule lang) {
		super(model, controller, p, lang);
		_body = new ArrayList<ParameterView>();
		_return = null;
		_leftWidth = 100;
		_space = 10;
		_varWidth = 70;
		_insert = 0;
		
	}
	
	@Override
	public void set(int x, int y) {
		init(x,y,_startWidth,_startHeight);
		loadParams();
		updateShapes();
		resetPositions();
	}

	private void loadParams() {
		_body = new ArrayList<ParameterView>();
		for(int i = 0 ; i < ((FunctionCallController)getController()).getAmountOfParameters() ; i++){
			_body.add(new ParameterView(_leftWidth + (i * (_space+_varWidth)), 10));
			_startWidth += _space+_varWidth;
		}
		
		if(((FunctionCallController)getController()).getAmountOfReturns() > 0){
			JLabel arrow = new JLabel("->");
			arrow.setBounds(_startWidth, 10, 20, 20);
	        addToPanel(this, arrow); 
	        _return = new ParameterView(_startWidth + 30, 10);
	        _startWidth += (_space+_varWidth) + 30;
		}

	}

	@Override
	public void makeTransparant(boolean trans) {
		setTransparency(trans);
			if (_body != null){
	    		for (ParameterView block : _body) {
					if(block.getContent() != null)
						block.getContent().makeTransparant(trans);
				}
	    	}
			if(_return != null){
				if(_return.getContent() != null)
					_return.getContent().makeTransparant(trans);
			}
		
	}

	@Override
	public void addComponents() {
		_title = new JLabel(_lang.getString("bfunccall")+ " " +((FunctionCallController)getController()).getFuncName());
		_title.setBounds(5, 0, 100, 20);
		_title.setFont(new Font(_title.getFont().getFontName(), Font.BOLD, _title.getFont().getSize()));
        addToPanel(this, _title);   
	}

	@Override
	public void makeShapes() {
		Rectangle top = new Rectangle(0, 0, getWidthSize(), getHeightSize());
		if(((FunctionCallController)getController()).getError())
			addShape(top, Color.red, _color);
		else
			addShape(top, _color, _color);
		for (ParameterView block : _body) {
				block.makeShapes(this);
		}
		if (_return != null)
			_return.makeShapes(this);
	}

	@Override
	protected void checkUnsnap(int x, int y, int screenX, int screenY) {
		
	}
	
	@Override
	public BlockView getSelectedComponent(int x, int y) {
		if (_body != null) {
			for (ParameterView block : _body) {
				if(block.getContent() != null && block.containsBlock(x - getX(), y - getY()))
					return block.getContent();
			}
		}
		if(_return != null){
			if(_return.getContent() != null && _return.containsBlock(x - getX(), y - getY()))
				return _return.getContent();
		}
		
		if (containsBlock(x, y)) return this;
		return null;
	}

	@Override
	public BlockView getBlockToUnsnap(int x, int y) {
		if ((_body == null)) return null;
		int i = 0;
		for (ParameterView block : _body) {
			if(block.getContent() != null && block.containsBlock(x, y)){
				BlockView out = block.getContent();
				block.setContent(null);
				((FunctionCallController)getController()).removeParam(i);
				for (Component cinner : out.getInnerComponents()) {
					removeFromPanel(cinner);

					out.addToPanel(out, cinner);
				}
				return out;
			}
			i++;
		}
		
		if(_return != null){
			if(_return.getContent() != null && _return.containsBlock(x, y)){
				BlockView out = _return.getContent();
				_return.setContent(null);
				((FunctionCallController)getController()).removeReturn(out);
				
				for (Component cinner : out.getInnerComponents()) {
					removeFromPanel(cinner);

					out.addToPanel(out, cinner);
				}
				
				return out;
			}
		}
		
		
		
		return null;
	}

	@Override
	public BlockView getBlockToSnapScreen(int x, int y, Component comp) {
		return null;
	}

	@Override
	public BlockView getBlockToSnap(int x, int y, Component c) {
		if(!containsBlock(x + getX(), y + getY())) return null;
		if (_body != null && acceptBlock(c)){
			_insert = 0;
			for (ParameterView block : _body) {
				if(block.getContent() == null && block.containsBlock(x, y))
					return this;
				_insert++;
			}
		}
		if(_return != null && acceptBlock(c) ){
			if(_return.getContent() == null && _return.containsBlock(x, y))
				return this;
		}
		return null;
	}

	@Override
	public Component add(Component comp) {
		
		if(_insert < _body.size())
			((FunctionCallController)getController()).addParam(_insert, (BlockView) comp);
		else
			((FunctionCallController)getController()).addReturn((BlockView) comp);
		addView((BlockView) comp);
		return comp;
	}
	
	@Override
	public BlockView addView(BlockView comp) {
		comp.setParentBlock(this);
		if(_insert < _body.size())
			_body.get(_insert).setContent(comp);
		else
			_return.setContent(comp);
		_insert++;
		
		Point p1 = comp.getLocation();
		for (Component cinner : comp.getInnerComponents()) {
			Point p = cinner.getLocation();
			p.setLocation(((int) p1.getX()) + p.getX(),( (int) p1.getY()) + p.getY());
			
			cinner.setLocation(p);

			this.addToPanel(comp, cinner);
		}
		
		this.repaint();
		return comp;
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
		
		setBounds(getX(), getY(), getWidthSize(), getHeightSize());
		updateShapes();
		this.repaint();
	}

	@Override
	protected boolean acceptBlock(Component comp) {
		return ((FunctionCallController)getController()).acceptVar((BlockView) comp);
	}

	@Override
	public Controller defaultController(Observable model) {
		return new FunctionCallController(getModel());
	}
	
    @Override
    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	Graphics2D g2d = (Graphics2D) g.create();

    	Point p = getLocation();
    	if (getParentBlock() != null) g2d.translate(p.getX(), p.getY());
   
    	// Draw the body
    	if (_body != null){
    		for (ParameterView block : _body) {
				if(block.getContent() != null)
					block.getContent().paintComponent(g2d);
			}
    	}
    	if(_return != null){
 
    		if(_return.getContent() != null)
    			_return.getContent().paintComponent(g2d);
    	}
    		
        g2d.dispose();
    }

	@Override
	public void updateLanguage() {
		_title.setText(_lang.getString("bfunccall")+ " " +((FunctionCallController)getController()).getFuncName());
	}

	@Override
	public void removeBlock() {
		
		
	}
	
	@Override
	public String getHelpMenu() {
		return _lang.getString("bfunccallHelp");
	}
	
	
}
