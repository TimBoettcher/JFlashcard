package de.rwth_aachen.itc.jflashcard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Contains static methods which handle XML-related tasks (file creation, save,
 * load). More verbosely, the tasks are as follows:
 * <ul>
 * <li>Create an XML file to store a flashcard group,</li>
 * <li>Parse a flashcard list into XML, then save the list,</li>
 * <li>Convert XML to a flashcard list.</li>
 * </ul>
 * 
 * @author Tim B&ouml;ttcher, &lt;tim.boettcher2@rwth-aachen.de&gt;
 * @version 1.0.1, 2019-09-28
 * @since 1.0
 */
public class XMLHandler {

	/**
	 * Parses a specified flashcard group file into a list of flashcards. The list
	 * of flashcards returned by this method can later get used in the UI.
	 * 
	 * @param flashcardList The list of flashcards in the group.
	 * @param groupName     The name of the flashcard group and thus of the XML
	 *                      file.
	 * @param path          The path to the XML file.
	 * @return A list of flashcard objects if the execution was successful, an empty
	 *         list if no objects were detected and null if an exception got thrown.
	 */
	public static String writeFlashcardList(List<Flashcard> flashcardList, String groupName, String path) {
		// Our setup for the XML document.
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			Document doc = db.newDocument();
			// Now the root element of the XML file.
			Element root = doc.createElement("flashcards");
			// Now let's go through all flashcards and append their values.
			for (Flashcard f : flashcardList) {
				// Element which holds the values of the sides.
				Element el = doc.createElement("flashcard");
				// Now the sides themselves...
				Element subEl1 = doc.createElement("side1");
				Element subEl2 = doc.createElement("side2");
				// The String values of the sides should be left alone by XML,
				// so we insert them as text nodes, not attributes.
				Node data1 = doc.createCDATASection(f.getSide1());
				Node data2 = doc.createCDATASection(f.getSide2());
				// Now we need to add everything to the appropriate elements.
				subEl1.appendChild(data1);
				subEl2.appendChild(data2);
				el.appendChild(subEl1);
				el.appendChild(subEl2);
				root.appendChild(el);
			}
			doc.appendChild(root);
			// We have the XML now, but we still need to write it to a file...
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			// For better human-readability of the XML file.
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			DOMSource source = new DOMSource(doc);
			StreamResult sr = new StreamResult(new File(path + File.separator + groupName + ".xml"));
			transformer.transform(source, sr);
			return "success";
		} catch (TransformerException te) {
			return te.getLocalizedMessage();
		} catch (ParserConfigurationException pce) {
			return pce.getLocalizedMessage();
		}
	}

	public static List<Flashcard> readFlashcardList(String groupName, String path, Shell shell) {
		// The same procedure as last year?
		// No wait, the same procedure as in above method ;-)
		List<Flashcard> flashcards = new ArrayList<>();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document doc;
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(path + File.separator + groupName + ".xml");
			// Get the root element.
			Element docEl = doc.getDocumentElement();
			// Now get a list of all flashcard elements.
			NodeList nl1 = docEl.getElementsByTagName("side1");
			NodeList nl2 = docEl.getElementsByTagName("side2");
			// Are there any nodes in there? (In that case, the nodes are the text values)
			if (nl1.getLength() > 0 && nl1.item(0).hasChildNodes()) {
				// Iterate over all elements with tag side1 and side2...
				for (int i = 0; i < nl2.getLength(); i++) {
					// strip is necessary to get rid of the indentation before the CDATA sections.
					String s1 = nl1.item(i).getTextContent().strip();
					String s2 = nl2.item(i).getTextContent().strip();
					// Create a flashcard object and add it to the list.
					Flashcard f = new Flashcard(s1, s2);
					flashcards.add(f);
				}
			}
			return flashcards;
		} catch (ParserConfigurationException pce) {
			MessageBox errorMB = new MessageBox(shell, SWT.ICON_ERROR);
			errorMB.setText("Ein Fehler ist aufgetreten:" + System.lineSeparator() + pce.getLocalizedMessage());
			return null;
		} catch (SAXException se) {
			MessageBox errorMB = new MessageBox(shell, SWT.ICON_ERROR);
			errorMB.setText("Ein Fehler ist aufgetreten:" + System.lineSeparator() + se.getLocalizedMessage());
			return null;
		} catch (IOException ioe) {
			MessageBox errorMB = new MessageBox(shell, SWT.ICON_ERROR);
			errorMB.setText("Ein Fehler ist aufgetreten:" + System.lineSeparator() + ioe.getLocalizedMessage());
			return null;
		}
	}
}
