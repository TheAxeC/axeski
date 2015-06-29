package ide.frontend.menu;

import ide.backend.language.LanguageModule;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

public class LanguageMenu implements Observer {
	
	/**
	 * The language module
	 */
	private LanguageModule _lang;
	
	/** A submenu for the language */
	private JMenu _langMenu;

	/** The language settings */
	private JMenuItem _englishItem;
	private JMenuItem _dutchItem;
	
	public LanguageMenu(LanguageModule lang) {
		_lang = lang;
		_lang.addObserver(this);
		createLanguageMenu();
	}
	
	/**
	 * Add the menu "settings"
	 */
	private void createLanguageMenu() {
		_langMenu = new JMenu(_lang.getString("lang"));
		ButtonGroup langGroup = new ButtonGroup();
		
		addEnglishMenu(langGroup);
		addDutchMenu(langGroup);
	}
	
	private void addEnglishMenu(ButtonGroup langGroup) {
		_englishItem = new JRadioButtonMenuItem();
		_englishItem.setSelected(true);
		addLanguageItem(_englishItem, "english", "en");
		langGroup.add(_englishItem);
		
		_englishItem.setAccelerator(KeyStroke.getKeyStroke(
		        java.awt.event.KeyEvent.VK_E, 
		        java.awt.Event.CTRL_MASK));
	}
	
	private void addDutchMenu(ButtonGroup langGroup) {
		_dutchItem = new JRadioButtonMenuItem();
		addLanguageItem(_dutchItem, "dutch", "nl");
		langGroup.add(_dutchItem);
		
		_dutchItem.setAccelerator(KeyStroke.getKeyStroke(
		        java.awt.event.KeyEvent.VK_D, 
		        java.awt.Event.CTRL_MASK));
	}
	
	/**
	 * Set a language item
	 * @param item, the item to set
	 * @param name, name of the item
	 */
	private void addLanguageItem(JMenuItem item, String name, String lang) {
		item.setText(_lang.getString(name));
		_langMenu.add(item);
		
		item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					_lang.loadLanguage(lang);
				}
			});
	}
	
	/**
	 * Getter for the UI element of this view
	 * @return the view
	 */
	public void setUI(JMenuBar menu) {
		menu.add(_langMenu);
	}
	
	/**
	 * Getter for the UI element of this view
	 * @return the view
	 */
	public void setUI(JMenu menu) {
		menu.add(_langMenu);
	}
	
	/**
	 * Set the language of the components of the menu bar
	 */
	private void setLanguageTo() {
		_langMenu.setText(_lang.getString("lang"));
		
		_englishItem.setText(_lang.getString("english"));
		_dutchItem.setText(_lang.getString("dutch"));
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg0 instanceof LanguageModule)
			setLanguageTo();
	}

}
