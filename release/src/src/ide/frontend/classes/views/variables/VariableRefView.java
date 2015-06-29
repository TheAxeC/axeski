package ide.frontend.classes.views.variables;

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
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Observable;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * View for a reference to a variable.
 * @author matthijs
 *
 */
public class VariableRefView extends BlockView{
	private static final long serialVersionUID = 1L;
	private static final int _startWidth = 70;
	private static final int _startHeight = 30;
	
	private static final Color _color = new Color(255,221,113);
	
	private JLabel _content;
	private boolean _error;

	public VariableRefView(Observable model, Controller controller, IDEPanel p, LanguageModule lang) {
		super(model, controller, p, lang);
		_error = ((VariableRefViewController)getController()).getError();
		
	}

	public static JPanel getRepresentative(LanguageModule lang){
		return new JPanel() {
			private static final long serialVersionUID = 1L;

			{
				setOpaque(false);
				setLayout(null);
				JLabel title = new JLabel("");
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
	public void update(Observable arg0, Object arg1) {
		super.update(arg0, arg1);
		if (isInit()) {
			_content.setText(((VariableRefViewController)getController()).getName());
			_error = ((VariableRefViewController)getController()).getError();
			updateShapes();
			getPanel().repaint();
		}
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
		_content = new JLabel(((VariableRefViewController)getController()).getName());
		_content.setFont(new Font(_content.getFont().getFontName(), Font.BOLD, _content.getFont().getSize()));
		_content.setBounds(10, 5, _startWidth - 20, 25);
        addToPanel(this, _content); 
	}

	
	@Override
	public void makeShapes() {
		if(!_error){
			Rectangle top = new Rectangle(0, 0, getWidthSize(), getHeightSize());
			addShape(top, _color, _color);
		}else{
			Rectangle top = new Rectangle(0, 0, getWidthSize()-2, getHeightSize()-2);
			addShape(top, Color.RED, _color);
		}
	}

	@Override
	protected void checkUnsnap(int x, int y, int screenX, int screenY) {
		// Will never unsnap anything
	}
	
	@Override
	public BlockView getSelectedComponent(int x, int y) {
		if (containsBlock(x, y)) return this;
		return null;
	}

	@Override
	public BlockView getBlockToUnsnap(int x, int y) {
		return null;
	}

	@Override
	public BlockView getBlockToSnapScreen(int x, int y, Component comp) {
		return null;
	}

	@Override
	public BlockView getBlockToSnap(int x, int y, Component c) {
		return null;
	}

	@Override
	public Component add(Component comp) {
		return null;
	}

	@Override
	public BlockView addView(BlockView comp) {
		return null;
	}
	
	@Override
	public boolean notContainBlock(BlockView comp) {
		return true;
	}

	@Override
	public void resetPositions() {
		// Resetting the bounds
		setHeightSize( _startHeight);
		setWidthSize( _startWidth);
		
		// Set all components to the original positions
		for (Component cinner : getInnerComponents()) {
			addToPanel(this, cinner);
		}
		
		setBounds(getX(), getY(), getWidthSize(), getHeightSize());
		updateShapes();
		this.revalidate();
		this.repaint();
	}

	@Override
	protected boolean acceptBlock(Component comp) {
		return true;
	}

	@Override
	public Controller defaultController(Observable model) {
		return new VariableRefViewController(getModel());
	}
	
	@Override
	public void updateLanguage() {

	}

	@Override
	public void removeBlock() {
		
		
	}
	
	@Override
	public String getHelpMenu() {
		return _content.getText();
	}
}
