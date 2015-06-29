package ide.frontend.classes.views.functions;

import ide.backend.language.LanguageModule;
import ide.backend.model.event.EventModel;
import ide.backend.model.function.EmitModel;
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

public class EmitView extends BlockView {
	private static final long serialVersionUID = 1L;
	
	private static final int representationWidth = 150;
	private static final int representationHeight = 50;
	
	private static final int _startWidth = 230;
	private static final int _startHeight = 35;
	
	private static final Color _color = new Color(200,82,0);
	
	private boolean _canChange;
	private JComboBox<String> _dropdown;
	private EmitBox _emitBox;
	private JLabel _title;

	public EmitView(Observable model, Controller controller, IDEPanel p, LanguageModule lang) {
		super(model, controller, p, lang);
		_emitBox = new EmitBox(this, (EmitController) getController(), 0, _startHeight);
		_canChange = true;
	}

	public static JPanel getRepresentative(LanguageModule lang){
		return new JPanel() {
			private static final long serialVersionUID = 1L;

			{
				setOpaque(false);
				setLayout(null);
				JLabel title = new JLabel(lang.getString("Bemit"));
				title.setFont(new Font(title.getFont().getFontName(), Font.BOLD, title.getFont().getSize()));
				title.setBounds(5, 0, 100, 20);
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
	public void update(Observable o, Object arg) {
		if (isInit()) {
			if(arg != null && (arg instanceof String) && ((String)arg).equals("error")){
				super.update(o, arg);
			} else if (o instanceof EmitModel && arg != null && arg instanceof String && ((String) arg).equals("updateEmit") ) {
				String ev = (String) _dropdown.getSelectedItem();
	
				if (ev != null) {
					_emitBox.removeMembers();
					((EmitController)getController()).setEvent(getPanel().getClassModel(), getPanel().getAllEvents(), ev);	
					_emitBox.makeMembers();
					getPanel().resetPositions();
				}
			}
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
		_title = new JLabel(_lang.getString("Bemit"));
		_title.setBounds(5, 7, 80, 20);
		_title.setFont(new Font(_title.getFont().getFontName(), Font.BOLD, _title.getFont().getSize()));
        addToPanel(this, _title);   
        
		_dropdown = new JComboBox<String>();
		_dropdown.setBounds(85, 5, _startWidth - 90, 25);
        addToPanel(this, _dropdown); 
        
        setEventPicker();
        addListener();
	}
	
	private void addListener() {
		_dropdown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String ev = (String) _dropdown.getSelectedItem();

				if (ev != null && _canChange) {
					_emitBox.removeMembers();
					((EmitController)getController()).setEvent(getPanel().getClassModel(), getPanel().getAllEvents(), ev);	
					_emitBox.makeMembers();
					getPanel().resetPositions();
				}
			}
		});
	}
	
	/**
	 * Set the events on the dropdown list
	 */
	public void setEventPicker() {
		_canChange = false;
		String selected = (String) _dropdown.getSelectedItem();
		_dropdown.removeAllItems();
		boolean foundSelec = false;
		for(EventModel m: getPanel().getAllEvents()) {
			_dropdown.addItem(m.getType());
			
			if (selected != null && m.getType().equals(selected)) foundSelec = true;
		}
		_dropdown.setSelectedItem(selected);
		
		if (!foundSelec && selected != null) {
			// remove the selected event
			((EmitController)getController()).deselectEvent(getPanel().getClassModel());
			_emitBox.removeMembers();
			//resetPositions();
		}
		_canChange = true;
	}
	
	/**
	 * Set the event for the picker
	 * @param ev
	 */
	public void setEvent(String ev) {
		_canChange = false;
		_dropdown.setSelectedItem(ev);
		_canChange = true;
		((EmitController)getController()).generateData();
		_emitBox.makeMembers();
		resetPositions();
	}
	
	@Override
	public void makeShapes() {
		Rectangle top = new Rectangle(0, 0, _startWidth, _startHeight);
		if(!((EmitController)getController()).getError())
			addShape(top, _color, _color);
		else
			addShape(top, Color.red, _color);
		_emitBox.makeShapes();
	}

	@Override
	protected void checkUnsnap(int x, int y, int screenX, int screenY) {
		// Should never be used
		// The emit block can never appear without a containing connected block
	}
	
	@Override
	public BlockView getSelectedComponent(int x, int y) {
		if (!containsBlock(x, y)) return null;
		
		BlockView select = _emitBox.getSelectedComponent(x- getX(), y- getY());
		
		if (select == null) return this;
		else return select;
	}

	@Override
	public BlockView getBlockToUnsnap(int x, int y) {
		BlockView rec = _emitBox.getBlockToUnsnap(x-_emitBox.getX(), y-_emitBox.getY());
		
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
		// The emit block can never appear without a containing connected block
		return null;
	}

	@Override
	public BlockView getBlockToSnap(int x, int y, Component c) {
		return _emitBox.getBlockToSnap(x, y, c);
	}

	@Override
	public Component add(Component comp) {
		// Add to the controller
		_emitBox.addMember((BlockView) comp);
		addView((BlockView) comp);
		return comp;
	}
	
	@Override
	public BlockView addView(BlockView comp) {
		_emitBox.addView(comp);
		return comp;
	}
	
	/**
	 * Add the view for a member
	 * @param member
	 * @param comp
	 */
	public void addView(String member, BlockView comp) {
		_emitBox.addView(member, comp);
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
		
		_emitBox.resetPositions();
		
		setBounds(getX(), getY(), getWidthSize(), getHeightSize());
		updateShapes();
		this.revalidate();
		this.repaint();
	}
	
    @Override
    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	Graphics2D g2d = (Graphics2D) g.create();

    	Point p = getLocation();
    	if (getParentBlock() != null) g2d.translate(p.getX(), p.getY());
    	
    	_emitBox.paintComponent(g2d);
   
        g2d.dispose();
    }

	@Override
	protected boolean acceptBlock(Component comp) {
		return ((EmitController)getController()).acceptVar((BlockView) comp);
	}

	@Override
	public Controller defaultController(Observable model) {
		return new EmitController(model);
	}

	@Override
	public void updateLanguage() {
		_title.setText(_lang.getString("Bemit"));
	}

	@Override
	public void removeBlock() {
		getPanel().removeOutputEvent((String)_dropdown.getSelectedItem());	
	}
	
	@Override
	public String getHelpMenu() {
		return _lang.getString("BemitHelp");
	}
}
