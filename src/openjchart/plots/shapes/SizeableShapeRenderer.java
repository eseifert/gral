package openjchart.plots.shapes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;

import openjchart.AbstractDrawable;
import openjchart.Drawable;
import openjchart.data.DataSource;
import openjchart.util.GeometryUtils;

public class SizeableShapeRenderer extends AbstractShapeRenderer {

	@Override
	public Drawable getShape(final DataSource data, final int row) {
		Drawable drawable = new AbstractDrawable() {
			@Override
			public void draw(Graphics2D g2d) {
				Color colorOld = g2d.getColor();

				g2d.setColor(SizeableShapeRenderer.this.<Color>getSetting(KEY_COLOR));
				double size = data.get(2, row).doubleValue();
				Shape shape = GeometryUtils.grow(SizeableShapeRenderer.this.<Shape>getSetting(KEY_SHAPE), size);

				g2d.fill(shape);
				g2d.setColor(colorOld);
			}
		};

		return drawable;
	}
}
