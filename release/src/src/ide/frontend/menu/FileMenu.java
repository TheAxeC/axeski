package ide.frontend.menu;

import ide.backend.exceptions.FileException;
import ide.backend.exceptions.LoadException;
import ide.backend.language.LanguageModule;
import ide.backend.load.DataLoader;
import ide.backend.runtime.ModelCollection;
import ide.backend.save.DataSaver;
import ide.frontend.main.FrameIDE;
import ide.frontend.main.FrameModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Menu which holds all the file options
 * @author axelfaes
 *
 */
public class FileMenu implements Observer {
	
	/**
	 * The language module
	 */
	private LanguageModule _lang;
	
	/** The actual file menu */
	private JMenu _fileMenu;
	
	/** The actions of the menu */
	private JMenuItem _openItem;
	private JMenuItem _saveItem;
	private JMenuItem _newItem;
	
	public FileMenu(LanguageModule lang, FrameIDE frame, FrameModel mdl) {
		_lang = lang;
		_lang.addObserver(this);
		createMenuFile(mdl.getModelCollection(), mdl.getDataLoader(), mdl.getDataSaver(), frame);
	}
	
	/**
	 * Add the menu "file"
	 */
	private void createMenuFile(ModelCollection collection, DataLoader loader, DataSaver<?> saver, FrameIDE ide) {
		_fileMenu = new JMenu(_lang.getString("file"));
		
		// Add an open button
		setOpenItem(_fileMenu, collection, loader, saver, ide);
		setSaveItem(_fileMenu, collection, saver, ide);
		setNewItem(_fileMenu, saver, ide, collection);
	}
	
	private void setNewItem(JMenu menu, DataSaver<?> saver, FrameIDE ide, ModelCollection collection) {
		_newItem = new JMenuItem(_lang.getString("new"));
		menu.add(_newItem);
		
		_newItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				newFile(collection, saver, ide, _lang);
			}
		});
		
		_newItem.setAccelerator(KeyStroke.getKeyStroke(
		        java.awt.event.KeyEvent.VK_N, 
		        java.awt.Event.CTRL_MASK));
	}
	
	/**
	 * @param menu
	 * @param collection
	 * @param loader
	 */
	private void setSaveItem(JMenu menu, ModelCollection collection, DataSaver<?> saver, FrameIDE ide) {
		_saveItem = new JMenuItem(_lang.getString("save"));
		menu.add(_saveItem);
		
		_saveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveFile(collection, saver, ide, _lang);
			}
		});
		
		_saveItem.setAccelerator(KeyStroke.getKeyStroke(
		        java.awt.event.KeyEvent.VK_S, 
		        java.awt.Event.CTRL_MASK));
	}
	
	/**
	 * Open a new file
	 * @param collection the program to reset
	 * @param saver the saver to use
	 * @param ide frame of the main IDE
	 * @param lang the language module
	 */
	public static void newFile(ModelCollection collection, DataSaver<?> saver, FrameIDE ide, LanguageModule lang) {
		int dialogResult = JOptionPane.showConfirmDialog (null, lang.getString("warning"), lang.getString("warningKey"), JOptionPane.YES_NO_OPTION);
		if(dialogResult == JOptionPane.YES_OPTION){
			System.out.println(lang.getString("newFile"));
			collection.reset();
			ide.setDataEditorContent(saver.save(collection));
		}
	}
	
	/**
	 * Saves a file
	 * @param collection the program to save
	 * @param saver the saver to use
	 * @param ide frame of the main IDE
	 * @param lang the language module
	 */
	public static void saveFile(ModelCollection collection, DataSaver<?> saver, FrameIDE ide, LanguageModule lang) {
		JFileChooser c = new JFileChooser();
		File workingDirectory = new File(System.getProperty("user.dir"));
		c.setCurrentDirectory(workingDirectory);
		
	    String[] ext = saver.getAllowedExtensions();
	    for(String e: ext) {
	    	 FileNameExtensionFilter filter = new FileNameExtensionFilter(e, e);
			 c.setFileFilter(filter);
	    }
	   
	    int rVal = c.showSaveDialog(ide);
	    if (rVal == JFileChooser.APPROVE_OPTION) {
			File file = c.getSelectedFile();
			String path = file.getAbsolutePath();
			FileNameExtensionFilter filter = (FileNameExtensionFilter) c.getFileFilter();
			
			String extension = "";
			if (filter.getExtensions().length > 0)
				extension = filter.getExtensions()[0];
			
			if(!path.endsWith(extension) && !extension.isEmpty()) {
				file = new File(path + "." + extension);
			}
						    	
	        try {
	        	System.out.println(lang.getString("startSave"));
	        	ide.moveToOrigin();
	            saver.save(collection, file.getAbsolutePath());
	            System.out.println(lang.getString("endSave"));
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
	    }
	}
	
	private void setOpenItem(JMenu menu, ModelCollection collection, DataLoader loader, DataSaver<?> saver, FrameIDE ide) {
		_openItem = new JMenuItem(_lang.getString("open"));
		menu.add(_openItem);
		
		_openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String progression = "";
				try {
					progression = saver.save(collection);
		        } catch (Exception ex) {
		            ex.printStackTrace();
		        }
				collection.reset();
				if (!loadFile(collection, loader, saver, ide, _lang)) {
					try {
						loader.loadString(collection, progression);
					} catch (LoadException | FileException e) {
						// Shouldn't happen
						e.printStackTrace();
					}
				}
				
				collection.alertObservers();
			}
		});
		
		_openItem.setAccelerator(KeyStroke.getKeyStroke(
		        java.awt.event.KeyEvent.VK_O, 
		        java.awt.Event.CTRL_MASK));
	}
	
	/**
	 * Loads a file
	 * @param collection the program to save
	 * @param loader the loader to use
	 * @param saver the saver to use
	 * @param ide frame of the main IDE
	 * @param lang the language module
	 */
	public static boolean loadFile(ModelCollection collection, DataLoader loader, DataSaver<?> saver, FrameIDE ide, LanguageModule lang) {
		JFileChooser c = new JFileChooser();	
		File workingDirectory = new File(System.getProperty("user.dir"));
		c.setCurrentDirectory(workingDirectory);
		int rVal = c.showOpenDialog(ide);
		if (rVal == JFileChooser.APPROVE_OPTION) {
			String fileName = c.getSelectedFile().getAbsolutePath();
			
			try {
				System.out.println(lang.getString("startLoad"));
				loader.load(collection, fileName);
				//ide.setDataEditorContent(DataLoader.loadFile(fileName));
				ide.setDataEditorContent(saver.save(collection));
				System.out.println(lang.getString("endLoad"));
			} catch (LoadException | FileException e) {
				System.out.println(lang.getString("loadFail"));
				e.printStackTrace();
				return false;
			}	
			return true;
		}
		return false;
	}
	
	/**
	 * Getter for the UI element of this view
	 * @return the view
	 */
	public void setUI(JMenuBar menu) {
		menu.add(_fileMenu);
	}
	
	/**
	 * Set the language of the components of the menu bar
	 */
	private void setLanguageTo() {
		_fileMenu.setText(_lang.getString("file"));
		_openItem.setText(_lang.getString("open"));
		_saveItem.setText(_lang.getString("save"));
		_newItem.setText(_lang.getString("new"));
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg0 instanceof LanguageModule)
			setLanguageTo();
	}
}
