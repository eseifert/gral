/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2018 Erich Seifert <dev[at]erichseifert.de>,
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
package de.erichseifert.gral.examples.rasterplot;

import java.awt.Color;
import java.awt.Dimension;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.examples.ExamplePanel;
import de.erichseifert.gral.plots.RasterPlot;
import de.erichseifert.gral.plots.colors.LinearGradient;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.graphics.Insets2D;


public class SimpleRasterPlot extends ExamplePanel {
	/** Version id for serialization. */
	private static final long serialVersionUID = -2515812178479580541L;

	private static final int SIZE = 64;
	private static final double ZOOM = 0.3;

	public SimpleRasterPlot() {
		setPreferredSize(new Dimension(600, 600));

		// Create example data
		DataTable raster = new DataTable(SIZE, Double.class);
		for (int rowIndex = 0; rowIndex < raster.getColumnCount(); rowIndex++) {
			Comparable<?>[] row = new Comparable<?>[raster.getColumnCount()];
			double y = ZOOM*rowIndex;
			for (int colIndex = 0; colIndex < row.length; colIndex++) {
				double x = ZOOM*colIndex;
				row[colIndex] =
					Math.cos(Math.hypot(x - ZOOM*SIZE/2.0, y - ZOOM*SIZE/2.0)) *
					Math.cos(Math.hypot(x + ZOOM*SIZE/2.0, y + ZOOM*SIZE/2.0));
			}
			raster.add(row);
		}

		// Convert raster matrix to (x, y, value)
		DataSource valuesByCoord = RasterPlot.createRasterData(raster);

		// Create new bar plot
		RasterPlot plot = new RasterPlot(valuesByCoord);

		// Format plot
		plot.setInsets(new Insets2D.Double(20.0, 60.0, 40.0, 20.0));
		plot.setColors(new LinearGradient(GraphicsUtils.deriveDarker(COLOR1), COLOR1, Color.WHITE));

		// Add plot to Swing component
		InteractivePanel panel = new InteractivePanel(plot);
		panel.setPannable(false);
		panel.setZoomable(false);
		add(panel);
	}

	@Override
	public String getTitle() {
		return "Raster plot";
	}

	@Override
	public String getDescription() {
		return String.format("Raster plot of %d×%d values", SIZE, SIZE);
	}

	public static void main(String[] args) {
		new SimpleRasterPlot().showInFrame();
	}
}
