package openjchart.charts;

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
		bounds = new Rectangle2D.Double(-4.0, -4.0, 8.0, 8.0);
		color = Color.BLACK;
	}

	@Override
	public Drawable getShape(final DataTable data, final DataSeries series, final int row) {
		Drawable drawable = new AbstractDrawable() {
			@Override
			public void draw(Graphics2D graphics) {
				Color colorOld = graphics.getColor();

				graphics.setColor(color);
				Integer sizeCol = series.get(DataSeries.SIZE);
				Shape shape;
				if (sizeCol != null) {
					double size = data.get(sizeCol, row).doubleValue();
					shape = new Rectangle2D.Double(-size/2.0, -size/2.0, size, size);
				}
				else {
					shape = bounds;
				}
				graphics.fill(shape);
				graphics.setColor(colorOld);
			}
		};

		return drawable;
	}
}
