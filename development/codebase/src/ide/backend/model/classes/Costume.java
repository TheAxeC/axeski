package ide.backend.model.classes;

import javax.swing.ImageIcon;

/**
 * Represents a costume for the class.
 * @author matthijs
 */
public class Costume {
	
	/**
	 * FIELDS
	 */
	//name of the costume.
	private String _name;
	//image of the costume.
	private ImageIcon _image;
	//path to the image of the costume
	private String _path;
	
	/**
	 * Create a new costume with given name and path to image.
	 * @param name name of the costume
	 * @param path path to the costume image
	 */
	public Costume(String name, String path) {
		_name = name;
		_path = path;
		_image = null;
	}
	
	/**
	 * Copy constructor
	 * @param other the costume to copy
	 */
	public Costume(Costume other){
		_name = other._name;
		_path = other._path;
		_image = other._image;
	}
	
	/**
	 * returns the name of the costume.
	 * @return the name
	 */
	public String getName(){
		return _name;
	}
	
	/**
	 * Returns the path of the image of the costume.
	 * @return the path
	 */
	public String getPath(){
		return _path;
	}
	
	/**
	 * Sets the path of the image of the costume.
	 * @param path the new path
	 */
	public void setPath(String path){
		_path = path;
	}
	
	/**
	 * Returns the image of the costume.
	 * If the image is not yet set, null will be returned.
	 * @return the image
	 */
	public ImageIcon getImage(){
		return _image;
	}
	
	/**
	 * Sets the image of the costume to the given image.
	 * NOTE: if image is changed, make sure to set the path right aswell.
	 * @param image the image
	 */
	public void setImage(ImageIcon image){
		_image = image;
	}
	

	
	
	
}
