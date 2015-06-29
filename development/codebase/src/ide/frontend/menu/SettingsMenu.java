package ide.frontend.menu;

import ide.backend.language.LanguageModule;
import ide.frontend.main.FrameModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * Settings menu of the application
 * @author axel
 *
 */
public class SettingsMenu implements Observer {
	
	/**
	 * The language module
	 */
	private LanguageModule _lang;
	
	/** The actual settings menu */
	private JMenu _settingsMenu;
	
	/** A submenu for the language */
	private LanguageMenu _langMenu;
	
	/** A menu to clear the console */
	private JMenuItem _clearItem;
	
	public SettingsMenu(LanguageModule lang, FrameModel mdl) {
		_lang = lang;
		_lang.addObserver(this);
		createMenuSettings(mdl);
	}
	
	/**
	 * Add the menu "settings"
	 */
	private void createMenuSettings(FrameModel mdl) {
		_settingsMenu = new JMenu(_lang.getString("settings"));
		addLanguageMenu(_settingsMenu);
		addClearConsole(_settingsMenu, mdl);
	}
	
	/**
	 * Add the language menu to a parent menu
	 * @param menu
	 */
	private void addLanguageMenu(JMenu menu) {
		_langMenu = new LanguageMenu(_lang);
		_langMenu.setUI(menu);
	}
	
	/**
	 * Add the clearConsole menu to a parent menu
	 * @param menu
	 */
	private void addClearConsole(JMenu menu, FrameModel mdl) {
		_clearItem = new JMenuItem(_lang.getString("clear"));
		menu.add(_clearItem);
		
		_clearItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mdl.getDebugModel().clearViews();
			}
		});
	}
	
	/**
	 * Getter for the UI element of this view
	 * @return the view
	 */
	public void setUI(JMenuBar menu) {
		menu.add(_settingsMenu);
	}
	
	/**
	 * Set the language of the components of the menu bar
	 */
	private void setLanguageTo() {
		_settingsMenu.setText(_lang.getString("settings"));
		_clearItem.setText(_lang.getString("clear"));
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg0 instanceof LanguageModule)
			setLanguageTo();
	}
}
