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
package de.erichseifert.gral.plots.areas;

import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.List;

import de.erichseifert.gral.graphics.AbstractDrawable;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.plots.DataPoint;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.MathUtils;
import de.erichseifert.gral.util.PointND;

/**
 * Default two-dimensional implementation of the {@code AreaRenderer}
 * interface.
 */
public class DefaultAreaRenderer2D extends AbstractAreaRenderer {
	/** Version id for serialization. */
	private static final long serialVersionUID = -202003022764142849L;

	/**
	 * Returns the graphical representation to be drawn for the specified
	 * data points.
	 * @param points Points to be used for creating the area.
	 * @param shape Geometric shape of the area.
	 * @return Representation of the area.
	 */
	public Drawable getArea(final List<DataPoint> points, final Shape shape) {
		return new AbstractDrawable() {
			/** Version id for serialization. */
			private static final long serialVersionUID = -3659798228877496727L;

			/**
			 * Draws the {@code Drawable} with the specified drawing context.
			 * @param context Environment used for drawing
			 */
			public void draw(DrawingContext context) {
				Paint paint = DefaultAreaRenderer2D.this.getColor();
				GraphicsUtils.fillPaintedShape(context.getGraphics(),
					shape, paint, null);
			}
		};
	}

	/**
	 * Returns the shape used for rendering the area of a data points.
	 * @param points Data points.
	 * @return Geometric shape for the area of the specified data points.
	 */
	public Shape getAreaShape(List<DataPoint> points) {
		if (points.isEmpty() || points.get(0) == null) {
			return null;
		}

		Axis axisY = points.get(0).data.axes.get(1);
		AxisRenderer axisRendererY = points.get(0).data.axisRenderers.get(1);

		double axisYMin = axisY.getMin().doubleValue();
		double axisYMax = axisY.getMax().doubleValue();
		double axisYOrigin = MathUtils.limit(0.0, axisYMin, axisYMax);

		PointND<Double> posOrigin = null;
		if (axisRendererY != null) {
			posOrigin = axisRendererY.getPosition(
					axisY, axisYOrigin, true, false);
		}

		Path2D shape = new Path2D.Double();
		if (posOrigin == null) {
			return shape;
		}

		double posYOrigin = posOrigin.get(PointND.Y);
		double x = 0.0;
		double y = 0.0;

		for (DataPoint p: points) {
			Point2D pos = p.position.getPoint2D();
			x = pos.getX();
			y = pos.getY();
			if (shape.getCurrentPoint() == null) {
				shape.moveTo(x, posYOrigin);
			}
			shape.lineTo(x, y);
		}

		if (shape.getCurrentPoint() != null) {
			shape.lineTo(x, posYOrigin);
			shape.closePath();
		}

		return shape;
	}
}
