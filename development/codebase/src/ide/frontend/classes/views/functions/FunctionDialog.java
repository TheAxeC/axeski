package ide.frontend.classes.views.functions;

import ide.backend.language.LanguageModule;
import ide.backend.model.variables.VariableModel;
import ide.backend.variables.Variable.VariableType;
import ide.frontend.models.VariableView;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Dialog used to create new functions
 * @author axelfaes
 *
 */
public class FunctionDialog extends JDialog implements Observer {

	private static final long serialVersionUID = 1L;
	
	private static HashMap<String, String> _typeStrings;

	/**FIELDS**/
	private LanguageModule _lang;

	private JPanel _main;
	private JPanel _exitButtons;
	private JPanel _namePanel;
	private JPanel _returnPanel;	
	private JPanel _paramPanel;
	
	//views
	private JLabel _name;
	private JLabel _returnLabel;
	private String _types[] ;
	private JComboBox<String> _spinner;
	
	private JPanel _buttons;
	private JButton _addParam;
	private JButton _removeParam;

	private JPanel _currentView;
	private JLabel _noParams;
	private JComboBox<String> _members;
	
	//date structures.
	private HashMap<String, VariableView> _viewmap;
	private HashMap<String, VariableModel> _membermap;
	
	private FunctionView _view;
	private FunctionDialog _self;
	
	private String _nameFunction;

	/**
	 * Creates a view for creating new member variables for a given class.
	 * Change type of the variable and creating references to a variable.
	 * @param lang language model being used in the program.
	 * @param m classmodel for which members are created.
	 * @param select selectblock panel used to make references.
	 */
	public FunctionDialog(FunctionView view, LanguageModule lang) {
		_lang = lang;
		_lang.addObserver(this);
		
		_viewmap = new HashMap<String, VariableView>();
		_membermap = new HashMap<String, VariableModel>();
		
		_view = view;
		_self = this;
		_nameFunction = "";

		initNameViews();
		initParamViews();
		initReturnViews();
		initExitViews();
		
		_main = new JPanel();
		_main.setLayout(new BorderLayout());
		_main.add(_paramPanel, BorderLayout.CENTER);
		_main.add(_returnPanel, BorderLayout.SOUTH);
		
		this.setLayout(new BorderLayout());
		this.add(_namePanel, BorderLayout.NORTH);
		this.add(_main, BorderLayout.CENTER);
		this.add(_exitButtons, BorderLayout.SOUTH);
		
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	    this.addWindowListener(new WindowAdapter() {
	        @Override
	        public void windowClosing(WindowEvent arg0) {
				_view.getPanel().removeFromPanelAndClass(_view);
	        }
	    });
	}
	
