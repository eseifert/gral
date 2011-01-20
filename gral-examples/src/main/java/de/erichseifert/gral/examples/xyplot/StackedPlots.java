/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2010 Erich Seifert <dev[at]erichseifert.de>,
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
package de.erichseifert.gral.examples.xyplot;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.geom.Ellipse2D;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.areas.AreaRenderer;
import de.erichseifert.gral.plots.areas.DefaultAreaRenderer2D;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.Insets2D;

public class StackedPlots extends JPanel {
	/** Version id for serialization. */
	private static final long serialVersionUID = 1L;
	/** Instance to generate random data values. */
	private static Random random = new Random();

	public StackedPlots() {
		super(new GridLayout(2, 1));

		// Generate data
		DataTable data = new DataTable(Double.class, Double.class);
		double x=0.0, y=0.0;
		for (x=0.0; x<100.0; x+=2.0) {
			y += 10.0*random.nextGaussian();
			data.add(x, Math.abs(y));
		}

		// Create and format upper plot
		XYPlot plotUpper = new XYPlot(data);
		Color colorUpper = new Color(0.9f, 0.3f, 0.2f);
		AxisRenderer axisRendererXUpper = plotUpper.getAxisRenderer(XYPlot.AXIS_X);
		AxisRenderer axisRendererYUpper = plotUpper.getAxisRenderer(XYPlot.AXIS_Y);
		axisRendererXUpper.setSetting(AxisRenderer.TICKS_SPACING,  5.0);
		axisRendererYUpper.setSetting(AxisRenderer.TICKS_SPACING, 10.0);
		plotUpper.setPointRenderer(data, null);
		LineRenderer lineUpper = new DefaultLineRenderer2D();
		lineUpper.setSetting(LineRenderer.COLOR, colorUpper);
		plotUpper.setLineRenderer(data, lineUpper);
		AreaRenderer areaUpper = new DefaultAreaRenderer2D();
		areaUpper.setSetting(AreaRenderer.COLOR, new Color(colorUpper.getRed(), colorUpper.getGreen(), colorUpper.getBlue(), 63));
		plotUpper.setAreaRenderer(data, areaUpper);
		plotUpper.setInsets(new Insets2D.Double(20.0, 50.0, 40.0, 20.0));
		InteractivePanel panelUpper = new InteractivePanel(plotUpper);
		add(panelUpper);

		// Create and format lower plot
		XYPlot plotLower = new XYPlot(data);
		Color colorLower = new Color(0.0f, 0.3f, 1.0f);
		AxisRenderer axisRendererXLower = plotLower.getAxisRenderer(XYPlot.AXIS_X);
		AxisRenderer axisRendererYLower = plotLower.getAxisRenderer(XYPlot.AXIS_Y);
		axisRendererXLower.setSetting(AxisRenderer.TICKS_SPACING,  5.0);
		axisRendererYLower.setSetting(AxisRenderer.TICKS_SPACING, 10.0);
		PointRenderer pointsLower = plotLower.getPointRenderer(data);
		pointsLower.setSetting(PointRenderer.COLOR, colorLower);
		pointsLower.setSetting(PointRenderer.SHAPE, new Ellipse2D.Double(-3, -3, 6, 6));
		LineRenderer lineLower = new DefaultLineRenderer2D();
		lineLower.setSetting(LineRenderer.STROKE, new BasicStroke(2f));
		lineLower.setSetting(LineRenderer.GAP, 1.0);
		lineLower.setSetting(LineRenderer.COLOR, colorLower);
		plotLower.setLineRenderer(data, lineLower);
		plotLower.setInsets(new Insets2D.Double(20.0, 50.0, 40.0, 20.0));
		InteractivePanel panelLower = new InteractivePanel(plotLower);
		add(panelLower);

		// Connect the two panels, i.e. user (mouse) actions affect both plots
		panelUpper.connect(panelLower);
	}

	public static void main(String[] args) {
		StackedPlots example = new StackedPlots();
		JFrame frame = new JFrame("GRALTest");
		frame.getContentPane().add(example, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setVisible(true);
	}
}
