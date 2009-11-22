package openjchart.plots.shapes;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;

import openjchart.AbstractDrawable;
import openjchart.Drawable;
import openjchart.data.DataSource;
import openjchart.util.GraphicsUtils;

public class DefaultShapeRenderer extends AbstractShapeRenderer {

	@Override
	public Drawable getShape(final DataSource data, final int row) {
		Drawable drawable = new AbstractDrawable() {
			@Override
			public void draw(Graphics2D g2d) {
				Paint paint = getSetting(KEY_COLOR);
				Shape shape = getShapePath(data, row);
				GraphicsUtils.fillPaintedShape(g2d, shape, paint, null);
			}
		};

		return drawable;
	}

	@Override
	public Shape getShapePath(DataSource data, int row) {
		Shape shape = getSetting(KEY_SHAPE);
		return shape;
	}
}
