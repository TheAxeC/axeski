package ide.frontend.mvcbase;

import java.util.Observable;

/**
 *
 * @author jvermeulen
 * @edit Matthijs Kaminski, 
 * Added some comments.
 */
public interface Controller {
    
	/**
	 * Sets the View.
	 * @param view the view
	 */
    void setView(View controller);
    /**
     * return the View.
     * @return the view
     */
    View getView();
    
    /**
     * Sets the model
     * @param model the model to observe
     */
    void setModel(Observable model);
    
    /**
     * returns the model
     * @return the model that is being observed
     */
    Observable getModel();
    
}
