package ide.frontend.events;

import ide.backend.language.LanguageModule;
import ide.backend.model.event.EventModel;
import ide.frontend.mvcbase.AbstractView;
import ide.frontend.mvcbase.Controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * This view is used for creating/defining new events in the IDE.
 * @author Matthijs Kaminski
 *
 */
public class EventCreationView extends AbstractView {
	
	/**Fields**/
	//main panel of the view
	private JSplitPane _main;
	//listmodel containing all the existing events.
	private DefaultListModel<String> _eventsListModel;
	//list showing all existing events.
	private JList<String> _events;
	//scrollpane containing the list.
	private JScrollPane _scrollPane;
	//left panel of this view, containing list of events and buttons for creating/deleting a event.
	private JPanel _left;
	//selected event from the list
	private JPanel _selectedEvent;
	//right panel of the view containing the selected event.
	private JPanel _right;
	//button for adding a new event (left).
	private JButton _addEvent;
	//button for removing a event (left),
	private JButton _removeEvent;
	//language module of the ide.
	private LanguageModule _lang;
	//hashmap mapping names of event on their views.
	private HashMap<String, EventView> _eventViews;
	//panel containg the left buttons (add and remove).
	private JPanel _leftButtons;
	
	/**
	 * Creates a new EventCreationView for creating new events.
	 * @param model Event CreationModel of this view
	 * @param controller controller to its model (if null default controller is created)
	 * @param lang languagemodule of the ide.
	 */
	public EventCreationView(Observable model, Controller controller, LanguageModule lang) {
		super(model, controller);
		model.addObserver(this);
		_lang = lang;
		lang.addObserver(this);
		initViews();
		
	}

