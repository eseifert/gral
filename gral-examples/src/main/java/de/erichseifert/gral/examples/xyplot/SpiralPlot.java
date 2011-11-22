/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2011 Erich Seifert <dev[at]erichseifert.de>,
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Ellipse2D;

import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.examples.ExamplePanel;
import de.erichseifert.gral.plots.Plot;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.plots.points.SizeablePointRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.Insets2D;


public class SpiralPlot extends ExamplePanel {
	public SpiralPlot() {
		setPreferredSize(new Dimension(600, 600));

		// Generate data
		DataTable data = new DataTable(Double.class, Double.class, Double.class);
		for (double alpha = 0.0, r = 0.0; r <= 10.0; alpha -= 1.0, r += 0.05) {
			double x = r*Math.cos(alpha);
			double y = r*Math.sin(alpha);
			double z = 3.0 + 4.0*r;
			data.add(x, y, z);
		}

		// Create a new data series (optional)
		DataSeries series = new DataSeries("Spiral", data);

		// Create a new xy-plot
		XYPlot plot = new XYPlot(series);

		// Format plot
		plot.setInsets(new Insets2D.Double(40.0));  // Add a margin to the plot
		plot.setSetting(Plot.BACKGROUND, new Color(0.75f, 0.75f, 0.75f));

		// Format plot area
		plot.getPlotArea().setSetting(XYPlot.XYPlotArea2D.BORDER, null);        // Remove border of plot area
		plot.getPlotArea().setSetting(XYPlot.XYPlotArea2D.GRID_MAJOR_X, false); // Disable vertical grid
		plot.getPlotArea().setSetting(XYPlot.XYPlotArea2D.GRID_MAJOR_Y, false); // Disable horizontal grid
		plot.getPlotArea().setSetting(XYPlot.XYPlotArea2D.CLIPPING, null);      // Disable clipping

		// Format axes
		plot.getAxisRenderer(XYPlot.AXIS_X).setSetting(AxisRenderer.SHAPE_VISIBLE, false);  // Disable x axis
		plot.getAxisRenderer(XYPlot.AXIS_X).setSetting(AxisRenderer.TICKS, false);          // Disable tick marks on x axis
		plot.getAxisRenderer(XYPlot.AXIS_Y).setSetting(AxisRenderer.SHAPE_VISIBLE, false);  // Disable y axis
		plot.getAxisRenderer(XYPlot.AXIS_Y).setSetting(AxisRenderer.TICKS, false);          // Disable tick marks on y axis
		plot.getAxis(XYPlot.AXIS_X).setRange(-10.0, 10.0);  // Scale x axis from -10 to 10
		plot.getAxis(XYPlot.AXIS_Y).setRange(-10.0, 10.0);  // Scale y axis from -10 to 10

		// Format data series
		Color color = GraphicsUtils.deriveWithAlpha(COLOR1, 96);
		PointRenderer pointRenderer = new SizeablePointRenderer();
		pointRenderer.setSetting(PointRenderer.SHAPE, new Ellipse2D.Double(-0.5, -0.5, 1.0, 1.0));  // shape of data points
		pointRenderer.setSetting(PointRenderer.COLOR, color);  // color of data points
		pointRenderer.setSetting(SizeablePointRenderer.COLUMN, 2);  // data column which determines the scaling of data point shapes
		plot.setPointRenderer(series, pointRenderer);  // Assign the point renderer to the data series

		add(new InteractivePanel(plot), BorderLayout.CENTER);  // Add the plot to the Swing component
	}

	@Override
	public String getTitle() {
		return "Spiral bubble plot";
	}

	@Override
	public String getDescription() {
		return "Bubble plot showing data points in a spiral-like shape";
	}

	public static void main(String[] args) {
		new SpiralPlot().showInFrame();
	}
}
