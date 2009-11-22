package openjchart.plots.shapes;

import java.awt.Shape;
import java.awt.geom.AffineTransform;

import openjchart.data.DataSource;

public class SizeableShapeRenderer extends DefaultShapeRenderer {
	@Override
	public Shape getShapePath(DataSource data, int row) {
		double size = data.get(2, row).doubleValue();
		AffineTransform tx = AffineTransform.getScaleInstance(size, size);

		Shape shapeOrig = getSetting(KEY_SHAPE);
		Shape shape = tx.createTransformedShape(shapeOrig);
		return shape;
	}
}
