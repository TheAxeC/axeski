package ide.frontend.wire;

import ide.backend.language.LanguageModule;
import ide.backend.model.classes.ClassModel;
import ide.backend.model.classes.InstanceModel;
import ide.backend.model.classes.WireModel;
import ide.backend.model.event.EventModel;
import ide.frontend.mvcbase.AbstractView;
import ide.frontend.mvcbase.Controller;
import ide.frontend.wire.SelectInstancePanel.LoadClassInstance;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
/**
 * This class represent the main wireframe view in the ide.
 * It contains a splitpane consisting of selectinstance pane and filter on the right.
 * And the wirepanel for drawning connections between instanceViews and moving them on the left.
 * It also delegates the updates from its model through to all the views.
 * @author Matthijs Kaminski
 *
 */
public class WireCreationView extends AbstractView {
	
	/**
	 * FIELDS
	 */
	//main panel of this view.
	private JPanel _main;
	//splitpane
	private JSplitPane _splitPane;
	//instance selectpane for creating new instances.
	private SelectInstancePanel _instances;
	//wire panel for creating connections.
	private WirePanel _wirepanel;
	//filter for events.
	private EventFilterPanel _filter;
	//language module of the ide.
	private LanguageModule _lang;
	
	/**
	 * Creates a new wireCreationview
	 * @param model wireCreation model for making a connection to the modelcollection of the program.
	 * @param controller controller to the model. (null) will make a new controller
	 * @param lang language module of the program.
	 */
	public WireCreationView(Observable model, Controller controller, LanguageModule lang) {
		super(model, controller);
		model.addObserver(this);
		_lang = lang;
		lang.addObserver(this);
		initPanels();
	}
	
	/**
	 * Create all the panels.
	 */
	private void initPanels(){
		//main panel
		_main = new JPanel(new BorderLayout());
		_main.setBorder(null);
		//left container for select and filter.
		JPanel left = new JPanel(new GridLayout(2, 1));
		left.setBorder(null);
		//create wire panel
		_wirepanel = new WirePanel(this, _lang);
		//create select instance panel
		_instances = new SelectInstancePanel(_wirepanel, _lang);
		left.add(_instances.getUI());
		// create filter
		_filter = new EventFilterPanel(this, _lang);
		left.add(_filter);
		//make splitpane add add left and right.
		_splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					left,_wirepanel.getUI());
		_splitPane.setOneTouchExpandable(true);
		_splitPane.setResizeWeight(0.1);  
		//add splitpanel to main.
		_main.add(_splitPane);
	}
	
	/**
	 * Returns the loaded instance type (class)
	 * @return
	 */
	public LoadClassInstance getLoaded() {
		return _instances.getLoaded();
	}
	
	/**
	 * unload the selected instance type (class)
	 */
	public void unload(){
		 _instances.unload();
	}
	
	/**
	 * returns the UI of this panel.
	 * @return
	 */
	public JPanel getUI(){
		return _main;
	}
	
	@Override
    public Controller defaultController(Observable model) {
        return new WireCreationController(this.getModel());
    }

	@Override
	public void update(Observable o, Object arg) {
		if(o.getClass() == LanguageModule.class)
			updateLang();
		else if(arg != null){
			
			if(((String)((Object[]) arg)[0]).equals("addClass")){
				_instances.addClass((ClassModel)((Object[]) arg)[1]);
			}
			else if(((String)((Object[]) arg)[0]).equals("deleteClass")) {
				_instances.deleteClass(((ClassModel)((Object[]) arg)[1]));
				_wirepanel.removeInstances(((ClassModel)((Object[]) arg)[1]).getName());
			}else if(((String)((Object[]) arg)[0]).equals("addEvent")) {
				_filter.addEvent((EventModel)((Object[]) arg)[1]);
			}else if(((String)((Object[]) arg)[0]).equals("deleteEvent")) {
				_filter.removeEvent((EventModel)((Object[]) arg)[1]);
			}
		}else{
			updateTotalView();
		}
	}

	/**
	 * Updates the totalv view. (reset)
	 */
	private void updateTotalView() {
		_instances.resetClasses(((WireCreationController)getController()).getClasses());
		_wirepanel.clear();
		//all new instances.
		for(InstanceModel m: ((WireCreationController)getController()).getInstanceModels()) {
			_wirepanel.add(new InstanceView(m, null, _lang, _wirepanel), null);
		}
		//all new wires.
		for(WireModel w: ((WireCreationController)getController()).getWireModels()) {
			_wirepanel.addExistingWire(w.getFrom(), w.getEvent(), w.getTo());
		}
		//reset filter
		_filter.resetFilter();
	}
	
	/**
	 * update  the language of the view.
	 */
	private void updateLang() {
				
	}
	
	/**
	 * Add a new instance
	 * @param i model of the instance
	 */
	public void addInstance(InstanceModel i) {
		((WireCreationController)getController()).addInstance(i);
	}
	
	/**
	 * remove an instance
	 * @param i model of the instance.
	 */
	public void removeInstance(InstanceModel i) {
		((WireCreationController)getController()).removeInstance(i);
	}
	
	/**
	 * Add a wire.
	 * @param instanceOut instance sending event
	 * @param eventModel event being send
	 * @param instanceIn instance recieving event
	 */
	public void addWire(InstanceModel instanceOut,
			EventModel eventModel, InstanceModel instanceIn) {
		((WireCreationController)getController()).addWire(instanceOut, eventModel, instanceIn);
		
	}
	
	/***
	 * removes a wire.
	 * @param instanceModelOut instance sending event.
	 * @param eventModel event being send over wire.
	 * @param instanceModelIn instance receiving event.
	 */
	public void removeWire(InstanceModel instanceModelOut, EventModel eventModel,
			InstanceModel instanceModelIn) {
		((WireCreationController)getController()).removeWire(instanceModelOut, eventModel, instanceModelIn);
	}
	
	/**
	 * Adds an observer to the wirepanel.
	 * @param o observer being added.
	 */
	public void registerObserver(Observer o) {
		_wirepanel.addObserver(o);
	}
	
	/**
	 * returns the currently existing events in the program.
	 * @return
	 */
	public ArrayList<EventModel> getExistingEvents() {
		return ((WireCreationController)getController()).getExistingEvents();
	}
	
	/**
	 * Returns the currently unchecked events, wires of these events shouldn't be drawn.
	 * @return currently unchecked events.
	 */
	public HashSet<EventModel> getUnchecked() {
		return _filter.getUnchecked();
	}
	
	/**
	 * Repaint the wires on the wire panel.
	 */
	public void repaintWires() {
		_wirepanel.repaint();
		
	}

	/**
	 * Sets all instances to its origin
	 */
	public void moveToOrigin() {
		_wirepanel.moveToOrigin();
		_instances.unload();
	}

}
