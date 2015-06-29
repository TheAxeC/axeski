package ide.backend.language;

import java.util.Locale;
import java.util.Observable;
import java.util.ResourceBundle;

/**
 * Contains the required data for the different languages used by the application
 * @author axel
 */
public class LanguageModule extends Observable {
	
	/** The recource bundle used to set the application to different languages */
	private ResourceBundle _language;
	
	/** the root folder of the application */
	private String _rootFolder;

	/**
	 * Creates a new languageModule with root folder.
	 * @param root folder containing language files.
	 */
	public LanguageModule(String root) {
		_rootFolder = root;
		loadLanguage("");
	}	
	
	/**
	 * Creates a new LanguageModule with given root and language.
	 * @param root older containing language files
	 * @param lang language to be loaded.
	 */
	public LanguageModule(String root, String lang) {
		_rootFolder = root;
		loadLanguage(lang);
	}
	
	/**
	 * Get the string belonging to the key [key] in the
	 * loaded language
	 * @param key, the key string
	 * @return the string in the correct language
	 */
	public String getString(String key) {
		return _language.getString(key);
	}
	
	/**
	 * Load a different language [lang]
	 * @param lang
	 */
	public void loadLanguage(String lang) {
		_language = ResourceBundle.getBundle(_rootFolder + "language", Locale.forLanguageTag(lang));
		setChanged();
		notifyObservers();
	}

}
