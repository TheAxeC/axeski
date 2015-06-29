package ide.frontend.classes;

import ide.backend.language.LanguageModule;
import ide.backend.model.classes.ClassModel;
import ide.backend.model.classes.Costume;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;

/**
 * View for adding, selecting or removing costumes to a class.
 * @author Matthijs Kaminski
 */
public class ClassCostumeView extends JPanel implements Observer{

	private static final long serialVersionUID = 1L;
	/*FIELDS*/
	//controller to the class model.
	private ClassModelController _controller;
	//language module.
	private LanguageModule _lang;
	/*VIEW COMPONENTS*/
	private JLabel _primaryLabel;
	private HashMap<String,ImageIcon> images;
	private JComboBox<String> _selector;

	private String _primary;
	private JPanel _buttonPanel;
	private JButton _add;
	private JButton _remove;
	private JButton _setPrimary;
	
	
	//fields for dialog
	private String _path;
	private boolean _succes;
	private ImageIcon _newImg;
	

	public ClassCostumeView(ClassModel c, LanguageModule lang) {
		this.setLayout(new BorderLayout());
		init(c,lang);
		initSelector();
		add(_selector, BorderLayout.CENTER);
		
		_primaryLabel = new JLabel(_lang.getString("noCostume"), null, JLabel.CENTER);
		_primaryLabel.setBorder(BorderFactory.createLineBorder(Color.black));
		add(_primaryLabel, BorderLayout.NORTH);
		loadExistingCostumes();
		initButtons();
		setBorder(BorderFactory.createTitledBorder(_lang.getString("costumes")));
	}

