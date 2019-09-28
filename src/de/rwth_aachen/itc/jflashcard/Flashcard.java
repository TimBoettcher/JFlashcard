package de.rwth_aachen.itc.jflashcard;

/**
 * Implementation of a flashcard with two sides. The only purpose of this class
 * is to hold and return the text for the flashcard sides.
 * 
 * @author Tim B&ouml;ttcher, &lt;tim.boettcher2@rwth-aachen.de&gt;
 * @version 1.0.1, 2019-09-28
 * @since 1.0
 */

public class Flashcard {
	private String side1;
	private String side2;

	/**
	 * The normal constructor for this class.
	 * 
	 * @param side1 The first side of the flashcard (must be of type String).
	 * @param side2 The second side of the flashcard (must be of type String).
	 */
	public Flashcard(String side1, String side2) {
		this.side1 = side1;
		this.side2 = side2;
	}

	/**
	 * The copy constructor of the class.
	 * 
	 * @param f The other flashcard object we copy the values from.
	 */
	public Flashcard(Flashcard f) {
		this.side1 = f.getSide1();
		this.side2 = f.getSide2();
	}

	/**
	 * The getter for side1.
	 * 
	 * @return The value of side1 (a String).
	 */
	public String getSide1() {
		return side1;
	}

	/**
	 * The getter for side2.
	 * 
	 * @return The value of side2 (a String).
	 */
	public String getSide2() {
		return side2;
	}

	/**
	 * The setter for side1.
	 * 
	 * @param side1 The value we want to assign to side1 (must be of type String).
	 */
	public void setSide1(String side1) {
		this.side1 = side1;
	}

	/**
	 * The setter for side2.
	 * 
	 * @param side2 The value we want to assign to side2 (must be of type String).
	 */
	public void setSide2(String side2) {
		this.side2 = side2;
	}

	@Override
	public String toString() {
		return "Side 1: " + side1 + System.lineSeparator() + "Side 2: " + side2;
	}

}