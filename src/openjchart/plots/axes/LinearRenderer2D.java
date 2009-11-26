package openjchart.plots.axes;



public class LinearRenderer2D extends AbstractAxisRenderer2D {

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
		return (val - min)/(max - min)*getShapeLength();
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
		return value/getShapeLength()*(max - min) + min;
	}
}
