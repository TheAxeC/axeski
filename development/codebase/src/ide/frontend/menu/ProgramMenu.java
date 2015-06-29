package ide.frontend.menu;

import ide.backend.language.LanguageModule;
import ide.backend.runtime.ModelCollection;
import ide.backend.save.DataSaver;
import ide.frontend.main.FrameIDE;
import ide.frontend.main.FrameModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JCheckBox;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

/**
 * Menu in which the run and debug menus are placed.
 * @author axel
 *
 */
public class ProgramMenu implements Observer {
	
	/**
	 * The language module
	 */
	private LanguageModule _lang;
	
	/** The actual program menu */
	private JMenu _programMenu;
	private JMenu _advancedMenu;
	private RunMenu _runMenu;
	private DebugMenu _debugMenu;
	
	private JCheckBox _dataItem;
	
	/**
	 * Creates a new programMenu
	 * @param lang the language moduel of the ide
	 * @param ide frame of the ide.
	 * @param model model of the ide.
	 */
	public ProgramMenu(LanguageModule lang, FrameIDE ide, FrameModel model) {
		_lang = lang;
		_lang.addObserver(this);
		createMenuProgram(model, ide);
	}
	
	/**
	 * Creates the program menu
	 */
	private void createMenuProgram(FrameModel model, FrameIDE ide) {
		_programMenu = new JMenu(_lang.getString("program"));
		ModelCollection collection = model.getModelCollection();
		DataSaver<?> saver = model.getDataSaver();

		_runMenu = new RunMenu(_lang, ide, model);
		_debugMenu = new DebugMenu(_lang, ide, model);
		
		_programMenu.add(_runMenu.getUI());
		_programMenu.add(_debugMenu.getUI());
		addAdvancedMenu(_programMenu, collection, saver, ide);		
	}
	
	/**
	 * Add the advanced menu
	 * @param menu
	 * @param collection
	 * @param saver
	 * @param ide
	 */
	private void addAdvancedMenu(JMenu menu, ModelCollection collection, DataSaver<?> saver, FrameIDE ide) {
		_advancedMenu = new JMenu(_lang.getString("advanced"));
		menu.add(_advancedMenu);
		
		_dataItem = new JCheckBox(_lang.getString("data"));
		_dataItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					ide.setDataEditorContent(saver.save(collection));
					ide.switchDataEditor();
				}
			});
		_dataItem.setSelected(false);
		_advancedMenu.add(_dataItem);
	}
	
	/**
	 * Getter for the UI element of this view
	 * @return the view
	 */
	public void setUI(JMenuBar menu) {
		menu.add(_programMenu);
	}
	
	/**
	 * Set the language of the components of the menu bar
	 */
	private void setLanguageTo() {
		_programMenu.setText(_lang.getString("program"));

		_dataItem.setText(_lang.getString("data"));
		_advancedMenu.setText(_lang.getString("advanced"));
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg0 instanceof LanguageModule)
			setLanguageTo();
	}
}
