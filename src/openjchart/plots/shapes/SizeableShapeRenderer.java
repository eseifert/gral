package openjchart.plots.shapes;

import java.awt.Shape;
import java.awt.geom.AffineTransform;

import openjchart.data.DataSource;

public class SizeableShapeRenderer extends DefaultShapeRenderer {
	@Override
	public Shape getShapePath(DataSource data, int row) {
		Shape shape = getSetting(KEY_SHAPE);
		if (data.getColumnCount() >= 2) {
			double size = data.get(2, row).doubleValue();
			AffineTransform tx = AffineTransform.getScaleInstance(size, size);
			shape = tx.createTransformedShape(shape);
		}
		return shape;
	}
}
