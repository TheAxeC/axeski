package ide.frontend.canvas;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;

import ide.backend.model.classes.InstanceModel;
import ide.backend.runtime.EventCatcher;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * View for the instance in the canvas.
 * @author Axel
 *
 */
public class SpriteView extends JPanel implements Observer {

	private static final long serialVersionUID = 1L;
	/**FIELDS**/
	//model which the view represents.
	private InstanceModel _model;
	//current image set on the view.
	private Image _bgImg;

	/**
	 * Creates a new spriteview which observers the given model
	 * @param model model for representing and observing.
	 */
	public SpriteView(InstanceModel model) {
		// Observe the model
		model.addObserver(this);
		_model = model;
		
		// set the correct costume
		if (_model.getCostume() != null)
			_bgImg = _model.getCostume().getImage().getImage();
		
		// catch and send the events
		addingMouseListener();
		
		this.setLayout(null);
		this.setBounds(model.getPos().x, model.getPos().y, 190, 140);
		this.setBorder(BorderFactory.createTitledBorder(_model.getName()));
	}
	
	/**
	 * Add a mouse listener
	 * @param p
	 */
	private void addingMouseListener() {
		this.addMouseListener(new MouseAdapter(){
			// If we release the mouse
			// We either release it when we hold an unsnapped block
			// or when we where moving ourselves
            public void mouseReleased(MouseEvent e){
            	_model.pushEvent(EventCatcher.SystemEvent.MOUSE_RELEASED);
            }
            
            // When we press the mouse we start updating the mouse coordinates
            // Possible we also want to unsnap a block, that may be nested
            public void mousePressed(MouseEvent e){
            	_model.pushEvent(EventCatcher.SystemEvent.MOUSE_PRESSED);
            }

        });
	}
	
	@Override
	public void update(Observable o, Object arg) {
		//set visiblity of instance.
		if(arg instanceof Boolean){
			setVisible((Boolean)arg);
		}
		//set position of instance.
		if( arg instanceof double[]){
			Point out = getLocation();
			out.translate((int)((double[]) arg)[0],(int) ((double[]) arg)[1]);
			setLocation(out);
		}
		//set costume of instance.
		if (_model.getCostume() != null  && !_model.getCostume().equals(""))
			_bgImg = _model.getCostume().getImage().getImage();
		else
			_bgImg = null;
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
 
        Dimension d = getSize();
        if (_bgImg != null)
            g2d.drawImage(_bgImg, 0, 0, d.width, d.height, null);
         
        g2d.dispose();
	}

}
