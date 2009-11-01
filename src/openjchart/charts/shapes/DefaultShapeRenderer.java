package openjchart.charts.shapes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import openjchart.AbstractDrawable;
import openjchart.Drawable;
import openjchart.data.DataSeries;
import openjchart.data.DataTable;

public class DefaultShapeRenderer extends AbstractShapeRenderer implements ShapeRenderer {

	public DefaultShapeRenderer() {
	}

	@Override
	public Drawable getShape(final DataTable data, final DataSeries series, final int row) {
		Drawable drawable = new AbstractDrawable() {
			@Override
			public void draw(Graphics2D graphics) {
				Color colorOld = graphics.getColor();

				graphics.setColor(DefaultShapeRenderer.this.<Color>getSetting(KEY_COLOR));
				Integer sizeCol = series.get(DataSeries.SIZE);
				Shape shape;
				if (sizeCol != null) {
					double size = data.get(sizeCol, row).doubleValue();
					shape = new Rectangle2D.Double(-size/2.0, -size/2.0, size, size);
				}
				else {
					shape = getSetting(KEY_SHAPE);
				}
				graphics.fill(shape);
				graphics.setColor(colorOld);
			}
		};

		return drawable;
	}
}
