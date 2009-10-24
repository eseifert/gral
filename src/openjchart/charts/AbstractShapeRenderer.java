package openjchart.charts;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import openjchart.Drawable;
import openjchart.data.DataSeries;
import openjchart.data.DataTable;

public abstract class AbstractShapeRenderer implements ShapeRenderer {
	protected Rectangle2D bounds;
	protected Color color;

	public AbstractShapeRenderer() {
	}

	@Override
	public abstract Drawable getShape(DataTable data, DataSeries series, int row);

	@Override
	public Rectangle2D getBounds() {
		return bounds.getBounds2D();
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setBounds(Rectangle2D bounds) {
		this.bounds.setFrame(bounds.getBounds2D());
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
	}

}