	/**
	 * init all the buttons on this view:
	 * 	add button
	 * 	set primary button
	 *  remove button
	 */
	private void initButtons() {
		_buttonPanel = new JPanel(new GridLayout(3, 1));
		this.add(_buttonPanel, BorderLayout.SOUTH);
		initAddButton();
		initRemoveButton();
		initSetPrimaryButton();
	}
	/**
	 * init the set primary button, which sets the selected 
	 * costume to be the primary costume of the class.
	 */
	private void initSetPrimaryButton() {
		_setPrimary = new JButton(_lang.getString("setPrimary"));
		_setPrimary.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setPrimaryToSelected();	
			}
		});
		_buttonPanel.add(_setPrimary);
	}

	/**
	 * init the add button, which adds a new costume to the class.
	 */
	private void initAddButton(){
		_add = new JButton(_lang.getString("addCostume"));
		_add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createNewCostumeDialog();
			}
		});
		_buttonPanel.add(_add);
	}
	
	/**
	 * Update the primary costume
	 * @param cos
	 */
	private void updatePrimary(Costume cos) {
		if (cos == null) {
			_primaryLabel.setText(_lang.getString("noCostume"));
			_primary = null;
		} else {
			_controller.setPrimaryCostume(cos.getName());
			_primaryLabel.setIcon(new ImageIcon(cos.getImage().getImage().getScaledInstance(45, 45, java.awt.Image.SCALE_SMOOTH)));
			_primary = cos.getName();
			_primaryLabel.setText(_primary);
		}
	}
	
	/**
	 * Update the primary costume
	 * @param cos
	 * @param name
	 */
	private void updatePrimary(ImageIcon cos, String name) {
		if (cos == null) {
			_primaryLabel.setText(_lang.getString("noCostume"));
			_primary = null;
		} else {
			_controller.setPrimaryCostume(name);
			_primaryLabel.setIcon(new ImageIcon(cos.getImage().getScaledInstance(45, 45, java.awt.Image.SCALE_SMOOTH)));
			_primaryLabel.setText(name);
			_primary = name;
		}
	}
	
	/**
	 * Loads the existing costumes of the class on creation of this view.
	 * If an image of a costume is not yet loaded it will be loaded and 
	 * added to the model of the costume.
	 */
	private void loadExistingCostumes(){
		ArrayList<Costume> costumes = _controller.getCostumes();
		ImageIcon img;
		for (Costume costume : costumes) {
			if((img = costume.getImage()) == null){
				img = createImageIcon( costume.getPath());
				costume.setImage(img);
			}
			if(img != null){
				img.setDescription(costume.getName());
				addImageToSelector(images.size(), img);
			}
		}
		if (costumes.size() > 0)
			updatePrimary(costumes.get(0));
		else
			updatePrimary(null);
	}

	/**
	 * Adds a (costume) image to the class.
	 * @param name name of the costume to be added.
	 * @param path path of the costume to be added.
	 * @param img image of the costume to be added.
	 */
	private void addImage(String name, String path, ImageIcon img){
		_controller.addCostume(name, path, img);
	}
	
	/**
	 * init the remove button, which removes the selected image form the class and the selector.
	 */
	private void initRemoveButton(){
		_remove =  new JButton(_lang.getString("removeCostume"));
		_remove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeImage();
			}
		});
		
		_buttonPanel.add(_remove);
	}
	
	/**
	 * Sets the selected costume to be the primary costume for the class.
	 * (if the selector contains images)
	 */
	private void setPrimaryToSelected(){
		if(_selector.getSelectedIndex() != -1){
			ImageIcon i = images.get(_selector.getItemAt(_selector.getSelectedIndex()));
			updatePrimary(i, i.getDescription());
		}
	}
	
	/**
	 * Removes the (costume) image at the selected index from the selector and the class.
	 * If the image was the primary image: see function : checkPrimaryOnDelete
	 */
	private void removeImage(){
		String index = (String) _selector.getSelectedItem();
		if(index != null){
			_controller.removeCostume(images.get(index).getDescription());
			_selector.removeItemAt(_selector.getSelectedIndex());
			images.remove(index);
			checkPrimaryOnDelete(index);
		}
		if(images.size() != 0){
			_selector.setSelectedIndex(0);
		}else{
			_selector.setSelectedIndex(-1);
		}
		
	}

	/**
	 * Check whether the deleted image was the primary image set for this class.
	 * If this is the case, the image at the front of the list is set as primary.
	 * If the list is empty a label with a message will be shown to the user.
	 * @param index index of the deleted image.
	 */
	private void checkPrimaryOnDelete(String name) {
		if(name.equals(_primary)) {
			if(images.size() != 0){
				_controller.setPrimaryCostume(images.get((String)_selector.getItemAt(0)).getDescription());
				_primary = images.get((String)_selector.getItemAt(0)).getDescription();
				_primaryLabel.setIcon(new ImageIcon(images.get((String)_selector.getItemAt(0)).getImage().getScaledInstance(45, 45, java.awt.Image.SCALE_SMOOTH)));
				_primaryLabel.setText(_primary);
			}else{
				_controller.setPrimaryCostume("");
				_primary = null;
				_primaryLabel.setText(_lang.getString("noCostume"));
				_primaryLabel.setIcon(null);
			}
		}
	}
	
	/**
	 * Add a scaled version of the image to the selector. at the given index.
	 * @param i index of the new image.
	 * @param img image to be scaled and added to the selector.
	 */
	private void addImageToSelector(int i, ImageIcon img) {
		ImageIcon scaledImage = new ImageIcon(img.getImage().getScaledInstance(90, 90, java.awt.Image.SCALE_SMOOTH));
		scaledImage.setDescription(img.getDescription());
		images.put(img.getDescription(),scaledImage);
		if (images.size() -1  == i) {
			_selector.addItem(img.getDescription());
			_selector.setSelectedItem(img.getDescription());
		}
	}
	
	/**
	 * Add a new Costume (img) to the classmodel and to the selector.
	 * @param name name of the new image.
	 * @param path path of the new image.
	 * @param img the new image itself.
	 */
	private void addNewImg(String name, String path, ImageIcon img){
		addImage(name, path, img);
		img.setDescription(name);
		addImageToSelector(images.size(), img);
	}

	/**
	 * Init fields of the view.
	 * @param c
	 * @param lang
	 */
	private void init(ClassModel c, LanguageModule lang) {
		_controller = new ClassModelController(c);
		_lang = lang;
		_lang.addObserver(this);
		images = new HashMap<String, ImageIcon>();
		_primary = _controller.getPrimaryCostume();
		
	}

	/**
	 * init the selector (combobox).
	 */
	private void initSelector() {
		_selector = new JComboBox<String>();
		ComboBoxRenderer renderer= new ComboBoxRenderer();
		renderer.setPreferredSize(new Dimension(100, 100));
		_selector.setRenderer(renderer);
		_selector.setMaximumRowCount(3);
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	private ImageIcon createImageIcon(String path) {
		java.net.URL imgURL;
		try {
			imgURL = new URL( "File://" + path);
			if (imgURL != null) 
				return new ImageIcon(imgURL);
			
		} catch (MalformedURLException e) {
			System.err.println( _lang.getString("costumeNotFound")+ path);
		}
		return null;
	}

	

	class ComboBoxRenderer extends JLabel
	implements ListCellRenderer<Object> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Font uhOhFont;

		public ComboBoxRenderer() {
			setOpaque(true);
			setHorizontalAlignment(CENTER);
			setVerticalAlignment(CENTER);
		}

		/*
		 * This method finds the image and text corresponding
		 * to the selected value and returns the label, set up
		 * to display the text and image.
		 */
		public Component getListCellRendererComponent(
				JList<?> list,
				Object value,
				int index,
				boolean isSelected,
				boolean cellHasFocus) {

			//Get the selected index. (The index param isn't
			//always valid, so just use the value.)
			int selectedIndex = _selector.getSelectedIndex();
			if(selectedIndex == -1 || images.size() == 0)
			{
				setIcon(null);
				setUhOhText(_lang.getString("noCostume"),
						list.getFont());
				setBackground(list.getBackground());
				setForeground(list.getForeground());
				return this;
			} else{
				//Set the icon and text.  If icon was null, say so.
				ImageIcon icon = images.get(value);

				setIcon(icon);
				if (icon != null) {
					String name = icon.getDescription();
					setText(name);
					setFont(list.getFont());
				} 
					
			}	
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			return this;
		}

		//Set the font and text when no image was found.
		protected void setUhOhText(String uhOhText, Font normalFont) {
			if (uhOhFont == null) { //lazily create this font
				uhOhFont = normalFont.deriveFont(Font.ITALIC);
			}
			setFont(uhOhFont);
			setText(uhOhText);
		}
		
		
	}
	
	
	/**
	 * Create a new dialog to add a new Costume to the class
	 */
	private void createNewCostumeDialog() {
		JDialog dialog = new JDialog();
		Component root = SwingUtilities.getRoot(this);
		dialog.setBounds(root.getWidth()/2 - root.getWidth()/6, root.getHeight()/2 - root.getHeight()/8 ,root.getWidth()/3, root.getHeight()/4);
		dialog.setTitle(_lang.getString("addCostumeDialog"));
		JLabel name = new JLabel(_lang.getString("costumeName"));
		JTextField input = new JTextField("");
		input.setSize(dialog.getWidth()/2, input.getHeight());
		JLabel error = new JLabel("                                    ");
		JButton add = new JButton(_lang.getString("addCostumeButton"));
		JButton cancel = new JButton(_lang.getString("addCostumeCancel"));
		JButton open =  new JButton(_lang.getString("addCostumeOpenButton"));
		dialog.setAlwaysOnTop(true);
		dialog.getRootPane().setDefaultButton(add);
		_newImg = null;
		_path = null;
		_succes = false;
		layoutingDialog(dialog,name, input, error, add, cancel,open);
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(input.getText().trim().length() > 0  && !_controller.containsCostume(input.getText()) && _succes){
					addNewImg(input.getText(), _path, _newImg);
					dialog.setVisible(false);
				}else{
					if(!_succes)
						error.setText(_lang.getString("costumeNotFound"));
					else
						error.setText(_lang.getString("addCostumeFail"));
				}
			}
		});
		
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
			}
		});
		
		open.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				String out = loadFile(dialog);
				if(!out.equals("") &&(_newImg = createImageIcon(out)) != null){
					_path = out;
					_succes = true;
				}else{
					error.setText(_lang.getString("costumeNotFound"));
					_succes = false;
				}
				
			}
		});
	}
	
	/**
	 * Layouting for the dialog
	 * @param dialog
	 * @param input
	 * @param error
	 * @param add
	 * @param cancel
	 */
	private void layoutingDialog(JDialog dialog, JLabel name,JTextField input,
			JLabel error, JButton add, JButton cancel, JButton open ) {
		dialog.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints(0, 0, 8, 1, 0, 0,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 80, 0);
		error.setForeground(Color.red);
		c.gridwidth = 8;
		dialog.add(name, c);
		c.gridx = 4;
		//c.gridwidth = 8;
		dialog.add(input, c);
		c.gridx = 0;
		c.gridy = 1;
		c.ipady = 20;
		
		dialog.add(error, c);
		c.gridwidth = 4;
		c.ipadx = 20;
		c.gridy = 3;
		dialog.add(open,c);
		c.gridx = 4;
		dialog.add(add, c);
		c.gridx = 8;
		dialog.add(cancel, c);
		dialog.setVisible(true);
	}
	
	/**
	 * Selects a file from which a image need to be loaded.
	 * @param p parent component for scaling and positions the dialog.
	 * @return the name of the image to be loaded or the empty string if none selected.
	 */
	public  String loadFile(Component p) {
		JFileChooser c = new JFileChooser();	
		File workingDirectory = new File(System.getProperty("user.dir"));
		c.setCurrentDirectory(workingDirectory);
		int rVal = c.showOpenDialog(p);
		if (rVal == JFileChooser.APPROVE_OPTION) {
			String fileName = c.getSelectedFile().getAbsolutePath();
			return fileName;
		}
		return "";
	}



	@Override
	public void update(Observable o, Object arg) {
		if(o instanceof LanguageModule){
			updateLang();
		}
		
	}

	/**
	 * Update the labels of the components to the new language as defined in the language model
	 * which triggers this update.
	 */
	private void updateLang() {
		setBorder(BorderFactory.createTitledBorder(_lang.getString("costumes")));
		_remove.setText(_lang.getString("removeCostume"));
		_add.setText(_lang.getString("addCostume"));
		_setPrimary.setText(_lang.getString("setPrimary"));
		_primaryLabel.setText(_lang.getString("noCostume"));
	}
}

