package openjchart.plots.axes;


public class Log10Renderer2D extends AbstractAxisRenderer2D {

	@Override
	public double worldToView(Axis axis, Number value, boolean extrapolate) {
		double min = axis.getMin().doubleValue();
		double max = axis.getMax().doubleValue();
		double val = value.doubleValue();
		if (!extrapolate) {
			if (val <= min) {
				return 0.0;
			}
			if (val >= max) {
				return getShapeLength();
			}
		}
		double minLog = (min > 0.0) ? Math.log10(min) : 0.0;
		double maxLog = Math.log10(max);
		return (Math.log10(value.doubleValue()) - minLog)*getShapeLength() / maxLog;
	}

	@Override
	public Number viewToWorld(Axis axis, double value, boolean extrapolate) {
		double min = axis.getMin().doubleValue();
		double max = axis.getMax().doubleValue();
		if (!extrapolate) {
			if (value <= 0.0) {
				return min;
			}
			if (value >= getShapeLength()) {
				return max;
			}
		}
		// TODO
		return value;
	}

}
