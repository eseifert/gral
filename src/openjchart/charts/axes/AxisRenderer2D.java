package openjchart.charts.axes;

import openjchart.Drawable;


/**
 * Basic interface for classes that want to display an axis.
 * @author Michael Seifert
 *
 */
public interface AxisRenderer2D {
	/**
	 * Returns a component that displays the specified axis.
	 * @param axis axis to be displayed
	 * @return component displaying the axis
	 * @see Axis
	 */
	Drawable getRendererComponent(Axis axis);

	double worldToView(Axis axis, Number value);
	Number viewToWorld(Axis axis, double value);
}
