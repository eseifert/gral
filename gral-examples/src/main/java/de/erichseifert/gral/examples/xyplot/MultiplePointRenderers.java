/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2018 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <mseifert[at]error-reports.org>
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
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.examples.ExamplePanel;
import de.erichseifert.gral.graphics.AbstractDrawable;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.graphics.Insets2D;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.plots.points.AbstractPointRenderer;
import de.erichseifert.gral.plots.points.DefaultPointRenderer2D;
import de.erichseifert.gral.plots.points.PointData;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.GraphicsUtils;


public class MultiplePointRenderers extends ExamplePanel {
	/** Version id for serialization. */
	private static final long serialVersionUID = -5263057758564264677L;

	private static class ShadowPointRenderer extends AbstractPointRenderer {
		private final PointRenderer pointRenderer;

		public ShadowPointRenderer(PointRenderer pointRenderer) {
			this.pointRenderer = pointRenderer;
		}

		@Override
		public Shape getPointShape(PointData data) {
			return pointRenderer.getPointShape(data);
		}

		@Override
		public Drawable getPoint(final PointData data, final Shape shape) {
			return new AbstractDrawable() {
				@Override
				public void draw(DrawingContext context) {
					Graphics2D graphics2D = context.getGraphics();
					AffineTransform txOld = graphics2D.getTransform();
					graphics2D.translate(2.0, 1.0);
					GraphicsUtils.fillPaintedShape(
							graphics2D, shape, new Color(0.0f, 0.0f, 0.0f, 0.2f), null);
					graphics2D.setTransform(txOld);
				}
			};
		}

		@Override
		public Drawable getValue(PointData data, Shape shape) {
			return pointRenderer.getValue(data, shape);
		}
	}

	@SuppressWarnings("unchecked")
	public MultiplePointRenderers() {
		// Generate data
		DataTable data = new DataTable(Double.class, Double.class);
		for (double x = 1.0; x <= 20.0; x += 1.0) {
			data.add(x, x*x);
		}

		// Create new xy-plot
		XYPlot plot = new XYPlot(data);

		// Format plot
		plot.setInsets(new Insets2D.Double(20.0, 60.0, 40.0, 40.0));
		plot.setBackground(Color.WHITE);
		plot.getTitle().setText(getDescription());

		// Format rendering of data points
		PointRenderer defaultPointRenderer = new DefaultPointRenderer2D();
		defaultPointRenderer.setColor(GraphicsUtils.deriveDarker(COLOR1));
		plot.setPointRenderers(data, defaultPointRenderer);
		PointRenderer shadowRenderer = new ShadowPointRenderer(defaultPointRenderer);
		plot.addPointRenderer(data, shadowRenderer);

		LineRenderer lineRenderer = new DefaultLineRenderer2D();
		lineRenderer.setGap(2.0);
		plot.setLineRenderers(data, lineRenderer);

		// Add plot to Swing component
		add(new InteractivePanel(plot), BorderLayout.CENTER);
	}

	@Override
	public String getTitle() {
		return "Multiple renderers";
	}

	@Override
	public String getDescription() {
		return "Plot with point shadows";
	}

	public static void main(String[] args) {
		new MultiplePointRenderers().showInFrame();
	}
}
