package de.erichseifert.gral.graphics;

import de.erichseifert.gral.util.Orientation;

/**
 * Represents a layout with a specific orientation.
 * @see Orientation
 */
public interface OrientedLayout extends Layout {
	/**
	 * Returns the orientation of this layout.
	 * @return Layout orientation.
	 */
	Orientation getOrientation();
}
