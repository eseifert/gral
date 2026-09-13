/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2026 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <mseifert[at]error-reports.org>
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

import java.util.Random;

import de.erichseifert.gral.examples.Example;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.graphics.Insets2D;


/**
 * <p>A scatter plot of random points.</p>
 *
 * <p>The smallest complete {@link de.erichseifert.gral.plots.XYPlot} example:
 * fill a {@link de.erichseifert.gral.data.DataTable} and hand it to the
 * plot.</p>
 */
public class ScatterPlot extends Example {
	private static final int SAMPLE_COUNT = 100000;
	/** Instance to generate random data values. */
	private static final Random random = new Random();

	/**
	 * Creates the example and its plot.
	 */
	@SuppressWarnings("unchecked")
	public ScatterPlot() {
		// Generate 100,000 data points
		var data = new DataTable(Double.class, Double.class);
		for (int i = 0; i <= SAMPLE_COUNT; i++) {
			data.add(random.nextGaussian()*2.0,  random.nextGaussian()*2.0);
		}

		// Create a new xy-plot
		var plot = new XYPlot(data);

		// Format plot
		plot.setInsets(new Insets2D.Double(20.0, 40.0, 40.0, 40.0));
		plot.getTitle().setText(getDescription());

		// Format points
		plot.getPointRenderers(data).get(0).setColor(COLOR1);

		// Add plot to Swing component
		setDrawable(plot);
	}

	@Override
	public String getTitle() {
		return "Scatter plot";
	}

	@Override
	public String getDescription() {
		return String.format("Scatter plot with %d data points", SAMPLE_COUNT);
	}

}
