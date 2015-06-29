package ide.frontend.events;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Observable;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import ide.backend.language.LanguageModule;
import ide.backend.model.variables.VariableModel;
import ide.frontend.models.VariableView;
import ide.frontend.mvcbase.AbstractView;
import ide.frontend.mvcbase.Controller;

/**
 * This view is used to define and edit an Eventmodel which can than be used in
 * the program. 
 * @author Matthijs Kaminski
 *
 */
public class EventView extends AbstractView {

	/*FIELDS*/
	//store members on the names.
	private HashMap<String, VariableView> _members;
	//main panel
	private JPanel _main;
	//panel containing all the members.
	private JPanel _memberPanel;
	//button for adding a new member
	private JButton _addMember;
	//error label for incorrect name of new member
	private JLabel _error;
	//input field for new member name.
	private JTextField _input;
	//buttons 
	private JPanel _buttons;
	//language module of the ide.
	private LanguageModule _lang;
	
	/**
	 * Creates a new view for defining a event.
	 * @param model Event model being defined
	 * @param controller controller to the event (if null new default is created)
	 * @param lang language module used in the ide.
	 * @param dimension dimension of the parent view.
	 */
	public EventView(Observable model, Controller controller, LanguageModule lang, Dimension dimension) {
		super(model, controller);
		model.addObserver(this);
		_lang = lang;
		lang.addObserver(this);
		_members = new HashMap<String, VariableView>();
		initPanels();
		initInput();
		initAddButton();
	}
	
	
	/**
	 * init all the panels (size, layouting)
	 */
	private void initPanels(){
		_main = new JPanel(new BorderLayout()){
			private static final long serialVersionUID = 1L;

			@Override
			public Dimension getPreferredSize(){
				Container c = _main.getParent().getParent();

				int size = Math.min(c.getHeight(), c.getWidth());
				int other = 0;
				if(_memberPanel != null)
				 other = Math.max(_memberPanel.getWidth(), _memberPanel.getHeight());
				size = Math.max(other, size);
		        Dimension d = new Dimension((int)(size/1.5),(int)(size/1.5));
		        setMinimumSize(d);
		        return d;
			}
		};
		
		_main.setBorder(BorderFactory.createTitledBorder(((EventController)getController()).getName()));

		initMembersPanel();
		_buttons = new JPanel();
		_buttons.setLayout(new BoxLayout(_buttons, BoxLayout.Y_AXIS));
		_main.add(_buttons, BorderLayout.SOUTH);
	}

	


	/**
	 * init the member panel of the event and load existing members (on load).
	 */
	private void initMembersPanel() {
		_memberPanel = new JPanel();
		_memberPanel.setLayout(new BoxLayout(_memberPanel, BoxLayout.Y_AXIS));
		
		_main.add(new JScrollPane(_memberPanel), BorderLayout.CENTER);
		//add all the existing members.
		for(VariableModel m :((EventController)getController()).getMembers()){
			addMember(m);
		}
	}
	
	
	/**
	 * init input fields for new members.
	 */
	private void initInput(){
		_input = new JTextField("");
		_buttons.add(_input);
		_error = new JLabel("");
		_error.setHorizontalAlignment(JLabel.CENTER);
		_error.setForeground(Color.red);
		_buttons.add(_error);
	}
	
	/**
	 * init the button for adding a new member.
	 * Member name must be unique and have length > 0 (will be trimmed).
	 */
	private void initAddButton(){
		_addMember = new JButton(_lang.getString("addMemberButton"));
		_addMember.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//check conditions for new member
				if(_input.getText().trim().length() == 0 || _members.containsKey(_input.getText())){
					_error.setText(_lang.getString("addMemberError"));
				} else{
					_error.setText("");
					addMember(_input.getText());
					_input.setText("");
				}
				
			}
		});
		_buttons.add(_addMember);
	}
	
	/**
	 * add new member with given name
	 * @param name name of the new member
	 */
	public void addMember(String name){
		VariableModel m = ((EventController)getController()).addMember(name);
		addMember(m);
	}
	
	/**
	 * add view for the member to the panel
	 * @param m model of the variable
	 */
	public void addMember(VariableModel m ){
		VariableView v = new VariableView(m, null, _lang, false);
		_members.put(m.getName(), v);
		JPanel p = new JPanel();
		_memberPanel.add(p);
		p.add(v.getUI());
		JButton b = new JButton("X");
		b.setForeground(Color.red);
		b.setFont(new Font(b.getFont().getFontName(), Font.ITALIC, b.getFont().getSize()));
		p.add(b);
		b.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				removeMember(m.getName(), p);
				
			}
		});
		_memberPanel.revalidate();
		_memberPanel.repaint();
		
	}
	
	/**
	 * removes a given member from the event and it's view.
	 * @param name name of the variable being removed.
	 * @param m panel representing the variable in the view.
	 */
	private void removeMember(String name, JPanel m){
		((EventController)getController()).removeMember((VariableModel)_members.get(name).getModel());
		_members.remove(name);
		_memberPanel.remove(m);
		_memberPanel.revalidate();
		_memberPanel.repaint();
	}
	
	@Override
    public Controller defaultController(Observable model) {
        return new EventController(getModel());
    }

	@Override
	public void update(Observable o, Object arg) {
		if(o.getClass() == LanguageModule.class)
			updateLang();
	}

	/**
	 * update the language module
	 */
	private void updateLang() {
		if(_error.getText().length() != 0)
			_error.setText(_lang.getString("addMemberError"));
		_addMember.setText(_lang.getString("addMemberButton"));
		
	}
	
	/**
	 * return the UI of this view
	 * @return the ui
	 */
	public JPanel getUI(){
		return _main;
	}
}
