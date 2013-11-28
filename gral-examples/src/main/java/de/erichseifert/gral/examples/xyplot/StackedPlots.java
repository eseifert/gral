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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.util.Random;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.examples.ExamplePanel;
import de.erichseifert.gral.graphics.DrawableContainer;
import de.erichseifert.gral.graphics.TableLayout;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.areas.AreaRenderer;
import de.erichseifert.gral.plots.areas.DefaultAreaRenderer2D;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.Insets2D;

public class StackedPlots extends ExamplePanel {
	/** Version id for serialization. */
	private static final long serialVersionUID = 6832343098989019088L;

	/** Instance to generate random data values. */
	private static final Random random = new Random();

	@SuppressWarnings("unchecked")
	public StackedPlots() {
		// Generate data
		DataTable data = new DataTable(Double.class, Double.class);
		double x=0.0, y=0.0;
		for (x=0.0; x<100.0; x+=2.0) {
			y += 10.0*random.nextGaussian();
			data.add(x, Math.abs(y));
		}

		// Create and format upper plot
		XYPlot plotUpper = new XYPlot(data);
		Color colorUpper = COLOR1;
		plotUpper.setPointRenderer(data, null);
		LineRenderer lineUpper = new DefaultLineRenderer2D();
		lineUpper.setColor(colorUpper);
		plotUpper.setLineRenderer(data, lineUpper);
		AreaRenderer areaUpper = new DefaultAreaRenderer2D();
		areaUpper.setColor(GraphicsUtils.deriveWithAlpha(colorUpper, 64));
		plotUpper.setAreaRenderer(data, areaUpper);
		plotUpper.setInsets(new Insets2D.Double(20.0, 50.0, 40.0, 20.0));

		// Create and format lower plot
		XYPlot plotLower = new XYPlot(data);
		Color colorLower = COLOR1;
		PointRenderer pointsLower = plotLower.getPointRenderer(data);
		pointsLower.setColor(colorLower);
		pointsLower.setShape(new Ellipse2D.Double(-3, -3, 6, 6));
		LineRenderer lineLower = new DefaultLineRenderer2D();
		lineLower.setStroke(new BasicStroke(2f));
		lineLower.setGap(1.0);
		lineLower.setColor(colorLower);
		plotLower.setLineRenderer(data, lineLower);
		plotLower.setInsets(new Insets2D.Double(20.0, 50.0, 40.0, 20.0));

		DrawableContainer plots = new DrawableContainer(new TableLayout(1));
		plots.add(plotUpper);
		plots.add(plotLower);

		// Connect the two plots, i.e. user (mouse) actions affect both plots
		plotUpper.getNavigator().connect(plotLower.getNavigator());

		InteractivePanel panel = new InteractivePanel(plots);
		add(panel);
	}

	@Override
	public String getTitle() {
		return "Stacked plots";
	}

	@Override
	public String getDescription() {
		return "An area and a line plot with synchronized actions.";
	}

	public static void main(String[] args) {
		new StackedPlots().showInFrame();
	}
}
