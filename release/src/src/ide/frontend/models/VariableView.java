package ide.frontend.models;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Observable;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ide.backend.language.LanguageModule;
import ide.frontend.mvcbase.AbstractView;
import ide.frontend.mvcbase.Controller;

/**
 * Shows the variables within the event view
 * @author axel
 *
 */
public class VariableView extends AbstractView {
	/**FIELDS**/
	private static HashMap<String, String> _typeStrings;
	private JPanel _main;
	private String _types[] ;
	private JComboBox<String> _spinner;
	private String _name;
	private LanguageModule _lang;
	private JTextField _nameLabel;
	private JLabel _nameInfoLabel;
	private JLabel _typeInfoLabel;
	private boolean _type;
	
	private ActionListener _listener;

	
	public VariableView(Observable model, Controller controller, LanguageModule lang, boolean type) {
		super(model, controller);
		model.addObserver(this); 
		_lang = lang;
		_lang.addObserver(this);
		_type = type;
		initTypes();
		initName();
		initViews();
		
	}
	
	/**
	 * Initiate the views
	 */
	private void initViews() {
		if(!_type){
			_main = new JPanel(new GridLayout());
			_nameInfoLabel = new JLabel(_lang.getString("varName") + ": ");
			_nameInfoLabel.setFont(new Font(_nameInfoLabel.getFont().getName(), Font.BOLD,_nameInfoLabel.getFont().getSize()) );
			_main.add(_nameInfoLabel);
			JPanel fieldPanel = new JPanel(new BorderLayout());
			fieldPanel.add(_nameLabel, BorderLayout.WEST);
			_main.add(fieldPanel);
			_nameLabel.setText(_name);
		}
		else{
			_main = new JPanel();
		}
	
		_typeInfoLabel = new JLabel(_lang.getString("varType") + ": ");
		_typeInfoLabel.setFont(new Font(_typeInfoLabel.getFont().getName(), Font.BOLD,_typeInfoLabel.getFont().getSize()) );
		_main.add(_typeInfoLabel);
		_main.add(_spinner);
		
	}

	/**
	 * Initiate the variable name
	 */
	private void initName() {
		_name = ((VariableController)getController()).getName();
		_nameLabel = new JTextField(_name);
		_nameLabel.setEditable(false);
	}

	/**
	 * Initiate the variable types
	 */
	private void initTypes(){
		_types = new String[3];
		if(_typeStrings == null )
			_typeStrings = new HashMap<String, String>();
		
		_types[0] = _lang.getString("TypeNumber");
		_typeStrings.put(_lang.getString("TypeNumber"),"TypeNumber");
		_types[1] = _lang.getString("TypeString");
		_typeStrings.put(_lang.getString("TypeString"),"TypeString");
		_types[2] = _lang.getString("TypeBoolean");
		_typeStrings.put(_lang.getString("TypeBoolean"),"TypeBoolean");
		
		_spinner = new JComboBox<>(_types);

		_listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				((VariableController)getController()).setType(_typeStrings.get((String)_spinner.getSelectedItem()));
			}
			
		};
		_spinner.setSelectedItem( _lang.getString(((VariableController)getController()).getType()));
		_spinner.addActionListener(_listener);
	}
	

	
	@Override
    public Controller defaultController(Observable model) {
        return new VariableController(this.getModel());
    }

	@Override
	public void update(Observable o, Object arg) {
		if(o.getClass() == LanguageModule.class)
			updateLang();
	}

	/**
	 * Update the language of this class
	 */
	private void updateLang() {
		//updates types in spinners;
		_types[0] = _lang.getString("TypeNumber");
		_typeStrings.put(_lang.getString("TypeNumber"),"TypeNumber");
		_types[1] = _lang.getString("TypeString");
		_typeStrings.put(_lang.getString("TypeString"),"TypeString");
		_types[2] = _lang.getString("TypeBoolean");
		_typeStrings.put(_lang.getString("TypeBoolean"),"TypeBoolean");
	
		int index = _spinner.getSelectedIndex();
		_spinner.removeActionListener(_listener);
		_spinner.removeAllItems();
		
		_spinner.addItem(_types[0]);
		_spinner.addItem(_types[1]);
		_spinner.addItem(_types[2]);
		
		_spinner.setSelectedIndex(index);
		
		//update labels
		if (_nameInfoLabel != null)
			_nameInfoLabel.setText(_lang.getString("varName") + ": ");
		_typeInfoLabel.setText(_lang.getString("varType") + ": ");
		
	}

	/**
	 * Get the UI element
	 * @return
	 */
	public JPanel getUI() {
		return _main;
	}
	
}
