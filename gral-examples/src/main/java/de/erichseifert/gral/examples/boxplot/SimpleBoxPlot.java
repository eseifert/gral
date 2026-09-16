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
package de.erichseifert.gral.examples.boxplot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Random;

import de.erichseifert.gral.examples.Example;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.BoxPlot;
import de.erichseifert.gral.plots.BoxPlot.BoxWhiskerRenderer;
import de.erichseifert.gral.plots.XYPlot.XYNavigationDirection;
import de.erichseifert.gral.plots.colors.LinearGradient;
import de.erichseifert.gral.util.DataUtils;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.graphics.Insets2D;


/**
 * <p>A box-and-whisker plot of three columns of random values.</p>
 *
 * <p>Shows how {@link de.erichseifert.gral.plots.BoxPlot#createBoxData(
 * de.erichseifert.gral.data.DataSource)} turns raw observations into the
 * six-column summary the plot expects, and how the boxes and whiskers are
 * styled through {@code BoxPlot.BoxWhiskerRenderer}.</p>
 */
public class SimpleBoxPlot extends Example {
	private static final int SAMPLE_COUNT = 50;
	private final Random random = createRandom();

	/**
	 * Creates the example and its plot.
	 */
	@SuppressWarnings("unchecked")
	public SimpleBoxPlot() {
		setPreferredSize(new Dimension(400, 600));

		// Create example data
		var data = new DataTable(Integer.class, Integer.class, Integer.class);
		for (int i = 0; i < SAMPLE_COUNT; i++) {
			int x = (int) Math.round(5.0*random.nextGaussian());
			int y = (int) Math.round(5.0*random.nextGaussian());
			int z = (int) Math.round(5.0*random.nextGaussian());
			data.add(x, y, z);
		}

		// Create new box-and-whisker plot
		DataSource boxData = BoxPlot.createBoxData(data);
		var plot = new BoxPlot(boxData);

		// Format plot
		plot.setInsets(new Insets2D.Double(20.0, 50.0, 40.0, 20.0));

		// Format axes
		plot.getAxisRenderer(BoxPlot.AXIS_X).setCustomTicks(
			DataUtils.map(
					new Double[] {1.0, 2.0, 3.0},
					new String[] {"Column 1", "Column 2", "Column 3"}
			)
		);

		// Format boxes
		var stroke = new BasicStroke(2f);
		var colors = new LinearGradient(GraphicsUtils.deriveBrighter(COLOR1), Color.WHITE);
		colors.setRange(1.0, 3.0);

		BoxWhiskerRenderer pointRenderer =
				(BoxWhiskerRenderer) plot.getPointRenderers(boxData).get(0);
		pointRenderer.setWhiskerStroke(stroke);
		pointRenderer.setBoxBorderStroke(stroke);
		pointRenderer.setBoxBackground(colors);
		pointRenderer.setBoxBorderColor(COLOR1);
		pointRenderer.setWhiskerColor(COLOR1);
		pointRenderer.setCenterBarColor(COLOR1);

		plot.getNavigator().setDirection(XYNavigationDirection.VERTICAL);

		// Add plot to Swing component
		setDrawable(plot);
	}

	@Override
	public String getTitle() {
		return "Box-and-whisker plot";
	}

	@Override
	public String getDescription() {
		return String.format("Three box-and-whisker plots created from %d random samples", SAMPLE_COUNT);
	}
}
