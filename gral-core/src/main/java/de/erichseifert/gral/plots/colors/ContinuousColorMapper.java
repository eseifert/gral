package de.erichseifert.gral.plots.colors;

import java.awt.Paint;

import de.erichseifert.gral.util.MathUtils;

/**
 * Interface that maps numbers to Paint objects. This can be used to generate
 * colors or gradients for various elements in a plot, e.g. lines, areas, etc.
 */
public abstract class ContinuousColorMapper extends AbstractColorMapper<Double> {
	/** Version id for serialization. */
	private static final long serialVersionUID = 4616781244057993699L;

	/**
	 * Returns the Paint object according to the specified value.
	 * @param value Numeric value.
	 * @return Paint object.
	 */
	public abstract Paint get(double value);

	/**
	 * Returns the Paint object according to the specified value. The specified
	 * value will be handled like a double value.
	 * @param value Numeric value object.
	 * @return Paint object.
	 */
	public Paint get(Number value) {
		return get(value.doubleValue());
	}

	@Override
	protected Double applyMode(Double value, Double rangeMin, Double rangeMax) {
		if (value.doubleValue() >= rangeMin.doubleValue() &&
				value.doubleValue() <= rangeMax.doubleValue()) {
			return value;
		}
		Mode mode = getMode();
		if (mode == Mode.REPEAT) {
			return MathUtils.limit(value, rangeMin, rangeMax);
		} else if (mode == Mode.CIRCULAR) {
			double range = rangeMax.doubleValue() - rangeMin.doubleValue();
			double i = value.doubleValue()%range;
			if (i < 0.0) {
				i += range;
			}
			return i + rangeMin.doubleValue();
		}
		return null;
	}
}
