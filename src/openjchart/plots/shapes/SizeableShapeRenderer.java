package openjchart.plots.shapes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import openjchart.AbstractDrawable;
import openjchart.Drawable;
import openjchart.data.DataSource;

public class SizeableShapeRenderer extends AbstractShapeRenderer {

	@Override
	public Drawable getShape(final DataSource data, final int row) {
		Drawable drawable = new AbstractDrawable() {
			@Override
			public void draw(Graphics2D g2d) {
				Color colorOld = g2d.getColor();

				Shape shape = getShapePath(data, row);
				g2d.setColor(SizeableShapeRenderer.this.<Color>getSetting(KEY_COLOR));
				g2d.fill(shape);

				g2d.setColor(colorOld);
			}
		};

		return drawable;
	}

	@Override
	public Shape getShapePath(DataSource data, int row) {
		double size = data.get(2, row).doubleValue();
		AffineTransform tx = AffineTransform.getScaleInstance(size, size);

		Shape shapeOrig = getSetting(KEY_SHAPE);
		Shape shape = tx.createTransformedShape(shapeOrig);
		return shape;
	}
}
