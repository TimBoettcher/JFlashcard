package de.rwth_aachen.itc.jflashcard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * The main GUI of the application. This is where the true magic happens. The UI
 * consists of two labels, two text fields and ten buttons. One text field holds
 * the group name, the other text field the currently presented flashcard side.
 * 
 * @author Tim B&ouml;ttcher, &lt;tim.boettcher2@rwth-aacne.de&gt;
 * @version 1.0.1, 2019-09-28
 * @since 1.0
 */
public class MainGUI {
	// UI elements
	private Shell shell;
	private Label groupLabel;
	private Text group;
	private Label flashcardLabel;
	private Text flashcardText;
	private Button newGroupButton;
	private Button openGroupButton;
	private Button prevButton;
	private Button nextButton;
	private Button shuffleButton;
	private Button unshuffleButton;
	private Button addItemButton;
	private Button removeItemButton;
	private Button editItemButton;
	private Button flipFlashcardButton;

	// internal stuff
	private String currentPath;
	private String currentGroup;
	private int currentIndex;
	private boolean displaySide1;
	private Flashcard currentFlashcard;
	private List<Flashcard> flashcards;
	private List<Flashcard> initialOrder;

	/**
	 * The constructor of the GUI. A display is required for the creation, you can
	 * find that one in program.java.
	 * 
	 * @param display The display containing the shell, colors, fonts etc.
	 */
	public MainGUI(Display display) {
		// We need to do the initialization in the right order,
		// otherwise we risk a NullPointerException.
		// Note that I'm not initializing currentIndex and currentFlashcard, as 0 and
		// null are fine.
		currentGroup = "Keine Gruppe ausgewählt";
		currentPath = "";
		displaySide1 = true;
		flashcards = new ArrayList<>();
		initialOrder = new ArrayList<>();
		// I moved all the UI setup to another place so the constructor is less messy.
		doGUISetup(display);
	}

