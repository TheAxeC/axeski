package ide.frontend.classes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ide.backend.language.LanguageModule;
import ide.backend.model.classes.ClassModel;
import ide.frontend.mvcbase.AbstractView;
import ide.frontend.mvcbase.Controller;

/**
 * View used to create new classes
 * @author axelfaes and matthijs
 *
 */
public class ClassCreationView extends AbstractView {
	
	private JTabbedPane _tabs;
	private JPanel _main;
	private SelectBlocksPanel _blocks;
	private JSplitPane _splitPane;
	private LanguageModule _lang;
	
	private JButton _addTab;
	private JPanel _dummy;
	private JLabel _noClass;
	
	public ClassCreationView(Observable model, Controller controller, LanguageModule lang) {
		super(model, controller);
		_lang = lang;
		lang.addObserver(this);
		model.addObserver(this);

		initComponents();
	    _main.setVisible(true);
	   
	}

	/**
	 * Initiate all components
	 */
	private void initComponents() {
		_main = new JPanel(new BorderLayout());
		_tabs = new JTabbedPane();
		_blocks = new SelectBlocksPanel(_tabs, _lang);
		
	    _splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					_blocks.getUI(),_tabs);
			_splitPane.setOneTouchExpandable(true);
			_splitPane.setResizeWeight(0.05);  
			_main.add(_splitPane);

