package ide.frontend.main;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

/**
 * The Data editor embedded within the IDE
 * @author axel
 *
 */
public class DataEditor extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private JTextArea _dataField;
	
	private JScrollPane _scrollPane;

	public DataEditor() {
		_dataField = createText();
		_scrollPane = new JScrollPane(_dataField);
		
		//this.add(_dataField);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(_scrollPane);
	}
	
	/**
	 * The new content of the editor
	 * @param content
	 */
	public void setText(String content) {
		_dataField.setText(content);
	}
	
	/**
	 * @return the content of the editor
	 */
	public String getText() {
		return _dataField.getText();
	}
	
	private JTextArea createText() {
		RSyntaxTextArea dataField = new RSyntaxTextArea();

		dataField.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
		dataField.setCodeFoldingEnabled(true);
		dataField.setAutoIndentEnabled(true);
		dataField.setAutoscrolls(true);
		dataField.setAntiAliasingEnabled(true);
		dataField.setPaintMarkOccurrencesBorder(true);
		dataField.setMarkOccurrences(true); 
		dataField.setCloseCurlyBraces(true);
        dataField.setCloseMarkupTags(true);

        return dataField;
	}

}
