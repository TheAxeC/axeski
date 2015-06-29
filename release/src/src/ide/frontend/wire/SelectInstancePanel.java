package ide.frontend.wire;

import ide.backend.language.LanguageModule;
import ide.backend.model.classes.ClassModel;
import ide.backend.model.classes.InstanceModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Panel to select an class for creating an instance to be placed on the wirepanel.
 * @author matthijs && axel
 *
 */
public class SelectInstancePanel implements Observer{
	private JPanel _main;
	private JPanel _classes;
	private JPanel _container;
	private JPanel _loadedPanel;
	private HashMap<ClassModel, JPanel> _classButtons;
	private JScrollPane _outer;
	private LanguageModule _lang;
	private WirePanel _wirePanel;
	
	/**FIELDS FOR INSTANCE MENU VIEW**/
	private static final int _startWidth = 150;
	private static final int _startHeight = 35;
	
	private static final Color _color = new Color(255,128,14);
	
	/**
	 * Class that holds variables needed to instantiate a new class
	 * @author axel
	 *
	 */
	public class LoadClassInstance {
		private ClassModel _key;
		
		public LoadClassInstance(ClassModel key) {
			_key = key;
		}
		
		public void createInstance(int x,int y) {
			createNewNameDialog(_key, x, y);
		}
	}
	
	private LoadClassInstance _loaded;
	
	/**
	 * Creates a new SelectInstancePanel.
	 * @param wirepanel wirepanel to place instances,
	 * @param lang language module of the IDE.
	 */
	public SelectInstancePanel(WirePanel wirepanel, LanguageModule lang) {
		_wirePanel = wirepanel;
		_lang = lang;
		_lang.addObserver(this);
		
		_loadedPanel = new JPanel();
		_loadedPanel.setBorder(BorderFactory.createTitledBorder(_lang.getString("loadedborder")));
		
		_container = new JPanel(new BorderLayout());
		_container.setBorder(BorderFactory.createEmptyBorder());
		
		_main = new JPanel(new BorderLayout());

		_container.add(_loadedPanel, BorderLayout.NORTH);
		
		_classes = new JPanel();
		_classes.setLayout(new GridLayout(0,1));
		_main.setBorder(BorderFactory.createTitledBorder(_lang.getString("classInstanceborder")));

		_outer = new JScrollPane(_classes);
		_outer.setBorder(BorderFactory.createEmptyBorder());
		_main.add(_outer, BorderLayout.CENTER);
		_classButtons = new HashMap<ClassModel, JPanel>();
		_outer = new JScrollPane(_main);
		
		_container.add(_main, BorderLayout.CENTER);
	}
	
	/**
	 * Get the UI from the select panel
	 * @return
	 */
	public JPanel getUI(){
		return _container;
	}
	
	/**
	 * Get the loaded instance
	 * @return
	 */
	public LoadClassInstance getLoaded() {
		return _loaded;
	}
	
	/**
	 * Unload the instance loader.
	 */
	public void unload() {
		_loaded = null;
		
		_loadedPanel.removeAll();
		_loadedPanel.revalidate();
		_loadedPanel.repaint();
		_container.revalidate();
		_container.repaint();
	}
	
	/**
	 * Reset all classes. 
	 * @param classes new classes array.
	 */
	public void resetClasses(ArrayList<ClassModel> classes) {
		_classes.removeAll();
		_classButtons.clear();
		for (ClassModel classModel : classes) {
			createInstanceRep(classModel);
		}
		
	}
	/**
	 * Adds a new class to the select panel
	 * @param m model of the class
	 */
	public void addClass(ClassModel m ){
		createInstanceRep(m);
	}
	
	/**
	 * Deletes a given class form the panel
	 * @param m model of the class being removed.
	 */
	public void deleteClass(ClassModel m){
		_classes.remove(_classButtons.get(m));
		_classButtons.remove(m);
	}
	
