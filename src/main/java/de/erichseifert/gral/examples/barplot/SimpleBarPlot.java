/* GRAL : a free graphing library for the Java(tm) platform
 *
 * (C) Copyright 2009-2010, by Erich Seifert and Michael Seifert.
 *
 * This file is part of GRAL.
 *
 * GRAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.erichseifert.gral.examples.barplot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.LinearGradientPaint;

import javax.swing.JFrame;

import de.erichseifert.gral.InteractivePanel;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.BarPlot;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.shapes.ShapeRenderer;
import de.erichseifert.gral.util.Insets2D;


public class SimpleBarPlot extends JFrame {

	public SimpleBarPlot() {
		super("GRALTest");
		DataTable data = new DataTable(Integer.class, Integer.class, Integer.class);
		data.add(1,  1,  6);
		data.add(2,  3,  8);
		data.add(3, -2,  2);
		data.add(4,  6,  6);
		data.add(5, -4,  8);
		data.add(6,  8, 18);
		data.add(7,  9,  9);
		data.add(8, 11,  1);
		BarPlot plot = new BarPlot(data);
		plot.getAxis(Axis.X).setRange(0.5, 8.5);
		plot.getAxis(Axis.Y).setRange(-4.0, 11.0);
		plot.setInsets(new Insets2D.Double(40.0, 40.0, 40.0, 40.0));
		plot.setSetting(BarPlot.KEY_BAR_WIDTH, 0.75);
		ShapeRenderer pointRenderer = plot.getShapeRenderer(data);
		pointRenderer.setSetting(ShapeRenderer.KEY_COLOR,
				new LinearGradientPaint(0f,0f, 0f,1f,
						new float[] {0.0f, 0.5f, 1.0f},
						new Color[] {new Color(0.5f, 0.8f, 0.0f), new Color(0.0f, 0.5f, 0.6f), new Color(0.0f, 0.2f, 0.9f)}));
		pointRenderer.setSetting(ShapeRenderer.KEY_VALUE_DISPLAYED, true);
		getContentPane().add(new InteractivePanel(plot), BorderLayout.CENTER);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(800, 600);
	}

	public static void main(String[] args) {
		SimpleBarPlot test = new SimpleBarPlot();
		test.setVisible(true);
	}
}
