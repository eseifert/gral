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

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Random;

import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.filters.Convolution;
import de.erichseifert.gral.data.filters.Filter;
import de.erichseifert.gral.data.filters.Kernel;
import de.erichseifert.gral.data.filters.KernelUtils;
import de.erichseifert.gral.data.filters.Median;
import de.erichseifert.gral.examples.ExamplePanel;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.Insets2D;
import de.erichseifert.gral.util.Orientation;

/**
 * Example that shows how to use convultion filtering.
 */
public class ConvolutionExample extends ExamplePanel {
	/** Version id for serialization. */
	private static final long serialVersionUID = 5084898568751883516L;

	private static final int SAMPLE_COUNT = 200;

	@SuppressWarnings("unchecked")
	public ConvolutionExample() {
		// Generate 200 data points
		DataTable data = new DataTable(Double.class, Double.class);
		Random r = new Random();
		for (int i = 0; i < SAMPLE_COUNT; i++) {
			double x = i/2.0/Math.PI;
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
		plot.setLegendVisible(true);

		// Format legend
		plot.getLegend().setOrientation(Orientation.HORIZONTAL);
		plot.getLegend().setAlignmentY(1.0);

		// Format data series as lines of different colors
		formatLine(plot, ds, Color.BLACK);
		formatLine(plot, dsLowpass, COLOR1);
		formatLine(plot, dsHighpass, GraphicsUtils.deriveDarker(COLOR1));
		formatLine(plot, dsMovingAverage, COLOR2);
		formatLine(plot, dsMovingMedian, GraphicsUtils.deriveDarker(COLOR2));

		// Add plot to Swing component
		add(new InteractivePanel(plot), BorderLayout.CENTER);
	}

	private static void formatLine(XYPlot plot, DataSeries series, Color color) {
		plot.setPointRenderer(series, null);
		DefaultLineRenderer2D line = new DefaultLineRenderer2D();
		line.setColor(color);
		plot.setLineRenderer(series, line);
	}

	@Override
	public String getTitle() {
		return "Convolution filtering";
	}

	@Override
	public String getDescription() {
		return "Line plot showing various ways of filtering data with convolution";
	}

	public static void main(String[] args) {
		new ConvolutionExample().showInFrame();
	}
}
