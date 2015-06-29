package ide.frontend.main;

import ide.backend.language.LanguageModule;

import javax.swing.JMenuBar;

import ide.frontend.menu.FileMenu;
import ide.frontend.menu.ProgramMenu;
import ide.frontend.menu.SettingsMenu;

/**
 * The main menu bar on the IDE
 * @author axel
 */
public class MenuBar {
	
	/** the main menu bar*/
	private JMenuBar _menuBar;
	
	/** Controls the language settings from the IDE */
	private LanguageModule _lang;
	
	private ProgramMenu _programMenu;
	private FileMenu _fileMenu;
	private SettingsMenu _settingsMenu;

	/**
	 * Creates a new MenuBar
	 * @param lang languageModule of the IDE.
	 */
	public MenuBar(LanguageModule lang) {
		_menuBar = new JMenuBar();
		_lang = lang;
	}
	
	/**
	 * Initialise the menubar
	 * @param mdl FrameModel of the IDE
	 * @param ide Frame of the IDE
	 */
	public void initialise(FrameModel mdl, FrameIDE ide) {
		_fileMenu = new FileMenu(_lang, ide, mdl);
		_fileMenu.setUI(_menuBar);
		
		_settingsMenu = new SettingsMenu(_lang, mdl);
		_settingsMenu.setUI(_menuBar);
		
		_programMenu = new ProgramMenu(_lang, ide, mdl);
		_programMenu.setUI(_menuBar);
	}
	
	/**
	 * Getter for the UI element of this view
	 * @return the view
	 */
	public JMenuBar getUI() {
		return _menuBar;
	}

}
