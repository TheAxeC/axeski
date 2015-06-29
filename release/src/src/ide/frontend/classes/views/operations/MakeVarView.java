package ide.frontend.classes.views.operations;

import ide.backend.language.LanguageModule;
import ide.backend.model.variables.RefVariabelModel;
import ide.frontend.classes.IDEPanel;
import ide.frontend.classes.views.BlockView;
import ide.frontend.classes.views.variables.VariableRefView;
import ide.frontend.models.VariableController;
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
import java.util.Observable;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
/**
 * View for creating a new local variable. view for VariableModel
 */
public class MakeVarView extends BlockView{

	private static final long serialVersionUID = 1L;
	
	private static final int representationWidth = 150;
	private static final int representationHeight = 50;
	
	private static final int _startHeight = 55;
	private static final int _startWidth = 240;
	
	private static final Color _color = new Color(95,158,209);
	
	/**FIELDS*/
	private static HashMap<String, String> _typeStrings;
	private String _types[] ;
	private JComboBox<String> _spinner;
	private JTextField _nameField;
	private JButton _makeRefButton;
	private BlockView _self;
	private boolean _error;
	private JLabel _title;

	public MakeVarView(Observable model, Controller controller, IDEPanel p, LanguageModule lang) {
		super(model, controller, p, lang);
		_self = this;
	}

	public static JPanel getRepresentative(LanguageModule lang){
		return new JPanel() {
			private static final long serialVersionUID = 1L;

			{
				setOpaque(false);
				setLayout(null);
				JLabel title = new JLabel(lang.getString("Bmake"));
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
		super.update(o, arg);
		if (isInit()) {
			if(o instanceof LanguageModule)
				updateLang();
			else {
				_error = ((VariableController)getController()).getError();
				updateShapes();
				getPanel().repaint();
			}
		}
	}

	@Override
	public void set(int x, int y) {
		init(x,y,_startWidth,_startHeight);
		_nameField.setText(((VariableController)getController()).getName());
		_spinner.setSelectedItem(getLang().getString(((VariableController)getController()).getType()));
	}

	@Override
	public void makeTransparant(boolean trans) {
		setTransparency(trans);
		
	}

	@Override
	public void addComponents() {
		_title = new JLabel(_lang.getString("Bmake"));
		_title.setBounds(5, 0, 80, 20);
		_title.setFont(new Font(_title.getFont().getFontName(), Font.BOLD, _title.getFont().getSize()));
        addToPanel(this, _title);   
        initName();
        _nameField.setBounds(10, 20, 80, 30);
        addToPanel(this, _nameField);
        initTypes();
        _spinner.setBounds(95, 20, 90, 30);
        addToPanel(this, _spinner);
        initMakeRefButton();
        _makeRefButton.setBounds(190,20,40,30);
        addToPanel(this,_makeRefButton);
        
        addListener();
	}
	
	private void addListener() {
		_nameField.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				setContent();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				setContent();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				setContent();
			}
		});
		
	}
	
	private void setContent() {
		_error = ((VariableController)getController()).changeContent(_nameField.getText());
		updateShapes();
		getPanel().repaint();
	}

	@Override
	public void makeShapes() {
		if (_error) {
			Rectangle err = new Rectangle(0, 0, getWidthSize()-2, getHeightSize()-2);
			addShape(err, Color.RED, _color);
		} else {
			Rectangle top = new Rectangle(0, 0, getWidthSize(), getHeightSize());
			addShape(top, _color, _color);
		}
	}

	@Override
	protected void checkUnsnap(int x, int y, int screenX, int screenY) {
		// Should never be used
		// The print block can never appear without a containing connected block
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
		// Should never be used
		// The print block can never appear without a containing connected block
		return null;
	}

	@Override
	public BlockView getBlockToSnap(int x, int y, Component c) {
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
		
		for (Component cinner : getInnerComponents()) {
			addToPanel(this, cinner);
		}
		
		
		setBounds(getX(), getY(), getWidthSize(), getHeightSize());
		updateShapes();
		this.repaint();
	}

	@Override
	protected boolean acceptBlock(Component comp) {
		return ((VariableController)getController()).acceptSingle((BlockView) comp);
	}

	@Override
	public Controller defaultController(Observable model) {
		return new VariableController(getModel());
	}
	
    @Override
    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	Graphics2D g2d = (Graphics2D) g.create();

    	Point p = getLocation();
    	if (getParentBlock() != null) g2d.translate(p.getX(), p.getY());
 
        g2d.dispose();
    }
    

	/**
	 * Initiate the variable types
	 */
	private void initTypes(){
		_types = new String[3];
		if(_typeStrings == null )
			_typeStrings = new HashMap<String, String>();
		
		_types[0] = getLang().getString("TypeNumber");
		_typeStrings.put(getLang().getString("TypeNumber"),"TypeNumber");
		_types[1] = getLang().getString("TypeString");
		_typeStrings.put(getLang().getString("TypeString"),"TypeString");
		_types[2] = getLang().getString("TypeBoolean");
		_typeStrings.put(getLang().getString("TypeBoolean"),"TypeBoolean");
		
		_spinner = new JComboBox<>(_types);

		ActionListener _listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				((VariableController)getController()).setType(_typeStrings.get((String)_spinner.getSelectedItem()));
			}
			
		};
		
		_spinner.addActionListener(_listener);
	}
	
	/**
	 * Update the language of this class
	 */
	private void updateLang() {
		
		//updates types in spinners;
		_types[0] = getLang().getString("TypeNumber");
		_typeStrings.put(getLang().getString("TypeNumber"),"TypeNumber");
		_types[1] = getLang().getString("TypeString");
		_typeStrings.put(getLang().getString("TypeString"),"TypeString");
		_types[2] = getLang().getString("TypeBoolean");
		_typeStrings.put(getLang().getString("TypeBoolean"),"TypeBoolean");
	
		int index = _spinner.getSelectedIndex();
		
		_spinner.removeAllItems();
		
		_spinner.addItem(_types[0]);
		_spinner.addItem(_types[1]);
		_spinner.addItem(_types[2]);
		
		_spinner.setSelectedIndex(index);
		_title.setText(_lang.getString("Bmake"));

	}
	
	private void initName(){
		_nameField = new JTextField();
	}
	
	private void initMakeRefButton(){
		_makeRefButton = new JButton("REF");
		_makeRefButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (((VariableController)getController()).getName().trim().equals("")) return;
				
				RefVariabelModel m = (RefVariabelModel)((VariableController)getController()).makeRef();
				VariableRefView v = new VariableRefView(m, null, getPanel(), getLang());
				int x = 0;
				int y = 0;
				if(_self.getParentBlock().getParentBlock() != null){
					x = _self.getParentBlock().getParentBlock().getX() + _self.getParentBlock().getParentBlock().getWidthSize();
					y = _self.getParentBlock().getParentBlock().getY();
				}else{
					x = _self.getParentBlock().getX() + _startWidth + 10;
					y = _self.getParentBlock().getY();
				}

				v.set(  x , _makeRefButton.getY() + y);
				getPanel().add(v,m);
				
			}
		});
	}

	@Override
	public Component add(Component comp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BlockView addView(BlockView comp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateLanguage() {
		_title.setText(_lang.getString("Bmake"));
	}

	@Override
	public void removeBlock() {
		
		
	}
	
	@Override
	public String getHelpMenu() {
		return _lang.getString("BmakeHelp");
	}


}
