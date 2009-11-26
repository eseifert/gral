package openjchart.plots.axes;



public class LogarithmicRenderer2D extends AbstractAxisRenderer2D {

	@Override
	public double worldToView(Axis axis, Number value, boolean extrapolate) {
		double axisMin = axis.getMin().doubleValue();
		double axisMax = axis.getMax().doubleValue();
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
		double axisMinLog = (axisMin > 0.0) ? Math.log(axisMin) : 0.0;
		double axisMaxLog = (axisMax > 0.0) ? Math.log(axisMax) : 1.0;
		return (Math.log(value.doubleValue()) - axisMinLog)*getShapeLength() / axisMaxLog;
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
