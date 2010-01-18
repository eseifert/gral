/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009, by Erich Seifert and Michael Seifert.
 *
 * This file is part of OpenJChart.
 *
 * OpenJChart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenJChart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenJChart.  If not, see <http://www.gnu.org/licenses/>.
 */

package openjchart.examples.pieplot;

import java.awt.BorderLayout;
import java.util.Random;

import javax.swing.JFrame;

import openjchart.InteractivePanel;
import openjchart.data.DataTable;
import openjchart.plots.PiePlot;
import openjchart.util.Insets2D;

public class SimplePiePlot extends JFrame {

	public SimplePiePlot() {
		super("OpenJChartTest");
		DataTable data = new DataTable(Integer.class);
		/*
		data.add(1, 1);
		data.add(2, 3);
		data.add(3, 2);
		data.add(4, 6);
		data.add(5, 4);
		data.add(6, 8);
		data.add(7, 9);
		data.add(8, 11);//*/
		//*
		Random r = new Random();
		for (int i = 0; i < 15; i++) {
			data.add(r.nextInt(10) + 1);
		}
		PiePlot plot = new PiePlot(data);
		plot.setSetting(PiePlot.KEY_TITLE, "A Sample Pie Plot");
		// Change rotation
		//plot.setSetting(PiePlot.KEY_CLOCKWISE, false);
		// Custom start angle
		//plot.setSetting(PiePlot.KEY_START, 70.0);
		// Custom colors
		//plot.setSetting(PiePlot.KEY_COLORS, new RainbowColors());
		// Random blue colors
		//QuasiRandomColors colors = new QuasiRandomColors();
		//colors.setColorVariance(new float[] {0.60f, 0.00f, 0.75f, 0.25f, 0.25f, 0.75f});
		//plot.setSetting(PiePlot.KEY_COLORS, colors);
		plot.setInsets(new Insets2D.Double(20.0, 40.0, 40.0, 40.0));
		getContentPane().add(new InteractivePanel(plot), BorderLayout.CENTER);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(800, 600);
	}

	public static void main(String[] args) {
		SimplePiePlot test = new SimplePiePlot();
		test.setVisible(true);
	}
}
