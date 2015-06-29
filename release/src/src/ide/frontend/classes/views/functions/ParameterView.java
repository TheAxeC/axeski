package ide.frontend.classes.views.functions;

import ide.frontend.classes.views.BlockView;


import java.awt.Color;
import java.awt.Rectangle;
import javax.swing.JPanel;

/**
 * View used in functionCall to represent each parameter.
 * @author matthijs
 *
 */
public class ParameterView extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Width and height of the field
	 */
	private int _width = 70, _height = 30;
	
	
	private BlockView _body;
	public ParameterView(int x, int y) {
		setLocation(x, y);
	}
	
	/**
	 * Getter for the height of the field
	 */
	public int getHeight() {
		return _height;
	}
	
	/**
	 * Getter for the height of the field
	 */
	public int getWidth() {
		return _width;
	}
	
	/**
	 * Make shapes
	 * @param view
	 */
	public void makeShapes(FunctionCallView view) {
		Rectangle top = new Rectangle((int) getLocation().getX(), (int) getLocation().getY() , _width, _height);
		view.addShape(top, Color.BLACK, null);
	}
	
	
	/**
	 * Check if this block contains the (x,y) point
	 * @param x, the x-position
	 * @param y, the y-position
	 * @return 
	 */
	public boolean containsBlock(int x,int y) {
		if (x >= getX() && x <= (getX() + _width)) {
			if (y >= getY() && y <= (getY() + _height)) {
				return true;	
			}
		}
		return false;
	}
	
	/**
	 * Set the content for this view
	 * @param v
	 */
	public void setContent(BlockView v) {
		_body = v;
		if (_body != null) {
			_body.setLocation((int) getLocation().getX(), (int) getLocation().getY());
		}
	}
	
	/**
	 * Getter for the content of this view
	 * @return
	 */
	public BlockView getContent() {
		return _body;
	}
}