	/**
	 * init the views.
	 */
	private void initViews() {
		//init left panel
		_left = new JPanel(new BorderLayout());
		_left.setBorder(BorderFactory.createTitledBorder("Events"));
		_right = new JPanel(new GridBagLayout());
		_leftButtons = new JPanel(new GridLayout(2,1));
		_left.add(_leftButtons,BorderLayout.SOUTH);
		 initEventList(); 
		 //init right panel
		 _selectedEvent = new JPanel();
		 _selectedEvent.add(new JLabel("no events"));
		 //make split pane
		  _main = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
						_left, _right);
			_main.setOneTouchExpandable(true);
			_main.setResizeWeight(0.05); 
		//add selected event on right panel.
		_right.add(_selectedEvent);
		//init left buttons.
		addEventButtonInit();
		addRemoveButton();
	}
	
	
	/**
	 * init the eventlist and its view.
	 */
	private void initEventList() {
		_eventsListModel = new DefaultListModel<String>();
		_eventViews = new HashMap<String, EventView>();
		_events = new JList<String>(_eventsListModel);

		_events.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_events.setSelectedIndex(0);
		_events.setVisibleRowCount(5);
		_scrollPane = new JScrollPane(_events);
		_left.add(_scrollPane, BorderLayout.CENTER);

		_events.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				_right.remove(_selectedEvent);
				if (!_eventViews.isEmpty()
						&& _eventViews.get(_events.getSelectedValue()) != null)
					_selectedEvent = _eventViews
							.get(_events.getSelectedValue()).getUI();
				if (_eventViews.isEmpty() || _selectedEvent == null) {
					_selectedEvent = new JPanel();
					_selectedEvent.add(new JLabel("no events"));
				}
				_right.add(_selectedEvent);
				_selectedEvent.repaint();
				_right.revalidate();
				_right.repaint();

			}
		});
	}
	
	/**
	 * Init button for adding a new event. 
	 * Event name must be unique and length > 0.
	 */
	private void addEventButtonInit(){
		_addEvent = new JButton(_lang.getString("addEventButton"));
		_leftButtons.add(_addEvent);
		_addEvent.addActionListener(new ActionListener() {
			
			//create a new dialog for adding a new event.
			@Override
			public void actionPerformed(ActionEvent e) {
				JDialog dialog = new JDialog();
				dialog.setAlwaysOnTop(true);
				Component root = SwingUtilities.getRoot(_main);
				dialog.setBounds(root.getWidth()/2 - root.getWidth()/8, root.getHeight()/2 - root.getHeight()/8 ,root.getWidth()/4, root.getHeight()/4);
				dialog.setTitle(_lang.getString("addEventDialog"));
				JTextField input = new JTextField("");
				input.setSize(dialog.getWidth()/2, input.getHeight());
				JLabel error = new JLabel("                                    ");
				JButton add = new JButton(_lang.getString("addEvent"));
				dialog.getRootPane().setDefaultButton(add);
				JButton cancel = new JButton(_lang.getString("addEventCancel"));
				layoutingDialog(dialog, input, error, add, cancel);
				//add  button.
				add.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						if(!input.getText().trim().equals("") 
								&& ((EventCreationController)getController()).isDefaultEvent(input.getText().trim()) 
								&& !_eventsListModel.contains(input.getText())){
							addEvent(input.getText());
							dialog.setVisible(false);
						}else{
							error.setText(_lang.getString("addEventFail"));
						}						
					}
				});
				//cancel button
				cancel.addActionListener(new ActionListener() {					
					@Override
					public void actionPerformed(ActionEvent e) {
						dialog.setVisible(false);						
					}
				});						
			}			
		});		
	}
	
	/**
	 * add remove button for removing an event.
	 */
	private void addRemoveButton(){
		_removeEvent = new JButton(_lang.getString("removeEvent"));
		_leftButtons.add(_removeEvent);
		_removeEvent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String d = _events.getSelectedValue();
				EventView v = _eventViews.get(d);
				if(v != null){
					((EventCreationController)getController()).removeEvent((EventModel)v.getModel());
					_eventViews.remove(d);
					_eventsListModel.removeElement(d);
					_events.setSelectedIndex(0);				
				}				
			}
		});		
	}
	
	/**
	 * Layouting for the dialog.
	 * @param dialog dialog being layout
	 * @param input input field
	 * @param error error label
	 * @param add add button
	 * @param cancel cancel button
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
	 * add an event to the model and create a view for tit
	 * @param text name of the new event being created.
	 */
	private void addEvent(String text) {
		EventModel m = ((EventCreationController)getController()).addEvent(text);
		EventView v = new EventView(m, null, _lang, new Dimension(_main.getWidth()/4, _left.getHeight()/2));
		_eventViews.put(text, v);
		_eventsListModel.addElement(text);
		_events.setSelectedIndex(_eventsListModel.indexOf(text));
		
		
	}
	
	/**
	 * Return the ui of this view.
	 * @return the ui
	 */
	public JSplitPane getUI(){
		return _main;
	}
	
	@Override
    public Controller defaultController(Observable model) {
        return new EventCreationController(this.getModel());
    }

	@SuppressWarnings("unchecked")
	@Override
	public void update(Observable o, Object arg) {
		if(o.getClass() == LanguageModule.class)
			updateLang();
		else{
			//reset all and load existing events.
			updateTotalView((ArrayList<EventModel>) arg);
		}
	}
	
	/**
	 * update the language of this view.
	 */
	private void updateLang() {
		_addEvent.setText(_lang.getString("addEventButton"));
		_removeEvent.setText(_lang.getString("removeEvent"));
	}

	/**
	 * Resets the total view and loads existing events.
	 * @param models existing eventmodels.
	 */
	private void updateTotalView(ArrayList<EventModel> models) {
		_eventsListModel.clear();
		_eventViews.clear();
		for (EventModel m : models) {
			EventView v = new EventView(m, null, _lang, new Dimension(_left.getWidth()/4, _left.getHeight()/2));
			_eventViews.put(m.getType(), v);
			_eventsListModel.addElement(m.getType());
		}
		_events.setSelectedIndex(0);
		_events.revalidate();
		_events.repaint();
	}

	
}
