package ide.frontend.main;

import ide.backend.exceptions.FileException;
import ide.backend.exceptions.LoadException;
import ide.frontend.classes.ClassCreationModel;
import ide.frontend.classes.ClassCreationView;
import ide.frontend.events.EventCreationModel;
import ide.frontend.events.EventCreationView;
import ide.frontend.wire.WireCreationModel;
import ide.frontend.wire.WireCreationView;

import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * The main frame of the IDE
 * @author axel
 *
 */
public class FrameIDE extends JFrame implements Observer {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Main content of the IDE
	 */
	private JTabbedPane _tabs;
	private JSplitPane _splitPane;
	
	/**
	 * The data editor
	 */
	private DataEditor _dataEditor;
	
	/**
	 * The Debug pane
	 */
	private DebugPane _debugPane;	
	
	/**
	 * All different views
	 */
	private EventCreationView _eventView;
	private ClassCreationView _classView;
	private WireCreationView _wireView;
	private WelcomeView _welcomeView;
	private CanvasView _canvas;
	
	/**
	 * Container of all views
	 */
	
	FrameModel _frameModel;

	public FrameIDE() {
		super();
	}
	
	/**
	 * Create a new frame with the given model
	 * @param frameMdl the frame model
	 */
	public FrameIDE(FrameModel frameMdl) {
		super(frameMdl.getLang().getString("title"));
		frameMdl.getLang().addObserver(this);
		
		_frameModel = frameMdl;
		
		_dataEditor = new DataEditor();
		_debugPane = new DebugPane(_frameModel.getDebugModel());
		
		_tabs = new JTabbedPane(JTabbedPane.RIGHT);
		_tabs.setBorder(BorderFactory.createEmptyBorder());
		_splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				_tabs, _debugPane);
		_splitPane.setOneTouchExpandable(true);
		_splitPane.setResizeWeight(0.8);  
		
		this.setContentPane(_splitPane);
		
		EventCreationModel mdl = new EventCreationModel(_frameModel.getModelCollection());
		_eventView = new EventCreationView(mdl, null, _frameModel.getLang());
		ClassCreationModel cmdl = new ClassCreationModel(_frameModel.getModelCollection());
		_classView = new ClassCreationView(cmdl, null, _frameModel.getLang());
		WireCreationModel wmdl = new WireCreationModel(_frameModel.getModelCollection());
		_wireView = new WireCreationView(wmdl, null, _frameModel.getLang());
		
		_welcomeView = new WelcomeView(frameMdl, this, _frameModel.getLang());
		_canvas = new CanvasView(null, null, _frameModel.getLang(), _wireView);
		_frameModel.getRuntime().catchEvent(_canvas);
		
		_tabs.addTab (_frameModel.getLang().getString("welcome"), _welcomeView.getUI());
		_tabs.addTab (_frameModel.getLang().getString("eventMenu"), _eventView.getUI());
		_tabs.addTab (_frameModel.getLang().getString("classMenu"), _classView.getUI());
		_tabs.addTab (_frameModel.getLang().getString("frameMenu"), _wireView.getUI());
		_tabs.addTab (_frameModel.getLang().getString("canvas"), _canvas.getUI());
		
		_tabs.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				saveDataEditor();
				_frameModel.getSettings().setEditorInUse(false);
				
				if (_tabs.getSelectedComponent() == _dataEditor)
					_frameModel.getSettings().setEditorInUse(true);					
			}
		});
	}
	
	/**
	 * Start the frame up
	 */
	public void startUp() {
		// Start the menu bar
		MenuBar menu = new MenuBar(_frameModel.getLang());
		menu.initialise(_frameModel, this);
        this.setJMenuBar(menu.getUI());
        
        
        // Some settings for the frame
		this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
	}
	
	/**
	 * Open the data editor
	 */
	public void switchDataEditor() {
		if (!_frameModel.getSettings().isEditorRevealed()) {
			_frameModel.getSettings().setEditorRevealed(true);
			_tabs.addTab(_frameModel.getLang().getString("data"), _dataEditor);
		} else {
			_frameModel.getSettings().setEditorRevealed(false);
			_tabs.removeTabAt(_tabs.indexOfTab(_frameModel.getLang().getString("data")));
		}
		
		this.revalidate();
	}
	
	/**
	 * Set the data editor content
	 * @param content content for the data editor
	 */
	public void setDataEditorContent(String content) {
		_dataEditor.setText(content);
	}
	
	/**
	 * Get the content from the data editor
	 * @return the content
	 */
	public String getDataEditorContent() {
		return _dataEditor.getText();
	}
	
	/**
	 * Saves the data editor if it is open
	 */
	private void saveDataEditor() {
		if (_frameModel.getSettings().isEditorInUse()) {
			String content = getDataEditorContent();
			_frameModel.getModelCollection().reset();
			try {
				_frameModel.getDataLoader().loadString(_frameModel.getModelCollection(), content);
			} catch (LoadException e) {
				System.out.println(e.getMessage());
			} catch (FileException e) {
				System.out.println(e.getMessage());
			}
			_frameModel.getModelCollection().alertObservers();
		} else {
			moveToOrigin();
			String content = _frameModel.getDataSaver().save(_frameModel.getModelCollection());
			setDataEditorContent(content);
		}
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		this.setTitle(_frameModel.getLang().getString("title"));
		
		int i = _tabs.indexOfComponent(_eventView.getUI());
		_tabs.setTitleAt(i, _frameModel.getLang().getString("eventMenu"));
		
		i =_tabs.indexOfComponent(_classView.getUI());
		_tabs.setTitleAt(i, _frameModel.getLang().getString("classMenu"));
		
		i = _tabs.indexOfComponent(_wireView.getUI());
		_tabs.setTitleAt(i, _frameModel.getLang().getString("frameMenu"));
		
		i =_tabs.indexOfComponent(_welcomeView.getUI());
		_tabs.setTitleAt(i, _frameModel.getLang().getString("welcome"));
		
		i =_tabs.indexOfComponent(_canvas.getUI());
		_tabs.setTitleAt(i, _frameModel.getLang().getString("canvas"));
		
		i =_tabs.indexOfComponent(_dataEditor);
		if (i >= 0)
			_tabs.setTitleAt(i, _frameModel.getLang().getString("data"));
	}
	
	/**
	 * Move all blocks and instances to the origin
	 */
	public void moveToOrigin() {
		_classView.moveToOrigin();
		_wireView.moveToOrigin();
		_canvas.moveToOrigin();
	}

}
