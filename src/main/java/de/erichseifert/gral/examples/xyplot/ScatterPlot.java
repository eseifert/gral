/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2010 Erich Seifert <info[at]erichseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
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
import java.util.Random;

import javax.swing.JFrame;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.Insets2D;


public class ScatterPlot extends JFrame {
	private static final Random random = new Random();

	public ScatterPlot() {
		super("GRALTest");

		DataTable data = new DataTable(Double.class, Double.class);
		for (int i = 0; i <= 100000; i++) {
			data.add(random.nextGaussian()*2.0,  random.nextGaussian()*2.0);
		}

		XYPlot plot = new XYPlot(data);

		// Setting the title
		plot.setSetting(XYPlot.TITLE, "A Large Scatter Plot");
		plot.setSetting(XYPlot.ANTIALISING, false);

		plot.setInsets(new Insets2D.Double(20.0, 40.0, 40.0, 40.0));
		getContentPane().add(new InteractivePanel(plot), BorderLayout.CENTER);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 600);
	}

	public static void main(String[] args) {
		ScatterPlot test = new ScatterPlot();
		test.setVisible(true);
	}

}