		// Create a JButton for adding the tabs
	    createClassButton();
	}

	/**
	 * Create the button to create a new class
	 */
	private void createClassButton() {
		_dummy = new JPanel();
		_noClass = new JLabel();
		_noClass.setText(_lang.getString("noClass"));
		_dummy.add(_noClass);
		_dummy.setName("$addTab");
		_tabs.addTab ("$addTab", _dummy);
		
		FlowLayout f = new FlowLayout (FlowLayout.CENTER, 0, 0);

		// Make a small JPanel with the layout and make it non-opaque
		JPanel pnlTab = new JPanel (f);
		pnlTab.setName("$addTab");
		pnlTab.setOpaque (false);
		
		// Create a JButton for adding the tabs
		_addTab = new JButton("  +  ");
		_addTab.setBounds(0, 0, 20, 20);
	    _addTab.setOpaque (false); 
	    _addTab.setBorder (null);
	    _addTab.setContentAreaFilled (false);
	    _addTab.setFocusPainted (false);
	    _addTab.setFocusable (false);
	    pnlTab.add(_addTab);
	   
	    _tabs.setTabComponentAt (_tabs.getTabCount () -1 , pnlTab);
	    _addTab.setToolTipText(_lang.getString("addClasslabel"));
	   _addTab.setFocusable (false);
	   
	   
	    ActionListener listener = addClassListener();
	    _addTab.addActionListener(listener);
	    _addTab.setFocusable (false);
	    
	    _tabs.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				moveToOrigin();
			}
		});
	    
	    _tabs.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e){
            	if (SwingUtilities.isRightMouseButton(e)) {
            		// Rename the class through popup
            		if (_tabs.getSelectedComponent() instanceof ClassView) {
            			PopUpClick menu = new PopUpClick((ClassView) _tabs.getSelectedComponent(), _tabs);
            			menu.show(e.getComponent(), e.getX(), e.getY());
            		}
                }
            }
		});
	    
	}
	
	/**
	 * Used as popup on blocks
	 * @author axel
	 *
	 */
	class PopUpClick extends JPopupMenu implements Observer {
		private static final long serialVersionUID = 1L;
		private JMenuItem _changeName;
	    private ClassView _view;
	    private JTabbedPane _tabs;
	    
	    public PopUpClick(ClassView v, JTabbedPane tabs){
	    	_view = v;
	    	_tabs = tabs;
	    	addChangeNameItem();
	    }
	    
	    /**
	     * Add the removeitem
	     */
	    private void addChangeNameItem() {
	    	_changeName = new JMenuItem(_lang.getString("changeName"));
	    	_changeName.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// Change name
					JDialog dialog = new JDialog();
					Component root = SwingUtilities.getRoot(_main);
					dialog.setBounds(root.getWidth()/2 - root.getWidth()/8, root.getHeight()/2 - root.getHeight()/8 ,root.getWidth()/4, root.getHeight()/4);
					dialog.setTitle(_lang.getString("addClassDialog"));
					JTextField input = new JTextField("");
					input.setSize(dialog.getWidth()/2, input.getHeight());
					JLabel error = new JLabel("                                    ");
					JButton add = new JButton(_lang.getString("addClass"));
					JButton cancel = new JButton(_lang.getString("addClassCancel"));
					layoutingDialog(dialog, input, error, add, cancel);
					dialog.setAlwaysOnTop(true);
					dialog.getRootPane().setDefaultButton(add);
					add.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							if(!input.getText().equals("") && !((ClassCreationController)getController()).containsClass(input.getText())){
								_view.setClassName(input.getText());
								 EventQueue.invokeLater(new Runnable() {
									 @Override
							         public void run() {
										 _tabs.setTitleAt(_tabs.getSelectedIndex(), ((ClassView) _tabs.getSelectedComponent()).getClassName());
										 ((ButtonTabComponent)_tabs.getTabComponentAt(_tabs.getSelectedIndex())).updateUI();
										 _tabs.revalidate();
										 _tabs.repaint();
									 }
								 });
			            		dialog.setVisible(false);
							}else{
								error.setText(_lang.getString("addClassFail"));
							}
						}
					});
					
					cancel.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							dialog.setVisible(false);
						}
					});	
				}
	        });
	        add(_changeName);
	    }

		@Override
		public void update(Observable arg0, Object arg1) {
			_changeName.setText(_lang.getString("changeName"));
		}
	}

	/**
	 * Add a class listener
	 * @return
	 */
	private ActionListener addClassListener() {
		return new ActionListener () {
	        @Override
	        public void actionPerformed (ActionEvent e) {
	            createNewClassDialog();
	        }
	    };
	}
	
	/**
	 * Create a new class dialog to create a new class
	 */
	private void createNewClassDialog() {
		JDialog dialog = new JDialog();
		Component root = SwingUtilities.getRoot(_main);
		dialog.setBounds(root.getWidth()/2 - root.getWidth()/8, root.getHeight()/2 - root.getHeight()/8 ,root.getWidth()/4, root.getHeight()/4);
		dialog.setTitle(_lang.getString("addClassDialog"));
		JTextField input = new JTextField("");
		input.setSize(dialog.getWidth()/2, input.getHeight());
		JLabel error = new JLabel("                                    ");
		JButton add = new JButton(_lang.getString("addClass"));
		JButton cancel = new JButton(_lang.getString("addClassCancel"));
		layoutingDialog(dialog, input, error, add, cancel);
		dialog.setAlwaysOnTop(true);
		dialog.getRootPane().setDefaultButton(add);
		add.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!input.getText().equals("") && !((ClassCreationController)getController()).containsClass(input.getText())){
					createClass(input.getText());
					dialog.setVisible(false);
				}else{
					error.setText(_lang.getString("addClassFail"));
				}	
			}
		});
		
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
				
			}
		});		
		
	}
	
	/**
	 * Set the layout for a dialog
	 * @param dialog the dialog
	 * @param input the input field
	 * @param error the error text
	 * @param add the add button
	 * @param cancel the cancel button
	 */
	private void layoutingDialog(JDialog dialog, JTextField input,
			JLabel error, JButton add, JButton cancel) {
		dialog.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints(0, 0, 8, 1, 0, 0,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 80, 0);
		error.setForeground(Color.red);
		dialog.add(input, c);
		c.gridy = 1;
		c.ipady = 20;
		dialog.add(error, c);
		c.gridwidth = 4;
		c.ipadx = 20;
		c.gridy = 3;
		dialog.add(add, c);
		c.gridx = 4;
		dialog.add(cancel, c);
		dialog.setVisible(true);
	}
	
	/**
	 * Create a new class with the given name
	 * @param name name of the class
	 */
	public void createClass(String name){
		ClassModel c = ((ClassCreationController)getController()).createClass(name);
		//make idepanel
		ClassView v = new ClassView((ClassCreationModel)getModel(),c, _blocks, _lang);
		v.setName(name);
		_tabs.add(name, v);
		_tabs.setSelectedIndex(_tabs.getTabCount()-1);
		
		_tabs.setTabComponentAt(_tabs.getSelectedIndex(), new ButtonTabComponent(_tabs, ((ClassCreationController)getController()), c));
	}
	
	/**
	 * Get the current selected class
	 * @return the current selected class
	 */
	public Component getCurrentClass(){
		return _tabs.getSelectedComponent();
	}
	
	/**
	 * Get the UI
	 * @return the UI
	 */
	public JPanel getUI(){
		return _main;
	}
	
	@Override
    public Controller defaultController(Observable model) {
        return new ClassCreationController(this.getModel());
    }

	@Override
	public void update(Observable o, Object arg) {
		if(o.getClass() == LanguageModule.class)
			updateLang();
		else if(arg == null){
			updateTotalView();
		}
	}

	/**
	 * Update language specific text
	 */
	private void updateLang() {
		 _addTab.setToolTipText(_lang.getString("addClasslabel"));		
		 _noClass.setText(_lang.getString("noClass"));
	}
	
	/**
	 * Update the entire view
	 */
	private void updateTotalView(){
		_tabs.removeAll();
		createClassButton();
		ArrayList<ClassModel> classes = ((ClassCreationController)getController()).getClasses();
		for (ClassModel classModel : classes) {
			ClassView c = new ClassView((ClassCreationModel)getModel(),classModel, _blocks, _lang);
			c.setName(classModel.getName());
			c.initClass();
			_tabs.add(classModel.getName(), c);
			_tabs.setSelectedIndex(_tabs.getTabCount()-1);
			_tabs.setTabComponentAt(_tabs.getSelectedIndex(), new ButtonTabComponent(_tabs, ((ClassCreationController)getController()), classModel));
		}
		_tabs.setSelectedIndex(_tabs.getTabCount()-1);
	}

	/**
	 * Move everything to the origin
	 */
	public void moveToOrigin() {
		for(int i=0; i<_tabs.getTabCount(); i++) {
			if (_tabs.getComponentAt(i) instanceof ClassView) {
				((ClassView)_tabs.getComponentAt(i)).moveToOrigin();
			}
		}
		_blocks.deselectLoaded();
	}
}
