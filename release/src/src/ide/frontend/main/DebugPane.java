package ide.frontend.main;

import java.awt.Color;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 * View to represent the console window
 * @author axel
 *
 */
public class DebugPane extends JPanel implements Observer {

	private static final long serialVersionUID = 1L;

	private JTextPane _dataField;

	private JScrollPane _scrollPane;

	/**
	 * Model belonging to this pane
	 * @param model
	 */
	public DebugPane(DebugModel model) {
		_dataField = new JTextPane();
		_scrollPane = new JScrollPane(_dataField);
		_dataField.setEditable(false);
		
		model.addObserver(this);
		
		// this.add(_dataField);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(_scrollPane);
	}

	/**
	 * Update the text pane
	 * @param text
	 */
	private void updateTextPane(final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Document doc = _dataField.getDocument();
				
				try {
					doc.insertString(doc.getLength(), text, null);
				} catch (BadLocationException e) {
					throw new RuntimeException(e);
				}
				_dataField.setCaretPosition(doc.getLength());
			}
		});
		
	}

	/**
	 * Update the error text
	 * @param text
	 */
	private void updateTextPaneErr(final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Document doc = _dataField.getDocument();
				StyleContext context = new StyleContext();
				// build a style
				Style style = context.addStyle("err", null);
				// set some style properties
				StyleConstants.setForeground(style, Color.RED);
				try {
					doc.insertString(doc.getLength(), text, style);
				} catch (BadLocationException e) {
					throw new RuntimeException(e);
				}
				_dataField.setCaretPosition(doc.getLength());
			}
		});
	}
	
	/**
	 * Clears the view
	 */
	private void clearView() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				_dataField.setText("");
				_dataField.setCaretPosition(0);
			}
		});
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg1 != null) {
			clearView();
		} else {
			updateTextPane(((DebugModel) arg0).getText());
			updateTextPaneErr(((DebugModel) arg0).getErr());
		}
	}
}