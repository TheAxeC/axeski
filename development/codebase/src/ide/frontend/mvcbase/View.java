package ide.frontend.mvcbase;

import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author jvermeulen
 * @edit Matthijs Kaminski, 
 * Added some comments.
 */
public interface View extends Observer {
    
	/**
	 * Sets the controller.
	 * @param controller
	 */
    void setController(Controller controller);
    /**
     * return the controller.
     * @return the controller
     */
    Controller getController();
    
    /**
     * Sets the model
     * @param model
     */
    void setModel(Observable model);
    
    /**
     * returns the model
     * @return the model
     */
    Observable getModel();
    
    /**
     * creates the desired controller (if no controller is set), should be implemented in the derived class.
     * @param model
     * @return the controller
     */
    Controller defaultController(Observable model);
}