	private void doGUISetup(Display display) {
		// The shell is our window. Its parent is the display passed to the constructor.
		shell = new Shell(display);
		/*
		 * A GridLayout is table-like. It ensures no components overlap, which is nice
		 * if you can't see. the Grid has four columns which don't have to have equal
		 * width.
		 */
		shell.setLayout(new GridLayout(4, false));
		shell.setText("JFlashcards"); // window title
		shell.addListener(SWT.Close, event -> saveList(event)); // When the window gets closed, the saveList(Event e)
		// method gets called. Lambdas rock.
		groupLabel = new Label(shell, SWT.NONE);
		// the & means that you can move focus to the text field or "click" the buttons
		// by pressing alt (or option) + the letter after the &.
		groupLabel.setText("Aktuelle &Gruppe:");
		group = new Text(shell, SWT.BORDER | SWT.READ_ONLY);
		group.setText(currentGroup);
		newGroupButton = new Button(shell, SWT.PUSH);
		newGroupButton.setText("&Neue Gruppe erstellen...");
		newGroupButton.addListener(SWT.Selection, event -> createNewGroup());
		openGroupButton = new Button(shell, SWT.PUSH);
		openGroupButton.setText("Gruppe &öffnen...");
		openGroupButton.addListener(SWT.Selection, event -> openGroupOnSelection(event));
		flashcardLabel = new Label(shell, SWT.NONE);
		flashcardLabel.setText("&Karteikartentext:");
		flashcardText = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.READ_ONLY); // This is a
		// multi-line
		// text
		// field.
		flashcardText.setText(getCurrentSide()); // This is a private method further down.
		GridData data = new GridData();
		// I want the text field to stretch over three columns vertically.
		// Plus it should claim all available space. Not sure if that's wise visually.
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.horizontalSpan = 3;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		flashcardText.setLayoutData(data);
		// The previous/next buttons are meant to sit close to each other - one aligned
		// right, the other left.
		prevButton = new Button(shell, SWT.PUSH);
		prevButton.setText("&Vorherige Karte");
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalAlignment = SWT.RIGHT;
		prevButton.setLayoutData(data);
		prevButton.setEnabled(false);
		prevButton.addListener(SWT.Selection, event -> getOtherFlashcard(-1));
		nextButton = new Button(shell, SWT.PUSH);
		nextButton.setText("N&ächste Karte");
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalAlignment = SWT.LEFT;
		nextButton.setLayoutData(data);
		nextButton.setEnabled(false);
		nextButton.addListener(SWT.Selection, event -> getOtherFlashcard(1));
		// The shuffle button spans two columns and is aligned center.
		shuffleButton = new Button(shell, SWT.PUSH);
		shuffleButton.setText("Karteikarten &mischen");
		shuffleButton.addListener(SWT.Selection, event -> shuffleFlashcards());
		unshuffleButton = new Button(shell, SWT.PUSH);
		unshuffleButton.setText("Ursprüngliche Ordnung &wiederherstellen");
		unshuffleButton.addListener(SWT.Selection, event -> unshuffle());
		shuffleButton.setEnabled(false);
		unshuffleButton.setEnabled(false);
		addItemButton = new Button(shell, SWT.PUSH);
		addItemButton.setText("Karteikarte &hinzufügen...");
		addItemButton.addListener(SWT.Selection, event -> addNewItem());
		addItemButton.setEnabled(false);
		flipFlashcardButton = new Button(shell, SWT.PUSH);
		flipFlashcardButton.setText("Karteikarte &umdrehen");
		flipFlashcardButton.addListener(SWT.Selection, event -> flipFlashcard());
		flipFlashcardButton.setEnabled(false);
		editItemButton = new Button(shell, SWT.PUSH);
		editItemButton.setText("Karteikarte &bearbeiten...");
		editItemButton.addListener(SWT.Selection, event -> editFlashcard());
		editItemButton.setEnabled(false);
		removeItemButton = new Button(shell, SWT.PUSH);
		removeItemButton.setText("Karteikarte &entfernen");
		removeItemButton.addListener(SWT.Selection, event -> deleteFlashcard());
		removeItemButton.setEnabled(false);
		shell.open(); // Opens the window.
		/*
		 * Here is the so-called message loop. While the shell isn't disposed, so the
		 * window is open, The app will check if there are pending messages, and if not,
		 * the display will just idle around, aka "sleep".
		 */
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private String getCurrentSide() {
		if (currentFlashcard != null) {
			if (displaySide1) {
				return currentFlashcard.getSide1();
			} else {
				return currentFlashcard.getSide2();
			}
		} else {
			return "Keine Karteikarte ausgewählt.";
		}
	}

	/*
	 * Gets called by clicking the openGroupButton. First checks if something needs
	 * to be saved, then displays a file dialog so the user can select the desired
	 * group (aka file).
	 */
	private void openGroupOnSelection(Event e) {
		boolean cont = saveList(e);
		// If cancel gets selected on save, cancel the file dialog also.
		if (cont) {
			String[] data = FileHandler.getGroupFileInfo(shell);
			if (data.length > 0) {
				// update attributes
				currentPath = data[0];
				currentGroup = data[1].substring(0, data[1].lastIndexOf("."));
				group.setText(currentGroup);
				// Update the flashcard-related UI.
				retrieveGroupData();
			}
		}
	}

	private void retrieveGroupData() {
		// If it's null, an error occured while reading the file.
		if (XMLHandler.readFlashcardList(currentGroup, currentPath, shell) != null) {
			flashcards = XMLHandler.readFlashcardList(currentGroup, currentPath, shell);
			initialOrder = new ArrayList<>(flashcards);
			if (flashcards.size() > 0) {
				currentFlashcard = flashcards.get(0);
				flashcardText.setText(getCurrentSide());
				flashcardText.setFocus();
				currentIndex = 0;
			} else {
				flashcardText.setText("Keine Karteikarte ausgewählt.");
				group.setFocus();
			}
		}
		toggleButtons();
	}

