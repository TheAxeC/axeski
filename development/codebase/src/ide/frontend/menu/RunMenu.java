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
import javax.swing.KeyStroke;

/**
 * Menu for run options of the IDE.
 * @author aael
 *
 */
public class RunMenu implements Observer {
	
	/**
	 * The language module
	 */
	private LanguageModule _lang;
	
	/** The actual program menu */
	private JMenu _programMenu;
	
	private JMenuItem _compileItem;
	private JMenuItem _stopItem;
	private JMenuItem _runItem;
	private JMenuItem _stepItem;
	
	/**
	 * Creates a new runmenu
	 * @param lang lanuage of the IDE
	 * @param ide ideFrame
	 * @param model model of the IDE.
	 */
	public RunMenu(LanguageModule lang, FrameIDE ide, FrameModel model) {
		_lang = lang;
		_lang.addObserver(this);
		createMenuProgram(model, ide);
	}
	
	/**
	 * Creates the program menu
	 */
	private void createMenuProgram(FrameModel model, FrameIDE ide) {
		_programMenu = new JMenu(_lang.getString("release"));
		ModelCollection collection = model.getModelCollection();
		Runtime runtime = model.getRuntime(); 

		addCompileItem(_programMenu, collection, runtime, model.getSettings(), model.getDataLoader(), ide);	
		addRunItem(_programMenu, collection, runtime);
		addStepItem(_programMenu, collection, runtime);	
		addStopItem(_programMenu, collection, runtime);	
	}
	
	private void addStopItem(JMenu prnt, ModelCollection collection, Runtime runtime) {
		_stopItem = new JMenuItem(_lang.getString("stop"));
		prnt.add(_stopItem);
		
		_stopItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!runtime.isCompiled()) {
	        		System.out.println(_lang.getString("notCompiled"));
	        	} else if (runtime.isDebugMode()) {
	        		System.out.println(_lang.getString("inDebug"));
	        	} else if (runtime.isRunning()) {
					System.out.println(_lang.getString("stopRunning"));
					runtime.stopRunning();
	        	} else {
	        		System.out.println(_lang.getString("isNotRunning"));
	        		runtime.stopRunning();
	        	}
			}
		});
		
		_stopItem.setAccelerator(KeyStroke.getKeyStroke(
		        java.awt.event.KeyEvent.VK_Q, 
		        java.awt.Event.CTRL_MASK));
	}
	
	private void addCompileItem(JMenu prnt, ModelCollection collection, Runtime runtime, 
			Settings settings, DataLoader loader, FrameIDE ide) {
		_compileItem = new JMenuItem(_lang.getString("compile"));
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
					if (runtime.compile(collection))
						System.out.println(_lang.getString("endCompile"));
					else 
						System.out.println(_lang.getString("failCompile"));
	        	} else {
	        		System.out.println(_lang.getString("isRunning"));
	        	}
			}
		});
		
		_compileItem.setAccelerator(KeyStroke.getKeyStroke(
		        java.awt.event.KeyEvent.VK_C, 
		        java.awt.Event.CTRL_MASK));
	}
	
	/**
	 * Step through the program
	 * @param prnt
	 * @param collection
	 * @param runtime
	 */
	private void addStepItem(JMenu prnt, ModelCollection collection, Runtime runtime) {
		_stepItem = new JMenuItem(_lang.getString("step"));
		prnt.add(_stepItem);
		
		_stepItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (!runtime.isCompiled()) {
		        		System.out.println(_lang.getString("notCompiled"));
		        	} else if (runtime.isDebugMode()) {
		        		System.out.println(_lang.getString("inDebug"));
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
	
	private void addRunItem(JMenu prnt, ModelCollection collection, Runtime runtime) {
		_runItem = new JMenuItem(_lang.getString("run"));
		prnt.add(_runItem);
		
		_runItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				(new Thread() {
				    public void run() {
				        try {
				        	if (!runtime.isCompiled()) {
				        		System.out.println(_lang.getString("notCompiled"));
				        	} else if (runtime.isDebugMode()) {
				        		System.out.println(_lang.getString("inDebug"));
				        	} else if (!runtime.isRunning()) {
				        		System.out.println(_lang.getString("startRunning"));
				        		runtime.run();
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
		
		_runItem.setAccelerator(KeyStroke.getKeyStroke(
		        java.awt.event.KeyEvent.VK_R, 
		        java.awt.Event.CTRL_MASK));
	}
	
	/**
	 * Getter for the UI element of this view
	 * @return the view
	 */
	public JMenu getUI() {
		return _programMenu;
	}
	
	/**
	 * Set the language of the components of the menu bar
	 */
	private void setLanguageTo() {
		_programMenu.setText(_lang.getString("release"));

		_compileItem.setText(_lang.getString("compile"));
		_runItem.setText(_lang.getString("run"));
		_stopItem.setText(_lang.getString("stop"));
		_stepItem.setText(_lang.getString("step"));
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg0 instanceof LanguageModule)
			setLanguageTo();
	}
}
