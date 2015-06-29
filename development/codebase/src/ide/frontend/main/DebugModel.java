package ide.frontend.main;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Observable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Model to represent debug output to for example a console
 * @author axel
 */
public class DebugModel extends Observable {
	
	/** The text buffer */
	private StringBuffer _text;
	
	/** The error buffer */
	private StringBuffer _err;
	
	/** Lock to synchronize buffer access */
	private Lock _lock;
	
	private final long sleepTime = 500;

	public DebugModel() {
		_text = new StringBuffer();
		_err = new StringBuffer();
		_lock = new ReentrantLock();
		redirectSystemStreams();
		
		(new Thread() {
			private void testBuffer() {
				if (_text.length() > 0 || _err.length() > 0) {
					try {
						_lock.lock();
						setChanged();
						notifyObservers();
						_text.setLength(0);
						_err.setLength(0);
					} catch(Exception e) {
						e.printStackTrace();
					} finally {
						_lock.unlock();
					}
				}
			}
			
			public void run() {
				while(true) {
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					testBuffer();
				}
			}
		}).start();
	}
	
	/**
	 * Clears all views
	 */
	public void clearViews() {
		try {
			_lock.lock();
			setChanged();
			notifyObservers("");
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			_lock.unlock();
		}
	}
	
	/**
	 * Get the contents of the text buffer
	 * @return the regular string buffer
	 */
	public String getText() {
		return _text.toString();
	}
	
	/**
	 * Get the contents of the error buffer
	 * @return the error contents
	 */
	public String getErr() {
		return _err.toString();
	}
	
	/**
	 * Update an buffer [buf] with text [text]
	 * @param text
	 * @param buf
	 */
	private void updateBuffer(final String text, StringBuffer buf) {
		try {
			Thread.sleep(1);
			_lock.lock();
			buf.append(text);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			_lock.unlock();
		}
	}
	
	/**
	 * Update the error buffer
	 * @param text
	 */
	private void updateText(final String text) {
		updateBuffer(text, _text);
	}

	/**
	 * Update the error buffer
	 * @param text
	 */
	private void updateErr(final String text) {
		updateBuffer(text, _err);
	}
	
	/**
	 * Create an text stream
	 * @return
	 */
	private OutputStream getTextStream() {
		return new OutputStream() {
			@Override
			public void write(final int b) throws IOException {
				updateText(String.valueOf((char) b));
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				updateText(new String(b, off, len));
			}

			@Override
			public void write(byte[] b) throws IOException {
				write(b, 0, b.length);
			}
		};
	}
	
	/**
	 * Create an error stream
	 * @return
	 */
	private OutputStream getErrStream() {
		return new OutputStream() {
			@Override
			public void write(final int b) throws IOException {
				updateErr(String.valueOf((char) b));
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				updateErr(new String(b, off, len));
			}

			@Override
			public void write(byte[] b) throws IOException {
				write(b, 0, b.length);
			}
		};
	}

	/**
	 * Redirect the system output
	 */
	private void redirectSystemStreams() {
		System.setOut(new PrintStream(getTextStream(), true));
		System.setErr(new PrintStream(getErrStream(), true));
	}

}
