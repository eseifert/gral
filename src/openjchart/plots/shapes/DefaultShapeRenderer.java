package openjchart.plots.shapes;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;

import openjchart.AbstractDrawable;
import openjchart.Drawable;
import openjchart.data.Row;
import openjchart.util.GraphicsUtils;

public class DefaultShapeRenderer extends AbstractShapeRenderer {

	@Override
	public Drawable getShape(final Row row) {
		Drawable drawable = new AbstractDrawable() {
			@Override
			public void draw(Graphics2D g2d) {
				Paint paint = getSetting(KEY_COLOR);
				Shape shape = getShapePath(row);
				GraphicsUtils.fillPaintedShape(g2d, shape, paint, null);
			}
		};

		return drawable;
	}

	@Override
	public Shape getShapePath(Row row) {
		Shape shape = getSetting(KEY_SHAPE);
		return shape;
	}
}
