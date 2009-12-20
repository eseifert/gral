package openjchart;

import java.awt.geom.Dimension2D;

/**
 * Interface that provides basic functions for arranging a layout.
 * Functionality includes the arrangement of the layout itself and
 * returning the preferred size of a container.
 */
public interface Layout {

	/**
	 * Arranges the components of this Container according to this Layout.
	 * @param container Container to be laid out.
	 */
	void layout(Container container);

	/**
	 * Returns the preferred size of the specified Container using this Layout.
	 * @param container Container whose preferred size is to be returned.
	 * @return Preferred extent of the specified Container.
	 */
	Dimension2D getPreferredSize(Container container);
}