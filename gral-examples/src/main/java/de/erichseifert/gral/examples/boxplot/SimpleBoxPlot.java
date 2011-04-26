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
package de.erichseifert.gral.examples.boxplot;

import java.awt.BorderLayout;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.BoxPlot;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.ui.InteractivePanel.NavigationDirection;
import de.erichseifert.gral.util.Insets2D;
import de.erichseifert.vectorgraphics2d.DataUtils;


public class SimpleBoxPlot extends JPanel {
	/** Version id for serialization. */
	private static final long serialVersionUID = 1L;

	public SimpleBoxPlot() {
		super(new BorderLayout());
		Random random = new Random();

		// Create example data
		DataTable data = new DataTable(Integer.class, Integer.class, Integer.class);
		for (int i = 0; i < 50; i++) {
			int x = (int) Math.round(5.0*random.nextGaussian());
			int y = (int) Math.round(5.0*random.nextGaussian());
			int z = (int) Math.round(5.0*random.nextGaussian());
			data.add(x, y, z);
		}

		// Create new box-and-whisker plot
		BoxPlot plot = new BoxPlot(data);

		// Format plot
		plot.setInsets(new Insets2D.Double(20.0, 40.0, 40.0, 20.0));

		// Format axes
		plot.getAxisRenderer(BoxPlot.AXIS_X).setSetting(
			AxisRenderer.TICKS_CUSTOM, DataUtils.map(
					new Double[] {1.0, 2.0, 3.0},
					new String[] {"Column 1", "Column 2", "Column 3"}
			)
		);

		// Add plot to Swing component
		InteractivePanel panel = new InteractivePanel(plot);
		panel.setNavigateDirection(NavigationDirection.VERTICAL);
		add(panel);
	}

	public static void main(String[] args) {
		SimpleBoxPlot example = new SimpleBoxPlot();
		JFrame frame = new JFrame("GRALTest");
		frame.getContentPane().add(example, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 600);
		frame.setVisible(true);
	}
}
