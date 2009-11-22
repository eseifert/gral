package openjchart.plots.shapes;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;

import openjchart.AbstractDrawable;
import openjchart.Drawable;
import openjchart.data.DataSource;

public class DefaultShapeRenderer extends AbstractShapeRenderer {

	@Override
	public Drawable getShape(final DataSource data, final int row) {
		Drawable drawable = new AbstractDrawable() {
			@Override
			public void draw(Graphics2D g2d) {
				Paint paintOld = g2d.getPaint();

				Shape shape = getShapePath(data, row);
				g2d.setPaint(DefaultShapeRenderer.this.<Paint>getSetting(KEY_COLOR));
				g2d.fill(shape);

				g2d.setPaint(paintOld);
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
