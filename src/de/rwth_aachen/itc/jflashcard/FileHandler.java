package de.rwth_aachen.itc.jflashcard;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Handles file and directory actions. The tasks of this class are:
 * <ul>
 * <li>Get file name and path,</li>
 * <li>Get the path only,</li>
 * <li>Create a new group (aka new XML file).
 * </ul>
 * 
 * @author Tim&ouml;ttcher, &lt;tim.boettcher2@rwth-aachen.de&gt;
 * @version 1.0.1, 2019-09-28
 * @since 1.0
 */
public class FileHandler {

	/**
	 * Creates an empty file named &lt;group name&gt;.xml to store the flashcard
	 * list.
	 * 
	 * @param groupName The name of the group, and thus of the file.
	 * @param path      The path to the file.
	 * @return True if the file was created successfully, otherwise false.
	 */
	public static boolean createGroup(String groupName, String path) {
		try {
			File group = new File(path + "/" + groupName + ".xml");
			// Returns true if the file got created, and false if it already existed.
			return group.createNewFile();
		} catch (IOException ioe) {
			return false;
		}
	}

	/**
	 * Returns the path to a certain directory. This gets used to make the user
	 * select the destination directory of the new group file.
	 * 
	 * @param shell The parent window, used to display the directory dialog.
	 * @return The path to the directory (without trailing \ or /) or null if the
	 *         dialog got canceled.
	 */
	public static String getPath(Shell shell) {
		DirectoryDialog dialog = new DirectoryDialog(shell);
		dialog.setText("Zielordner für die neue Gruppe");
		String path = dialog.open();
		if (path != null) {
			return path;
		} else {
			return null;
		}
	}

	/**
	 * Returns path and group name (with .xml attached). Used to retrieve the info
	 * of a group file, preparing switching to a different group.
	 * 
	 * @param shell The parent window, used to display the file dialog.
	 * @return An array with element 0 being path, element 1 being the group name or
	 *         an empty array if the dialog got canceled.
	 */
	public static String[] getGroupFileInfo(Shell shell) {
		FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		dialog.setText("Gruppe auswählen");
		// We only want XML files to show up, so we need to adjust the filters.
		String[] filterNames = new String[] { "XML-Dateien" };
		String[] filterExtensions = new String[] { "*.xml" };
		dialog.setFilterNames(filterNames);
		dialog.setFilterExtensions(filterExtensions);
		String path = dialog.open();
		String[] res = new String[] {};
		if (path != null) {
			File f = new File(path);
			String filename = f.getName(); // returns <filename>.<extension>
			String pathStr = path.substring(0, path.lastIndexOf(File.separator)); // Returns the path to the last \ or
																					// /, not including it.
			res = new String[] { pathStr, filename };
		}
		return res;
	}
}