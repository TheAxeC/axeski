package ide.frontend.classes;

import ide.backend.language.LanguageModule;
import ide.backend.model.classes.ClassModel;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

/**
 * View in which the user creates new classes
 * @author axel && Matthijs
 */
public class ClassView extends JPanel {

	private static final long serialVersionUID = 1L;

	private IDEPanel _panel;

	private JPanel _linePanel;
	
	private Color _lineColor;
	
	private JLayeredPane _left;

	private JSplitPane _splitPane;

	private JPanel _right;

	private ClassModel _model;

	public ClassView(ClassCreationModel m , ClassModel c, SelectBlocksPanel select, LanguageModule lang) {
		//this.setLayout(new BorderLayout());
		_left = new JLayeredPane();
		_model = c;
		_lineColor = new Color(89, 89, 89);
		_panel = new IDEPanel(m, c, select, lang);
		_panel.setBounds(0, 0, 2000, 2000);
		_left.add(_panel, new Integer(1));
		
		
		_linePanel = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				QuadCurve2D q;
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(
						RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setStroke(new BasicStroke(5));
				g2d.setPaint(_lineColor);
				JButton a;
				ArrayList<JButton> b;
				for (HashMap.Entry<JButton, ArrayList<JButton>> entry : _panel.getButtonMap().entrySet()) {
					a = entry.getKey();
					b = entry.getValue();
					for (JButton j : b) {
						q = _panel.calculateWire(a, j);
						g2d.draw(q);
					}

				}
				
				JButton[] selected = _panel.getSelectedWire();
				if(selected != null){
					g2d.setPaint(Color.RED);
					q = _panel.calculateWire(selected[0], selected[1]);
					g2d.draw(q);
				}

			}

		};

		_linePanel.setOpaque(false);
		_linePanel.setBounds(0, 0, 2000, 2000);
		_left.add(_linePanel, new Integer(2));
		_panel.setLinePanel(_linePanel);
		_right = new JPanel();
		_right.setLayout(new GridLayout(2,1));

		_right.add(new ClassCostumeView(c,lang));
		_right.add(new MemberVariablesView(lang, c, select));
		

		 _splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					_left,_right);
			_splitPane.setOneTouchExpandable(true);
			_splitPane.setResizeWeight(0.9);  
		this.setLayout(new BorderLayout());
		this.add(_splitPane);

	}

	/**
	 * Get the panel of the class which contains the blocks
	 * @return
	 */
	public IDEPanel getPanel() {
		return _panel;
	}
	
	/**
	 * Initiate the class
	 */
	public void initClass(){
		_panel.update(null, null);
	}

	/**
	 * Move the origin point back to (0,0)
	 */
	public void moveToOrigin() {
		_panel.moveToOrigin();
	}

	/**
	 * Set the class Name
	 * @param name the class name
	 */
	public void setClassName(String name) {
		_model.changeName(name);
	}
	
	/**
	 * @return Get the class Name
	 */
	public String getClassName() {
		return _model.getName();
	}

}
