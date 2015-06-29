package ide.frontend.classes.views.functions;

import ide.backend.language.LanguageModule;
import ide.backend.model.event.EventModel;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The view that belongs to the accesblock
 * @author axelfaes
 *
 */
public class AccesView extends BlockView {
	private static final long serialVersionUID = 1L;
	private static final int _startWidth = 150;
	private static final int _startHeight = 35;
	
	private static final Color _color = new Color(200,82,0);
	
	private JComboBox<String> _dropdown;
	private JLabel _title;

	/**
	 * Constructor
	 * @param model, the model to observe
	 * @param controller, the controller (or null to use the default controller)
	 * @param p, the IDEPanel on which the accessView sits
	 * @param lang, the languagemodule
	 */
	public AccesView(Observable model, Controller controller, IDEPanel p, LanguageModule lang) {
		super(model, controller, p, lang);
	}

	public static JPanel getRepresentative(LanguageModule lang){
		return new JPanel() {
			private static final long serialVersionUID = 1L;

			{
				setOpaque(false);
				setLayout(null);
				JLabel title = new JLabel(lang.getString("Bacces"));
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
	public void update(Observable o, Object arg) {
		super.update(o, arg);
		if (isInit())
			setEventPicker();
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
		_title = new JLabel(_lang.getString("Bacces"));
		_title.setBounds(5, 7, 60, 20);
		_title.setFont(new Font(_title.getFont().getFontName(), Font.BOLD, _title.getFont().getSize()));
        addToPanel(this, _title);   
        
		_dropdown = new JComboBox<String>();
		_dropdown.setBounds(60, 5, _startWidth - 70, 25);
        addToPanel(this, _dropdown); 
        
        setEventPicker();
        addListener();
	}
	
	private void addListener() {
		_dropdown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				((AccesController)getController()).setSelectedMember((String)_dropdown.getSelectedItem());
			}
		});
	}
	
	/**
	 * Set the members on the dropdown list
	 */
	private void setEventPicker() {
		String oldSelected = ((AccesController)getController()).getSelectedMember();
		_dropdown.removeAllItems();
		
		EventModel m = 	((AccesController)getController()).getEventModel();
		if(m != null){
			
			for(String member : m.getMemberNames()){
				_dropdown.addItem(member);
			}
			if(oldSelected != null && m.getMemberNames().contains(oldSelected)){
				_dropdown.setSelectedItem(oldSelected);
				
			}
				
			else if(m.getMemberNames().size() != 0){
				
				_dropdown.setSelectedIndex(0);
			}
		}
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

		return comp;
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
		return new AccesController(model);
	}

	@Override
	public void updateLanguage() {
		_title.setText(_lang.getString("Bacces"));
	}

	@Override
	public void removeBlock() {
		getPanel().removeOutputEvent((String)_dropdown.getSelectedItem());	
	}

	@Override
	public String getHelpMenu() {
		return _lang.getString("BaccessHelp");
	}
}
