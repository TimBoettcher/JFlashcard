package de.rwth_aachen.itc.jflashcard;

import org.eclipse.swt.SWT;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * This implements an input dialog, similar to the showInputDialog() function of
 * JOptionPane. Customizable parts are the window title, dialog styles, and
 * label of the text input. The dialog's
 * 
 * @author Tim B&ouml;ttcher, &lt;tim.boettcher2@rwth-aachen.de&gt;
 * @version 1.0.1, 2019-09-28
 * @since 1.0
 */

public class InputDialog extends Dialog {
	// result of the text field.
	private String input;
	// text field label
	private String message;
	// multi-line?
	private boolean multi;

	/**
	 * This constructor assigns the default styles to the dialog by calling the more
	 * elaborate constructor with the default styles.
	 * 
	 * @param shell The parent window the dialog will appear in.
	 */
	public InputDialog(Shell shell) {
		this(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	}

	/**
	 * This constructor offers style costumization options.
	 * 
	 * @param shell The parent window the dialog will appear in.
	 * @param style The styles applied to the dialog.
	 */
	public InputDialog(Shell shell, int style) {
		super(shell, style);
		// setText() and setMessage() are inherited methods.
		setText("Input dialog");
		setMessage("Please enter a value:");
		input = "";
	}

	/**
	 * This constructor assigns default styles and a specific value for multi.
	 * 
	 * @param shell The parent window.
	 * @param multi Indicator for multi or single line text field.
	 */
	public InputDialog(Shell shell, boolean multi) {
		this(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL, multi);
	}

	/**
	 * This constructor assigns custom styles as well as a value for multi.
	 * 
	 * @param shell The parent window.
	 * @param style The styles the for the dialog.
	 * @param multi Indicator for multi or single line text field.
	 */
	public InputDialog(Shell shell, int style, boolean multi) {
		this(shell, style);
		this.multi = multi;
	}

	/**
	 * Returns the current text of the label.
	 * 
	 * @return The text of the label.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the text of the label.
	 * 
	 * @param msg The text you wish to assign to the label.
	 */
	public void setMessage(String msg) {
		message = msg;
	}

	/**
	 * Gets the value of the input attribute..
	 * 
	 * @return The value of the input attribute.
	 */
	public String getInput() {
		return input;
	}

	/**
	 * Sets the value of the input attribute. Note this value will not show in the
	 * input field.
	 * 
	 * @param input The value you want to assign to the input attribute.
	 */
	public void setInput(String input) {
		this.input = input;
	}

	/**
	 * Creates the dialog contents and displays them on the screen.
	 * 
	 * @return The value of the input text field or null
	 */
	public String open() {
		Shell shell = new Shell(getParent(), getStyle());
		shell.setText(getText());
		createContents(shell);
		shell.pack();
		shell.open();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return input;
	}

	private void createContents(final Shell shell) {
		/*
		 * All the widgets of the dialog get created here, the styles get set, and the
		 * listeners added. This is a GridLayout, just like in the MainGUI, however, if
		 * the text isn't multi-line the columns are all columns with the same width.
		 */
		if (!multi) {
			shell.setLayout(new GridLayout(2, true));
		} else {
			shell.setLayout(new GridLayout(2, false));
		}
		Label label = new Label(shell, SWT.NONE);
		label.setText(message);
		GridData data = new GridData();
		data.horizontalSpan = 2;
		label.setLayoutData(data);
		// Avoiding issues with final...
		final Text text = getTextObject(shell);
		text.setText(input);
		Button ok = new Button(shell, SWT.PUSH);
		ok.setText("&OK");
		data = new GridData(GridData.FILL_HORIZONTAL);
		ok.setLayoutData(data);
		// As opposed to the MainGUI, I'm adding the event handler inline
		// instead of "outsourcing" the handler to an external method.
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				input = text.getText();
				shell.close();
			}
		});
		Button cancel = new Button(shell, SWT.PUSH);
		cancel.setText("&Abbrechen");
		data = new GridData(GridData.FILL_HORIZONTAL);
		cancel.setLayoutData(data);
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				input = null;
				shell.close();
			}
		});
		if (!multi) {
			shell.setDefaultButton(ok);
		}
	}

	private Text getTextObject(final Shell shell) {
		Text text = null;
		GridData data = null;
		if (!multi) {
			text = new Text(shell, SWT.BORDER);
			data = new GridData(GridData.FILL_HORIZONTAL);
			data.horizontalSpan = 2;
			text.setLayoutData(data);
		} else {
			// XXX In spite of my best efforts, this looks like the text field is very
			// small.
			// Someone with better visual abilities will have to implement this.
			text = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL);
			data = new GridData(GridData.FILL);
			data.horizontalSpan = 2;
			data.grabExcessHorizontalSpace = true;
			data.grabExcessVerticalSpace = true;
			text.setLayoutData(data);
		}
		return text;
	}
}