	private void createInstanceRep(ClassModel m) {
		JPanel out = instanceMenuView(m, m.getName());
		JPanel dummy = new JPanel();
		dummy.add(out);
		_classes.add(dummy);
		_classButtons.put(m, dummy);
		out.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				_loadedPanel.removeAll();
				_loaded = new LoadClassInstance(m);	
				JPanel out = instanceMenuView(m, m.getName());
				out.setBackground(Color.black);
				JPanel dummy = new JPanel();
				dummy.add(out);
				_loadedPanel.add(dummy);
				
				_loadedPanel.revalidate();
				_loadedPanel.repaint();
				_container.revalidate();
				_container.repaint();
			}
		});
	}
	
	/**
	 * Create a new dialog to create a new instance
	 */
	private void createNewNameDialog(ClassModel m, int x, int y) {
		JDialog dialog = new JDialog();
		dialog.setAlwaysOnTop(true);
		Component toGrab = SwingUtilities.getRoot(_container);
		dialog.setBounds(toGrab.getWidth()/2 - toGrab.getWidth()/8, toGrab.getHeight()/2 - toGrab.getHeight()/8 ,toGrab.getWidth()/4, toGrab.getHeight()/4);
		dialog.setTitle(_lang.getString("addInstanceDialog"));
		JTextField input = new JTextField("");
		input.setSize(dialog.getWidth()/2, input.getHeight());
		JLabel error = new JLabel("                                    ");
		JButton add = new JButton(_lang.getString("addInstance"));
		dialog.getRootPane().setDefaultButton(add);
		JButton cancel = new JButton(_lang.getString("addInstanceCancel"));
		layoutingDialog(dialog, input, error, add, cancel);
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!input.getText().equals("") && !_wirePanel.getInstanceNames().contains(input.getText())){
					InstanceModel i = new InstanceModel(m, m, input.getText());
					InstanceView v = new InstanceView(i, null, _lang, _wirePanel);
					v.setLocation(x, y);
					v.revalidate();
					_wirePanel.add(v, i);
					_wirePanel.repaint();
					
					dialog.setVisible(false);
				}else{
					error.setText(_lang.getString("addInstanceFail"));
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
	 * Layouting for the dialog
	 * @param dialog
	 * @param input
	 * @param error
	 * @param add
	 * @param cancel
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
	
	class InstanceMenuViewClass extends JPanel implements Observer {
		private static final long serialVersionUID = 1L;

		private ClassModel _model;
		
		public InstanceMenuViewClass(ClassModel m, String name){
			setOpaque(false);
			
			m.addObserver(this);
			_model = m;
			JLabel title = new JLabel(name);
			title.setAlignmentX(CENTER_ALIGNMENT);
			title.setAlignmentY(CENTER_ALIGNMENT);
			add(title);
		}
		
	    @Override
	    public void paintComponent(Graphics g) {
	    	super.paintComponent(g);
	    	Graphics2D g2d = (Graphics2D) g.create();

	    	Rectangle top = new Rectangle(0, 0, _startWidth-1, _startHeight-1);
	    	draw(g2d, top, Color.BLACK, _color);

			g2d.dispose();
	    }
	    
	    @Override
	    public Dimension getPreferredSize() {
	    	return new Dimension(_startWidth, _startHeight);
	    }

	    private void draw(Graphics2D g2d, Shape shape, Color foreground, Color background) {
	    	g2d.setColor(background);
	        g2d.fill(shape);
	        g2d.setColor(foreground);
	        g2d.draw(shape);
	    }
		@Override
		public void update(Observable arg0, Object arg1) {
			if (arg1 != null && arg1 instanceof String && arg1.equals("changeName")) {
				this.removeAll();
				JLabel title = new JLabel(_model.getName());
				title.setAlignmentX(CENTER_ALIGNMENT);
				title.setAlignmentY(CENTER_ALIGNMENT);
				add(title);
				repaint();
				_container.repaint();
			}
		}
		
	}
	
	private JPanel instanceMenuView(ClassModel m, String name) {
		JPanel out = new InstanceMenuViewClass(m, name);
		return out;
	}
	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}


}
