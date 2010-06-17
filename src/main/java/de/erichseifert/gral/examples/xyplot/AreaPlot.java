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

import java.awt.Color;
import java.util.Random;

import javax.swing.JFrame;

import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.areas.AreaRenderer;
import de.erichseifert.gral.plots.areas.DefaultAreaRenderer2D;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.plots.points.DefaultPointRenderer;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.Insets2D;

public class AreaPlot extends JFrame {
	private static Random random = new Random();

	public AreaPlot() {
		super("GRALTest");

		// Create table
		DataTable data = new DataTable(Double.class, Double.class, Double.class, Double.class);
		for (double x=0.0; x<2.5*Math.PI; x+=Math.PI/15.0) {
			double y1 = (x>=0.00*Math.PI && x<2.25*Math.PI) ? (4.0*Math.sin(x + 0.5*Math.PI) + 0.1*random.nextGaussian()) : Double.NaN;
			double y2 = (x>=0.25*Math.PI && x<2.50*Math.PI) ? (4.0*Math.cos(x + 0.5*Math.PI) + 0.1*random.nextGaussian()) : Double.NaN;
			double y3 = (x>=0.00*Math.PI && x<2.50*Math.PI) ? (2.0*Math.sin(2.0*x/2.5)       + 0.1*random.nextGaussian()) : Double.NaN;
			data.add(x, y1, y2, y3);
		}
		DataSeries data1 = new DataSeries("red",   data, 0, 1);
		DataSeries data2 = new DataSeries("green", data, 0, 2);
		DataSeries data3 = new DataSeries("blue",  data, 0, 3);

		XYPlot plot = new XYPlot(data1, data2, data3);
		plot.setSetting(XYPlot.LEGEND, true);
		plot.setInsets(new Insets2D.Double(20.0, 40.0, 20.0, 20.0));
		getContentPane().add(new InteractivePanel(plot));

		formatData(plot, data1, new Color(0.9f, 0.3f, 0.2f));
		formatData(plot, data2, new Color(0.0f, 0.3f, 0.9f));
		formatData(plot, data3, new Color(0.0f, 0.5f, 0.0f));

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(800, 600);
	}

	private static void formatData(XYPlot plot, DataSource data, Color color) {
		PointRenderer point = new DefaultPointRenderer();
		point.setSetting(PointRenderer.COLOR, color);
		plot.setPointRenderer(data, point);
		LineRenderer line = new DefaultLineRenderer2D();
		line.setSetting(LineRenderer.COLOR, color);
		line.setSetting(LineRenderer.GAP, 3.0);
		line.setSetting(LineRenderer.GAP_ROUNDED, true);
		plot.setLineRenderer(data, line);
		AreaRenderer area = new DefaultAreaRenderer2D();
		area.setSetting(AreaRenderer.COLOR, new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
		plot.setAreaRenderer(data, area);
	}

	public static void main(String[] args) {
		AreaPlot test = new AreaPlot();
		test.setVisible(true);
	}
}
