/**
 * GRAL: Vector export for Java(R) Graphics2D
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

import de.erichseifert.gral.InteractivePanel;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.areas.AreaRenderer2D;
import de.erichseifert.gral.plots.areas.DefaultAreaRenderer2D;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer2D;
import de.erichseifert.gral.plots.points.DefaultPointRenderer;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.util.Insets2D;

public class AreaPlot extends JFrame {
	private static Random random = new Random();

	public AreaPlot() {
		super("GRALTest");

		// Create table 1
		DataTable data1 = new DataTable(Double.class, Double.class);
		for (double x=0.0; x<2.25*Math.PI; x+=Math.PI/50.0) {
			double y = 4.0*Math.sin(x + 0.5*Math.PI) + 0.1*random.nextGaussian();
			data1.add(x, y);
		}
		// Create table 2
		DataTable data2 = new DataTable(Double.class, Double.class);
		for (double x=0.25*Math.PI; x<2.5*Math.PI; x+=Math.PI/50.0) {
			double y = 4.0*Math.cos(x + 0.5*Math.PI) + 0.1*random.nextGaussian();
			data2.add(x, y);
		}

		XYPlot plot = new XYPlot(data1, data2);
		plot.setInsets(new Insets2D.Double(20.0, 50.0, 40.0, 20.0));
		getContentPane().add(new InteractivePanel(plot));

		PointRenderer point1 = new DefaultPointRenderer();
		point1.setSetting(PointRenderer.KEY_COLOR, new Color(0.9f, 0.3f, 0.2f));
		plot.setPointRenderer(data1, point1);
		LineRenderer2D line1 = new DefaultLineRenderer2D();
		line1.setSetting(LineRenderer2D.KEY_COLOR, new Color(0.9f, 0.3f, 0.2f));
		line1.setSetting(LineRenderer2D.KEY_GAP, 3.0);
		line1.setSetting(LineRenderer2D.KEY_GAP_ROUNDED, true);
		plot.setLineRenderer(data1, line1);
		AreaRenderer2D area1 = new DefaultAreaRenderer2D();
		area1.setSetting(AreaRenderer2D.KEY_COLOR, new Color(0.9f, 0.3f, 0.2f, 0.2f));
		plot.setAreaRenderer(data1, area1);

		PointRenderer point2 = new DefaultPointRenderer();
		point2.setSetting(PointRenderer.KEY_COLOR, new Color(0.0f, 0.3f, 0.9f));
		plot.setPointRenderer(data2, point2);
		LineRenderer2D line2 = new DefaultLineRenderer2D();
		line2.setSetting(LineRenderer2D.KEY_COLOR, new Color(0.0f, 0.3f, 0.9f));
		line2.setSetting(LineRenderer2D.KEY_GAP, 3.0);
		line2.setSetting(LineRenderer2D.KEY_GAP_ROUNDED, true);
		plot.setLineRenderer(data2, line2);
		AreaRenderer2D area2 = new DefaultAreaRenderer2D();
		area2.setSetting(AreaRenderer2D.KEY_COLOR, new Color(0.0f, 0.3f, 0.9f, 0.2f));
		plot.setAreaRenderer(data2, area2);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(800, 600);
	}

	public static void main(String[] args) {
		AreaPlot test = new AreaPlot();
		test.setVisible(true);
	}
}