	private void flipFlashcard() {
		// Just get the other String (aka side) of the card.
		displaySide1 = !displaySide1;
		flashcardText.setText(getCurrentSide());
		flashcardText.setFocus();
	}

	/*
	 * Displays a simple confirmation dialog for save/don't save/cancel. The event
	 * parameter is necessary to abort the close event of the window.
	 */
	private boolean saveList(Event e) {
		unshuffle();
		if (currentGroup != "Keine Gruppe ausgewählt") {
			MessageBox mb = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
			mb.setText("Möchtest du etwaige Änderungen an der Gruppe \"" + currentGroup + "\" speichern?");
			int dialogResult = mb.open();
			if (dialogResult == SWT.YES) {
				String res = XMLHandler.writeFlashcardList(flashcards, currentGroup, currentPath);
				if (res == "success") {
					MessageBox successMB = new MessageBox(shell, SWT.ICON_INFORMATION);
					successMB.setText("Gruppe erfolgreich gespeichert!");
					successMB.open();
				} else {
					MessageBox errorMB = new MessageBox(shell, SWT.ICON_ERROR);
					errorMB.setText("Ein Fehler ist beim Speichern aufgetreten:" + System.lineSeparator() + res);
					errorMB.open();
					e.doit = false;
				}
			} else if (dialogResult == SWT.CANCEL) {
				e.doit = false;
			}
		}
		return e.doit;
	}

	// Same, just without event as parameter.
	private boolean saveList() {
		unshuffle();
		boolean success = true;
		if (currentGroup != "Keine Gruppe ausgewählt") {
			MessageBox mb = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
			mb.setText("Möchtest du etwaige Änderungen an der Gruppe \"" + currentGroup + "\" speichern?");
			int dialogResult = mb.open();
			if (dialogResult == SWT.YES) {
				String res = XMLHandler.writeFlashcardList(flashcards, currentGroup, currentPath);
				if (res == "success") {
					MessageBox successMB = new MessageBox(shell, SWT.ICON_INFORMATION);
					successMB.setText("Gruppe erfolgreich gespeichert!");
					successMB.open();
					success = true;
				} else {
					MessageBox errorMB = new MessageBox(shell, SWT.ICON_ERROR);
					errorMB.setText("Ein Fehler ist beim Speichern aufgetreten:" + System.lineSeparator() + res);
					errorMB.open();
				}
			} else {
				success = true;
			}
		} else {
			success = true;
		}
		return success;
	}

	/*
	 * This one first shows a prompt to safe any changes, then a folder dialog to
	 * select the path and lastly a custom made input dialog for the name. If only
	 * an empty String gets entered, the dialog will keep reappearing until it gets
	 * canceled or the value is not an empty String (or series of spaces).
	 */
	private void createNewGroup() {
		boolean saveSuccessful = saveList();
		if (saveSuccessful) {
			String path = FileHandler.getPath(shell);
			if (path != null) {
				InputDialog dialog = new InputDialog(shell);
				dialog.setText("Gruppenname");
				dialog.setMessage("Gib den Namen der neuen Gruppe ein:");
				String name = "";
				while (true) {
					name = dialog.open();
					if (name != null && name.strip() != "") {
						boolean success = FileHandler.createGroup(name, path);
						if (success) {
							group.setText(name);
							currentGroup = name;
							currentPath = path;
							retrieveGroupData();
							break;
						} else {
							MessageBox errorMB = new MessageBox(shell, SWT.ICON_ERROR);
							errorMB.setText(
									"Ein Fehler ist beim Erstellen der Gruppe aufgetreten. Die Gruppe existiert bereits oder die Datei konnte nicht erstellt werden.");
						}
					} else {
						if (name != null) {
							MessageBox errorMB = new MessageBox(shell, SWT.ICON_ERROR);
							errorMB.setText("Das Namensfeld darf nicht leer sein.");
							errorMB.open();
						} else {
							break;
						}
					}
				}
			}
		}
	}

