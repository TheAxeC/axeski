package ide.frontend.main;

/**
 * The settings from the IDE.
 * @author axel
 */
public class Settings {
	/**FIELDS**/
	private boolean _editorInUse;
	
	private boolean _editorRevealed;

	/**
	 * Creates a new settings.
	 * EditorInUse is false.
	 * EditorRevealed is false.
	 */
	public Settings() {
		_editorInUse = false;
		_editorRevealed = false;
	}

	/**
	 * Returns if the editor is in use.
	 * @return if in use true, else false.
	 */
	public boolean isEditorInUse() {
		return _editorInUse;
	}

	/**
	 * Sets the editor in to given boolean
	 * @param editorInUse state of use.
	 */
	public void setEditorInUse(boolean editorInUse) {
		this._editorInUse = editorInUse;
	}
	
	/**
	 * returns if the editor is revealed
	 * @return true if revealed else false.
	 */
	public boolean isEditorRevealed() {
		return _editorRevealed;
	}

	/**
	 * ets the editorrevealed in to given boolean
	 * @param editorInUse ditorInUse state of use.
	 */
	public void setEditorRevealed(boolean editorInUse) {
		this._editorRevealed = editorInUse;
	}
	
	

}
