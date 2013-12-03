/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2013 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael[at]erichseifert.de>
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.LinearGradientPaint;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.examples.ExamplePanel;
import de.erichseifert.gral.plots.BarPlot;
import de.erichseifert.gral.plots.BarPlot.BarRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.Insets2D;
import de.erichseifert.gral.util.Location;


public class SimpleBarPlot extends ExamplePanel {
	/** Version id for serialization. */
	private static final long serialVersionUID = -2793954497895054530L;

	@SuppressWarnings("unchecked")
	public SimpleBarPlot() {
		// Create example data
		DataTable data = new DataTable(Double.class, Integer.class, String.class);
		data.add(0.1,  1, "January");
		data.add(0.2,  3, "February");
		data.add(0.3, -2, "March");
		data.add(0.4,  6, "April");
		data.add(0.5, -4, "May");
		data.add(0.6,  8, "June");
		data.add(0.7,  9, "July");
		data.add(0.8, 11, "August");

		// Create new bar plot
		BarPlot plot = new BarPlot(data);

		// Format plot
		plot.setInsets(new Insets2D.Double(40.0, 40.0, 40.0, 40.0));
		plot.setBarWidth(0.075);

		// Format bars
		BarRenderer pointRenderer = (BarRenderer) plot.getPointRenderer(data);
		pointRenderer.setColor(
			new LinearGradientPaint(0f,0f, 0f,1f,
					new float[] { 0.0f, 1.0f },
					new Color[] { COLOR1, GraphicsUtils.deriveBrighter(COLOR1) }
			)
		);
		pointRenderer.setBorderStroke(new BasicStroke(3f));
		pointRenderer.setBorderColor(
			new LinearGradientPaint(0f,0f, 0f,1f,
					new float[] { 0.0f, 1.0f },
					new Color[] { GraphicsUtils.deriveBrighter(COLOR1), COLOR1 }
			)
		);
		pointRenderer.setValueVisible(true);
		pointRenderer.setValueColumn(2);
		pointRenderer.setValueLocation(Location.CENTER);
		pointRenderer.setValueColor(GraphicsUtils.deriveDarker(COLOR1));
		pointRenderer.setValueFont(Font.decode(null).deriveFont(Font.BOLD));

		// Add plot to Swing component
		add(new InteractivePanel(plot));
	}

	@Override
	public String getTitle() {
		return "Bar plot";
	}

	@Override
	public String getDescription() {
		return "Bar plot with example data and color gradients";
	}

	public static void main(String[] args) {
		new SimpleBarPlot().showInFrame();
	}
}
