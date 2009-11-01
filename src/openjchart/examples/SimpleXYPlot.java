package openjchart.examples;

import java.awt.BasicStroke;
import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.border.EmptyBorder;

import openjchart.charts.axes.AxisRenderer2D;
import openjchart.charts.axes.LogarithmicRenderer2D;
import openjchart.data.DataSeries;
import openjchart.data.DataTable;
import openjchart.plots.XYPlot;

public class SimpleXYPlot extends JFrame {

	public SimpleXYPlot() {
		super("OpenJChartTest");
		/*final DataTable data = new DataTable(Integer.class, Integer.class);
		data.add(1, 1);
		data.add(2, 3);
		data.add(3, 2);
		data.add(4, 6);
		data.add(5, 4);
		data.add(6, 8);
		data.add(7, 9);
		data.add(8, 11);*/
		final DataTable data = new DataTable(Double.class, Double.class, Double.class);
		/*data.add(-8.0, 2.07944154168);
		data.add(-7.0, 1.94591014906);
		data.add(-6.0, 1.79175946923);
		data.add(-5.0, 1.60943791243);
		data.add(-4.0, 1.38629436112);
		data.add(-3.0, 1.09861228867);
		data.add(-2.0, 0.69314718056);
		data.add(-1.0, 0.0);*/
		data.add(1.0, 0.0, 3.0);
		data.add(2.0, 0.69314718056, 3.0);
		data.add(3.0, 1.09861228867, 1.0);
		data.add(4.0, 1.38629436112, 5.0);
		data.add(5.0, 1.60943791243, 3.0);
		data.add(6.0, 1.79175946923, 7.0);
		data.add(7.0, 1.94591014906, 3.0);
		data.add(8.0, 2.07944154168, 4.0);
		DataSeries seriesLog = new DataSeries();
		seriesLog.put(DataSeries.X, 0);
		seriesLog.put(DataSeries.Y, 1);
		DataSeries seriesLin = new DataSeries();
		seriesLin.put(DataSeries.X, 0);
		seriesLin.put(DataSeries.Y, 0);
		seriesLin.put(DataSeries.SIZE, 2);
		XYPlot plot = new XYPlot(data, seriesLog, seriesLin);
		// Setting the title
		plot.setSetting(XYPlot.KEY_TITLE, "An XY plot");
		// Custom title alignment
		//plot.getTitle().setSetting(Label.KEY_ALIGNMENT, 0.3);
		// Custom shape bounds
		//plot.getShapeRenderer().setBounds(new Rectangle2D.Double(-10.0, -5.0, 20.0, 5.0));
		// Custom shape coloring
		//plot.getShapeRenderer().setColor(Color.RED);
		// Custom grid color
		//plot.setSetting(ScatterPlot.KEY_GRID_COLOR, Color.BLUE);
		// Grid disabled
		//plot.setSetting(ScatterPlot.KEY_GRID, false);
		// Custom axis renderers
		LogarithmicRenderer2D logRendererX = new LogarithmicRenderer2D();
		plot.setAxisXRenderer(logRendererX);
		// Custom stroke for the x-axis
		BasicStroke stroke = new BasicStroke(3f);
		logRendererX.setSetting(AxisRenderer2D.KEY_SHAPE_STROKE, stroke);
		// Custom stroke for the ticks
		logRendererX.setSetting(AxisRenderer2D.KEY_TICK_STROKE, stroke);
		//plot.setAxisYRenderer(new LogarithmicRenderer2D());
		plot.getAxisXRenderer().setSetting(AxisRenderer2D.KEY_TICK_SPACING, 0.67);
		plot.setBorder(new EmptyBorder(20, 20, 20, 20));
		getContentPane().add(plot, BorderLayout.CENTER);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(800, 600);
	}

	public static void main(String[] args) {
		SimpleXYPlot test = new SimpleXYPlot();
		test.setVisible(true);
	}
}
