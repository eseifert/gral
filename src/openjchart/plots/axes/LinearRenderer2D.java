package openjchart.plots.axes;


public class LinearRenderer2D extends AbstractAxisRenderer2D {

	public LinearRenderer2D() {
	}

	@Override
	public double worldToView(Axis axis, Number value) {
		return (value.doubleValue() - axis.getMin().doubleValue())/axis.getRange() * shapeLength;
	}

	@Override
	public Number viewToWorld(Axis axis, double value) {
		return value/shapeLength * axis.getRange() + axis.getMin().doubleValue();
	}
}
