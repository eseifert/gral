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
package de.erichseifert.gral.examples.barplot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.EnumeratedData;
import de.erichseifert.gral.data.statistics.Histogram1D;
import de.erichseifert.gral.data.statistics.Statistics;
import de.erichseifert.gral.plots.BarPlot;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.Insets2D;
import de.erichseifert.gral.util.MathUtils;
import de.erichseifert.gral.util.Orientation;


public class HistogramPlot extends JPanel {
	/** Version id for serialization. */
	private static final long serialVersionUID = 1L;

	public HistogramPlot() {
		super(new BorderLayout());

		// Create example data
		Random random = new Random();
		DataTable data = new DataTable(Double.class);
		for (int i = 0; i < 1000; i++) {
			data.add(random.nextGaussian());
		}

		// Create histogram from data
		Histogram1D histogram = new Histogram1D(data, Orientation.VERTICAL,
				new Number[] {-4.0, -3.2, -2.4, -1.6, -0.8, 0.0, 0.8, 1.6, 2.4, 3.6, 4.0});
		// Create a second dimension (x axis) for plotting
		DataSource histogram2d = new EnumeratedData(histogram, (-4.0 + -3.2)/2.0, 0.8);

		// Create new bar plot
		BarPlot plot = new BarPlot(histogram2d);

		// Format plot
		plot.setInsets(new Insets2D.Double(20.0, 65.0, 50.0, 40.0));
		plot.setSetting(BarPlot.TITLE,
				String.format("Distribution of %d random samples", data.getRowCount()));
		plot.setSetting(BarPlot.BAR_WIDTH, 0.78);

		// Format x axis
		plot.getAxis(BarPlot.AXIS_X).setRange(-4.4, 4.4);
		plot.getAxisRenderer(BarPlot.AXIS_X).setSetting(AxisRenderer.TICKS_ALIGNMENT, 0.0);
		plot.getAxisRenderer(BarPlot.AXIS_X).setSetting(AxisRenderer.TICKS_SPACING, 0.8);
		plot.getAxisRenderer(BarPlot.AXIS_X).setSetting(AxisRenderer.TICKS_MINOR, false);
		// Format y axis
		plot.getAxis(BarPlot.AXIS_Y).setRange(0.0,
				MathUtils.ceil(histogram.getStatistics().get(Statistics.MAX), 25.0));
		plot.getAxisRenderer(BarPlot.AXIS_Y).setSetting(AxisRenderer.TICKS_ALIGNMENT, 0.0);
		plot.getAxisRenderer(BarPlot.AXIS_Y).setSetting(AxisRenderer.TICKS_SPACING, 25.0);
		plot.getAxisRenderer(BarPlot.AXIS_Y).setSetting(AxisRenderer.TICKS_MINOR, false);
		plot.getAxisRenderer(BarPlot.AXIS_Y).setSetting(AxisRenderer.INTERSECTION, -4.4);

		// Format bars
		plot.getPointRenderer(histogram2d).setSetting(
				PointRenderer.COLOR, new Color(0.5f, 0.6f, 0.7f, 0.5f));

		// Add plot to Swing component
		InteractivePanel panel = new InteractivePanel(plot);
		panel.setPannable(false);
		panel.setZoomable(false);
		add(panel);
	}

	public static void main(String[] args) {
		HistogramPlot example = new HistogramPlot();
		JFrame frame = new JFrame("GRALTest");
		frame.getContentPane().add(example, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setVisible(true);
	}
}
