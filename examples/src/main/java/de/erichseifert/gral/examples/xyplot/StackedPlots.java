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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.Random;

import javax.swing.JFrame;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.Insets2D;

public class StackedPlots extends JFrame {
	private static Random random = new Random();

	public StackedPlots() {
		super("GRALTest");

		DataTable data = new DataTable(Double.class, Double.class);
		double x=0.0, y=0.0;
		for (x=0.0; x<100.0; x+=0.1) {
			y += random.nextGaussian();
			data.add(x, Math.abs(y));
		}

		getContentPane().setLayout(new GridLayout(2, 1));

		// Upper plot
		XYPlot plotUpper = new XYPlot(data);
		AxisRenderer axisRendererXUpper = plotUpper.getAxisRenderer(XYPlot.AXIS_X);
		AxisRenderer axisRendererYUpper = plotUpper.getAxisRenderer(XYPlot.AXIS_Y);
		axisRendererXUpper.setSetting(AxisRenderer.TICKS_SPACING,  5.0);
		axisRendererYUpper.setSetting(AxisRenderer.TICKS_SPACING, 10.0);
		plotUpper.setPointRenderer(data, null);
		LineRenderer lineUpper = new DefaultLineRenderer2D();
		lineUpper.setSetting(LineRenderer.COLOR, new Color(0.9f, 0.3f, 0.2f));
		plotUpper.setLineRenderer(data, lineUpper);
		plotUpper.setInsets(new Insets2D.Double(20.0, 50.0, 40.0, 20.0));
		InteractivePanel panelUpper = new InteractivePanel(plotUpper);
		getContentPane().add(panelUpper);

		// Lower plot
		XYPlot plotLower = new XYPlot(data);
		AxisRenderer axisRendererXLower = plotLower.getAxisRenderer(XYPlot.AXIS_X);
		AxisRenderer axisRendererYLower = plotLower.getAxisRenderer(XYPlot.AXIS_Y);
		axisRendererXLower.setSetting(AxisRenderer.TICKS_SPACING,  5.0);
		axisRendererYLower.setSetting(AxisRenderer.TICKS_SPACING, 10.0);
		plotLower.setPointRenderer(data, null);
		LineRenderer lineLower = new DefaultLineRenderer2D();
		lineLower.setSetting(LineRenderer.STROKE, new BasicStroke(2f));
		lineLower.setSetting(LineRenderer.COLOR, new Color(0.0f, 0.3f, 1.0f));
		plotLower.setLineRenderer(data, lineLower);
		plotLower.setInsets(new Insets2D.Double(20.0, 50.0, 40.0, 20.0));
		InteractivePanel panelLower = new InteractivePanel(plotLower);
		getContentPane().add(panelLower);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(800, 400);
	}

	public static void main(String[] args) {
		StackedPlots test = new StackedPlots();
		test.setVisible(true);
	}
}
