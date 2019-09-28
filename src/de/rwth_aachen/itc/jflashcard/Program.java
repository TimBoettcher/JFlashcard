package de.rwth_aachen.itc.jflashcard;

import org.eclipse.swt.widgets.Display;
/**
 * The entry point of the application.
 * Only holds a Display object, which gets disposed after application shutdown.
 * 
 * @author Tim B&ouml;ttcher, &lt;tim.boettcher2@rwth-aachen.de&gt;
 * @version 1.0.1, 2019-09-28
 * @since 1.0
 */
public class Program {

	public static void main(String[] args) {
		Display display = new Display();
		MainGUI gui = new MainGUI(display);
		display.dispose();
	}

}
