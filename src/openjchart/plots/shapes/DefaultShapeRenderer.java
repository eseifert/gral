package openjchart.plots.shapes;

import java.awt.Color;
import java.awt.Graphics2D;
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
				Color colorOld = g2d.getColor();

				Shape shape = getShapePath(data, row);
				g2d.setColor(DefaultShapeRenderer.this.<Color>getSetting(KEY_COLOR));
				g2d.fill(shape);

				g2d.setColor(colorOld);
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
