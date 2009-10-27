package openjchart.charts.axes;


public class Log10Renderer2D extends AbstractAxisRenderer2D {

	@Override
	public double worldToView(Axis axis, Number value) {
		double axisMin = axis.getMin().doubleValue();
		double axisMinLog = (axisMin > 0.0) ? Math.log10(axisMin) : 0.0;
		double axisMax = axis.getMax().doubleValue();
		double axisMaxLog = Math.log10(axisMax);
		double pos = (Math.log10(value.doubleValue()) - axisMinLog)*shapeLength / axisMaxLog;

		return pos;
	}

	@Override
	public Number viewToWorld(Axis axis, double value) {
		// TODO
		return value;
	}

}
