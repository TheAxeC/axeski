package ide.frontend.main;

import ide.backend.exceptions.FileException;
import ide.backend.exceptions.LoadException;
import ide.backend.language.LanguageModule;
import ide.frontend.menu.FileMenu;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The welcome view
 * @author axel
 *
 */
public class WelcomeView implements Observer {
	
	private JPanel _main;
	private LanguageModule _lang;
	
	// Shows the welcome messgae
	private JLabel _intro;
	private JPanel _labelPanel;
	
	// The button panel
	private JPanel _buttonPanel;
	private JPanel _filePanel;
	
	// File buttons
	private JButton _saveButton;
	private JButton _loadButton;
	private JButton _newButton;
	
	public WelcomeView(FrameModel mdl, FrameIDE ide, LanguageModule lang) {
		_lang = lang;
		lang.addObserver(this);

		createInterface(mdl, ide);
	}

	/**
	 * Get the UI
	 * @return
	 */
	public JPanel getUI(){
		return _main;
	}
	
	/**
	 * Create the interface
	 */
	private void createInterface(FrameModel mdl, FrameIDE ide) {
		_main = new JPanel(new GridBagLayout()) {
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g) {
				resizeFont();
				
				_main.revalidate();
				_intro.repaint();
				_buttonPanel.repaint();
				_labelPanel.repaint();
			}
		};
	    _main.setVisible(true);
	    
	    setIntro();
	    setButtons(mdl, ide);
	    
	    _intro.repaint();
		_labelPanel.repaint();
		_buttonPanel.repaint();
	    _main.repaint();
	}
	
	private void resizeFont() {
		Font labelFont = _intro.getFont();
		String labelText = _intro.getText();

		int stringWidth = _intro.getFontMetrics(labelFont).stringWidth(labelText);
		int componentWidth = (int) (_main.getWidth() * 0.8);

		// Find out how much the font can grow in width.
		double widthRatio = (double)componentWidth / (double)stringWidth;

		int newFontSize = (int)(labelFont.getSize() * widthRatio);
		int componentHeight = _main.getHeight();

		// Pick a new font size so it will not be larger than the height of label.
		int fontSizeToUse = Math.min(newFontSize, componentHeight);
		
		if (fontSizeToUse < 5) fontSizeToUse = 5;
		if (Math.abs(fontSizeToUse - labelFont.getSize()) < 5) fontSizeToUse = labelFont.getSize();

		// Set the label's font size to the newly determined size.
		_intro.setFont(new Font(labelFont.getName(), Font.PLAIN, fontSizeToUse));
	}
	
	/**
	 * Set the intro message
	 */
	private void setIntro() {
		_labelPanel = new JPanel();
		_intro = new JLabel(_lang.getString("Intro"));
		_intro.setFont(new Font("Verdana",1,50));
		_labelPanel.add(_intro);
		
	    GridBagConstraints gbc = new GridBagConstraints();
	    gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = gbc.weighty = 20;
		gbc.gridwidth = gbc.gridheight = 1;
	    
		_main.add(_labelPanel, gbc);
	}
	
	/**
	 * Set the input view
	 */
	private void setButtons(FrameModel mdl, FrameIDE ide) {
		_buttonPanel = new JPanel(new GridLayout(2,2));
		//_buttonPanel.setBorder(BorderFactory.createTitledBorder(_lang.getString("title")));
		
		setFileButtons(mdl, ide);
		
		_buttonPanel.add(new JLabel());
		_buttonPanel.add(new JLabel());
		_buttonPanel.add(new JLabel());
		
	    GridBagConstraints gbc = new GridBagConstraints();        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = gbc.weighty = 70;
	    
		_main.add(_buttonPanel, gbc);
	}
	
	private void setFileButtons(FrameModel mdl, FrameIDE ide) {
		_filePanel = new JPanel();
		_filePanel.setBorder(BorderFactory.createTitledBorder(_lang.getString("file")));
		_saveButton = new JButton(_lang.getString("save"));
		_loadButton = new JButton(_lang.getString("open"));
		_newButton = new JButton(_lang.getString("new"));
		
		_filePanel.add(_saveButton);
		_filePanel.add(_loadButton);
		_filePanel.add(_newButton);
	    
	    _saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				FileMenu.saveFile(mdl.getModelCollection(), mdl.getDataSaver(), ide, _lang);
			}
		});
	    
	    _loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String progression = "";
				try {
					progression = mdl.getDataSaver().save(mdl.getModelCollection());
		        } catch (Exception ex) {
		            ex.printStackTrace();
		        }
				mdl.getModelCollection().reset();
				if (!FileMenu.loadFile(mdl.getModelCollection(), mdl.getDataLoader(), mdl.getDataSaver(), ide, _lang)) {
					try {
						mdl.getDataLoader().loadString(mdl.getModelCollection(), progression);
					} catch (LoadException | FileException e) {
						// Shouldn't happen
						e.printStackTrace();
					}
				}
				
				mdl.getModelCollection().alertObservers();
			}
		});
	    
	    _newButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				FileMenu.newFile(mdl.getModelCollection(), mdl.getDataSaver(), ide, _lang);
			}
		});
	    
		_buttonPanel.add(_filePanel);
	}

	@Override
	public void update(Observable o, Object arg) {
		if(o.getClass() == LanguageModule.class)
			updateLang();
	}

	private void updateLang() {
		_intro.setText(_lang.getString("Intro"));
		//_buttonPanel.setBorder(BorderFactory.createTitledBorder(_lang.getString("title")));
		_filePanel.setBorder(BorderFactory.createTitledBorder(_lang.getString("file")));
		
		_saveButton.setText(_lang.getString("save"));
		_loadButton.setText(_lang.getString("open"));
		_newButton.setText(_lang.getString("new"));
	}


}
