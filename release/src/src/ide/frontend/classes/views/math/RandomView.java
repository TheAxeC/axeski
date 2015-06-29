package ide.frontend.classes.views.math;

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

import ide.backend.language.LanguageModule;
import ide.frontend.classes.IDEPanel;
import ide.frontend.classes.views.BlockView;
import ide.frontend.mvcbase.Controller;

public class RandomView extends BlockView{
	private static final long serialVersionUID = 1L;
	private static final int _startWidth = 80;
	private static final int _startHeight = 30;
	
	private static final Color _color = new Color(255,221,113);
	
	private JLabel _name;


	public RandomView(Observable model, Controller controller, IDEPanel p, LanguageModule lang) {
		super(model, controller, p, lang);
		
		
	}

	public static JPanel getRepresentative(LanguageModule lang){
		return new JPanel() {
			private static final long serialVersionUID = 1L;

			{
				setOpaque(false);
				setLayout(null);
				JLabel title = new JLabel(lang.getString("Brandom"));
				title.setBounds(5, 0, 60, 20);
				title.setFont(new Font(title.getFont().getFontName(), Font.BOLD, title.getFont().getSize()));
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
		_name = new JLabel(_lang.getString("Brandom"));
		_name.setBounds(10, 5, _startWidth - 10, 25);
		_name.setFont(new Font(_name.getFont().getFontName(), Font.BOLD, _name.getFont().getSize()));
        addToPanel(this, _name); 
	}

	
	@Override
	public void makeShapes() {
		Rectangle top = new Rectangle(0, 0, getWidthSize(), getHeightSize());
		addShape(top, _color, _color);
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
		return new RandomViewController(getModel());
	}
	
	@Override
	public void updateLanguage() {

	}

	@Override
	public void removeBlock() {
		
		
	}
	
	@Override
	public String getHelpMenu() {
		return _lang.getString("BrandomHelp");
	}
}