	// Get the previous/next flashcard
	// or a flashcard a couple of numbers ahead in the list.
	private void getOtherFlashcard(int moveBy) {
		if ((currentIndex + moveBy) < 0) {
			currentFlashcard = flashcards.get(flashcards.size() - 1);
			currentIndex = flashcards.size() - 1;
			flashcardText.setText(getCurrentSide());
			flashcardText.setFocus();
		} else if (currentIndex + moveBy >= flashcards.size()) {
			currentFlashcard = flashcards.get(0);
			currentIndex = 0;
			flashcardText.setText(getCurrentSide());
			flashcardText.setFocus();
		} else {
			currentIndex += moveBy;
			currentFlashcard = flashcards.get(currentIndex);
			flashcardText.setText(getCurrentSide());
			flashcardText.setFocus();
		}
	}

	private void shuffleFlashcards() {
		Collections.shuffle(flashcards);
		currentFlashcard = flashcards.get(0);
		flashcardText.setText(getCurrentSide());
		flashcardText.setFocus();
		currentIndex = 0;
	}

	private void unshuffle() {
		flashcards = new ArrayList<>(initialOrder);
		if (flashcards.size() > 0) {
			currentFlashcard = flashcards.get(0);
		} else {
			currentFlashcard = null;
		}
		flashcardText.setText(getCurrentSide());
		flashcardText.setFocus();
		currentIndex = 0;
	}

	private void addNewItem() {
		String side1 = "";
		String side2 = "";
		boolean aborted = false;
		while (true) {
			InputDialog dialog = new InputDialog(shell, true);
			dialog.setText("Seite 1 definieren");
			dialog.setMessage("Bitte gib den Text für Seite 1 der Karteikarte ein:");
			side1 = dialog.open();
			if (side1 == null) {
				aborted = true;
				break;
			} else if (side1.strip() == "") {
				MessageBox errorMB = new MessageBox(shell, SWT.ICON_ERROR);
				errorMB.setText("Das Textfeld darf nicht leer sein.");
				errorMB.open();
			} else {
				// This looks absurd, but is what's necessary to get the actual character
				// sequence we want,
				// because we need to escape the backslash and double quote.
				side1.replace("\\", "\\\\");
				side1.replace("\"", "\\\"");
				side1.replaceAll("'", "\\\'");
				break;
			}
		}
		while (true) {
			InputDialog dialog = new InputDialog(shell, true);
			dialog.setText("Seite 2 definieren");
			dialog.setMessage("Bitte gib den Text für Seite 2 der Karteikarte ein:");
			side2 = dialog.open();
			if (side2 == null) {
				aborted = true;
				break;
			} else if (side2.strip() == "") {
				MessageBox errorMB = new MessageBox(shell, SWT.ICON_ERROR);
				errorMB.setText("Das Textfeld darf nicht leer sein.");
				errorMB.open();
			} else {
				side2.replace("\\", "\\\\");
				side2.replace("\"", "\\\"");
				side2.replaceAll("'", "\\\'");
				break;
			}
		}
		if (!aborted) {
			flashcards.add(new Flashcard(side1, side2));
			currentIndex = flashcards.size() - 1;
			currentFlashcard = flashcards.get(currentIndex);
			flashcardText.setText(getCurrentSide());
			flashcardText.setFocus();
			initialOrder.add(new Flashcard(side1, side2));
			toggleButtons();
		}
	}

	// enable/disable buttons as appropriate.
	private void toggleButtons() {
		if (group.getText() != "Keine Gruppe ausgewählt") {
			addItemButton.setEnabled(true);
		} else {
			addItemButton.setEnabled(false);
		}
		if (flashcards.size() > 0) {
			prevButton.setEnabled(true);
			nextButton.setEnabled(true);
			shuffleButton.setEnabled(true);
			unshuffleButton.setEnabled(true);
			editItemButton.setEnabled(true);
			flipFlashcardButton.setEnabled(true);
			removeItemButton.setEnabled(true);
		} else {
			prevButton.setEnabled(false);
			nextButton.setEnabled(false);
			shuffleButton.setEnabled(false);
			unshuffleButton.setEnabled(false);
			editItemButton.setEnabled(false);
			flipFlashcardButton.setEnabled(false);
			removeItemButton.setEnabled(false);
		}
	}

