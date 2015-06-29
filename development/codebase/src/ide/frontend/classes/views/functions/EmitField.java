package ide.frontend.classes.views.functions;

import ide.frontend.classes.views.BlockView;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * An emitField holding a name and a field to insert a variable into
 * @author axel
 *
 */
public class EmitField extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Width and height of the field
	 */
	private int _width = 140, _height = 50;
	
	private int _refWidth = 70, _refHeight = 30;
	
	private ArrayList< Component> _comps;
	
	private String _member;
	
	private BlockView _body;
	
	public EmitField(String str, int x, int y) {
		_comps = new ArrayList<>();
		JLabel f = new JLabel(str + " :");
		f.setBounds(x + 5, y + 10, 50, 20);
		_comps.add(f);
		setLocation(x, y);
		_member = str;
	}
	
	/**
	 * Getter for the height of the field
	 * @return the height
	 */
	public int getHeight() {
		return _height;
	}
	
	/**
	 * Getter for the height of the field
	 * @return the width
	 */
	public int getWidth() {
		return _width;
	}
	
	/**
	 * Make shapes
	 * @param view Parent view
	 */
	public void makeShapes(EmitView view) {
		Rectangle top = new Rectangle((int) getLocation().getX() + 60, (int) getLocation().getY() + 10, _refWidth, _refHeight);
		view.addShape(top, Color.BLACK, null);
	}
	
	/**
	 * Get the inner components
	 * @return the inner components
	 */
	public ArrayList< Component> getInnerComponents() {
		return _comps;
	}
	
	/**
	 * Get the member this field represents
	 * @return get the member represented by this view
	 */
	public String getMember() {
		return _member;
	}
	
	/**
	 * Check if this block contains the (x,y) point
	 * @param x, the x-position
	 * @param y, the y-position
	 * @return 
	 */
	public boolean containsBlock(int x,int y) {
		if (x >= getX()+60 && x <= (getX()+60 + _refWidth)) {
			if (y >= getY()+10 && y <= (getY()+10 + _refHeight)) {
				return true;	
			}
		}
		return false;
	}
	
	/**
	 * Set the content for this view
	 * @param v view of the member
	 */
	public void setContent(BlockView v) {
		_body = v;
		if (_body != null) {
			_body.setLocation((int) getLocation().getX() + 60, (int) getLocation().getY() + 10);
		}
	}
	
	/**
	 * Getter for the content of this view
	 * @return view of the member or null if there is none
	 */
	public BlockView getContent() {
		return _body;
	}
}
