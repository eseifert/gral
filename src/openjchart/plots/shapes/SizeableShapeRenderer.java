package openjchart.plots.shapes;

import java.awt.Shape;
import java.awt.geom.AffineTransform;

import openjchart.data.Row;

public class SizeableShapeRenderer extends DefaultShapeRenderer {
	@Override
	public Shape getShapePath(Row row) {
		Shape shape = getSetting(KEY_SHAPE);
		if (row.getSource().getColumnCount() >= 2) {
			double size = row.get(2).doubleValue();
			AffineTransform tx = AffineTransform.getScaleInstance(size, size);
			shape = tx.createTransformedShape(shape);
		}
		return shape;
	}
}
