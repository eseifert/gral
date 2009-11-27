package openjchart;

import java.awt.geom.Rectangle2D;

import openjchart.util.Insets2D;

public interface Container extends Iterable<Drawable> {
	/**
	 * Return the space that this DrawableContainer must leave at each of its edges.
	 * @return The insets of this DrawableContainer
	 */
	Insets2D getInsets();

	/**
	 * Returns the dimensions of this container.
	 * @return Dimensions
	 */
	Rectangle2D getBounds();

	/**
	 * Returns the layout associated with with this container.
	 * @return Layout manager
	 */
	Layout getLayout();

	/**
	 * Adds a new component to this container.
	 * @param drawable Component
	 */
	void add(Drawable drawable);
	/**
	 * Adds a new component to this container.
	 * @param drawable Component
	 * @param constraints Additional information (e.g. for layout)
	 */
	void add(Drawable drawable, Object constraints);

	/**
	 * Return additional information on component
	 * @param drawable Component
	 * @return Information object or <code>null</code>
	 */
	Object getConstraints(Drawable drawable);

	/**
	 * Removes a component from this container.
	 * @param drawable Component
	 */
	void remove(Drawable drawable);

}
