/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2010 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael.seifert[at]gmx.net>
 *
 * This file is part of GRAL.
 *
 * GRAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.erichseifert.gral.examples.xyplot;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import de.erichseifert.gral.PlotArea;
import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.axes.LogarithmicRenderer2D;
import de.erichseifert.gral.plots.lines.DiscreteLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.plots.points.DefaultPointRenderer;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.plots.points.SizeablePointRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.Insets2D;
import de.erichseifert.gral.util.Orientation;


public class SimpleXYPlot extends JPanel {
	private static final Random random = new Random();

	public SimpleXYPlot() {
		super(new BorderLayout());
		setBackground(new Color(1.0f, 0.99f, 0.95f));

		// Generate data
		DataTable data = new DataTable(Double.class, Double.class, Double.class, Double.class);
		for (double x = 1.0; x <= 400.0; x *= 1.5) {
			double x2 = x/5.0;
			data.add(-Math.sqrt(x2) + 5.0,  x2,  5.0*Math.log10(x2),  1.0 + 2.0*random.nextDouble());
		}

		// Create data series
		DataSeries seriesLog = new DataSeries(data, 1, 2, 3, 3);
		DataSeries seriesLin = new DataSeries(data, 1, 0, 3);

		// Create new xy-plot
		XYPlot plot = new XYPlot(seriesLog, seriesLin);

		// Format plot
		plot.setInsets(new Insets2D.Double(20.0, 40.0, 40.0, 40.0));
		plot.setSetting(XYPlot.TITLE, "A Sample XY Plot");

		// Format plot area
		plot.getPlotArea().setSetting(PlotArea.BACKGROUND, new LinearGradientPaint(
				0f,0f, 1f,0f, new float[] {0.00f, 0.05f},
				new Color[] {new Color(0.15f,0.05f,0.00f,0.15f), new Color(0.15f,0.05f,0.00f,0.00f)}));
		plot.getPlotArea().setSetting(PlotArea.BORDER, null);
		// Set custom grid color
		//plot.getPlotArea().setSetting(XYPlot.XYPlotArea2D.GRID_MAJOR_COLOR, Color.BLUE);
		// Disable grid
		//plot.getPlotArea().setSetting(XYPlot.XYPlotArea2D.GRID_MAJOR_X, false);
		//plot.getPlotArea().setSetting(XYPlot.XYPlotArea2D.GRID_MAJOR_Y, false);

		// Format axes
		AxisRenderer axisRendererX = new LogarithmicRenderer2D();
		AxisRenderer axisRendererY = plot.getAxisRenderer(XYPlot.AXIS_Y);
		axisRendererX.setSetting(AxisRenderer.LABEL, "Logarithmic axis");
		plot.setAxisRenderer(XYPlot.AXIS_X, axisRendererX);
		// Custom tick labels
		Map<Double, String> labels = new HashMap<Double, String>();
		labels.put(2.0, "Two");
		labels.put(1.5, "OnePointFive");
		axisRendererX.setSetting(AxisRenderer.TICKS_CUSTOM, labels);
		// Custom stroke for the x-axis
		BasicStroke stroke = new BasicStroke(2f);
		axisRendererX.setSetting(AxisRenderer.SHAPE_STROKE, stroke);
		axisRendererY.setSetting(AxisRenderer.LABEL, "Linear axis");
		// Custom stroke for the ticks
		//axisRendererX.setSetting(AxisRenderer.TICKS_STROKE, stroke);
		// Swap axis direction
		//axisRendererX.setSetting(AxisRenderer.SHAPE_DIRECTION_SWAPPED, true);
		//plot.setAxisRenderer(XYPlot.AXIS_Y, new LogarithmicRenderer2D());
		// Change intersection point of Y axis
		axisRendererY.setSetting(AxisRenderer.INTERSECTION, 1.0);
		// Change tick spacing
		axisRendererX.setSetting(AxisRenderer.TICKS_SPACING, 2.0);
		axisRendererY.setSetting(AxisRenderer.TICKS_SPACING, 2.0);

		// Format rendering of data points
		PointRenderer sizeablePointRenderer = new SizeablePointRenderer();
		plot.setPointRenderer(seriesLin, sizeablePointRenderer);
		PointRenderer defaultPointRenderer = new DefaultPointRenderer();
		defaultPointRenderer.setSetting(PointRenderer.ERROR_DISPLAYED, true);
		plot.setPointRenderer(seriesLog, defaultPointRenderer);
		// Custom point bounds
		//plot.getPointRenderer(seriesLog).setBounds(new Rectangle2D.Double(-10.0, -5.0, 20.0, 5.0));
		// Custom point coloring
		//plot.getPointRenderer().setColor(Color.RED);

		// Format data lines
		LineRenderer discreteRenderer = new DiscreteLineRenderer2D();
		discreteRenderer.setSetting(LineRenderer.COLOR, new Color(0.5f, 0.2f, 0.0f, 0.7f));
		discreteRenderer.setSetting(LineRenderer.STROKE, new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, new float[] {3f, 6f}, 0.0f));
		plot.setLineRenderer(seriesLin, discreteRenderer);
		// Custom gaps for points
		discreteRenderer.setSetting(LineRenderer.GAP, 2.0);
		discreteRenderer.setSetting(LineRenderer.GAP_ROUNDED, true);
		// Custom ascending
		discreteRenderer.setSetting(DiscreteLineRenderer2D.ASCENT_DIRECTION, Orientation.VERTICAL);
		discreteRenderer.setSetting(DiscreteLineRenderer2D.ASCENDING_POINT, 0.5);

		// Add plot to Swing component
		add(new InteractivePanel(plot), BorderLayout.CENTER);
	}

	public static void main(String[] args) {
		SimpleXYPlot example = new SimpleXYPlot();
		JFrame frame = new JFrame("GRALTest");
		frame.getContentPane().add(example, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setVisible(true);
	}
}
