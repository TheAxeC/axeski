package ide.frontend.menu;

import ide.backend.exceptions.FileException;
import ide.backend.exceptions.FunctionNotFoundException;
import ide.backend.exceptions.LoadException;
import ide.backend.language.LanguageModule;
import ide.backend.load.DataLoader;
import ide.backend.runtime.ModelCollection;
import ide.backend.runtime.Runtime;
import ide.frontend.main.FrameIDE;
import ide.frontend.main.FrameModel;
import ide.frontend.main.Settings;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * Menu which contains all debug buttons in the menubar
 * @author axelfaes
 *
 */
public class DebugMenu implements Observer {
	/**
	 * The language module
	 */
	private LanguageModule _lang;
	
	/** The actual file menu */
	private JMenu _debugMenu;
	
	/** The actions of the menu */
	private JMenuItem _compileItem;
	private JMenuItem _runItem;
	private JMenuItem _stopItem;
	private JMenuItem _stepItem;
	
	/**
	 * @param lang the language module
	 * @param frame the main frame from the IDE
	 * @param mdl the model from the main frame
	 */
	public DebugMenu(LanguageModule lang, FrameIDE frame, FrameModel mdl) {
		_lang = lang;
		_lang.addObserver(this);
		createMenu(mdl, frame);
	}
	
	/**
	 * Add the menu "file"
	 */
	private void createMenu(FrameModel model, FrameIDE ide) {
		_debugMenu = new JMenu(_lang.getString("debug"));
		
		ModelCollection collection = model.getModelCollection();
		Runtime runtime = model.getRuntime(); 

		addCompileItem(_debugMenu, collection, runtime, model.getSettings(), model.getDataLoader(), ide);	
		addRunItem(_debugMenu, collection, runtime);
		addStepItem(_debugMenu, collection, runtime);	
		addStopItem(_debugMenu, collection, runtime);	
	}
	
	/**
	 * Add the stop item
	 * @param prnt
	 * @param collection
	 * @param runtime
	 */
	private void addStopItem(JMenu prnt, ModelCollection collection, Runtime runtime) {
		_stopItem = new JMenuItem(_lang.getString("stopDebug"));
		prnt.add(_stopItem);
		
		_stopItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!runtime.isCompiled()) {
	        		System.out.println(_lang.getString("notCompiled"));
	        	} else if (!runtime.isDebugMode()) {
	        		System.out.println(_lang.getString("notDebug"));
	        	} else if (runtime.isRunning()) {
					System.out.println(_lang.getString("stopRunning"));
					runtime.stopRunning();
	        	} else {
	        		System.out.println(_lang.getString("isNotRunning"));
	        		runtime.stopRunning();
	        	}
			}
		});
	}
	
	/**
	 * Step through the program
	 * @param prnt
	 * @param collection
	 * @param runtime
	 */
	private void addStepItem(JMenu prnt, ModelCollection collection, Runtime runtime) {
		_stepItem = new JMenuItem(_lang.getString("stepDebug"));
		prnt.add(_stepItem);
		
		_stepItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				try {
					if (!runtime.isCompiled()) {
		        		System.out.println(_lang.getString("notCompiled"));
		        	} else if (!runtime.isDebugMode()) {
		        		System.out.println(_lang.getString("notDebug"));
		        	} else if (!runtime.isRunning()) {
						if (!runtime.step())
							System.out.println(_lang.getString("programEnd"));
		        	} else {
		        		System.out.println(_lang.getString("isRunning"));
		        	}
				} catch (FunctionNotFoundException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Add the compile item
	 * @param prnt
	 * @param collection
	 * @param runtime
	 * @param settings
	 * @param loader
	 * @param ide
	 */
	private void addCompileItem(JMenu prnt, ModelCollection collection, Runtime runtime, 
			Settings settings, DataLoader loader, FrameIDE ide) {
		_compileItem = new JMenuItem(_lang.getString("compileDebug"));
		prnt.add(_compileItem);
		
		_compileItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!runtime.isRunning()) {
					System.out.println(_lang.getString("startCompile"));
					// Check if the Data editor is active
					// if so, load the Data editor, not the regular collection
					if (settings.isEditorInUse()) {
						System.out.println(_lang.getString("useData"));
						String content = ide.getDataEditorContent();
						collection.reset();
						try {
							loader.loadString(collection, content);
						} catch (LoadException e) {
							System.out.println(e.getMessage());
						} catch (FileException e) {
							System.out.println(e.getMessage());
						}
					}
				
					// Compile the program
					if (runtime.compileDebug(collection))
						System.out.println(_lang.getString("endCompile"));
					else 
						System.out.println(_lang.getString("failCompile"));
	        	} else {
	        		System.out.println(_lang.getString("isRunning"));
	        	}
			}
		});
	}
	
	/**
	 * Add the run item
	 * @param prnt
	 * @param collection
	 * @param runtime
	 */
	private void addRunItem(JMenu prnt, ModelCollection collection, Runtime runtime) {
		_runItem = new JMenuItem(_lang.getString("runDebug"));
		prnt.add(_runItem);
		
		_runItem.addActionListener(new ActionListener() {
			private boolean hasBreaked = false;
			
			public void actionPerformed(ActionEvent arg0) {
				(new Thread() {
				    public void run() {
				        try {
				        	if (!runtime.isCompiled()) {
				        		System.out.println(_lang.getString("notCompiled"));
				        	} else if (!runtime.isDebugMode()) {
				        		System.out.println(_lang.getString("notDebug"));
				        	} else if (!runtime.isRunning()) {
				        		if (!hasBreaked) System.out.println(_lang.getString("startRunning"));
				        		if (!runtime.run()) {
				        			System.out.println(_lang.getString("break"));
				        			hasBreaked = true;
				        		} else {
				        			hasBreaked = false;
				        		}
				        	} else {
				        		System.out.println(_lang.getString("isRunning"));
				        	}
				        } catch (FunctionNotFoundException e) {
							e.printStackTrace();
						}
				    }
				}).start();
			}
		});
	}
	
	/**
	 * Getter for the UI element of this view
	 * @return the view
	 */
	public JMenu getUI() {
		return _debugMenu;
	}
	
	/**
	 * Set the language of the components of the menu bar
	 */
	private void setLanguageTo() {
		_debugMenu.setText(_lang.getString("debug"));
		
		_compileItem.setText(_lang.getString("compileDebug"));
		_runItem.setText(_lang.getString("runDebug"));
		_stopItem.setText(_lang.getString("stopDebug"));
		_stepItem.setText(_lang.getString("stepDebug"));
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg0 instanceof LanguageModule)
			setLanguageTo();
	}
}
