package openjchart.plots.shapes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

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

				g2d.setColor(DefaultShapeRenderer.this.<Color>getSetting(KEY_COLOR));
				Shape shape;
				// TODO
				if (data.getColumnCount() > 2) {
					double size = data.get(2, row).doubleValue();
					shape = new Rectangle2D.Double(-size/2.0, -size/2.0, size, size);
				}
				else {
					shape = getSetting(KEY_SHAPE);
				}
				g2d.fill(shape);
				g2d.setColor(colorOld);
			}
		};

		return drawable;
	}
}
