package ide.frontend.classes;

import ide.backend.language.LanguageModule;
import ide.backend.model.BlockModel;
import ide.backend.model.classes.ClassModel;
import ide.backend.model.event.EventModel;
import ide.backend.model.function.HandlerModel;
import ide.frontend.classes.views.BlockView;
import ide.frontend.classes.views.functions.EmitView;
import ide.frontend.classes.views.functions.HandlerView;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.QuadCurve2D;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
/**
 * This class represents the panel on which blocks are placed and programs can be constructed.
 * @author Matthijs Kaminski && Axel Faes
 *
 */
public class IDEPanel extends JPanel  implements Observer {

	private static final long serialVersionUID = 1L;

	private static final int AXIS_LENGTH = Integer.MAX_VALUE;
	
	private ArrayList<BlockView> _contains;
	private ClassModel _model;
	private ClassCreationModel _classCreation;
	private LanguageModule _lang;
	private ClassModelController _controller;
	/*fields for adding wirres*/
	private JPanel _linePanel;
	private IDEPanelData wires;
	/***/

	/*Input view*/
	private ClassInputEventsView _inputView;
	/*Selected block view*/
	private SelectBlocksPanel _select;
	// Holds mouse coordinates
	private int oldMouseX;
	private int oldMouseY;
	private boolean mouseReleased;

	private Point _origin;
	
	// list of all emits that happen within this class
	ArrayList<EmitView> _emits;

	/**
	 * Creates a new IDEPanel on which a class is defined.
	 * @param m model of the classCreationView.
	 * @param c classmodel of the class.
	 * @param select selectPanel for selection blocks.
	 * @param l languageModule of the IDE.
	 */
	public IDEPanel(ClassCreationModel m , ClassModel c, SelectBlocksPanel select, LanguageModule l) {
		_emits = new ArrayList<>();
		_contains = new ArrayList<BlockView>();
		_controller = new ClassModelController(c);
		_model = c;
		_lang = l;
		c.addObserver(this);
		_lang.addObserver(this);

		setLayout(null);

		_classCreation = m;
		_inputView= new ClassInputEventsView(c, m, null, _lang, this);
		_inputView.update(null, null);
		super.add(_inputView);
		_inputView.revalidate();

		setMinimumSize(new Dimension(1000,1000));
		wires = new IDEPanelData();

		_select = select;
		_origin = new Point(0,0);
		addingMouseListener();
		addMouseMotionListener();
	}
	
	/**
	 * Reset the position of all blocks of this class
	 */
	public void resetPositions() {
		for(BlockView c: _contains) {
			c.resetPositions();
		}
	}

	/**
	 * adds a component of to the panel.
	 * @param comp view of the component.
	 * @param model model of the component.
	 * @return the component being added.
	 */
	public Component add(Component comp, BlockModel model) {
		return add(comp);
	}

	/**
	 * Add an emit to this class
	 * @param comp viewof the imit.
	 */
	public void addEmit(Component comp) {
		if (comp instanceof EmitView)
			_emits.add((EmitView) comp);
	}
	
	/**
	 * Remove an emit from this classs
	 * @param comp EmitView being removed.
	 */
	public void removeEmit(EmitView comp) {
		_emits.remove(comp);
	}

	/**
	 * Adds a view to the panel
	 * @param comp BlockView being added.
	 * @return Component added to the panel.
	 */
	public Component addView(Component comp) {
		if(comp instanceof BlockView) {
			_contains.add((BlockView) comp);
		}
		super.add(comp);

		return comp;
	}
	
	/**
	 * Adds a component to the panel.
	 */
	public Component add(Component comp) {
		if(comp instanceof BlockView) {
			_controller.addBlock((BlockModel) ((BlockView) comp).getModel());
		}
		return addView(comp);
	}

	/**
	 * invoked if a block is released from dragging.
	 * @param comp the component being released.
	 * @param x x position of the component.
	 * @param y y position of the component.
	 */
	public void setReleased(Component comp, int x, int y) {	
		BlockView out;
		for(BlockView c: _contains) {
			if (c != comp) {
				if ((out = c.getBlockToSnapScreen(x, y, comp)) != null) {
					if (!((BlockView) comp).notContainBlock(out)) return;

					this.remove(comp);
					_controller.removeBlock((BlockModel)((BlockView) comp).getModel());
					_contains.remove(comp);
					out.add(comp);
					c.resetPositions();

					repaint();
					break;
				}
			}
		}
		deselectLoaded();
	}