	/*
	 * Offers two dialogs two edit the flashcards. Much like the addNewItem method
	 * No empty text field allowed, as usual.
	 */
	private void editFlashcard() {
		String s1 = "";
		String s2 = "";
		boolean aborted = false;
		while (true) {
			InputDialog dialog = new InputDialog(shell, true);
			dialog.setInput(currentFlashcard.getSide1());
			dialog.setMessage("Seite 1:");
			dialog.setText("Karteikarte bearbeiten");
			s1 = dialog.open();
			if (s1.strip() != "" && s1 != null) {
				break;
			} else {
				if (s1 == null) {
					aborted = true;
					break;
				} else {
					MessageBox errorMB = new MessageBox(shell, SWT.ICON_ERROR);
					errorMB.setText("Das Textfeld darf nicht leer sein.");
				}
			}
		}
		while (true) {
			InputDialog dialog = new InputDialog(shell, true);
			dialog.setInput(currentFlashcard.getSide2());
			dialog.setMessage("Seite 2:");
			dialog.setText("Karteikarte bearbeiten");
			s2 = dialog.open();
			if (s2.strip() != "" && s2 != null) {
				break;
			} else {
				if (s2 == null) {
					aborted = true;
					break;
				} else {
					MessageBox errorMB = new MessageBox(shell, SWT.ICON_ERROR);
					errorMB.setText("Das Textfeld darf nicht leer sein.");
				}
			}
		}
		if (!aborted) {
			s1.replace("\\", "\\\\");
			s1.replace("\"", "\\\"");
			s1.replace("\'", "\\\'");
			s2.replace("\\", "\\\\");
			s2.replace("\"", "\\\"");
			s2.replace("\'", "\\\'");
			currentFlashcard.setSide1(s1);
			currentFlashcard.setSide2(s2);
			flashcardText.setText(getCurrentSide());
			flashcardText.setFocus();
			int matchingIndex = findMatchingFlashcard(currentFlashcard);
			initialOrder.get(matchingIndex).setSide1(s1);
			initialOrder.get(matchingIndex).setSide2(s2);
		}
	}

	/*
	 * Removes the current item from the list. Deletion gets final once the list
	 * gets saved.
	 */
	private void deleteFlashcard() {
		MessageBox mb = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		mb.setText("Bist du sicher, dass du das aktuelle Element \"" + currentFlashcard.getSide1()
				+ "\" löschen möchtest?" + System.lineSeparator()
				+ "Dies löscht die Karteikarte aus der aktuellen Liste, erst nach dem Speichern der Liste ist die Löschung endgültig.");
		int res = mb.open();
		if (res == SWT.YES) {
			int toDelete = currentIndex;
			Flashcard deleteObj = new Flashcard(currentFlashcard);
			getOtherFlashcard(1);
			flashcards.remove(toDelete);
			initialOrder.remove(findMatchingFlashcard(deleteObj));
			if (flashcards.size() == 0) {
				currentFlashcard = null;
				toggleButtons();
			}
			flashcardText.setText(getCurrentSide());
		}
	}

	// Helper function to find the object to delete
	// indexOf uses equals, so doesn't work for clones.
	private int findMatchingFlashcard(Flashcard f) {
		int index = -1;
		for (int i = 0; i < initialOrder.size(); i++) {
			if (f.getSide1() == initialOrder.get(i).getSide1() && f.getSide2() == initialOrder.get(i).getSide2()) {
				index = i;
				break;
			}
		}
		return index;
	}
}