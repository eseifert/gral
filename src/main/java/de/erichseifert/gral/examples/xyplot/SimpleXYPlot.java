/*
 * GRAL: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2009-2010 Erich Seifert <info[at]erichseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
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

import de.erichseifert.gral.InteractivePanel;
import de.erichseifert.gral.PlotArea2D;
import de.erichseifert.gral.DrawableConstants.Orientation;
import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.axes.AxisRenderer2D;
import de.erichseifert.gral.plots.axes.LogarithmicRenderer2D;
import de.erichseifert.gral.plots.lines.DiscreteLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer2D;
import de.erichseifert.gral.plots.points.DefaultPointRenderer;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.plots.points.SizeablePointRenderer;
import de.erichseifert.gral.util.Insets2D;


public class SimpleXYPlot extends JFrame {
	private static final Random random = new Random();

	public SimpleXYPlot() {
		super("GRALTest");
		getContentPane().setBackground(new Color(1.0f, 0.99f, 0.95f));

		DataTable data = new DataTable(Double.class, Double.class, Double.class, Double.class);
		for (double x = 1.0; x <= 400.0; x *= 1.5) {
			double x2 = x/5.0;
			data.add(-Math.sqrt(x2) + 5.0,  x2,  5.0*Math.log10(x2),  1.0 + 2.0*random.nextDouble());
		}

		DataSeries seriesLog = new DataSeries(data, 1, 2, 3, 3);
		DataSeries seriesLin = new DataSeries(data, 1, 0, 3);

		XYPlot plot = new XYPlot(seriesLog, seriesLin);
		// Custom plot area formatting
		plot.getPlotArea().setSetting(PlotArea2D.BACKGROUND, new LinearGradientPaint(
				0f,0f, 1f,0f, new float[] {0.00f, 0.05f},
				new Color[] {new Color(0.15f,0.05f,0.00f,0.15f), new Color(0.15f,0.05f,0.00f,0.00f)}));
		plot.getPlotArea().setSetting(PlotArea2D.BORDER, null);
		// Setting the title
		plot.setSetting(XYPlot.TITLE, "A Sample XY Plot");
		// Custom title alignment
		//plot.getTitle().setSetting(Label.ALIGNMENT, 0.3);
		// Custom point renderer
		PointRenderer sizeablePointRenderer = new SizeablePointRenderer();
		plot.setPointRenderer(seriesLin, sizeablePointRenderer);
		PointRenderer defaultPointRenderer = new DefaultPointRenderer();
		defaultPointRenderer.setSetting(PointRenderer.ERROR_DISPLAYED, true);
		plot.setPointRenderer(seriesLog, defaultPointRenderer);
		// Custom point bounds
		//plot.getPointRenderer().setBounds(new Rectangle2D.Double(-10.0, -5.0, 20.0, 5.0));
		// Custom point coloring
		//plot.getPointRenderer().setColor(Color.RED);
		// Custom grid color
		//plot.getPlotArea().setSetting(XYPlot.XYPlotArea2D.GRID_COLOR, Color.BLUE);
		// Grid disabled
		//plot.getPlotArea().setSetting(XYPlot.XYPlotArea2D.GRID_X, false);
		//plot.getPlotArea().setSetting(XYPlot.XYPlotArea2D.GRID_Y, false);
		// Custom line renderer
		LineRenderer2D discreteRenderer = new DiscreteLineRenderer2D();
		discreteRenderer.setSetting(LineRenderer2D.COLOR, new Color(0.5f, 0.2f, 0.0f, 0.7f));
		discreteRenderer.setSetting(LineRenderer2D.STROKE, new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, new float[] {3f, 6f}, 0.0f));
		plot.setLineRenderer(seriesLin, discreteRenderer);
		// Custom gaps for points
		discreteRenderer.setSetting(LineRenderer2D.GAP, 2.0);
		discreteRenderer.setSetting(LineRenderer2D.GAP_ROUNDED, true);
		// Custom ascending
		discreteRenderer.setSetting(DiscreteLineRenderer2D.ASCENT_DIRECTION, Orientation.VERTICAL);
		discreteRenderer.setSetting(DiscreteLineRenderer2D.ASCENDING_POINT, 0.5);
		// Custom axis renderers
		AxisRenderer2D axisRendererX = new LogarithmicRenderer2D();
		axisRendererX.setSetting(AxisRenderer.LABEL, "Logarithmic axis");
		plot.setSetting(XYPlot.AXIS_X_RENDERER, axisRendererX);
		// Custom tick labels
		Map<Double, String> labels = new HashMap<Double, String>();
		labels.put(2.0, "Two");
		labels.put(1.5, "OnePointFive");
		axisRendererX.setSetting(AxisRenderer.TICKS_CUSTOM, labels);
		// Custom stroke for the x-axis
		BasicStroke stroke = new BasicStroke(2f);
		axisRendererX.setSetting(AxisRenderer.SHAPE_STROKE, stroke);
		((AxisRenderer2D) plot.getSetting(XYPlot.AXIS_Y_RENDERER)).setSetting(AxisRenderer.LABEL, "Linear axis");
		// Custom stroke for the ticks
		//axisRendererX.setSetting(AxisRenderer2D.TICK_STROKE, stroke);
		// Swap axis direction
		//axisRendererX.setSetting(AxisRenderer2D.SHAPE_DIRECTION_SWAPPED, true);
		//plot.setAxisYRenderer(new LogarithmicRenderer2D());
		// Change intersection point of Y axis
		AxisRenderer2D axisRendererY = plot.getSetting(XYPlot.AXIS_Y_RENDERER);
		axisRendererY.setSetting(AxisRenderer.INTERSECTION, 1.0);
		// Change tick spacing
		plot.<AxisRenderer>getSetting(XYPlot.AXIS_X_RENDERER).setSetting(AxisRenderer.TICKS_SPACING, 2.0);
		plot.<AxisRenderer>getSetting(XYPlot.AXIS_Y_RENDERER).setSetting(AxisRenderer.TICKS_SPACING, 2.0);

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
