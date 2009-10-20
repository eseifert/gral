package openjchart.charts.axes;

import openjchart.Drawable;


/**
 * Basic interface for classes that want to display an axis.
 * @author Michael Seifert
 *
 */
public interface AxisRenderer2D {
	public static enum Orientation {
		HORIZONTAL, VERTICAL
	};

	/**
	 * Returns a component that displays the specified axis.
	 * @param axis axis to be displayed
	 * @param orientation orientation of the axis
	 * @return component displaying the axis
	 * @see Axis
	 */
	Drawable getRendererComponent(Axis axis, Orientation orientation);
}