	/**
	 * returns the preffered size of the panel.
	 */
	public Dimension getPreferredSize() {
		return new Dimension(getWidth(), getHeight());
	}

	/**
	 * returns the preffered size of the panel.
	 */
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	/**
	 * Removes a BlockView from the panel and its model from the class.
	 */
	public void removeFromPanelAndClass(BlockView v){
		wires.unselect();
		_contains.remove(v);
		super.remove(v);
		v.removeBlock();
		_controller.removeBlock((BlockModel)v.getModel());
		if(v instanceof HandlerView){
			wires.removeHandlerWire((HandlerModel)v.getModel());
		}
		repaint();
	}

	/**
	 * If an inputEvent is pressed or the connection of a handler is pressed. This function will be invoked.
	 * The selected event/handlers is saved/changed.
	 * If an Event and handler is selected a connection is made and saved.
	 * @param b button that was pressed.
	 * @param m model whose view contained the button that was pressed.
	 */
	public void pressedButton(JButton b, BlockModel m ){
		wires.pressedButton(b, m, _controller, _linePanel);
	}

	/**
	 * Adds a connection between a handler and a InputEvent.
	 * @param e Event connected.
	 * @param b button of the event.
	 * @param m HandlerModel of the handler being connected.
	 */
	public void addConnectionHandler(EventModel e, JButton b, HandlerModel m){
		wires.addConnectionHandler(e, b, m, _linePanel, _inputView);
	}

	/**
	 * Add a mouse motion listener
	 */
	private void addMouseMotionListener() {
		this.addMouseMotionListener(new MouseAdapter(){
			public void mouseDragged(MouseEvent e){
				if (mouseReleased) {
					// Scroll the pane
					for(BlockView v: _contains) {
						v.setLocation(v.getX() + e.getXOnScreen() - oldMouseX , v.getY() + e.getYOnScreen() - oldMouseY);
					}
					_inputView.setLocation(_inputView.getX() + e.getXOnScreen() - oldMouseX, _inputView.getY() + e.getYOnScreen() - oldMouseY);
					_origin.setLocation(_origin.getX() + e.getXOnScreen() - oldMouseX , _origin.getY() + e.getYOnScreen() - oldMouseY);
					oldMouseX = e.getXOnScreen();
					oldMouseY = e.getYOnScreen();
					repaint();
				}
			}
		});
	}

