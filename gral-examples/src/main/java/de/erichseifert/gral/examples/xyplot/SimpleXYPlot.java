/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2013 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael[at]erichseifert.de>
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
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.examples.ExamplePanel;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.axes.LogarithmicRenderer2D;
import de.erichseifert.gral.plots.lines.DiscreteLineRenderer2D;
import de.erichseifert.gral.plots.points.DefaultPointRenderer2D;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.plots.points.SizeablePointRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.Insets2D;
import de.erichseifert.gral.util.Orientation;


public class SimpleXYPlot extends ExamplePanel {
	/** Version id for serialization. */
	private static final long serialVersionUID = -5263057758564264676L;

	/** Instance to generate random data values. */
	private static final Random random = new Random();

	@SuppressWarnings("unchecked")
	public SimpleXYPlot() {
		// Generate data
		DataTable data = new DataTable(Double.class, Double.class, Double.class,
				Double.class, Double.class, Double.class);
		for (double x = 1.0; x <= 400.0; x *= 1.5) {
			double x2 = x/5.0;
			data.add(x2, -Math.sqrt(x2) + 5.0,  5.0*Math.log10(x2),
				random.nextDouble() + 1.0, random.nextDouble() + 0.5, 1.0 + 2.0*random.nextDouble());
		}

		// Create data series
		DataSeries seriesLog = new DataSeries(data, 0, 2, 3, 4);
		DataSeries seriesLin = new DataSeries(data, 0, 1, 5);

		// Create new xy-plot
		XYPlot plot = new XYPlot(seriesLog, seriesLin);

		// Format plot
		plot.setInsets(new Insets2D.Double(20.0, 40.0, 40.0, 40.0));
		plot.setBackground(Color.WHITE);
		plot.getTitle().setText(getDescription());

		// Format plot area
		plot.getPlotArea().setBackground(new RadialGradientPaint(
			new Point2D.Double(0.5, 0.5),
			0.75f,
			new float[] { 0.6f, 0.8f, 1.0f },
			new Color[] { new Color(0, 0, 0, 0), new Color(0, 0, 0, 32), new Color(0, 0, 0, 128) }
		));
		plot.getPlotArea().setBorderStroke(null);

		// Format axes
		AxisRenderer axisRendererX = new LogarithmicRenderer2D();
		AxisRenderer axisRendererY = plot.getAxisRenderer(XYPlot.AXIS_Y);
		axisRendererX.setLabel("Logarithmic axis");
		plot.setAxisRenderer(XYPlot.AXIS_X, axisRendererX);
		// Custom tick labels
		Map<Double, String> labels = new HashMap<Double, String>();
		labels.put(2.0, "Two");
		labels.put(1.5, "OnePointFive");
		axisRendererX.setCustomTicks(labels);
		// Custom stroke for the x-axis
		BasicStroke stroke = new BasicStroke(2f);
		axisRendererX.setShapeStroke(stroke);
		axisRendererY.setLabel("Linear axis");
		// Change intersection point of Y axis
		axisRendererY.setIntersection(1.0);
		// Change tick spacing
		axisRendererX.setTickSpacing(2.0);

		// Format rendering of data points
		PointRenderer sizeablePointRenderer = new SizeablePointRenderer();
		sizeablePointRenderer.setColor(GraphicsUtils.deriveDarker(COLOR1));
		plot.setPointRenderer(seriesLin, sizeablePointRenderer);
		PointRenderer defaultPointRenderer = new DefaultPointRenderer2D();
		defaultPointRenderer.setColor(GraphicsUtils.deriveDarker(COLOR2));
		defaultPointRenderer.setErrorVisible(true);
		defaultPointRenderer.setErrorColor(COLOR2);
		plot.setPointRenderer(seriesLog, defaultPointRenderer);

		// Format data lines
		DiscreteLineRenderer2D discreteRenderer = new DiscreteLineRenderer2D();
		discreteRenderer.setColor(COLOR1);
		discreteRenderer.setStroke(new BasicStroke(
			3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
			10.0f, new float[] {3f, 6f}, 0.0f));
		plot.setLineRenderer(seriesLin, discreteRenderer);
		// Custom gaps for points
		discreteRenderer.setGap(2.0);
		discreteRenderer.setGapRounded(true);
		// Custom ascending
		discreteRenderer.setAscentDirection(Orientation.VERTICAL);
		discreteRenderer.setAscendingPoint(0.5);

		// Add plot to Swing component
		add(new InteractivePanel(plot), BorderLayout.CENTER);
	}

	@Override
	public String getTitle() {
		return "x-y plot";
	}

	@Override
	public String getDescription() {
		return "Styled x-y plot with example data";
	}

	public static void main(String[] args) {
		new SimpleXYPlot().showInFrame();
	}
}
