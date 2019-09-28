# JFlashcard
Project intended as accessible flashcard solution and Java playground for the MATSEs.
## What is JFlashcard?
JFlashcard is a desktop application with a **GUI** (Graphical User Interface) written in Java. As `javax.swing` doesn't appear to be accessible in spite of Oracle saying [it is](https://docs.oracle.com/javase/tutorial/uiswing/misc/access.html), JFlashcard relies on [SWT](https://www.eclipse.org/swt/) for the GUI creation. This means the application looks and behaves differently on different operating systems, as SWT uses the native components to create the UI.

## Why should I use it?
I'm not going to lie: There are way better flashcard applications out there. Nonetheless, JFlashcard might be worth considering if
- you're blind (like me) and want a fully accessible solution,
- you want to decide which features the flashcard app includes,
- you're curious how creating an app with a GUI in Java works.

## How does it work?
JFlashcard has two important internal components: **Groups** which are, in fact, just XML files and contain a set of flashcards, and **flashcards** which are actual Java objects. The objects have two attributes which define the text for each side of the flashcard. The UI modifies either the groups, the flashcards in the groups, or the flashcards in the internal list at runtime.

## Features
The following features are currently supported:
- creating new groups,
- opening existing groups,
- Adding new flashcards to the current group,
- going to the previous/next flashcard,
- flipping the current flashcard,
- edditing the current flashcard,
- shuffling all flashcards, and getting them back into the initial order,
- editing the current flashcard,
- deleting the current flashcard.

## Installation

You can either clone this repository to your local machine using `git pull https://github.com/TimBoettcher/JFlashcard.git` or download it as zip and unpack it somewhere. To integrate it into Eclipse, go to File -> import... -> select "existing project into workspace" -> click next -> choose path to the root directory of the project -> select the package "de.rwth_aachen.itc.jflashcard" -> select other options you consider helpful -> click finish. 

**NOTE: The GUI of the flashcard is currently written in German. Localization is planned, but not implemented yet.**