	/**
	 * Initialise the view to set the name of the function
	 */
	private void initNameViews() {
		_namePanel = new JPanel();
		_name = new JLabel(_lang.getString("varName"));
		JTextField nameField = new JTextField();
		
		nameField.setColumns(20);
		nameField.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				_nameFunction = nameField.getText();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				_nameFunction = nameField.getText();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				_nameFunction = nameField.getText();
			}
		});
		
		_namePanel.add(_name);
		_namePanel.add(nameField);
	}
	
	/**
	 * Initialise the view to set the return type
	 */
	private void initReturnViews() {
		_returnPanel = new JPanel();
		
		initTypes();
		_returnLabel = new JLabel(_lang.getString("returnLabel"));
		
		_returnPanel.add(_returnLabel);
		_returnPanel.add(_spinner);
	}
	
	/**
	 * Initialise the exit views to skip the dialog
	 */
	private void initExitViews() {
		_exitButtons = new JPanel();

		JButton add = new JButton(_lang.getString("addFunction"));
		JButton cancel = new JButton(_lang.getString("cancelFunction"));
		JLabel error = new JLabel("                                    ");
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = _nameFunction;
				if(!name.trim().equals("") && _view.getPanel().functionNotExist(name)){
					((FunctionController)_view.getController()).changeName(name);
					
					// Get the return type
					if (_typeStrings.get(_spinner.getSelectedItem()) != null)
						((FunctionController)_view.getController()).setReturn(_typeStrings.get(_spinner.getSelectedItem()));
					
					_self.setVisible(false);
					
					_view.prepareFunction(_membermap);
				}else{
					error.setText(_lang.getString("addFunctionFail"));
				}
			}
		});
		
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_self.setVisible(false);
				_view.getPanel().removeFromPanelAndClass(_view);
			}
		});
		
		_exitButtons.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints(0, 0, 8, 1, 0, 0,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 80, 0);
		error.setForeground(Color.red);
		c.gridy = 1;
		c.ipady = 20;
		_exitButtons.add(error, c);
		c.gridwidth = 4;
		c.ipadx = 20;
		c.gridy = 3;
		_exitButtons.add(add, c);
		c.gridx = 4;
		_exitButtons.add(cancel, c);
	}
	
	/**
	 * Init the views of this view.
	 */
	private void  initParamViews(){
		_paramPanel = new JPanel();
		_paramPanel.setLayout(new BorderLayout());
		_currentView = new JPanel();
		initNoParams();
		initParams();
		
		initButtons();
		initAddParams();
		initRemoveParams();

		
		_paramPanel.add(_members, BorderLayout.NORTH);
		_paramPanel.add(_currentView, BorderLayout.CENTER);
		setCurrentView();
		_paramPanel.add(_buttons, BorderLayout.SOUTH);
		_buttons.add(_addParam);
		_buttons.add(_removeParam);
	}

	/**
	 * Creates a wrapper panel for the buttons.
	 */
	private void initButtons() {
		_buttons = new JPanel(new GridLayout(3,1));
	}

	/**
	 * Create button to remove the current selected member from the class.
	 */
	private void initRemoveParams() {
		_removeParam = new JButton(_lang.getString("removeParamVar"));
		_removeParam.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (_membermap.size() > 0) {
					_viewmap.remove(_members.getSelectedItem());
					_membermap.remove(_members.getSelectedItem());
					_members.removeItemAt(_members.getSelectedIndex());
					if(_members.getItemCount() > 0)
						_members.setSelectedIndex(0);
					setCurrentView();
				}
			}
		});
		
	}

	/**
	 * Create button for adding a member to the Class.
	 */
	private void initAddParams() {
		_addParam = new JButton(_lang.getString("addParamVar"));
		_addParam.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createNewParamDialog();
			}
		});
	}
	

	/***
	 * Create a new member variable
	 */
	private void addParam(String name) {
		VariableModel m = new VariableModel(null, name, VariableType.NUMBER);
		addParamToView(name, m);
		_members.setSelectedItem(name);
		_membermap.put(name, m);
		
	}

	/**
	 * Init the members of the class
	 */
	private void initParams() {
		_members = new JComboBox<String>();
		for (HashMap.Entry<String, VariableModel> entry : _membermap.entrySet()) {
			 addParamToView(entry.getKey(),entry.getValue());
		}
		if(_membermap.size() != 0)
			_members.setSelectedIndex(0);
		_members.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setCurrentView();
				
			}
		});
	}

	/**
	 * Creat a variable view for the new member and add it to the selector.
	 * @param name
	 * @param m
	 */
	private void addParamToView(String name, VariableModel m) {
		 _viewmap.put(name, new VariableView(m, null, _lang, true));
		 _members.addItem(name);
		
	}
	
	/**
	 * Sets the current view to the selected VariabelView.
	 * If not a message is shown.
	 */
	private void setCurrentView() {
		_currentView.removeAll();
		if(_members.getSelectedItem() != null){
			_currentView.add( _viewmap.get(_members.getSelectedItem()).getUI());
		}
		else
			_currentView.add(_noParams);
		
		this.revalidate();
		
	}

	/**
	 * Label to show if the class had no defined members.
	 */
	private void initNoParams(){
		_noParams = new JLabel(_lang.getString("noParams"));
	}

	/**
	 * Create a new dialog to create a new instance
	 */
	private void createNewParamDialog() {
		JDialog dialog = new JDialog();

		Component root = SwingUtilities.getRoot(_view);
		dialog.setBounds(root.getWidth()/2 - root.getWidth()/8, root.getHeight()/2 - root.getHeight()/8 ,root.getWidth()/4, root.getHeight()/4);
		//dialog.setBounds((int) (toGrab.getWidth() * 1.5), toGrab.getHeight(),toGrab.getWidth(), toGrab.getHeight()/2);
		dialog.setTitle(_lang.getString("addParamDialog"));
		JTextField input = new JTextField("");
		input.setSize(dialog.getWidth()/2, input.getHeight());
		JLabel error = new JLabel("                                    ");
		JButton add = new JButton(_lang.getString("addParamVarButton"));
		JButton cancel = new JButton(_lang.getString("addParamVarCancel"));
		layoutingDialog(dialog, input, error, add, cancel);
		dialog.setAlwaysOnTop(true);
		dialog.getRootPane().setDefaultButton(add);
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(input.getText().trim().length() > 0 && !_viewmap.containsKey(input.getText())){
					addParam(input.getText());
					dialog.setVisible(false);
				}else{
					error.setText(_lang.getString("addParamFail"));
				}
			}
		});
		
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
			}
		});
	}
	
	/**
	 * Layouting for the dialog
	 * @param dialog
	 * @param input
	 * @param error
	 * @param add
	 * @param cancel
	 */
	private void layoutingDialog(JDialog dialog, JTextField input,
			JLabel error, JButton add, JButton cancel) {
		dialog.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints(0, 0, 8, 1, 0, 0,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 80, 0);
		error.setForeground(Color.red);
		dialog.add(input, c);
		c.gridy = 1;
		c.ipady = 20;
		dialog.add(error, c);
		c.gridwidth = 4;
		c.ipadx = 20;
		c.gridy = 3;
		dialog.add(add, c);
		c.gridx = 4;
		dialog.add(cancel, c);
		dialog.setVisible(true);
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if(o instanceof LanguageModule){
			  updateLang();
		}
		
	}
	
	/**
	 * Initiate the variable types
	 */
	private void initTypes(){
		_types = new String[4];
		if(_typeStrings == null )
			_typeStrings = new HashMap<String, String>();
		
		_types[0] = _lang.getString("TypeNull");
		_typeStrings.put(_lang.getString("TypeNull"),"TypeNull");
		_types[1] = _lang.getString("TypeNumber");
		_typeStrings.put(_lang.getString("TypeNumber"),"TypeNumber");
		_types[2] = _lang.getString("TypeString");
		_typeStrings.put(_lang.getString("TypeString"),"TypeString");
		_types[3] = _lang.getString("TypeBoolean");
		_typeStrings.put(_lang.getString("TypeBoolean"),"TypeBoolean");
		
		_spinner = new JComboBox<>(_types);
	}

	/**
	 * Updates text of components to the given language.
	 */
	private void updateLang() {
		this.setTitle(_lang.getString("addFunctionDialog"));
		_addParam.setText(_lang.getString("addParamVar"));
		_removeParam.setText(_lang.getString("removeParamVar"));
		_noParams.setText(_lang.getString("noParams"));
		_name.setText(_lang.getString("varName"));
		
		//updates types in spinners;
		_types[0] = _lang.getString("TypeNull");
		_typeStrings.put(_lang.getString("TypeNull"),null);
		_types[1] = _lang.getString("TypeNumber");
		_typeStrings.put(_lang.getString("TypeNumber"),"TypeNumber");
		_types[2] = _lang.getString("TypeString");
		_typeStrings.put(_lang.getString("TypeString"),"TypeString");
		_types[3] = _lang.getString("TypeBoolean");
		_typeStrings.put(_lang.getString("TypeBoolean"),"TypeBoolean");
	
		int index = _spinner.getSelectedIndex();
		_spinner.removeAllItems();
		
		_spinner.addItem(_types[0]);
		_spinner.addItem(_types[1]);
		_spinner.addItem(_types[2]);
		_spinner.addItem(_types[3]);
		
		_spinner.setSelectedIndex(index);
		_returnLabel.setText(_lang.getString("returnLabel"));
	}
}
