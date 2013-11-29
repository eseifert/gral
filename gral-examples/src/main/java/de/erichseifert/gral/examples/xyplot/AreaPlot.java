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
package de.erichseifert.gral.examples.xyplot;

import java.awt.Color;
import java.util.Random;

import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.examples.ExamplePanel;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.areas.AreaRenderer;
import de.erichseifert.gral.plots.areas.DefaultAreaRenderer2D;
import de.erichseifert.gral.plots.areas.LineAreaRenderer2D;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.plots.points.DefaultPointRenderer2D;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.Insets2D;

public class AreaPlot extends ExamplePanel {
	/** Version id for serialization. */
	private static final long serialVersionUID = 3287044991898775949L;

	/** Instance to generate random data values. */
	private static final Random random = new Random();

	@SuppressWarnings("unchecked")
	public AreaPlot() {
		// Generate data
		DataTable data = new DataTable(Double.class, Double.class, Double.class, Double.class);
		for (double x=0.0; x<2.5*Math.PI; x+=Math.PI/15.0) {
			double y1 = Double.NaN, y2 = Double.NaN, y3 = Double.NaN;
			if (x>=0.00*Math.PI && x<2.25*Math.PI) {
				y1 = 4.0*Math.sin(x + 0.5*Math.PI) + 0.1*random.nextGaussian();
			}
			if (x>=0.25*Math.PI && x<2.50*Math.PI) {
				y2 = 4.0*Math.cos(x + 0.5*Math.PI) + 0.1*random.nextGaussian();
			}
			if (x>=0.00*Math.PI && x<2.50*Math.PI) {
				y3 = 2.0*Math.sin(2.0*x/2.5)       + 0.1*random.nextGaussian();
			}
			data.add(x, y1, y2, y3);
		}

		// Create data series
		DataSeries data1 = new DataSeries("red", data, 0, 1);
		DataSeries data2 = new DataSeries("blue 1", data, 0, 2);
		DataSeries data3 = new DataSeries("blue 2", data, 0, 3);

		// Create new xy-plot
		XYPlot plot = new XYPlot(data1, data2, data3);
		plot.setLegendVisible(true);
		plot.setInsets(new Insets2D.Double(20.0, 40.0, 20.0, 20.0));

		// Format data series
		formatFilledArea(plot, data1, COLOR2);
		formatFilledArea(plot, data2, COLOR1);
		formatLineArea(plot, data3, GraphicsUtils.deriveDarker(COLOR1));

		// Add plot to Swing component
		add(new InteractivePanel(plot));
	}

	private static void formatFilledArea(XYPlot plot, DataSource data, Color color) {
		PointRenderer point = new DefaultPointRenderer2D();
		point.setColor(color);
		plot.setPointRenderer(data, point);
		LineRenderer line = new DefaultLineRenderer2D();
		line.setColor(color);
		line.setGap(3.0);
		line.setGapRounded(true);
		plot.setLineRenderer(data, line);
		AreaRenderer area = new DefaultAreaRenderer2D();
		area.setColor(GraphicsUtils.deriveWithAlpha(color, 64));
		plot.setAreaRenderer(data, area);
	}

	private static void formatLineArea(XYPlot plot, DataSource data, Color color) {
		PointRenderer point = new DefaultPointRenderer2D();
		point.setColor(color);
		plot.setPointRenderer(data, point);
		plot.setLineRenderer(data, null);
		AreaRenderer area = new LineAreaRenderer2D();
		area.setGap(3.0);
		area.setColor(color);
		plot.setAreaRenderer(data, area);
	}

	@Override
	public String getTitle() {
		return "Area plot";
	}

	@Override
	public String getDescription() {
		return "Area plot of three series with different styling";
	}

	public static void main(String[] args) {
		new AreaPlot().showInFrame();
	}
}
