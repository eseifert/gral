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
import java.awt.Color;
import java.awt.geom.Ellipse2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.plots.points.SizeablePointRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.Insets2D;


public class SpiralPlot extends JPanel {
	public SpiralPlot() {
		super(new BorderLayout());
		setBackground(new Color(0.75f, 0.75f, 0.75f));

		DataTable data = new DataTable(Double.class, Double.class, Double.class);
		for (double alpha = 0.0, r = 0.0; r <= 10.0; alpha -= 1.0, r += 0.05) {
			double x = r*Math.cos(alpha);
			double y = r*Math.sin(alpha);
			double z = 3.0 + 4.0*r;
			data.add(x, y, z);
		}

		DataSeries series = new DataSeries("Spiral", data);

		XYPlot plot = new XYPlot(series);

		PointRenderer pointRenderer = new SizeablePointRenderer();
		pointRenderer.setSetting(PointRenderer.SHAPE, new Ellipse2D.Double(-0.5, -0.5, 1.0, 1.0));
		pointRenderer.setSetting(PointRenderer.COLOR, new Color(0f, 0f, 0.5f, 0.25f));
		pointRenderer.setSetting(SizeablePointRenderer.COLUMN_SIZE, 2);
		plot.setPointRenderer(series, pointRenderer);

		plot.getPlotArea().setSetting(XYPlot.XYPlotArea2D.BORDER, null);
		plot.getPlotArea().setSetting(XYPlot.XYPlotArea2D.GRID_MAJOR_X, false);
		plot.getPlotArea().setSetting(XYPlot.XYPlotArea2D.GRID_MAJOR_Y, false);
		plot.getAxisRenderer(XYPlot.AXIS_X).setSetting(AxisRenderer.SHAPE_VISIBLE, false);
		plot.getAxisRenderer(XYPlot.AXIS_X).setSetting(AxisRenderer.TICKS, false);
		plot.getAxisRenderer(XYPlot.AXIS_Y).setSetting(AxisRenderer.SHAPE_VISIBLE, false);
		plot.getAxisRenderer(XYPlot.AXIS_Y).setSetting(AxisRenderer.TICKS, false);
		plot.getAxis(XYPlot.AXIS_X).setRange(-10.0, 10.0);
		plot.getAxis(XYPlot.AXIS_Y).setRange(-10.0, 10.0);

		plot.setInsets(new Insets2D.Double(40.0, 40.0, 40.0, 40.0));
		add(new InteractivePanel(plot), BorderLayout.CENTER);
	}

	public static void main(String[] args) {
		SpiralPlot example = new SpiralPlot();
		JFrame frame = new JFrame("GRALTest");
		frame.getContentPane().add(example, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 600);
		frame.setVisible(true);
	}
}
