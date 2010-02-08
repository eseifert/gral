/**
 * GRAL : Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2010 Erich Seifert <info[at]erichseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
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
 * Lesser GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.erichseifert.gral.examples.xyplot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Random;

import javax.swing.JFrame;

import de.erichseifert.gral.InteractivePanel;
import de.erichseifert.gral.Legend;
import de.erichseifert.gral.DrawableConstants.Location;
import de.erichseifert.gral.DrawableConstants.Orientation;
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
import de.erichseifert.gral.util.Insets2D;


public class ConvolutionExample extends JFrame {

	public ConvolutionExample() {
		super("GRALTest");
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

		//*
		Kernel kernelLowpass = KernelUtils.getBinomial(KERNEL_VARIANCE).normalize();
		Filter dataLowpass = new Convolution(data, kernelLowpass, Filter.Mode.MODE_REPEAT, 1);
		DataSeries dsLowpass = new DataSeries("Lowpass", dataLowpass, 0, 1);

		Kernel kernelHighpass = KernelUtils.getBinomial(KERNEL_VARIANCE).normalize().negate().add(new Kernel(1.0));
		Filter dataHighpass = new Convolution(data, kernelHighpass, Filter.Mode.MODE_REPEAT, 1);
		DataSeries dsHighpass = new DataSeries("Highpass", dataHighpass, 0, 1);

		int kernelMovingAverageSize = (int)Math.round(4.0*KERNEL_VARIANCE);
		Kernel kernelMovingAverage = KernelUtils.getUniform(kernelMovingAverageSize, kernelMovingAverageSize - 1, 1.0).normalize();
		Filter dataMovingAverage = new Convolution(data, kernelMovingAverage, Filter.Mode.MODE_OMIT, 1);
		DataSeries dsMovingAverage = new DataSeries("Moving Average", dataMovingAverage, 0, 1);
		//*/

		int kernelMovingMedianSize = (int)Math.round(4.0*KERNEL_VARIANCE);
		Filter dataMovingMedian = new Median(data, kernelMovingMedianSize, kernelMovingMedianSize - 1, Filter.Mode.MODE_OMIT, 1);
		DataSeries dsMovingMedian = new DataSeries("Moving Median", dataMovingMedian, 0, 1);

		XYPlot plot = new XYPlot(ds, dsLowpass, dsHighpass, dsMovingAverage, dsMovingMedian);

		plot.setPointRenderer(ds, null);
		DefaultLineRenderer2D lineData = new DefaultLineRenderer2D();
		lineData.setSetting(DefaultLineRenderer2D.KEY_COLOR, new Color(0f, 0f, 0f));
		plot.setLineRenderer(ds, lineData);

		//*
		plot.setPointRenderer(dsLowpass, null);
		DefaultLineRenderer2D lineLowpass = new DefaultLineRenderer2D();
		lineLowpass.setSetting(DefaultLineRenderer2D.KEY_COLOR, new Color(1.0f, 0.2f, 0.0f));
		plot.setLineRenderer(dsLowpass, lineLowpass);

		plot.setPointRenderer(dsHighpass, null);
		DefaultLineRenderer2D lineHighpass = new DefaultLineRenderer2D();
		lineHighpass.setSetting(DefaultLineRenderer2D.KEY_COLOR, new Color(0.2f, 0.4f, 0.8f));
		plot.setLineRenderer(dsHighpass, lineHighpass);

		plot.setPointRenderer(dsMovingAverage, null);
		DefaultLineRenderer2D lineMovingAverage = new DefaultLineRenderer2D();
		lineMovingAverage.setSetting(DefaultLineRenderer2D.KEY_COLOR, new Color(0f, 0.67f, 0f));
		plot.setLineRenderer(dsMovingAverage, lineMovingAverage);
		//*/

		plot.setPointRenderer(dsMovingMedian, null);
		DefaultLineRenderer2D lineMovingMedian = new DefaultLineRenderer2D();
		lineMovingMedian.setSetting(DefaultLineRenderer2D.KEY_COLOR, new Color(0.5f, 0f, 0.5f));
		plot.setLineRenderer(dsMovingMedian, lineMovingMedian);

		plot.setSetting(Plot.KEY_LEGEND, true);
		plot.setSetting(Plot.KEY_LEGEND_LOCATION, Location.SOUTH_WEST);
		plot.getLegend().setSetting(Legend.KEY_ORIENTATION, Orientation.HORIZONTAL);

		plot.setInsets(new Insets2D.Double(20.0, 40.0, 40.0, 40.0));
		getContentPane().add(new InteractivePanel(plot), BorderLayout.CENTER);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setMinimumSize(getContentPane().getMinimumSize());
		setSize(900, 600);
	}

	public static void main(String[] args) {
		ConvolutionExample test = new ConvolutionExample();
		test.setVisible(true);
	}
}
