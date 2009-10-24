package openjchart.charts;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import openjchart.AbstractDrawable;
import openjchart.Drawable;
import openjchart.data.DataSeries;
import openjchart.data.DataTable;

public class DefaultShapeRenderer implements ShapeRenderer {
	private Shape shape;
	private Color color;

	public DefaultShapeRenderer() {
		shape = new Rectangle2D.Float(-4f, -4f, 8f, 8f);
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
				if (sizeCol != null) {
					double size = data.get(sizeCol, row).doubleValue();
					shape = new Rectangle2D.Double(-size/2.0, -size/2.0, size, size);
				}
				graphics.fill(shape);
				graphics.setColor(colorOld);
			}
		};

		return drawable;
	}

}
