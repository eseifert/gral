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

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import de.erichseifert.gral.Legend;
import de.erichseifert.gral.Location;
import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.filters.Convolution;
import de.erichseifert.gral.data.filters.Filter;
import de.erichseifert.gral.data.filters.Kernel;
import de.erichseifert.gral.data.filters.KernelUtils;
import de.erichseifert.gral.data.filters.Median;
import de.erichseifert.gral.plots.Plot;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.Insets2D;
import de.erichseifert.gral.util.Orientation;

/**
 * Example that shows how to use convultion filtering.
 */
public class ConvolutionExample extends JPanel {

	public ConvolutionExample() {
		super(new BorderLayout());

		// Generate 200 data points
		DataTable data = new DataTable(Double.class, Double.class);
		Random r = new Random();
		for (int i = 0; i < 200; i++) {
			double x = i/2.0/3.141;
			double yError = Math.sqrt(3.0*0.1)*r.nextGaussian();
			double y = 10.0*Math.sin(x/5.0) + yError*yError*yError;
			data.add(x, y);
		}
		DataSeries ds = new DataSeries("Data", data, 0, 1);

		final double KERNEL_VARIANCE = 5.0;

		// Create a smoothed data series from a binomial (near-gaussian) convolution filter
		Kernel kernelLowpass = KernelUtils.getBinomial(KERNEL_VARIANCE).normalize();
		Filter dataLowpass = new Convolution(data, kernelLowpass, Filter.Mode.REPEAT, 1);
		DataSeries dsLowpass = new DataSeries("Lowpass", dataLowpass, 0, 1);

		// Create a derived data series from a binomial convolution filter
		Kernel kernelHighpass = KernelUtils.getBinomial(KERNEL_VARIANCE).normalize().negate().add(new Kernel(1.0));
		Filter dataHighpass = new Convolution(data, kernelHighpass, Filter.Mode.REPEAT, 1);
		DataSeries dsHighpass = new DataSeries("Highpass", dataHighpass, 0, 1);

		// Create a new data series that calculates the moving average using a custom convolution kernel
		int kernelMovingAverageSize = (int)Math.round(4.0*KERNEL_VARIANCE);
		Kernel kernelMovingAverage = KernelUtils.getUniform(kernelMovingAverageSize, kernelMovingAverageSize - 1, 1.0).normalize();
		Filter dataMovingAverage = new Convolution(data, kernelMovingAverage, Filter.Mode.OMIT, 1);
		DataSeries dsMovingAverage = new DataSeries("Moving Average", dataMovingAverage, 0, 1);

		// Create a new data series that calculates the moving median
		int kernelMovingMedianSize = (int)Math.round(4.0*KERNEL_VARIANCE);
		Filter dataMovingMedian = new Median(data, kernelMovingMedianSize, kernelMovingMedianSize - 1, Filter.Mode.OMIT, 1);
		DataSeries dsMovingMedian = new DataSeries("Moving Median", dataMovingMedian, 0, 1);

		// Create a new xy-plot
		XYPlot plot = new XYPlot(ds, dsLowpass, dsHighpass, dsMovingAverage, dsMovingMedian);

		// Format plot
		plot.setInsets(new Insets2D.Double(20.0, 40.0, 40.0, 40.0));
		plot.setSetting(Plot.LEGEND, true);
		plot.setSetting(Plot.LEGEND_LOCATION, Location.SOUTH_WEST);

		// Format legend
		plot.getLegend().setSetting(Legend.ORIENTATION, Orientation.HORIZONTAL);

		// Format data series as lines of different colors
		formatLine(plot, ds, new Color(0f, 0f, 0f));
		formatLine(plot, dsLowpass, new Color(1.0f, 0.2f, 0.0f));
		formatLine(plot, dsHighpass, new Color(0.2f, 0.4f, 0.8f));
		formatLine(plot, dsMovingAverage, new Color(0f, 0.67f, 0f));
		formatLine(plot, dsMovingMedian, new Color(0.5f, 0f, 0.5f));

		// Add plot to Swing component
		add(new InteractivePanel(plot), BorderLayout.CENTER);
	}

	private static void formatLine(XYPlot plot, DataSeries series, Color color) {
		plot.setPointRenderer(series, null);
		DefaultLineRenderer2D line = new DefaultLineRenderer2D();
		line.setSetting(DefaultLineRenderer2D.COLOR, color);
		plot.setLineRenderer(series, line);
	}

	public static void main(String[] args) {
		ConvolutionExample example = new ConvolutionExample();
		JFrame frame = new JFrame("GRALTest");
		frame.getContentPane().add(example, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setVisible(true);
	}
}
