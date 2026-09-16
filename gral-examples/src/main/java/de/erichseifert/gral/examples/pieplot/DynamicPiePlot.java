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
package de.erichseifert.gral.examples.pieplot;

import java.text.MessageFormat;
import java.util.Random;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.examples.Example;
import de.erichseifert.gral.graphics.Insets2D;
import de.erichseifert.gral.plots.PiePlot;
import de.erichseifert.gral.plots.PiePlot.PieSliceRenderer;
import de.erichseifert.gral.plots.colors.LinearGradient;


/**
 * <p>A pie plot whose data changes while it is displayed, driven by a
 * slider.</p>
 *
 * <p>Shows that a plot follows its data source: adding and removing rows of the
 * {@link de.erichseifert.gral.data.DataTable} is enough to update the pie, no
 * repaint call is needed. Also demonstrates the inner radius, which turns the
 * pie into a ring, and how negative values leave their slice empty.</p>
 */
public class DynamicPiePlot extends Example implements Example.Adjustable {
	private static final int SAMPLE_COUNT = 5;
	/** Instance to generate random data values. */
	private final Random random = createRandom();

	private final DataTable data;
	private final PiePlot plot;

	/**
	 * Creates the example and its plot.
	 */
	@SuppressWarnings("unchecked")
	public DynamicPiePlot() {
		// Create initial data
		data = new DataTable(Integer.class);
		DataSource pieData = PiePlot.createPieData(data);

		// Create new pie plot
		plot = new PiePlot(pieData);
		// Change relative size of pie
		plot.setRadius(0.9);
		// Change the starting angle of the first pie slice
		plot.setStart(90.0);
		// Add some margin to the plot area
		plot.setInsets(new Insets2D.Double(20.0));

		PieSliceRenderer pointRenderer =
				(PieSliceRenderer) plot.getPointRenderer(pieData);
		// Change the width of gaps between segments
		pointRenderer.setGap(0.2);
		// Change the colors
		var colors = new LinearGradient(COLOR1, COLOR2);
		pointRenderer.setColor(colors);

		setDrawable(plot);
		setValueCount(SAMPLE_COUNT);
	}

	@Override
	public int getMinimum() {
		return 0;
	}

	@Override
	public int getMaximum() {
		return 50;
	}

	@Override
	public int getTickSpacing() {
		return 10;
	}

	@Override
	public int getValue() {
		return data.getRowCount();
	}

	@Override
	public String getTitle() {
		return "Pie plot";
	}

	@Override
	public String getDescription() {
		return "Pie with a changeable number of random data values";
	}

	@Override
	public void setValue(int count) {
		setValueCount(count);
	}

	private void setValueCount(int count) {
		if (count == data.getRowCount()) {
			return;
		}
		while (data.getRowCount() != count) {
			if (data.getRowCount() < count) {
				int val = random.nextInt(10) + 1;
				data.add(val);
			} else {
				int rowIndexLast = data.getRowCount() - 1;
				data.remove(rowIndexLast);
			}
		}
		if (plot != null) {
			String title = MessageFormat.format("{0,number,integer} random values", data.getRowCount());
			plot.getTitle().setText(title);
		}
	}
}
