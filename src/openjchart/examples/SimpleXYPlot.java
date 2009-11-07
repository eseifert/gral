package openjchart.examples;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.border.EmptyBorder;

import openjchart.data.DataSeries;
import openjchart.data.DataTable;
import openjchart.plots.XYPlot;
import openjchart.plots.axes.AxisRenderer2D;
import openjchart.plots.axes.LogarithmicRenderer2D;
import openjchart.plots.lines.DiscreteLineRenderer2D;
import openjchart.plots.lines.LineRenderer2D;

public class SimpleXYPlot extends JFrame {

	public SimpleXYPlot() {
		super("OpenJChartTest");
		DataTable data = new DataTable(Double.class, Double.class, Double.class);
		data.add(1.0, 0.00000000000,  3.0);
		data.add(2.0, 0.69314718056,  8.1);
		data.add(3.0, 1.09861228867,  2.5);
		data.add(4.0, 1.38629436112,  5.0);
		data.add(5.0, 1.60943791243, 13.7);
		data.add(6.0, 1.79175946923, 10.2);
		data.add(7.0, 1.94591014906,  3.8);
		data.add(8.0, 2.07944154168, 15.5);

		DataSeries seriesLog = new DataSeries();
		seriesLog.put(DataSeries.X, 0);
		seriesLog.put(DataSeries.Y, 1);
		
		DataSeries seriesLin = new DataSeries();
		seriesLin.put(DataSeries.X, 0);
		seriesLin.put(DataSeries.Y, 0);
		seriesLin.put(DataSeries.SIZE, 2);

		XYPlot plot = new XYPlot(data, seriesLog, seriesLin);
		// Setting the title
		plot.setSetting(XYPlot.KEY_TITLE, "A Sample XY Plot");
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
		// Custom line renderer
		LineRenderer2D discreteRenderer = new DiscreteLineRenderer2D();
		discreteRenderer.setSetting(LineRenderer2D.KEY_LINE_COLOR, Color.RED);
		plot.setLineRenderer(seriesLin, discreteRenderer);
		// Custom insets of start and end points of shapes
		discreteRenderer.setSetting(LineRenderer2D.KEY_POINT_INSETS, new Insets(10, 10, 10, 10));
		// Custom ascending point
		discreteRenderer.setSetting(DiscreteLineRenderer2D.KEY_ASCENDING_POINT, 0.5);
		// Custom axis renderers
		LogarithmicRenderer2D logRendererX = new LogarithmicRenderer2D();
		plot.setAxisXRenderer(logRendererX);
		// Custom stroke for the x-axis
		BasicStroke stroke = new BasicStroke(3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] {15f, 5f, 5f, 5f}, 0.0f);
		logRendererX.setSetting(AxisRenderer2D.KEY_SHAPE_STROKE, stroke);
		// Custom stroke for the ticks
		//logRendererX.setSetting(AxisRenderer2D.KEY_TICK_STROKE, stroke);
		// Swap axis direction
		//logRendererX.setSetting(AxisRenderer2D.KEY_SHAPE_DIRECTION_SWAPPED, true);
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
