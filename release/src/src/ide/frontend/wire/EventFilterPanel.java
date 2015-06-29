package ide.frontend.wire;

import ide.backend.language.LanguageModule;
import ide.backend.model.event.EventModel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Implements the filter functionality for the wirepanel.
 * Checkbox's are generated for each possible event. An unchecked checkbox result in
 * the wires with that event not being drawn.
 * @author Matthijs Kaminski
 *
 */
public class EventFilterPanel extends JPanel implements Observer{
	private static final long serialVersionUID = 1L;

	/**FIELDS*/
	//all unchecked events
	private HashSet<EventModel> _unchecked;
	//all checkboxes (also unchecked)
	private HashMap< JCheckBox, EventModel> _checked;
	//language module of the ide
	private LanguageModule _lang;
	//wireCreatrion view
	private WireCreationView _v;
	//scrollpane containing the filter
	private JScrollPane _outer;
	//innerpanel (actual filter)
	private JPanel _inner;


	/**
	 * Creates a new Filter
	 * @param v the eventCreationview creating this view.
	 * @param lang the language module of the program.
	 */
	public EventFilterPanel(WireCreationView v, LanguageModule lang) {
		_unchecked = new HashSet<EventModel>();
		_checked = new HashMap<JCheckBox, EventModel>();
		_lang = lang;
		_lang.addObserver(this);
		_v = v;
		initView();
		initExistingEvents();
	}


	/**
	 * Create all panels.
	 */
	private void initView() {
		this.setBorder(BorderFactory.createTitledBorder(_lang.getString("filter")));
		this.setLayout(new BorderLayout());
		_outer = new JScrollPane();
		_outer.setBorder(null);
		_inner = new JPanel();
		_outer.setViewportView(_inner);
		_inner.setLayout(new BoxLayout(_inner, BoxLayout.Y_AXIS));
		_inner.setBorder(null);
		this.add(_outer);
	}


	/**
	 * load all existing events in the filter.
	 */
	private void initExistingEvents() {
		for (EventModel ev : _v.getExistingEvents()) {
			addEvent(ev);
		}
	}


	/**
	 * Add an event to the filter
	 * @param ev event model being added to the filter.
	 */
	public  void addEvent(EventModel ev){
		JCheckBox out = new JCheckBox(ev.getType());
		_checked.put( out, ev);
		out.setSelected(true);
		out.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//unchecked
				if(!out.isSelected())
					_unchecked.add(_checked.get(out));
				//checked
				else
					_unchecked.remove(_checked.get(out));
				//repaint the wires.
				_v.repaintWires();
			}
		});
		//add to the panel.
		addCheckBoxToPanel(out);
	}


	/**
	 * Add checkbox to the panel
	 * @param out checkbox being added to the panel.
	 */
	private void addCheckBoxToPanel(JCheckBox out) {
		_inner.add(out);
	}

	/**
	 * Remove an event from the filter.
	 * @param ev event being removed.
	 */
	public void removeEvent(EventModel ev){
		_unchecked.remove(ev);
		for(HashMap.Entry<JCheckBox, EventModel> entry: _checked.entrySet()){
			if(entry.getValue() == ev){
				_checked.remove(entry.getKey());
				removeCheckBoxFromPanel(entry.getKey());
				return;
			}
		}
	}

	/**
	 * Remove checkbox from the panel.
	 * @param out checkboc being removed.
	 */
	private void removeCheckBoxFromPanel(JCheckBox out) {
		_inner.remove(out);
	}


	/**
	 * Returns a set of all unchecked eventmodels.
	 * @return list of all unchecked event models
	 */
	public HashSet<EventModel> getUnchecked(){
		return _unchecked;
	}


	@Override
	public void update(Observable o, Object arg) {
		if(o instanceof LanguageModule){
			updateLang();
		}
	}


	/**
	 * Updated components text to new language.
	 */
	private void updateLang() {
		this.setBorder(BorderFactory.createTitledBorder(_lang.getString("filter")));
	}

	/**
	 * reset the total filter (if program is loaded)
	 */
	public void resetFilter(){
		//clear unchecked
		_unchecked.clear();
		//remove all checkboxes
		for(HashMap.Entry<JCheckBox, EventModel> entry: _checked.entrySet()){
			removeCheckBoxFromPanel(entry.getKey());
		}
		//clear checkboxes
		_checked.clear();
		//load program again.
		initExistingEvents();
	}





}
