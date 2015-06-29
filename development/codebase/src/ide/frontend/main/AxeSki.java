package ide.frontend.main;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Main class
 * @author axel
 *  
 */
public class AxeSki {

	/**
	 * Set the look and feel of the IDE
	 */
	public static void setLookAndFeel() {
		try {
	    	for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		        	UIManager.setLookAndFeel(info.getClassName());
		        	break;
		        }
		    }
    	} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException
				| UnsupportedLookAndFeelException e) {
    		try {
    			UIManager.setLookAndFeel(
    			        UIManager.getSystemLookAndFeelClassName());
    		} catch (ClassNotFoundException | InstantiationException
    				| IllegalAccessException | UnsupportedLookAndFeelException e1) {
    			// Do nothing and use the standard look and feel
    		}
		}
		UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
	}
	
    /**
     * Creates the main GUI
     */
    public void createGUI() {
    	// Create a new IDE model
    	FrameModel mdl = new FrameModel();
    	
    	// Create a new GUI
    	FrameIDE frame = new FrameIDE(mdl);
    	frame.startUp();
    }
	
    /**
     * The main entrance point of the program
     * @param args, these are not used
     */
    public static void main(String[] args) {
    	
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
           public void run() {
            	setLookAndFeel();
                createAndShowGUI();
            }
        });
    }
    
    /**
     * Creates the GUI
     */
    private static void createAndShowGUI() {
    	AxeSki ide = new AxeSki();
    	ide.createGUI();
    }
    
    
    /**
     * A utility function to print a stack trace
     * Can be used during debugging
     */
    public static void printStackTrace() {
    	for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
    	    System.out.println(ste);
    	}
    	System.out.println();
    }
}
