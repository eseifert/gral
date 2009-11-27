package openjchart.examples;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.LinearGradientPaint;

import javax.swing.JFrame;

import openjchart.PlotPanel;
import openjchart.data.DataSeries;
import openjchart.data.DataTable;
import openjchart.plots.XYPlot;
import openjchart.plots.axes.AxisRenderer2D;
import openjchart.plots.axes.LogarithmicRenderer2D;
import openjchart.plots.lines.DiscreteLineRenderer2D;
import openjchart.plots.lines.LineRenderer2D;
import openjchart.plots.shapes.ShapeRenderer;
import openjchart.plots.shapes.SizeableShapeRenderer;
import openjchart.util.Insets2D;

public class SimpleXYPlot extends JFrame {

	public SimpleXYPlot() {
		super("OpenJChartTest");
		DataTable data = new DataTable(Double.class, Double.class, Double.class, Double.class);
		data.add(-1.5,  1.0,  0.00000000000,  0.30);
		data.add( 0.0,  2.0,  0.69314718056,  0.81);
		data.add( 1.5,  3.0,  1.09861228867,  3.50);
		data.add( 4.0,  4.0,  1.38629436112,  0.50);
		data.add( 5.0,  5.0,  1.60943791243,  1.80);
		data.add( 6.0,  6.0,  1.79175946923,  1.02);
		data.add( 7.0,  7.0,  1.94591014906,  0.38);
		data.add( 8.0,  8.0,  2.07944154168,  2.55);

		DataSeries seriesLog = new DataSeries(data, 1, 2);

		DataSeries seriesLin = new DataSeries(data, 1, 0, 3);

		XYPlot plot = new XYPlot(seriesLog, seriesLin);
		// Custom background
		plot.setSetting(XYPlot.KEY_PLOTAREA_BACKGROUND,
				new LinearGradientPaint(0f,0f, 0f,1f, new float[] {0f, 1f}, new Color[] {Color.WHITE, new Color(1f, 0.9f, 0.9f)}));
		// Setting the title
		plot.setSetting(XYPlot.KEY_TITLE, "A Sample XY Plot");
		// Custom title alignment
		//plot.getTitle().setSetting(Label.KEY_ALIGNMENT, 0.3);
		// Custom shape renderer
		ShapeRenderer sizeableShapeRenderer = new SizeableShapeRenderer();
		plot.setShapeRenderer(seriesLin, sizeableShapeRenderer);
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
		discreteRenderer.setSetting(LineRenderer2D.KEY_LINE_STROKE, new BasicStroke(7.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, new float[] {5f, 10f}, 0.0f));
		plot.setLineRenderer(seriesLin, discreteRenderer);
		// Custom gaps for shapes
		discreteRenderer.setSetting(LineRenderer2D.KEY_LINE_GAP, 2.0);
		discreteRenderer.setSetting(LineRenderer2D.KEY_LINE_GAP_ROUNDED, true);
		// Custom ascending point
		discreteRenderer.setSetting(DiscreteLineRenderer2D.KEY_ASCENDING_POINT, 0.5);
		// Custom axis renderers
		AxisRenderer2D logRendererX = new LogarithmicRenderer2D();
		plot.setSetting(XYPlot.KEY_RENDERER_AXIS_X, logRendererX);
		// Custom stroke for the x-axis
		BasicStroke stroke = new BasicStroke(3f);
		logRendererX.setSetting(AxisRenderer2D.KEY_SHAPE_STROKE, stroke);
		// Custom stroke for the ticks
		//logRendererX.setSetting(AxisRenderer2D.KEY_TICK_STROKE, stroke);
		// Swap axis direction
		//logRendererX.setSetting(AxisRenderer2D.KEY_SHAPE_DIRECTION_SWAPPED, true);
		//plot.setAxisYRenderer(new LogarithmicRenderer2D());
		plot.<AxisRenderer2D>getSetting(XYPlot.KEY_RENDERER_AXIS_X).setSetting(AxisRenderer2D.KEY_TICK_SPACING, 0.67);
		plot.setInsets(new Insets2D.Double(40.0, 40.0, 40.0, 40.0));
		getContentPane().add(new PlotPanel(plot), BorderLayout.CENTER);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(800, 600);
	}

	public static void main(String[] args) {
		SimpleXYPlot test = new SimpleXYPlot();
		test.setVisible(true);
	}
}
