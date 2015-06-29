package ide.frontend.main;

import ide.backend.language.LanguageModule;
import ide.backend.load.DataLoader;
import ide.backend.load.XMLDataLoader;
import ide.backend.runtime.InterpreterRuntime;
import ide.backend.runtime.ModelCollection;
import ide.backend.runtime.Runtime;
import ide.backend.save.DataSaver;
import ide.backend.save.XMLDataSaver;

/**
 * Model of the IDE
 * Could be used with multiple views
 * @author axel
 *
 */
public class FrameModel {
	
	/** Collection of all models */
	ModelCollection _collection;
	
	/** Runtime of the ide */
	Runtime _runtime;
	
	/** Debug Model */
	DebugModel _debugModel;
	
	/**
	 * Settings of the IDE
	 */
	Settings _settings;
	
	LanguageModule _lang;

	public FrameModel() {
		initIDE();
	}
	
	private void initIDE() {
		// Create a new language module
		_collection = new ModelCollection();
		_runtime = new InterpreterRuntime();
    	_lang = new LanguageModule("lang/", "");
    	_debugModel = new DebugModel();
    	_settings = new Settings();
    	
	}
	
	/**
	 * Get the language module
	 * @return
	 */
	public LanguageModule getLang() {
		return _lang;
	}
	
	/**
	 * Get the model collection
	 * @return
	 */
	public ModelCollection getModelCollection() {
		return _collection;
	}
	
	/**
	 * Get the settings file
	 * @return the settings
	 */
	public Settings getSettings() {
		return _settings;
	}
	
	/**
	 * Get the debug model
	 * @return debug model
	 */
	public DebugModel getDebugModel() {
		return _debugModel;
	}
	
	/**
	 * Get the runtime
	 * @return the runtime
	 */
	public Runtime getRuntime() {
		return _runtime;
	}
	
	/**
	 * Create a new data loader
	 * @return the loader
	 */
	public DataLoader getDataLoader() {
		return new XMLDataLoader();
	}
	
	/**
	 * Create a new data saver
	 * @return the saver
	 */
	public DataSaver<?> getDataSaver() {
		return new XMLDataSaver();
	}	

}
