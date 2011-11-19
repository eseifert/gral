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
package de.erichseifert.gral.examples.rasterplot;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.RasterPlot;
import de.erichseifert.gral.plots.colors.HeatMap;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.Insets2D;


public class SimpleRasterPlot extends JPanel {
	/** Version id for serialization. */
	private static final long serialVersionUID = 1L;

	public SimpleRasterPlot() {
		super(new BorderLayout());

		// Create example data
		int size = 128;
		double f = 0.15;
		DataTable raster = new DataTable(size, Double.class);
		for (int rowIndex = 0; rowIndex < raster.getColumnCount(); rowIndex++) {
			Number[] row = new Number[raster.getColumnCount()];
			double y = f*rowIndex;
			for (int colIndex = 0; colIndex < row.length; colIndex++) {
				double x = f*colIndex;
				row[colIndex] =
					Math.cos(Math.hypot(x - f*size/2.0, y - f*size/2.0)) *
					Math.cos(Math.hypot(x + f*size/2.0, y + f*size/2.0));
			}
			raster.add(row);
		}

		// Convert raster matrix to (x, y, value)
		DataSource valuesByCoord = RasterPlot.createRasterData(raster);

		// Create new bar plot
		RasterPlot plot = new RasterPlot(valuesByCoord);

		// Format plot
		plot.setInsets(new Insets2D.Double(20.0, 60.0, 40.0, 20.0));
		plot.setSetting(RasterPlot.COLORS, new HeatMap());

		// Add plot to Swing component
		InteractivePanel panel = new InteractivePanel(plot);
		panel.setPannable(false);
		panel.setZoomable(false);
		add(panel);
	}

	public static void main(String[] args) {
		SimpleRasterPlot example = new SimpleRasterPlot();
		JFrame frame = new JFrame("GRALTest");
		frame.getContentPane().add(example, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 600);
		frame.setVisible(true);
	}
}
