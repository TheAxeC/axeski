package ide.frontend.mvcbase;



import java.util.Observable;

/**
 *
 * @author jvermeulen
 * @edit Matthijs Kaminski, 
 * Added some comments.
 */
public abstract class AbstractController implements Controller {
    private Observable mModel;
    private View mView;
    
    /**
     * Constuctor for the controller, sets the model
     * @param model the model to control
     */
    public AbstractController(Observable model) {
        // Set the model.
        setModel(model);
    }
    
    
    @Override
    /**
     * Sets the view of the controller to the given view.
     * @param view  the view to set
     */
    public void setView(View view) {
        mView = view;
    }

    @Override
    /**
     * Getter for the view of the controller.
     * @return the view of the controller.
     */
    public View getView() {
        return mView;
    }

    @Override
    /**
     * Sets the model of the controller to the given view.
     * @param model the model to set
     */
    public void setModel(Observable model) {
        mModel = model;
    }

    @Override
    /**
     * Getter for the model of the controller.
     * @return the model of the controller.
     */
    public Observable getModel() {
        return mModel;
    }
    
}
