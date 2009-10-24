package openjchart.charts;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import openjchart.Drawable;
import openjchart.data.DataSeries;
import openjchart.data.DataTable;

public interface ShapeRenderer {
	Drawable getShape(DataTable data, DataSeries series, int row);

	Color getColor();
	void setColor(Color color);

	Rectangle2D getBounds();
	void setBounds(Rectangle2D bounds);
}
