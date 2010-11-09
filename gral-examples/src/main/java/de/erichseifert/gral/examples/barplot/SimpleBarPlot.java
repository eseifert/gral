/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2010 Erich Seifert <dev[at]richseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
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
import java.awt.LinearGradientPaint;

import javax.swing.JFrame;
import javax.swing.JPanel;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.BarPlot;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.Insets2D;


public class SimpleBarPlot extends JPanel {

	public SimpleBarPlot() {
		super(new BorderLayout());

		// Create example data
		DataTable data = new DataTable(Integer.class, Integer.class, Integer.class);
		data.add(1,  1,  6);
		data.add(2,  3,  8);
		data.add(3, -2,  2);
		data.add(4,  6,  6);
		data.add(5, -4,  8);
		data.add(6,  8, 18);
		data.add(7,  9,  9);
		data.add(8, 11,  1);

		// Create new bar plot
		BarPlot plot = new BarPlot(data);

		// Format plot
		plot.setInsets(new Insets2D.Double(40.0, 40.0, 40.0, 40.0));
		plot.setSetting(BarPlot.BAR_WIDTH, 0.75);

		// Format axes
		plot.getAxis(BarPlot.AXIS_X).setRange(0.5, 8.5);
		plot.getAxis(BarPlot.AXIS_Y).setRange(-4.0, 11.0);

		// Format bars
		PointRenderer pointRenderer = plot.getPointRenderer(data);
		pointRenderer.setSetting(PointRenderer.COLOR,
				new LinearGradientPaint(0f,0f, 0f,1f,
						new float[] {0.0f, 0.5f, 1.0f},
						new Color[] {
							new Color(0.5f, 0.8f, 0.0f),
							new Color(0.0f, 0.5f, 0.6f),
							new Color(0.0f, 0.2f, 0.9f)
						}
				)
		);
		pointRenderer.setSetting(PointRenderer.VALUE_DISPLAYED, true);
		pointRenderer.setSetting(PointRenderer.VALUE_ALIGNMENT_Y, 0.5);

		// Add plot to Swing component
		add(new InteractivePanel(plot));
	}

	public static void main(String[] args) {
		SimpleBarPlot example = new SimpleBarPlot();
		JFrame frame = new JFrame("GRALTest");
		frame.getContentPane().add(example, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setVisible(true);
	}
}
