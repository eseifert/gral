package de.erichseifert.gral;

/**
 * Indicates the location of {@link Drawable} instances or other elements.
 */
public enum Location {
	/** Central location. */
	CENTER(0.5, 0.5),
	/** Northern location. */
	NORTH(0.5, 0.0),
	/** North-eastern location. */
	NORTH_EAST(1.0, 0.0),
	/** Eastern location. */
	EAST(1.0, 0.5),
	/** South-eastern location. */
	SOUTH_EAST(1.0, 1.0),
	/** Southern location. */
	SOUTH(0.5, 1.0),
	/** South-western location. */
	SOUTH_WEST(0.0, 1.0),
	/** Western location. */
	WEST(0.0,  0.5),
	/** North-western location. */
	NORTH_WEST(0.0, 0.0);

	/** Horizontal alignment. */
	private final double alignH;
	/** Vertical alignment. */
	private final double alignV;

	/**
	 * Constructor that initializes a new location.
	 * @param alignH Horizontal alignment.
	 * @param alignV Vertical alignment.
	 */
	Location(double alignH, double alignV) {
		this.alignH = alignH;
		this.alignV = alignV;
	}

	/**
	 * Returns the horizontal alignment as a double value.
	 * @return horizontal alignment
	 */
	public double getAlignmentH() {
		return alignH;
	}
	/**
	 * Returns the vertical alignment as a double value.
	 * @return vertical alignment
	 */
	public double getAlignmentV() {
		return alignV;
	}
}