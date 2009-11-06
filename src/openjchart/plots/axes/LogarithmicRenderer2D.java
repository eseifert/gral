package openjchart.plots.axes;



public class LogarithmicRenderer2D extends AbstractAxisRenderer2D {

	@Override
	public double worldToView(Axis axis, Number value) {
		double axisMin = axis.getMin().doubleValue();
		double axisMinLog = (axisMin > 0.0) ? Math.log(axisMin) : 0.0;
		double axisMax = axis.getMax().doubleValue();
		double axisMaxLog = Math.log(axisMax);
		double pos = (Math.log(value.doubleValue()) - axisMinLog)*shapeLength / axisMaxLog;

		return pos;
	}

	@Override
	public Number viewToWorld(Axis axis, double value) {
		// TODO
		return value;
	}

}
