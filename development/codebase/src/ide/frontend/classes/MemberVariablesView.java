package ide.frontend.classes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import ide.backend.language.LanguageModule;
import ide.backend.model.classes.ClassModel;
import ide.backend.model.variables.MemberModel;
import ide.backend.variables.Variable.VariableType;
import ide.frontend.models.VariableView;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * ClassView for adding memberVariables to a class and creating references to them.
 * @author Matthijs Kaminski
 *
 */
public class MemberVariablesView extends JPanel implements Observer{
	

	private static final long serialVersionUID = 1L;
	/**FIELDS**/
	private LanguageModule _lang;
	private ClassModelController _controller;
	private SelectBlocksPanel _select;
	
	//views
	private JPanel _buttons;
	private JButton _addMember;
	private JButton _removeMember;
	private JButton _makeReference;
	private JPanel _currentView;
	private JLabel _noMembers;
	private JComboBox<String> _members;
	
	//date structures.
	private HashMap<String, VariableView> _viewmap;
	private HashMap<String, MemberModel> _membermap;
	
	

	/**
	 * Creates a view for creating new member variables for a given class.
	 * Change type of the variable and creating references to a variable.
	 * @param lang language model being used in the program.
	 * @param m classmodel for which members are created.
	 * @param select selectblock panel used to make references.
	 */
	public MemberVariablesView(LanguageModule lang, ClassModel m, SelectBlocksPanel select) {
		_lang = lang;
		_lang.addObserver(this);
		_controller = new ClassModelController(m);
		_viewmap = new HashMap<String, VariableView>();
		_membermap = new HashMap<String, MemberModel>();
		_select = select;
		initViews();
	}
	
	/**
	 * Init the views of this view.
	 */
	private void  initViews(){
		this.setBorder(BorderFactory.createTitledBorder("Members"));
		this.setLayout(new BorderLayout());
		_currentView = new JPanel();
		initNoMembers();
		initMembers();
		
		initButtons();
		initAddMembers();
		initRemoveMembers();
		initMakeRef();
		this.add(_members, BorderLayout.NORTH);
		this.add(_currentView, BorderLayout.CENTER);
		setCurrentView();
		this.add(_buttons, BorderLayout.SOUTH);
		_buttons.add(_addMember);
		_buttons.add(_removeMember);
		_buttons.add(_makeReference);
		
	}

	/**
	 * Creates a button to make a reference to the current selected member.
	 */
	private void initMakeRef() {
		_makeReference = new JButton(_lang.getString("makeRefMember"));
		_makeReference.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(_members.getSelectedItem() != null){
					MemberModel m = _membermap.get(_members.getSelectedItem());
					//make ref of this models and give them to a Refview.
					_select.load(m);
				}
				
			}
		});
		
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
	private void initRemoveMembers() {
		_removeMember = new JButton(_lang.getString("removeMemberVar"));
		_removeMember.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (_members.getSelectedIndex() != -1) {
					_controller.removeMemberVar((String)_members.getSelectedItem());
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
	private void initAddMembers() {
		_addMember = new JButton(_lang.getString("addMemberVar"));
		_addMember.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createNewMemberDialog();
			}
		});
	}
	

	/***
	 * Create a new member variable
	 */
	private void addMember(String name) {
		MemberModel m = _controller.addMemberVar(name, VariableType.NUMBER);
		addMemberToView(name, m);
		_members.setSelectedItem(name);
		_membermap.put(name, m);
		
	}

	/**
	 * Init the members of the class
	 */
	private void initMembers() {
		loadExistingMemberNames();
		_members = new JComboBox<String>();
		for (HashMap.Entry<String, MemberModel> entry : _membermap.entrySet()) {
			 addMemberToView(entry.getKey(),entry.getValue());
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
	private void addMemberToView(String name, MemberModel m) {
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
			_currentView.add(_noMembers);
		
		this.revalidate();
		this.repaint();
	}

	/**
	 * Label to show if the class had no defined members.
	 */
	private void initNoMembers(){
		_noMembers = new JLabel(_lang.getString("noMembers"));
	}

	/**
	 * Create a new dialog to create a new instance
	 */
	private void createNewMemberDialog() {
		JDialog dialog = new JDialog();
		Component root = SwingUtilities.getRoot(this);
		dialog.setBounds(root.getWidth()/2 - root.getWidth()/8, root.getHeight()/2 - root.getHeight()/8 ,root.getWidth()/4, root.getHeight()/4);
		dialog.setTitle(_lang.getString("addMemberDialog"));
		JTextField input = new JTextField("");
		input.setSize(dialog.getWidth()/2, input.getHeight());
		JLabel error = new JLabel("                                    ");
		JButton add = new JButton(_lang.getString("addMemberVarButton"));
		JButton cancel = new JButton(_lang.getString("addMemberVarCancel"));
		dialog.setAlwaysOnTop(true);
		dialog.getRootPane().setDefaultButton(add);
		layoutingDialog(dialog, input, error, add, cancel);
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(input.getText().trim().length() > 0 && !_controller.containsMemberVar(input.getText())){
					addMember(input.getText());
					dialog.setVisible(false);
				}else{
					error.setText(_lang.getString("addMemberFail"));
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
	/**
	 * Load the existing members of the class.
	 */
	private void loadExistingMemberNames(){
		_membermap =  _controller.getMembers();
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if(o instanceof LanguageModule){
			  updateLang();
		}
		
	}

	/**
	 * Updates text of components to the given language.
	 */
	private void updateLang() {
		_addMember.setText(_lang.getString("addMemberVar"));;
		  _removeMember.setText(_lang.getString("removeMemberVar"));
		  _makeReference.setText(_lang.getString("makeRefMember"));
		  _noMembers.setText(_lang.getString("noMembers"));
	}
}
