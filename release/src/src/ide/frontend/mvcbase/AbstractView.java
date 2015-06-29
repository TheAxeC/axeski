package ide.frontend.mvcbase;

import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author jvermeulen
 * @edit Matthijs Kaminski, 
 * Added some comments.
 */
public abstract class AbstractView extends Observable implements View, Observer {

    private Observable mModel;
    private Controller mController;
    
    /**
     * Empty constructor so that the model and controller can be set later.
     */
    public AbstractView() {        
    }
    
    /**
     * Sets the model and the contoller (if controller != null)
     * @param model, the model to observe
     * @param controller, the controller connected to the view or null if the default contructor is to be used
     */
    public AbstractView(Observable model, Controller controller) {
        // Set the model.
        setModel(model);
        // If a controller was supplied, use it. Otherwise let the first call to 
        // getController() create the default controller.
        if (controller != null) {
            setController(controller);
        }
    }
    
   
    @Override
    public void setController(Controller controller) {
        mController = controller;
        // Tell the controller this object is its view.
        getController().setView(this);
    }

    @Override
    public Controller getController() {
        // If a controller hasn't been defined yet...
        if (mController == null) {
            // ...make one. Note that defaultController is normally overriden by 
            // the AbstractView subclass so that it returns the appropriate 
            // controller for the view.
            setController(defaultController(getModel()));
        }
        
        return mController;
    }

    @Override
    public void setModel(Observable model) {
        mModel = model;
    }

    @Override
    public Observable getModel() {
        return mModel;
    }

    @Override
    //should be implemented in the sub-class that extends this class
    //creates a new controller of the desired type (implement) if the contoller is not given at use of the constructor,
    //this will create one.
    public Controller defaultController(Observable model) {
        return null;
    }

    /**
     * A do-nothing implementation of the Observer interface's update method.
     * Subclasses of AbstractView will provide a concrete implementation for 
     * this method.
     */
    @Override
    public void update(Observable o, Object arg) {    
    }
    
}
