package openjchart.plots.axes;



public class LinearRenderer2D extends AbstractAxisRenderer2D {

	@Override
	public double worldToView(Axis axis, Number value) {
		return (value.doubleValue() - axis.getMin().doubleValue())/axis.getRange() * getShapeLength();
	}

	@Override
	public Number viewToWorld(Axis axis, double value) {
		return value/getShapeLength() * axis.getRange() + axis.getMin().doubleValue();
	}
}
