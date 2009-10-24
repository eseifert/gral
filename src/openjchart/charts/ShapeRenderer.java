package openjchart.charts;

import openjchart.Drawable;
import openjchart.data.DataSeries;
import openjchart.data.DataTable;

public interface ShapeRenderer {
	Drawable getShape(DataTable data, DataSeries series, int row);
}