	/**
	 * Add a mouse listener
	 */
	private void addingMouseListener() {
		this.addMouseListener(new MouseAdapter(){
			// If we release the mouse
			// We either release it when we hold an unsnapped block
			// or when we where moving ourselves
			public void mouseReleased(MouseEvent e){
				mouseReleased = false;
				repaint();
			}

			// When we press the mouse we start updating the mouse coordinates
			// Possible we also want to unsnap a block, that may be nested
			public void mousePressed(MouseEvent e){
				if (!mouseReleased && SwingUtilities.isLeftMouseButton(e)) {
					oldMouseX = e.getXOnScreen();
					oldMouseY = e.getYOnScreen();
					mouseReleased = true;
				}
			}

			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					PopUpClick menu = new PopUpClick();
					menu.show(e.getComponent(), e.getX(), e.getY());
				} else{
					if (_select.getLoaded() != null) {
						try {
							_select.getLoaded().createBlockView(e.getX(), e.getY());
						} catch (InstantiationException | IllegalAccessException
								| NoSuchMethodException | SecurityException
								| IllegalArgumentException
								| InvocationTargetException e1) {
							e1.printStackTrace();
						}
					}else{
						wires.checkClickedWire(e, _linePanel);
					}
				}
			}
		});
	}
	
	/**
	 * Move the screen to the origin
	 */
	public void moveToOrigin() {
		// Scroll the pane
		for(BlockView v: _contains) {
			v.setLocation((int) (v.getX() - _origin.getX()) ,(int) ( v.getY() - _origin.getY()));
		}
		_inputView.setLocation((int)(_inputView.getX() - _origin.getX()), (int) (_inputView.getY() - _origin.getY()));
		_origin.setLocation(0, 0);
		repaint();
	}

	/**
	 * Deselect the loaded class
	 */
	public void deselectLoaded() {
		_select.deselectLoaded();
	}

	/**
	 * Returns the made connections between handlers and Events.
	 * @return connections between handlers and Events.
	 */
	public HashMap<JButton, ArrayList<JButton>> getButtonMap(){
		return wires.get_buttonmap();
	}

	/**
	 * Sets the lines panel for this IDEPANEl on which the connections are drawn.
	 * @param lines
	 */
	public void setLinePanel(JPanel lines){
		_linePanel = lines;
	}

	/**
	 * Repaints the IDEPanel and the linePanel
	 */
	@Override
	public void repaint() {
		super.repaint();
		if (_linePanel != null) {
			_linePanel.repaint();
		}
	};

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g.create();

		g2d.setColor(Color.RED);
		g2d.drawLine((int)_origin.getX(), -AXIS_LENGTH, (int) _origin.getX(), AXIS_LENGTH);
		g2d.setColor(Color.BLUE);
		g2d.drawLine(-AXIS_LENGTH, (int)_origin.getY(), AXIS_LENGTH, (int)_origin.getY());
		g2d.setColor(Color.BLACK);

		g2d.dispose();
	}

	@Override
	public void update(Observable o, Object arg) {
		if(o == null){
			new LoadClassViewFromModel(_model, this,_lang);
		}

	}

	/**
	 * Get the class model of the panel
	 * @return class of the panel
	 */
	public ClassModel getClassModel() {
		return _model;
	}

	/**
	 * all existing Events in the ide.
	 * @return all existing Eventss
	 */
	public ArrayList<EventModel> getAllEvents() {
		return _classCreation.getEvents();
	}

	public JButton[] getSelectedWire() {
		return wires.getSelectedWire();
	}

	/**
	 * Used as popup on blocks
	 * @author axel
	 *
	 */
	class PopUpClick extends JPopupMenu {
		private static final long serialVersionUID = 1L;
		JMenuItem _remove;
		JMenuItem _deselect;

		public PopUpClick(){
			addRemoveItem();
			addDeselectItem();
		}
		
		private void addDeselectItem() {
			_deselect = new JMenuItem(_lang.getString("IDEdeselectBlock"));
			_deselect.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					deselectLoaded();
				}
			});
			add(_deselect);
		}

		private void addRemoveItem() {
			_remove = new JMenuItem(_lang.getString("IDEremovewire"));
			_remove.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					wires.removeSelected(_controller, _linePanel);
				}
			});
			add(_remove);
		}
	}

	/**
	 * Remove an input event.
	 * @param event event being removed.
	 */
	public void removeInputEvent(JButton event) {
		wires.removeInputEvent(event, _controller, _linePanel);
	}

	/**
	 * Remove an outputEvent.
	 * @param event output event being removed.
	 */
	public void removeOutputEvent(String event) {
		_controller.removeOutputEvent(event);
		
	}

	/**
	 * Calculate a wire between two given buttons
	 * @param a EventButton
	 * @param j HandlerButton
	 * @return calculated wire.
	 */
	public QuadCurve2D calculateWire(JButton a, JButton j) {
		return wires.calculateWire(a, j);
	}
	
	/**
	 * Signals that an event has been added to the program
	 */
	public void addEvent() {
		for(EmitView v: _emits) {
			v.setEventPicker();
			v.repaint();
		}
	}

	/**
	 * Signals that an event has been removed from the program
	 * @param ev event being removed from program.
	 */
	public void deleteEvent(String ev) {
		for(EmitView v: _emits) {
			v.setEventPicker();
			v.repaint();
		}
	}

	/**
	 * Check if function of given name exists.
	 * @param name name of the function
	 * @return true if not exists else false.
	 */
	public boolean functionNotExist(String name) {
		return _model.functionNotExist(name);
	}

}
