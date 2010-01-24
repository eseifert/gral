/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009-2010, by Erich Seifert and Michael Seifert.
 *
 * This file is part of OpenJChart.
 *
 * OpenJChart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenJChart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenJChart.  If not, see <http://www.gnu.org/licenses/>.
 */

package openjchart.examples.xyplot;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.JFrame;

import openjchart.InteractivePanel;
import openjchart.PlotArea2D;
import openjchart.DrawableConstants.Orientation;
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
	private static final Random random = new Random();

	public SimpleXYPlot() {
		super("OpenJChartTest");
		getContentPane().setBackground(new Color(1.0f, 0.99f, 0.95f));

		DataTable data = new DataTable(Double.class, Double.class, Double.class, Double.class);
		for (double x = 1.0; x <= 400.0; x *= 1.5) {
			double x2 = x/5.0;
			data.add(-Math.sqrt(x2) + 5.0,  x2,  5.0*Math.log10(x2),  1.0 + 2.0*random.nextDouble());
		}

		DataSeries seriesLog = new DataSeries(data, 1, 2);
		DataSeries seriesLin = new DataSeries(data, 1, 0, 3);

		XYPlot plot = new XYPlot(seriesLog, seriesLin);
		// Custom plot area formatting
		plot.getPlotArea().setSetting(PlotArea2D.KEY_BACKGROUND, new LinearGradientPaint(
				0f,0f, 1f,0f, new float[] {0.00f, 0.05f},
				new Color[] {new Color(0.15f,0.05f,0.00f,0.15f), new Color(0.15f,0.05f,0.00f,0.00f)}));
		plot.getPlotArea().setSetting(PlotArea2D.KEY_BORDER, null);
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
		//plot.getPlotArea().setSetting(XYPlot.XYPlotArea2D.KEY_GRID_COLOR, Color.BLUE);
		// Grid disabled
		//plot.getPlotArea().setSetting(XYPlot.XYPlotArea2D.KEY_GRID_X, false);
		//plot.getPlotArea().setSetting(XYPlot.XYPlotArea2D.KEY_GRID_Y, false);
		// Custom line renderer
		LineRenderer2D discreteRenderer = new DiscreteLineRenderer2D();
		discreteRenderer.setSetting(LineRenderer2D.KEY_COLOR, new Color(0.5f, 0.2f, 0.0f, 0.7f));
		discreteRenderer.setSetting(LineRenderer2D.KEY_STROKE, new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, new float[] {3f, 6f}, 0.0f));
		plot.setLineRenderer(seriesLin, discreteRenderer);
		// Custom gaps for shapes
		discreteRenderer.setSetting(LineRenderer2D.KEY_GAP, 2.0);
		discreteRenderer.setSetting(LineRenderer2D.KEY_GAP_ROUNDED, true);
		// Custom ascending
		discreteRenderer.setSetting(DiscreteLineRenderer2D.KEY_ASCENT_DIRECTION, Orientation.VERTICAL);
		discreteRenderer.setSetting(DiscreteLineRenderer2D.KEY_ASCENDING_POINT, 0.5);
		// Custom axis renderers
		AxisRenderer2D axisRendererX = new LogarithmicRenderer2D();
		axisRendererX.setSetting(AxisRenderer2D.KEY_LABEL, "Logarithmic axis");
		plot.setSetting(XYPlot.KEY_AXIS_X_RENDERER, axisRendererX);
		// Custom tick labels
		Map<Double, String> labels = new HashMap<Double, String>();
		labels.put(2.0, "Two");
		labels.put(1.5, "OnePointFive");
		axisRendererX.setSetting(AxisRenderer2D.KEY_TICKS_CUSTOM, labels);
		// Custom stroke for the x-axis
		BasicStroke stroke = new BasicStroke(2f);
		axisRendererX.setSetting(AxisRenderer2D.KEY_SHAPE_STROKE, stroke);
		((AxisRenderer2D) plot.getSetting(XYPlot.KEY_AXIS_Y_RENDERER)).setSetting(AxisRenderer2D.KEY_LABEL, "Linear axis");
		// Custom stroke for the ticks
		//axisRendererX.setSetting(AxisRenderer2D.KEY_TICK_STROKE, stroke);
		// Swap axis direction
		//axisRendererX.setSetting(AxisRenderer2D.KEY_SHAPE_DIRECTION_SWAPPED, true);
		//plot.setAxisYRenderer(new LogarithmicRenderer2D());
		// Change intersection point of Y axis
		AxisRenderer2D axisRendererY = plot.getSetting(XYPlot.KEY_AXIS_Y_RENDERER);
		axisRendererY.setSetting(AxisRenderer2D.KEY_INTERSECTION, 1.0);
		// Change tick spacing
		plot.<AxisRenderer2D>getSetting(XYPlot.KEY_AXIS_X_RENDERER).setSetting(AxisRenderer2D.KEY_TICKS_SPACING, 2.0);
		plot.<AxisRenderer2D>getSetting(XYPlot.KEY_AXIS_Y_RENDERER).setSetting(AxisRenderer2D.KEY_TICKS_SPACING, 2.0);

		plot.setInsets(new Insets2D.Double(20.0, 40.0, 40.0, 40.0));
		getContentPane().add(new InteractivePanel(plot), BorderLayout.CENTER);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(800, 600);
	}

	public static void main(String[] args) {
		SimpleXYPlot test = new SimpleXYPlot();
		test.setVisible(true);
	}
}
